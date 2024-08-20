package com.im.vt;

import android.text.TextUtils;


/**
 * 服务器环境配置枚举
 *
 * @author LiuFeng
 * @data 2022/5/12 19:34
 */
public enum ServerEnum {
    /**
     * 正式版
     */
    OFFICIAL(),

    /**
     * 预生产版
     */
    UAT(),

    /**
     * 测试版
     */
    BETA(),

    /**
     * 开发版
     */
    DEVELOP(),

    /**
     * 未知
     */
    UNKNOWN();


    public static ServerEnum parse(String value) {
        for (ServerEnum type : values()) {
            if (TextUtils.equals(type.name(), value)) {
                return type;
            }
        }

        return OFFICIAL;
    }
}
