package com.hcmus.easywork.models;

import com.google.gson.annotations.SerializedName;

public class Avatar {
    @SerializedName("type")
    private String type;
    @SerializedName("data")
    private byte[] data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
