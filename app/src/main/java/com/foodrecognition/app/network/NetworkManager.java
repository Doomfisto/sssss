package com.foodrecognition.app.network;

import com.foodrecognition.app.network.api.CalorieMamaApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络管理类
 * 负责创建和管理网络请求相关的实例
 */
public class NetworkManager {
    private static NetworkManager instance;
    private final CalorieMamaApi api;

    private NetworkManager() {
        // 创建OkHttpClient，配置超时和日志
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        // 创建Retrofit实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CalorieMamaApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 创建API接口实现
        api = retrofit.create(CalorieMamaApi.class);
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public CalorieMamaApi getApi() {
        return api;
    }
} 