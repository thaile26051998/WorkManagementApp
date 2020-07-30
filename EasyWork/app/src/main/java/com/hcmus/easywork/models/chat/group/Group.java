package com.hcmus.easywork.models.chat.group;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;

import java.util.ArrayList;
import java.util.List;

public class Group implements IGroup, IComparableModel<Group> {
    // region Attributes
    @SerializedName("groupID")
    private int mGroupId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("type")
    private boolean mSingleChat;
    @SerializedName("creatorID")
    private int mCreatorId;
    @SerializedName("userID")
    private Integer mUserId;
    @SerializedName("projectID")
    private int mProjectId;
    @SerializedName("members_groups")
    private List<GroupMember> mMembers = new ArrayList<>();
    // endregion

    @Override
    public boolean isTheSame(@NonNull Group item) {
        return (this.getGroupId() == item.getGroupId());
    }

    @Override
    public int getGroupId() {
        return mGroupId;
    }

    @Override
    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Override
    public boolean isSingleChat() {
        return mSingleChat;
    }

    @Override
    public void setSingleChat(boolean singleChat) {
        this.mSingleChat = singleChat;
    }

    @Override
    public int getCreatorId() {
        return mCreatorId;
    }

    @Override
    public void setCreatorId(int creatorId) {
        this.mCreatorId = creatorId;
    }

    @Override
    public int getUserId() {
        return (mUserId == null ? 0 : mUserId);
    }

    @Override
    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    @Override
    public int getProjectId() {
        return mProjectId;
    }

    @Override
    public void setProjectId(int projectId) {
        this.mProjectId = projectId;
    }

    public List<GroupMember> getMembers() {
        return this.mMembers;
    }

    public void setMembers(List<GroupMember> members) {
        this.mMembers = members;
    }
}
