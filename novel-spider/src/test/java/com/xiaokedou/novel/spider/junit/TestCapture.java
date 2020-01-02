package com.xiaokedou.novel.spider.junit;

import com.xiaokedou.novel.service.util.NovelSpiderHttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * TestCapture
 * <p>
 * 网络Capture图，缓存到另外服务器
 *
 * @Auther: renyajian
 * @Date: 2019/12/19
 */
public class TestCapture {

    //抓取并存储
    @Test
    public void Capture() {
        String url = "https://www.bxwx8.la/image/176/176787/176787s.jpg";
        String prefix = "https://";
        String rootPath = "/data/img/";
        String imgPath = url.replace(prefix, rootPath);
        File file = new File(imgPath);
        FileImageOutputStream outputStream = null;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse httpResponse = httpClient.execute(new NovelSpiderHttpGet(url))) {

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
            byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
            outputStream = new FileImageOutputStream(file);
            outputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void root() {
        //linux 一个盘，丢到根目录下
        String rootPath = "/data/img/";
        File file = new File(rootPath + "www.bxwx8.la/image/176/176787/");
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void dir(){
        String rootPath = "../Spider-Rule.xml";
        File file = new File(rootPath);
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void split() {
        String url = "http://www.bxwx8.la/image/176/176787/176787s.jpg";
        String prefix = "http://";
        String rootPath = "/data/img/";
        url = url.replace(prefix, rootPath);
        System.out.println(url);
    }
}
