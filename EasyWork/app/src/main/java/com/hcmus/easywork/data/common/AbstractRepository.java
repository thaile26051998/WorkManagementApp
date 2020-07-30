package com.hcmus.easywork.data.common;

import retrofit2.Retrofit;

/**
 * Base repository class for all repositories.
 *
 * @param <T> Predefined: <code>AuthenticatedApiService</code> or <code>UnauthenticatedApiService</code>
 */
public abstract class AbstractRepository<T extends BaseApiService> {
    // API service
    private T mApi;

    /**
     * Get API service instance.
     *
     * @return API service instance of provided interface.
     */
    protected synchronized T getApi() {
        if (this.mApi == null)
            this.mApi = getRetrofit().create(getApiClass());
        return this.mApi;
    }

    /**
     * Get Retrofit instance for authenticated or unauthenticated API call.
     *
     * @return <code>Retrofit</code> instance from <code>RetrofitClient</code>.
     */
    protected abstract Retrofit getRetrofit();

    /**
     * Get class of provided API interface.
     *
     * @return <code>Class</code> instance. For example:
     * <p><code>return ExampleApiService.class;</code></p>
     */
    protected abstract Class<T> getApiClass();
}
