package com.xiaokedou.novel.spider.storage.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xiaokedou.novel.common.util.KeyUtil;
import com.xiaokedou.novel.dao.mapper.ChapterDetailMapper;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.service.spider.IChapterDetailSpider;
import com.xiaokedou.novel.service.spider.IChapterSpider;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.spider.storage.Processor;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
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
    protected ChapterDetailMapper chapterDetailMapper;
    protected final static Integer keepAliveTime = 120;
    protected ThreadPoolExecutor threadPoolExecutor = null;

    public AbstractMapperNovelStorage() {

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
                        List <Chapter> chapters = Lists.newArrayList();
                        List <ChapterDetail> chapterDetails = Lists.newArrayList();
                        for (Novel novel : novels) {
                            novel.setId(KeyUtil.getRandomKey());
                            novel.setFirstLetter(key.charAt(0) + "");    //设置小说的名字的首字母
                            //todo 拿到小说的所有章节
                            IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(novel.getChapterUrl());
                            //拼接chapters 列表
                            chapters = chapterSpider.getChaptersByNovelChapterUrl(novel);
                            for (Chapter chapter : chapters){
                                //todo 拿到章节的具体内容及上下章索引
                                chapter.setId(KeyUtil.getRandomKey());
                                IChapterDetailSpider chapterDetailSpider = NovelSpiderFactory.getChapterDetailSpider(chapter.getUrl());
                                ChapterDetail chapterDetail = chapterDetailSpider.getChapterDetail(chapter.getUrl());
                                chapterDetail.setContent(chapterDetail.getContent().replace("\n", "<br/>"));
                                chapterDetail.setId(chapter.getId());
                                chapterDetail.setNovelId(chapter.getNovelId());
                                String preId = chapters.stream().filter(prev -> prev.getUrl().equals(chapterDetail.getPrev()))
                                        .collect(Collectors.toList()).get(0).getId();
                                chapterDetail.setPrevId(preId);
                                String nextId = chapters.stream().filter(next -> next.getUrl().equals(chapterDetail.getNext()))
                                        .collect(Collectors.toList()).get(0).getId();
                                chapterDetail.setNextId(nextId);
                                chapterDetails.add(chapterDetail);
                            }
                        }
                        System.out.println(JSON.toJSONString(novels));
                        novelMapper = SpringUtil.getApplicationContext().getBean(NovelMapper.class);
                        chapterDetailMapper = SpringUtil.getApplicationContext().getBean(ChapterDetailMapper.class);

                        novelMapper.batchInsert(novels);
                        Thread.sleep(5_000);
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
