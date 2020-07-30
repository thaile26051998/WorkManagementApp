package com.hcmus.easywork.ui.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentProjectsBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.twilio.shared.TwilioViewModel;
import com.hcmus.easywork.viewmodels.project.ProjectViewModel;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

public class FragmentProjects extends BaseFragment<FragmentProjectsBinding>
        implements SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener,
        SearchView.OnQueryTextListener {
    private AdapterProject adapterProject;
    private AdapterProject adapterArchivedProject;
    private ProjectViewModel projectViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private AuthenticationViewModel authViewModel;
    private TwilioViewModel twilioViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_projects;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = createViewModel(AuthenticationViewModel.class);

        authViewModel = createViewModel(AuthenticationViewModel.class);
        projectViewModel = createViewModel(ProjectViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        twilioViewModel = createViewModel(TwilioViewModel.class);
        adapterProject = new AdapterProject(activity);
        adapterArchivedProject = new AdapterProject(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.appToolbar.setOnMenuItemClickListener(this);
        ((SearchView) binding.toolbar.getMenu().findItem(R.id.action_search).getActionView())
                .setOnQueryTextListener(this);

        binding.avatar.setOnClickListener(v -> getNavController().navigate(R.id.action_view_profile));

        adapterProject.setOnClickListener((object, position) -> {
            singleProjectViewModel.setProject(object);
            singleProjectViewModel.fetchMembers();
            getNavController().navigate(R.id.action_view_project_detail);
        });

        adapterArchivedProject.setOnClickListener((object, position) -> {
            singleProjectViewModel.setProject(object);
            createOkCancelDialog(R.string.request_unarchive_project, (dialog, which) -> {
                singleProjectViewModel.toggleArchiveProject(false);
                projectViewModel.fetch();
                projectViewModel.fetchArchiveProject();
                onRefresh();
            }, null).show();
        });

        projectViewModel.fetch();
        projectViewModel.fetchArchiveProject();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.rvProjects.setAdapter(adapterProject);
        binding.rvArchivedProjects.setAdapter(adapterArchivedProject);

        authViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.setUser(user);
            }
        });

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
                    adapterProject.setList(result.getResult());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
            }
        });

        projectViewModel.getLoadingArchiveResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    adapterArchivedProject.setList(result.getResult());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
            }
        });

        twilioViewModel.isVoiceAvailable.observe(getViewLifecycleOwner(), available -> binding.setVoiceCallAvail(available));
        twilioViewModel.isVideoAvailable.observe(getViewLifecycleOwner(), available -> binding.setVideoCallAvail(available));
    }

    @Override
    public void onRefresh() {
        binding.swipeLayout.post(() -> {
            projectViewModel.fetch();
            projectViewModel.fetchArchiveProject();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getGroupId() == R.id.group_sort) {
            item.setChecked(true);
        }
        switch (item.getItemId()) {
            case R.id.action_add_project: {
                getNavController().navigate(R.id.action_create_project);
                return true;
            }
            case R.id.action_sort_by_name: {
                adapterProject.sortByName();
                adapterArchivedProject.sortByName();
                return true;
            }
            case R.id.action_sort_by_due_date: {
                adapterProject.sortByDueDate();
                adapterArchivedProject.sortByDueDate();
                return true;
            }
            case R.id.action_preferences: {
                getNavController().navigate(R.id.action_view_preferences);
                return true;
            }
            case R.id.action_about: {
                getNavController().navigate(R.id.action_view_about);
                return true;
            }
            case R.id.action_log_out: {
                authViewModel.logout();
                getNavController().navigate(R.id.action_authenticate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapterProject.getFilter().filter(query);
        adapterArchivedProject.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterProject.getFilter().filter(newText);
        adapterArchivedProject.getFilter().filter(newText);
        return false;
    }
}
