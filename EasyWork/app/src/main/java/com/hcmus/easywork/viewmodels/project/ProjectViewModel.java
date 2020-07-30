package com.hcmus.easywork.viewmodels.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;

import java.util.List;

public class ProjectViewModel extends BaseUserViewModel {
    private ProjectRepository projectRepository;
    private MutableLiveData<LoadingResult> mLoadingResult;
    private MutableLiveData<LoadingArchiveResult> mLoadingArchiveResult;
    private MutableLiveData<Project> selectedProject;

    public ProjectViewModel() {
        this.projectRepository = new ProjectRepository();
        this.mLoadingResult = new MutableLiveData<>(LoadingResult.INITIALIZED);
        this.mLoadingArchiveResult = new MutableLiveData<>(LoadingArchiveResult.INITIALIZED);
        selectedProject = new MutableLiveData<>(new Project());
    }

    public void fetch() {
        this.mLoadingResult.setValue(LoadingResult.LOADING);
        this.projectRepository.getAll(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Project>>() {
            @Override
            public void onResponse(List<Project> response) {
                mLoadingResult.setValue(LoadingResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(LoadingResult.createFailedResult(message));
            }
        });
    }

    public void getSingleProject(int projectId) {
        for (Project project : getLoadingResult().getValue().getResult()) {
            if (project.getProjectId() == projectId) {
                selectedProject.setValue(project);
                break;
            }
        }
    }

    public void fetchArchiveProject() {
        this.mLoadingArchiveResult.setValue(LoadingArchiveResult.LOADING);
        this.projectRepository.getArchiveProject(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Project>>() {
            @Override
            public void onResponse(List<Project> response) {
                mLoadingArchiveResult.setValue(LoadingArchiveResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingArchiveResult.setValue(LoadingArchiveResult.createFailedResult(message));
            }
        });
    }

    public LiveData<LoadingResult> getLoadingResult() {
        return this.mLoadingResult;
    }

    public LiveData<LoadingArchiveResult> getLoadingArchiveResult() {
        return this.mLoadingArchiveResult;
    }

    public LiveData<Project> getSelectedProject() {
        return this.selectedProject;
    }

    public static class LoadingResult extends BaseLoadingResult<List<Project>> {

        public LoadingResult(LoadingState state, List<Project> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingResult createLoadedResult(List<Project> projects) {
            return new LoadingResult(LoadingState.LOADED, projects, null);
        }

        public static LoadingResult createFailedResult(String errorMsg) {
            return new LoadingResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingResult INITIALIZED = new LoadingResult(LoadingState.INIT, null, null);
        public static final LoadingResult LOADING = new LoadingResult(LoadingState.LOADING, null, null);
    }

    public static class LoadingArchiveResult extends BaseLoadingResult<List<Project>> {

        public LoadingArchiveResult(LoadingState state, List<Project> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingArchiveResult createLoadedResult(List<Project> projects) {
            return new LoadingArchiveResult(LoadingState.LOADED, projects, null);
        }

        public static LoadingArchiveResult createFailedResult(String errorMsg) {
            return new LoadingArchiveResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingArchiveResult INITIALIZED = new LoadingArchiveResult(LoadingState.INIT, null, null);
        public static final LoadingArchiveResult LOADING = new LoadingArchiveResult(LoadingState.LOADING, null, null);
    }
}
