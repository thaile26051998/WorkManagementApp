package com.hcmus.easywork.models.chat.group;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;
import com.hcmus.easywork.models.member.Member;

public class GroupMember extends Member implements IComparableModel<GroupMember> {
    @SerializedName("groupID")
    private int mGroupId;
    @SerializedName("hasSeen")
    private boolean mHasSeen;

    @Override
    public boolean isTheSame(@NonNull GroupMember item) {
        return (this.getUserId() == item.getUserId());
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public boolean hasSeen() {
        return this.mHasSeen;
    }
}
