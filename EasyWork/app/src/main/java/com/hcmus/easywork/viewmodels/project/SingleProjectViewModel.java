package com.hcmus.easywork.viewmodels.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.util.List;

public class SingleProjectViewModel extends BaseUserViewModel {
    private MutableLiveData<LoadingResult> mLoadingResult;
    private MutableLiveData<LoadingMembersResult> mLoadingMemberResult;
    private MutableLiveData<LoadingSingleMembersResult> mLoadingSingleMembersResult;
    private MutableLiveData<UpdatingProjectResult> mUpdatingProjectResult;
    private MutableLiveData<UpdatingProjectMemberResult> mUpdatingProjectMemberResult;
    private MutableLiveData<AddingMemberResult> mAddingMemberResult;
    private MutableLiveData<RemovingMemberResult> mRemovingMemberResult;
    private MutableLiveData<ArchiveProjectResult> mArchiveProjectResult;
    private MutableLiveData<LoadingGroupsResult> mLoadingGroupsResult;
    private ProjectRepository projectRepository;
    private MutableLiveData<Integer> mProjectId;

    public SingleProjectViewModel() {
        mLoadingResult = new MutableLiveData<>(new LoadingResult(LoadingState.INIT, null, null));
        mLoadingMemberResult = new MutableLiveData<>(new LoadingMembersResult(LoadingState.INIT, null, null));
        mLoadingSingleMembersResult = new MutableLiveData<>(new LoadingSingleMembersResult(LoadingState.INIT, null, null));
        mUpdatingProjectResult = new MutableLiveData<>(new UpdatingProjectResult());
        mUpdatingProjectMemberResult = new MutableLiveData<>(new UpdatingProjectMemberResult());
        mAddingMemberResult = new MutableLiveData<>(new AddingMemberResult());
        mRemovingMemberResult = new MutableLiveData<>(new RemovingMemberResult());
        mArchiveProjectResult = new MutableLiveData<>(new ArchiveProjectResult());
        mLoadingGroupsResult = new MutableLiveData<>(new LoadingGroupsResult());
        projectRepository = new ProjectRepository();
        mProjectId = new MutableLiveData<>();
    }

