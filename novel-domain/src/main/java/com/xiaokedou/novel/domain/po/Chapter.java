package com.xiaokedou.novel.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    private static final long serialVersionUID = -7574082095190014403L;
    //小说Id
    private String id;
    //章节Id
    private String novelId;
    //书名
    private String name;
    //作者
    private String author;
    //章节介绍 ex:第一章 特招学子
    private String title;
    //原站地址
    private String url;
}
