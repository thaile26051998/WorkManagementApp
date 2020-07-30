package com.hcmus.easywork.data.old;

import com.hcmus.easywork.models.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

@Deprecated
public interface TokenRequiredApiService {

    @POST("tasks")
    Call<Task> createTask(@Body Task task);
}
