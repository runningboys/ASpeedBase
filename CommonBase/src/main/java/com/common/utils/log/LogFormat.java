package com.common.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志格式
 *
 * @author LiuFeng
 * @data 2021/3/13 8:37
 */
public abstract class LogFormat {
    protected final StringBuilder buffer = new StringBuilder();

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK);

    private final Date date = new Date();

    /**
     * 格式化输出
     *
     * @param logEvent
     * @param TAG
     * @return
     */
    public abstract String format(LogEvent logEvent, String TAG);

    /**
     * 获取格式化时间
     *
     * @return
     */
    public String getDateTime(long timestamp) {
        date.setTime(timestamp);
        return timeFormat.format(date);
    }

    /**
     * 获取堆栈信息
     *
     * @param currentThread 当前线程
     * @param stackTraceArr 堆栈数组数据
     * @param deep          取堆栈数据深度(下标)
     *
     * @return
     */
    public String getStackTrace(String currentThread, StackTraceElement[] stackTraceArr, int deep) {
        if (stackTraceArr == null) {
            return "";
        }

        if (deep >= stackTraceArr.length) {
            return "Index Out of Bounds! deep:" + deep + " length:" + stackTraceArr.length;
        }

        StackTraceElement stackTrace = stackTraceArr[deep];
        String format = "[(%s:%d)# %s -> %s]";
        String fileName = stackTrace.getFileName();
        int methodLine = stackTrace.getLineNumber();
        String methodName = stackTrace.getMethodName();
        return String.format(Locale.CHINESE, format, fileName, methodLine, methodName, currentThread);
    }
}