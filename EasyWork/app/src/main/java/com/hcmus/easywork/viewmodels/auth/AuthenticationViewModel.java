package com.hcmus.easywork.viewmodels.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.LoginResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.utils.SharedPreferencesManager;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.AuthenticationRepository;
import com.hcmus.easywork.data.repository.UserRepository;
import com.hcmus.easywork.models.Authentication;
import com.hcmus.easywork.models.user.IntegratedUser;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import okhttp3.MultipartBody;

public class AuthenticationViewModel extends AndroidViewModel {
    private MutableLiveData<State> mStateModel = new MutableLiveData<>();
    private MutableLiveData<User> mUser = new MutableLiveData<>();
    private MutableLiveData<ResetResult> mResetPassword = new MutableLiveData<>(null);
    private AuthenticationRepository authenticationRepository;
    private UserRepository userRepository;
    private MutableLiveData<EditingProfileResult> mEditingProfileResult;
    private MutableLiveData<UploadingAvatarResult> mUploadingAvatarResult;

    // Warning: temporary solution
    private String mMessage;

    public AuthenticationViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository = new AuthenticationRepository();
        userRepository = new UserRepository();
        mEditingProfileResult = new MutableLiveData<>(new EditingProfileResult());
        mUploadingAvatarResult = new MutableLiveData<>(new UploadingAvatarResult());
    }

    /**
     * <p>Three states of an authentication.</p>
     * <p><code>AUTHENTICATED</code>: login succeeded</p>
     * <p><code>UNAUTHENTICATED</code>: user has not logon or just log out</p>
     * <p><code>INVALID</code>: username or password does not match</p>
     */
    public enum State {
        AUTHENTICATED, UNAUTHENTICATED, INVALID
    }

    // region Getters
    public LiveData<State> getState() {
        return this.mStateModel;
    }

    public LiveData<User> getUser() {
        return this.mUser;
    }

    public String getMessage() {
        return mMessage;
    }

    public LiveData<EditingProfileResult> getEditingProfileResult() {
        return mEditingProfileResult;
    }

    public LiveData<UploadingAvatarResult> getUploadingAvatarResult() {
        return mUploadingAvatarResult;
    }
    // endregion

    public void fetch() {
        userRepository.getUser(mUser.getValue().getUserId()).enqueue(new ResponseManager.OnResponseListener<User>() {
            @Override
            public void onResponse(User response) {
                setUser(response);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void login(String email, String password) {
        this.authenticationRepository.login(email, password)
                .enqueue(new ResponseManager.OnResponseListener<LoginResponse>() {
                    @Override
                    public void onResponse(LoginResponse response) {
                        SharedPreferencesManager manager = SharedPreferencesManager.getInstance();

                        manager.setAccessTokenValue(response.getAccessToken());
                        manager.setAuthenticationValue(email, password);
                        setUser(response.getUser());
                        setAuthenticationState(State.AUTHENTICATED);
                    }

                    @Override
                    public void onFailure(String message) {
                        mMessage = message;
                        setAuthenticationState(State.INVALID);
                    }
                });
    }

    public void selfLogin() throws SelfLoginException {
        SharedPreferencesManager manager = SharedPreferencesManager.getInstance();
        Authentication authentication = manager.getAuthenticationValue();
        if (!authentication.isNull()) {
            login(authentication.getEmail(), authentication.getPassword());
        } else {
            throw new SelfLoginException();
        }
    }

    public void setAuthenticationState(State state) {
        this.mStateModel.setValue(state);
    }

    public void setUser(User user) {
        this.mUser.setValue(user);
    }

    public void logout() {
        SharedPreferencesManager.getInstance().clear();
        setUser(null);
        setAuthenticationState(State.UNAUTHENTICATED);
    }

    public void editProfile(int userId, User user) {
        userRepository.editProfile(userId, user).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                mEditingProfileResult.setValue(new EditingProfileResult(OperatingState.DONE, response, null));
                mEditingProfileResult.postValue(new EditingProfileResult());
            }

            @Override
            public void onFailure(String message) {
                mEditingProfileResult.setValue(new EditingProfileResult(OperatingState.FAILED, null, message));
                mEditingProfileResult.postValue(new EditingProfileResult());
            }
        });
    }

    public void uploadAvatar(int userId, MultipartBody.Part multipartBody) {
        userRepository.uploadAvatar(userId, multipartBody).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                mUploadingAvatarResult.setValue(new UploadingAvatarResult(OperatingState.DONE, response, null));
                mUploadingAvatarResult.postValue(new UploadingAvatarResult());
            }

            @Override
            public void onFailure(String message) {
                mUploadingAvatarResult.setValue(new UploadingAvatarResult(OperatingState.FAILED, null, message));
                mUploadingAvatarResult.postValue(new UploadingAvatarResult());
            }
        });
    }
    // endregion

    public void useGoogle(IntegratedUser user) {
        this.authenticationRepository.useGoogle(user)
                .enqueue(getIntegratedResponseListener());
    }

    public void useFacebook(IntegratedUser user) {
        this.authenticationRepository.useFacebook(user)
                .enqueue(getIntegratedResponseListener());
    }

    private ResponseManager.OnResponseListener<LoginResponse> getIntegratedResponseListener() {
        return new ResponseManager.OnResponseListener<LoginResponse>() {
            @Override
            public void onResponse(LoginResponse response) {
                SharedPreferencesManager manager = SharedPreferencesManager.getInstance();
                manager.setAccessTokenValue(response.getAccessToken());
                manager.setAuthenticationValue(response.getUser().getMail(), response.getUser().getPassword());
                setUser(response.getUser());
                setAuthenticationState(State.AUTHENTICATED);
            }

            @Override
            public void onFailure(String message) {
                mMessage = message;
                setAuthenticationState(State.INVALID);
            }
        };
    }

    public LiveData<ResetResult> resetPassword(String email) {
        authenticationRepository.resetPassword(email).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                mResetPassword.setValue(new ResetResult(true, response.getMessage()));
            }

            @Override
            public void onFailure(String message) {
                mResetPassword.setValue(new ResetResult(false, message));
            }
        });
        return mResetPassword;
    }

    public static class EditingProfileResult extends BaseOperatingResult<MessageResponse> {
        public EditingProfileResult() {
            super();
        }

        public EditingProfileResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class UploadingAvatarResult extends BaseOperatingResult<MessageResponse> {
        public UploadingAvatarResult() {
            super();
        }

        public UploadingAvatarResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static final class ResetResult {
        private boolean complete;
        private String message;

        ResetResult(boolean complete, String message) {
            this.complete = complete;
            this.message = message;
        }

        public boolean isComplete() {
            return complete;
        }

        public String getMessage() {
            return message;
        }
    }
}
