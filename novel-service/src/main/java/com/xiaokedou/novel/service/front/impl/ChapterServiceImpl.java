package com.xiaokedou.novel.service.front.impl;

import com.xiaokedou.novel.common.annotation.RedisAnontation;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {
    {
        NovelSpiderUtil.setConfPath("D:\\learns\\novel\\novel-service\\src\\main\\resources\\rule\\Spider-Rule.xml");
//	NovelSpiderUtil.setConfPath("/conf/novelSpider/Spider-Rule.xml");
    }

    @RedisAnontation(clazz = EncryptedChapter.class, serialType = RedisAnontation.SerialType.LIST)
    @Override
    public List <EncryptedChapter> getChapters(String url) {
        IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
        List <Chapter> chapters = chapterSpider.getChapters(url);
        return EncryptUtils.encryptChapters(chapters);
    }


    @RedisAnontation(clazz = EncryptedChapterDetail.class, serialType = RedisAnontation.SerialType.OBJ)
    @Override
    public EncryptedChapterDetail getChapterDetail(String url) {
        IChapterDetailSpider spider = NovelSpiderFactory.getChapterDetailSpider(url);
        ChapterDetail chapterDetail = spider.getChapterDetail(url);
        chapterDetail.setContent(chapterDetail.getContent().replace("\n", "<br/>"));
        EncryptedChapterDetail encryptChapterDetail = EncryptUtils.encryptChapterDetail(chapterDetail);
        return encryptChapterDetail;
    }

    @RedisAnontation(clazz = ChapterList.class, serialType = RedisAnontation.SerialType.OBJ)
    @Override
    public ChapterList getChapters(String url, int offset, int length) {
        IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
        //获取章节列表dom
        Elements elements = chapterSpider.getChapterElements(url);
        List <Chapter> chapters = chapterSpider.getChapterFromElements(elements, offset, length);
        int size = elements.size();
        //拼接chapters 列表
        chapters = chapterSpider.getChapterFromElements(elements, offset, length);
        ChapterList chapterList = new ChapterList();
        //返回的每章节地址加密
        chapterList.setChapters(EncryptUtils.encryptChapters(chapters));
        chapterList.setTotal(size);
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
