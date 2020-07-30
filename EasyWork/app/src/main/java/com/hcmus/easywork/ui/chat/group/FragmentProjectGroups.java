package com.hcmus.easywork.ui.chat.group;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentProjectGroupsBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.chat.AdapterConversation;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

public class FragmentProjectGroups extends BaseFragment<FragmentProjectGroupsBinding> {
    private SingleProjectViewModel projectViewModel;
    private AdapterConversation adapterConversation;
    private SingleGroupViewModel groupViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_project_groups;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectViewModel = createViewModel(SingleProjectViewModel.class);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        adapterConversation = new AdapterConversation(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());

        adapterConversation.setOnClickListener((object, position) -> {
            groupViewModel.setGroup(object);
            getNavController().navigate(R.id.action_open_chat);
        });

        binding.rvGroups.setAdapter(adapterConversation);
        projectViewModel.getGroups();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        projectViewModel.getLoadingGroupsResult().observe(getViewLifecycleOwner(), loadingGroupsResult -> {
            switch (loadingGroupsResult.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    adapterConversation.submitList(loadingGroupsResult.getResult());
                    break;
                }
                case FAILED: {
                    makePopup(loadingGroupsResult.getErrorMessage());
                    break;
                }
            }
        });
    }
}
