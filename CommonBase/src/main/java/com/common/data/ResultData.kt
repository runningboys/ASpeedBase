package com.common.data

import java.io.Serializable

/**
 * 服务器响应的结果数据
 *
 * @param <T>
</T> */
class ResultData<T> : Serializable {
    /**
     * 状态码
     */
    var code = 0

    /**
     * 描述
     */
    var desc: String? = null

    /**
     * 操作结果
     */
    var ok = false

    /**
     * 数据
     */
    @JvmField
    var data: T? = null

    /**
     * 状态描述
     */
    var state: State? = null

    inner class State {
        var code = 0
        var desc: String? = null
    }

    override fun toString(): String {
        return "ResultData{code=$code, desc='$desc', ok=$ok, data=$data}"
    }
}