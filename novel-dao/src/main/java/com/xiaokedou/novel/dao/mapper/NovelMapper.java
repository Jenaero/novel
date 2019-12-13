package com.xiaokedou.novel.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaokedou.novel.common.base.Page;
import com.xiaokedou.novel.common.base.Pager;
import com.xiaokedou.novel.domain.po.Novel;

import java.util.List;

public interface NovelMapper extends BaseMapper <Novel> {

    void batchInsert(List <Novel> novels);

    int deleteByPrimaryKey(Long id);

    int insert(Novel record);

    int insertSelective(Novel record);

    Novel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Novel record);

    int updateByPrimaryKey(Novel record);

    List <Novel> selectPageNovelsByRecommend(Pager pager);

    List <Novel> selectPageNovelsByMonthRecommend(Pager pager);

    List <Novel> selectPageNovelsByWeekRecommend(Pager pager);

    List <Novel> selectPageNovelsByBoyRecommend(Pager pager);

    List <Novel> selectPageNovelsByGirlRecommend(Pager pager);

    List <Novel> selectPageNovelsByOther(Pager pager);

    Novel selectByNovelUrl(String url);

    List <Novel> selectPageNovelsByKeyWord(Page <String> page);
}