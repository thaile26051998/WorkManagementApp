package com.hcmus.easywork.models.member;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.Avatar;

public class Member {
    @SerializedName("userID")
    protected int mUserId;
    @SerializedName("projectID")
    protected int mProjectId;
    @SerializedName("user")
    protected User mUser;

    public Member() {

    }

    public User getUser() {
        return this.mUser;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public int getProjectId() {
        return this.mProjectId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public void setProjectId(int projectId) {
        this.mProjectId = projectId;
    }

    public static class User {
        @SerializedName("avatar")
        private Avatar mAvatar;
        @SerializedName("avatarType")
        private String mAvatarType;
        @SerializedName("displayedName")
        private String mDisplayName;
        @SerializedName("mail")
        private String mMail;

        public Avatar getAvatar() {
            return mAvatar;
        }

        public String getAvatarType() {
            return mAvatarType;
        }

        public String getDisplayName() {
            return mDisplayName;
        }

        public String getMail() {
            return mMail;
        }
    }
}
