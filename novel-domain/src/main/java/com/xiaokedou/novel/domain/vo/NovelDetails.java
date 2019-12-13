package com.xiaokedou.novel.domain.vo;

import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.Novel;

import java.util.List;

public class NovelDetails {
    private Novel novel;
    private List <Chapter> chapters;

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public List <Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List <Chapter> chapters) {
        this.chapters = chapters;
    }

}
