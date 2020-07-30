package com.hcmus.easywork.viewmodels.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.CommentRepository;

import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.util.List;

import okhttp3.MultipartBody;

public class CommentViewModel extends BaseUserViewModel {
    private CommentRepository commentRepository = new CommentRepository();
    private MutableLiveData<CommentViewModel.LoadingProjectCommentResult> mLoadingProjectCommentResult;
    private MutableLiveData<CommentViewModel.LoadingTaskCommentResult> mLoadingTaskCommentResult;
    private MutableLiveData<DeletingCommentResult> mDeletingCommentResult;
    private MutableLiveData<AddingCommentResult> mAddingCommentResult;
    private MutableLiveData<AddingImageCommentResult> mAddingImageCommentResult;

    public CommentViewModel() {
        this.mLoadingProjectCommentResult = new MutableLiveData<>(CommentViewModel.LoadingProjectCommentResult.INITIALIZED);
        this.mLoadingTaskCommentResult = new MutableLiveData<>(CommentViewModel.LoadingTaskCommentResult.INITIALIZED);
        this.mDeletingCommentResult = new MutableLiveData<>(new DeletingCommentResult());
        this.mAddingCommentResult = new MutableLiveData<>(new AddingCommentResult());
        this.mAddingImageCommentResult = new MutableLiveData<>(new AddingImageCommentResult());
    }

