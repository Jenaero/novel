package com.xiaokedou.novel.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NovelTypeEnum
 *
 * 小说类型
 *
 * @Author: renyajian
 * @Date: 2020/2/02
 */
@AllArgsConstructor
@Getter
public enum NovelTypeEnum {
    //其他类型,历史军事,同人小说,武侠修真,游戏竞技,玄幻魔法,现代都市,科幻灵异,耽美小说,言情小说
    A(1, "其他类型"),
    B(2, "历史军事"),
    C(3, "同人小说"),
    D(4, "武侠修真"),
    E(5, "游戏竞技"),
    F(6, "玄幻魔法"),
    G(7, "现代都市"),
    H(8, "科幻灵异"),
    I(9, "耽美小说"),
    J(10, "言情小说");

    private int key;
    private String label;

    public static String getLabelByKey(int key) {
        for (NovelTypeEnum novelStatusEnum : values()) {
            if (key == novelStatusEnum.getKey()) {
                return novelStatusEnum.getLabel();
            }
        }
        return null;
    }
}
