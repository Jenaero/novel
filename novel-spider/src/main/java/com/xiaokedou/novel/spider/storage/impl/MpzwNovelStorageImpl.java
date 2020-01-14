package com.xiaokedou.novel.spider.storage.impl;

/**
 * MpzwNovelStorageImpl
 *
 * http://www.maopuzww.com/
 * 猫扑中文
 *
 * @Author: renyajian
 * @Date: 2019/01/14
 */
public class MpzwNovelStorageImpl extends AbstractAllNovelStorage {

    public MpzwNovelStorageImpl() {
        tasks.put("0", "https://www.bxwx8.la/binitial1/0/1.htm");
        for (int i = 0; i <= 25; i++) {
            String letter = (char) (i + 'A') + "";
            System.out.println("https://www.bxwx8.la/binitial" + letter + "/0/1.htm");
            tasks.put(letter, "https://www.bxwx8.la/binitial" + letter + "/0/1.htm");
        }
//        for (int i = 1; i <= 102; i++) {
//            String letter = i+"";
//            tasks.put(letter,"https://www.bxwxorg.com/all/"+ i +".html");
//        }
    }
}
