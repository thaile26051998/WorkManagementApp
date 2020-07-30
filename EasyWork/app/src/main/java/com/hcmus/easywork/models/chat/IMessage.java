package com.hcmus.easywork.models.chat;

import java.util.Date;

public interface IMessage {
    // region Getters
    int getId();

    int getGroupId();

    int getUserId();

    String getContent();

    boolean isPinned();

    Date getTime();

    boolean isNewDate();

    int getRepliedMessageId();

    boolean isDeleted();
    // endregion

    // region Setters
    void setId(int id);

    void setGroupId(int groupId);

    void setUserId(int userId);

    void setContent(String content);

    void setPinned(boolean pinned);

    void setTime(Date time);

    void setIsNewDate(boolean isNewDate);

    void setRepliedMessageId(int repliedMessageId);

    void setDeleted(boolean deleted);
    // endregion
}
