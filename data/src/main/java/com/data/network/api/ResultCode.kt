package com.data.network.api


object ResultCode {

    const val unknownError = -10000   //未知错误
    const val socketTimeout = -10001  //超时
    const val connectFailed = -10002  //连接失败
    const val withoutNetwork = -10003 //没有网络

    const val ok = 200 //请求成功

}