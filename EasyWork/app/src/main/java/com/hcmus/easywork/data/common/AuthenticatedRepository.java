package com.hcmus.easywork.data.common;

import com.hcmus.easywork.data.util.RetrofitClient;

import retrofit2.Retrofit;

public abstract class AuthenticatedRepository<T extends AuthenticatedApiService> extends AbstractRepository<T> {
    private static final Retrofit RETROFIT = RetrofitClient.getInstance().getAuthenticatedRetrofit();

    @Override
    public Retrofit getRetrofit() {
        return RETROFIT;
    }
}
