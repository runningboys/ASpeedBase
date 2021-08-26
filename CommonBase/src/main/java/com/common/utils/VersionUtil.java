package com.common.utils;

import android.text.TextUtils;

/**
 * 版本比较工具类
 *
 * @author LiuFeng
 * @data 2021/8/26 10:57
 */
public class VersionUtil {

    /**
     * 判断是否需要更新版本
     *
     * @param newVersion
     * @param oldVersion
     * @return
     */
    public static boolean isUpdateVersion(String newVersion, String oldVersion) {
        return compareVersion(newVersion, oldVersion) > 0;
    }

    /**
     * 比较版本号的大小：1.新版本大则返回一个正数；2. 旧版本则大返回一个负数；3. 版本相等则返回0；
     *
     * @param newVersion
     * @param oldVersion
     */
    public static int compareVersion(String newVersion, String oldVersion) {
        // 版本都为空，则相等
        if (TextUtils.isEmpty(newVersion) && TextUtils.isEmpty(oldVersion)) {
            return 0;
        }

        // 版本都不为空
        if (!TextUtils.isEmpty(newVersion) && !TextUtils.isEmpty(oldVersion)) {
            String[] newArr = newVersion.split("\\.");
            String[] oldArr = oldVersion.split("\\.");

            // 取最小位长度
            int minLength = Math.min(newArr.length, oldArr.length);
            for (int i = 0; i < minLength; i++) {
                String newStr = removeNonNumber(newArr[i]);
                String oldStr = removeNonNumber(oldArr[i]);

                // 空位跳过
                if (TextUtils.isEmpty(newStr) || TextUtils.isEmpty(oldStr)) {
                    continue;
                }

                int newNum = Integer.parseInt(newStr);
                int oldNum = Integer.parseInt(oldStr);
                if (newNum > oldNum) {
                    return 1;
                }

                if (newNum < oldNum) {
                    return -1;
                }
            }

            // 比较完后，位长相同为0；新版位长大为1；旧版位长大为-1；
            return Integer.compare(newArr.length, oldArr.length);
        }

        // 旧版本为空且新版本不为空，则为1
        if (!TextUtils.isEmpty(newVersion)) {
            return 1;
        }

        return -1;
    }

    /**
     * 移除字符串中的非数字
     *
     * @param content
     * @return
     */
    private static String removeNonNumber(String content) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                builder.append(c);
            }
        }

        return builder.toString();
    }
}
