package com.common.utils.store

import android.content.SharedPreferences
import com.common.base.BaseApp
import com.tencent.mmkv.MMKV
import java.util.concurrent.ConcurrentHashMap


/**
 * MMKV存储管理类
 */
class MMKVManager private constructor(name: String, isMultiProcess: Boolean) {
    private var mmkv = MMKV.mmkvWithID(name, if (isMultiProcess) MMKV.MULTI_PROCESS_MODE else MMKV.SINGLE_PROCESS_MODE)

    companion object {
        private const val DEFAULT_NAME = "default-mmkv"
        private val instanceMap = ConcurrentHashMap<String, MMKVManager>()

        init {
            MMKV.initialize(BaseApp.context)
        }

        /**
         * 获取默认实例
         */
        @JvmStatic
        fun defaultInstance(): MMKVManager {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         */
        @JvmStatic
        fun of(name: String, isMultiProcess: Boolean = false): MMKVManager {
            return instanceMap.getOrPut(name) { MMKVManager(name, isMultiProcess) }
        }
    }


    /**
     * 获取String--基础方法
     */
    fun getString(key: String, defValue: String): String {
        return mmkv.decodeString(key, defValue) ?: defValue
    }

    /**
     * 设置String--基础方法
     */
    fun putString(key: String, value: String) {
        mmkv.encode(key, value)
    }

    /**
     * 获取Boolean--基础方法
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mmkv.decodeBool(key, defValue)
    }

    /**
     * 设置Boolean--基础方法
     */
    fun putBoolean(key: String, value: Boolean) {
        mmkv.encode(key, value)
    }

    /**
     * 获取Int--基础方法
     */
    fun getInt(key: String, defValue: Int): Int {
        return mmkv.decodeInt(key, defValue)
    }

    /**
     * 设置Int--基础方法
     */
    fun putInt(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    /**
     * 获取Long--基础方法
     */
    fun getLong(key: String, defValue: Long): Long {
        return mmkv.decodeLong(key, defValue)
    }

    /**
     * 设置Long--基础方法
     */
    fun putLong(key: String, value: Long) {
        mmkv.encode(key, value)
    }

    /**
     * 包含key键--基础方法
     */
    operator fun contains(key: String): Boolean {
        return mmkv.contains(key)
    }

    /**
     * 移除指定key--基础方法
     */
    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }


    /**
     * 清除全部--基础方法
     */
    fun clearAll() {
        mmkv.clearAll()
    }


    /**
     * 迁移原SharedPreferences的数据到MMKV
     */
    fun transferSpToMMKV(sp: SharedPreferences) {
        mmkv.importFromSharedPreferences(sp)
    }
}