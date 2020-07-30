package com.hcmus.easywork.models;

import com.hcmus.easywork.models.file.EwFile;

import java.util.Date;

import okhttp3.MultipartBody;

public interface IComment {
    // region Getters
    int getCommentId();

    int getTaskId();

    int getProjectId();

    int getUserId();

    Comment.CommentType getCommentType();

    String getContentEn();

    String getContentVi();

    MultipartBody.Part getFile();

    String getFileType();

    String getFileName();

    EwFile.Data getFileData();

    String getFileSize();

    Date getCreatedAt();

    Date getUpdatedAt();

    String getActor();

    Comment.Action getAction();

    String getMessage();
    // endregion

    // region Setters
    void setCommentId(int commentId);

    void setTaskId(int taskId);

    void setProjectId(int projectId);

    void setUserId(int userId);

    void setCommentType(Comment.CommentType commentType);

    void setContentEn(String content);

    void setContentVi(String content);

    void setFile(MultipartBody.Part file);

    void setFileType(String fileType);

    void setFileName(String fileName);

    void setFileData(EwFile.Data fileData);

    void setFileSize(String fileSize);

    void setCreatedAt(Date createdAt);

    void setUpdatedAt(Date updatedAt);

    void setActor(String actor);

    void setAction(Comment.Action action);

    void setMessage(String message);
    // endregion
}
