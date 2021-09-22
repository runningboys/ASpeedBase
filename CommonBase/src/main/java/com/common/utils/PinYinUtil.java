package com.common.utils;

import android.text.TextUtils;
import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.pinyinhelper.PinyinMapDict;
import java.util.HashMap;
import java.util.Map;

/**
 * 拼音工具类
 *
 * @author LiuFeng
 * @data 2021/3/23 10:29
 */
public class PinYinUtil {

    /**
     * 汉字转换为拼音
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String getPinyin(String chinese) {
        return Pinyin.toPinyin(chinese, "");
    }


    /**
     * 字符转换为拼音
     *
     * @param c
     * @return
     */
    public static String getPinyin(char c) {
        return Pinyin.toPinyin(c);
    }

    /**
     * 判断字符是否是汉字
     *
     * @param c
     * @return true：是 false：不是
     */
    public static boolean isChinese(char c) {
        return Pinyin.isChinese(c);
    }

    /**
     * 人名转换为拼音
     * 备注：处理人名汉字的多意问题
     *
     * @param name
     * @return
     */
    public static String getPinyinForName(String name) {
        Pinyin.init(Pinyin.newConfig().with(new PinyinMapDict() {
            @Override
            public Map<String, String[]> mapping() {
                Map<String, String[]> map = new HashMap<>();
                map.put("重", new String[]{"CHONG"});
                map.put("区", new String[]{"OU"});
                map.put("仇", new String[]{"QIU"});
                map.put("秘", new String[]{"BI"});
                map.put("冼", new String[]{"XIAN"});
                map.put("折", new String[]{"SHE"});
                map.put("单", new String[]{"SHAN"});
                map.put("朴", new String[]{"PIAO"});
                map.put("翟", new String[]{"ZHAI"});
                map.put("查", new String[]{"ZHA"});
                map.put("解", new String[]{"XIE"});
                map.put("曾", new String[]{"ZENG"});
                map.put("隗", new String[]{"KUI"});
                map.put("乐", new String[]{"YUE"});
                return map;
            }
        }));
        return Pinyin.toPinyin(name, "&");
    }


    /**
     * 获取所有字转拼音后的第一个字符（经人名转拼音）
     * 举例：白居易  -> BJY
     *
     * @param text
     * @return
     */
    public static String getWordsFirstPinyin(String text) {
        // 边界值处理
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        String content = getPinyinForName(text);
        String[] contents = content.split("&");
        StringBuilder builder = new StringBuilder();
        if (contents != null) {
            for (String s : contents) {
                // 截取第一个字符，最后转换为小写
                if (s.length() > 0) {
                    String chat = s.substring(0, 1);
                    builder.append(chat);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 获取所有字转拼音后的第一个字符并用拼音隔开（经人名转拼音）
     * 举例：白居易  -> B J Y
     *
     * @param text
     * @return
     */
    public static String getWordsFirstPinyinWithSpace(String text) {
        // 边界值处理
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        // 遍历加空格
        String content = getWordsFirstPinyin(text);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            builder.append(content.charAt(i)).append(" ");
        }

        // 去掉尾部空格
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 获取所有字转拼音加空格
     * 举例：白居易  -> BAI JU YI
     *
     * @param text
     * @return
     */
    public static String getAllWordsWithSpace(String text) {
        // 边界值处理
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        String content = getPinyinForName(text);
        String[] contents = content.split("&");
        StringBuilder builder = new StringBuilder();
        if (contents != null) {
            // 遍历加空格
            for (String s : contents) {
                builder.append(s).append(" ");
            }

            // 去掉尾部空格
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
        }
        return builder.toString();
    }

    /**
     * 获取所有字加空格
     * 举例：白居易  -> 白 居 易
     *
     * @param text
     * @return
     */
    public static String getWordsWithSpace(String text) {
        // 边界值处理
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        // 遍历加空格
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            builder.append(text.charAt(i)).append(" ");
        }

        // 去掉尾部空格
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 获取简单中文分词
     *
     * @param content
     * @return
     */
    public static String getChineseSegment(String content) {
        StringBuilder builder = new StringBuilder();
        // 原内容
        builder.append(content).append(" ");

        // 所有字加空格
//        builder.append(getWordsWithSpace(content)).append(" ");

        // 拼音
        String pinyin = getPinyin(content);
        builder.append(pinyin.toLowerCase()).append(" ");
//        builder.append(pinyin.toUpperCase()).append(" ");

        // 拼音加空格
        String pinyinWithSpace = getAllWordsWithSpace(content);
        builder.append(pinyinWithSpace.toLowerCase()).append(" ");
//        builder.append(pinyinWithSpace.toUpperCase()).append(" ");

        // 每个字首字母拼音简写
        String firstPinyin = getWordsFirstPinyin(content);
        builder.append(firstPinyin.toLowerCase()).append(" ");
//        builder.append(firstPinyin.toUpperCase()).append(" ");

        // 每个字首字母拼音简写加空格
        String firstPinyinWithSpace = getWordsFirstPinyinWithSpace(content);
        builder.append(firstPinyinWithSpace.toLowerCase()).append(" ");
//        builder.append(firstPinyinWithSpace.toUpperCase());

        return builder.toString();
    }
}
