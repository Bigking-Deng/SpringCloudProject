package com.bigking.springcloud.utils;


import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class OkHttpUtil2 {

    private static final int CONNECT_TIMEOUT_IN_SECONDS = 10;
    private static final int READ_TIMEOUT_IN_SECONDS = 60;
    private static final int WRITE_TIMEOUT_IN_SECONDS = 60;

    private static volatile OkHttpClient clientInstance = null;
    private static volatile OkHttpClient clientInstanceWithNoRedirects = null;

    public static OkHttpClient getClientInstance() {
        if (clientInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (clientInstance == null) {
                    initClientInstance();
                }
            }
        }
        return clientInstance;
    }

    public static OkHttpClient getClientInstanceWithNoRedirects() {
        if (clientInstanceWithNoRedirects == null) {
            synchronized (OkHttpUtil.class) {
                if (clientInstanceWithNoRedirects == null) {
                    initClientInstanceWithNoRedirects();
                }
            }
        }
        return clientInstanceWithNoRedirects;
    }


    private static void initClientInstance() {
        if (clientInstance == null) {
            clientInstance = new OkHttpClient().newBuilder()
//                    .addInterceptor(new LoggingInterceptor())
                    .followRedirects(true)
                    .connectTimeout(CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .build();
        }
    }

    private static void initClientInstanceWithNoRedirects() {
        if (clientInstanceWithNoRedirects == null) {
            clientInstanceWithNoRedirects = new OkHttpClient().newBuilder()
//                    .addInterceptor(new LoggingInterceptor())
                    .followRedirects(false)
                    .connectTimeout(CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .build();
        }
    }

    private static Response get(String url, Headers headers, OkHttpClient client) {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("Failed to get, url is {}, headers is {}, client is {}, error: ", url, headers, client, e);
        }
        return response;
    }

    public static Response get(String url, Headers headers) {
        return get(url, headers, getClientInstance());
    }

    public static Response getWithNoRedirects(String url, Headers headers) {
        return get(url, headers, getClientInstanceWithNoRedirects());
    }

    private static Response post(String url, RequestBody body, Headers headers, OkHttpClient client) {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("Failed to post, url is {}, body is {}, headers is {}, client is {}, error: ", url, bodyToString(body), headers, client, e);
        }
        return response;
    }

    public static Response post(String url, RequestBody body, Headers headers) {
        return post(url, body, headers, getClientInstance());
    }

    public static Response postWithNoRedirects(String url, RequestBody body, Headers headers) {
        return post(url, body, headers, getClientInstanceWithNoRedirects());
    }

    public static String bodyToString(final RequestBody request) {
        try {
            if (null == request) {
                return null;
            }
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}