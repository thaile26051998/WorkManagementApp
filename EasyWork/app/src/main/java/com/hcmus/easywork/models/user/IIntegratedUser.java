package com.hcmus.easywork.models.user;

public interface IIntegratedUser {
    // region Getters
    String getEmail();

    String getDisplayName();

    String getPassword();

    String getGoogleId();

    String getFacebookId();

    String getAvatarUrl();
    // endregion

    // region Setters
    void setEmail(String email);

    void setDisplayName(String displayName);

    void setPassword(String password);

    void setGoogleId(String googleId);

    void setFacebookId(String facebookId);

    void setAvatarUrl(String avatarUrl);
    // endregion
}
