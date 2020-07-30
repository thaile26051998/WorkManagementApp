package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.models.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NewsApiService extends AuthenticatedApiService {
    @GET("news/onlyUser/{uid}")
    Call<List<News>> getNews(@Path("uid") int userId);

    @PUT("news/watched/{id}")
    Call<MessageResponse> markAsRead(@Path("id") int newsId);

    @PUT("news/watchedAll/{id}")
    Call<MessageResponse> markAllAsRead(@Path("id") int userId);

    @DELETE("news/{id}")
    Call<MessageResponse> deleteNews(@Path("id") int newsId);

    @DELETE("news/deleteAll/{uid}")
    Call<MessageResponse> deleteAllNews(@Path("uid") int userId);

    @POST("news")
    Call<News> createNews(@Body News news);

}
