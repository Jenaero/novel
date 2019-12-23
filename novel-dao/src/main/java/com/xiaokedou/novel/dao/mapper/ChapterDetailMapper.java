package com.xiaokedou.novel.dao.mapper;

import com.xiaokedou.novel.domain.po.ChapterDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ChapterDetailMapper
 * 小说文本信息丢到mongoDB
 *
 * @Auther: renyajian
 * @Date: 2019/12/19
 */
public interface ChapterDetailMapper extends MongoRepository<ChapterDetail,String> {
}
