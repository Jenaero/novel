package com.xiaokedou.novel.spider.storage.impl;

/**
 * BxwxUpdateNovelStorageImpl
 *
 * 笔下文学最近更新
 *
 * @Author: renyajian
 * @Date: 2019/12/11
 */
public class BxwxUpdateNovelStorageImpl extends AbstractUpdateNovelStorage {

    public BxwxUpdateNovelStorageImpl() {
        tasks.put("0", "http://www.bxwx8.la/btoplastupdate/0/1.htm");
    }
}
