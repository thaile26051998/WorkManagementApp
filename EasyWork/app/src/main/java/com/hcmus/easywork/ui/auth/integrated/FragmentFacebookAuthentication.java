package com.hcmus.easywork.ui.auth.integrated;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hcmus.easywork.data.facebook.FacebookUser;
import com.hcmus.easywork.data.facebook.GetUserCallback;
import com.hcmus.easywork.data.facebook.UserRequest;
import com.hcmus.easywork.models.user.IntegratedUser;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;

import java.util.Arrays;
import java.util.List;

public class FragmentFacebookAuthentication extends Fragment implements GetUserCallback.IGetUserResponse {
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static List<String> permissions = Arrays.asList(EMAIL, PUBLIC_PROFILE);
    private CallbackManager callbackManager;
    private AuthenticationViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, permissions);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                UserRequest.makeRequest(new GetUserCallback(FragmentFacebookAuthentication.this).getCallback());
            }

            @Override
            public void onCancel() {
                getNavController().popBackStack();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    @Override
    public void onCompleted(FacebookUser user) {
        IntegratedUser integratedUser = new IntegratedUser() {
            {
                setEmail(user.getEmail());
                setDisplayName(user.getName());
                setPassword(user.getEmail());
                setFacebookId(user.getId());
                setAvatarUrl(user.getPicture().toString());
            }
        };
        authViewModel.useFacebook(integratedUser);
        getNavController().popBackStack();
    }
}
