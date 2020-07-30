package com.hcmus.easywork.twilio.shared;

import com.hcmus.easywork.data.common.ResponseManager;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class TwilioImpl {
    private final TwilioApiService service;

    public TwilioImpl() {
        service = TwilioRetrofit.getInstance().getRetrofit().create(TwilioApiService.class);
    }

    public ResponseManager<String> getAccessToken(String identity) {
        return new ResponseManager<>(service.getAccessToken(identity));
    }

    public ResponseManager<String> getVideoAccessToken(String identity, String room) {
        return new ResponseManager<>(service.getVideoAccessToken(identity, room));
    }

    public interface TwilioApiService {
        @GET("accessToken/")
        Call<String> getAccessToken(@Query("identity") String identity);

        @GET("video/")
        Call<String> getVideoAccessToken(@Query("identity") String identity,
                                         @Query("room") String room);
    }
}
