package com.hcmus.easywork.models.chat.group;

public interface IGroup {
    // region Getters
    int getGroupId();

    String getName();

    String getDescription();

    boolean isSingleChat();

    int getCreatorId();

    int getUserId();

    int getProjectId();
    // endregion

    // region Setters
    void setGroupId(int groupId);

    void setName(String name);

    void setDescription(String description);

    void setSingleChat(boolean singleChat);

    void setCreatorId(int creatorId);

    void setUserId(int userId);

    void setProjectId(int projectId);
    // endregion
}
