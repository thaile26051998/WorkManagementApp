package com.hcmus.easywork.data.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcmus.easywork.data.old.TokenRequiredApiService;
import com.hcmus.easywork.utils.SharedPreferencesManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://easywork-app.herokuapp.com/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit, tokenRequiredRetrofit;

    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();

                            Request.Builder requestBuilder = original.newBuilder()
                                    .method(original.method(), original.body());

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                ).build();

        Gson gson = new GsonBuilder()
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setDateFormat("yyyy-MM-dd HH:mm:ss") // WARNING: wrong datetime format
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        long timeout = 5;
        tokenRequiredRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient.Builder()
                        .connectTimeout(timeout, TimeUnit.SECONDS)
                        .writeTimeout(timeout, TimeUnit.SECONDS)
                        .readTimeout(timeout, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .method(original.method(), original.body());
                            final String accessToken = SharedPreferencesManager.getAccessToken();
                            if (accessToken != null)
                                requestBuilder.addHeader("access-token", accessToken);
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }).build())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    @Deprecated
    public TokenRequiredApiService getTokenRequiredApi() {
        return tokenRequiredRetrofit.create(TokenRequiredApiService.class);
    }

    public Retrofit getAuthenticatedRetrofit() {
        return this.tokenRequiredRetrofit;
    }

    public Retrofit getUnauthenticatedRetrofit() {
        return this.retrofit;
    }
}
