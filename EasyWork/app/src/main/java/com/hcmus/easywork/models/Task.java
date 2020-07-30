package com.hcmus.easywork.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Task implements ITask, IComparableModel<Task> {
    // region Attributes
    @SerializedName("taskID")
    private int mTaskId = 0;
    @SerializedName("projectID")
    private int mProjectId = 0;
    @SerializedName("projectName")
    private String projectName = "null";
    @SerializedName("userID")
    private Integer mUserId = null;
    @SerializedName("name")
    private String mName = null;
    @SerializedName("dueDate")
    private Date mDueDate = null;
    @SerializedName("description")
    private String mDescription = null;
    private String assigned = null;
    @SerializedName("state")
    private State mState = State.New;
    @SerializedName("priority")
    private Priority mPriority = Priority.Medium;
    @SerializedName("createdAt")
    private Date mCreatedAt = null;
    @SerializedName("updatedAt")
    private Date mUpdatedAt = null;
    // endregion

    public enum State {
        @SerializedName("1") New(0),
        @SerializedName("2") Active(1),
        @SerializedName("3") Reviewing(2),
        @SerializedName("4") Resolved(3),
        @SerializedName("5") Closed(4),
        @SerializedName("6") Overdue(5);

        private int mIndex;

        State(int index) {
            this.mIndex = index;
        }

        @Nullable
        public State getNext() {
            return get(mIndex + 1);
        }

        @Nullable
        public State getPrevious() {
            return get(mIndex - 1);
        }

        private State get(int index) {
            switch (index) {
                case 0:
                    return New;
                case 1:
                    return Active;
                case 2:
                    return Reviewing;
                case 3:
                    return Resolved;
                case 4:
                    return Closed;
                case 5:
                    return Overdue;
            }
            return null;
        }
    }

    public enum Priority {
        @SerializedName("1") High,
        @SerializedName("2") Medium,
        @SerializedName("3") Low
    }

    public Task() {

    }

    @Override
    public boolean isTheSame(@NonNull Task item) {
        return (getTaskId() == item.getTaskId()) &&
                (getProjectId() == item.getProjectId()) &&
                (getUserId() == item.getUserId()) &&
                getName().equals(item.getName()) &&
                //getDueDate().equals(item.getDueDate()) &&
                // WARNING: due date is null
                getDescription().equals(item.getDescription()) &&
                getState().equals(item.getState()) &&
                getPriority().equals(item.getPriority()) &&
                getCreatedAt().equals(item.getCreatedAt()) &&
                getUpdatedAt().equals(item.getUpdatedAt());
    }

    // region Getters
    @Override
    public int getTaskId() {
        return mTaskId;
    }

    @Override
    public int getProjectId() {
        return mProjectId;
    }

    @Override
    public int getUserId() {
        return mUserId != null ? mUserId : 0;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Date getDueDate() {
        return mDueDate;
    }

    @Override
    public String getDescription() {
        return (mDescription == null) ? "" : mDescription;
    }

    public String getAssigned() {
        return assigned;
    }

    @Override
    public State getState() {
        return mState;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    @Override
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    @Override
    public Date getUpdatedAt() {
        return mUpdatedAt;
    }
    // endregion

    // region Setters
    @Override
    public void setTaskId(int taskId) {
        this.mTaskId = taskId;
    }

    @Override
    public void setProjectId(int projectId) {
        this.mProjectId = projectId;
    }

    @Override
    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public void setDueDate(Date dueDate) {
        this.mDueDate = dueDate;
    }

    @Override
    public void setDescription(String description) {
        this.mDescription = description;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    @Override
    public void setState(State state) {
        this.mState = state;
    }

    @Override
    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.mCreatedAt = createdAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.mUpdatedAt = updatedAt;
    }
    // endregion
}