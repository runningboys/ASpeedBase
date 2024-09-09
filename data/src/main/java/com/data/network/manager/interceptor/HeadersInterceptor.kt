package com.data.network.manager.interceptor

import com.common.utils.resource.AppUtil
import com.common.utils.resource.DeviceUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 请求头的拦截类，可以在此处添加公共的请求头
 */
class HeadersInterceptor : Interceptor {
    private val osName = DeviceUtil.getPhoneManufacturer()
    private val deviceModel = DeviceUtil.getPhoneModel()
    private val brand = DeviceUtil.getPhoneBrand()
    private val version = AppUtil.getVersionName()
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val requestBuilder: Request.Builder = builder.addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .addHeader("platform", "android")
                .addHeader("model", brand)
                .addHeader("version", version)
                .addHeader("osName", osName)
        return chain.proceed(requestBuilder.build())
    }
}