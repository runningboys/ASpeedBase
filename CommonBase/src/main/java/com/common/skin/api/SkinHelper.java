package com.common.skin.api;

/**
 * 皮肤操作辅助类
 *
 * @author LiuFeng
 * @data 2021/8/25 11:52
 */
public class SkinHelper {

    public static ResourceManager getResourceManager() {
        return SkinManager.getInstance().getResourceManager();
    }
}
