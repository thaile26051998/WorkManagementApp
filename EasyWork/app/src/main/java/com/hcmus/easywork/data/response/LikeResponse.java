package com.hcmus.easywork.data.response;

import com.google.gson.annotations.SerializedName;

public class LikeResponse {
    @SerializedName("userID")
    public int userId;
    @SerializedName("messID")
    public int messId;
    @SerializedName("type")
    public String type = "like";
}
