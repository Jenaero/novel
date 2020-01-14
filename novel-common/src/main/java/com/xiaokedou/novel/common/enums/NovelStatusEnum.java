package com.xiaokedou.novel.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NovelStatusEnum
 *
 * @Author: renyajian
 * @Date: 2020/1/14
 */
@AllArgsConstructor
@Getter
public enum NovelStatusEnum {
    serialized(1, "连载"),
    complete(2, "全本");

    private int key;
    private String label;

    public static String getLabelByKey(int key) {
        for (NovelStatusEnum novelStatusEnum : values()) {
            if (key == novelStatusEnum.getKey()) {
                return novelStatusEnum.getLabel();
            }
        }
        return null;
    }
}
