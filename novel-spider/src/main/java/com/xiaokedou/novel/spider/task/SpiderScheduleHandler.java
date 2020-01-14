package com.xiaokedou.novel.spider.task;

import com.xiaokedou.novel.spider.storage.Processor;
import com.xiaokedou.novel.spider.storage.impl.BxwxNovelStorageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SpiderScheduleHandler
 *
 * @Author: renyajian
 * @Date: 2020/1/2
 */
@Component
public class SpiderScheduleHandler {

    @Scheduled(fixedRate = 100*24*3600000)
    public void init() {
        Processor processor = new BxwxNovelStorageImpl();
        processor.process();
    }
}
