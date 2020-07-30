package com.hcmus.easywork.ui.chat.detail.group;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentGroupDetailBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;

public class FragmentGroupDetail extends BaseFragment<FragmentGroupDetailBinding>
        implements MaterialToolbar.OnMenuItemClickListener {
    private SingleGroupViewModel groupViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_group_detail;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());

        binding.btnManageMembers.setOnClickListener(l -> getNavController().navigate(R.id.action_open_list_members));
        binding.btnPinned.setOnClickListener(l -> getNavController().navigate(R.id.action_open_pinned_messages));
        binding.btnSharedFiles.setOnClickListener(l -> getNavController().navigate(R.id.action_open_shared_files));
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.get().observe(getViewLifecycleOwner(), group -> {
            binding.setGroup(group);
            UserDataLookup.find(group.getCreatorId()).setOnRecordFoundListener(userRecord ->
                    binding.edtCreator.setText(userRecord.getName()));
        });

        groupViewModel.getDeletingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    getNavController().navigate(R.id.action_exit_group);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getDeletingGroupResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    getNavController().navigate(R.id.action_exit_group);
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
        switch (item.getItemId()) {
            case R.id.action_save: {
                getNavController().popBackStack();
                return true;
            }
            case R.id.action_leave_group: {
                groupViewModel.leaveGroup();
                return true;
            }
            case R.id.action_archive_group: {
                groupViewModel.archiveGroup();
                return true;
            }
        }
        return false;
    }
}
