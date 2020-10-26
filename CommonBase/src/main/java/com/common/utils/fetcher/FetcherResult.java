package com.common.utils.fetcher;

import java.net.URL;

/**
 * 截取 URL 页面的结果信息封装。
 */
public class FetcherResult {

    /**
     * 页面的 URL 字符串。
     */
    public String pageURL;

    /**
     * 页面的标题。如果截取失败为 null 值。
     */
    public String pageTitle;

    /**
     * 页面的描述信息。如果截取失败为 null 值。
     */
    public String pageDescription;

    /**
     * 页面图标的 URL 。如果截取失败为 null 值。
     */
    public String pageIconURL;

    /**
     * 页面的首图的 URL 。如果截取失败为 null 值。
     */
    public String pageFirstImageURL;


    /**
     * 使用 URL 初始化。
     */
    public FetcherResult(String pageURL) {
        this.pageURL = pageURL;
    }

    public void setFirstImageURL(URL url, String src) {
        if (!src.startsWith("http")) {
            if (src.startsWith("//")) {
                this.pageFirstImageURL = url.getProtocol() + ":" + src;
            }
            else if (src.startsWith("/")) {
                this.pageFirstImageURL = url.getProtocol() + "://" + url.getHost() + src;
            }
            else {
                this.pageFirstImageURL = url.getProtocol() + "://" + url.getHost() + "/" + src;
            }
        }
        else {
            this.pageFirstImageURL = src;
        }
    }
}
