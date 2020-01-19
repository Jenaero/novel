package com.xiaokedou.novel.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaokedou.novel.common.annotation.RedisAnontation;
import com.xiaokedou.novel.dao.mapper.ChapterDetailMapper;
import com.xiaokedou.novel.dao.mapper.ChapterMapper;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.vo.ChapterList;
import com.xiaokedou.novel.domain.vo.EncryptedChapter;
import com.xiaokedou.novel.domain.vo.EncryptedChapterDetail;
import com.xiaokedou.novel.service.front.ChapterService;
import com.xiaokedou.novel.service.spider.IChapterDetailSpider;
import com.xiaokedou.novel.service.spider.IChapterSpider;
import com.xiaokedou.novel.service.util.EncryptUtils;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.service.util.NovelSpiderUtil;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

//@Service
public class ChapterServiceINewImpl implements ChapterService {
    {
        NovelSpiderUtil.setConfPath("../webapps/Spider-Rule.xml");
    }

    @Resource
    private ChapterMapper chapterDao;
    @Resource
    private NovelMapper novelDao;
    @Resource
    private ChapterDetailMapper chapterDetailDao;

    @RedisAnontation(clazz = EncryptedChapter.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedChapter> getChapters(String url) {
        IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
        List <Chapter> chapters = chapterSpider.getChapters(url);
        return EncryptUtils.encryptChapters(chapters);
    }


//    @RedisAnontation(clazz = EncryptedChapterDetail.class, serialType = RedisAnontation.SerialType.OBJ)
    @Override
    public EncryptedChapterDetail getChapterDetail(String url) {
        LambdaQueryWrapper <Chapter> queryWrapper = new LambdaQueryWrapper <Chapter>().eq(Chapter::getUrl, url);
        Chapter chapter = chapterDao.selectOne(queryWrapper);
        if (null == chapter){
            return new EncryptedChapterDetail();
        }
//        ChapterDetail chapterDetail = chapterDetailDao.findById(String.valueOf(chapter.getId())).get();
        ChapterDetail chapterDetail = new ChapterDetail();
        chapterDetail.setId(chapter.getId());
//        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("novelId", "prevId", "nextId");
//        Example <ChapterDetail> example = Example.of(chapterDetail, matcher);
        Example <ChapterDetail> example = Example.of(chapterDetail);
        Optional <ChapterDetail> optionalDetail = chapterDetailDao.findOne(example);
        chapterDetail = optionalDetail.get();
        chapterDetail.setContent(chapterDetail.getContent().replace("\n", "<br/>"));
        EncryptedChapterDetail encryptChapterDetail = EncryptUtils.encryptChapterDetail(chapterDetail);
        return encryptChapterDetail;
    }

//    @RedisAnontation(clazz = ChapterList.class, serialType = RedisAnontation.SerialType.OBJ)
    @Override
    public ChapterList getChapters(String url, int offset, int length) {
        ChapterList chapterList = new ChapterList();
        Novel novel = novelDao.selectOne(new LambdaQueryWrapper <Novel>().eq(Novel::getChapterUrl, url));
        if (null == novel){
            return chapterList;
        }
        LambdaQueryWrapper <Chapter> queryWrapper = new LambdaQueryWrapper <Chapter>().eq(Chapter::getNovelId, novel.getId());
        Integer total = chapterDao.selectCount(queryWrapper);
        queryWrapper.last("limit " + offset + "," + length);
        List <Chapter> chapters = chapterDao.selectList(queryWrapper);
        //返回的每章节地址加密
        chapterList.setChapters(EncryptUtils.encryptChapters(chapters));
        chapterList.setTotal(total);
        return chapterList;
    }

    @RedisAnontation(clazz = Chapter.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <Chapter> getChaptersByOffset(String url, int offset,
                                              int length) {
        IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
        List <Chapter> chapters = chapterSpider.getChapters(url, offset, length);
        return chapters;
    }
}
