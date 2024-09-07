package com.common.utils.fetcher;

import com.common.utils.thread.ThreadUtil;
import com.common.utils.thread.UIHandler;
import com.common.utils.log.LogUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * 用于截取指定 URL 内容的辅助工具类。
 */
public class Fetcher {
    private static final Pattern PATTERN_CHARSET = Pattern.compile("charset=\\S*");
    private static final Pattern PATTERN_NODE_TEXT = Pattern.compile("[^>]+?(?=<)");
    private static final Pattern PATTERN_TITLE = Pattern.compile("<title[^>]*?>.*?</title>");

    private static final String[] IGNORE_LIST = new String[]{
            "data-pjax-transient"
    };

    private static Map<String, HttpURLConnection> requestMap = new ConcurrentHashMap<>();
    private static Map<String, List<FetchListener>> urlListenerMap = new ConcurrentHashMap<>();

    /**
     * 截取指定 URL 连接的页面信息。
     *
     * @param url
     * @param listener
     */
    public static void fetch(final String url, final FetchListener listener) {
        ThreadUtil.request(() -> fetchUrl(url, null, listener));
    }

    /**
     * 停止指定操作。
     */
    public synchronized static void stop(String url) {
        HttpURLConnection connection = requestMap.get(url);
        if (null != connection) {
            connection.disconnect();
            requestMap.remove(url);
            urlListenerMap.remove(url);
        }
    }

    /**
     * 停止全部操作。
     */
    public synchronized static void stopAll() {
        for (Map.Entry<String, HttpURLConnection> entry : requestMap.entrySet()) {
            String url = entry.getKey();
            requestMap.remove(url);
            urlListenerMap.remove(url);
            HttpURLConnection connection = entry.getValue();
            connection.disconnect();
        }
    }

