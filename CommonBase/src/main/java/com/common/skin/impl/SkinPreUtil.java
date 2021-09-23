package com.common.skin.impl;


import com.common.CommonUtil;
import com.common.skin.api.SkinItem;
import com.common.utils.GsonUtil;
import com.common.utils.SpManager;

/**
 * 皮肤偏好设置
 *
 * @author LiuFeng
 * @data 2021/9/16 14:14
 */
public class SkinPreUtil {
    private static final String SKIN_PRE_NAME = "skin_plugin";
    private static final String KEY_SKIN_NAME = "key_skin_name";
    private static final SpManager spManager = SpManager.of(CommonUtil.getContext(), SKIN_PRE_NAME);


    /**
     * 设置皮肤
     *
     * @param skinItem
     */
    public static void setSkin(SkinItem skinItem) {
        spManager.setString(KEY_SKIN_NAME, GsonUtil.toJson(skinItem));
    }

    /**
     * 获取皮肤名称
     *
     * @return
     */
    public static SkinItem getSkin() {
        String skinJson = spManager.getString(KEY_SKIN_NAME, null);
        if (skinJson != null) {
            SkinItem item = GsonUtil.toBean(skinJson, SkinItem.class);
            if (!item.isPlugin()) {
                // Gson反序列化不走构造方法，所以them是无效的，重新创建下
                return new SkinItem(item.getSkin(), item.getStyleRes());
            }

            return item;
        }

        return null;
    }

    /**
     * 情况偏好设置
     */
    public static void clear() {
        spManager.clearAll();
    }
}
