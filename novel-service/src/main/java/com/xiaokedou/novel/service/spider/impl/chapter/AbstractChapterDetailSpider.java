package com.xiaokedou.novel.service.spider.impl.chapter;


import com.xiaokedou.novel.common.enums.NovelSiteEnum;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.service.spider.IChapterDetailSpider;
import com.xiaokedou.novel.service.spider.impl.AbstractSpider;
import com.xiaokedou.novel.service.util.NovelSpiderUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

public abstract class AbstractChapterDetailSpider extends AbstractSpider implements IChapterDetailSpider {

    @Override
    public ChapterDetail getChapterDetail(String url) {
        ChapterDetail detail = null;
        for (int i = 0; i < 3; i++) {
            try {
                String result = super.crawl(url);
                result = result.replace("&nbsp;", " ").replace("<br />", "${line}").replace("<br/>", "${line}");
                Document doc = Jsoup.parse(result);
                doc.setBaseUri(url);
                Map <String, String> contexts = NovelSpiderUtil.getContext(NovelSiteEnum.getEnumByUrl(url));

                //拿标题内容
                String titleSelector = contexts.get("chapter-detail-title-selector");
                String[] splits = titleSelector.split("\\,");
                splits = parseSelector(splits);
                detail = new ChapterDetail();
                detail.setTitle(doc.select(splits[0]).get(Integer.parseInt(splits[1])).text());

                //拿章节内容
                String contentSelector = contexts.get("chapter-detail-content-selector");
                detail.setContent(doc.select(contentSelector).first().text().replace("${line}", "\n"));

                //拿前一章的地址ַ
                String prevSelector = contexts.get("chapter-detail-prev-selector");
                splits = prevSelector.split("\\,");
                splits = parseSelector(splits);
                detail.setPrev(doc.select(splits[0]).get(Integer.parseInt(splits[1])).absUrl("href"));

                //拿后一章的地址ַ
                String nextSelector = contexts.get("chapter-detail-next-selector");
                splits = nextSelector.split("\\,");
                splits = parseSelector(splits);
                detail.setNext(doc.select(splits[0]).get(Integer.parseInt(splits[1])).absUrl("href"));

                return detail;
            } catch (Exception e) {
            }
        }
        throw new RuntimeException(url+",尝试了三次依然下载失败!");
    }

    /**
     * 处理具体元素的下标索引
     */
    private String[] parseSelector(String[] splits) {
        String[] newSplits = new String[2];
        if (splits.length == 1) {
            newSplits[0] = splits[0];
            newSplits[1] = "0";
            return newSplits;
        } else {
            return splits;
        }
    }

}
