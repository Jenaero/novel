package com.xiaokedou.novel.spider.storage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.xiaokedou.novel.common.enums.NovelStatusEnum;
import com.xiaokedou.novel.dao.mapper.ChapterDetailMapper;
import com.xiaokedou.novel.dao.mapper.ChapterMapper;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.service.idworker.IdWorkerService;
import com.xiaokedou.novel.service.spider.IChapterDetailSpider;
import com.xiaokedou.novel.service.spider.IChapterSpider;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.util.FastdfsClientUtil;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.service.util.SpringUtil;
import com.xiaokedou.novel.spider.storage.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AbstractUpdateNovelStorage
 *
 * @Author: renyajian
 * @Date: 2019/12/11
 */
public abstract class AbstractUpdateNovelStorage implements Processor {

    private final Logger logger = LoggerFactory.getLogger(AbstractUpdateNovelStorage.class);


    protected static Map <String, String> tasks = new TreeMap <>();
    protected NovelMapper novelMapper;
    protected ChapterMapper chapterMapper;
    protected ChapterDetailMapper chapterDetailMapper;
    protected IdWorkerService idWorkerService;
    protected final static Integer keepAliveTime = 120;
    protected ThreadPoolExecutor threadPoolExecutor = null;
    protected PlatformTransactionManager transactionManager;
    protected FastdfsClientUtil fastdfsClientUtil;

    public AbstractUpdateNovelStorage() {
        ApplicationContext context = SpringUtil.getApplicationContext();
        novelMapper = context.getBean(NovelMapper.class);
        chapterMapper = context.getBean(ChapterMapper.class);
        chapterDetailMapper = context.getBean(ChapterDetailMapper.class);
        idWorkerService = context.getBean(IdWorkerService.class);
        transactionManager = context.getBean(PlatformTransactionManager.class);
        fastdfsClientUtil = context.getBean(FastdfsClientUtil.class);
    }

