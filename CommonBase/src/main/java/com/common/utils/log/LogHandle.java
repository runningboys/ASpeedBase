package com.common.utils.log;

/**
 * 日志操作器基类
 *
 * @author LiuFeng
 * @data 2018/9/20 11:45
 */
public abstract class LogHandle {
    protected static String TAG = "DEFAULT_TAG";

    protected LogFormat logFormat;

    public LogHandle(LogFormat logFormat) {
        this.logFormat = logFormat;
    }

    /**
     * 获得日志句柄名称（默认类名）
     *
     * @return 返回日志句柄名称。
     */
    public String getLogHandleName() {
        return this.getClass().getName();
    }

    /**
     * 日志打印
     *
     * @param logEvent 日志事件
     */
    public abstract void log(LogEvent logEvent);

    /**
     * 获取tag
     *
     * @return 全局日志标签。
     */
    public String getTag() {
        return TAG;
    }

    /**
     * 设置tag
     *
     * @param tag 全局日志标签。
     */
    public void setTag(String tag) {
        TAG = tag;
    }
}
