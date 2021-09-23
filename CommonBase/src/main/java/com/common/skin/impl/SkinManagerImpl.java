package com.common.skin.impl;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.common.base.R;
import com.common.skin.api.ResourceManager;
import com.common.skin.api.SkinItem;
import com.common.skin.api.SkinManager;
import com.common.skin.api.SkinType;
import com.common.skin.api.ViewSkin;
import com.common.skin.attr.SkinAttr;
import com.common.skin.attr.SkinAttrBuilder;
import com.common.skin.attr.SkinAttrSupport;
import com.common.skin.attr.SkinAttrType;
import com.common.skin.callback.SkinApplyListener;
import com.common.skin.callback.SkinAttrAcquirer;
import com.common.skin.callback.SkinChangeListener;
import com.common.skin.callback.SkinInterceptor;
import com.common.skin.factory.SkinLayoutInflaterFactory;
import com.common.skin.handler.AlphaHandler;
import com.common.skin.handler.BackgroundHandler;
import com.common.skin.handler.HintColorHandler;
import com.common.skin.handler.SkinHandler;
import com.common.skin.handler.SrcHandler;
import com.common.skin.handler.TextColorHandler;
import com.common.utils.AppUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.MainThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


/**
 * 皮肤管理器
 *
 * @author LiuFeng
 * @data 2021/8/24 20:04
 */
public class SkinManagerImpl implements SkinManager {
    private static final String TAG = "SkinManager";
    private static final SkinItem DEFAULT_SKIN = new SkinItem("", 0);
    private static final SkinManagerImpl instance = new SkinManagerImpl();
    private SkinItem currentSkin = DEFAULT_SKIN;
    private ResourceManager resourceManager;

    private final Map<String, SkinItem> skinMap = new HashMap<>();
    private final Map<String, SkinHandler> handlerMap = new HashMap<>();
    private final List<SkinChangeListener> listeners = new ArrayList<>();
    private final List<WeakReference<?>> skinObservers = new ArrayList<>();

