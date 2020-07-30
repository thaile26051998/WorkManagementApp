package com.hcmus.easywork.models.task;

import com.hcmus.easywork.models.Task;

import java.util.Date;

public interface ITaskSetter {
    // region Setters
    void setTaskId(int taskId);

    void setProjectId(int projectId);

    void setUserId(int userId);

    void setName(String name);

    void setDueDate(Date dueDate);

    void setDescription(String description);

    void setState(Task.State state);

    void setPriority(Task.Priority priority);

    void setCreatedAt(Date createdAt);

    void setUpdatedAt(Date updatedAt);
    // endregion
}
