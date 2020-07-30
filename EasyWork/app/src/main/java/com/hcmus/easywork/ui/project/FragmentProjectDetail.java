package com.hcmus.easywork.ui.project;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentProjectDetailBinding;
import com.hcmus.easywork.databinding.MemberInputDialogBinding;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.models.task.SingleTaskViewModel;
import com.hcmus.easywork.ui.chat.image.SharedImageViewModel;
import com.hcmus.easywork.ui.chat.mention.AdapterSuggestedPeople;
import com.hcmus.easywork.ui.chat.mention.SuggestedPeople;
import com.hcmus.easywork.ui.common.AdapterComment;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.file.MediaPicker;
import com.hcmus.easywork.ui.task.AdapterTask;
import com.hcmus.easywork.utils.AppFileWriter;
import com.hcmus.easywork.utils.GridSpacingItemDecoration;
import com.hcmus.easywork.viewmodels.auth.User;
import com.hcmus.easywork.viewmodels.auth.UserViewModel;
import com.hcmus.easywork.viewmodels.comment.CommentViewModel;
import com.hcmus.easywork.viewmodels.news.NewsViewModel;
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

public class FragmentProjectDetail extends BaseFragment<FragmentProjectDetailBinding>
        implements SwipeRefreshLayout.OnRefreshListener, Toolbar.OnMenuItemClickListener {
    private AdapterTask adapterTask;
    private AdapterMember adapterMember;
    private AdapterComment adapterComment;
    private TaskViewModel taskViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private SingleTaskViewModel singleTaskViewModel;
    private CommentViewModel commentViewModel;
    private NewsViewModel newsViewModel;
    private UserViewModel userViewModel;
    private SharedImageViewModel sharedImageViewModel;
    private List<ProjectMember> members;
    private List<User> users;
    private ProjectMember member;
    private Project project;
    private Date startDate, dueDate;
    private String name, leader;
    private String description = null;
    private Comment comment;
    private Project.State state;
    private ArrayList<IPersona> samplePersonas, memberSamplePersonas;
    private AdapterSuggestedPeople adapterSuggestedPeople;
    private MemberInputDialogBinding memberInputDialogBinding;
    private String memberName;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_project_detail;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        userViewModel = createViewModel(UserViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        singleTaskViewModel = createViewModel(SingleTaskViewModel.class);
        taskViewModel = createViewModel(TaskViewModel.class);
        commentViewModel = createViewModel(CommentViewModel.class);
        newsViewModel = createViewModel(NewsViewModel.class);
        sharedImageViewModel = createViewModel(SharedImageViewModel.class);
        singleProjectViewModel.getSingleMember();
        adapterTask = new AdapterTask(activity);
        adapterMember = new AdapterMember(activity);
        adapterComment = new AdapterComment(activity);
        project = new Project();
        comment = new Comment();
        members = new ArrayList<>();
        samplePersonas = new ArrayList<>();
        memberSamplePersonas = new ArrayList<>();
        users = new ArrayList<>();
        adapterSuggestedPeople = new AdapterSuggestedPeople(activity);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.swipeLayout.setOnRefreshListener(this);
        onRefresh();
        binding.toolbar.setOnMenuItemClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().popBackStack());
        setupMention();
        binding.rvMember.setAdapter(adapterMember);
        binding.rvTasks.setAdapter(adapterTask);
        binding.rvCommentsNew.setAdapter(adapterComment);

        //open image
        adapterComment.setOnSharedImageClickListener((object, position) -> {
//            EwFile file = new EwFile();
//            file.setData(comment.getFileData());
//
//            file.setmSize(comment.getFileSize());
//            file.setName(comment.getFileName());
//            file.setType(comment.getFileType());
//
//            sharedImageViewModel.selectFile(file);
//            getNavController().navigate(R.id.action_view_shared_image);
        });

        //open file
        adapterComment.setOnSharedFileClickListener((object, position) -> {

        });

        comment.setUserId(singleProjectViewModel.getUserId());
        comment.setProjectId(singleProjectViewModel.getProjectId());

        //update project name
        new TextInputDialog.Builder()
                .setConnectedView(binding.txtProjectName)
                .setOnTextSubmitted(text -> {
                    if (!name.equals(text)) {
                        project.setName(text);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_name) + " " + text);
                        comment.setContentVi(getString(R.string.change_name) + " " + text);
                        commentViewModel.createComment(comment);
                        adapterComment.notifyDataSetChanged();

                        createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_name) + " " + text, getString(R.string.change_name) + " " + text);
                        singleProjectViewModel.updateProject(project.getProjectId(), project);
                    }
                })
                .bind();

        //update project description
        new TextInputDialog.Builder()
                .setConnectedView(binding.txtDescription)
                .setOnTextSubmitted(text -> {
                    if (!text.equals(description)) {
                        project.setDescription(text);
                        comment.setCommentType(Comment.CommentType.ACTIVITY);
                        comment.setContentEn(getString(R.string.change_description));
                        comment.setContentVi(getString(R.string.change_description));
                        commentViewModel.createComment(comment);
                        createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_description), getString(R.string.change_description));
                        singleProjectViewModel.updateProject(project.getProjectId(), project);
                    }
                })
                .bind();

        //update start date
        binding.startDate.setOnDatePickedListener(pickedDate -> {
            if (startDate.compareTo(pickedDate) != 0) {
                if (pickedDate.compareTo(dueDate) < 0) {
                    project.setStartDate(pickedDate);
                    comment.setCommentType(Comment.CommentType.ACTIVITY);
                    comment.setContentEn(getString(R.string.change_start_date) + " " + "'" + binding.startDate.getCurrentDateString(pickedDate) + "'");
                    comment.setContentVi(getString(R.string.change_start_date) + " " + "'" + binding.startDate.getCurrentDateString(pickedDate) + "'");
                    commentViewModel.createComment(comment);

                    createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_start_date) + " " + "'" + binding.startDate.getCurrentDateString(pickedDate) + "'",
                            getString(R.string.change_start_date) + " " + "'" + binding.startDate.getCurrentDateString(pickedDate) + "'");
                    singleProjectViewModel.updateProject(project.getProjectId(), project);
                } else {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_due_date_less_than_start_date)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                }
            }
            return true;
        });

        //update due date
        binding.dueDate.setOnDatePickedListener(pickedDate -> {
            if (dueDate.compareTo(pickedDate) != 0) {
                if (pickedDate.compareTo(startDate) > 0) {
                    project.setDueDate(pickedDate);
                    comment.setCommentType(Comment.CommentType.ACTIVITY);
                    comment.setContentEn(getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    comment.setContentVi(getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    commentViewModel.createComment(comment);
                    createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'",
                            getString(R.string.change_due_date) + " " + "'" + binding.dueDate.getCurrentDateString(pickedDate) + "'");
                    singleProjectViewModel.updateProject(project.getProjectId(), project);
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

        //navigate to task detail
        adapterTask.setOnClickListener((object, position) -> {
            singleTaskViewModel.setTask(object);
            getNavController().navigate(R.id.action_view_task_detail);
        });

        binding.rvTasks.expand();

        //send text comment
        binding.wgComment.setOnCommentListener(commentText -> {
            comment.setCommentType(Comment.CommentType.TEXT);
            comment.setContentEn(commentText);
            comment.setContentVi(commentText);
            commentViewModel.createComment(comment);
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

        //send photo
        binding.wgComment.setOnPhotoAttachedListener(() -> {
            MediaPicker mediaPicker = new MediaPicker(activity);
            mediaPicker.setOnImagePickedListener(uri -> {
                File file = AppFileWriter.getImageFileFromUri(activity, uri);
                if (file != null && file.exists()) {
                    String mimeType = activity.getContentResolver().getType(uri);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    commentViewModel.createImageComment(singleProjectViewModel.getProjectId(), singleProjectViewModel.getUserId(), 3, multipartBody);
                }
            });
            mediaPicker.apply();
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
                    commentViewModel.createImageComment(singleProjectViewModel.getProjectId(), singleProjectViewModel.getUserId(), 3, multipartBody);
                }
            });
            mediaPicker.apply();
        });

        //remove member
        adapterMember.setOnClickRemoveMemberListener(((object, position) -> {
            if (member.isLeader()) {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.request_remove_member)
                        .setPositiveButton(R.string.string_accept, (dialog, which) -> {
                            if (member.getUserId() != object.getUserId()) {
                                singleProjectViewModel.removeMember(object.getUserId());
//                                    adapterMember.notifyItemChanged(position);
                                createNews(object.getUserId(), News.NewsType.REMOVE, getString(R.string.remove_member), getString(R.string.remove_member));
                            }
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();
            }
        }));

        //set leader
        binding.avatar.setOnClickListener(v -> {
            if (member.isLeader()) {
                memberInputDialogBinding = MemberInputDialogBinding.inflate(LayoutInflater.from(v.getContext()));
                memberInputDialogBinding.peoplePickerSelect.setAvailablePersonas(memberSamplePersonas);
                memberInputDialogBinding.peoplePickerSelect.setAllowPersonaChipDragAndDrop(true);
                new MaterialAlertDialogBuilder(activity)
                        .setTitle(R.string.action_set_leader)
                        .setView(memberInputDialogBinding.getRoot())
                        .setPositiveButton(R.string.string_set, (dialog, which) -> {
                            for (ProjectMember item : members) {
                                if (memberInputDialogBinding.peoplePickerSelect.getPickedPersonas().get(0).getName().equals(item.getUser().getDisplayName())) {
                                    UserDataLookup.find(item.getUserId()).setOnRecordFoundListener(userRecord -> {
                                        memberName = userRecord.getName();
                                        if (!leader.equals(memberName)) {
                                            item.setLeader(true);
                                            member.setLeader(false);
                                            binding.leader.setText(memberName);
                                            binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
                                            createNews(item.getUserId(), News.NewsType.NORMAL, getString(R.string.set_leader), getString(R.string.set_leader));
                                            singleProjectViewModel.updateMember(item.getUserId(), item);
                                            singleProjectViewModel.updateMember(member.getUserId(), member);
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(R.string.string_cancel, (dialog, which) -> {
                        })
                        .show();
            }
        });

        //update state
        PopupMenuDialog stateDialog = new PopupMenuDialog(activity, R.menu.menu_select_project_state, binding.stateWidget);
        stateDialog.setViewGravity(Gravity.END | Gravity.BOTTOM);
        stateDialog.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_select_project_state_active: {
                    binding.stateWidget.setState(Project.State.Active);
                    comment.setCommentType(Comment.CommentType.ACTIVITY);
                    comment.setContentEn(getString(R.string.change_state) + " '" + Project.State.Active.toString() + "'");
                    comment.setContentVi(getString(R.string.change_state) + " '" + Project.State.Active.toString() + "'");
                    commentViewModel.createComment(comment);

                    if (!state.equals(Project.State.Active)) {
                        createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_state) + " '" + Project.State.Active.toString() + "'",
                                getString(R.string.change_state) + " '" + Project.State.Active.toString() + "'");
                    }
                    project.setState(Project.State.Active);
                    singleProjectViewModel.updateProject(project.getProjectId(), project);
                    return true;
                }
                case R.id.action_select_project_state_completed: {
                    binding.stateWidget.setState(Project.State.Completed);
                    comment.setCommentType(Comment.CommentType.ACTIVITY);
                    comment.setContentEn(getString(R.string.change_state) + " '" + Project.State.Completed.toString() + "'");
                    comment.setContentVi(getString(R.string.change_state) + " '" + Project.State.Completed.toString() + "'");
                    commentViewModel.createComment(comment);

                    if (!state.equals(Project.State.Completed)) {
                        createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.change_state) + " '" + Project.State.Completed.toString() + "'",
                                getString(R.string.change_state) + " '" + Project.State.Completed.toString() + "'");
                    }

                    project.setState(Project.State.Completed);
                    singleProjectViewModel.updateProject(project.getProjectId(), project);
                    return true;
                }
            }
            return false;
        });
        binding.stateWidget.setOnClickListener(v -> stateDialog.show());

        //add member to project
        binding.btnAddMember.setOnClickListener(v -> {
            memberInputDialogBinding = MemberInputDialogBinding.inflate(LayoutInflater.from(v.getContext()));
            memberInputDialogBinding.peoplePickerSelect.setAvailablePersonas(samplePersonas);
            memberInputDialogBinding.peoplePickerSelect.setAllowPersonaChipDragAndDrop(true);
            new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.action_add_member)
                    .setView(memberInputDialogBinding.getRoot())
                    .setPositiveButton(R.string.string_add, (dialog, which) -> {
                        for (IPersona persona : memberInputDialogBinding.peoplePickerSelect.getPickedPersonas()) {
                            for (User user : users) {
                                if (persona.getName().equals(user.getDisplayName())) {
                                    if (user.getUserId() != singleProjectViewModel.getUserId()) {
                                        News news = new News();
                                        news.setOwnerId(user.getUserId());
                                        news.setExecutorId(singleProjectViewModel.getUserId());
                                        news.setNewsOfTask(false);
                                        news.setProjectId(singleProjectViewModel.getProjectId());
                                        news.setNewsType(News.NewsType.NORMAL);
                                        news.setContentVi(getString(R.string.add_to_project));
                                        news.setContentEn(getString(R.string.add_to_project));
                                        newsViewModel.createNews(news);
                                        singleProjectViewModel.addMember(singleProjectViewModel.getProjectId(), user.getUserId(), 0);
                                        singleProjectViewModel.fetchMembers();
                                        onRefresh();
                                    }
                                }
                            }
                        }
                    })
                    .setNegativeButton(R.string.string_cancel, (dialog, which) -> {
                    })
                    .show();
        });

        final int spacing = getResources().getDimensionPixelSize(R.dimen.margin_medium);
        binding.rvCommentsNew.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));

        adapterComment.setOnClickDeleteListener(((object, position) ->
                commentViewModel.deleteComment(object.getCommentId())));

        singleProjectViewModel.fetch();
        singleProjectViewModel.fetchMembers();
        commentViewModel.getProjectComment(singleProjectViewModel.getProjectId());
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
        news.setExecutorId(singleProjectViewModel.getUserId());
        news.setNewsOfTask(false);
        news.setProjectId(singleProjectViewModel.getProjectId());
        news.setNewsType(type);
        news.setContentVi(contentVi);
        news.setContentEn(contentEn);
        newsViewModel.createNews(news);
    }

    public void createNewsToAllMember(News.NewsType type, String contentVi, String contentEn) {
        for (ProjectMember member : members) {
            if (singleProjectViewModel.getUserId() != member.getUserId()) {
                createNews(member.getUserId(), type, contentVi, contentEn);
            }
        }
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        binding.rvMentions.setAdapter(adapterSuggestedPeople);

        userViewModel.fetch();
        userViewModel.getLoadingUserResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    break;
                }
                case LOADED: {
                    samplePersonas.clear();
                    users = result.getResult();
                    for (User user : result.getResult()) {
                        ProjectMember member = new ProjectMember();
                        Persona persona = (Persona) createPersona(user.getDisplayName(), user.getMail(), "");
                        UserDataLookup.find(user.getUserId()).setOnRecordFoundListener(userRecord ->
                                persona.setAvatarImageBitmap(userRecord.getAvatar()));
                        samplePersonas.add(persona);
                        member.setPersona(persona);
                        member.setUserId(user.getUserId());
                    }
                    break;
                }
                case FAILED: {
                    break;
                }
            }
        });

        singleProjectViewModel.getLoadingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT: {
                    break;
                }
                case LOADING: {
                    binding.swipeLayout.setRefreshing(false);
                    break;
                }
                case LOADED: {
                    project = result.getResult();
                    if (project != null) {
                        taskViewModel.fetchProjectTask(project.getProjectId());
                        binding.setProject(project);
                        name = project.getName();
                        if (project.getDescription().isEmpty()) {
                            binding.txtDescription.setHint(R.string.hint_enter_project_description);
                        } else {
                            description = project.getDescription();
                        }
                        startDate = project.getStartDate();
                        dueDate = project.getDueDate();
                        state = project.getState();
                    }
                    binding.stateWidget.setState(project.getState());
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

        singleProjectViewModel.getLoadingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case LOADED: {
                    adapterMember.submitList(result.getResult());
                    members = result.getResult();
                    memberSamplePersonas.clear();
                    for (ProjectMember member : members) {
                        if (member.isLeader()) {
                            UserDataLookup.find(member.getUserId()).setOnRecordFoundListener(userRecord -> {
                                binding.leader.setText(userRecord.getName());
                                binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
                                leader = userRecord.getName();
                            });
                        }
                        UserDataLookup.find(member.getUserId()).setOnRecordFoundListener(userRecord -> {
                            Persona persona = (Persona) createPersona(userRecord.getName(), userRecord.getEmail(), "");
                            persona.setAvatarImageBitmap(userRecord.getAvatar());
                            memberSamplePersonas.add(persona);
                        });

                    }
                    binding.btnAddMember.setVisibility(View.VISIBLE);
                    binding.wgComment.setAvailablePeople(getSuggestedPeople(result.getResult()));
                    break;
                }
                case FAILED: {
                    adapterMember.submitList(new ArrayList<>());
                    makePopup(result.getErrorMessage());
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

        singleProjectViewModel.getAddingMemberResult().observe(getViewLifecycleOwner(), result -> {
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

        singleProjectViewModel.getRemovingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    singleProjectViewModel.fetchMembers();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        singleProjectViewModel.getUpdatingProjectResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    singleProjectViewModel.fetch();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        singleProjectViewModel.getArchiveProjectResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    singleProjectViewModel.fetch();
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        taskViewModel.fetchProjectTask(singleProjectViewModel.getProjectId());
        taskViewModel.getLoadingTaskProjectResult().observe(getViewLifecycleOwner(), result -> {
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

        commentViewModel.getLoadingProjectCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT:
                case LOADING: {
                    break;
                }
                case LOADED: {
                    adapterComment.setList(result.getResult());
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

        commentViewModel.getDeletingCommentResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    makeSnack("Deleted");
                    commentViewModel.getProjectComment(singleProjectViewModel.getProjectId());
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
                    commentViewModel.getProjectComment(singleProjectViewModel.getProjectId());
                    adapterComment.notifyDataSetChanged();
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
                    commentViewModel.getProjectComment(singleProjectViewModel.getProjectId());
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
            binding.swipeLayout.post(() -> singleProjectViewModel.fetch());
            binding.swipeLayout.post(() -> singleProjectViewModel.fetchMembers());
            binding.swipeLayout.post(() -> taskViewModel.fetchProjectTask(singleProjectViewModel.getProjectId()));
            binding.swipeLayout.post(() -> commentViewModel.getProjectComment(singleProjectViewModel.getProjectId()));
        }, 500);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_task: {
                getNavController().navigate(R.id.action_create_task);
                break;
            }
            case R.id.action_open_team_conversation: {
                getNavController().navigate(R.id.action_open_list_groups);
                break;
            }
            case R.id.action_leave_project: {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.request_leave_project)
                        .setPositiveButton(R.string.string_accept, (dialog, which) -> {
                            singleProjectViewModel.leaveProject(singleProjectViewModel.getUserId());
                            createNewsToAllMember(News.NewsType.NORMAL, getString(R.string.leave_project), getString(R.string.leave_project));
                            getNavController().popBackStack();
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();
                break;
            }
            case R.id.action_archive_project: {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.request_archive_project)
                        .setPositiveButton(R.string.string_accept, (dialog, which) -> {
                            singleProjectViewModel.archiveProject();
                            createNewsToAllMember(News.NewsType.ARCHIVE, getString(R.string.archive_project), getString(R.string.archive_project));
                            getNavController().popBackStack();
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();
                break;
            }
        }
        return true;
    }

    private IPersona createPersona(String name, String email, String role) {
        Persona persona = new Persona(name, role);
        persona.setSubtitle(email);
        return persona;
    }
}
