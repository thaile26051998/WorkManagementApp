package com.hcmus.easywork.data.common;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The <code>ResponseManager</code> class accepts Retrofit invocation method, then enqueues and listens to response.
 *
 * @param <T> Type of expected response object
 */
public class ResponseManager<T> {
    private Call<T> call;

    /**
     * Default constructor marked as private to prevent empty initialization.
     */
    private ResponseManager() {
        // Empty declaration
    }

    /**
     * Constructor that accepts a Retrofit invocation method.
     *
     * @param call Expected response type call
     */
    public ResponseManager(Call<T> call) {
        this();
        this.call = call;
    }

    /**
     * Asynchronously send the request, notify callback and observe response.
     *
     * @param listener <code>OnResponseListener</code> instance of expected response type
     */
    public void enqueue(OnResponseListener<T> listener) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onResponse(response.body());
                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                listener.onFailure(t.getLocalizedMessage());
            }
        });
    }

    public interface OnResponseListener<T> {
        void onResponse(T response);

        void onFailure(String message);
    }
}
