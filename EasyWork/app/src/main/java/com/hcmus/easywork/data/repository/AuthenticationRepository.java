package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.LoginResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.response.RegisterResponse;
import com.hcmus.easywork.data.response.VerifyResponse;
import com.hcmus.easywork.data.api.AuthApiService;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.common.UnauthenticatedRepository;
import com.hcmus.easywork.models.user.IntegratedUser;

public class AuthenticationRepository extends UnauthenticatedRepository<AuthApiService> {
    @Override
    protected Class<AuthApiService> getApiClass() {
        return AuthApiService.class;
    }

    public ResponseManager<LoginResponse> login(String mail, String password) {
        return new ResponseManager<>(getApi().login(mail, password));
    }

    public ResponseManager<RegisterResponse> register(String mail, String password) {
        return new ResponseManager<>(getApi().register(mail, password));
    }

    public ResponseManager<VerifyResponse> verify(String mail, String password, Number code) {
        return new ResponseManager<>(getApi().verify(mail, password, code));
    }

    public ResponseManager<MessageResponse> resetPassword(String mail) {
        return new ResponseManager<>(getApi().resetPassword(mail));
    }

    public ResponseManager<LoginResponse> useGoogle(IntegratedUser user) {
        return new ResponseManager<>(getApi().useGoogle(user));
    }

    public ResponseManager<LoginResponse> useFacebook(IntegratedUser user) {
        return new ResponseManager<>(getApi().useFacebook(user));
    }
}
