package com.data.network.api

import com.data.network.api.user.UserService

/**
 * 网络接口工厂类
 *
 * @author LiuFeng
 * @data 2021/3/30 16:18
 */
object ApiFactory {

    /**
     * 获取用户请求服务
     */
    @JvmStatic
    fun getUserService(): UserService = UserService
}