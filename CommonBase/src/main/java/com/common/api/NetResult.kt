package com.common.api

/**
 * 网络请求结果
 *
 * @param <T>
</T> */
class NetResult<T>(
    /**
     * 请求状态
     */
    var status: NetStatus,

    /**
     * 数据
     */
    var data: T,

    /**
     * 状态码
     */
    var code: Int,

    /**
     * 描述
     */
    var desc: String?
) {
    companion object {
        /**
         * 加载中
         *
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> loading(): NetResult<T?> {
            return NetResult(NetStatus.Loading, null, 0, null)
        }

        /**
         * 加载完成
         *
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> complete(): NetResult<T?> {
            return NetResult(NetStatus.Complete, null, 0, null)
        }

        /**
         * 成功
         *
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> success(data: T): NetResult<T> {
            return NetResult(NetStatus.Success, data, 200, "ok")
        }

        /**
         * 失败
         *
         * @param <T>
         * @return
        </T> */
        @JvmStatic
        fun <T> failed(code: Int, desc: String?): NetResult<T?> {
            return NetResult(NetStatus.Failed, null, code, desc)
        }
    }
}