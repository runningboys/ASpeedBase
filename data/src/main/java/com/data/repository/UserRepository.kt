package com.data.repository

import com.data.network.api.user.UserService
import com.data.sp.AppSp
import kotlinx.coroutines.flow.flow

/**
 * 用户数据仓库
 */
object UserRepository {

    /**
     * 登录
     */
    fun login(phone: String, password: String) = flow {
        val result = UserService.login2(phone, password)

        if (result.isSuccess()) {
            AppSp.login(result.data!!.id)
        }

        emit(result)
    }
}