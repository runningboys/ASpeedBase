package com.util.base.skin

import android.os.Environment
import android.text.TextUtils
import com.common.CommonUtil
import com.common.skin.api.SkinAutoChange
import com.common.skin.api.SkinItem
import com.common.skin.api.SkinManager
import com.common.skin.api.SkinType
import com.common.utils.AppUtil
import com.common.utils.AssetUtil
import com.util.base.R
import java.io.File

/**
 * UI管理类
 *
 * @author LiuFeng
 * @data 2021/8/24 18:31
 */
object UIManager {
    /**
     * 安装UI组件
     */
    fun instanceUI() {
        SkinAutoChange.clear()
        addSkin(SkinType.SKIN_BLUE, R.style.app_skin_blue)
        addSkin(SkinType.SKIN_DARK, R.style.app_skin_dark)
        addSkin(SkinType.SKIN_WHITE, R.style.app_skin_white)

        addPluginSkin("purple")
        addPluginSkin("red")

        var skin = currentSkin.skin
        val isDarkMode = AppUtil.isDarkMode()
        if (isDarkMode && !TextUtils.equals(skin, SkinType.SKIN_DARK.name)) {
            skin = SkinType.SKIN_DARK.name
        } else if (!isDarkMode && !TextUtils.equals(skin, SkinType.SKIN_DARK.name)) {
            skin = SkinType.SKIN_BLUE.name
        }
        SkinAutoChange.changeSkin(skin)
    }

    /**
     * 添加主题皮肤
     *
     * @param skin
     * @param styleRes
     */
    fun addSkin(skin: SkinType, styleRes: Int) {
        SkinAutoChange.addSkin(skin.name)
        SkinManager.getInstance().addThemeSkin(skin.name, styleRes)
    }

    /**
     * 添加插件皮肤
     *
     * @param skinName
     * @param pluginPath
     * @param pkgName
     */
    fun addSkin(skinName: String, pluginPath: String, pkgName: String) {
        SkinAutoChange.addSkin(skinName)
        SkinManager.getInstance().addPluginSkin(skinName, pluginPath, pkgName)
    }

    /**
     * 获取当前皮肤
     *
     * @return
     */
    val currentSkin: SkinItem
        get() = SkinManager.getInstance().currentSkin

    /**
     * 添加插件皮肤
     *
     * @param skinName
     */
    private fun addPluginSkin(skinName: String) {
        val pkgName = "com.skin.skinlib"
        val pluginPath = getPluginPath(skinName)
        addSkin(skinName, pluginPath, pkgName)
    }

    /**
     * 获取插件路径
     *
     * @param name
     * @return
     */
    private fun getPluginPath(name: String): String {
        val root = CommonUtil.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val appDir = File(root, "skin")
        val fileName = "$name.skin"
        val skinFile = File(appDir, fileName)
        if (!skinFile.exists()) {
            AssetUtil.copyFile("skin/$fileName", appDir)
        }
        return skinFile.absolutePath
    }
}