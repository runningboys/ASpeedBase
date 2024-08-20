package com.common.utils;

import com.common.CommonUtil;
import com.common.data.ResultData;
import com.common.utils.log.LogUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络时间工具
 *
 * @author LiuFeng
 * @data 2021/3/1 17:26
 */
public class NetTimeUtil {
    private static final String TAG = "NetTimeUtil";
    private static final String NET_TIME_NAME = "net_time";
    private static final String KEY_OFFSET_TIME = "offsetTime";

    private static long offsetTime;

    // Boolean值表示链接能否高精度获取时间戳
    private static final Map<String, Boolean> webUrlMap = new HashMap<>();

    // sp数据持久化
    private static final SpManager spManager = SpManager.of(NET_TIME_NAME);

    static {
        webUrlMap.put("http://www.baidu.com", false);  //百度
        webUrlMap.put("http://www.taobao.com", false); //淘宝
        webUrlMap.put("http://www.360.cn", false);     //360
        webUrlMap.put("http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp", true); // 淘宝接口
    }


    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis() + offsetTime;
    }


    /**
     * 计算时间偏移量
     */
    public static void calculateOffsetTime() {
        // 先取sp中数据
        offsetTime = spManager.getLong(KEY_OFFSET_TIME, 0);

        // 无网络退出
        if (!NetworkUtil.isNetAvailable()) {
            LogUtil.i(TAG, "calculateOffsetTime --> 网络不可用");
            return;
        }

        ThreadUtil.request(() -> {
            Map<String, TimeData> timeMap = new ConcurrentHashMap<>();

            // 发起请求，保存每个请求数据
            for (Map.Entry<String, Boolean> entry : webUrlMap.entrySet()) {
                TimeData data = getOffsetTime(entry.getKey(), entry.getValue());
                timeMap.put(entry.getKey(), data);
            }

            // 移除无效数据
            for (Map.Entry<String, TimeData> entry : timeMap.entrySet()) {
                String webUrl = entry.getKey();
                TimeData data = entry.getValue();
                if (data.webTime <= 0) {
                    timeMap.remove(webUrl);
                }
            }

            // 优先取高精度平均值
            Long averageOffsetTime = getAverageOffsetTime(timeMap, true);

            // 无高精度平均值时，取低精度平均值
            if (averageOffsetTime == null) {
                averageOffsetTime = getAverageOffsetTime(timeMap, false);
            }

            // 处理时间差值
            if (averageOffsetTime != null) {
                // 时间差在100毫秒内，则说明本地时间是精准的，以本地为准
                if (Math.abs(averageOffsetTime) < 100) {
                    offsetTime = 0;
                } else {
                    offsetTime = averageOffsetTime;
                }

                // 存到sp
                spManager.putLong(KEY_OFFSET_TIME, offsetTime);
                LogUtil.i(TAG, "calculateOffsetTime --> offsetTime:" + offsetTime);
            }
        });
    }

    /**
     * 获取平均时间偏移量
     *
     * @param timeMap
     * @param isHighPrecision
     * @return
     */
    private static Long getAverageOffsetTime(Map<String, TimeData> timeMap, boolean isHighPrecision) {
        int count = 0;
        long totalOffsetTime = 0;
        for (Map.Entry<String, TimeData> entry : timeMap.entrySet()) {
            TimeData data = entry.getValue();
            if (data.isHighPrecision == isHighPrecision) {
                count++;
                totalOffsetTime += data.offsetTime;
            }
        }

        return count > 0 ? totalOffsetTime / count : null;
    }

    /**
     * 获取本地与网络时间偏移量
     *
     * @param webUrl
     * @param isHighPrecision
     * @return
     */
    private static TimeData getOffsetTime(String webUrl, boolean isHighPrecision) {
        TimeData data = new TimeData();
        data.webUrl = webUrl;
        data.isHighPrecision = isHighPrecision;
        data.starTime = System.currentTimeMillis();
        data.webTime = getWebsiteDatetime(webUrl, isHighPrecision);
        data.endTime = System.currentTimeMillis();
        data.ioTime = (data.endTime - data.starTime) / 2;
        data.offsetTime = data.webTime - data.starTime - data.ioTime;

//        LogUtil.i(TAG, " webTime:" + data.webTime + " localTime:" + data.starTime + " offsetTime:" + data.offsetTime + " ioTime:" + data.ioTime + " webUrl:" + webUrl);
        return data;
    }

    /**
     * 获取网络时间（精度：秒）
     *
     * @param webUrl
     * @param isHighPrecision
     * @return
     */
    private static long getWebsiteDatetime(String webUrl, boolean isHighPrecision) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(webUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();

            if (isHighPrecision) {
                Long resultData = getHighPrecisionTime(connection);
                if (resultData != null) {
                    return resultData;
                }
            }

            return connection.getDate();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return 0;
    }

    /**
     * 获取高精度网络时间
     *
     * @param connection
     * @return
     */
    private static Long getHighPrecisionTime(HttpURLConnection connection) {
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                String result = getContent(connection.getInputStream());
                ResultData<WebData> resultData = GsonUtil.toBean(result, new TypeToken<ResultData<WebData>>() {}.getType());
                if (resultData != null && resultData.data != null) {
                    return resultData.data.time;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return 字符串
     */
    private static String getContent(InputStream is) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            return new String(out.toByteArray());
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.close();
                is.close();
            } catch (IOException ignored) {
            }
        }
    }


    /**
     * 时间数据类
     */
    private static class TimeData {
        public String webUrl;
        public long starTime;
        public long webTime;
        public long endTime;
        public long ioTime;
        public long offsetTime;
        public boolean isHighPrecision;
    }

    /**
     * 网络数据model
     */
    private static class WebData {
        @SerializedName("t")
        public long time;
    }
}
