package com.common.utils.fetcher;

/**
 * 截取错误描述类。
 */
public class FetcherError {

    private String url;

    private int code;

    private String description;

    public FetcherError(String url, int code, String description) {
        this.url = url;
        this.code = code;
        this.description = description;
    }

    public String getURL() {
        return this.url;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

}
