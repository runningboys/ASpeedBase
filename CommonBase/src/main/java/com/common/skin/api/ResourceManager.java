package com.common.skin.api;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import com.common.utils.AppUtil;


/**
 * 资源管理
 *
 * @author LiuFeng
 * @data 2021/9/16 18:34
 */
public class ResourceManager {
    private static final String TAG = "ResourceManager";
    private static final String ATTR = "attr";
    private static final String COLOR = "color";
    private static final String DRAWABLE = "drawable";
    private static final TypedValue typeValue = new TypedValue();

    private final Resources mResources;
    private final String mPackageName;
    private final boolean isPluginSkin;

    /**
     * 构造函数
     *
     * @param resources
     * @param packageName
     * @param isPlugin
     */
    public ResourceManager(Resources resources, String packageName, boolean isPlugin) {
        this.mResources = resources;
        this.isPluginSkin = isPlugin;
        this.mPackageName = packageName;
    }

    /**
     * 是否插件
     *
     * @return
     */
    public boolean isPluginSkin() {
        return isPluginSkin;
    }

    /**
     * 获取资源
     *
     * @return
     */
    public Resources getResources() {
        return mResources;
    }

    /**
     * 获取包名
     *
     * @return
     */
    public String getPackageName() {
        return mPackageName;
    }

    /**
     * 通过资源id获取颜色
     *
     * @param resId
     * @return
     */
    public int getColor(int resId) {
        try {
            return mResources.getColor(resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    /**
     * 通过资源id获取颜色集
     *
     * @param resId
     * @return
     */
    public ColorStateList getColorStateList(int resId) {
        try {
            return mResources.getColorStateList(resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 通过资源id获取图片
     *
     * @param resId
     * @return
     */
    public Drawable getDrawable(int resId) {
        try {
            return mResources.getDrawable(resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 获取属性名对应的值
     *
     * @param resName
     * @return
     */
    public float getFloatValue(String resName) {
        try {
            mResources.getValue(resName, typeValue, true);
            return typeValue.getFloat();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return -1;
    }

    /**
     * 获取属性名对应的resId
     *
     * @param resName
     * @return
     */
    public int getResId(String resName) {
        int resId = getIdentifier(resName, ATTR);
        if (resId != 0) {
            return resId;
        }

        resId = getIdentifier(resName, COLOR);
        if (resId != 0) {
            return resId;
        }

        resId = getIdentifier(resName, DRAWABLE);
        return resId;
    }

    /**
     * 通过在本地id映射到插件中的真实id
     *
     * @param resId
     * @return
     */
    public int getRealResId(int resId) {
        if (isPluginSkin) {
            String resName = getResName(resId, false);
            return getResId(resName);
        }

        return resId;
    }

    /**
     * 解析主题属性id对应的资源color
     *
     * @param theme
     * @param resId
     * @return
     */
    public int getColor(Resources.Theme theme, int resId) {
        try {
            String resName = getResName(resId, false);
            return getTypedValue(theme, getIdentifier(resName, ATTR)).data;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return 0;
    }

    /**
     * 解析主题属性名对应的值
     *
     * @param theme
     * @param resName
     * @return
     */
    public float getFloatValue(Resources.Theme theme, String resName) {
        return getTypedValue(theme, getIdentifier(resName, ATTR)).getFloat();
    }

    /**
     * 解析主题属性名对应的resId
     *
     * @param theme
     * @param resName
     * @return
     */
    public int getResId(Resources.Theme theme, String resName) {
        return getTypedValue(theme, getIdentifier(resName, ATTR)).resourceId;
    }

    /**
     * 解析主题attrId对应的resId
     *
     * @param theme
     * @param attrId
     * @return
     */
    public int getResId(Resources.Theme theme, int attrId) {
        return getTypedValue(theme, attrId).resourceId;
    }

    /**
     * 根据resId获取资源名称
     *
     * @param resId
     * @param isPluginRes 代表resId是否为插件中的资源id
     * @return
     */
    public String getResName(int resId, boolean isPluginRes) {
        try {
            Resources resources = !isPluginSkin || isPluginRes ? mResources : AppUtil.getResources();
            return resources.getResourceEntryName(resId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 根据主题和资源id，获取类型值
     *
     * @param theme
     * @param resId
     * @return
     */
    private TypedValue getTypedValue(Resources.Theme theme, int resId) {
        try {
            if (!theme.resolveAttribute(resId, typeValue, true)) {
                return typeValue;
            }

            if (typeValue.type == TypedValue.TYPE_ATTRIBUTE) {
                return getTypedValue(theme, typeValue.data);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return typeValue;
    }

    /**
     * 根据名称和类型获取resId
     *
     * @param resName
     * @param defType
     * @return
     */
    private int getIdentifier(String resName, String defType) {
        return mResources.getIdentifier(resName, defType, mPackageName);
    }
}
