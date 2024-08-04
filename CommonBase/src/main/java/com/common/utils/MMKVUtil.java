package com.common.utils;

import android.content.Context;

import com.common.utils.log.LogUtil;
import com.tencent.mmkv.MMKV;

public class MMKVUtil {

    public static void init(Context context) {
        String rootDir = MMKV.initialize(context);
        LogUtil.d("mmkv root: " + rootDir);
    }


    public static <T> void put(String key, T value) {
        MMKV kv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null);
        if (value instanceof String) {
            kv.encode(key, (String) value);
        } else if (value instanceof Integer) {
            kv.encode(key, (Integer) value);
        } else if (value instanceof Long) {
            kv.encode(key, (Long) value);
        } else if (value instanceof Boolean) {
            kv.encode(key, (Boolean) value);
        } else if (value instanceof Float) {
            kv.encode(key, (Float) value);
        } else if (value instanceof Double) {
            kv.encode(key, (Double) value);
        }
    }


    public static <T> T get(String key, T defaultValue) {
        MMKV kv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null);
        if (defaultValue instanceof String) {
            return (T) kv.decodeString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (T) ((Integer) kv.decodeInt(key, (Integer) defaultValue));
        } else if (defaultValue instanceof Long) {
            return (T) ((Long) kv.decodeLong(key, (Long) defaultValue));
        } else if (defaultValue instanceof Boolean) {
            return (T) ((Boolean) kv.decodeBool(key, (Boolean) defaultValue));
        } else if (defaultValue instanceof Float) {
            return (T) ((Float) kv.decodeFloat(key, (Float) defaultValue));
        } else if (defaultValue instanceof Double) {
            return (T) ((Double) kv.decodeDouble(key, (Double) defaultValue));
        }

        return defaultValue;
    }

    public static void remove(String key) {
        MMKV kv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null);
        kv.removeValueForKey(key);
    }
}