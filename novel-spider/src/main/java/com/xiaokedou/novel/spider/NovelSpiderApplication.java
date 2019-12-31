package com.xiaokedou.novel.spider;

import com.xiaokedou.novel.spider.storage.Processor;
import com.xiaokedou.novel.spider.storage.impl.TestNovelStorageImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xiaokedou.novel")
@MapperScan("com.xiaokedou.novel.dao.mapper")
public class NovelSpiderApplication{

    public static void main(String[] args) {
        SpringApplication.run(NovelSpiderApplication.class, args);
        Processor processor = new TestNovelStorageImpl();
        processor.process();
    }
}
