package com.common.data;

import java.io.Serializable;

/**
 * 服务器响应的结果数据
 *
 * @param <T>
 */
public class ResultData<T> implements Serializable {

    /**
     * 状态码
     */
    public int code;

    /**
     * 描述
     */
    public String desc;

    /**
     * 操作结果
     */
    public boolean ok;

    /**
     * 数据
     */
    public T data;

    /**
     * 状态描述
     */
    public State state;

    public class State {
        public int    code;
        public String desc;
    }

    @Override
    public String toString() {
        return "ResultData{" + "code=" + code + ", desc='" + desc + '\'' + ", ok=" + ok + ", data=" + data + '}';
    }
}
