package com.hcmus.easywork.viewmodels.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.GroupRepository;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.util.ArrayList;
import java.util.List;

public class ConversationViewModel extends BaseUserViewModel {
    private GroupRepository repository;
    private MutableLiveData<LoadingResult> mLoadingResult;
    private MutableLiveData<CreatingResult> mCreatingResult;

    public ConversationViewModel() {
        repository = new GroupRepository();
        mLoadingResult = new MutableLiveData<>(LoadingResult.INITIALIZED);
        mCreatingResult = new MutableLiveData<>(new CreatingResult());
    }

    public void getAllGroups() {
        mLoadingResult.setValue(LoadingResult.LOADING);
        repository.getGroups(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<Group>>() {
            @Override
            public void onResponse(List<Group> response) {
                mLoadingResult.setValue(LoadingResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(LoadingResult.createFailedResult(message));
            }
        });
    }

    public void createGroup(Group group) {
        group.setCreatorId(getUserId());
        repository.createGroup(group).enqueue(new ResponseManager.OnResponseListener<Group>() {
            @Override
            public void onResponse(Group response) {
                mCreatingResult.setValue(new CreatingResult(OperatingState.DONE, response, null));
                mCreatingResult.postValue(new CreatingResult());
            }

            @Override
            public void onFailure(String message) {
                mCreatingResult.setValue(new CreatingResult(OperatingState.FAILED, null, message));
                mCreatingResult.postValue(new CreatingResult());
            }
        });
    }

    public LiveData<LoadingResult> getLoadingResult() {
        return this.mLoadingResult;
    }

    public LiveData<CreatingResult> getCreatingResult() {
        return this.mCreatingResult;
    }

    public static class LoadingResult extends BaseLoadingResult<List<Group>> {
        public LoadingResult(LoadingState state, List<Group> item, String errorMsg) {
            super(state, item, errorMsg);
        }

        public static LoadingResult createLoadedResult(List<Group> groups) {
            return new LoadingResult(LoadingState.LOADED, groups, null);
        }

        public static LoadingResult createFailedResult(String errorMsg) {
            return new LoadingResult(LoadingState.FAILED, new ArrayList<>(), errorMsg);
        }

        public static final LoadingResult INITIALIZED = new LoadingResult(LoadingState.INIT, null, null);
        public static final LoadingResult LOADING = new LoadingResult(LoadingState.LOADING, null, null);
    }

    public static class CreatingResult extends BaseOperatingResult<Group> {
        public CreatingResult() {
            super();
        }

        public CreatingResult(OperatingState state, Group result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
