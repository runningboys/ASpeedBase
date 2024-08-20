package com.data.network.api.user

import com.data.network.api.model.UserBean
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 用户api接口
 */
interface UserApiService {
    /**
     * 通过手机号注册
     *
     * @return
     */
    @POST("/api/user/phone/public/register")
    fun register(@Body params: Map<String, String>): Observable<UserBean>

    /**
     * 验证码登录
     *
     * @param params
     * @return
     */
    @POST("/api/user/phone/public/login")
    fun login(@Body params: Map<String, String>): Observable<UserBean>
}