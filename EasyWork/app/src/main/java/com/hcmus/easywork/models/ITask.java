package com.hcmus.easywork.models;

import com.hcmus.easywork.models.task.ITaskSetter;

import java.util.Date;

/**
 * Declare getters and setters for model Task
 */
public interface ITask extends ITaskSetter {
    // region Getters
    int getTaskId();

    int getProjectId();

    int getUserId();

    String getName();

    Date getDueDate();

    String getDescription();

    Task.State getState();

    Task.Priority getPriority();

    Date getCreatedAt();

    Date getUpdatedAt();
    // endregion
}
