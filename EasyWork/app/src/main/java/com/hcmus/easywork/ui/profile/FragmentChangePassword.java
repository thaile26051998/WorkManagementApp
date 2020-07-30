package com.hcmus.easywork.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.UserRepository;
import com.hcmus.easywork.databinding.FragmentChangePasswordBinding;
import com.hcmus.easywork.ui.common.fragment.BaseBottomFragment;
import com.hcmus.easywork.utils.TextUtil;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.auth.User;

public class FragmentChangePassword extends BaseBottomFragment<FragmentChangePasswordBinding> {
    private AuthenticationViewModel authViewModel;
    private PasswordNotifier passwordNotifier;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_change_password;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(activity).get(AuthenticationViewModel.class);
        passwordNotifier = new PasswordNotifier();
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.edtCurrentPassword.addTextChangedListener(new PasswordFieldChangedListener() {
            @Override
            public void onFieldChanged(String text, boolean isValidPassword) {
                passwordNotifier.notifyCurrentPassword(isValidPassword);

                String message = (text.isEmpty() || isValidPassword) ? null : TextUtil.getInvalidPasswordText(activity);
                binding.tilCurrentPassword.setError(message);
            }
        });

        binding.edtNewPassword.addTextChangedListener(new PasswordFieldChangedListener() {
            @Override
            public void onFieldChanged(String text, boolean isValidPassword) {
                passwordNotifier.notifyNewPassword(isValidPassword);

                String message = isValidPassword ? null : TextUtil.getInvalidPasswordText(activity);
                binding.tilNewPassword.setError(message);

                // TODO: also check confirm password
                // WARNING: bad way
                if (isValidPassword) {
                    String confirmPassword = binding.edtConfirmPassword.getEditableText().toString();
                    binding.edtConfirmPassword.getEditableText().clear();
                    binding.edtConfirmPassword.setText(confirmPassword);
                }
            }
        });

        binding.edtConfirmPassword.addTextChangedListener(new PasswordFieldChangedListener() {
            @Override
            public void onFieldChanged(String text, boolean isValidPassword) {
                String newPassword = binding.edtNewPassword.getEditableText().toString();
                boolean isMatch = newPassword.equals(text);
                passwordNotifier.notifyConfirmPassword(isMatch);
                String message = isMatch ? null : "Confirm password not match";
                binding.tilConfirmPassword.setError(message);
            }
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            final User user = authViewModel.getUser().getValue();
            if (user != null) {
                String currentPassword = binding.edtCurrentPassword.getEditableText().toString();
                String newPassword = binding.edtNewPassword.getEditableText().toString();
                changePassword(user.getUserId(), user.getMail(), currentPassword, newPassword);
            }

        });
    }

    private void changePassword(int id, String email, String oldPassword, String newPassword) {
        binding.btnChangePassword.setEnabled(false);
        UserRepository userRepository = new UserRepository();
        userRepository.changePassword(id, email, oldPassword, newPassword)
                .enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
                    @Override
                    public void onResponse(MessageResponse response) {
                        binding.btnChangePassword.setEnabled(true);
                        makePopup(response.getMessage());
                        getNavController().popBackStack();
                    }

                    @Override
                    public void onFailure(String message) {
                        makePopup(message);
                        binding.btnChangePassword.setEnabled(true);
                    }
                });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        passwordNotifier.observeEnableButton(getViewLifecycleOwner(),
                aBoolean -> binding.btnChangePassword.setEnabled(aBoolean));
    }

    public static class PasswordNotifier {
        private MutableLiveData<Boolean> mEnableButton = new MutableLiveData<>();
        private boolean mCurrent = false, mNew = false, mConfirm = false;

        void notifyCurrentPassword(boolean isValid) {
            mCurrent = isValid;
            setEnableButton();
        }

        void notifyNewPassword(boolean isValid) {
            mNew = isValid;
            setEnableButton();
        }

        void notifyConfirmPassword(boolean isValid) {
            mConfirm = isValid;
            setEnableButton();
        }

        private void setEnableButton() {
            mEnableButton.setValue(mCurrent && mNew && mConfirm);
        }

        void observeEnableButton(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
            mEnableButton.observe(owner, observer);
        }
    }

    /**
     * Replace TextWatcher for shorter code
     */
    private static abstract class PasswordFieldChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = s.toString();
            boolean isValid = TextUtil.isValidPassword(password);
            onFieldChanged(password, isValid);
        }

        public abstract void onFieldChanged(String text, boolean isValidPassword);
    }
}
