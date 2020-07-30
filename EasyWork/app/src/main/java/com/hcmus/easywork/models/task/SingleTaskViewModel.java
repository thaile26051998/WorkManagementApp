package com.hcmus.easywork.models.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.TaskRepository;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

public class SingleTaskViewModel extends BaseUserViewModel {
    private MutableLiveData<LoadingResult> mLoadingResult;
    private MutableLiveData<UpdatingTaskResult> mUpdatingTaskResult;
    private TaskRepository taskRepository;
    private MutableLiveData<Integer> mTaskId;

    public SingleTaskViewModel() {
        mLoadingResult = new MutableLiveData<>(new LoadingResult(LoadingState.INIT, null, null));
        mUpdatingTaskResult = new MutableLiveData<>(new UpdatingTaskResult());
        mTaskId = new MutableLiveData<>();
        taskRepository = new TaskRepository();
    }

    public void fetchTask() {
        mLoadingResult.setValue(LoadingResult.LOADING);
        taskRepository.getSingleTask(getTaskId()).enqueue(new ResponseManager.OnResponseListener<Task>() {
            @Override
            public void onResponse(Task response) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void getSingleTask(int taskId) {
        taskRepository.getSingleTask(taskId).enqueue(new ResponseManager.OnResponseListener<Task>() {
            @Override
            public void onResponse(Task response) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void updateTask(int taskId, Task task) {
        this.taskRepository.updateTask(taskId, task).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mUpdatingTaskResult.setValue(new UpdatingTaskResult(OperatingState.DONE, response, null));
                } else {
                    mUpdatingTaskResult.setValue(new UpdatingTaskResult(OperatingState.FAILED, null, result));
                }
                mUpdatingTaskResult.postValue(new UpdatingTaskResult());
            }

            @Override
            public void onFailure(String message) {
                mUpdatingTaskResult.setValue(new UpdatingTaskResult(OperatingState.FAILED, null, message));
                mUpdatingTaskResult.postValue(new UpdatingTaskResult());
            }
        });
    }

    public void setTask(Task task) {
        this.mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, task, null));
        this.mTaskId.setValue(task.getTaskId());
    }

    public int getTaskId() {
        return mTaskId.getValue() != null ? mTaskId.getValue() : 0;
    }

    public LiveData<LoadingResult> getLoadingResult() {
        return this.mLoadingResult;
    }

    public LiveData<UpdatingTaskResult> getUpdatingTaskResult() {
        return this.mUpdatingTaskResult;
    }

    public static final class LoadingResult extends BaseLoadingResult<Task> {
        public LoadingResult(LoadingState state, Task result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static final LoadingResult LOADING = new LoadingResult(LoadingState.LOADING, null, null);
    }

    public static class UpdatingTaskResult extends BaseOperatingResult<MessageResponse> {
        public UpdatingTaskResult() {
            super();
        }

        public UpdatingTaskResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    //    public void stepNextState() {
//        Task.State state = mState.getValue();
//        if (state != null && state.getNext() != null)
//            mState.setValue(state.getNext());
//    }
//
//    public void backPreviousState() {
//        Task.State state = mState.getValue();
//        if (state != null && state.getPrevious() != null)
//            mState.setValue(state.getPrevious());
//    }
}