    public void setProject(Project project) {
        this.mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, project, null));
        this.mProjectId.setValue(project.getProjectId());
    }

    public LiveData<LoadingResult> getLoadingResult() {
        return this.mLoadingResult;
    }

    public LiveData<LoadingMembersResult> getLoadingMemberResult() {
        return this.mLoadingMemberResult;
    }

    public LiveData<LoadingSingleMembersResult> getLoadingSingleMemberResult() {
        return this.mLoadingSingleMembersResult;
    }

    public LiveData<UpdatingProjectResult> getUpdatingProjectResult() {
        return this.mUpdatingProjectResult;
    }

    public LiveData<UpdatingProjectMemberResult> getUpdatingProjectMemberResult() {
        return this.mUpdatingProjectMemberResult;
    }

    public LiveData<AddingMemberResult> getAddingMemberResult() {
        return this.mAddingMemberResult;
    }

    public LiveData<RemovingMemberResult> getRemovingMemberResult() {
        return this.mRemovingMemberResult;
    }

    public LiveData<ArchiveProjectResult> getArchiveProjectResult() {
        return this.mArchiveProjectResult;
    }

    public LiveData<LoadingGroupsResult> getLoadingGroupsResult() {
        return this.mLoadingGroupsResult;
    }

    public void fetch() {
        mLoadingResult.setValue(LoadingResult.LOADING);
        projectRepository.getProject(getProjectId()).enqueue(new ResponseManager.OnResponseListener<Project>() {
            @Override
            public void onResponse(Project response) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void fetchMembers() {
        mLoadingMemberResult.setValue(LoadingMembersResult.LOADING);
        this.projectRepository.getAllMembers(getProjectId()).enqueue(new ResponseManager.OnResponseListener<List<ProjectMember>>() {
            @Override
            public void onResponse(List<ProjectMember> response) {
                mLoadingMemberResult.setValue(LoadingMembersResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingMemberResult.setValue(LoadingMembersResult.createFailedResult(message));
            }
        });
    }

    public void getSingleMember() {
        mLoadingSingleMembersResult.setValue(LoadingSingleMembersResult.LOADING);
        this.projectRepository.getSingleProjectMember(getProjectId(), getUserId()).enqueue(new ResponseManager.OnResponseListener<ProjectMember>() {
            @Override
            public void onResponse(ProjectMember response) {
                mLoadingSingleMembersResult.setValue(new LoadingSingleMembersResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingSingleMembersResult.setValue(new LoadingSingleMembersResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void updateMember(int memberId, ProjectMember member) {
        this.projectRepository.updateProjectMember(getProjectId(), memberId, member).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mUpdatingProjectMemberResult.setValue(new UpdatingProjectMemberResult(OperatingState.DONE, response, null));
                } else {
                    mUpdatingProjectMemberResult.setValue(new UpdatingProjectMemberResult(OperatingState.FAILED, null, result));
                }
                mUpdatingProjectMemberResult.postValue(new UpdatingProjectMemberResult());
            }

            @Override
            public void onFailure(String message) {
                mUpdatingProjectMemberResult.setValue(new UpdatingProjectMemberResult(OperatingState.FAILED, null, message));
                mUpdatingProjectMemberResult.postValue(new UpdatingProjectMemberResult());
            }
        });
    }

    public void addMember(int projectId, int userId, int role) {
        this.projectRepository.addProjectMember(projectId, userId, role).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                mAddingMemberResult.setValue(new AddingMemberResult(OperatingState.DONE, response, null));
                mAddingMemberResult.postValue(new AddingMemberResult());
            }

            @Override
            public void onFailure(String message) {
                mAddingMemberResult.setValue(new AddingMemberResult(OperatingState.DONE, null, message));
                mAddingMemberResult.postValue(new AddingMemberResult());
            }
        });
    }

    public void removeMember(int memberId) {
        this.projectRepository.removeMember(getProjectId(), memberId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mRemovingMemberResult.setValue(new RemovingMemberResult(OperatingState.DONE, response, null));
                } else {
                    mRemovingMemberResult.setValue(new RemovingMemberResult(OperatingState.FAILED, null, result));
                }
                mRemovingMemberResult.postValue(new RemovingMemberResult());
            }

            @Override
            public void onFailure(String message) {
                mRemovingMemberResult.setValue(new RemovingMemberResult(OperatingState.FAILED, null, message));
                mRemovingMemberResult.postValue(new RemovingMemberResult());
            }
        });
    }

    public void leaveProject(int userId) {
        this.projectRepository.leaveProject(getProjectId(), userId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void toggleArchiveProject(boolean archiveStatus) {
        this.projectRepository.toggleArchiveProject(getProjectId(), archiveStatus).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void archiveProject() {
        this.projectRepository.archiveProject(getProjectId()).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mArchiveProjectResult.setValue(new ArchiveProjectResult(OperatingState.DONE, response, null));
                } else {
                    mArchiveProjectResult.setValue(new ArchiveProjectResult(OperatingState.FAILED, null, result));
                }
                mArchiveProjectResult.postValue(new ArchiveProjectResult());
            }

            @Override
            public void onFailure(String message) {
                mArchiveProjectResult.setValue(new ArchiveProjectResult(OperatingState.FAILED, null, message));
                mArchiveProjectResult.postValue(new ArchiveProjectResult());
            }
        });
    }

    public void updateProject(int projectId, Project project) {
        this.projectRepository.updateProject(projectId, project).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mUpdatingProjectResult.setValue(new UpdatingProjectResult(OperatingState.DONE, response, null));
                } else {
                    mUpdatingProjectResult.setValue(new UpdatingProjectResult(OperatingState.FAILED, null, result));
                }
                mUpdatingProjectResult.postValue(new UpdatingProjectResult());
            }

            @Override
            public void onFailure(String message) {
                mUpdatingProjectResult.setValue(new UpdatingProjectResult(OperatingState.FAILED, null, message));
                mUpdatingProjectResult.postValue(new UpdatingProjectResult());
            }
        });
    }

    public int getProjectId() {
        return mProjectId.getValue() != null ?
                mProjectId.getValue() : 0;
    }

    public void setProjectId(int projectId) {
        this.mProjectId.setValue(projectId);
    }

    public void getGroups() {
        projectRepository.getGroups(getProjectId()).enqueue(new ResponseManager.OnResponseListener<List<Group>>() {
            @Override
            public void onResponse(List<Group> response) {
                mLoadingGroupsResult.setValue(new LoadingGroupsResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingGroupsResult.setValue(new LoadingGroupsResult(OperatingState.FAILED, null, message));
            }
        });
    }

    public static final class LoadingResult extends BaseLoadingResult<Project> {
        public static final LoadingResult LOADING = new LoadingResult(LoadingState.LOADING, null, null);

        public LoadingResult(LoadingState state, Project result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static final class LoadingMembersResult extends BaseLoadingResult<List<ProjectMember>> {

        public static final LoadingMembersResult INITIALIZED = new LoadingMembersResult(LoadingState.INIT, null, null);
        public static final LoadingMembersResult LOADING = new LoadingMembersResult(LoadingState.LOADING, null, null);

        public LoadingMembersResult(LoadingState state, List<ProjectMember> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static LoadingMembersResult createLoadedResult(List<ProjectMember> members) {
            return new LoadingMembersResult(LoadingState.LOADED, members, null);
        }

        public static LoadingMembersResult createFailedResult(String errorMsg) {
            return new LoadingMembersResult(LoadingState.LOADED, null, errorMsg);
        }
    }

    public static final class LoadingSingleMembersResult extends BaseLoadingResult<ProjectMember> {

        public static final LoadingSingleMembersResult LOADING = new LoadingSingleMembersResult(LoadingState.LOADING, null, null);

        public LoadingSingleMembersResult(LoadingState state, ProjectMember result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class UpdatingProjectResult extends BaseOperatingResult<MessageResponse> {
        public UpdatingProjectResult() {
            super();
        }

        public UpdatingProjectResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class UpdatingProjectMemberResult extends BaseOperatingResult<MessageResponse> {
        public UpdatingProjectMemberResult() {
            super();
        }

        public UpdatingProjectMemberResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class AddingMemberResult extends BaseOperatingResult<MessageResponse> {
        public AddingMemberResult() {
            super();
        }

        public AddingMemberResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class RemovingMemberResult extends BaseOperatingResult<MessageResponse> {
        public RemovingMemberResult() {
            super();
        }

        public RemovingMemberResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class ArchiveProjectResult extends BaseOperatingResult<MessageResponse> {
        public ArchiveProjectResult() {
            super();
        }

        public ArchiveProjectResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingGroupsResult extends BaseOperatingResult<List<Group>> {
        public LoadingGroupsResult() {
            super();
        }

        public LoadingGroupsResult(OperatingState state, List<Group> result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
