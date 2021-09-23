package com.util.base.skin;

import android.os.Environment;

import com.common.CommonUtil;
import com.common.skin.api.SkinAutoChange;
import com.common.skin.api.SkinItem;
import com.common.skin.api.SkinManager;
import com.common.skin.api.SkinType;
import com.common.utils.AssetUtil;
import com.util.base.R;

import java.io.File;

/**
 * UI管理类
 *
 * @author LiuFeng
 * @data 2021/8/24 18:31
 */
public class UIManager {

    /**
     * 安装UI组件
     */
    public static void instanceUI() {
        SkinAutoChange.clear();

        addSkin(SkinType.SKIN_BLUE, R.style.app_skin_blue);
        addSkin(SkinType.SKIN_DARK, R.style.app_skin_dark);
        addSkin(SkinType.SKIN_WHITE, R.style.app_skin_white);

        addPluginSkin("purple");
        addPluginSkin("red");

        String skin = getCurrentSkin().getSkin();
        /*boolean isDarkMode = AppUtil.isDarkMode();
        if (isDarkMode && !TextUtils.equals(skin, SkinType.SKIN_DARK.name())) {
            skin = SkinType.SKIN_DARK.name();
        }
        else if (!isDarkMode && !TextUtils.equals(skin, SkinType.SKIN_DARK.name())) {
            skin = SkinType.SKIN_BLUE.name();
        }*/

        SkinAutoChange.changeSkin(skin);
    }

    /**
     * 添加主题皮肤
     *
     * @param skin
     * @param styleRes
     */
    public static void addSkin(SkinType skin, int styleRes) {
        SkinAutoChange.addSkin(skin.name());
        SkinManager.getInstance().addThemeSkin(skin.name(), styleRes);
    }

    /**
     * 添加插件皮肤
     *
     * @param skinName
     * @param pluginPath
     * @param pkgName
     */
    public static void addSkin(String skinName, String pluginPath, String pkgName) {
        SkinAutoChange.addSkin(skinName);
        SkinManager.getInstance().addPluginSkin(skinName, pluginPath, pkgName);
    }

    /**
     * 获取当前皮肤
     *
     * @return
     */
    public static SkinItem getCurrentSkin() {
        return SkinManager.getInstance().getCurrentSkin();
    }

    /**
     * 添加插件皮肤
     *
     * @param skinName
     */
    private static void addPluginSkin(String skinName) {
        String pkgName = "com.skin.skinlib";
        String pluginPath = getPluginPath(skinName);
        addSkin(skinName, pluginPath, pkgName);
    }

    /**
     * 获取插件路径
     *
     * @param name
     * @return
     */
    private static String getPluginPath(String name) {
        File root = CommonUtil.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File appDir = new File(root, "skin");
        String fileName = name + ".skin";
        File skinFile = new File(appDir, fileName);
        if (!skinFile.exists()) {
            AssetUtil.copyFile("skin/" + fileName, appDir);
        }
        return skinFile.getAbsolutePath();
    }
}
