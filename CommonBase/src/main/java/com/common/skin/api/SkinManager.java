package com.common.skin.api;

import android.app.Activity;
import android.view.View;

import com.common.skin.callback.SkinApplyListener;
import com.common.skin.callback.SkinAttrAcquirer;
import com.common.skin.callback.SkinChangeListener;
import com.common.skin.callback.SkinInterceptor;
import com.common.skin.handler.SkinHandler;
import com.common.skin.impl.SkinManagerImpl;


/**
 * 皮肤管理器api
 *
 * @author LiuFeng
 * @data 2021/9/8 17:38
 */
public interface SkinManager {

    /**
     * 获取单例
     *
     * @return
     */
    static SkinManager getInstance() {
        return SkinManagerImpl.getInstance();
    }

    /**
     * 添加皮肤变更监听
     *
     * @param listener
     */
    void addListener(SkinChangeListener listener);

    /**
     * 移除皮肤变更监听
     *
     * @param listener
     */
    void removeListener(SkinChangeListener listener);

    /**
     * 给View设置皮肤应用的监听
     *
     * @param view
     * @param listener
     */
    void setSkinApplyListener(View view, SkinApplyListener listener);

    /**
     * 给View设置拦截器，根据需要进行皮肤分发拦截
     *
     * @param view
     * @param interceptor
     */
    void setSkinInterceptor(View view, SkinInterceptor interceptor);

    /**
     * 添加主题皮肤
     *
     * @param skinName
     * @param styleRes
     */
    void addThemeSkin(String skinName, int styleRes);

    /**
     * 添加插件皮肤
     *
     * @param skinName
     * @param pluginPath
     * @param pkgName
     */
    void addPluginSkin(String skinName, String pluginPath, String pkgName);

    /**
     * 切换皮肤
     *
     * @param skin
     */
    void changeSkin(String skin);

    /**
     * 注册UI，支持View、Activity、Fragment、Dialog、Window、PopupWindow
     *
     * @param obj
     */
    void register(Object obj);

    /**
     * 移除注册
     *
     * @param obj
     */
    void unRegister(Object obj);

    /**
     * 加载Activity的布局，用于解析布局中的皮肤属性
     * 注意：需要在Activity的onCreate方法前调用！！
     *
     * @param activity
     */
    void loadActivityXml(Activity activity);

    /**
     * 加载view的皮肤属性
     *
     * @param view
     * @param acquirer
     */
    void loadSkinAttr(View view, SkinAttrAcquirer acquirer);

    /**
     * 给皮肤不同属性类型添加处理器
     *
     * @param attrType
     * @param handler
     */
    void setHandler(String attrType, SkinHandler handler);

    /**
     * 获取当前皮肤
     *
     * @return
     */
    SkinItem getCurrentSkin();

    /**
     * 获取view的皮肤
     *
     * @param view
     * @return
     */
    ViewSkin getViewSkin(View view);

    /**
     * 获取皮肤
     *
     * @param skinName
     * @return
     */
    SkinItem getSkin(String skinName);

    /**
     * 获取资源管理器
     *
     * @return
     */
    ResourceManager getResourceManager();
}
