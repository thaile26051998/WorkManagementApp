package com.hcmus.easywork.models.chat.group;

public interface IGroupMember {
    // region Getters
    int getGroupId();

    int getUserId();

    int getProjectId();

    int getCreatorId();

    boolean isCreator();
    // endregion

    // region Setters
    void setGroupId(int groupId);

    void setUserId(int userId);

    void setProjectId(int projectId);

    void setCreatorId(int creatorId);

    void setCreator(boolean isCreator);
    // endregion
}
