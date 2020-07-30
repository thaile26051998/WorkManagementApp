package com.hcmus.easywork.models.task;

import androidx.lifecycle.LiveData;

import com.hcmus.easywork.models.Task;

import java.util.Date;

public interface ILiveTask extends ITaskSetter {
    // region Getters
    LiveData<Integer> getTaskId();

    LiveData<Integer> getProjectId();

    LiveData<Integer> getUserId();

    LiveData<String> getName();

    LiveData<Date> getDueDate();

    LiveData<String> getDescription();

    LiveData<Task.State> getState();

    LiveData<Task.Priority> getPriority();

    LiveData<Date> getCreatedAt();

    LiveData<Date> getUpdatedAt();
    // endregion
}
