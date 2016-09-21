package com.zj.mpocket;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zj.mpocket.utils.LogUtil;

import java.util.Locale;

public class ApiHttpClient {

    //测试环境
    //public final static String HOST = "http://112.74.126.9:8080";
    //private static String API_URL = "http://112.74.126.9:8080/%s";

    //生产环境
    public final static String HOST = "http://www.koudailingqian.com";
    private static String API_URL = "http://www.koudailingqian.com/%s";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static AsyncHttpClient client;

    public ApiHttpClient() {
    }

    public static com.loopj.android.http.AsyncHttpClient getHttpClient() {
        return client;
    }

    public static void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("GET ").append(partUrl).toString());
    }

    public static void get(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("GET ").append(partUrl).append("&").append(params).toString());
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        return String.format(API_URL, partUrl);
    }

    public static String getApiUrl() {
        return API_URL;
    }

    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
        log(new StringBuilder("GET ").append(url).toString());
    }

    public static void log(String log) {
        LogUtil.log(log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("POST ").append(partUrl).toString());
    }

    public static void post(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder().append(getAbsoluteApiUrl(partUrl)).append("?").append(params).toString());
    }

    public static void postDirect(String url, RequestParams params,
                                  AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
        log(new StringBuilder("POST ").append(url).append("&").append(params).toString());
    }

    public static void setApiUrl(String apiUrl) {
        API_URL = apiUrl;
    }

    public static void setHttpClient(AsyncHttpClient c) {
        client = c;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        //client.addHeader("Host", HOST);
        client.addHeader("Connection", "Keep-Alive");
    }


}
