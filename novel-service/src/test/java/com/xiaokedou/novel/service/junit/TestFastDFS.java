package com.xiaokedou.novel.service.junit;

import com.xiaokedou.novel.service.util.FastdfsClientUtil;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

/**
 * TestFastDFS
 *
 * @Author: renyajian
 * @Date: 2019/12/23
 */
public class TestFastDFS {
    @Resource
    private FastdfsClientUtil fastdfsClientUtil;


    @Test
    public void testUpload() {
        File file = new File("D:\\img\\176756s.jpg");

        try {
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpg", IOUtils.toByteArray(input));
            System.out.println(fastdfsClientUtil.upload(multipartFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
