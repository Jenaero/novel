package com.xiaokedou.novel.service.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * NovelCaptureFileUtil
 * 文件抓取并存储
 *
 * @Auther: renyajian
 * @Date: 2019/12/19
 */
@Deprecated
public class NovelCaptureFileUtil {

    @Value("${capture.prefix}")
    private static String prefix;
    @Value("${capture.rootpath}")
    private static String rootPath;


    public static void capture(String url) {

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
}
