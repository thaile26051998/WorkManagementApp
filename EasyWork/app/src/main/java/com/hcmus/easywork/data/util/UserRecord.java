package com.hcmus.easywork.data.util;

import android.graphics.Bitmap;

public class UserRecord {
    private String mName;
    private Bitmap mAvatar;
    private String mEmail;

    public UserRecord(String name, Bitmap avatar, String email) {
        this.mName = name;
        this.mAvatar = avatar;
        this.mEmail = email;
    }

    public String getName() {
        return this.mName;
    }

    public Bitmap getAvatar() {
        return this.mAvatar;
    }

    public String getEmail() {
        return this.mEmail;
    }
}
