package com.common.utils

import android.content.Context
import com.common.CommonUtil

/**
 * SharedPreferences存储管理类
 *
 * @author LiuFeng
 * @data 2021/9/16 14:21
 */
class SpManager private constructor(name: String) {
    private val sp = CommonUtil.getContext().getSharedPreferences(name, Context.MODE_PRIVATE)


    /**
     * 获取String--基础方法
     */
    fun getString(key: String, defValue: String): String {
        return sp.getString(key, defValue) ?: defValue
    }

    /**
     * 设置String--基础方法
     */
    fun putString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    /**
     * 获取Boolean--基础方法
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sp.getBoolean(key, defValue)
    }

    /**
     * 设置Boolean--基础方法
     */
    fun putBoolean(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * 获取Int--基础方法
     */
    fun getInt(key: String, defValue: Int): Int {
        return sp.getInt(key, defValue)
    }

    /**
     * 设置Int--基础方法
     */
    fun putInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    /**
     * 获取Long--基础方法
     */
    fun getLong(key: String, defValue: Long): Long {
        return sp.getLong(key, defValue)
    }

    /**
     * 设置Long--基础方法
     */
    fun putLong(key: String, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    /**
     * 包含key键--基础方法
     */
    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * 移除指定key--基础方法
     */
    fun remove(key: String) {
        sp.edit().remove(key).apply()
    }

    /**
     * 清除全部--基础方法
     */
    fun clearAll() {
        sp.edit().clear().apply()
    }

    companion object {
        private const val DEFAULT_NAME = "default-sp"
        private val instanceMap: MutableMap<String, SpManager> = HashMap()

        /**
         * 获取默认实例
         */
        fun defaultInstance(): SpManager {
            return of(DEFAULT_NAME)
        }

        /**
         * 获取对应实例
         *
         * @param name
         * @return
         */
        @JvmStatic
        fun of(name: String): SpManager {
            var instance = instanceMap[name]
            if (instance == null) {
                instance = SpManager(name)
                instanceMap[name] = instance
            }
            return instance
        }
    }
}