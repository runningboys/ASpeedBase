package com.common.utils.log;

/**
 * 打印到磁盘日志格式
 *
 * @author LiuFeng
 * @data 2021/3/15 22:58
 */
public class DiskLogFormat extends LogFormat {

    @Override
    public String format(LogEvent logEvent, String TAG) {
        buffer.append(buffer.length() > 0 ? "\n" : "");
        buffer.append(TAG);
        buffer.append(": ");
        buffer.append("[");
        buffer.append(logEvent.level.name());
        buffer.append("] ");
        buffer.append(getDateTime(logEvent.timestamp));
        buffer.append(" ");
        buffer.append(getStackTrace(logEvent.threadName, logEvent.stackTrace, 5));
        buffer.append(" ");
        buffer.append(logEvent.tag);
        buffer.append(" ");
        buffer.append(logEvent.message);
        buffer.append("\n");
        String content = buffer.toString();
        buffer.delete(0, buffer.length());
        return content;
    }
}