package com.hcmus.easywork.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.utils.AuthConfig;
import com.hcmus.easywork.databinding.FragmentLoginBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.utils.TextUtil;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;

public class FragmentLogin extends BaseFragment<FragmentLoginBinding> {
    private ValidationViewModel validationViewModel;
    private AuthenticationViewModel authViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validationViewModel = new ViewModelProvider(activity).get(ValidationViewModel.class);
        authViewModel = new ViewModelProvider(activity).get(AuthenticationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.edtUserName.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean condition = TextUtil.isValidEmail(s.toString());
                validationViewModel.validateUsername(condition);
                // Do not warn if text is empty
                String message = (condition || s.toString().isEmpty()) ? null : getString(R.string.text_email_warning_invalid);
                binding.tilUserName.setError(message);
            }
        });

        binding.edtPassword.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean condition = TextUtil.isValidPassword(s.toString());
                validationViewModel.validatePassword(condition);
                int length = s.length();
                String message = (condition || length == 0) ? null :
                        getString(R.string.hint_password_requirement, AuthConfig.passwordMinLength, AuthConfig.passwordMaxLength);
                binding.tilPassword.setError(message);
            }
        });

        binding.btnForgotPassword.setOnClickListener(v -> getNavController().navigate(R.id.action_forgot_password));
        binding.btnSignUp.setOnClickListener(v -> getNavController().navigate(R.id.action_sign_up));
        binding.btnUseGoogle.setOnClickListener(l ->
                getNavController().navigate(R.id.action_continue_with_google));
        binding.btnUseFacebook.setOnClickListener(l ->
                getNavController().navigate(R.id.action_continue_with_facebook));
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtUserName.getEditableText().toString();
            String password = binding.edtPassword.getEditableText().toString();
            binding.btnLogin.setEnabled(false);
            authViewModel.login(email, password);
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        // Enable/disable login button
        validationViewModel.getState().observe(getViewLifecycleOwner(), aBoolean
                -> binding.btnLogin.setEnabled(aBoolean));

        // Observe authentication state.
        // AUTHENTICATED: navigate to main screen
        // UNAUTHENTICATED: by default
        // INVALID: notify that login failed
        authViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case AUTHENTICATED: {
                    validationViewModel.clear();
                    getNavController().navigate(R.id.action_logon);
                    break;
                }
                case UNAUTHENTICATED: {
                    break;
                }
                case INVALID: {
                    new MaterialAlertDialogBuilder(activity)
                            .setTitle("Login response")
                            .setMessage(authViewModel.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                    authViewModel.setAuthenticationState(AuthenticationViewModel.State.UNAUTHENTICATED);
                    binding.btnLogin.setEnabled(true);
                    break;
                }
            }
        });
    }

    /**
     * ViewModel to enable/disable login button
     */
    public static class ValidationViewModel extends ViewModel {
        private MutableLiveData<Boolean> isValidModel = new MutableLiveData<>(false);
        private boolean isUsernameValid = false, isPasswordValid = false;

        public ValidationViewModel() {

        }

        void validateUsername(boolean value) {
            this.isUsernameValid = value;
            isValidModel.setValue(this.isUsernameValid && this.isPasswordValid);
        }

        void validatePassword(boolean value) {
            this.isPasswordValid = value;
            isValidModel.setValue(this.isUsernameValid && this.isPasswordValid);
        }

        public void clear() {
            this.isUsernameValid = false;
            this.isPasswordValid = false;
            this.isValidModel.setValue(false);
        }

        public LiveData<Boolean> getState() {
            return this.isValidModel;
        }
    }

    /**
     * Replace TextWatcher for shorter code
     */
    private static abstract class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public abstract void afterTextChanged(Editable s);
    }
}