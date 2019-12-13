package com.xiaokedou.novel.spider.storage.impl;

import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.spider.storage.Processor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractNovelStorage implements Processor {

    protected SqlSessionFactory sqlSessionFactory;
    protected Map <String, String> tasks = new TreeMap <>();

    public AbstractNovelStorage() throws FileNotFoundException {
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("conf/SqlMapConfig.xml"));
    }

    public AbstractNovelStorage(String confPath) throws FileNotFoundException {
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream(confPath));
    }

    public void process() {
        ExecutorService service = Executors.newFixedThreadPool(tasks.size());
        List <Future <String>> futures = new ArrayList <>(tasks.size());
        for (Entry <String, String> entry : tasks.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            futures.add(service.submit(new Callable <String>() {
                @Override
                public String call() throws Exception {
                    INovelSpider spider = NovelSpiderFactory.getNovelSpider(value);
                    Iterator <List <Novel>> iterator = spider.iterator(value, 10);
                    while (iterator.hasNext()) {
                        System.err.println("开始抓取[" + key + "] 的 URL:" + spider.next());
                        List <Novel> novels = iterator.next();
                        for (Novel novel : novels) {
                            novel.setFirstLetter(key.charAt(0) + "");    //设置小说的名字的首字母
                        }
                        SqlSession session = sqlSessionFactory.openSession();
                        session.insert("batchInsert", novels);
                        session.getMapper(NovelMapper.class).batchInsert(novels);
                        session.commit();
                        session.close();
                        Thread.sleep(1_000);
                    }
                    return key;
                }

            }));
        }
        service.shutdown();
        for (Future <String> future : futures) {
            try {
                System.out.println("抓取[" + future.get() + "]结束！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
