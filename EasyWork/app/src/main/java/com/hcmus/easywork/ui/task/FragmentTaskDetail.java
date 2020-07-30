package com.hcmus.easywork.ui.task;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.TaskRepository;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentTaskDetailBinding;
import com.hcmus.easywork.databinding.MemberInputDialogBinding;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.models.task.SingleTaskViewModel;
import com.hcmus.easywork.ui.chat.mention.AdapterSuggestedPeople;
import com.hcmus.easywork.ui.chat.mention.SuggestedPeople;
import com.hcmus.easywork.ui.common.AdapterComment;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.file.MediaPicker;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.utils.GridSpacingItemDecoration;
import com.hcmus.easywork.viewmodels.comment.CommentViewModel;
import com.hcmus.easywork.viewmodels.member.MemberViewModel;
import com.hcmus.easywork.viewmodels.news.NewsViewModel;
import com.hcmus.easywork.viewmodels.project.ProjectViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;
import com.hcmus.easywork.viewmodels.task.TaskViewModel;
import com.hcmus.easywork.views.PopupMenuDialog;
import com.hcmus.easywork.views.TextInputDialog;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.microsoft.officeuifabric.persona.IPersona;
import com.microsoft.officeuifabric.persona.Persona;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FragmentTaskDetail extends BaseFragment<FragmentTaskDetailBinding>
        implements SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener {
    private TaskViewModel taskViewModel;
    private AdapterComment adapterComment;
    private CommentViewModel commentViewModel;
    private NewsViewModel newsViewModel;
    private SingleTaskViewModel singleTaskViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private ProjectViewModel projectViewModel;
    private MemberViewModel memberViewModel;
    private PopupMenuDialog priorityMenuDialog, stateMenuDialog;
    private TaskRepository taskRepository;
    private Task task;
    private Project project;
    private ProjectMember member;
    private Date dueDate;
    private String name, description;
    private Task.Priority priority;
    private Task.State state;
    private int userId;
    private String memberName;
    private Comment comment;
    private ArrayList<IPersona> samplePersonas;
    private AdapterSuggestedPeople adapterSuggestedPeople;
    private MemberInputDialogBinding memberInputDialogBinding;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_task_detail;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Set windowSoftInputMode to ADJUST_RESIZE to show section comment while keyboard is displayed
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDetach() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = createViewModel(TaskViewModel.class);
        commentViewModel = createViewModel(CommentViewModel.class);
        adapterComment = new AdapterComment(activity);
        singleTaskViewModel = createViewModel(SingleTaskViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        projectViewModel = new ViewModelProvider(activity).get(ProjectViewModel.class);
        newsViewModel = createViewModel(NewsViewModel.class);
        memberViewModel = createViewModel(MemberViewModel.class);
        memberViewModel.fetch(singleProjectViewModel.getProjectId());
        taskRepository = new TaskRepository();
        task = new Task();
        project = new Project();
        comment = new Comment();
        samplePersonas = new ArrayList<>();
        adapterSuggestedPeople = new AdapterSuggestedPeople(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());
        binding.toolbar.setOnMenuItemClickListener(this);
        setupMention();

        comment.setUserId(taskViewModel.getUserId());
        comment.setTaskId(singleTaskViewModel.getTaskId());

        //update task name
        new TextInputDialog.Builder()
                .setConnectedView(binding.taskName)
                .setOnTextSubmitted(text -> {
                    if (!name.equals(text)) {
                        task.setName(text);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_name) + " " + text);
                        comment.setContentVi(getString(R.string.change_name) + " " + text);
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_name) + " " + text, getString(R.string.change_name) + " " + text);
                        }
                    }
                })
                .bind();

        //update task description
        new TextInputDialog.Builder()
                .setConnectedView(binding.txtDescription)
                .setOnTextSubmitted(text -> {
                    if (!text.equals(description)) {
                        task.setDescription(text);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_description));
                        comment.setContentVi(getString(R.string.change_description));
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_description), getString(R.string.change_description));
                        }
                    }
                })
                .bind();

        //update task due date
        binding.dueDate.setOnDatePickedListener(pickedDate -> {
            if (dueDate.compareTo(pickedDate) != 0) {
                if (true) {
                    task.setDueDate(pickedDate);
                    comment.setCommentType(Comment.CommentType.ACTIVITY);
                    comment.setContentEn(getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    comment.setContentVi(getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    commentViewModel.createComment(comment);
                    adapterComment.notifyDataSetChanged();

                    singleTaskViewModel.updateTask(task.getTaskId(), task);
                    if (task.getUserId() != taskViewModel.getUserId()) {
                        createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'",
                                getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    }
                } else {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_due_date_less_than_start_date)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                }
            }
            singleProjectViewModel.fetch();
            return true;
        });

        //update assign member
        binding.avatar.setOnClickListener(v -> {
            memberInputDialogBinding = MemberInputDialogBinding.inflate(LayoutInflater.from(v.getContext()));
            memberInputDialogBinding.peoplePickerSelect.setAvailablePersonas(samplePersonas);
            memberInputDialogBinding.peoplePickerSelect.setPersonaChipLimit(1);
            memberInputDialogBinding.peoplePickerSelect.setAllowPersonaChipDragAndDrop(true);
            new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.assignee)
                    .setView(memberInputDialogBinding.getRoot())
                    .setPositiveButton(R.string.string_assign, (dialog, which) -> {
                        for (ProjectMember member : singleProjectViewModel.getLoadingMemberResult().getValue().getResult()) {
                            if (memberInputDialogBinding.peoplePickerSelect.getPickedPersonas().get(0).getName().equals(member.getUser().getDisplayName())) {
                                if (userId == 0) {
                                    binding.tvAssign.setVisibility(View.VISIBLE);
                                    binding.assigned.setVisibility(View.VISIBLE);
                                    binding.tvUnassigned.setVisibility(View.GONE);
                                }
                                task.setUserId(member.getUserId());
                                UserDataLookup.find(member.getUserId()).setOnRecordFoundListener(userRecord -> {
                                    memberName = userRecord.getName();
                                    binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
                                });

                                comment.setCommentType(Comment.CommentType.ACTIVITY);
                                comment.setContentEn(getString(R.string.assigned).toLowerCase() + " " + memberName);
                                comment.setContentVi(getString(R.string.assigned).toLowerCase() + " " + memberName);
                                commentViewModel.createComment(comment);
                                adapterComment.notifyDataSetChanged();

                                if (task.getUserId() != taskViewModel.getUserId()) {
                                    createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.assign_to_task).toLowerCase(),
                                            getString(R.string.assign_to_task).toLowerCase());
                                }
                                singleTaskViewModel.updateTask(task.getTaskId(), task);
                                singleProjectViewModel.fetch();
                                break;
                            }
                        }
                    })
                    .setNegativeButton(R.string.string_cancel, (dialog, which) -> {
                    })
                    .show();
        });

        //update task priority
        priorityMenuDialog = new PopupMenuDialog(activity, R.menu.menu_select_priority, binding.priorityWidget);
        priorityMenuDialog.setViewGravity(Gravity.END | Gravity.BOTTOM);
        priorityMenuDialog.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_select_priority_low: {
                    binding.priorityWidget.setValue(Task.Priority.Low);
                    if (!priority.equals(Task.Priority.Low)) {
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        comment.setContentVi(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'",
                                    getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        }
                        task.setPriority(Task.Priority.Low);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                    }
                    return true;
                }
                case R.id.action_select_priority_medium: {
                    binding.priorityWidget.setValue(Task.Priority.Medium);
                    if (!priority.equals(Task.Priority.Medium)) {
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        comment.setContentVi(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'",
                                    getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        }
                        task.setPriority(Task.Priority.Medium);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                    }
                    return true;
                }
                case R.id.action_select_priority_high: {
                    binding.priorityWidget.setValue(Task.Priority.High);
                    if (!priority.equals(Task.Priority.High)) {
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        comment.setContentVi(getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'",
                                    getString(R.string.change_priority) + " " + "'" + binding.priorityWidget.getValue().toString() + "'");
                        }
                        task.setPriority(Task.Priority.High);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                    }
                    return true;
                }
            }
            return false;
        });
        binding.priorityWidget.setOnClickListener(v -> priorityMenuDialog.show());


        //update task state
        stateMenuDialog = new PopupMenuDialog(activity, R.menu.menu_select_task_tate, binding.stateWidget);
        stateMenuDialog.setViewGravity(Gravity.END | Gravity.BOTTOM);
        stateMenuDialog.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_select_test_state_new: {
                    return true;
                }
                case R.id.action_select_test_state_active: {
                    if (state.equals(Task.State.New) || (state.equals(Task.State.Reviewing) && member.isLeader())) {
                        binding.stateWidget.setState(Task.State.Active);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        comment.setContentVi(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'",
                                    getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        }
                        task.setState(Task.State.Active);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        singleProjectViewModel.fetchMembers();
                    }
                    return true;
                }
                case R.id.action_select_test_state_reviewing: {
                    if (state.equals(Task.State.Active)) {
                        binding.stateWidget.setState(Task.State.Reviewing);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        comment.setContentVi(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'",
                                    getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        }
                        task.setState(Task.State.Reviewing);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        singleProjectViewModel.fetchMembers();
                    }
                    return true;
                }
                case R.id.action_select_test_state_resolved: {
                    if (state.equals(Task.State.Reviewing) && member.isLeader()) {
                        binding.stateWidget.setState(Task.State.Resolved);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        comment.setContentVi(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'",
                                    getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        }
                        task.setState(Task.State.Resolved);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        singleProjectViewModel.fetchMembers();
                    }
                    return true;
                }
                case R.id.action_select_test_state_closed: {
                    if (state.equals(Task.State.Resolved)) {
                        binding.stateWidget.setState(Task.State.Closed);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        comment.setContentVi(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'",
                                    getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        }
                        task.setState(Task.State.Closed);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        singleProjectViewModel.fetchMembers();
                    }
                    return true;
                }
                case R.id.action_select_test_state_overdue: {
                    if (state.equals(Task.State.Closed)) {
                        binding.stateWidget.setState(Task.State.Overdue);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        comment.setContentVi(getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        if (task.getUserId() != taskViewModel.getUserId()) {
                            createNews(task.getUserId(), News.NewsType.NORMAL, getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'",
                                    getString(R.string.change_state) + " " + "'" + binding.stateWidget.getState().toString() + "'");
                        }
                        task.setState(Task.State.Overdue);
                        singleTaskViewModel.updateTask(task.getTaskId(), task);
                        singleProjectViewModel.fetchMembers();
                    }
                    return true;
                }
            }
            return false;
        });
        binding.stateWidget.setOnClickListener(v -> stateMenuDialog.show());

        //send photo
        binding.wgComment.setOnPhotoAttachedListener(() -> {
            MediaPicker picker = new MediaPicker(activity);
            picker.setOnImagePickedListener(uri -> {
                File file = AppFileWriter.getImageFileFromUri(activity, uri);
                if (file != null && file.exists()) {
                    String mimeType = activity.getContentResolver().getType(uri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    commentViewModel.createImageTaskComment(singleTaskViewModel.getTaskId(), singleTaskViewModel.getUserId(), 3, multipartBody);
                }
            });
            picker.apply();
        });

        //send file
        binding.wgComment.setOnFileAttachedListener(() -> {
            MediaPicker mediaPicker = new MediaPicker(activity);
            mediaPicker.setOnFilePickedListener(uri -> {
                String path = uri.getPath();
                if (path != null) {
                    path = path.replace("/document/raw:", "");
                    File file = new File(path);
                    String mimeType = activity.getContentResolver().getType(uri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    commentViewModel.createImageTaskComment(singleTaskViewModel.getTaskId(), singleTaskViewModel.getUserId(), 3, multipartBody);
                }
            });
            mediaPicker.apply();
        });

        //send text comment
        binding.wgComment.setOnCommentListener(commentText -> {
            comment.setCommentType(Comment.CommentType.TEXT);
            comment.setContentEn(commentText);
            comment.setContentVi(commentText);
            commentViewModel.createComment(comment);
            adapterComment.notifyDataSetChanged();
            return true;
        });

        //show mention
        binding.wgComment.setOnMentionQueriedListener(queryResult -> {
            adapterSuggestedPeople.submitList(queryResult);
            binding.rvMentions.swapAdapter(adapterSuggestedPeople, true);
            boolean display = queryResult.size() > 0;
            showSuggestions(display);
        });

        binding.wgComment.setSuggestionsVisibilityManager(new SuggestionsVisibilityManager() {
            @Override
            public void displaySuggestions(boolean display) {
                showSuggestions(display);
            }

            @Override
            public boolean isDisplayingSuggestions() {
                return binding.rvMentions.getVisibility() == RecyclerView.VISIBLE;
            }
        });

        final int spacing = getResources().getDimensionPixelSize(R.dimen.margin_medium);
        binding.rvCommentsNew.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));

        adapterComment.setOnClickDeleteListener(((object, position) ->
                commentViewModel.deleteComment(object.getCommentId())));

        commentViewModel.getTaskComment(singleTaskViewModel.getTaskId());
        singleProjectViewModel.fetchMembers();
    }

    private void setupMention() {
        adapterSuggestedPeople.setOnClickListener((object, position) -> {
            binding.wgComment.insertMention(object);
            binding.rvMentions.swapAdapter(new AdapterSuggestedPeople(activity), true);
            showSuggestions(false);
            binding.wgComment.requestTextFieldFocus();
        });
    }

    private void showSuggestions(boolean display) {
        binding.rvMentions.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    private List<SuggestedPeople> getSuggestedPeople(List<ProjectMember> members) {
        List<SuggestedPeople> list = new ArrayList<>();
        for (ProjectMember member : members) {
            SuggestedPeople people = new SuggestedPeople();
            people.setUserId(member.getUserId());
            people.setName(member.getUser().getDisplayName());
            list.add(people);
        }
        return list;
    }

    public void createNews(int ownerId, News.NewsType type, String contentVi, String contentEn) {
        News news = new News();
        news.setOwnerId(ownerId);
        news.setExecutorId(taskViewModel.getUserId());
        news.setNewsOfTask(true);
        news.setProjectId(singleProjectViewModel.getProjectId());
        news.setTaskId(singleTaskViewModel.getTaskId());
        news.setNewsType(type);
        news.setContentVi(contentVi);
        news.setContentEn(contentEn);
        newsViewModel.createNews(news);
    }

    private IPersona createPersona(String name, String email, String role) {
        Persona persona = new Persona(name, role);
        persona.setSubtitle(email);
        return persona;
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.setAdapter(adapterComment);
        binding.rvMentions.setAdapter(adapterSuggestedPeople);

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

        singleProjectViewModel.getLoadingSingleMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case LOADED: {
                    member = result.getResult();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        singleProjectViewModel.fetchMembers();
        singleProjectViewModel.getLoadingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(true);
                    break;
                }
                case LOADED: {
                    binding.swipeLayout.setRefreshing(false);
                    samplePersonas.clear();
                    for (ProjectMember member : result.getResult()) {
                        IPersona persona = createPersona(member.getUser().getDisplayName(), member.getUser().getMail(), "");
                        UserDataLookup.find(member.getUserId()).setOnRecordFoundListener(userRecord -> persona.setAvatarImageBitmap(userRecord.getAvatar()));
                        samplePersonas.add(persona);
                    }
                    binding.wgComment.setAvailablePeople(getSuggestedPeople(result.getResult()));
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.swipeLayout.setRefreshing(false);
                    break;
                }
            }
        });

        singleTaskViewModel.fetchTask();
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
                    task = result.getResult();
                    if (task != null) {
                        projectViewModel.getSingleProject(task.getProjectId());
                        project = projectViewModel.getSelectedProject().getValue();
                        task.setProjectName(project.getName());

                        if (task.getUserId() > 0) {
                            UserDataLookup.find(task.getUserId()).setOnRecordFoundListener(userRecord -> {
                                binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
                                binding.assigned.setText(userRecord.getName());
                            });
                        } else {
                            binding.tvAssign.setVisibility(View.GONE);
                            binding.assigned.setVisibility(View.GONE);
                            binding.tvUnassigned.setVisibility(View.VISIBLE);
                        }

                        if (task.getDescription().isEmpty()) {
                            binding.txtDescription.setHint(R.string.hint_enter_task_description);
                        } else {
                            description = task.getDescription();
                        }

                        binding.priorityWidget.setValue(task.getPriority());
                        binding.stateWidget.setState(task.getState());
                        binding.setTask(task);
                        name = task.getName();
                        dueDate = task.getDueDate();
                        priority = task.getPriority();
                        state = task.getState();
                        userId = task.getUserId();
                    }
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

        singleTaskViewModel.getUpdatingTaskResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    singleTaskViewModel.fetchTask();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        commentViewModel.getLoadingTaskCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT:
                case LOADING: {
                    break;
                }
                case LOADED: {
                    adapterComment.submitList(result.getResult());
                    binding.swipeLayout.postDelayed(() -> binding.swipeLayout.setRefreshing(false), 500);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        commentViewModel.getDeletingCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack("Deleted");
                    commentViewModel.getTaskComment(singleTaskViewModel.getTaskId());
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        commentViewModel.getAddingCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    commentViewModel.getTaskComment(singleTaskViewModel.getTaskId());
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        commentViewModel.getAddingImageCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    commentViewModel.getTaskComment(singleTaskViewModel.getTaskId());
                    adapterComment.notifyDataSetChanged();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        newsViewModel.getCreatingNewsResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
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
            binding.swipeLayout.post(() -> commentViewModel.getTaskComment(singleTaskViewModel.getTaskId()));
            binding.swipeLayout.post(() -> singleTaskViewModel.fetchTask());
        }, 500);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_task: {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.request_set_leader)
                        .setPositiveButton(R.string.string_accept, (dialog, which) -> {
                            if (member.isLeader()) {
                                deleteTask(singleTaskViewModel.getTaskId());
                                if (task.getUserId() != singleTaskViewModel.getUserId()) {
                                    createNews(task.getUserId(), News.NewsType.REMOVE, getString(R.string.delete_task), getString(R.string.delete_task));
                                }
                            } else {
                                new MaterialAlertDialogBuilder(activity)
                                        .setMessage(R.string.response_leader_permission)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();
                break;
            }
        }
        return true;
    }

    private void deleteTask(int taskId) {
        this.taskRepository.deleteTask(taskId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                taskViewModel.removeSelected();
                getNavController().navigateUp();
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.response_delete_task_success)
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onFailure(String message) {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.response_delete_task_failed)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
}
