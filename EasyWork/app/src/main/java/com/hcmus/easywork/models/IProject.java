package com.hcmus.easywork.models;

import java.util.Date;

/**
 * Declare getters and setters for model Project
 */
public interface IProject {
    // region Getters
    int getProjectId();

    String getName();

    Date getStartDate();

    Date getDueDate();

    String getDescription();

    Project.State getState();

    boolean getArchive();

    Date getCreatedAt();

    Date getUpdatedAt();
    // endregion

    // region Setters
    void setProjectId(int projectId);

    void setName(String name);

    void setStartDate(Date startDate);

    void setDueDate(Date dueDate);

    void setDescription(String description);

    void setState(Project.State state);

    void setArchive(boolean archive);

    void setCreatedAt(Date createdAt);

    void setUpdatedAt(Date updatedAt);
    // endregion
}
