package com.hcmus.easywork.ui.news;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentNewsBinding;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.task.SingleTaskViewModel;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.news.NewsViewModel;
import com.hcmus.easywork.viewmodels.project.ProjectViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;
import com.hcmus.easywork.viewmodels.task.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentNews extends BaseFragment<FragmentNewsBinding>
        implements SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener {
    private AdapterNews adapterNews;
    private NewsViewModel newsViewModel;
    private ProjectViewModel projectViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private TaskViewModel taskViewModel;
    private SingleTaskViewModel singleTaskViewModel;
    private List<Project> projects;
    private List<Task> tasks;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsViewModel = createViewModel(NewsViewModel.class);
        projectViewModel = createViewModel(ProjectViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        taskViewModel = createViewModel(TaskViewModel.class);
        singleTaskViewModel = createViewModel(SingleTaskViewModel.class);
        adapterNews = new AdapterNews(activity);
        projects = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.swipeLayout.setOnRefreshListener(this);

        adapterNews.setOnClickListener((object, position) -> {
            if (!object.getNewsType().equals(News.NewsType.REMOVE)) {
                if (object.isNewsOfTask()) {
                    taskViewModel.getSingleTask(object.getTaskId());
                    Task task = taskViewModel.getSelectedTask().getValue();
                    for (Task item : tasks) {
                        if (item.getTaskId() == object.getTaskId()) {
                            singleTaskViewModel.setTask(task);
                            getNavController().navigate(R.id.action_view_task_detail);
                        }
                    }
                } else {
                    if (object.getNewsType().equals(News.NewsType.ARCHIVE)) {
                        getNavController().navigate(R.id.action_view_projects);
                    }
                    projectViewModel.getSingleProject(object.getProjectId());
                    Project project = projectViewModel.getSelectedProject().getValue();
                    for (Project item : projects) {
                        if (item.getProjectId() == object.getProjectId()) {

                            singleProjectViewModel.setProject(project);
                            getNavController().navigate(R.id.action_view_project_detail);
                        }
                    }
                }
            }
        });

        adapterNews.setOnClickMarkAsReadListener(((object, position) -> {
            if (!object.isWatched()) {
                newsViewModel.markAsReadNews(object.getNewsId());
            }
        }));

        adapterNews.setOnClickDeleteListener(((object, position) ->
                new MaterialAlertDialogBuilder(activity)
                .setMessage(R.string.request_delete_news)
                .setPositiveButton(R.string.string_accept, (dialog, which) -> newsViewModel.deleteNews(object.getNewsId()))
                .setNegativeButton(R.string.string_cancel, null)
                .show()));

        newsViewModel.getNews();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.setAdapter(adapterNews);
        newsViewModel.getLoadingNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    adapterNews.setList(result.getResult());
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

        newsViewModel.getMarkAsReadNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack(R.string.response_mark_news);
                    newsViewModel.getNews();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        newsViewModel.getMarkAllAsReadNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack(R.string.response_mark_all_news);
                    newsViewModel.getNews();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        newsViewModel.getDeletingNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack(R.string.response_delete);
                    newsViewModel.getNews();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        newsViewModel.getDeletingAllNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack(R.string.response_delete);
                    newsViewModel.getNews();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
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
                    projects = result.getResult();
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

        taskViewModel.getAllTask();
        taskViewModel.getLoadingAllTaskResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    tasks = result.getResult();
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
            }
        });

        singleTaskViewModel.getLoadingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        binding.swipeLayout.post(() -> newsViewModel.getNews());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_all: {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.request_delete_all_news)
                        .setPositiveButton(R.string.string_accept, (dialog, which) -> newsViewModel.deleteAllNews())
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();
                break;
            }
            case R.id.action_read_all: {
                newsViewModel.markAllAsReadNews();
                break;
            }
            case R.id.action_filter_all_news: {
                adapterNews.filter(null);
                item.setChecked(true);
                break;
            }
            case R.id.action_filter_mention_news: {
                adapterNews.filter(News.NewsType.MENTION);
                item.setChecked(true);
                break;
            }
            case R.id.action_filter_assigned_news: {
                adapterNews.filter(News.NewsType.ASSIGN);
                item.setChecked(true);
                break;
            }
        }
        return true;
    }
}
