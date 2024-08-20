package com.common.utils.cache

import androidx.collection.LruCache

/**
 * 内存缓存
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
class MemoryCache<K : Any, V : Any> : ICache<K, V> {
    private var cache: LruCache<K, V>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 8
        cache = LruCache(cacheSize)
    }


    override fun put(key: K, value: V) {
        if (cache[key] != null) {
            cache.remove(key)
        }
        cache.put(key, value)
    }

    override fun get(key: K): V? {
        return cache[key]
    }

    override fun remove(key: K) {
        if (cache[key] != null) {
            cache.remove(key)
        }
    }

    override fun containsKey(key: K): Boolean {
        return cache[key] != null
    }

    override fun isEmpty(): Boolean {
        return cache.size() == 0
    }

    override fun clear() {
        cache.evictAll()
    }
}