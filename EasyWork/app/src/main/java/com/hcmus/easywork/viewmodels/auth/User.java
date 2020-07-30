package com.hcmus.easywork.viewmodels.auth;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.Avatar;

import java.util.Date;

import okhttp3.MultipartBody;

public class User implements IUser {
    // region Attributes
    @SerializedName("userID")
    private int mUserId = 0;
    @SerializedName("mail")
    private String mMail = null;
    @SerializedName("password")
    private String mPassword = null;
    @SerializedName("displayedName")
    private String mDisplayName = null;
    @SerializedName("file")
    private MultipartBody.Part file = null;
    @SerializedName("avatar")
    private Avatar avatar = null;
    @SerializedName("avatarType")
    private String mAvatarType = null;
    @SerializedName("phone")
    private String mPhone = null;
    @SerializedName("birthday")
    private Date mDob = null;
    @SerializedName("address")
    private String mAddress = null;
    @SerializedName("online")
    private boolean mIsOnline = false;
    @SerializedName("googleID")
    private String mGoogleId = null;
    @SerializedName("facebookID")
    private String mFacebookId = null;
    // endregion

    public User() {

    }

    // region Setters
    @Override
    public int getUserId() {
        return mUserId;
    }

    @Override
    public String getMail() {
        return mMail;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public MultipartBody.Part getFile() {
        return null;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getAvatarType() {
        return mAvatarType;
    }

    @Override
    public String getPhone() {
        return mPhone;
    }

    @Override
    public Date getDateOfBirth() {
        return mDob;
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    @Override
    public boolean isOnline() {
        return mIsOnline;
    }

    @Override
    public String getGoogleId() {
        return mGoogleId;
    }

    @Override
    public String getFacebookId() {
        return mFacebookId;
    }
    // endregion

    // region Setters
    @Override
    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    @Override
    public void setMail(String mail) {
        this.mMail = mail;
    }

    @Override
    public void setPassword(String password) {
        this.mPassword = password;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    @Override
    public void setFile(MultipartBody.Part file) {
        this.file = file;
    }

    @Override
    public void setAvatarType(String avatarType) {
        this.mAvatarType = avatarType;
    }

    @Override
    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        this.mDob = dateOfBirth;
    }

    @Override
    public void setAddress(String address) {
        this.mAddress = address;
    }

    @Override
    public void setOnline(boolean online) {
        this.mIsOnline = online;
    }

    @Override
    public void setGoogleId(String googleId) {
        this.mGoogleId = googleId;
    }

    @Override
    public void setFacebookId(String facebookId) {
        this.mFacebookId = facebookId;
    }
    // endregion
}
