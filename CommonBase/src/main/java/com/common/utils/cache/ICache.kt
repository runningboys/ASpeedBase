package com.common.utils.cache

/**
 * 缓存接口
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
internal interface ICache<K, V> {
    /**
     * 存入缓存
     *
     * @param key
     * @param value
     */
    fun put(key: K, value: V)

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    operator fun get(key: K): V?

    /**
     * 删除缓存
     *
     * @param key
     */
    fun remove(key: K)

    /**
     * 缓存中是否存在
     *
     * @param key
     * @return
     */
    fun containsKey(key: K): Boolean

    /**
     * 判断是否为空
     *
     * @return
     */
    fun isEmpty(): Boolean

    /**
     * 清空缓存
     */
    fun clear()
}