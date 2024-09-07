package com.common.skin.api;

import android.content.res.Resources;
import android.text.TextUtils;

import com.common.utils.resource.AppUtil;

import java.util.Arrays;

/**
 * 皮肤项
 *
 * @author LiuFeng
 * @data 2021/8/24 18:29
 */
public class SkinItem {
    private final String skin;
    private int styleRes;
    private Resources.Theme theme;
    private String pluginPath;
    private String pkgName;
    private final boolean isPlugin;

    public SkinItem(String skin, int styleRes) {
        this.skin = skin;
        this.styleRes = styleRes;
        this.isPlugin = false;
        this.theme = AppUtil.createTheme(styleRes);
    }

    public SkinItem(String skin, String pluginPath, String pkgName) {
        this.skin = skin;
        this.pluginPath = pluginPath;
        this.pkgName = pkgName;
        this.isPlugin = true;
    }

    public String getSkin() {
        return skin;
    }

    public int getStyleRes() {
        return styleRes;
    }

    public Resources.Theme getTheme() {
        return theme;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public String getPkgName() {
        return pkgName;
    }

    public boolean isPlugin() {
        return isPlugin;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof SkinItem) {
            SkinItem item = (SkinItem) o;
            return TextUtils.equals(skin, item.skin);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(skin.getBytes());
    }
}
