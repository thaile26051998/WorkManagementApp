package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.UserApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.viewmodels.auth.User;

import java.util.List;

import okhttp3.MultipartBody;

public class UserRepository extends AuthenticatedRepository<UserApiService> {
    @Override
    protected Class<UserApiService> getApiClass() {
        return UserApiService.class;
    }

    public ResponseManager<MessageResponse> changePassword(int id, String mail, String oldPassword, String newPassword) {
        return new ResponseManager<>(getApi().changePassword(id, mail, oldPassword, newPassword));
    }

    public ResponseManager<User> getUser(int userId) {
        return new ResponseManager<>(getApi().getUser(userId));
    }

    public ResponseManager<List<User>> getAllUser() {
        return new ResponseManager<>(getApi().getAllUser());
    }

    public ResponseManager<MessageResponse> editProfile(int userId, User user) {
        return new ResponseManager<>(getApi().editProfile(userId, user));
    }

    public ResponseManager<MessageResponse> uploadAvatar(int userId, MultipartBody.Part file) {
        return new ResponseManager<>(getApi().uploadAvatar(userId, file));
    }
}
