package com.xiaokedou.novel.domain.po;

import lombok.Data;

import java.io.Serializable;


@Data
public class ChapterDetail implements Serializable {
    private static final long serialVersionUID = -7303060644500661569L;
    private String title;
    private String content;
    private String prev;
    private String next;
}
