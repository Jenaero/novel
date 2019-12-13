package com.xiaokedou.novel.spider.storage.impl;

/**
 * TestNovelStorageImpl
 *
 * @Auther: renyajian
 * @Date: 2019/12/11
 */
public class TestNovelStorageImpl extends AbstractMapperNovelStorage {

    public TestNovelStorageImpl() {
        tasks.put("0", "https://www.bxwx8.org/binitial1/0/1.htm");
        for (int i = 0; i <= 25; i++) {
            String letter = (char) (i + 'A') + "";
            System.out.println("https://www.bxwx8.org/binitial" + letter + "/0/1.htm");
            tasks.put(letter, "https://www.bxwx8.org/binitial" + letter + "/0/1.htm");
        }
    }
}
