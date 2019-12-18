package com.xiaokedou.novel.service.spider.impl.novel;


import com.xiaokedou.novel.common.enums.NovelSiteEnum;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.po.NovelInfo;
import com.xiaokedou.novel.service.spider.INovelInfoSpider;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.service.util.NovelSpiderUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔下文学网站的书籍列表爬取
 */
public class BxwxNovelSpider extends AbstractNovelSpider {
    public BxwxNovelSpider() {
    }

    @Override
    public List <Novel> getsNovel(String url, Integer maxTryTimes) {
        List <Novel> novels = new ArrayList <>();
        INovelInfoSpider infoSpider = NovelSpiderFactory.getNovelInfoSpider(url);
        try {
            Elements lis = super.getsTr(url, maxTryTimes);
            for (int index = 1, size = lis.size(); index < size; index++) {
                try {

//                    Element li = lis.get(index);
//                    Novel novel = new Novel();
//                    novel.setName(li.getElementsByTag("a").text());
//                    String novelUrl = li.getElementsByTag("a").first().absUrl("href");
//                    novel.setNovelUrl(novelUrl);
//                    NovelInfo novelInfo = infoSpider.getNovelInfo(novelUrl);
//                    novel.setNovelUrl(novelInfo.getNovelUrl());
//                    novel.setChapterUrl(novelInfo.getChapterUrl());
//                    novel.setLastUpdateChapter(novelInfo.getLastUpdateChapter());
//                    novel.setLastUpdateChapterUrl(novelInfo.getLastUpdateChapterUrl());
//                    novel.setLastUpdateTime(novelInfo.getLastUpdateTime());
//                    novel.setImg(novelInfo.getImg());
//                    novel.setAuthor(novelInfo.getAuthor());
//                    novel.setIntroduction(novelInfo.getIntroduction());
//                    novel.setStatus(novelInfo.getStatus());


                    Element tr = lis.get(index);
                    Elements tds = tr.getElementsByTag("td");
                    Novel novel = new Novel();
                    novel.setName(tds.first().text());
                    String novelUrl = tds.first().getElementsByTag("a").first().absUrl("href");
                    NovelInfo novelInfo = infoSpider.getNovelInfo(novelUrl);
                    novel.setNovelInfo(novelInfo);

                    novel.setNovelUrl(novelUrl);
                    novel.setLastUpdateChapter(tds.get(1).text());
                    novel.setLastUpdateChapterUrl(tds.get(1).getElementsByTag("a").first().absUrl("href"));
                    novel.setAuthor(tds.get(2).text());

                    novel.setLastUpdateTime(NovelSpiderUtil.getDate(tds.get(4).text(), "yy-MM-dd"));//2016-10-14 yyyy-MM-dd
                    novel.setStatus(NovelSpiderUtil.getNovelStatus(tds.get(5).text()));
                    novel.setPlatformId(NovelSiteEnum.getEnumByUrl(url).getId());
                    novels.add(novel);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return novels;
    }
}
