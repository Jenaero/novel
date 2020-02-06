package com.xiaokedou.novel.web.controller;

import com.xiaokedou.novel.common.base.Pager;
import com.xiaokedou.novel.common.enums.NovelStatusEnum;
import com.xiaokedou.novel.common.enums.NovelTypeEnum;
import com.xiaokedou.novel.dao.mapper.ChapterDetailMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.vo.EncryptedNovel;
import com.xiaokedou.novel.service.front.ChapterDetailService;
import com.xiaokedou.novel.service.front.ChapterService;
import com.xiaokedou.novel.service.front.NovelService;
import com.xiaokedou.novel.service.util.EncryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class ShowNovelsController {
	@Resource
	private NovelService novelService;

	@Resource
	private ChapterService chapterService;

	@Resource
	private ChapterDetailService chapterDetailService;

	@Value("${novel.defaultNovelId}")
	private Long defaultNovelId;

	@Value("${novel.defaultChapterId}")
	private Long defaultChapterId;

	@RequestMapping(value = {"/","/index"},method = RequestMethod.GET)
	public String index(Model model){
		//top
		List<Novel> items=novelService.getPageByTypeOrderByTotalClick(new Pager(6),null);
		//玄幻、修真、都市、穿越、网游、科幻
		//其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
		//武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,言情小说
        List <String> types = Arrays.asList("武侠修真","游戏竞技","玄幻魔法","现代都市","科幻灵异","言情小说");
		List <Novel> hots = novelService.searchNovelByTypes(types, new Pager(13));
		Map <String, List <Novel>> hotsMap = hots.stream().collect(Collectors.groupingBy(Novel::getType));
		//最近更新小说 30
		List <Novel> lastUpdates = novelService.getPageByTypeOrderByLastUpdateTime(new Pager(30),null);
		List <Novel> lastAdds = novelService.getPageByTypeOrderByAddTime(new Pager(30),null);
		//最新入库小说 30
		model.addAttribute("items",items);
		model.addAttribute("hotsMap",hotsMap);
		model.addAttribute("lastUpdates",lastUpdates);
		model.addAttribute("lastAdds",lastAdds);

		return "index";
	}

	@RequestMapping(value = "/channel",method = RequestMethod.GET)
	public String channel(Model model){
		//top
		List<Novel> items=novelService.getPageByTypeOrderByTotalClick(new Pager(6),null);
		//玄幻、修真、都市、穿越、网游、科幻
		//其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
		//武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,言情小说
		List <String> types = Arrays.asList("武侠修真","游戏竞技","玄幻魔法","现代都市","科幻灵异","言情小说");
		//玄幻最近更新小说 30
		List <Novel> lastUpdates = novelService.getPageByTypeOrderByLastUpdateTime(new Pager(50), NovelTypeEnum.F);
		List <Novel> hots = novelService.getPageByTypeOrderByTotalClick(new Pager(50),NovelTypeEnum.F);
		//玄幻最新入库小说 30
		model.addAttribute("items",items);
		model.addAttribute("lastUpdates",lastUpdates);
		model.addAttribute("hots",hots);

		return "channel";
	}

	@RequestMapping(value = "/chapter",method = RequestMethod.GET)
	public String chapter(Model model,Long novelId){
		//默认元尊
		novelId = novelId == null ? defaultNovelId : novelId;
		Novel novel = novelService.getOneNovel(novelId);
		List <Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
		//热门推荐
		List <Novel> hots = novelService.getPageByTypeOrderByTotalClick(new Pager(10),NovelTypeEnum.F);
		//新书推荐
		List <Novel> news = novelService.getPageByTypeOrderByAddTime(new Pager(10),NovelTypeEnum.F);
		model.addAttribute("novel",novel);
		model.addAttribute("chapters",chapters);
		model.addAttribute("hots",hots);
		model.addAttribute("news",news);
		return "chapter";
	}

	@RequestMapping(value = "/detail",method = RequestMethod.GET)
	public String detail(Model model,Long chapterId,String chapterName){
		if (null == chapterId){
			if (StringUtils.isEmpty(chapterName)){
				chapterId = defaultChapterId;
			}else {
				Chapter chapter = chapterService.getChapterByName(chapterName);
				chapterId = chapter.getId();
			}
		}
		ChapterDetail detail = chapterDetailService.getOneDetailById(chapterId);
		Novel novel = novelService.getOneNovel(detail.getNovelId());
		//热门推荐
		List <Novel> hots = novelService.getPageByTypeOrderByTotalClick(new Pager(10),NovelTypeEnum.F);
		//新书推荐
		List <Novel> news = novelService.getPageByTypeOrderByAddTime(new Pager(10),NovelTypeEnum.F);
		model.addAttribute("detail",detail);
		model.addAttribute("novel",novel);
		model.addAttribute("hots",hots);
		model.addAttribute("news",news);
		return "detail";
	}

	@RequestMapping(value = "/search",method = RequestMethod.GET)
	public String search(RedirectAttributes model, String keyWord){
		List <Novel> novels = novelService.searchNovelByNameAuthor(keyWord, new Pager(1));
		model.addAttribute("novelId", CollectionUtils.isEmpty(novels)? null : novels.get(0).getId());
		return "redirect:/chapter";
	}

	@RequestMapping(value = "/all",method = RequestMethod.GET)
	public String all(Model model){
		//其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
		//奇幻、玄幻小说 500
		List <String> typeA = Arrays.asList("玄幻魔法");
		List <Novel> typeANovels = novelService.searchNovelByTypes(typeA, new Pager(500));
		//武侠、仙侠、修真小说
		List <String> typeB = Arrays.asList("武侠修真");
		List <Novel> typeBNovels = novelService.searchNovelByTypes(typeB, new Pager(500));
		//言情、都市小说
		List <String> typeC = Arrays.asList("耽美小说","言情小说");
		List <Novel> typeCNovels = novelService.searchNovelByTypes(typeC, new Pager(500));
		//历史、军事、穿越小说
		List <String> typeD = Arrays.asList("历史军事","同人小说");
		List <Novel> typeDNovels = novelService.searchNovelByTypes(typeD, new Pager(500));
		//游戏、竞技、网游小说
		List <String> typeE = Arrays.asList("游戏竞技");
		List <Novel> typeENovels = novelService.searchNovelByTypes(typeE, new Pager(500));
		//异灵、科幻小说
		List <String> typeF = Arrays.asList("科幻灵异");
		List <Novel> typeFNovels = novelService.searchNovelByTypes(typeF, new Pager(500));

		model.addAttribute("typeANovels",typeANovels);
		model.addAttribute("typeBNovels",typeBNovels);
		model.addAttribute("typeCNovels",typeCNovels);
		model.addAttribute("typeDNovels",typeDNovels);
		model.addAttribute("typeENovels",typeENovels);
		model.addAttribute("typeFNovels",typeFNovels);
		return "all";
	}

	@RequestMapping(value = "/top",method = RequestMethod.GET)
	public String top(Model model){
		//其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
		//玄幻、奇幻小说推荐排行榜 20
		List <String> typeA = Arrays.asList("玄幻魔法");
		List <Novel> typeATotalTops = novelService.getPageByTypesOrderByTotalClick(typeA,new Pager(20));
		List <Novel> typeAMonthTops = novelService.getPageByTypesOrderByMonthClick(typeA,new Pager(20));
		List <Novel> typeAWeekTops = novelService.getPageByTypesOrderByWeekClick(typeA,new Pager(20));

		//修真、仙侠小说推荐排行榜 20
		List <String> typeB = Arrays.asList("武侠修真");
		List <Novel> typeBTotalTops = novelService.getPageByTypesOrderByTotalClick(typeB,new Pager(20));
		List <Novel> typeBMonthTops = novelService.getPageByTypesOrderByMonthClick(typeB,new Pager(20));
		List <Novel> typeBWeekTops = novelService.getPageByTypesOrderByWeekClick(typeB,new Pager(20));

		//都市、青春小说推荐排行榜 20
		List <String> typeC = Arrays.asList("耽美小说","言情小说");
		List <Novel> typeCTotalTops = novelService.getPageByTypesOrderByTotalClick(typeC,new Pager(20));
		List <Novel> typeCMonthTops = novelService.getPageByTypesOrderByMonthClick(typeC,new Pager(20));
		List <Novel> typeCWeekTops = novelService.getPageByTypesOrderByWeekClick(typeC,new Pager(20));

		//历史、穿越小说推荐排行榜 20
		List <String> typeD = Arrays.asList("历史军事","同人小说");
		List <Novel> typeDTotalTops = novelService.getPageByTypesOrderByTotalClick(typeD,new Pager(20));
		List <Novel> typeDMonthTops = novelService.getPageByTypesOrderByMonthClick(typeD,new Pager(20));
		List <Novel> typeDWeekTops = novelService.getPageByTypesOrderByWeekClick(typeD,new Pager(20));

		//网游、竞技小说推荐排行榜 20
		List <String> typeE = Arrays.asList("游戏竞技");
		List <Novel> typeETotalTops = novelService.getPageByTypesOrderByTotalClick(typeE,new Pager(20));
		List <Novel> typeEMonthTops = novelService.getPageByTypesOrderByMonthClick(typeE,new Pager(20));
		List <Novel> typeEWeekTops = novelService.getPageByTypesOrderByWeekClick(typeE,new Pager(20));

		//科幻、灵异小说推荐排行榜 20
		List <String> typeF = Arrays.asList("科幻灵异");
		List <Novel> typeFTotalTops = novelService.getPageByTypesOrderByTotalClick(typeF,new Pager(20));
		List <Novel> typeFMonthTops = novelService.getPageByTypesOrderByMonthClick(typeF,new Pager(20));
		List <Novel> typeFWeekTops = novelService.getPageByTypesOrderByWeekClick(typeF,new Pager(20));

		//全本小说推荐排行榜 20
		List <Novel> completeTotalTops = novelService.getPageByStatusOrderByTotalClick(NovelStatusEnum.complete.getKey(),new Pager(20));
		List <Novel> completeMonthTops = novelService.getPageByStatusOrderByMonthClick(NovelStatusEnum.complete.getKey(),new Pager(20));
		List <Novel> completeWeekTops = novelService.getPageByStatusOrderByWeekClick(NovelStatusEnum.complete.getKey(),new Pager(20));

		//全部小说推荐排行榜 20
		List <Novel> allTotalTops = novelService.getPageByStatusOrderByTotalClick(null,new Pager(20));
		List <Novel> allMonthTops = novelService.getPageByStatusOrderByMonthClick(null,new Pager(20));
		List <Novel> allWeekTops = novelService.getPageByStatusOrderByWeekClick(null,new Pager(20));

		model.addAttribute("typeATotalTops",typeATotalTops);
		model.addAttribute("typeAMonthTops",typeAMonthTops);
		model.addAttribute("typeAWeekTops",typeAWeekTops);

		model.addAttribute("typeBTotalTops",typeBTotalTops);
		model.addAttribute("typeBMonthTops",typeBMonthTops);
		model.addAttribute("typeBWeekTops",typeBWeekTops);

		model.addAttribute("typeCTotalTops",typeCTotalTops);
		model.addAttribute("typeCMonthTops",typeCMonthTops);
		model.addAttribute("typeCWeekTops",typeCWeekTops);

		model.addAttribute("typeDTotalTops",typeDTotalTops);
		model.addAttribute("typeDMonthTops",typeDMonthTops);
		model.addAttribute("typeDWeekTops",typeDWeekTops);

		model.addAttribute("typeETotalTops",typeETotalTops);
		model.addAttribute("typeEMonthTops",typeEMonthTops);
		model.addAttribute("typeEWeekTops",typeEWeekTops);

		model.addAttribute("typeFTotalTops",typeFTotalTops);
		model.addAttribute("typeFMonthTops",typeFMonthTops);
		model.addAttribute("typeFWeekTops",typeFWeekTops);

		model.addAttribute("completeTotalTops",completeTotalTops);
		model.addAttribute("completeMonthTops",completeMonthTops);
		model.addAttribute("completeWeekTops",completeWeekTops);

		model.addAttribute("allTotalTops",allTotalTops);
		model.addAttribute("allMonthTops",allMonthTops);
		model.addAttribute("allWeekTops",allWeekTops);

		return "top";
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
