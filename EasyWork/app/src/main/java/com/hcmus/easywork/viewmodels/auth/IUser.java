package com.hcmus.easywork.viewmodels.auth;

import com.hcmus.easywork.models.Avatar;

import java.util.Date;

import okhttp3.MultipartBody;

public interface IUser {
    // region Getters
    int getUserId();

    String getMail();

    String getPassword();

    String getDisplayName();

    Avatar getAvatar();

    MultipartBody.Part getFile();

    String getAvatarType();

    String getPhone();

    Date getDateOfBirth();

    String getAddress();

    boolean isOnline();

    String getGoogleId();

    String getFacebookId();
    // endregion

    // region Setters
    void setUserId(int userId);

    void setMail(String mail);

    void setPassword(String password);

    void setDisplayName(String displayName);

    void setFile( MultipartBody.Part file);

    void setAvatar(Avatar avatar);

    void setAvatarType(String avatarType);

    void setPhone(String phone);

    void setDateOfBirth(Date dateOfBirth);

    void setAddress(String address);

    void setOnline(boolean online);

    void setGoogleId(String googleId);

    void setFacebookId(String facebookId);
    // endregion
}
