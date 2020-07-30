package com.hcmus.easywork.models.chat.group;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;

public class GroupMemberAPI implements IGroupMember, IComparableModel<GroupMemberAPI> {
    // region Attributes
    @SerializedName("groupID")
    private int mGroupId;
    @SerializedName("userID")
    private int mUserId;
    @SerializedName("projectID")
    private int mProjectId;
    @SerializedName("creatorID")
    private int mCreatorId;
    @SerializedName("auth")
    private boolean mIsCreator;
    // endregion

    @Override
    public boolean isTheSame(@NonNull GroupMemberAPI item) {
        return (this.getGroupId() == item.getGroupId()) &&
                (this.getProjectId() == item.getProjectId());
    }

    // region Getters
    @Override
    public int getGroupId() {
        return mGroupId;
    }

    @Override
    public int getUserId() {
        return mUserId;
    }

    @Override
    public int getProjectId() {
        return mProjectId;
    }

    @Override
    public int getCreatorId() {
        return mCreatorId;
    }

    @Override
    public boolean isCreator() {
        return mIsCreator;
    }
    // endregion

    // region Setters
    @Override
    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

    @Override
    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    @Override
    public void setProjectId(int projectId) {
        this.mProjectId = projectId;
    }

    @Override
    public void setCreatorId(int creatorId) {
        this.mCreatorId = creatorId;
    }

    @Override
    public void setCreator(boolean isCreator) {
        this.mIsCreator = isCreator;
    }
    // endregion
}
