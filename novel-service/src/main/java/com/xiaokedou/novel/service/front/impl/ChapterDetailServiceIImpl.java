package com.xiaokedou.novel.service.front.impl;

import com.xiaokedou.novel.dao.mapper.ChapterDetailMapper;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.service.front.ChapterDetailService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class ChapterDetailServiceIImpl implements ChapterDetailService {

    @Resource
    private ChapterDetailMapper chapterDetailMapper;

    @Override
    public ChapterDetail getOneDetailById(Long chapterId) {
        ChapterDetail query = new ChapterDetail();
        query.setId(chapterId);
        Example <ChapterDetail> example = Example.of(query);
        Optional<ChapterDetail> result = chapterDetailMapper.findOne(example);
        return result.get();
    }
}