    @Override
    public void process() {
        try {
            threadPoolExecutor = new ThreadPoolExecutor(
                    tasks.size(),
                    tasks.size(),
                    keepAliveTime,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque <>(100000),
                    new NamedThreadFactory("novel-update")
            );
            List <Future <String>> futures = new ArrayList <>(tasks.size());
            for (Map.Entry <String, String> entry : tasks.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                futures.add(threadPoolExecutor.submit(() -> {
                    INovelSpider spider = NovelSpiderFactory.getNovelSpider(value);
                    Iterator <List <Novel>> iterator = spider.iterator(value, 10);
                    while (iterator.hasNext()) {
                        logger.info(Thread.currentThread().getName() + "开始抓取[" + key + "] 的 URL:" + spider.next());
                        List <Novel> novels = iterator.next();
                        Date now = new Date();
                        for (int i = 0; i < novels.size(); i++) {
                            Novel newNovel = novels.get(i);
                            //数据库是否存在且是连载状态
                            LambdaQueryWrapper <Novel> novelLambdaQueryWrapper = new LambdaQueryWrapper <Novel>()
                                    .eq(Novel::getName, newNovel.getName())
                                    .eq(Novel::getAuthor, newNovel.getAuthor())
                                    .eq(Novel::getStatus, NovelStatusEnum.serialized.getKey());
                            Integer novelCount = novelMapper.selectCount(novelLambdaQueryWrapper);
                            if (novelCount <= 0) {
                                logger.warn("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() +
                                        ">,status=" + NovelStatusEnum.getLabelByKey(newNovel.getStatus()) + ",不存在，不用更新！");
                                continue;
                            }
                            if (novelCount > 1) {
                                logger.warn("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() +
                                        ">,status=" + NovelStatusEnum.getLabelByKey(newNovel.getStatus()) + ",有" + novelCount + "本,数据异常,不更新！");
                                continue;
                            }
                            //todo 更新列表处理逻辑再想想
                            /** 每个站点更新每个站点的小说
                             * 1、最后更新时间
                             * 2、章节URL标签大小
                             *
                             */
                            Novel oldNovel = novelMapper.selectOne(novelLambdaQueryWrapper);
                            //获取本地章节数
                            LambdaQueryWrapper <Chapter> chapterLambdaQueryWrapper = new LambdaQueryWrapper <Chapter>().eq(Chapter::getNovelId, oldNovel.getId());
                            Integer oldChapterCount = chapterMapper.selectCount(chapterLambdaQueryWrapper);
                            //获取小说的所有章节
                            IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(newNovel.getChapterUrl());
                            //拼接chapters 列表
                            List <Chapter> newChapters = chapterSpider.getChaptersByNovelChapterUrl(newNovel);
                            Integer newChapterCount = newChapters.size();
                            //章节数目比较
                            if (newChapterCount <= oldChapterCount) {
                                logger.warn("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,已最新[数量相同],不用更新！");
                                continue;
                            }
                            //获取要更新的章节
                            List <Chapter> oldChapters = chapterMapper.selectList(chapterLambdaQueryWrapper);
                            String oldChapterMaxIndexUrl = oldChapters.stream().max(Comparator.comparing(Chapter::getId))
                                    .get().getUrl();
                            Long oldChapterMaxIndex = Long.parseLong(oldChapterMaxIndexUrl.substring(oldChapterMaxIndexUrl.lastIndexOf("/") + 1,
                                    oldChapterMaxIndexUrl.lastIndexOf(".")));
                            List <Chapter> needUpdateChapters = newChapters.stream().filter(newChapter -> {
                                String newChapterUrl = newChapter.getUrl();
                                Long newChapterIndex = Long.parseLong(newChapterUrl.substring(newChapterUrl.lastIndexOf('/') + 1, newChapterUrl.lastIndexOf('.')));
                                return newChapterIndex > oldChapterMaxIndex;
                            }).collect(Collectors.toList());
                            if (CollectionUtils.isEmpty(needUpdateChapters)) {
                                logger.warn("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,已最新[最大章节相同],不用更新！");
                                continue;
                            }

                            //更新小说、章节及明细
                            Novel updateNovel = new Novel();
                            updateNovel.setId(oldNovel.getId());
                            updateNovel.setStatus(newNovel.getStatus());
                            updateNovel.setIntroduction(newNovel.getIntroduction());
                            updateNovel.setLastUpdateChapter(newNovel.getLastUpdateChapter());
                            updateNovel.setLastUpdateChapterUrl(newNovel.getLastUpdateChapterUrl());
                            updateNovel.setLastUpdateTime(newNovel.getLastUpdateTime());
                            updateNovel.setLength(newNovel.getLength());
                            updateNovel.setCollection(newNovel.getCollection());
                            updateNovel.setWeekClick(newNovel.getWeekClick());
                            updateNovel.setWeekRecommend(newNovel.getWeekRecommend());
                            updateNovel.setMonthClick(newNovel.getMonthClick());
                            updateNovel.setMonthRecommend(newNovel.getMonthRecommend());
                            updateNovel.setTotalClick(newNovel.getTotalClick());
                            updateNovel.setTotalRecommend(newNovel.getTotalRecommend());

                            List <ChapterDetail> needUpdateChapterDetails = Lists.newArrayList();
                            //测试必须要启动redis
                            List <Long> needUpdateChapterIds = idWorkerService.getOrderIds(now, needUpdateChapters.size());
                            Map <String, Chapter> chapterGrpByUrl = needUpdateChapters.stream().collect(Collectors.toMap(Chapter::getUrl, Function.identity()));
                            for (int j = 0; j < needUpdateChapters.size(); j++) {
                                needUpdateChapters.get(j).setId(needUpdateChapterIds.get(j));
                            }
                            for (int j = 0; j < needUpdateChapters.size(); j++) {
                                //拿到章节的具体内容及上下章索引
                                try {
                                    StopWatch stopWatch = new StopWatch(j + "-" + needUpdateChapters.size());
                                    stopWatch.start("任务1-getChapterDetailSpider");
                                    Chapter needUpdateChapter = needUpdateChapters.get(j);
                                    String needUpdateChapterUrl = needUpdateChapter.getUrl();
                                    IChapterDetailSpider chapterDetailSpider = NovelSpiderFactory.getChapterDetailSpider(needUpdateChapterUrl);
                                    stopWatch.stop();
                                    stopWatch.start("任务2-封装");
                                    ChapterDetail chapterDetail = chapterDetailSpider.getChapterDetail(needUpdateChapterUrl);
                                    chapterDetail.setContent(chapterDetail.getContent().replace("\n", "<br/>"));
                                    chapterDetail.setId(needUpdateChapter.getId());
                                    chapterDetail.setNovelId(needUpdateChapter.getNovelId());
                                    Long preId = 0L;
                                    Long nextId = 0L;
                                    if (null == chapterGrpByUrl || null == chapterGrpByUrl.get(chapterDetail.getPrev())) {
                                        preId = needUpdateChapter.getId();
                                    } else {
                                        preId = chapterGrpByUrl.get(chapterDetail.getPrev()).getId();
                                    }
                                    if (null == chapterGrpByUrl || null == chapterGrpByUrl.get(chapterDetail.getNext())) {
                                        nextId = needUpdateChapter.getId();
                                    } else {
                                        nextId = chapterGrpByUrl.get(chapterDetail.getNext()).getId();
                                    }
                                    chapterDetail.setPrevId(preId);
                                    chapterDetail.setNextId(nextId);
                                    needUpdateChapterDetails.add(chapterDetail);
                                    stopWatch.stop();
//                                    logger.info(stopWatch.prettyPrint());
                                } catch (IllegalStateException e) {
                                    logger.error("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,获取更新章节失败", e);
                                    e.printStackTrace();
                                }
                            }
                            //事务控制一致性
                            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
                            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            TransactionStatus status = transactionManager.getTransaction(definition);
                            try {
                                int updateRows = novelMapper.updateByPrimaryKeySelective(updateNovel);
                                int chapterRows = chapterMapper.batchInsert(needUpdateChapters);
                                List <ChapterDetail> needUpdateChapterDetailRows = chapterDetailMapper.insert(needUpdateChapterDetails);
                                if (updateRows <= 0 || chapterRows <= 0 || CollectionUtils.isEmpty(needUpdateChapterDetailRows)) {
                                    throw new RuntimeException("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,入库失败");
                                }
                                logger.info("成功更新 author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,更新" + needUpdateChapters.size() + "章");
                                transactionManager.commit(status);
                            } catch (TransactionException e) {
                                transactionManager.rollback(status);
                                throw new RuntimeException("Transaction提交事务失败");
                            } catch (Exception e) {
                                transactionManager.rollback(status);
                                logger.error("author=" + newNovel.getAuthor() + ",name=<" + newNovel.getName() + ">,更新入库失败", e);
                            }
                        }
                        Thread.sleep(1_000);
                    }
                    return key;
                }));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
        }
    }
}
