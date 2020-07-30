package com.hcmus.easywork.ui.auth.signup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.AuthenticationRepository;
import com.hcmus.easywork.data.response.RegisterResponse;
import com.hcmus.easywork.databinding.FragmentSignUpBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.utils.SharedPreferencesManager;
import com.hcmus.easywork.utils.TextUtil;

public class FragmentSignUp extends BaseFragment<FragmentSignUpBinding> {
    private boolean isEmailValid = false,
            isPasswordValid = false,
            isConfirmPasswordValid = false;
    private MutableLiveData<Boolean> isInfoValid;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInfoValid = new MutableLiveData<>(null);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());

        final TextChangedListener passwordChangedListener = new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String password = binding.edtPassword.getEditableText().toString();
                String confirmPassword = binding.edtConfirmPassword.getEditableText().toString();

                isPasswordValid = TextUtil.isValidPassword(password);
                isConfirmPasswordValid = TextUtil.isValidPassword(confirmPassword) && isPasswordValid && password.equals(confirmPassword);

                binding.tilPassword.setError((isPasswordValid || password.isEmpty()) ? null : TextUtil.getInvalidPasswordText(activity));
                boolean hideConfirmWarning = (isPasswordValid && confirmPassword.isEmpty()) || (isPasswordValid && !isConfirmPasswordValid);
                binding.tilConfirmPassword.setError(hideConfirmWarning ? "Confirm password not match" : null);

                isInfoValid.setValue(isEmailValid && isPasswordValid && isConfirmPasswordValid);
            }
        };

        binding.edtUserName.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean condition = TextUtil.isValidEmail(s.toString());
                isEmailValid = condition;
                isInfoValid.setValue(isEmailValid && isPasswordValid && isConfirmPasswordValid);
                String message = (condition || s.toString().isEmpty()) ? null : getString(R.string.text_email_warning_invalid);
                binding.tilUserName.setError(message);

            }
        });
        binding.edtPassword.addTextChangedListener(passwordChangedListener);
        binding.edtConfirmPassword.addTextChangedListener(passwordChangedListener);

        binding.btnSignUp.setOnClickListener(v -> {
            String email = binding.edtUserName.getEditableText().toString();
            String password = binding.edtPassword.getEditableText().toString();
            signUp(email, password);
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        isInfoValid.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null) {
                binding.btnSignUp.setEnabled(aBoolean);
            }
        });
    }

    private void signUp(String username, String password) {
        AuthenticationRepository authenticationRepository = new AuthenticationRepository();
        binding.btnSignUp.setEnabled(false);
        authenticationRepository.register(username, password)
                .enqueue(new ResponseManager.OnResponseListener<RegisterResponse>() {
                    @Override
                    public void onResponse(RegisterResponse response) {
                        binding.btnSignUp.setEnabled(true);
                        SharedPreferencesManager.getInstance(requireContext()).setAuthenticationValue(username, password);
                        makePopup(R.string.response_send_mail_success);
                        getNavController().navigate(R.id.screen_code_confirmation);
                    }

                    @Override
                    public void onFailure(String message) {
                        binding.btnSignUp.setEnabled(true);
                        makePopup(R.string.response_sign_up_failed);
                    }
                });
    }

    private static abstract class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}
