package com.xiaokedou.novel.service.spider.impl.novel;


import com.google.common.base.Splitter;
import com.xiaokedou.novel.domain.po.NovelInfo;
import com.xiaokedou.novel.service.util.NovelSpiderUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Random;

public class MpzwNovelInfoSpider extends AbstractNovelInfoSpider {

    @Override
    public NovelInfo getNovelInfo(String url) {
        try {
            String result = super.crawl(url);
            Document doc = Jsoup.parse(result);
            doc.setBaseUri(url);
            String novelUrl = url;
            String img = doc.select("#fmimg").first().getElementsByTag("img").attr("src");
            String name = doc.select("#info").first().getElementsByTag("h1").text().trim();
            String chapterUrl = url;

            String type = doc.select(".con_top").text().split(">")[1].trim();
            String author = doc.selectFirst("#info").getElementsByTag("p").get(0).text().split("：")[1].trim();
            String status = "连载";

            Random random = new Random();
            String collection = String.valueOf(random.nextInt(10_0000));
            String length = String.valueOf(random.nextInt(1000_0000));
            String lastUpdateTime = doc.select("#info p").get(1).text().split("：")[1].trim();

            String totalClick = String.valueOf(random.nextInt(10_0000));
            String monthClick = String.valueOf(random.nextInt(5_0000));
            String weekClick = String.valueOf(random.nextInt(1_0000));

            String totalRecommend = String.valueOf(random.nextInt(10_0000));
            String monthRecommend = String.valueOf(random.nextInt(5_0000));
            String weekRecommend = String.valueOf(random.nextInt(1_0000));

            String lastUpdateChapter = doc.select("#list dl dd").get(0).text().trim();
            //todo 下面还没改完，先整对接
            String lastUpdateChapterUrl = doc.select("#list dl dd").get(0).text().trim();
            String introduction = doc.select("#intro p").first().text().trim();

            NovelInfo novelInfo = new NovelInfo();
            novelInfo.set(name, author, img, Integer.parseInt(collection), Integer.parseInt(length),
                    Integer.parseInt(totalClick),
                    Integer.parseInt(monthClick),
                    Integer.parseInt(weekClick),
                    Integer.parseInt(totalRecommend),
                    Integer.parseInt(monthRecommend),
                    Integer.parseInt(weekRecommend),
                    introduction, "", novelUrl, chapterUrl,
                    type, lastUpdateChapter,
                    NovelSpiderUtil.getDate(lastUpdateTime, "yy-MM-dd"),
                    NovelSpiderUtil.getNovelStatus(status));
            return novelInfo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
