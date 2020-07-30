package com.hcmus.easywork.ui.chat.image;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentSharedImageViewBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.file.EwFile;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;

public class FragmentSharedImageView extends BaseFragment<FragmentSharedImageViewBinding>
        implements MaterialToolbar.OnMenuItemClickListener {
    private SharedImageViewModel sharedImageViewModel;
    private SingleGroupViewModel groupViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_shared_image_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedImageViewModel = createViewModel(SharedImageViewModel.class);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(l -> getNavController().popBackStack());
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        sharedImageViewModel.getFile().observe(getViewLifecycleOwner(), file -> {
            binding.setFile(file);
            ImageLoadingLibrary.useContext(activity)
                    .load(file.mData.data)
                    .into(binding.imageView);
        });

        groupViewModel.getDeletingMessageResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makePopup("Deleted");
                    getNavController().popBackStack();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        EwFile file = sharedImageViewModel.getFile().getValue();
        if (file != null) {
            switch (item.getItemId()) {
                case R.id.action_save_file: {
                    AppFileWriter writer = AppFileWriter.getInstance();
                    writer.setContent(file);
                    writer.write(new AppFileWriter.OnResultListener() {
                        @Override
                        public void onWritten(String fileName) {
                            makePopup(fileName);
                        }

                        @Override
                        public void onFailed(String errorMessage) {
                            makePopup(errorMessage);
                        }
                    });
                    return true;
                }
                case R.id.action_delete_file: {
                    groupViewModel.deleteMessage(file.mMessageId);
                    return true;
                }
            }
        }
        return false;
    }
}
