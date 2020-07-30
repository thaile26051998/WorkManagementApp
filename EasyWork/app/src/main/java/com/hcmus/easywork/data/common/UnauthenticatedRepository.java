package com.hcmus.easywork.data.common;

import com.hcmus.easywork.data.util.RetrofitClient;

import retrofit2.Retrofit;

public abstract class UnauthenticatedRepository<T extends UnauthenticatedApiService> extends AbstractRepository<T> {
    private static final Retrofit RETROFIT = RetrofitClient.getInstance().getUnauthenticatedRetrofit();

    @Override
    public Retrofit getRetrofit() {
        return RETROFIT;
    }
}
