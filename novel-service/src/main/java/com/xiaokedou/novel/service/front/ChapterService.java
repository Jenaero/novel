package com.xiaokedou.novel.service.front;

import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.vo.ChapterList;
import com.xiaokedou.novel.domain.vo.EncryptedChapter;
import com.xiaokedou.novel.domain.vo.EncryptedChapterDetail;

import java.util.List;

public interface ChapterService {

    List <EncryptedChapter> getChapters(String url);

    List <Chapter> getChaptersByOffset(String url, int offset, int length);

    EncryptedChapterDetail getChapterDetail(String url);

    ChapterList getChapters(String url, int offset, int length);

    List <Chapter> getChaptersByNovelId(Long id);
}
