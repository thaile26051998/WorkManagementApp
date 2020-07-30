package com.hcmus.easywork.viewmodels.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.UserRepository;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;

import java.util.List;

public class UserViewModel extends BaseUserViewModel {
    private MutableLiveData<LoadingUserResult> mLoadingUserResult;
    private UserRepository userRepository;

    public UserViewModel(){
        userRepository = new UserRepository();
        mLoadingUserResult = new MutableLiveData<>(LoadingUserResult.INITIALIZED);
    }

    public void fetch(){
        userRepository.getAllUser().enqueue(new ResponseManager.OnResponseListener<List<User>>() {
            @Override
            public void onResponse(List<User> response) {
                mLoadingUserResult.setValue(LoadingUserResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingUserResult.setValue(LoadingUserResult.createFailedResult(message));
            }
        });
    }

    public LiveData<LoadingUserResult> getLoadingUserResult(){
        return this.mLoadingUserResult;
    }

    public static class LoadingUserResult extends BaseLoadingResult<List<User>> {
        public LoadingUserResult(LoadingState state, List<User> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static UserViewModel.LoadingUserResult createLoadedResult(List<User> users) {
            return new UserViewModel.LoadingUserResult(LoadingState.LOADED, users, null);
        }

        public static UserViewModel.LoadingUserResult createFailedResult(String errorMsg) {
            return new UserViewModel.LoadingUserResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final UserViewModel.LoadingUserResult INITIALIZED = new UserViewModel.LoadingUserResult(LoadingState.INIT, null, null);
        public static final UserViewModel.LoadingUserResult LOADING = new UserViewModel.LoadingUserResult(LoadingState.LOADING, null, null);
    }
}
