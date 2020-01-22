package com.xiaokedou.novel.web.controller;

import com.xiaokedou.novel.common.base.Pager;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.vo.EncryptedNovel;
import com.xiaokedou.novel.service.front.NovelService;
import com.xiaokedou.novel.service.util.EncryptUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class ShowNovelsNewController {
	@Resource
	private NovelService novelService;

	@RequestMapping(value = "/",method = RequestMethod.GET)
	public  String index(Model model){
		//top
		List<Novel> items=novelService.searchNovelByNameAuthor(null,new Pager(6));
		//玄幻、修真、都市、穿越、网游、科幻
		//其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
		//武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,言情小说
        List <String> types = Arrays.asList("武侠修真","游戏竞技","玄幻魔法","现代都市","科幻灵异","言情小说");
		List <Novel> hots = novelService.searchNovelByTypes(types, new Pager(13));
		Map <String, List <Novel>> hotsMap = hots.stream().collect(Collectors.groupingBy(Novel::getType));
		//最近更新小说 30
		//fixme sql 写的有问题，后续先排查sql
		List <Novel> lastUpdates = novelService.getLastUpdate(new Pager(30));
		model.addAttribute("items",items);
		model.addAttribute("hotsMap",hotsMap);
		return "index";
	}
	


	@ResponseBody
	@RequestMapping("getNovel.action")
	public  List<EncryptedNovel> getNovel(int index){
		List<EncryptedNovel> recommendNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(8);
			recommendNovels = novelService.getRecommendNovels(pager);
		}else{
			pager.setOffset(8+(index-1)*4);
			pager.setPageSize(4);
			recommendNovels=novelService.getRecommendNovels(pager);
		}
		return recommendNovels;
	}
	
//	首页推荐小说
	@ResponseBody
	@RequestMapping("getIndexRecommendNovels.action")
	public List<EncryptedNovel> getIndexRecommendNovels(int index){
		List<EncryptedNovel> recommendNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(8);
			recommendNovels = novelService.getRecommendNovels(pager);
		}else{
			pager.setOffset(8+(index-1)*4);
			pager.setPageSize(4);
			recommendNovels=novelService.getRecommendNovels(pager);
		}
		return recommendNovels;
	}
	
//	热门小说推荐
	@ResponseBody
	@RequestMapping("getHotNovels.action")
	public List<EncryptedNovel> getHotNovels(int index){
		List<EncryptedNovel> hotNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(5);
			hotNovels = novelService.getHotNovels(pager);
		}else{
			pager.setOffset(5+(index-1)*4);
			pager.setPageSize(4);
			hotNovels=novelService.getHotNovels(pager);
		}
		return hotNovels;
	}
	
//	粉丝推荐
	@ResponseBody
	@RequestMapping("getFanNovels.action")
	public List<EncryptedNovel> getFanNovels(int index){
		List<EncryptedNovel> FanNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(5);
			FanNovels = novelService.getFanNovels(pager);
		}else{
			pager.setOffset(5+(index-1)*4);
			pager.setPageSize(4);
			FanNovels=novelService.getFanNovels(pager);
		}
		return FanNovels;
	}
	
//	男生推荐
	@ResponseBody
	@RequestMapping("getBoyNovels.action")
	public List<EncryptedNovel> getBoyNovels(int index){
		List<EncryptedNovel> BoyNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(5);
			BoyNovels = novelService.getBoyNovels(pager);
		}else{
			pager.setOffset(5+(index-1)*4);
			pager.setPageSize(4);
			BoyNovels=novelService.getBoyNovels(pager);
		}
		return BoyNovels;
	}
//	女生推荐
	@ResponseBody
	@RequestMapping("getGirlNovels.action")
	public List<EncryptedNovel> getGirlNovels(int index){
		List<EncryptedNovel> FanNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(5);
			FanNovels = novelService.getGirlNovels(pager);
		}else{
			pager.setOffset(5+(index-1)*4);
			pager.setPageSize(4);
			FanNovels=novelService.getGirlNovels(pager);
		}
		return FanNovels;
	}
//	免费小说看不停
	@ResponseBody
	@RequestMapping("getOtherNovels.action")
	public List<EncryptedNovel> getOtherNovels(int index){
		List<EncryptedNovel> otherNovels =null;
		Pager pager = new Pager();
		if(index==0){
			pager.setOffset(index);
			pager.setPageSize(5);
			otherNovels = novelService.getOtherNovels(pager);
		}else{
			pager.setOffset(5+(index-1)*4);
			pager.setPageSize(4);
			otherNovels=novelService.getOtherNovels(pager);
		}
		return otherNovels;
	}
	
	

}