    // 布局变化监听，处理动态添加view后，修改其皮肤
    private final View.OnLayoutChangeListener layoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
        ViewSkin parentSkin;
        if (v instanceof ViewGroup && (parentSkin = getViewSkin(v)) != null) {
            ViewGroup viewGroup = (ViewGroup) v;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                ViewSkin childSkin = getViewSkin(child);
                if (!parentSkin.equals(childSkin)) {
                    dispatch(child, parentSkin.skin);
                }
            }
        }
    };

    // 布局层级变化监听，处理动态添加view后，修改其皮肤（这个监听处理的层级比Layout监听少，但它是set监听，可能被覆盖）
    private final ViewGroup.OnHierarchyChangeListener hierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            ViewSkin parentSkin = getViewSkin(parent);
            if (parentSkin != null) {
                ViewSkin childSkin = getViewSkin(child);
                if (!parentSkin.equals(childSkin)) {
                    dispatch(child, parentSkin.skin);
                }
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
        }
    };


    @MainThread
    public static SkinManagerImpl getInstance() {
        return instance;
    }

    /**
     * 私有化构造函数
     */
    private SkinManagerImpl() {
        // 安装皮肤处理器
        installHandlers();

        // 皮肤偏好设置
        SkinItem item = SkinPreUtil.getSkin();
        if (item != null && !TextUtils.equals(item.getSkin(), currentSkin.getSkin())) {
            // 加载成功才替换当前皮肤
            if (loadResManager(item)) {
                currentSkin = item;
                skinMap.put(item.getSkin(), item);
            }
        } else {
            loadResManager(currentSkin);
        }
    }

    @Override
    public void addListener(SkinChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SkinChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setSkinApplyListener(View view, SkinApplyListener listener) {
        view.setTag(R.id.skin_apply_listener, listener);
    }

    @Override
    public void setSkinInterceptor(View view, SkinInterceptor interceptor) {
        view.setTag(R.id.skin_interceptor, interceptor);
    }

    @Override
    public void addThemeSkin(String skinName, int styleRes) {
        SkinItem item = new SkinItem(skinName, styleRes);
        skinMap.put(skinName, item);
    }

    @Override
    public void addPluginSkin(String skinName, String pluginPath, String pkgName) {
        SkinItem item = new SkinItem(skinName, pluginPath, pkgName);
        skinMap.put(skinName, item);
    }

    @Override
    public void changeSkin(String skinName) {
        // 主题没变化，不更改
        if (TextUtils.isEmpty(skinName) || TextUtils.equals(skinName, currentSkin.getSkin())) {
            return;
        }

        // 无此主题，不更改
        SkinItem item = skinMap.get(skinName);
        if (item == null) {
            return;
        }

        // 插件主题加载失败，不更改
        if (!loadResManager(item)) {
            return;
        }

        SkinItem oldSkin = currentSkin;
        currentSkin = item;
        SkinPreUtil.setSkin(item);
        for (int i = skinObservers.size() - 1; i >= 0; i--) {
            Object obj = skinObservers.get(i).get();
            if (obj == null) {
                skinObservers.remove(i);
                continue;
            }

            View view = getView(obj);
            if (view != null) {
                dispatch(view, skinName);
            }
        }

        for (SkinChangeListener listener : listeners) {
            listener.onSkinChange(oldSkin, currentSkin);
        }
    }

    @Override
    public void setHandler(String attrType, SkinHandler handler) {
        handlerMap.put(attrType, handler);
    }

    @Override
    public void register(Object obj) {
        observe(obj);
    }

    @Override
    public void unRegister(Object obj) {
        removeSkinObserver(obj);
    }

    @Override
    public void loadActivityXml(Activity activity) {
        SkinLayoutInflaterFactory.load(activity);
    }

    /**
     * 注册观察者
     *
     * @param obj
     */
    private void observe(Object obj) {
        if (!containSkinObserver(obj) && containsView(obj)) {
            skinObservers.add(new WeakReference<>(obj));
        }
    }

    /**
     * 判断是否包含此观察者
     *
     * @param obj
     * @return
     */
    private boolean containSkinObserver(Object obj) {
        for (int i = skinObservers.size() - 1; i >= 0; i--) {
            Object item = skinObservers.get(i).get();
            if (item == obj) {
                return true;
            }

            if (item == null) {
                skinObservers.remove(i);
            }
        }

        return false;
    }

    /**
     * 移除观察者
     *
     * @param obj
     */
    private void removeSkinObserver(Object obj) {
        for (int i = skinObservers.size() - 1; i >= 0; i--) {
            Object item = skinObservers.get(i).get();
            if (item == obj) {
                skinObservers.remove(i);
                return;
            }

            if (item == null) {
                skinObservers.remove(i);
            }
        }
    }

    /**
     * 判断注册对象是否包含View
     *
     * @param obj
     * @return
     */
    private boolean containsView(Object obj) {
        if (obj instanceof Activity) {
            return true;
        }

        if (obj instanceof Fragment) {
            return true;
        }

        if (obj instanceof Dialog) {
            return true;
        }

        if (obj instanceof PopupWindow) {
            return true;
        }

        if (obj instanceof Window) {
            return true;
        }

        return obj instanceof View;
    }

    /**
     * 获取注册对象的View
     *
     * @param obj
     * @return
     */
    private View getView(Object obj) {
        if (obj instanceof Activity) {
            return ((Activity) obj).findViewById(Window.ID_ANDROID_CONTENT);
        }

        if (obj instanceof Fragment) {
            return ((Fragment) obj).getView();
        }

        if (obj instanceof Dialog) {
            Window window = ((Dialog) obj).getWindow();
            if (window != null) {
                return window.getDecorView();
            }
        }

        if (obj instanceof PopupWindow) {
            return ((PopupWindow) obj).getContentView();
        }

        if (obj instanceof Window) {
            return ((Window) obj).getDecorView();
        }

        if (obj instanceof View) {
            return (View) obj;
        }

        return null;
    }

    @Override
    public void loadSkinAttr(View view, SkinAttrAcquirer acquirer) {
        SkinAttrBuilder attrBuilder = SkinAttrBuilder.obtain();
        acquirer.capture(attrBuilder);
        if (!attrBuilder.isEmpty()) {
            String attrs = attrBuilder.build();
            attrBuilder.recycle();
            if (!TextUtils.isEmpty(attrs)) {
                view.setTag(R.id.skin_attrs, attrs);
                dispatch(view, currentSkin.getSkin());
            }
        }
    }

    /**
     * 下发皮肤到UI
     *
     * @param view
     * @param skin
     */
    private void dispatch(View view, String skin) {
        SkinItem skinItem = skinMap.get(skin);
        if (skinItem != null) {
            runDispatch(view, skinItem);
        } else if (!TextUtils.equals(skin, DEFAULT_SKIN.getSkin())) {
            throw new IllegalArgumentException("The skin " + skin + " does not exist");
        }
    }

    /**
     * 执行皮肤下发
     *
     * @param view
     * @param skinItem
     */
    private void runDispatch(View view, SkinItem skinItem) {
        String skin = skinItem.getSkin();
        ViewSkin viewSkin = getViewSkin(view);
        if (viewSkin != null && TextUtils.equals(viewSkin.skin, skin)) {
            return;
        }
        view.setTag(R.id.view_skin, new ViewSkin(skin));

        // 皮肤分发拦截
        Object obj = view.getTag(R.id.skin_interceptor);
        if (obj instanceof SkinInterceptor) {
            if (((SkinInterceptor) obj).intercept(view, skinItem)) {
                return;
            }
        }

        // 应用皮肤
        applySkin(view, skinItem);

        // 递归视图树
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            setViewGroupListener(viewGroup);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                runDispatch(viewGroup.getChildAt(i), skinItem);
            }
        }
    }

    /**
     * 应用皮肤
     *
     * @param view
     * @param skinItem
     */
    private void applySkin(View view, SkinItem skinItem) {
        String attrsTag = (String) view.getTag(R.id.skin_attrs);
        List<SkinAttr> skinAttrs = SkinAttrSupport.getSkinTags(attrsTag);
        for (SkinAttr attr : skinAttrs) {
            SkinAttrType attrType = attr.attrType;
            SkinHandler handler = handlerMap.get(attrType.type);
            if (handler != null) {
                String resName = attr.attrValueName;
                SkinAttr skinAttr = new SkinAttr(attrType, resName);

                // 插件资源：resName --> resId
                if (skinItem.isPlugin()) {
                    if (attrType == SkinAttrType.ALPHA) {
                        skinAttr.attrValueData = resourceManager.getFloatValue(resName);
                    } else {
                        skinAttr.attrValueResId = resourceManager.getResId(resName);
                    }
                }
                // 主题资源：resName --> attrId --> Theme解析 --> resId
                else {
                    if (attrType == SkinAttrType.ALPHA) {
                        skinAttr.attrValueData = resourceManager.getFloatValue(skinItem.getTheme(), resName);
                    } else {
                        skinAttr.attrValueResId = resourceManager.getResId(skinItem.getTheme(), resName);
                    }
                }

                handler.handle(view, skinAttr);
            }
        }

        // 皮肤应用监听
        Object obj = view.getTag(R.id.skin_apply_listener);
        if (obj instanceof SkinApplyListener) {
            ((SkinApplyListener) obj).onApply(view, skinItem);
        }
    }

    /**
     * 监听ViewGroup，处理view的添加移除等
     *
     * @param viewGroup
     */
    private void setViewGroupListener(ViewGroup viewGroup) {
        if (userHierarchyListener(viewGroup)) {
            viewGroup.setOnHierarchyChangeListener(hierarchyChangeListener);
        } else {
            viewGroup.addOnLayoutChangeListener(layoutChangeListener);
        }
    }

    /**
     * 判断对ViewGroup是否使用层次监听
     *
     * @param viewGroup
     * @return
     */
    private boolean userHierarchyListener(ViewGroup viewGroup) {
        return viewGroup instanceof RecyclerView || viewGroup instanceof ViewPager || viewGroup instanceof AdapterView;
    }

    @Override
    public ViewSkin getViewSkin(View view) {
        Object obj = view.getTag(R.id.view_skin);
        if (obj instanceof ViewSkin) {
            return (ViewSkin) obj;
        }

        return null;
    }

    @Override
    public SkinItem getSkin(String skinName) {
        return skinMap.get(skinName);
    }

    @Override
    public SkinItem getCurrentSkin() {
        return currentSkin;
    }

    @Override
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * 安装皮肤处理器
     */
    private void installHandlers() {
        setHandler(SkinAttrType.SRC.type, new SrcHandler());
        setHandler(SkinAttrType.ALPHA.type, new AlphaHandler());
        setHandler(SkinAttrType.BACKGROUND.type, new BackgroundHandler());
        setHandler(SkinAttrType.TEXT_COLOR.type, new TextColorHandler());
        setHandler(SkinAttrType.HINT_COLOR.type, new HintColorHandler());
    }

    /**
     * 加载资源管理器
     *
     * @param skinItem
     * @return
     */
    private boolean loadResManager(SkinItem skinItem) {
        // 非插件，创建本地资源管理
        if (!skinItem.isPlugin()) {
            if (resourceManager == null || resourceManager.isPluginSkin()) {
                resourceManager = new ResourceManager(AppUtil.getResources(), AppUtil.getPackageName(), false);
            }
            return true;
        }

        // 是插件，创建插件资源管理
        Resources resources = loadPlugin(skinItem.getPluginPath());
        if (resources == null) {
            // 插件资源资源创建失败，则创建本地资源
            if (resourceManager == null || resourceManager.isPluginSkin()) {
                resourceManager = new ResourceManager(AppUtil.getResources(), AppUtil.getPackageName(), false);
            }
            return false;
        }

        // 插件资源创建成功
        resourceManager = new ResourceManager(resources, skinItem.getPkgName(), true);
        return true;
    }

    /**
     * 加载插件资源，建议在非ui线程工作
     *
     * @param skinPath
     */
    private Resources loadPlugin(String skinPath) {
        if (TextUtils.isEmpty(skinPath) || !new File(skinPath).exists()) {
            return null;
        }

        try {
            //获得系统资源管理类实例
            AssetManager assetManager = AssetManager.class.newInstance();
            //获得系统资源管理类下的添加资源路径方法
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            //调用该方法，并且传入皮肤所在的路径
            addAssetPath.invoke(assetManager, skinPath);
            //获得resources对象，现在获得的还是当前app内的
            Resources superRes = AppUtil.getResources();
            //创建一个新的资源对象
            return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

}
