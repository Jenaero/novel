package com.xiaokedou.novel.spider.storage.impl;

/**
 * MpzwAllNovelStorageImpl
 *
 * http://www.maopuzww.com/
 * 猫扑中文
 *
 * @Author: renyajian
 * @Date: 2019/01/14
 */
public class MpzwAllNovelStorageImpl extends AbstractAllNovelStorage {

    public MpzwAllNovelStorageImpl() {
        tasks.put("0", "http://www.maopuzww.com/cbook_all.html");
    }
}
