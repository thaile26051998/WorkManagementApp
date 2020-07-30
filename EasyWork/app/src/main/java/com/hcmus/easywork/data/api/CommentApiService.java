package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.models.Comment;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CommentApiService extends AuthenticatedApiService {
    @GET("comments/project/{pid}")
    Call<List<Comment>> getProjectComment(@Path("pid") int projectId);

    @GET("comments/task/{tid}")
    Call<List<Comment>> getTaskComment(@Path("tid") int taskId);

    @GET("comments/download/{id}")
    Call<MessageResponse> downloadFile(@Path("id") int commentId);

    @DELETE("comments/{id}")
    Call<MessageResponse> deleteComment(@Path("id") int commentId);

    @POST("comments/")
    Call<Comment> createComment(@Body Comment comment);

    @Multipart
    @POST("comments/")
    Call<Comment> uploadFile(@Part("projectID") int projectId,
                             @Part("userID") int userId,
                             @Part("cmtType") int cmtType,
                             @Part MultipartBody.Part file);

    @Multipart
    @POST("comments/")
    Call<Comment> uploadTaskFile(@Part("taskID") int taskId,
                             @Part("userID") int userId,
                             @Part("cmtType") int cmtType,
                             @Part MultipartBody.Part file);

    @Multipart
    @POST("files")
    Call<MessageResponse> sendFile(@Part("cmtID") int messageId,
                                   @Part MultipartBody.Part file);

    @FormUrlEncoded
    @PUT("comments/{id}")
    Call<MessageResponse> editComment(@Path("id") int commentId, @Field("contentVi") String content);
}
