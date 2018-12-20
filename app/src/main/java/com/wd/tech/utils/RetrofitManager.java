package com.wd.tech.utils;

import com.wd.tech.App;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mumu on 2018/7/9.
 */

public class RetrofitManager {
    private static String BASE_URL = "https://172.17.8.100/techApi/";
    private Retrofit mRetrofit;

    private static class SingleHolder {
        private static final RetrofitManager _INSTANT = new RetrofitManager(BASE_URL);
    }

    public static RetrofitManager getDefault() {
        return SingleHolder._INSTANT;
    }

    private RetrofitManager(String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildOkhttpClinet())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 让ok支持https
     *
     * @return
     */
    private OkHttpClient buildOkhttpClinet() {
        HttpsUtils htppsUtils = new HttpsUtils(App.getAppContext());
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS);
        //支持https证书和域名认证
        OkHttpClient.Builder newBuilder = htppsUtils.setCertificateForOkhttp(builder);
        return newBuilder.build();
    }

    public <T> T create(Class<T> Clazz) {
        return mRetrofit.create(Clazz);
    }
}
