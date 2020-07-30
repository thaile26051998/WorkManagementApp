package com.hcmus.easywork.data.old;

import androidx.annotation.NonNull;

import com.hcmus.easywork.data.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Deprecated
public abstract class BaseRepository<T> {
    protected Call<T> call;
    protected OnResponseListener<T> onResponseListener;

    protected TokenRequiredApiService getTokenRequiredApiService() {
        return RetrofitClient.getInstance().getTokenRequiredApi();
    }

    public void setOnResponseListener(@NonNull OnResponseListener<T> listener) {
        this.onResponseListener = listener;
    }

    public void enqueue() {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (onResponseListener != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        onResponseListener.onResponse(response.body());
                    } else {
                        onResponseListener.onFailure(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                if (onResponseListener != null) {
                    onResponseListener.onFailure(t.getLocalizedMessage());
                }
            }
        });
    }

    public interface OnResponseListener<T> {
        void onResponse(T response);

        void onFailure(String message);
    }
}
