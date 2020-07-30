package com.hcmus.easywork.models.chat;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;

import java.util.Date;

public class Message implements IMessage, IComparableModel<Message> {
    // region Attributes
    @SerializedName("messID")
    private int mId = 0;
    @SerializedName("groupID")
    private int mGroupId = 0;
    @SerializedName("userID")
    private int mUserId = 0;
    @SerializedName("content")
    private String mContent = null;
    @SerializedName("pin")
    private boolean mPinned = false;
    @SerializedName("time")
    private Date mTime = null;
    @SerializedName("isNewDate")
    private boolean mIsNewDate;
    @SerializedName("replyMessID")
    private Integer mRepliedMessageId = null;
    @SerializedName("deleted")
    private boolean mDeleted = false;
    // endregion

    @Override
    public boolean isTheSame(@NonNull Message item) {
        return (this.getId() == item.getId()) &&
                (this.getGroupId() == item.getGroupId()) &&
                (this.getUserId() == item.getUserId()) &&
                (this.getContent().equals(item.getContent())) &&
                (this.isPinned() == item.isPinned());
    }

    public Message copy() {
        Message message = new Message();
        message.setId(getId());
        message.setGroupId(getGroupId());
        message.setUserId(getUserId());
        message.setContent(getContent());
        message.setPinned(isPinned());
        message.setTime(getTime());
        message.setRepliedMessageId(getRepliedMessageId());
        message.setDeleted(isDeleted());
        return message;
    }

    // region Getters
    @Override
    public int getId() {
        return mId;
    }

    @Override
    public int getGroupId() {
        return mGroupId;
    }

    @Override
    public int getUserId() {
        return mUserId;
    }

    @Override
    public String getContent() {
        return mContent;
    }

    @Override
    public boolean isPinned() {
        return mPinned;
    }

    @Override
    public Date getTime() {
        return mTime;
    }

    @Override
    public boolean isNewDate() {
        return mIsNewDate;
    }

    @Override
    public int getRepliedMessageId() {
        return mRepliedMessageId == null ? 0 : mRepliedMessageId;
    }

    @Override
    public boolean isDeleted() {
        return mDeleted;
    }
    // endregion

    // region Setters
    @Override
    public void setId(int id) {
        this.mId = id;
    }

    @Override
    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

    @Override
    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    @Override
    public void setContent(String content) {
        this.mContent = content;
    }

    @Override
    public void setPinned(boolean pinned) {
        this.mPinned = pinned;
    }

    @Override
    public void setTime(Date time) {
        this.mTime = time;
    }

    @Override
    public void setIsNewDate(boolean isNewDate) {
        this.mIsNewDate = isNewDate;
    }

    @Override
    public void setRepliedMessageId(int repliedMessageId) {
        this.mRepliedMessageId = repliedMessageId;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }
    // endregion
}
