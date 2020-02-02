package com.xiaokedou.novel.service.front;

import com.xiaokedou.novel.common.base.Pager;
import com.xiaokedou.novel.common.enums.NovelTypeEnum;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.vo.ChapterContent;
import com.xiaokedou.novel.domain.vo.EncryptedNovel;

import java.util.List;


public interface NovelService {

    public Novel getOneNovel(Long id);

    public List <EncryptedNovel> getRecommendNovels(Pager pager);

    public List <EncryptedNovel> getHotNovels(Pager pager);

    public List <EncryptedNovel> getFanNovels(Pager pager);

    public List <EncryptedNovel> getBoyNovels(Pager pager);

    public List <EncryptedNovel> getGirlNovels(Pager pager);

    public List <EncryptedNovel> getOtherNovels(Pager pager);

    public Novel getNovelByUrl(String url);

    public EncryptedNovel getNovelById(long key);

    public ChapterContent ChapterContent(String key);

    public List <Novel> searchNovelByNameAuthor(String kw, Pager pager);

    public List <Novel> searchNovelByTypes(List <String> types, Pager pager);

    public List<Novel> getPageByTypeOrderByLastUpdateTime(Pager pager, NovelTypeEnum typeEnum);

    public List<Novel> getPageByTypeOrderByAddTime(Pager pager,NovelTypeEnum typeEnum);

    public List<Novel> getPageByTypeOrderByTotalClick(Pager pager, NovelTypeEnum f);
}
