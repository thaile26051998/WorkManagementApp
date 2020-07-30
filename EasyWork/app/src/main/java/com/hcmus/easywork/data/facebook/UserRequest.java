package com.hcmus.easywork.data.facebook;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

public class UserRequest {
    private static final String ME_ENDPOINT = "/me";

    public static void makeRequest(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "picture,name,id,email,permissions");
        GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                ME_ENDPOINT, params, HttpMethod.GET, callback);
        graphRequest.executeAsync();
    }
}
