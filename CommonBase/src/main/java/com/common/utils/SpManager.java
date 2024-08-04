package com.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * SharedPreferences存储管理类
 *
 * @author LiuFeng
 * @data 2021/9/16 14:21
 */
public class SpManager {
    private static final String DEFAULT_NAME = "default-sp";
    private static final Map<String, SpManager> instanceMap = new HashMap<>();
    private final SharedPreferences sp;

    /**
     * 获取默认实例
     *
     * @param context
     * @return
     */
    public static SpManager defaultInstance(Context context) {
        return of(context, DEFAULT_NAME);
    }

    /**
     * 获取对应实例
     *
     * @param context
     * @param name
     * @return
     */
    public static SpManager of(Context context, String name) {
        SpManager instance = instanceMap.get(name);
        if (instance == null) {
            instance = new SpManager(context, name);
            instanceMap.put(name, instance);
        }
        return instance;
    }

    /**
     * 私有化构造函数
     *
     * @param context
     * @param name
     */
    private SpManager(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 获取String--基础方法
     */
    public String getString(@NonNull String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * 设置String--基础方法
     */
    public void putString(@NonNull String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取Boolean--基础方法
     */
    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * 设置Boolean--基础方法
     */
    public void putBoolean(@NonNull String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取Int--基础方法
     */
    public int getInt(@NonNull String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * 设置Int--基础方法
     */
    public void putInt(@NonNull String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 获取Long--基础方法
     */
    public long getLong(@NonNull String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    /**
     * 设置Long--基础方法
     */
    public void putLong(@NonNull String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    /**
     * 包含key键--基础方法
     */
    public boolean contains(@NonNull String key) {
        return sp.contains(key);
    }

    /**
     * 移除指定key--基础方法
     */
    public void remove(@NonNull String key) {
        sp.edit().remove(key).apply();
    }

    /**
     * 清除全部--基础方法
     */
    public void clearAll() {
        sp.edit().clear().apply();
    }
}
