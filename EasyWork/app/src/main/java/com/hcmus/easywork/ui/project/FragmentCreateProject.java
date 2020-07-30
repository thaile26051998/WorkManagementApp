package com.hcmus.easywork.ui.project;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentCreateProjectBinding;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.auth.User;
import com.hcmus.easywork.viewmodels.auth.UserViewModel;
import com.hcmus.easywork.viewmodels.comment.CommentViewModel;
import com.hcmus.easywork.viewmodels.news.NewsViewModel;
import com.hcmus.easywork.views.CustomDatePickerDialog;
import com.microsoft.officeuifabric.persona.IPersona;
import com.microsoft.officeuifabric.persona.Persona;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentCreateProject extends BaseFragment<FragmentCreateProjectBinding> {
    private ArrayList<IPersona> samplePersonas = new ArrayList<>();
    private ProjectRepository projectRepository;
    private Project project;
    private UserViewModel userViewModel;
    private NewsViewModel newsViewModel;
    private CommentViewModel commentViewModel;
    private AuthenticationViewModel authenticationViewModel;
    private Date startDate, dueDate;
    private String projectName, startDateStr, dueDateStr;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_project;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectRepository = new ProjectRepository();
        project = new Project();
        project.setStartDate(new Date());
        project.setDueDate(new Date());
        commentViewModel = createViewModel(CommentViewModel.class);
        userViewModel = createViewModel(UserViewModel.class);
        userViewModel.fetch();
        newsViewModel = createViewModel(NewsViewModel.class);
        authenticationViewModel = createViewModel(AuthenticationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().navigateUp());
        binding.txtStartDate.setOnClickListener(l -> new CustomDatePickerDialog.Builder(activity)
                .setOnDatePickedListener(new CustomDatePickerDialog.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(int day, int month, int year) {

                    }

                    @Override
                    public void onDatePicked(Date pickedDate) {
                        startDateStr = new SimpleDateFormat(activity.getString(R.string.format_standard_date), Locale.getDefault())
                                .format(pickedDate.getTime());
                        binding.txtStartDate.setText(startDateStr);
                        startDate = pickedDate;
                        project.setStartDate(startDate);
                    }
                })
                .show());

        binding.txtDueDate.setOnClickListener(l -> new CustomDatePickerDialog.Builder(activity)
                .setOnDatePickedListener(new CustomDatePickerDialog.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(int day, int month, int year) {

                    }

                    @Override
                    public void onDatePicked(Date pickedDate) {
                        dueDateStr = new SimpleDateFormat(activity.getString(R.string.format_standard_date), Locale.getDefault())
                                .format(pickedDate.getTime());
                        binding.txtDueDate.setText(dueDateStr);
                        dueDate = pickedDate;
                        project.setDueDate(dueDate);
                    }
                })
                .show());

        binding.toolbar.setOnMenuItemClickListener(item -> {
            projectName = binding.txtProjectName.getEditableText().toString();
            if (item.getItemId() == R.id.action_create) {
                if (projectName.isEmpty()) {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_project_name_empty)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                } else if (startDateStr == null) {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_start_date_empty)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                } else if (dueDateStr == null) {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_due_date_empty)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                } else if (startDate.compareTo(dueDate) >= 0) {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_due_date_less_than_start_date)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                } else {
                    createProject();
                }
                return true;
            }
            return false;
        });

        binding.peoplePickerSelect.setAllowPersonaChipDragAndDrop(true);
        binding.leaderPickerSelect.setAllowPersonaChipDragAndDrop(true);
        binding.leaderPickerSelect.setPersonaChipLimit(1);
    }

    private void createProject() {
        String projectDescription = binding.txtDescription.getEditableText().toString();
        project.setName(projectName);
        project.setDescription(projectDescription);

        projectRepository.create(project).enqueue(new ResponseManager.OnResponseListener<Project>() {
            @Override
            public void onResponse(Project response) {
                int projectId = response.getProjectId();
                for (IPersona persona : binding.peoplePickerSelect.getPickedPersonas()) {
                    for (User user : userViewModel.getLoadingUserResult().getValue().getResult()) {
                        if (persona.getName().equals(user.getDisplayName())) {
                            if (user.getDisplayName().equals(binding.leaderPickerSelect.getPickedPersonas().get(0).getName())) {
                                addMember(projectId, user.getUserId(), 1);
                            } else {
                                addMember(projectId, user.getUserId(), 0);
                            }

                            if (user.getUserId() != authenticationViewModel.getUser().getValue().getUserId()) {
                                News news = new News();
                                news.setOwnerId(user.getUserId());
                                news.setExecutorId(authenticationViewModel.getUser().getValue().getUserId());
                                news.setNewsOfTask(false);
                                news.setProjectId(response.getProjectId());
                                news.setNewsType(News.NewsType.NORMAL);
                                news.setContentVi(getString(R.string.add_to_project));
                                news.setContentEn(getString(R.string.add_to_project));
                                newsViewModel.createNews(news);
                            }
                        }
                    }
                }

                //create comment
                Comment comment = new Comment();
                comment.setProjectId(response.getProjectId());
                comment.setUserId(authenticationViewModel.getUser().getValue().getUserId());
                comment.setCommentType(Comment.CommentType.ACTIVITY);
                comment.setContentEn(getString(R.string.create_project));
                comment.setContentVi(getString(R.string.create_project));
                commentViewModel.createComment(comment);

                makePopup(R.string.response_create_project_success);
                getNavController().popBackStack();
            }

            @Override
            public void onFailure(String message) {
                makePopup(message);
            }
        });
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
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
                    for (User user : result.getResult()) {
                        ProjectMember member = new ProjectMember();
                        Persona persona = (Persona) createPersona(user.getDisplayName(), user.getMail(), "");
                        UserDataLookup.find(user.getUserId()).setOnRecordFoundListener(userRecord ->
                                persona.setAvatarImageBitmap(userRecord.getAvatar()));
                        samplePersonas.add(persona);
                        member.setPersona(persona);
                        member.setUserId(user.getUserId());
                    }
                    binding.peoplePickerSelect.setAvailablePersonas(samplePersonas);
                    binding.leaderPickerSelect.setAvailablePersonas(samplePersonas);
                    break;
                }
                case FAILED: {
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

        commentViewModel.getAddingCommentResult().observe(getViewLifecycleOwner(), result -> {
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

    private void addMember(int projectId, int userId, int roll) {
        this.projectRepository.addProjectMember(projectId, userId, roll).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private IPersona createPersona(String name, String email, String role) {
        Persona persona = new Persona(name, role);
        persona.setSubtitle(email);
        return persona;
    }
}
