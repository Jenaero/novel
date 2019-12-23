package com.xiaokedou.novel.domain.po;

import lombok.Data;

import java.io.Serializable;


@Data
public class ChapterDetail implements Serializable {
    private static final long serialVersionUID = -7303060644500661569L;
    //主键Id
    private String id;
    //小说id
    private String novelId;
    //小说
    private String title;
    //文本
    private String content;
    //原站上一章地址
    private String prev;
    //原站下一章地址
    private String next;
    //下一章主键Id
    private String prevId;
    //下一张主键Id
    private String nextId;
}
