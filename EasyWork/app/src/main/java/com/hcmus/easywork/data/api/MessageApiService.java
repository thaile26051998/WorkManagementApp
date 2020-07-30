package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.data.response.LikeResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.file.EwFile;

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
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MessageApiService extends AuthenticatedApiService {
    @POST("messages/")
    Call<Message> sendMessage(@Body Message message);

    @DELETE("messages/{id}")
    Call<MessageResponse> deleteMessage(@Path("id") int id);

    @Multipart
    @POST("files")
    Call<MessageResponse> sendFile(@Part("messID") int messageId,
                                   @Part MultipartBody.Part file);

    @GET("messages/{id}/files")
    Call<List<EwFile>> getFile(@Path("id") int messageId);

    @FormUrlEncoded
    @POST("emotions")
    Call<LikeResponse> likeMessage(@Field("userID") int userId,
                                   @Field("messID") int messId,
                                   @Field("type") String type);

    @DELETE("messages/{mid}/emotions/{uid}")
    Call<MessageResponse> unlikeMessage(@Path("uid") int userId, @Path("mid") int messId);
}
