package com.xiaokedou.novel.dao;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Novel;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NovelDaoApplicationTests {

    @Resource
    private NovelMapper novelMapper;

    @Test
    public void contextLoads() {
        List <Novel> novels = novelMapper.selectList(new LambdaQueryWrapper <Novel>().eq(Novel::getId, 1L));
        System.out.println("mybatisplus查询："+novels);
        Novel novel = novelMapper.selectByPrimaryKey(2L);
        System.out.println("mybatis XML查询："+novel);
    }

    @Test
    public void insert(){
        String novelJson = "{\"author\":\"辰东\",\"chapterUrl\":\"https://www.bxwx8.org/b/70/70093/index.html\",\"collection\":3715,\"comment\":\"\",\"firstLetter\":\"W\",\"img\":\"https://www.bxwx8.org/image/70/70093/70093s.jpg\",\"introduction\":\"bxwx8.org 一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆。 群雄并起，万族林立，诸圣争霸，乱天动地。问苍茫大地，谁主沉浮？！ 一个少年从大荒中走出，一切从这里开始…… wWw.bxwx8.org\",\"lastUpdateChapter\":\"我的新书《圣墟》已上传\",\"lastUpdateChapterUrl\":\"https://www.bxwx8.org/b/70/70093/index.html\",\"lastUpdateTime\":1499356800000,\"length\":25299456,\"monthClick\":1622,\"monthRecommend\":10,\"name\":\"完美世界\",\"novelUrl\":\"https://www.bxwx8.org/binfo/70/70093.htm\",\"platformId\":1,\"status\":2,\"totalClick\":1663812,\"totalRecommend\":119870,\"type\":\"玄幻魔法\",\"weekClick\":1907,\"weekRecommend\":10}";
        Novel novel = JSONObject.parseObject(novelJson, Novel.class);
        novelMapper.batchInsert(Lists.newArrayList(novel));
    }

}
