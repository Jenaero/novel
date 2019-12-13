package com.xiaokedou.novel.spider.storage.impl;

import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.spider.storage.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AbstractMapperNovelStorage
 *
 * @Auther: renyajian
 * @Date: 2019/12/11
 */
public abstract class AbstractMapperNovelStorage implements Processor {

    protected static Map <String, String> tasks = new TreeMap <>();
    protected NovelMapper novelMapper;
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
                    new LinkedBlockingDeque <>(3000),
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
                        System.err.println("开始抓取[" + key + "] 的 URL:" + spider.next());
                        List <Novel> novels = iterator.next();
                        for (Novel novel : novels) {
                            novel.setFirstLetter(key.charAt(0) + "");    //设置小说的名字的首字母
                        }
                        novelMapper = SpringUtil.getApplicationContext().getBean(NovelMapper.class);
                        novelMapper.batchInsert(novels);
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
