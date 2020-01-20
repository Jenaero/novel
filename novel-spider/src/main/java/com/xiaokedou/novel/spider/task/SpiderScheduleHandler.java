package com.xiaokedou.novel.spider.task;

import com.xiaokedou.novel.spider.storage.Processor;
import com.xiaokedou.novel.spider.storage.impl.BxwxAllNovelStorageImpl;
import com.xiaokedou.novel.spider.storage.impl.BxwxUpdateNovelStorageImpl;
import com.xiaokedou.novel.spider.storage.impl.MpzwAllNovelStorageImpl;
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

//    @Scheduled(fixedRate = 100 * 24 * 3600000)
    public void bxwxAll() {
        Processor processor = new BxwxAllNovelStorageImpl();
        processor.process();
    }

    //每天凌晨一点执行
//    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(fixedRate = 100*24*3600000)
    public void bxwxUpdate() {
        Processor processor = new BxwxUpdateNovelStorageImpl();
        processor.process();
    }

    @Scheduled(fixedRate = 100 * 24 * 3600000)
    public void mpzwAll() {
        Processor processor = new MpzwAllNovelStorageImpl();
        processor.process();
    }

}
