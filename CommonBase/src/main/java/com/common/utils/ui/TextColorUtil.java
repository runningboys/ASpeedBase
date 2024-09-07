package com.common.utils.ui;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.common.base.BaseApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.ColorRes;

/**
 * 文本颜色工具类
 *
 * @author LiuFeng
 * @data 2021/2/1 11:55
 */
public class TextColorUtil {
    private static final Context context = BaseApp.getContext();

    /**
     * 获取带颜色文本的html
     *
     * @param colorId
     * @param text
     * @return
     */
    public static String getColorTextHtml(@ColorRes int colorId, String text) {
        return String.format("<font color = %s>%s</font>", context.getResources().getColor(colorId), text);
    }

    /**
     * 获取指定大小、颜色文本的html
     *
     * @param textSize
     * @param colorId
     * @param text
     * @return
     */
    public static String getColorTextHtml(int textSize, @ColorRes int colorId, String text) {
        return String.format("<font size=%s  color = %s>%s</font>", textSize, context.getResources().getColor(colorId), text);
    }

    /**
     * 获取指定大小、颜色文本的html
     *
     * @param textSize
     * @param color
     * @param text
     * @return
     */
    public static String getColorTextHtml(int textSize, String color, String text) {
        return String.format("<font size=%s  color = %s>%s</font>", textSize, color, text);
    }

    /**
     * 获取带颜色文本的html
     *
     * @param colorId
     * @param text
     * @param start
     * @param end
     * @return
     */
    public static String getColorTextHtml(@ColorRes int colorId, String text, int start, int end) {
        if (start < 0) {
            start = 0;
        }

        if (end > text.length()) {
            end = text.length();
        }

        String prevText = start > 0 ? text.substring(0, start) : "";
        String colorText = text.substring(start, end);
        String nextText = end < text.length() ? text.substring(end) : "";
        return String.format("%s<font color = %s>%s</font>%s", prevText, context.getResources().getColor(colorId), colorText, nextText);
    }

    /**
     * 获取带颜色文本的Spanned
     *
     * @param colorId
     * @param text
     * @return
     */
    public static Spanned getColorTextSpanned(@ColorRes int colorId, String text) {
        String coloredText = getColorTextHtml(colorId, text);
        coloredText = coloredText.replace("\n","<br>");
        return Html.fromHtml(coloredText);
    }

    /**
     * 获取带颜色文本的Spanned
     *
     * @param colorId
     * @param text
     * @param start
     * @param end
     * @return
     */
    public static Spanned getColorTextSpanned(@ColorRes int colorId, String text, int start, int end) {
        String coloredText = getColorTextHtml(colorId, text, start, end);
        coloredText = coloredText.replace("\n","<br>");
        return Html.fromHtml(coloredText);
    }

    /**
     * 关键字高亮变色
     *
     * @param colorId
     * @param text
     * @param keyword
     * @return
     */
    public static SpannableString getColorText(@ColorRes int colorId, String text, String keyword) {
        String string = text.toLowerCase();
        String key = keyword.toLowerCase();
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(string);
        SpannableString ss = new SpannableString(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorId)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }
}
