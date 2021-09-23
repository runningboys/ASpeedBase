package com.common.skin.factory;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.common.skin.api.SkinManager;
import com.common.skin.attr.SkinAttr;
import com.common.skin.attr.SkinAttrSupport;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

/**
 * 布局xml加载工厂，用于解析布局中view的换肤属性
 *
 * @author LiuFeng
 * @data 2021/8/24 10:40
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2 {
    private static final String TAG = "SkinLayoutInflaterFactory";

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    private static final Map<String, String> sSuccessClassNamePrefixMap = new HashMap<>();

    /**
     * LayoutInflater.createView(four args) is provided in Android P, but some ROM did't follow the official.
     */
    private static boolean sCanUseCreateViewFourArguments = true;
    private static boolean sDidCheckLayoutInflaterCreateViewExitFourArgMethod = false;

    private final WeakReference<Activity> mActivity;
    private final LayoutInflater mInflater;

    public static void load(Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        LayoutInflaterCompat.setFactory2(layoutInflater, new SkinLayoutInflaterFactory(activity, layoutInflater));
    }

    public static void load(LayoutInflater layoutInflater) {
        LayoutInflater.Factory2 factory2 = layoutInflater.getFactory2();
        if (factory2 instanceof SkinLayoutInflaterFactory) {
            SkinLayoutInflaterFactory skinFactory = (SkinLayoutInflaterFactory) factory2;
            LayoutInflaterCompat.setFactory2(layoutInflater, skinFactory.cloneIfNeeded(layoutInflater));
        }
    }

    public SkinLayoutInflaterFactory cloneIfNeeded(LayoutInflater layoutInflater) {
        if(mInflater.getContext() == layoutInflater.getContext()) {
            return this;
        }
        return new SkinLayoutInflaterFactory(mActivity.get(), layoutInflater);
    }

    public SkinLayoutInflaterFactory(Activity activity, LayoutInflater inflater) {
        mActivity = new WeakReference<>(activity);
        mInflater = inflater;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = null;
        Activity activity = mActivity.get();
        if (activity instanceof AppCompatActivity) {
            view = ((AppCompatActivity) activity).getDelegate().createView(parent, name, context, attrs);
        }

        if (view == null) {
            try {
                if (!name.contains(".")) {
                    if (sSuccessClassNamePrefixMap.containsKey(name)) {
                        view = mInflater.createView(name, sSuccessClassNamePrefixMap.get(name), attrs);
                    } else {
                        for (String prefix : sClassPrefixList) {
                            try {
                                view = mInflater.createView(name, prefix, attrs);
                                if (view != null) {
                                    sSuccessClassNamePrefixMap.put(name, prefix);
                                    break;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (!sDidCheckLayoutInflaterCreateViewExitFourArgMethod) {
                            try {
                                LayoutInflater.class.getDeclaredMethod("createView", Context.class, String.class, String.class, AttributeSet.class);
                            } catch (Exception e) {
                                sCanUseCreateViewFourArguments = false;
                            }
                            sDidCheckLayoutInflaterCreateViewExitFourArgMethod = true;
                        }
                        if (sCanUseCreateViewFourArguments) {
                            view = mInflater.createView(name, null, attrs);
                        } else {
                            view = originCreateViewForLowSDK(name, context, attrs);
                        }
                    } else {
                        view = originCreateViewForLowSDK(name, context, attrs);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to inflate view " + name + "; error: " + e.getMessage());
            }
        }

        parseAttr(attrs, view);
        return view;
    }

    private View originCreateViewForLowSDK(String name, Context context, AttributeSet attrs) throws Exception {
        Field field = LayoutInflater.class.getDeclaredField("mConstructorArgs");
        field.setAccessible(true);
        Object[] mConstructorArgs = (Object[]) field.get(mInflater);
        Object lastContext = mConstructorArgs[0];
        mConstructorArgs[0] = context;
        View view = mInflater.createView(name, null, attrs);
        mConstructorArgs[0] = lastContext;
        return view;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return onCreateView(null, name, context, attrs);
    }

    private void parseAttr(AttributeSet attrs, View view) {
        if (view != null) {
            List<SkinAttr> skinAttrs = SkinAttrSupport.getSkinAttrs(attrs);
            if (!skinAttrs.isEmpty()) {
                SkinManager.getInstance().loadSkinAttr(view, attrBuilder -> attrBuilder.saveAttrs(skinAttrs));
            }
        }
    }
}
