package com.xiaokedou.novel.spider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.xiaokedou.novel")
@MapperScan("com.xiaokedou.novel.dao.mapper")
@EnableScheduling
public class NovelSpiderApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(NovelSpiderApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NovelSpiderApplication.class);
    }
}
