package com.data.cache

import com.common.utils.log.LogUtil
import com.data.preferences.AppSp

/**
 * 缓存数据管理类
 *
 * @author LiuFeng
 * @data 2020/2/19 20:47
 */
object CacheManager {
    /**
     * 构建缓存
     */
    fun buildCache() {
        // 判断是否有登录账号
        val account = AppSp.getLoginUserId()
        if (account.isBlank()) return

        LogUtil.d("构建缓存")
//        UserRepository.getInstance().buildCache()
    }

    /**
     * 清空缓存
     */
    fun clearCache() {
        LogUtil.d("清空缓存")
        CacheFactory.getUserCache().clear()
    }
}