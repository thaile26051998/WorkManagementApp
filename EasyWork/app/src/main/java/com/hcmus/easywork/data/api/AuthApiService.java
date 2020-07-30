package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.LoginResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.response.RegisterResponse;
import com.hcmus.easywork.data.response.VerifyResponse;
import com.hcmus.easywork.data.common.UnauthenticatedApiService;
import com.hcmus.easywork.models.user.IntegratedUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * All APIs related to user authentication (without access token): login, register & verify, resetPassword
 */
public interface AuthApiService extends UnauthenticatedApiService {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("mail") String mail,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<RegisterResponse> register(@Field("mail") String mail,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("verify")
    Call<VerifyResponse> verify(@Field("mail") String mail,
                                @Field("password") String password,
                                @Field("code") Number code);

    @FormUrlEncoded
    @POST("auth/password")
    Call<MessageResponse> resetPassword(@Field("mail") String mail);

    @POST("login/google")
    Call<LoginResponse> useGoogle(@Body IntegratedUser user);

    @POST("login/facebook")
    Call<LoginResponse> useFacebook(@Body IntegratedUser user);
}
