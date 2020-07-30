package com.hcmus.easywork.ui.chat.detail.personal;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentPersonalChatDetailBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;

public class FragmentPersonalChatDetail extends BaseFragment<FragmentPersonalChatDetailBinding>
        implements MaterialToolbar.OnMenuItemClickListener {
    private SingleGroupViewModel groupViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_personal_chat_detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.getMenu().findItem(R.id.action_show_group_option).setVisible(false);
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());

        binding.btnSharedFiles.setOnClickListener(l -> getNavController().navigate(R.id.action_open_shared_files));
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.get().observe(getViewLifecycleOwner(), group -> {
            int idToLoad;
            if (groupViewModel.getUserId() == group.getCreatorId()) {
                idToLoad = group.getUserId();
            } else {
                idToLoad = group.getCreatorId();
            }
            UserDataLookup.find(idToLoad).setOnRecordFoundListener(userRecord -> binding.setUserRecord(userRecord));
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            getNavController().popBackStack();
            return true;
        }
        return false;
    }
}
