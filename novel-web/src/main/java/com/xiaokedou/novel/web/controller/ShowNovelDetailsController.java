package com.xiaokedou.novel.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


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

@Controller
@RequestMapping("/novel/")
public class ShowNovelDetailsController {
	
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
//	@ResponseBody
//	@RequestMapping(value="chapterList.action",method=RequestMethod.POST)
////	展示某本小说的章节列表
//	public List<EncryptedChapter> showNovelChapters(String encr, String keys){
//		ObjectMapper mapper = NovelUtils.mapper;
//		String url;
//		try {
//			Map<String,List<Integer>> keyMap = mapper.readValue(keys, Map.class);
//			String encrChapterUrl = EncryptUtils.decryptNovelUrl(encr,"chapterUrl",keyMap);
//			url = EncryptUtils.decrypt(encrChapterUrl);
//		} catch (IOException e) {
//			return new ArrayList<EncryptedChapter>();
//		}
//		IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
//		Elements elements = chapterSpider.getChapterElements(url);
//		int taskSize=4;
//		ThreadPoolTaskExecutor service = taskExecutor;
//		List<Future<List<Chapter>>> futures0 = new ArrayList<>(taskSize);
//		for(int i=0;i<taskSize;i++){
//			if(i!=taskSize-1){
//				ChapterCallable callable0 = new ChapterCallable(elements, chapterSpider, (elements.size()/taskSize)*i, elements.size()/taskSize);
//				System.out.println( ((elements.size()/taskSize)*i)+":"+((elements.size()/taskSize)*(i+1)));
//				Future<List<Chapter>> t1 = service.submit(callable0);
//				futures0.add(t1);
//			}else{
//				ChapterCallable callable0 = new ChapterCallable(elements, chapterSpider, (elements.size()/taskSize)*i, elements.size()-(elements.size()/taskSize)*i);
//				System.out.println( ((elements.size()/taskSize)*i)+":"+(elements.size()-(elements.size()/taskSize)*i));
//				Future<List<Chapter>> t1 = service.submit(callable0);
//				futures0.add(t1);
//			}
//				
//		}
//		List<Chapter> chapters = new ArrayList<Chapter>();
//		for (Future<List<Chapter>> future : futures0) {
//			
//			List<Chapter> list=null;
//			try {
//				list = future.get();
//				chapters.addAll(list);
//			} catch (InterruptedException | ExecutionException e) {
//				e.printStackTrace();
//			}
//		}
//		Collections.sort(chapters, new Comparator<Chapter>() {
//			@Override
//			public int compare(Chapter o1, Chapter o2) {
//				String o1Url = o1.getUrl();
//				String o2Url = o2.getUrl();
//				String o1Index = o1Url.substring(o1Url.lastIndexOf('/') + 1, o1Url.lastIndexOf('.'));
//				String o2Index = o2Url.substring(o2Url.lastIndexOf('/') + 1, o2Url.lastIndexOf('.'));
//				return o1Index.compareTo(o2Index);
//			}
//		});
//		return EncryptUtils.encryptChapters(chapters);
//	}
	
	
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
