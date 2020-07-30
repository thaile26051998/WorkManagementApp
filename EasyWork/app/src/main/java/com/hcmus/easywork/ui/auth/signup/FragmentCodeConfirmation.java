package com.hcmus.easywork.ui.auth.signup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.AuthenticationRepository;
import com.hcmus.easywork.data.response.VerifyResponse;
import com.hcmus.easywork.databinding.FragmentCodeConfirmationBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.utils.SharedPreferencesManager;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.auth.SelfLoginException;
import com.hcmus.easywork.views.PinCodeEditText;

import java.util.ArrayList;

public class FragmentCodeConfirmation extends BaseFragment<FragmentCodeConfirmationBinding> {
    private ArrayList<PinCodeEditText> textView = new ArrayList<>();
    private SharedPreferencesManager sharedPreferencesManager;
    private AuthenticationViewModel authenticationViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_code_confirmation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(activity);
        authenticationViewModel = createViewModel(AuthenticationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().navigateUp());
        textView.add(null);
        textView.add(binding.code1);
        textView.add(binding.code2);
        textView.add(binding.code3);
        textView.add(binding.code4);
        textView.add(binding.code5);
        textView.add(binding.code6);
        textView.add(null);

        for (int i = 1; i < textView.size() - 1; i++) {
            textView.get(i).addTextChangedListener(new GenericTextWatcher(textView.get(i - 1), textView.get(i + 1)));
        }

        binding.btnVerify.setOnClickListener(v -> {
            Number code = Integer.parseInt(getVerifiedCode());
            AuthenticationRepository authenticationRepository = new AuthenticationRepository();
            String mail = sharedPreferencesManager.getAuthenticationValue().getEmail();
            String password = sharedPreferencesManager.getAuthenticationValue().getPassword();
            authenticationRepository.verify(mail, password, code)
                    .enqueue(new ResponseManager.OnResponseListener<VerifyResponse>() {
                        @Override
                        public void onResponse(VerifyResponse response) {
                            makePopup(R.string.response_verify_success);
                            try {
                                authenticationViewModel.selfLogin();
                            } catch (SelfLoginException e) {
                                e.printStackTrace();
                            }
                            getNavController().navigate(R.id.action_logon);
                        }

                        @Override
                        public void onFailure(String message) {
                            binding.btnVerify.setEnabled(true);
                            makePopup(R.string.response_verify_failed);
                        }
                    });
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    private String getVerifiedCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 1; i < textView.size() - 1; i++) {
            code.append(textView.get(i).getEditableText().toString());
        }
        return code.toString();
    }

    private static class GenericTextWatcher implements TextWatcher {
        private TextView tvPrevious, tvNext;

        private GenericTextWatcher(@Nullable TextView tv1, @Nullable TextView tv2) {
            this.tvPrevious = tv1;
            this.tvNext = tv2;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Focus on the previous or next TextView based on current TextView's text length
         *
         * @param s Text in TextView
         */
        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            if (length == 1 && tvNext != null) {
                tvNext.requestFocus();
            } else if (length == 0 && tvPrevious != null) {
                tvPrevious.requestFocus();
            }
        }
    }
}
