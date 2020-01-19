package com.xiaokedou.novel.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.vo.ChapterList;
import com.xiaokedou.novel.domain.vo.EncryptedChapter;
import com.xiaokedou.novel.domain.vo.EncryptedChapterDetail;
import com.xiaokedou.novel.domain.vo.EncryptedNovel;
import com.xiaokedou.novel.service.front.ChapterService;
import com.xiaokedou.novel.service.front.NovelService;
import com.xiaokedou.novel.service.util.EncryptUtils;
import com.xiaokedou.novel.service.util.NovelUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Controller
@RequestMapping("/novel/")
public class ShowNovelDetailsNewController {
	
	@Resource
	private NovelService novelService;
	@Resource 
	private ChapterService chapterService;
	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;

	@ResponseBody
	@RequestMapping("binfo.action")
//	展示某本小说的信息
	public EncryptedNovel showNovel(long key){
		EncryptedNovel novel = novelService.getNovelById(key);
		return novel;
	}

	@ResponseBody
	@RequestMapping(value="chapterList.action",method=RequestMethod.POST)
//	展示某本小说的章节列表
	public List<EncryptedChapter> showNovelChapters(String encr, String keys){
		ObjectMapper mapper = NovelUtils.mapper;
		String url;
		try {
			Map<String,List<Integer>> keyMap = mapper.readValue(keys, Map.class);
			String encrChapterUrl = EncryptUtils.decryptNovelUrl(encr,"chapterUrl",keyMap);
			url = EncryptUtils.decrypt(encrChapterUrl);
		} catch (IOException e) {
			return new ArrayList<EncryptedChapter>();
		}
		 long time = System.currentTimeMillis();
		List<EncryptedChapter> chapters = chapterService.getChapters(url);
		return chapters;
	}
	
	@ResponseBody
	@RequestMapping(value="chapterIndexList.action",method=RequestMethod.POST)
//	展示某本小说的章节列表
	public List<EncryptedChapter> showNovelNChapters(String encr, String keys,int offset,int length){
		ObjectMapper mapper = NovelUtils.mapper;
		String url;
		try {
			Map<String,List<Integer>> keyMap = mapper.readValue(keys, Map.class);
			String encrChapterUrl = EncryptUtils.decryptNovelUrl(encr,"chapterUrl",keyMap);
			url = EncryptUtils.decrypt(encrChapterUrl);
		} catch (IOException e) {
			return new ArrayList<EncryptedChapter>();
		}
		List<Chapter> chapters = chapterService.getChaptersByOffset(url,offset,length);
		return EncryptUtils.encryptChapters(chapters);
	}
	
	@ResponseBody
	@RequestMapping(value="chapterNList.action",method=RequestMethod.GET)
//	展示某本小说的章节列表
	public ChapterList showNovelChapters(String encr, String keys, int offset, int length){
		ObjectMapper mapper = NovelUtils.mapper;
		String url;
		try {
			//1、获取键值对
			Map<String,List<Integer>> keyMap = mapper.readValue(keys, Map.class);
			String encrChapterUrl = EncryptUtils.decryptNovelUrl(encr,"chapterUrl",keyMap);
			url = EncryptUtils.decrypt(encrChapterUrl);
		} catch (IOException e) {
			return new ChapterList();
		}
		return chapterService.getChapters(url, offset, length);
	}
	
//	ChapterList
	@ResponseBody
	@RequestMapping("chapterContent.action")
//	展示某本小说某个章节的具体内容
	public EncryptedChapterDetail showChapterContent(String encr, String keys){
		ObjectMapper mapper = NovelUtils.mapper;
		String url;
		try {
			Map<String,List<Integer>> keyMap = mapper.readValue(keys, Map.class);
			String encrChapterUrl = EncryptUtils.decryptNovelUrl(encr,"chapterUrl",keyMap);
			url = EncryptUtils.decrypt(encrChapterUrl);
		} catch (IOException e) {
			return new EncryptedChapterDetail();
		}
		
		EncryptedChapterDetail chapterDetail = chapterService.getChapterDetail(url);
		return chapterDetail;
	}
	
}
