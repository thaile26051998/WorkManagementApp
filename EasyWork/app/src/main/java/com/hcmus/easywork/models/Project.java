package com.hcmus.easywork.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Project implements IProject, IComparableModel<Project> {
    // region Attributes
    @SerializedName("projectID")
    private int mProjectId = 0;
    @SerializedName("name")
    private String mName = null;
    @SerializedName("startDate")
    private Date mStartDate = null;
    @SerializedName("dueDate")
    private Date mDueDate = null;
    @SerializedName("description")
    private String mDescription = null;
    @SerializedName("state")
    private State mState = State.Active;
    @SerializedName("archive")
    private boolean archive = false;
    @SerializedName("createdAt")
    private Date mCreatedAt = null;
    @SerializedName("updatedAt")
    private Date mUpdatedAt = null;
    // endregion

    public enum State {
        @SerializedName("1") Active,
        @SerializedName("2") Completed,
    }

    public Project() {

    }

    @Override
    public boolean isTheSame(@NonNull Project item) {
        return (getProjectId() == item.getProjectId()) &&
                getName().equals(item.getName()) &&
                getStartDate().equals(item.getStartDate()) &&
                getDueDate().equals(item.getDueDate()) &&
                getDescription().equals(item.getDescription()) &&
                // WARNING: project description is always null
                getState().equals(item.getState()) &&
                getCreatedAt().equals(item.getCreatedAt()) &&
                getUpdatedAt().equals(item.getUpdatedAt());
    }

    // region Getters
    @Override
    public int getProjectId() {
        return mProjectId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Date getStartDate() {
        return mStartDate;
    }

    @Override
    public Date getDueDate() {
        return mDueDate;
    }

    @Override
    public String getDescription() {
        return (mDescription == null) ? "" : mDescription;
    }

    @Override
    public Project.State getState() {
        return mState;
    }

    @Override
    public boolean getArchive() {
        return false;
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
    public void setProjectId(int projectId) {
        this.mProjectId = projectId;
    }

    @Override
    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.mStartDate = startDate;
    }

    @Override
    public void setDueDate(Date dueDate) {
        this.mDueDate = dueDate;
    }

    @Override
    public void setDescription(String description) {
        this.mDescription = description;
    }

    @Override
    public void setState(Project.State state) {
        this.mState = state;
    }

    @Override
    public void setArchive(boolean archive) {

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