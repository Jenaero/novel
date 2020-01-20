package com.xiaokedou.novel.service.util;


import com.xiaokedou.novel.common.enums.NovelSiteEnum;
import com.xiaokedou.novel.service.spider.IChapterDetailSpider;
import com.xiaokedou.novel.service.spider.IChapterSpider;
import com.xiaokedou.novel.service.spider.INovelInfoSpider;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.BxwxChapterSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.DefaultChapterDetailSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.MpzwChapterSpider;
import com.xiaokedou.novel.service.spider.impl.novel.BxwxNovelInfoSpider;
import com.xiaokedou.novel.service.spider.impl.novel.BxwxNovelSpider;
import com.xiaokedou.novel.service.spider.impl.novel.MpzwNovelInfoSpider;
import com.xiaokedou.novel.service.spider.impl.novel.MpzwNovelSpider;

/**
 * 生产书籍列表的实现类
 */
public final class NovelSpiderFactory {
    private NovelSpiderFactory() {
    }

    public static BxwxNovelSpider bxwxNovelSpider;
    public static BxwxNovelInfoSpider bxwxNovelInfoSpider;
    public static BxwxChapterSpider bxwxChapterSpider;
    public static DefaultChapterDetailSpider defaultChapterDetailSpider;

    public static MpzwNovelSpider mpzwNovelSpider;
    public static MpzwNovelInfoSpider mpzwNovelInfoSpider;
    public static MpzwChapterSpider mpzwChapterSpider;


    public static INovelSpider getNovelSpider(String url) {
        NovelSiteEnum novelSiteEnum = NovelSiteEnum.getEnumByUrl(url);
        switch (novelSiteEnum) {
            case Bxwx:
                return bxwxNovelSpider == null ? new BxwxNovelSpider() : bxwxNovelSpider;
            case Bxwx2:
                return bxwxNovelSpider == null ? new BxwxNovelSpider() : bxwxNovelSpider;
            case Mpzw:
                return mpzwNovelSpider == null ? new MpzwNovelSpider() : mpzwNovelSpider;
            default:
                throw new RuntimeException(url + "暂时不被支持");
        }
    }

    public static INovelInfoSpider getNovelInfoSpider(String url) {
        NovelSiteEnum novelSiteEnum = NovelSiteEnum.getEnumByUrl(url);
        switch (novelSiteEnum) {
            case Bxwx:
                return bxwxNovelInfoSpider == null ? new BxwxNovelInfoSpider() : bxwxNovelInfoSpider;
            case Bxwx2:
                return bxwxNovelInfoSpider == null ? new BxwxNovelInfoSpider() : bxwxNovelInfoSpider;
            case Mpzw:
                return mpzwNovelInfoSpider == null ? new MpzwNovelInfoSpider() : mpzwNovelInfoSpider;
            default:
                throw new RuntimeException(url + "暂时不被支持");
        }
    }

    public static IChapterSpider getChapterSpider(String url) {
        NovelSiteEnum novelSiteEnum = NovelSiteEnum.getEnumByUrl(url);
        switch (novelSiteEnum) {
            case Bxwx:
                return bxwxChapterSpider == null ? new BxwxChapterSpider() : bxwxChapterSpider;
            case Bxwx2:
                return bxwxChapterSpider == null ? new BxwxChapterSpider() : bxwxChapterSpider;
            case Mpzw:
                return mpzwChapterSpider == null ? new MpzwChapterSpider() : mpzwChapterSpider;
            default:
                throw new RuntimeException(url + "暂时不被支持");
        }
    }

    public static IChapterDetailSpider getChapterDetailSpider(String url) {
        NovelSiteEnum novelSiteEnum = NovelSiteEnum.getEnumByUrl(url);
        switch (novelSiteEnum) {
            case Bxwx:
                return defaultChapterDetailSpider == null ? new DefaultChapterDetailSpider() : defaultChapterDetailSpider;
            case Bxwx2:
                return defaultChapterDetailSpider == null ? new DefaultChapterDetailSpider() : defaultChapterDetailSpider;
            case Mpzw:
                return defaultChapterDetailSpider == null ? new DefaultChapterDetailSpider() : defaultChapterDetailSpider;
            default:
                throw new RuntimeException(url + "暂时不被支持");
        }
    }
}