    public void getProjectComment(int projectId) {
        this.mLoadingProjectCommentResult.setValue(LoadingProjectCommentResult.LOADING);
        this.commentRepository.getProjectComment(projectId).enqueue(new ResponseManager.OnResponseListener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                for (Comment comment : response) {
                    if (comment.getCommentType().equals(Comment.CommentType.ACTIVITY)) {
                        comment.setAction(Comment.Action.create);
                    } else if (comment.getCommentType().equals(Comment.CommentType.TEXT)) {
                        comment.setAction(Comment.Action.comment);
                    } else if (comment.getCommentType().equals(Comment.CommentType.FILE)) {
                        comment.setAction(Comment.Action.attach);
                    } else {
                        comment.setAction(Comment.Action.comment);
                    }
                    comment.setActor(comment.getUser().getDisplayName());
                }
                mLoadingProjectCommentResult.setValue(LoadingProjectCommentResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingProjectCommentResult.setValue(LoadingProjectCommentResult.createFailedResult(message));
            }
        });
    }

    public void getTaskComment(int taskId) {
        this.commentRepository.getTaskComment(taskId).enqueue(new ResponseManager.OnResponseListener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                for (Comment comment : response) {
                    if (comment.getCommentType().equals(Comment.CommentType.ACTIVITY)) {
                        comment.setAction(Comment.Action.create);
                    } else if (comment.getCommentType().equals(Comment.CommentType.TEXT)) {
                        comment.setAction(Comment.Action.comment);
                    } else {
                        comment.setAction(Comment.Action.attach);
                    }
                    comment.setActor(comment.getUser().getDisplayName());
                }
                mLoadingTaskCommentResult.setValue(LoadingTaskCommentResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingTaskCommentResult.setValue(LoadingTaskCommentResult.createFailedResult(message));
            }
        });
    }

    public void createComment(Comment comment) {
        commentRepository.createComment(comment).enqueue(new ResponseManager.OnResponseListener<Comment>() {
            @Override
            public void onResponse(Comment response) {
                mAddingCommentResult.setValue(new AddingCommentResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mAddingCommentResult.setValue(new AddingCommentResult(OperatingState.DONE, null, message));
            }
        });
    }

    public void createImageComment(int projectId, int userId, int commentType, MultipartBody.Part file) {
        commentRepository.uploadFile(projectId, userId, commentType, file).enqueue(new ResponseManager.OnResponseListener<Comment>() {
            @Override
            public void onResponse(Comment response) {
                mAddingImageCommentResult.setValue(new AddingImageCommentResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mAddingImageCommentResult.setValue(new AddingImageCommentResult(OperatingState.DONE, null, message));
            }
        });
    }

    public void createImageTaskComment(int taskId, int userId, int commentType, MultipartBody.Part file) {
        commentRepository.uploadTaskFile(taskId, userId, commentType, file).enqueue(new ResponseManager.OnResponseListener<Comment>() {
            @Override
            public void onResponse(Comment response) {
                mAddingImageCommentResult.setValue(new AddingImageCommentResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mAddingImageCommentResult.setValue(new AddingImageCommentResult(OperatingState.DONE, null, message));
            }
        });
    }

    public void editComment(int commentId, String content) {
        commentRepository.editComment(commentId, content).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void deleteComment(int commentId) {
        commentRepository.deleteComment(commentId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingCommentResult.setValue(new DeletingCommentResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingCommentResult.setValue(new DeletingCommentResult(OperatingState.FAILED, null, result));
                }
                mDeletingCommentResult.postValue(new DeletingCommentResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingCommentResult.setValue(new DeletingCommentResult(OperatingState.FAILED, null, message));
                mDeletingCommentResult.postValue(new DeletingCommentResult());
            }
        });
    }

    public LiveData<LoadingProjectCommentResult> getLoadingProjectCommentResult() {
        return this.mLoadingProjectCommentResult;
    }

    public LiveData<LoadingTaskCommentResult> getLoadingTaskCommentResult() {
        return this.mLoadingTaskCommentResult;
    }

    public LiveData<DeletingCommentResult> getDeletingCommentResult() {
        return this.mDeletingCommentResult;
    }

    public LiveData<AddingCommentResult> getAddingCommentResult() {
        return this.mAddingCommentResult;
    }

    public LiveData<AddingImageCommentResult> getAddingImageCommentResult() {
        return this.mAddingImageCommentResult;
    }

    public static class LoadingProjectCommentResult extends BaseLoadingResult<List<Comment>> {

        public LoadingProjectCommentResult(LoadingState state, List<Comment> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static CommentViewModel.LoadingProjectCommentResult createLoadedResult(List<Comment> comments) {
            return new CommentViewModel.LoadingProjectCommentResult(LoadingState.LOADED, comments, null);
        }

        public static CommentViewModel.LoadingProjectCommentResult createFailedResult(String errorMsg) {
            return new CommentViewModel.LoadingProjectCommentResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final CommentViewModel.LoadingProjectCommentResult INITIALIZED = new CommentViewModel.LoadingProjectCommentResult(LoadingState.INIT, null, null);
        public static final CommentViewModel.LoadingProjectCommentResult LOADING = new CommentViewModel.LoadingProjectCommentResult(LoadingState.LOADING, null, null);
    }

    public static class LoadingTaskCommentResult extends BaseLoadingResult<List<Comment>> {

        public LoadingTaskCommentResult(LoadingState state, List<Comment> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static CommentViewModel.LoadingTaskCommentResult createLoadedResult(List<Comment> comments) {
            return new CommentViewModel.LoadingTaskCommentResult(LoadingState.LOADED, comments, null);
        }

        public static CommentViewModel.LoadingTaskCommentResult createFailedResult(String errorMsg) {
            return new CommentViewModel.LoadingTaskCommentResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final CommentViewModel.LoadingTaskCommentResult INITIALIZED = new CommentViewModel.LoadingTaskCommentResult(LoadingState.INIT, null, null);
        public static final CommentViewModel.LoadingTaskCommentResult LOADING = new CommentViewModel.LoadingTaskCommentResult(LoadingState.LOADING, null, null);
    }

    public static class DeletingCommentResult extends BaseOperatingResult<MessageResponse> {
        public DeletingCommentResult() {
            super();
        }

        public DeletingCommentResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class AddingCommentResult extends BaseOperatingResult<Comment> {
        public AddingCommentResult() {
            super();
        }

        public AddingCommentResult(OperatingState state, Comment result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class AddingImageCommentResult extends BaseOperatingResult<Comment> {
        public AddingImageCommentResult() {
            super();
        }

        public AddingImageCommentResult(OperatingState state, Comment result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
