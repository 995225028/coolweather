package com.example.jinkai.coolweather.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by jinkai on 2017/5/15.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
