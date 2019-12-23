package com.xiaokedou.novel.common.util;

import java.util.UUID;

/**
 * KeyUtil
 *
 * @Auther: renyajian
 * @Date: 2019/12/23
 */
public class KeyUtil {

    public static String getRandomKey(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }
}
