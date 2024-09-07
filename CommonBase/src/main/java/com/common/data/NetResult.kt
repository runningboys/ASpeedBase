package com.common.data

/**
 * 网络请求结果
 *
 * @param <T>
</T> */
open class NetResult<out T>(
        /**
         * 请求状态
         */
        var status: NetStatus,

        /**
         * 数据
         */
        val data: T?,

        /**
         * 状态码
         */
        var code: Int,

        /**
         * 描述
         */
        var desc: String?
) {

    fun isSuccess(): Boolean {
        return code == 200
    }


    companion object {
        /**
         * 加载中
         */
        @JvmStatic
        fun <T> loading(): NetResult<Nothing> {
            return NetResult(NetStatus.Loading, null, 0, null)
        }

        /**
         * 加载完成
         */
        @JvmStatic
        fun <T> complete(): NetResult<T?> {
            return NetResult(NetStatus.Complete, null, 0, null)
        }

        /**
         * 成功
         */
        @JvmStatic
        fun <T> success(data: T): NetResult<T> {
            return NetResult(NetStatus.Success, data, 200, "ok")
        }

        /**
         * 失败
         */
        @JvmStatic
        fun failed(code: Int, desc: String?): NetResult<Nothing> {
            return NetResult(NetStatus.Failed, null, code, desc)
        }
    }
}