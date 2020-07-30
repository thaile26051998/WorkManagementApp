package com.hcmus.easywork.data.facebook;

import android.net.Uri;

public class FacebookUser {
    private Uri mPicture;
    private String mName;
    private String mId;
    private String mEmail;
    private String mPermissions;

    public FacebookUser(Uri picture, String name, String id, String email, String permission) {
        this.mPicture = picture;
        this.mName = name;
        this.mId = id;
        this.mEmail = email;
        this.mPermissions = permission;
    }

    public Uri getPicture() {
        return mPicture;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return this.mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPermissions() {
        return mPermissions;
    }
}
