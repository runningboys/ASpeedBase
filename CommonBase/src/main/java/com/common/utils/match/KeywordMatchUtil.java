package com.common.utils.match;

import android.text.TextUtils;
import android.util.Pair;

import com.common.utils.resource.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 关键字匹配工具
 *
 * @author LiuFeng
 * @data 2021/9/22 15:08
 */
public class KeywordMatchUtil {

    /**
     * 匹配文本
     *
     * @param keyword 关键字
     * @param text    原始文本
     * @return
     */
    public static LightText matchText(String keyword, String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        int index = StringUtil.indexOfIgnoreCase(text, keyword);
        if (index > -1) {
            return new LightText(text, index, index + keyword.length());
        } else {
            return null;
        }
    }

    /**
     * 匹配文本拼音
     *
     * @param keyword 关键字
     * @param text    原始文本
     * @return
     */
    public static LightText matchTextPy(String keyword, String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        Pair<Integer, Integer> range = matchPY(keyword, text);
        if (range == null) {
            return null;
        }
        // 闭合[first, end]区间数据
        return new LightText(text, range.first, range.second + 1);
    }

    /**
     * 匹配文本拼音
     *
     * @param keyword
     * @param rawText
     * @return
     */
    private static Pair<Integer, Integer> matchPY(String keyword, String rawText) {
        Pair<Integer, Integer> result = null;
        try {
            int textLength = rawText.length();
            StringBuilder allWordSB = new StringBuilder();
            StringBuilder headWordSB = new StringBuilder();

            // 辅助全拼判断，记录了一个字符转换成拼音后的区间
            List<Pair<Integer, Integer>> rangeIndex = new ArrayList<>();

            // 辅助映射：去空字符后的字符位置 ->之前未去空之前字符位置
            int j = 0;
            int[] rawCharMap = new int[textLength];

            for (int i = 0; i < textLength; i++) {
                String py = PinYinUtil.getPinyin(rawText.charAt(i));
                if (py.trim().length() > 0) {
                    rangeIndex.add(new Pair<>(allWordSB.length(), allWordSB.length() + py.length())); // 开闭区间
                    allWordSB.append(py);
                    headWordSB.append(py.charAt(0));

                    // 记录非空字位置j，对应原始字符的位置i
                    rawCharMap[j] = i;
                    ++j;
                }
            }

            // 首字母匹配算法
            String headWordNoBlankText = headWordSB.toString();
            final int headWordSearchStartIndex = StringUtil.indexOfIgnoreCase(headWordNoBlankText, keyword);
            if (headWordSearchStartIndex > -1) {
                final int headWordSearchEndIndex = headWordSearchStartIndex + keyword.length() - 1;
                result = new Pair<>(rawCharMap[headWordSearchStartIndex], rawCharMap[headWordSearchEndIndex]);
                return result;
            }

            // 全拼匹配算法
            String noBlankText = allWordSB.toString();
            final int searchStartIndex = StringUtil.indexOfIgnoreCase(noBlankText, keyword);
            if (searchStartIndex > -1) {
                final int searchEndIndex = searchStartIndex + keyword.length() - 1;
                int starChar = -1;
                int endChar = -1;

                // 查找开始点在哪个单词上面
                for (int i = 0; i < rangeIndex.size(); i++) {
                    Pair<Integer, Integer> oneCharRange = rangeIndex.get(i);
                    // 查找开头字符区间
                    if (searchStartIndex < oneCharRange.second && searchStartIndex >= oneCharRange.first) {
                        starChar = i;
                    }

                    // 查找结尾字符区间
                    if (searchEndIndex < oneCharRange.second && searchEndIndex >= oneCharRange.first) {
                        endChar = i;
                        break;
                    }
                }
                result = new Pair<>(rawCharMap[starChar], rawCharMap[endChar]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 高亮文本范围[start, end)，开闭区间
     */
    public static class LightText implements Serializable {
        private final String text;
        private final int start;
        private final int end;

        public LightText(String text, int start, int end) {
            this.text = text;
            this.start = start;
            this.end = end;
        }

        /**
         * 判断文本是否可以高亮
         *
         * @return
         */
        public boolean isLightWorkable() {
            if (text == null || text.isEmpty() || text.trim().isEmpty()) {
                return false;
            }
            if (start >= end) {
                return false;
            }
            if (start < 0 || start >= text.length()) {
                return false;
            }
            if (end < 1 || end > text.length()) {
                return false;
            }
            return true;
        }

        public String getText() {
            return text;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
