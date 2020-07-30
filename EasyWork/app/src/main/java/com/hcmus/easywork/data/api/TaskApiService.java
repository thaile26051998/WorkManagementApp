package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.models.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaskApiService extends AuthenticatedApiService {
    @DELETE("tasks/{id}")
    Call<MessageResponse> deleteTask(@Path("id") int id);

    @GET("tasks/mytasks/{uid}")
    Call<List<Task>> getMyTasks(@Path("uid") int userId);

    @GET("tasks/tasksOfAllProject/{uid}")
    Call<List<Task>> getAllTasksOfAllProjects(@Path("uid") int userId);

    @GET("tasks/{id}")
    Call<Task> getSingleTask(@Path("id") int taskId);

    @PUT("tasks/{id}")
    Call<MessageResponse> updateTask(@Path("id") int taskId, @Body Task task);
}

