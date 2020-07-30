package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.CommentApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.models.Comment;

import java.util.List;

import okhttp3.MultipartBody;

public class CommentRepository extends AuthenticatedRepository<CommentApiService> {
    public CommentRepository() {

    }

    public ResponseManager<Comment> createComment(Comment comment) {
        return new ResponseManager<>(getApi().createComment(comment));
    }

    public ResponseManager<Comment> uploadFile(int projectId, int userId, int commentType, MultipartBody.Part file) {
        return new ResponseManager<>(getApi().uploadFile(projectId, userId, commentType, file));
    }

    public ResponseManager<Comment> uploadTaskFile(int taskId, int userId, int commentType, MultipartBody.Part file) {
        return new ResponseManager<>(getApi().uploadTaskFile(taskId, userId, commentType, file));
    }


    public ResponseManager<List<Comment>> getProjectComment(int projectId) {
        return new ResponseManager<>(getApi().getProjectComment(projectId));
    }

    public ResponseManager<List<Comment>> getTaskComment(int taskId) {
        return new ResponseManager<>(getApi().getTaskComment(taskId));
    }

    public ResponseManager<MessageResponse> deleteComment(int commentId) {
        return new ResponseManager<>(getApi().deleteComment(commentId));
    }

    public ResponseManager<MessageResponse> editComment(int commentId, String content) {
        return new ResponseManager<>(getApi().editComment(commentId, content));
    }

    public ResponseManager<MessageResponse> sendFile(int cmtId, MultipartBody.Part file) {
        return new ResponseManager<>(getApi().sendFile(cmtId, file));
    }

    @Override
    protected Class<CommentApiService> getApiClass() {
        return CommentApiService.class;
    }
}
