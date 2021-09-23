package com.common.skin.api;

import android.text.TextUtils;

import java.util.Arrays;

/**
 * View的皮肤
 *
 * @author LiuFeng
 * @data 2021/8/24 18:31
 */
public class ViewSkin {
    public String skin;

    public ViewSkin(String skin) {
        this.skin = skin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewSkin viewSkin = (ViewSkin) o;
        return TextUtils.equals(skin, viewSkin.skin);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(skin.getBytes());
    }
}
