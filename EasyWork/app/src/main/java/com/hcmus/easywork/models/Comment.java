package com.hcmus.easywork.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.file.EwFile;
import com.hcmus.easywork.utils.UtilsTime;
import com.hcmus.easywork.viewmodels.auth.User;

import java.util.Date;

import okhttp3.MultipartBody;

public class Comment implements IComment, IComparableModel<Comment> {
    // region Attributes
    @SerializedName("cmtID")
    private int mCommentId = 0;
    @SerializedName("taskID")
    private Integer mTaskId = null;
    @SerializedName("projectID")
    private Integer mProjectId = null;
    @SerializedName("userID")
    private int mUserId = 0;
    @SerializedName("cmtType")
    private CommentType mCommentType; // TODO: determine default type
    @SerializedName("contentEn") // TODO: unify contentVi and contentEn
    private String mContentEn = null;
    @SerializedName("contentVi")
    private String mContentVi = null;
    @SerializedName("file")
    private  MultipartBody.Part file = null;
    @SerializedName("fileType")
    private String mFileType = null;
    @SerializedName("fileName")
    private String mFileName = null;
    @SerializedName("fileData")
    private EwFile.Data mFileData = null;
    @SerializedName("fileSize")
    private String mFileSize = null; // File size as text
    @SerializedName("edited")
    private boolean edited = false;
    @SerializedName("user")
    private User mUser;
    @SerializedName("createdAt")
    private Date mCreatedAt = null;
    @SerializedName("updatedAt")
    private Date mUpdatedAt = null;
    private String mActor;
    private Action mAction;

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    @SerializedName("message")
    private String message;

    @Override
    public boolean isTheSame(@NonNull Comment item) {
        return (this.getCommentId() == item.getCommentId());
    }
    // endregion

    public enum CommentType {
        @SerializedName("1") ACTIVITY,
        @SerializedName("2") TEXT,
        @SerializedName("3") FILE,
        @SerializedName("4") MENTION
    }

    public enum Action {
        attach, comment, create, mention
    }

    public MultipartBody.Part getFile() {
        return file;
    }

    @Override
    public int getCommentId() {
        return mCommentId;
    }

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
        return mUserId;
    }

    @Override
    public CommentType getCommentType() {
        return mCommentType;
    }

    @Override
    public String getContentEn() {
        return mContentEn;
    }

    @Override
    public String getContentVi() {
        return mContentVi;
    }

    @Override
    public String getFileType() {
        return mFileType;
    }

    @Override
    public String getFileName() {
        return mFileName;
    }

    public EwFile.Data getFileData() {
        return mFileData;
    }

    @Override
    public String getFileSize() {
        return mFileSize;
    }

    @Override
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    @Override
    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    @Override
    public String getActor() {
        return mActor;
    }

    @Override
    public Action getAction() {
        return mAction;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void setCommentId(int commentId) {
        this.mCommentId = commentId;
    }

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

    @Override
    public void setCommentType(CommentType commentType) {
        this.mCommentType = commentType;
    }

    @Override
    public void setContentEn(String content) {
        this.mContentEn = content;
    }

    @Override
    public void setContentVi(String content) {
        this.mContentVi = content;
    }

    @Override
    public void setFile(MultipartBody.Part file) {
        this.file = file;
    }

    @Override
    public void setFileType(String fileType) {
        this.mFileType = fileType;
    }

    @Override
    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    @Override
    public void setFileData(EwFile.Data fileData) {

    }

    @Override
    public void setFileSize(String fileSize) {
        this.mFileSize = fileSize;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.mCreatedAt = createdAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    @Override
    public void setActor(String actor) {
        this.mActor = actor;
    }

    @Override
    public void setAction(Action action) {
        this.mAction = action;
    }

    @Override
    public void setMessage(String message) {

    }

    public String getTimeStamp() {
        if (getCreatedAt() != null) {
            return UtilsTime.toFormat(getCreatedAt(), "dd/MM/YYYY, HH:mm");
        }
        return null;
    }

//    public void setTimeStamp(Date date){
//        this.setCreatedAt(UtilsTime.toFormat(date, "dd/MM/YYYY, HH:mm:ss"));
//    }
}
