package com.hcmus.easywork.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.utils.UtilsTime;
import com.hcmus.easywork.viewmodels.auth.User;

import java.util.Date;

public class News implements IComparableModel<News> {
    @SerializedName("newsID")
    private int newsId;
    @SerializedName("ownerID")
    private int ownerId = 0;
    private String owner;
    @SerializedName("executorID")
    private int executorId = 0;
    private String executor;
    @SerializedName("cmtID")
    private Integer cmtId = null;
    @SerializedName("watched")
    private boolean watched = false;
    @SerializedName("isNewsOfTask")
    private boolean isNewsOfTask = true;
    @SerializedName("projectID")
    private int projectId = 0;
    @SerializedName("taskID")
    private Integer taskId = null;
    @SerializedName("newsType")
    private NewsType newsType = NewsType.NORMAL;
    @SerializedName("contentEn")
    private String contentEn = null;
    @SerializedName("contentVi")
    private String contentVi = null;
    private User user;
    private Project project;
    private Task task;
    @SerializedName("createdAt")
    private Date createdAt = null;
    @SerializedName("updatedAt")
    private Date updatedAt = null;

    @Override
    public boolean isTheSame(@NonNull News item) {
        return (this.getNewsId() == item.getNewsId() &&
                this.isWatched() == item.isWatched());
    }

    public enum NewsType {
        @SerializedName("1") NORMAL,
        @SerializedName("2") ASSIGN,
        @SerializedName("3") MENTION,
        @SerializedName("4") REMOVE,
        @SerializedName("5") TASK_TODAY,
        @SerializedName("6") ARCHIVE
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getExecutorId() {
        return executorId;
    }

    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public int getCmtId() {
        return cmtId;
    }

    public void setCmtId(Integer cmtId) {
        this.cmtId = cmtId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public boolean isNewsOfTask() {
        return isNewsOfTask;
    }

    public void setNewsOfTask(boolean newsOfTask) {
        isNewsOfTask = newsOfTask;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public NewsType getNewsType() {
        return newsType;
    }

    public void setNewsType(NewsType newsType) {
        this.newsType = newsType;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getContentVi() {
        return contentVi;
    }

    public void setContentVi(String contentVi) {
        this.contentVi = contentVi;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTimeStamp() {
        if (getCreatedAt() != null) {
            return UtilsTime.toFormat(getCreatedAt(), "dd/MM/YYYY, HH:mm");
        }
        return null;
    }
}
