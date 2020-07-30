package com.hcmus.easywork.models.user;

import com.google.gson.annotations.SerializedName;

public class IntegratedUser implements IIntegratedUser {
    // region Attributes
    @SerializedName("mail")
    private String mEmail;
    @SerializedName("displayedName")
    private String mDisplayName;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("googleID")
    private String mGoogleId;
    @SerializedName("facebookID")
    private String mFacebookId;
    @SerializedName("avatarURL")
    private String mAvatarUrl;
    // endregion

    public IntegratedUser() {

    }

    // region Getters
    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public String getGoogleId() {
        return mGoogleId;
    }

    @Override
    public String getFacebookId() {
        return mFacebookId;
    }

    @Override
    public String getAvatarUrl() {
        return mAvatarUrl;
    }
    // endregion

    // region Setters
    @Override
    public void setEmail(String email) {
        this.mEmail = email;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    @Override
    public void setPassword(String password) {
        this.mPassword = password;
    }

    @Override
    public void setGoogleId(String googleId) {
        this.mGoogleId = googleId;
    }

    @Override
    public void setFacebookId(String facebookId) {
        this.mFacebookId = facebookId;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        this.mAvatarUrl = avatarUrl;
    }
    // endregion
}
