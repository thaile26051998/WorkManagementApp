package com.hcmus.easywork.data.response;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.viewmodels.auth.User;

public class LoginResponse {
    @SerializedName("accessToken")
    private String accessToken = null;
    @SerializedName("user")
    private User user = null;
    @SerializedName("message")
    private String message = null;

    public LoginResponse() {

    }

    public String getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
