package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.viewmodels.auth.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApiService extends AuthenticatedApiService {
    @FormUrlEncoded
    @PUT("auth/{id}")
    Call<MessageResponse> changePassword(@Path("id") int id,
                                         @Field("mail") String mail,
                                         @Field("password") String oldPassword,
                                         @Field("newPassword") String newPassword);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);

    @GET("users/")
    Call<List<User>> getAllUser();

    @PUT("users/{id}")
    Call<MessageResponse> editProfile(@Path("id") int id, @Body User user);

    @Multipart
    @PUT("users/{id}")
    Call<MessageResponse> uploadAvatar(@Path("id") int userId,
                                       @Part MultipartBody.Part file);
}
