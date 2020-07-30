package com.hcmus.easywork.twilio.shared;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TwilioRetrofit {
    private static final String BASE_URL = "https://easywork-twilio.herokuapp.com/";
    private static TwilioRetrofit mInstance;
    private final Retrofit retrofit;

    private TwilioRetrofit() {
        final long timeout = 5;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public static synchronized TwilioRetrofit getInstance() {
        if (mInstance == null) {
            mInstance = new TwilioRetrofit();
        }
        return mInstance;
    }

    public Retrofit getRetrofit() {
        return this.retrofit;
    }
}
