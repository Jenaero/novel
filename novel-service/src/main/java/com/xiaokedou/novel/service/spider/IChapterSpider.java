package com.xiaokedou.novel.service.spider;

import com.xiaokedou.novel.domain.po.Chapter;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public interface IChapterSpider {
	/**
	 * 给我们一个完整的url，我们就给你返回所有的章节列表，返回的章节列表必须是有顺序的
	 * @param url
	 * @return
	 */
	public List<Chapter> getChapters(String url);
	/**
	 * 给我们一个完整的url，我们就给你返回某几章
	 * @param url
	 * @return
	 */
	public List<Chapter> getChapters(String url, int offset, int length);

	public Elements getChapterElements(String url);

	public Chapter getChapterFromElement(Element e);

	public List<Chapter>  getChapterFromElements(Elements e, int offset, int length);
}
