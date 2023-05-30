package com.common.data

/**
 * api异常处理
 *
 * @author LiuFeng
 * @data 2020/2/10 11:14
 */
class ApiException(resultData: ResultData<*>) : RuntimeException() {
    val code: Int
    val desc: String?
    val data: Any?

    init {
        data = resultData.data
        this.code = resultData.code
        desc = resultData.desc
    }

    override fun toString(): String {
        return "ApiException{code=$code, desc='$desc', data=$data}"
    }
}