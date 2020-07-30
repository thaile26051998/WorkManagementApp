package com.hcmus.easywork.ui.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.views.CustomDatePickerDialog;
import com.hcmus.easywork.databinding.FragmentProfileBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.file.MediaPicker;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.auth.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FragmentProfile extends BaseFragment<FragmentProfileBinding> {
    private AuthenticationViewModel authViewModel;
    private String displayName;
    private String phoneNumber;
    private String address;
    private Date birthday;
    private File file = null;
    private User user;
    private String mimeType = null;
    private MultipartBody.Part multipartBody;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = createViewModel(AuthenticationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());

        binding.dateOfBirth.setOnClickListener(l -> new CustomDatePickerDialog.Builder(activity)
                //.setMinDate(Calendar.getInstance())
                .setOnDatePickedListener(new CustomDatePickerDialog.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(int day, int month, int year) {

                    }

                    @Override
                    public void onDatePicked(Date pickedDate) {
                        String result = new SimpleDateFormat(activity.getString(R.string.format_standard_date), Locale.getDefault())
                                .format(pickedDate.getTime());
                        binding.dateOfBirth.setText(result);
                        birthday = pickedDate;
                    }
                })
                .show());

        binding.btnChangePassword.setOnClickListener(l -> getNavController().navigate(R.id.action_change_password));

        binding.avatar.setOnClickListener(v -> {
            MediaPicker picker = new MediaPicker(activity);
            picker.setOnImagePickedListener(uri -> {
                file = AppFileWriter.getImageFileFromUri(activity, uri);
                if (file != null && file.exists()) {
                    mimeType = activity.getContentResolver().getType(uri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
                    multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    user.setFile(multipartBody);
                }
                ImageLoadingLibrary.useContext(activity)
                        .load(uri)
                        .into(binding.avatar);
            });
            picker.apply();
        });

        binding.btnSaveChanges.setOnClickListener(v -> {
            displayName = binding.edtDisplayName.getEditableText().toString();
            phoneNumber = binding.edtPhoneNumber.getEditableText().toString();
            displayName = binding.edtDisplayName.getEditableText().toString();
            address = binding.edtAddress.getEditableText().toString();

            if (displayName.isEmpty()) {
                makePopup(R.string.response_display_name_empty);
            } else {
                User user = new User();
                user.setUserId(authViewModel.getUser().getValue().getUserId());
                user.setDisplayName(displayName);
                user.setPhone(phoneNumber);
                user.setDateOfBirth(birthday);
                user.setAddress(address);

                authViewModel.editProfile(user.getUserId(), user);
                if (multipartBody != null) {
                    authViewModel.uploadAvatar(user.getUserId(), multipartBody);
                }
            }
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        authViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                ImageLoadingLibrary.useContext(activity)
                        .load(user.getAvatar().getData())
                        .into(binding.avatar);
                if (user.getDateOfBirth() != null) {
                    SimpleDateFormat appSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String birthday = appSdf.format(user.getDateOfBirth());
                    binding.dateOfBirth.setText(birthday);
                }
                binding.setUser(user);
            }
        });

        authViewModel.getEditingProfileResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makePopup(R.string.response_edit_profile_success);
                    authViewModel.fetch();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        authViewModel.getUploadingAvatarResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    authViewModel.fetch();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });
    }
}