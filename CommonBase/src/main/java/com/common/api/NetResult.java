package com.common.api;

/**
 * 网络请求结果
 *
 * @param <T>
 */
public class NetResult<T> {

    /**
     * 状态码
     */
    public int code;

    /**
     * 描述
     */
    public String desc;

    /**
     * 数据
     */
    public T data;

    /**
     * 请求状态
     */
    public NetStatus status;

    /**
     * 构造方法
     *
     * @param status
     * @param data
     * @param code
     * @param desc
     */
    public NetResult(NetStatus status, T data, int code, String desc) {
        this.code = code;
        this.desc = desc;
        this.data = data;
        this.status = status;
    }

    /**
     * 加载中
     *
     * @param <T>
     * @return
     */
    public static <T> NetResult<T> loading() {
        return new NetResult<>(NetStatus.Loading, null, 0, null);
    }

    /**
     * 加载完成
     *
     * @param <T>
     * @return
     */
    public static <T> NetResult<T> complete() {
        return new NetResult<>(NetStatus.Complete, null, 0, null);
    }

    /**
     * 成功
     *
     * @param <T>
     * @return
     */
    public static <T> NetResult<T> success(T data) {
        return new NetResult<>(NetStatus.Success, data, 200, "ok");
    }

    /**
     * 失败
     *
     * @param <T>
     * @return
     */
    public static <T> NetResult<T> failed(int code, String desc) {
        return new NetResult<>(NetStatus.Failed, null, code, desc);
    }
}
