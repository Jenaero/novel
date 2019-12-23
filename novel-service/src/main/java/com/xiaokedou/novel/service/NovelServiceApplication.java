package com.xiaokedou.novel.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xiaokedou.novel")
public class NovelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelServiceApplication.class, args);
	}

}
