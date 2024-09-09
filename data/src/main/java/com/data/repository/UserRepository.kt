package com.data.repository

import com.data.cache.CacheFactory
import com.data.database.db.DBFactory
import com.data.network.api.user.UserService
import com.data.preferences.AppSp
import com.data.preferences.UserSp
import kotlinx.coroutines.flow.flow

/**
 * 用户数据仓库
 */
object UserRepository {

    /**
     * 登录
     */
    fun login(phone: String, password: String) = flow {
        // 发起登录请求
        val result = UserService.login2(phone, password)

        // 登录成功
        if (result.isSuccess()) {
            val user = result.data!!
            // 记录登录并保存到SP
            AppSp.login(user.id)
            UserSp.putUser(user)

            // 保存到缓存和数据库
            CacheFactory.getUserCache().save(user )
            DBFactory.getUserDao().saveOrUpdate(user.toUserDB())
        }

        // 发送结果
        emit(result)
    }
}