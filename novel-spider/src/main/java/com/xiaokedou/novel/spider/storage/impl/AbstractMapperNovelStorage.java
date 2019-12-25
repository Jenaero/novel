package com.xiaokedou.novel.spider.storage.impl;

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
import org.springframework.context.ApplicationContext;
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

    protected static Map <String, String> tasks = new TreeMap <>();
    protected NovelMapper novelMapper;
    protected ChapterMapper chapterMapper;
    protected ChapterDetailMapper chapterDetailMapper;
    protected IdWorkerService idWorkerService;
    protected final static Integer keepAliveTime = 120;
    protected ThreadPoolExecutor threadPoolExecutor = null;

    public AbstractMapperNovelStorage() {
        ApplicationContext context = SpringUtil.getApplicationContext();
        novelMapper = context.getBean(NovelMapper.class);
        chapterMapper = context.getBean(ChapterMapper.class);
        chapterDetailMapper = context.getBean(ChapterDetailMapper.class);
        idWorkerService = context.getBean(IdWorkerService.class);
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
                            List <Chapter> chapters = Lists.newArrayList();
                            List <ChapterDetail> chapterDetails = Lists.newArrayList();
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
                                    StopWatch stopWatch = new StopWatch(j + "-"+chapters.size());
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
                                    System.out.println(stopWatch.prettyPrint());
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                    System.out.println(e);
                                }
                            }
                            System.out.println("下载第" + i + "本！");
                            novelMapper.insert(novel);
                            chapterMapper.batchInsert(chapters);
                            chapterDetailMapper.insert(chapterDetails);
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
