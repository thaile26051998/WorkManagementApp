package com.hcmus.easywork.ui.chat.group;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentStartConversationBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.viewmodels.project.ProjectViewModel;
import com.hcmus.easywork.viewmodels.chat.ConversationViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

public class FragmentStartConversation extends BaseFragment<FragmentStartConversationBinding>
        implements SwipeRefreshLayout.OnRefreshListener {
    private ProjectViewModel projectViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private ConversationViewModel conversationViewModel;
    private StartChatViewPagerAdapter viewPagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_start_conversation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectViewModel = createViewModel(ProjectViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        conversationViewModel = createViewModel(ConversationViewModel.class);
        viewPagerAdapter = new StartChatViewPagerAdapter(activity, activity.getSupportFragmentManager());
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.toolbar.setNavigationOnClickListener(l -> getNavController().navigateUp());

        binding.viewPager.setAdapter(viewPagerAdapter);

        projectViewModel.fetch();

        binding.ddlProjects.setOnItemSelectedListener(item -> {
            singleProjectViewModel.setProject(item);
            singleProjectViewModel.fetchMembers();
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        projectViewModel.getLoadingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    binding.ddlProjects.setSourceData(result.getResult(), Project::getName);
                    binding.swipeLayout.setRefreshing(false);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.swipeLayout.setRefreshing(false);
                    break;
                }
            }
        });

        singleProjectViewModel.getLoadingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED:
                case FAILED: {
                    binding.swipeLayout.setRefreshing(false);
                    break;
                }
            }
        });

        conversationViewModel.getCreatingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    // TODO: customize message
                    makePopup("Group created", (dialog, which) -> getNavController().popBackStack());
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
    public void onRefresh() {
        binding.swipeLayout.postDelayed(() -> {
            projectViewModel.fetch();
            binding.swipeLayout.setRefreshing(false);
        }, 500);
    }
}
