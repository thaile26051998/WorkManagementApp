package com.hcmus.easywork.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentForgotPasswordBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.utils.TextUtil;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;

public class FragmentForgotPassword extends BaseFragment<FragmentForgotPasswordBinding> {
    private AuthenticationViewModel authViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(activity).get(AuthenticationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().navigateUp());
        binding.btnResetPassword.setOnClickListener(v -> {
            v.setEnabled(false);
            String email = binding.txtEmail.getEditableText().toString();
            recoverPassword(email);
        });

        binding.txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                binding.btnResetPassword.setEnabled(TextUtil.isValidEmail(email));
            }
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    private void recoverPassword(String email) {
        authViewModel.resetPassword(email).observe(getViewLifecycleOwner(), resetResult -> {
            if (resetResult != null) {
                if (resetResult.isComplete()) {
                    makePopup(R.string.response_reset_pass_success);
                    getNavController().navigate(R.id.action_logon);
                } else {
                    makePopup(resetResult.getMessage());
                }
                binding.btnResetPassword.setEnabled(true);
            }
        });
    }
}
