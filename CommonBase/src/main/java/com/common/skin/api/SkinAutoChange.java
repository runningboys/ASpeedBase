package com.common.skin.api;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 皮肤自动切换，简化切换操作
 *
 * @author LiuFeng
 * @data 2021/9/18 10:51
 */
public class SkinAutoChange {
    private static int skinIndex = -1;
    private static final List<String> mSkins = new ArrayList<>();

    /**
     * 添加皮肤，切换时按添加顺序切换
     *
     * @param skin
     */
    public static void addSkin(String skin) {
        mSkins.add(skin);
    }

    /**
     * 按添加顺序自动切换每个皮肤
     */
    public static void changeSkin() {
        if (mSkins.isEmpty()) {
            return;
        }

        skinIndex++;
        skinIndex %= mSkins.size();
        String skin = mSkins.get(skinIndex);
        SkinManager.getInstance().changeSkin(skin);
    }

    /**
     * 切换到指定皮肤
     *
     * @param skin
     */
    public static void changeSkin(String skin) {
        int index = mSkins.indexOf(skin);
        if (index >= 0) {
            skinIndex = index - 1;
            changeSkin();
        }
    }

    /**
     * 自动切换下一个皮肤，跳过切换失败的皮肤
     */
    public static void changeSkinSkipFail() {
        if (mSkins.isEmpty()) {
            return;
        }

        boolean isFirst = skinIndex < 0;
        int index = Math.max(skinIndex, 0);
        String skin = mSkins.get(index);

        // 执行切换
        changeSkin();

        while (true) {
            // 取切换后的皮肤
            SkinItem item = SkinManager.getInstance().getCurrentSkin();

            if (isFirst) {
                // 第一次切换，第一个皮肤等于切换后皮肤，则成功
                if (TextUtils.equals(skin, item.getSkin())) {
                    break;
                }
                isFirst = false;
            } else {
                // 非第一次切换，当前皮肤等于切换后皮肤，则失败
                // 判断下标避免循环重复切换
                if (TextUtils.equals(skin, item.getSkin()) && index != skinIndex) {
                    changeSkin();
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 获取期望的当前皮肤（可能这个皮肤切换失败了）
     *
     * @return
     */
    public static String getExpectCurSkin() {
        if (skinIndex >= 0 && skinIndex < mSkins.size()) {
            return mSkins.get(skinIndex);
        }

        return null;
    }

    /**
     * 清空自动切换的皮肤
     */
    public static void clear() {
        skinIndex = -1;
        mSkins.clear();
    }
}
