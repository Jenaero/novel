package com.xiaokedou.novel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaokedou.novel.domain.po.Chapter;

import java.util.List;

/**
 * ChapterMapper
 *
 * @Auther: renyajian
 * @Date: 2019/12/19
 */
public interface ChapterMapper extends BaseMapper <Chapter> {

    void batchInsert(List<Chapter> chapters);
}
