package com.hcmus.easywork.ui.task;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentTasksBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.task.SingleTaskViewModel;
import com.hcmus.easywork.viewmodels.task.TaskViewModel;

public class FragmentTasks extends BaseFragment<FragmentTasksBinding>
        implements SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener,
        SearchView.OnQueryTextListener {
    private AdapterTask adapterTask;
    private TaskViewModel taskViewModel;
    private SingleTaskViewModel singleTaskViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tasks;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = createViewModel(TaskViewModel.class);
        singleTaskViewModel = createViewModel(SingleTaskViewModel.class);
        adapterTask = new AdapterTask(this.activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.toolbar.setOnMenuItemClickListener(this);

        ((SearchView) binding.toolbar.getMenu().findItem(R.id.action_search).getActionView())
                .setOnQueryTextListener(this);

        adapterTask.setOnClickListener((object, position) -> {
            singleTaskViewModel.setTask(object);
            getNavController().navigate(R.id.action_view_task_detail);
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.rvTasks.setAdapter(adapterTask);

        taskViewModel.getMyTask();
        taskViewModel.getLoadingMyTaskResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    adapterTask.setList(result.getResult());
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
    }

    @Override
    public void onRefresh() {
        binding.swipeLayout.post(() -> {
            // Fetch tasks
            taskViewModel.fetchMyTasks();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int groupId = item.getGroupId();
        int id = item.getItemId();
        if (groupId == R.id.group_filter || groupId == R.id.group_sort) {
            item.setChecked(true);
        }

        // Filter state
        switch (id) {
            case R.id.filter_all: {
                adapterTask.filterState(null);
                return true;
            }
            case R.id.filter_new: {
                adapterTask.filterState(Task.State.New);
                return true;
            }
            case R.id.filter_active: {
                adapterTask.filterState(Task.State.Active);
                return true;
            }
            case R.id.filter_reviewing: {
                adapterTask.filterState(Task.State.Reviewing);
                return true;
            }
            case R.id.filter_resolved: {
                adapterTask.filterState(Task.State.Resolved);
                return true;
            }
            case R.id.filter_closed: {
                adapterTask.filterState(Task.State.Closed);
                return true;
            }
            case R.id.filter_overdue: {
                adapterTask.filterState(Task.State.Overdue);
                return true;
            }
        }

        // Filter priority
        switch (id) {
            case R.id.filter_all_priority: {
                adapterTask.filterPriority(null);
                return true;
            }
            case R.id.filter_low: {
                adapterTask.filterPriority(Task.Priority.Low);
                item.setChecked(true);
                return true;
            }
            case R.id.filter_medium: {
                adapterTask.filterPriority(Task.Priority.Medium);
                item.setChecked(true);
                return true;
            }
            case R.id.filter_high: {
                adapterTask.filterPriority(Task.Priority.High);
                item.setChecked(true);
                return true;
            }
        }

        // Sort
        switch (id) {
            case R.id.action_sort_by_name: {
                adapterTask.sortByName();
                return true;
            }
            case R.id.action_sort_by_due_date: {
                adapterTask.sortByDueDate();
                return true;
            }
            case R.id.action_sort_by_state: {
                adapterTask.sortByState();
                return true;
            }
            case R.id.action_sort_by_priority: {
                adapterTask.sortByPriority();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapterTask.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapterTask.getFilter().filter(newText);
        return false;
    }
}
