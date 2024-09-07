package com.data.network.api

import com.common.data.NetResult
import com.common.data.NetStatus
import com.common.data.NoNetworkException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException


open class BaseService {

    /**
     * 对请求结果进行统一处理
     */
    protected suspend fun <T> handleResult(call: suspend () -> NetResult<T>): NetResult<T> {
        return try {
            val result = call()
            if (result.isSuccess()) {
                result.status = NetStatus.Success
            } else {
                result.status = NetStatus.Failed
            }
            result
        } catch (exception: Exception) {
            //请求异常
            handleException(exception)
        }
    }

    /**
     * 处理连接失败的异常
     */
    private fun handleException(ex: Exception): NetResult<Nothing> {
        return when (ex) {
            is HttpException -> NetResult.failed(ex.code(), ex.message())
            is SocketTimeoutException -> NetResult.failed(ResultCode.socketTimeout, "连接超时")
            is ConnectException -> NetResult.failed(ResultCode.connectFailed, "连接失败")
            is NoNetworkException -> NetResult.failed(ResultCode.withoutNetwork, "当前网络不可用，请稍后再试")

            else -> NetResult.failed(ResultCode.unknownError, ex.message.orEmpty())
        }
    }
}