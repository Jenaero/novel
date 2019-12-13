package com.xiaokedou.novel.service.spider;

import com.xiaokedou.novel.domain.po.ChapterDetail;

public interface IChapterDetailSpider {
	/**
	 * 给我一个url，我就给你一个对应网站的章节内容实体
	 * @param url
	 * @return
	 */
	public ChapterDetail getChapterDetail(String url);
}
