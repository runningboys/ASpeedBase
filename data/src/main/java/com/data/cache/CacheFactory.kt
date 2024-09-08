package com.data.cache

import com.data.cache.user.UserCache

/**
 * 缓存工厂类
 *
 * @author LiuFeng
 * @data 2021/3/30 16:18
 */
object CacheFactory {

    /**
     * 获取用户缓存
     */
    @JvmStatic
    fun getUserCache(): UserCache = UserCache.instance

}