    /**
     * 拉取url信息
     *
     * @param urlStr      url地址
     * @param location    重定向地址
     * @param listener    监听
     */
    private static void fetchUrl(String urlStr, String location, FetchListener listener) {
        // 判断地址重定向时，避免重复添加
        if (location == null) {
            synchronized (Fetcher.class) {
                // url相同时，避免产生多个请求
                List<FetchListener> listeners = urlListenerMap.get(urlStr);
                if (listeners != null) {
                    if (!listeners.contains(listener)) {
                        listeners.add(listener);
                    }
                    return;
                }

                listeners = new ArrayList<>();
                listeners.add(listener);
                urlListenerMap.put(urlStr, listeners);
            }
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 存在重定向地址时优先使用，url无协议头时添加http://
            URL url = new URL(location != null ? location : urlStr.contains("://") ? urlStr : "http://" + urlStr);
            connection = (HttpURLConnection) url.openConnection();
            requestMap.put(urlStr, connection);

            // 附加请求头
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            connection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Host", url.getHost());
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:54.0) Gecko/20100101 Firefox/54.0");

            // 超时设置
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            connection.connect();

            // 判断状态码
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                if (statusCode == 301 || statusCode == 302) {
                    connection.disconnect();
                    requestMap.remove(urlStr);

                    // 重定向跳转
                    location = connection.getHeaderField("Location");
                    fetchUrl(urlStr, location, listener);
                    return;
                }

                callOnFailed(urlStr, 5000, "Can not connect site : " + url.getHost());
                return;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName(preproccessCharset(connection))));

            // 读取内容
            LinkedList<String> lines = new LinkedList<String>();
            String line;
            String last = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                } else if (line.contains(IGNORE_LIST[0])) {
                    continue;
                }

                String[] array = line.split("><");
                if (array.length > 1) {
                    lines.add(array[0] + ">");
                    for (int i = 1, len = array.length - 1; i < len; ++i) {
                        lines.add("<" + array[i] + ">");
                    }
                    lines.add("<" + array[array.length - 1]);
                } else {
                    lines.add(line);
                }

                // 处理 Node Text 内换行的问题
                if (null != last) {
                    if (!last.endsWith(">") && !last.endsWith(";")) {
                        lines.removeLast();
                        lines.removeLast();
                        line = last + line;
                        lines.add(line);
                    }
                }

                last = lines.getLast();
            }

            reader.close();
            reader = null;

            // 提取有用的数据
            StringBuilder buf = new StringBuilder();
            String imgSrc = extractValidHTML(buf, lines);

            lines.clear();

            String htmlText = buf.toString();

            // 解析
            FetcherResult result = parse(url, htmlText);
            result.setFirstImageURL(url, imgSrc);

            // 回调
            callOnCompleted(urlStr, result);
        } catch (MalformedURLException e) {
            callOnFailed(urlStr, 1000, e.getMessage());
        } catch (IOException e) {
            callOnFailed(urlStr, 2000, e.getMessage());
        } catch (XPathExpressionException e) {
            callOnFailed(urlStr, 3000, e.getMessage());
        } catch (Exception e) {
            callOnFailed(urlStr, 4000, e.getMessage());
        } finally {
            if (null != connection) {
                connection.disconnect();
                requestMap.remove(urlStr);
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 回调成功
     *
     * @param url
     * @param result
     */
    private static void callOnCompleted(String url, FetcherResult result) {
        UIHandler.run(() -> {
            synchronized (Fetcher.class) {
                List<FetchListener> listeners = urlListenerMap.get(url);
                if (listeners != null) {
                    for (FetchListener listener : listeners) {
                        listener.onCompleted(result);
                    }

                    urlListenerMap.remove(url);
                }
            }
        });
    }

    /**
     * 回调失败
     *
     * @param url
     * @param code
     * @param description
     */
    private static void callOnFailed(String url, int code, String description) {
        UIHandler.run(() -> {
            synchronized (Fetcher.class) {
                List<FetchListener> listeners = urlListenerMap.get(url);
                if (listeners != null) {
                    for (FetchListener listener : listeners) {
                        listener.onFailed(new FetcherError(url, code, description));
                    }

                    urlListenerMap.remove(url);
                }
            }
        });
    }

    /**
     * 解析数据
     *
     * @param url
     * @param htmlText
     * @return
     * @throws XPathExpressionException
     */
    private static FetcherResult parse(URL url, String htmlText) throws XPathExpressionException {
        FetcherResult result = new FetcherResult(url.toString());

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        InputStream inputStream = null;

        // 提取标题
        try {
            inputStream = new ByteArrayInputStream(htmlText.getBytes(Charset.forName("UTF-8")));
            InputSource inputSource = new InputSource(inputStream);
            NodeList nodeList = (NodeList) xpath.evaluate("//title", inputSource, XPathConstants.NODESET);
            if (nodeList.getLength() > 0) {
                result.pageTitle = nodeList.item(0).getTextContent();
            }
        } catch (XPathExpressionException e) {
            // 非标准 XPath ，使用正则表达式
            result.pageTitle = matchTitle(htmlText);
            if (null == result.pageTitle) {
                LogUtil.w("Parse title error: " + e.getMessage());
                throw e;
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // Nothing
            }
        }

        // 提取描述
        try {
            inputStream = new ByteArrayInputStream(htmlText.getBytes(Charset.forName("UTF-8")));
            InputSource inputSource = new InputSource(inputStream);
            NodeList nodeList = (NodeList) xpath.evaluate("//meta[@name='description']", inputSource, XPathConstants.NODESET);
            if (nodeList.getLength() > 0) {
                result.pageDescription = ((Element) nodeList.item(0)).getAttribute("content");
            }
        } catch (XPathExpressionException e) {
            LogUtil.w("Parse description error: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Nothing
            }
        }

        if (null == result.pageDescription) {
            try {
                inputStream = new ByteArrayInputStream(htmlText.getBytes(Charset.forName("UTF-8")));
                InputSource inputSource = new InputSource(inputStream);
                NodeList nodeList = (NodeList) xpath.evaluate("//meta[@name='keywords']", inputSource, XPathConstants.NODESET);
                if (nodeList.getLength() > 0) {
                    result.pageDescription = ((Element) nodeList.item(0)).getAttribute("content");
                }
            } catch (XPathExpressionException e) {
                LogUtil.w("Parse keywords error: " + e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Nothing
                }
            }
        }

        // 组装 Icon 链接
        result.pageIconURL = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";

        return result;
    }

    /**
     * 提取有效数据
     *
     * @param buf
     * @param text
     * @return
     */
    private static String extractValidHTML(StringBuilder buf, List<String> text) {
        int head = 0;
        int script = 0;     // 跳过脚本的 Flag
        int style = 0;      // 跳过样式表
        int pickImg = 0;    // 提取首张图片用的控制参数
        String imgSrc = null;
        for (String line : text) {
            String lline = line.toLowerCase();

            if (pickImg > 0) {
                if (lline.contains("<img")) {
                    int start = line.indexOf("src=");
                    if (start < 0) {
                        continue;
                    }

                    start += 4;
                    byte[] src = line.getBytes();
                    byte[] bytes = new byte[src.length - start];
                    int index = 0;
                    for (int i = start; i < src.length; ++i) {
                        byte b = src[i];
                        if (b == '"') {
                            continue;
                        } else if (b == '>' || b == ' ') {
                            break;
                        }
                        bytes[index++] = b;
                    }
                    imgSrc = new String(bytes, 0, index);
                    break;
                }
            } else {
                if (lline.contains("<head>")) {
                    head = 1;
                } else if (lline.contains("</head>")) {
                    head = 2;
                } else if (lline.contains("<link")) {
                    continue;
                } else if (lline.indexOf("<meta") == 0) {
                    // 补 </meta>
                    buf.append(line);
                    if (!lline.endsWith("/>")) {
                        buf.append("</meta>\n");
                    } else {
                        buf.append("\n");
                    }
                    continue;
                } else if (lline.contains("<script")) {
                    script = 1;
                    if (lline.contains("</script>")) {
                        script = 2;
                    }
                } else if (lline.contains("</script>")) {
                    script = 2;
                } else if (lline.contains("<style")) {
                    style = 1;
                    if (lline.contains("</style>")) {
                        style = 2;
                    }
                } else if (lline.contains("</style>")) {
                    style = 2;
                }

                // 1.判断是否需要跳过脚本
                if (script == 1) {
                    // 跳过脚本内容
                    continue;
                } else if (script == 2) {
                    // 脚本结束
                    script = 0;
                    continue;
                }

                // 2.判断是否要跳过样式表
                if (style == 1) {
                    continue;
                } else if (style == 2) {
                    style = 0;
                    continue;
                }

                // 3.判断是否头结束
                if (head == 1) {
                    buf.append(line).append("\n");
                } else if (head == 2) {
                    buf.append(line).append("\n");
                    pickImg = 1;
                }
            }
        }

        return imgSrc;
    }

    /**
     * 预处理编码集。
     *
     * @param conn
     * @return
     */
    private static String preproccessCharset(HttpURLConnection conn) {
        String charset = "UTF-8";
        String contentType = conn.getContentType();
        if (null == contentType) {
            return charset;
        }

        Matcher matcher = PATTERN_CHARSET.matcher(conn.getContentType());
        if (matcher.find()) {
            charset = matcher.group().replace("charset=", "");
        }
        return charset;
    }

    /**
     * 使用正则表达式匹配标题。
     *
     * @param content
     * @return
     */
    private static String matchTitle(String content) {
        Matcher matcher = PATTERN_TITLE.matcher(content);
        if (matcher.find()) {
            String tag = matcher.group().trim();
            matcher = PATTERN_NODE_TEXT.matcher(tag);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        return null;
    }
}
