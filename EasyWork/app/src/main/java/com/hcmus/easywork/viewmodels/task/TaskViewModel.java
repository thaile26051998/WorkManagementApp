package com.hcmus.easywork.viewmodels.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.data.repository.TaskRepository;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends BaseUserViewModel {
    private MutableLiveData<LoadingTaskProjectResult> mLoadingTaskProjectResult;
    private MutableLiveData<LoadingMyTaskResult> mLoadingMyTaskResult;
    private MutableLiveData<LoadingAllTaskResult> mLoadingAllTaskResult;
    private MutableLiveData<Task> selectedModel;

    private MutableLiveData<ArrayList<Task>> model = new MutableLiveData<>(new ArrayList<>());
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;

    public TaskViewModel() {
        this.projectRepository = new ProjectRepository();
        this.taskRepository = new TaskRepository();
        this.mLoadingTaskProjectResult = new MutableLiveData<>(LoadingTaskProjectResult.INITIALIZED);
        this.mLoadingMyTaskResult = new MutableLiveData<>(LoadingMyTaskResult.INITIALIZED);
        this.mLoadingAllTaskResult = new MutableLiveData<>(LoadingAllTaskResult.INITIALIZED);
        selectedModel = new MutableLiveData<>(new Task());
    }

    public void getSingleTask(int taskId) {
        for (Task task : getLoadingAllTaskResult().getValue().getResult()) {
            if (task.getTaskId() == taskId) {
                selectedModel.setValue(task);
                break;
            }
        }
    }

    public LiveData<ArrayList<Task>> get() {
        return this.model;
    }

    public LiveData<Task> getSelectedTask() {
        return this.selectedModel;
    }

    public LiveData<LoadingTaskProjectResult> getLoadingTaskProjectResult() {
        return this.mLoadingTaskProjectResult;
    }

    public LiveData<LoadingMyTaskResult> getLoadingMyTaskResult() {
        return this.mLoadingMyTaskResult;
    }

    public LiveData<LoadingAllTaskResult> getLoadingAllTaskResult() {
        return this.mLoadingAllTaskResult;
    }

    public void select(Task task) {
        this.selectedModel.setValue(task);
    }
    ///

    public void removeSelected() {
        Task task = this.selectedModel.getValue();
        if (task != null) {
            ArrayList<Task> tasks = this.model.getValue();
            if (tasks != null) {
                tasks.remove(task);
                this.selectedModel.setValue(null);
                this.model.setValue(tasks);
            }
        }
    }

    public void addTask(Task task) {
        ArrayList<Task> currentTask = this.model.getValue();
        if (currentTask != null) {
            currentTask.add(task);
        }
        this.model.setValue(currentTask);
    }

    public void getAllTask() {
        mLoadingAllTaskResult.setValue(LoadingAllTaskResult.LOADING);
        this.taskRepository.getAllTasks(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Task>>() {
            @Override
            public void onResponse(List<Task> response) {
                mLoadingAllTaskResult.setValue(LoadingAllTaskResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingAllTaskResult.setValue(LoadingAllTaskResult.createFailedResult(message));
            }
        });
    }

    public void fetchProjectTask(int projectID) {
        this.mLoadingTaskProjectResult.setValue(LoadingTaskProjectResult.LOADING);
        this.projectRepository.getProjectTask(projectID).enqueue(new ResponseManager.OnResponseListener<List<Task>>() {
            @Override
            public void onResponse(List<Task> response) {
                mLoadingTaskProjectResult.setValue(LoadingTaskProjectResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingTaskProjectResult.setValue(LoadingTaskProjectResult.createFailedResult(message));
            }
        });
    }

    public void getMyTask() {
        this.mLoadingMyTaskResult.setValue(LoadingMyTaskResult.LOADING);
        taskRepository.getMyTasks(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Task>>() {
            @Override
            public void onResponse(List<Task> response) {
                mLoadingMyTaskResult.setValue(LoadingMyTaskResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingMyTaskResult.setValue(LoadingMyTaskResult.createFailedResult(message));
            }
        });
    }

    public void fetchMyTasks() {
        taskRepository.getMyTasks(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Task>>() {
            @Override
            public void onResponse(List<Task> response) {
                mLoadingMyTaskResult.setValue(LoadingMyTaskResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingMyTaskResult.setValue(LoadingMyTaskResult.createFailedResult(message));
            }
        });
    }

    public static class LoadingTaskProjectResult extends BaseLoadingResult<List<Task>> {

        public LoadingTaskProjectResult(LoadingState state, List<Task> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingTaskProjectResult createLoadedResult(List<Task> tasks) {
            return new LoadingTaskProjectResult(LoadingState.LOADED, tasks, null);
        }

        public static LoadingTaskProjectResult createFailedResult(String errorMsg) {
            return new LoadingTaskProjectResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingTaskProjectResult INITIALIZED = new LoadingTaskProjectResult(LoadingState.INIT, null, null);
        public static final LoadingTaskProjectResult LOADING = new LoadingTaskProjectResult(LoadingState.LOADING, null, null);
    }

    public static class LoadingMyTaskResult extends BaseLoadingResult<List<Task>> {

        public LoadingMyTaskResult(LoadingState state, List<Task> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingMyTaskResult createLoadedResult(List<Task> tasks) {
            return new LoadingMyTaskResult(LoadingState.LOADED, tasks, null);
        }

        public static LoadingMyTaskResult createFailedResult(String errorMsg) {
            return new LoadingMyTaskResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingMyTaskResult INITIALIZED = new LoadingMyTaskResult(LoadingState.INIT, null, null);
        public static final LoadingMyTaskResult LOADING = new LoadingMyTaskResult(LoadingState.LOADING, null, null);
    }

    public static class LoadingAllTaskResult extends BaseLoadingResult<List<Task>> {

        public LoadingAllTaskResult(LoadingState state, List<Task> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingAllTaskResult createLoadedResult(List<Task> tasks) {
            return new LoadingAllTaskResult(LoadingState.LOADED, tasks, null);
        }

        public static LoadingAllTaskResult createFailedResult(String errorMsg) {
            return new LoadingAllTaskResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingAllTaskResult INITIALIZED = new LoadingAllTaskResult(LoadingState.INIT, null, null);
        public static final LoadingAllTaskResult LOADING = new LoadingAllTaskResult(LoadingState.LOADING, null, null);
    }
}
