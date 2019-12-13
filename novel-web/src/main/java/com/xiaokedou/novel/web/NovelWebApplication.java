package com.xiaokedou.novel.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xiaokedou.novel")
@MapperScan("com.xiaokedou.novel.dao.mapper")
public class NovelWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NovelWebApplication.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(NovelWebApplication.class)
                .run(args);
    }
}
