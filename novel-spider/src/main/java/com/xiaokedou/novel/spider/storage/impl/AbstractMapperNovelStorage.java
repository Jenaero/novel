package com.xiaokedou.novel.spider.storage.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
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
 * AbstractMapperNovelStorage
 *
 * @Auther: renyajian
 * @Date: 2019/12/11
 */
public abstract class AbstractMapperNovelStorage implements Processor {

    private final Logger logger = LoggerFactory.getLogger(AbstractMapperNovelStorage.class);


    protected static Map <String, String> tasks = new TreeMap <>();
    protected NovelMapper novelMapper;
    protected ChapterMapper chapterMapper;
    protected ChapterDetailMapper chapterDetailMapper;
    protected IdWorkerService idWorkerService;
    protected final static Integer keepAliveTime = 120;
    protected ThreadPoolExecutor threadPoolExecutor = null;
    protected PlatformTransactionManager transactionManager;

    public AbstractMapperNovelStorage() {
        ApplicationContext context = SpringUtil.getApplicationContext();
        novelMapper = context.getBean(NovelMapper.class);
        chapterMapper = context.getBean(ChapterMapper.class);
        chapterDetailMapper = context.getBean(ChapterDetailMapper.class);
        idWorkerService = context.getBean(IdWorkerService.class);
        transactionManager = context.getBean(PlatformTransactionManager.class);
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
                    new NamedThreadFactory("novel")
            );
            List <Future <String>> futures = new ArrayList <>(tasks.size());
            for (Map.Entry <String, String> entry : tasks.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                futures.add(threadPoolExecutor.submit(() -> {
                    INovelSpider spider = NovelSpiderFactory.getNovelSpider(value);
                    Iterator <List <Novel>> iterator = spider.iterator(value, 10);
                    while (iterator.hasNext()) {
                        System.err.println(Thread.currentThread().getName() + "开始抓取[" + key + "] 的 URL:" + spider.next());
                        List <Novel> novels = iterator.next();
                        Date now = new Date();
                        for (int i = 0; i < novels.size(); i++) {
                            Novel novel = novels.get(i);
                            //重复下载跳过
                            Integer novelCount = novelMapper.selectCount(new LambdaQueryWrapper <Novel>()
                                    .eq(Novel::getName, novel.getName())
                                    .eq(Novel::getAuthor, novel.getAuthor()));
                            if (novelCount > 0) {
                                logger.warn("author=" + novel.getAuthor() + ",name=<" + novel.getName() + ">,已存在,跳过下载！");
                                continue;
                            }
                            List <Chapter> chapters = Lists.newArrayList();
                            List <ChapterDetail> chapterDetails = Lists.newArrayList();
                            //测试必须要启动redis
                            novel.setId(idWorkerService.getOrderId(now));
                            novel.setFirstLetter(key.charAt(0) + "");    //设置小说的名字的首字母
                            //todo 拿到小说的所有章节
                            IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(novel.getChapterUrl());
                            //拼接chapters 列表
                            chapters = chapterSpider.getChaptersByNovelChapterUrl(novel);
                            List <Long> chapterIds = idWorkerService.getOrderIds(now, chapters.size());
                            Map <String, Chapter> chapterGrpByUrl = chapters.stream().collect(Collectors.toMap(Chapter::getUrl, Function.identity()));
                            for (int j = 0; j < chapters.size(); j++) {
                                chapters.get(j).setId(chapterIds.get(j));
                            }
                            for (int j = 0; j < chapters.size(); j++) {
                                //todo 拿到章节的具体内容及上下章索引
                                try {
                                    StopWatch stopWatch = new StopWatch(j + "-" + chapters.size());
                                    stopWatch.start("任务1-getChapterDetailSpider");
                                    Chapter chapter = chapters.get(j);
                                    IChapterDetailSpider chapterDetailSpider = NovelSpiderFactory.getChapterDetailSpider(chapter.getUrl());
                                    stopWatch.stop();
                                    stopWatch.start("任务2-封装");
                                    ChapterDetail chapterDetail = chapterDetailSpider.getChapterDetail(chapter.getUrl());
                                    chapterDetail.setContent(chapterDetail.getContent().replace("\n", "<br/>"));
                                    chapterDetail.setId(chapter.getId());
                                    chapterDetail.setNovelId(chapter.getNovelId());
                                    Long preId = 0L;
                                    Long nextId = 0L;
                                    if (null == chapterGrpByUrl || null == chapterGrpByUrl.get(chapterDetail.getPrev())) {
                                        preId = chapter.getId();
                                    } else {
                                        preId = chapterGrpByUrl.get(chapterDetail.getPrev()).getId();
                                    }
                                    if (null == chapterGrpByUrl || null == chapterGrpByUrl.get(chapterDetail.getNext())) {
                                        nextId = chapter.getId();
                                    } else {
                                        nextId = chapterGrpByUrl.get(chapterDetail.getNext()).getId();
                                    }
                                    chapterDetail.setPrevId(preId);
                                    chapterDetail.setNextId(nextId);
                                    chapterDetails.add(chapterDetail);
                                    stopWatch.stop();
                                    logger.info(stopWatch.prettyPrint());
                                } catch (IllegalStateException e) {
                                    logger.error("author=" + novel.getAuthor() + ",name=" + novel.getName() + ",获取失败", e);
                                    e.printStackTrace();
                                }
                            }
                            //fixme 新增校验 事务一致性
                            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
                            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            TransactionStatus status = transactionManager.getTransaction(definition);
                            try {
                                int novelRows = novelMapper.insert(novel);
                                int chapterRows = chapterMapper.batchInsert(chapters);
                                List <ChapterDetail> chapterDetailList = chapterDetailMapper.insert(chapterDetails);
                                if (novelRows <= 0 || chapterRows <= 0 || CollectionUtils.isEmpty(chapterDetailList)) {
                                    throw new RuntimeException("author=" + novel.getAuthor() + ",name=" + novel.getName() + ",入库失败");
                                }
                                logger.info("成功下载 author=" + novel.getAuthor() + ",name=" + novel.getName());
                                transactionManager.commit(status);
                            } catch (TransactionException e) {
                                transactionManager.rollback(status);
                                throw new RuntimeException("Transaction提交事务失败");
                            } catch (Exception e) {
                                transactionManager.rollback(status);
                                logger.error("author=" + novel.getAuthor() + ",name=" + novel.getName() + ",入库失败", e);
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
