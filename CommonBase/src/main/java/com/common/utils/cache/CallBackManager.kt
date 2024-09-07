package com.common.utils.cache

/**
 * 回调管理
 *
 * @author LiuFeng
 * @data 2020/9/1 15:03
 */
object CallBackManager {
    @Volatile
    private var sn: Long = 1
    private val callbackMap = mutableMapOf<Long, Function<Any>>()

    /**
     * 判断是否包含
     *
     * @param sn
     * @return
     */
    fun containsSN(sn: Long): Boolean {
        return callbackMap.containsKey(sn)
    }

    /**
     * 获取普通回调
     * 备注：只能获取一次，不能重复获取！！！
     *
     * @param sn
     * @param <T>
     * @return
    </T> */
    fun <T : Function<Any>> getCallBack(sn: Long): T? {
        return callbackMap.remove(sn) as T?
    }

    /**
     * 获取保持回调
     * 备注：可多次获取
     *
     * @param sn
     * @param <T>
     * @return
    </T> */
    fun <T : Function<Any>> getKeepCallback(sn: Long): T? {
        return callbackMap[sn] as T?
    }

    /**
     * 添加回调
     *
     * @param callback
     * @return
     */
    fun addCallBack(callback: Function<Any>): Long {
        val sn = sn++
        return addCallBack(sn, callback)
    }

    /**
     * 添加回调
     *
     * @param sn
     * @param callback
     * @return
     */
    fun addCallBack(sn: Long, callback: Function<Any>): Long {
        callbackMap[sn] = callback
        return sn
    }

    /**
     * 移除回调
     *
     * @param sn
     */
    fun removeCallBack(sn: Long) {
        callbackMap.remove(sn)
    }

    /**
     * 清空回调
     */
    fun clear() {
        callbackMap.clear()
    }
}