package com.xiaokedou.novel.domain.vo;

import com.xiaokedou.novel.domain.po.Chapter;

import java.util.List;

public class ChapterDetail {
	private String name;

    private String author;

	private List<Chapter> chapters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	@Override
	public String toString() {
		return "ChapterDetail [name=" + name + ", author=" + author
				+ ", chapters=" + chapters + "]";
	}
	
//	public static void main(String[] args) {
//		NovelSpiderUtil.setConfPath("D:/code/webdevelop/conf/Spider-Rule.xml");
//		IChapterSpider chapterSpider = new BxwxChapterSpider();
//		List<Chapter> getsChapter = chapterSpider.getsChapter("https://www.bxwx9.org/b/211/211767/index.html");
//		for (Chapter chapter : getsChapter) {
//			System.out.println(chapter);
//		}
//	}

}
