package com.hcmus.easywork.ui.chat.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentSharedFilesBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;

public class FragmentSharedFiles extends BaseFragment<FragmentSharedFilesBinding> {
    private SingleGroupViewModel groupViewModel;
    private AdapterSharedFiles adapterSharedFiles;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_shared_files;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        adapterSharedFiles = new AdapterSharedFiles(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());
        binding.rvSharedFiles.setAdapter(adapterSharedFiles);

        adapterSharedFiles.setOnClickListener((object, position) -> {
            AppFileWriter writer = AppFileWriter.getInstance();
            writer.setContent(object);
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
        });
        groupViewModel.getAllFiles();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.getLoadingFilesResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT:
                case LOADING:
                    break;
                case LOADED: {
                    adapterSharedFiles.submitList(result.getResult());
                    int resultSize = result.getResult().size();
                    binding.toolbar.setSubtitle(getResources().
                            getQuantityString(R.plurals.shared_files_count, resultSize, resultSize));
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.toolbar.setSubtitle(getResources()
                            .getQuantityString(R.plurals.shared_files_count, 0, 0));
                    break;
                }
            }
        });
    }
}
