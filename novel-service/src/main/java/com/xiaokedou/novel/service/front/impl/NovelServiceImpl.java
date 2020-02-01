package com.xiaokedou.novel.service.front.impl;

import com.xiaokedou.novel.common.annotation.RedisAnontation;
import com.xiaokedou.novel.common.base.Page;
import com.xiaokedou.novel.common.base.Pager;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.vo.ChapterContent;
import com.xiaokedou.novel.domain.vo.EncryptedNovel;
import com.xiaokedou.novel.service.front.NovelService;
import com.xiaokedou.novel.service.util.EncryptUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NovelServiceImpl implements NovelService {
    @Resource
    private NovelMapper novelDao;

    @RedisAnontation(clazz = Novel.class, serialType = RedisAnontation.SerialType.OBJ)
    public Novel getOneNovel(Long id) {
        Novel novel = novelDao.selectByPrimaryKey(1l);
        return novel;
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getRecommendNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByRecommend(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getHotNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByMonthRecommend(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getFanNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByWeekRecommend(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getBoyNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByBoyRecommend(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getGirlNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByGirlRecommend(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @RedisAnontation(clazz = EncryptedNovel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedNovel> getOtherNovels(Pager pager) {
        List <Novel> novels = novelDao.selectPageNovelsByOther(pager);
        return EncryptUtils.encryptNovels(novels);
    }

    @Override
    public Novel getNovelByUrl(String url) {
        return novelDao.selectByNovelUrl(url);
    }

    @Override
    public EncryptedNovel getNovelById(long key) {
        // TODO Auto-generated method stub
        return EncryptUtils.encryptNovel(novelDao.selectByPrimaryKey(key));
    }

    @Override
    public ChapterContent ChapterContent(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 奇怪
     */
//    @RedisAnontation(clazz = Novel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <Novel> searchNovelByNameAuthor(String kw, Pager pager) {
        Page <String> page = new Page <String>();
        page.setT(kw);
        page.setPager(pager);
        return novelDao.selectPageNovelsByKeyWord(page);
    }

//    @RedisAnontation(clazz = Novel.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <Novel> searchNovelByTypes(List <String> types, Pager pager) {
        Page <String> page = new Page <String>();
        page.setList(types);
        page.setPager(pager);
        return novelDao.selectPageNovelsByTypes(page);
    }

    @Override
    public List <Novel> getPageOrderByLastUpdateTime(Pager pager) {
        Page <String> page = new Page <String>();
        page.setPager(pager);
        return novelDao.selectPageOrderByLastUpdateTime(page);
    }

    @Override
    public List <Novel> getPageOrderByAddTime(Pager pager) {
        Page <String> page = new Page <String>();
        page.setPager(pager);
        return novelDao.selectPageOrderByAddTime(page);
    }
}
