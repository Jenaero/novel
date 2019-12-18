package com.xiaokedou.novel.spider.junit;

import com.xiaokedou.novel.common.enums.NovelSiteEnum;
import com.xiaokedou.novel.dao.mapper.NovelMapper;
import com.xiaokedou.novel.domain.po.Chapter;
import com.xiaokedou.novel.domain.po.ChapterDetail;
import com.xiaokedou.novel.domain.po.Novel;
import com.xiaokedou.novel.domain.po.NovelInfo;
import com.xiaokedou.novel.service.spider.IChapterSpider;
import com.xiaokedou.novel.service.spider.INovelInfoSpider;
import com.xiaokedou.novel.service.spider.INovelSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.AbstractChapterDetailSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.AbstractChapterSpider;
import com.xiaokedou.novel.service.spider.impl.chapter.BxwxChapterSpider;
import com.xiaokedou.novel.service.util.NovelSpiderFactory;
import com.xiaokedou.novel.service.util.NovelSpiderHttpGet;
import com.xiaokedou.novel.service.util.NovelSpiderUtil;
import com.xiaokedou.novel.spider.storage.Processor;
import com.xiaokedou.novel.spider.storage.impl.BxwxNovelStorageImpl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TestBXWXcase {

    @Test
    public void testGetSite() {
        Map <String, String> contextMap = NovelSpiderUtil.getContext(NovelSiteEnum.getEnumByUrl("https://www.bxwxorg.com/"));
        for (String k : contextMap.keySet()) {
            System.out.println(k + ":" + contextMap.get(k));
        }
    }

    @Test
    public void testHttpGet() {
        NovelSpiderHttpGet novelSpiderHttpGet = new NovelSpiderHttpGet();
        String url = "https://www.bxwxorg.com/";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse httpResponse = httpClient.execute(new NovelSpiderHttpGet(url))) {
            String result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetChaptersThread() throws InterruptedException, ExecutionException {
        AbstractChapterSpider chapterSpider = new AbstractChapterSpider() {
        };
        String url = "https://www.bxwxorg.com/read/40964/";
        Elements elements = chapterSpider.getChapterElements(url);
        elements.size();
        int taskSize = 3;
        ExecutorService service = Executors.newFixedThreadPool(taskSize);
        List <Future <List <Chapter>>> futures0 = new ArrayList <>(taskSize);
        for (int i = 0; i < taskSize; i++) {
            if (i != taskSize - 1) {
                ChapterCallable callable0 = new ChapterCallable(elements, chapterSpider, (elements.size() / taskSize) * i, (elements.size() / taskSize));
                Future <List <Chapter>> t1 = service.submit(callable0);
                futures0.add(t1);
            } else {
                ChapterCallable callable0 = new ChapterCallable(elements, chapterSpider, (elements.size() / taskSize) * i, elements.size() - (elements.size() / taskSize) * i);
                Future <List <Chapter>> t1 = service.submit(callable0);
                futures0.add(t1);
            }

        }
        service.shutdown();
        List <Chapter> chapters = new ArrayList <Chapter>();
        for (Future <List <Chapter>> future : futures0) {

            List <Chapter> list = null;
            try {
                list = future.get();
                chapters.addAll(list);
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (Chapter chapter : list) {
                System.out.println(chapter);

            }
        }

    }

    @Test
    public void test2() {
        AbstractChapterSpider chapterSpider = new AbstractChapterSpider() {
        };
        String url = "https://www.bxwxorg.com/read/4211/";
        Elements elements = chapterSpider.getChapterElements(url);
        for (Element element : elements) {
            Chapter chapter = chapterSpider.getChapterFromElement(element);
            System.out.println(chapter);
        }
    }

    @Test
    public void testGetChapters() {
        AbstractChapterSpider chapterSpider = new AbstractChapterSpider() {
        };
        List <Chapter> getsChapter = chapterSpider.getChapters("https://www.bxwx9.org/b/33/33732/index.html");
        int cont = 1;
        for (Chapter chapter : getsChapter) {
            System.out.println(cont + "" + chapter);
            cont++;

        }
    }

    @Test
    public void testGetChapters2() {
        AbstractChapterSpider chapterSpider = new AbstractChapterSpider() {
        };
        List <Chapter> getsChapter = chapterSpider.getChapters("https://www.bxwx9.org/b/21/21385/index.html", 0, 20);
        int cont = 1;
        for (Chapter chapter : getsChapter) {
            System.out.println(cont + "" + chapter);
            cont++;

        }
    }

    @Test
    public void testGetChapters3() {
        AbstractChapterSpider chapterSpider = new BxwxChapterSpider() {
        };
        List <Chapter> getsChapter = chapterSpider.getChapters("https://www.bxwx9.org/b/21/21385/index.html", 0, 20);
        int cont = 1;
        for (Chapter chapter : getsChapter) {
            System.out.println(cont + "" + chapter);
            cont++;

        }
    }

    @Test
    public void testGetChapters4() {
        AbstractChapterSpider chapterSpider = new BxwxChapterSpider() {
        };
        List <Chapter> getsChapter = chapterSpider.getChapters("https://www.bxwx9.org/b/21/21385/index.html");
        int cont = 1;
        for (Chapter chapter : getsChapter) {
            System.out.println(cont + "" + chapter);
            cont++;

        }
    }

    @Test
    public void testGetChapterDetails() {
//		https://www.bxwx9.org/b/21/21385/3798993.html
        AbstractChapterDetailSpider abstractChapterDetailSpider = new AbstractChapterDetailSpider() {
        };
        ChapterDetail chapterDetail = abstractChapterDetailSpider.getChapterDetail("https://www.bxwx9.org/b/21/21385/3798993.html");
        System.out.println(chapterDetail.getContent());
    }

    @Test
    public void testBxwxGetsNovel() {
        INovelSpider spider = NovelSpiderFactory.getNovelSpider("https://www.bxwx9.org/binitial1/0/1.htm");
        List <Novel> novels = spider.getsNovel("https://www.bxwx9.org/bsort/0/4.htm", 10);
        for (Novel novel : novels) {
            System.out.println(novel);
        }
    }

    @Test
    public void testMyBaits() throws Exception {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(new FileInputStream("conf/SqlMapConfig.xml"));
        NovelMapper mapper = factory.openSession().getMapper(NovelMapper.class);
        Novel novel = mapper.selectByPrimaryKey(1l);
        System.out.println(mapper.selectByPrimaryKey(1l));
    }

    @Test
    public void test() {
//		https://www.bxwx9.org/binitialA/0/1.htm
        for (int i = 0; i <= 25; i++) {
            String letter = (char) (i + 'A') + "";

            System.out.println("https://www.bxwx9.org/binitial" + letter + "/0/1.htm");
        }

    }

    @Test
    public void testBxwxProcess() throws FileNotFoundException {
        Processor processor = new BxwxNovelStorageImpl();
        processor.process();
    }

    @Test
    public void testBxwxNovelInfoSpider() throws Exception {
        INovelInfoSpider infoSpider = NovelSpiderFactory.getNovelInfoSpider("https://www.bxwx9.org/binfo/21");
        NovelInfo novelInfo = infoSpider.getNovelInfo("https://www.bxwx9.org/binfo/212/212313.htm");
        System.out.println(novelInfo);
    }

    @Test
    public void testBatchInsert() throws FileNotFoundException {
//		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("D:/code/webdevelop/conf/SqlMapConfig.xml"));
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new FileInputStream("conf/SqlMapConfig.xml"));
        SqlSession session = sqlSessionFactory.openSession();
        INovelSpider spider = NovelSpiderFactory.getNovelSpider("https://www.bxwx9.org/binitial1/0/1.htm");
        List <Novel> novels = spider.getsNovel("https://www.bxwx9.org/bsort/0/4.htm", 10);
//        session.getMapper(NovelMapper.class).batchInsert(novels);
        session.commit();
        session.close();

    }

    @Test
    public void testChapter() {
        String url = "https://www.bxwx9.org/b/215/215253/index.html";
        int offset = 19;
        int length = 9999;
        IChapterSpider chapterSpider = NovelSpiderFactory.getChapterSpider(url);
        Elements elements = chapterSpider.getChapterElements(url);
        List <Chapter> chapters = chapterSpider.getChapterFromElements(elements, offset, length);
        int size = elements.size();
        chapters = chapterSpider.getChapterFromElements(elements, offset, length);
        System.out.println(chapters.size());
        for (Chapter chapter : chapters) {
            System.out.println(chapter);

        }
    }


}
