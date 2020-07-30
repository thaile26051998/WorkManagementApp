package com.hcmus.easywork.models.file;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;

public class EwFile implements IComparableModel<EwFile> {
    @SerializedName("fileID")
    public int mFileId;
    @SerializedName("type")
    public String mType;
    @SerializedName("name")
    public String mName;
    @SerializedName("data")
    public Data mData;
    @SerializedName("size")
    public String mSize;
    @SerializedName("messID")
    public int mMessageId;
    @SerializedName("groupID")
    public int mGroupId;

    @Override
    public boolean isTheSame(@NonNull EwFile item) {
        return (this.mFileId == item.mFileId);
    }

    public static class Data {
        @SerializedName("type")
        public String type;
        @SerializedName("data")
        public byte[] data;

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


    public int getFileId() {
        return mFileId;
    }

    public void setFileId(int mFileId) {
        this.mFileId = mFileId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data mData) {
        this.mData = mData;
    }

    public String getmSize() {
        return mSize;
    }

    public void setmSize(String mSize) {
        this.mSize = mSize;
    }

    public int getmMessageId() {
        return mMessageId;
    }

    public void setmMessageId(int mMessageId) {
        this.mMessageId = mMessageId;
    }

    public int getmGroupId() {
        return mGroupId;
    }

    public void setmGroupId(int mGroupId) {
        this.mGroupId = mGroupId;
    }
}
