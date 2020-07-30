package com.hcmus.easywork.ui.task;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.hcmus.easywork.data.old.BaseRepository;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentCreateTaskBinding;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.comment.CommentViewModel;
import com.hcmus.easywork.viewmodels.member.MemberViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;
import com.hcmus.easywork.viewmodels.task.TaskViewModel;
import com.hcmus.easywork.views.CustomDatePickerDialog;
import com.microsoft.officeuifabric.persona.IPersona;
import com.microsoft.officeuifabric.persona.Persona;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentCreateTask extends BaseFragment<FragmentCreateTaskBinding> {
    private ArrayList<IPersona> samplePersonas;
    private Task task;
    private Date dueDate;
    private CreateTask mCreateTask;
    private TaskViewModel taskViewModel;
    private MemberViewModel memberViewModel;
    private SingleProjectViewModel singleProjectViewModel;
    private CommentViewModel commentViewModel;
    private CustomDatePickerDialog.Builder datePickerBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        samplePersonas = new ArrayList<>();
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        taskViewModel = createViewModel(TaskViewModel.class);
        memberViewModel = createViewModel(MemberViewModel.class);
        commentViewModel = createViewModel(CommentViewModel.class);
        memberViewModel.fetch(singleProjectViewModel.getProjectId());
        mCreateTask = new CreateTask();
        task = new Task();
        datePickerBuilder = new CustomDatePickerDialog.Builder(activity);
    }

    private IPersona createPersona(String name, String email, String role) {
        Persona persona = new Persona(name, role);
        persona.setSubtitle(email);
        return persona;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_task;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> getNavController().navigateUp());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_create) {
                if (binding.txtTaskName.getEditableText().toString().isEmpty()) {
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(R.string.response_task_name_empty)
                            .setPositiveButton(R.string.string_accept, null)
                            .show();
                    return false;
                }

                task.setProjectId(singleProjectViewModel.getProjectId());
                for (ProjectMember member : singleProjectViewModel.getLoadingMemberResult().getValue().getResult()) {
                    if (binding.peoplePickerSelect.getPickedPersonas().get(0).getName().equals(member.getUser().getDisplayName()))
                        task.setUserId(member.getUserId());
                }

                task.setName(binding.txtTaskName.getEditableText().toString());
                task.setDescription(binding.txtDescription.getEditableText().toString());
                task.setDueDate(dueDate);
                task.setPriority(binding.ddlPriority.getSelectedItem());
                createTask(task);
            }
            return true;
        });

        binding.txtDueDate.setOnClickListener(l -> datePickerBuilder.setOnDatePickedListener(new CustomDatePickerDialog.OnDatePickedListener() {
            @Override
            public void onDatePicked(int day, int month, int year) {

            }

            @Override
            public void onDatePicked(Date pickedDate) {
                String result = new SimpleDateFormat(activity.getString(R.string.format_standard_date), Locale.getDefault())
                        .format(pickedDate.getTime());
                binding.txtDueDate.setText(result);
                dueDate = pickedDate;
            }
        }).show());

        //assign task to member
        binding.peoplePickerSelect.setPersonaChipLimit(1);
        binding.peoplePickerSelect.setAllowPersonaChipDragAndDrop(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            binding.txtTaskName.requestFocus();
    }

    private void createTask(Task task) {
        this.mCreateTask.createTask(task);
        this.mCreateTask.setOnResponseListener(new BaseRepository.OnResponseListener<Task>() {
            @Override
            public void onResponse(Task response) {
                taskViewModel.addTask(task);
                //create comment
                Comment comment = new Comment();
                comment.setTaskId(response.getTaskId());
                comment.setUserId(taskViewModel.getUserId());
                comment.setCommentType(Comment.CommentType.ACTIVITY);
                comment.setContentEn(getString(R.string.create_task));
                comment.setContentVi(getString(R.string.create_task));
                commentViewModel.createComment(comment);

                getNavController().popBackStack();
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.response_create_task_success)
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onFailure(String message) {
                new MaterialAlertDialogBuilder(activity)
                        .setMessage(R.string.response_create_task_failed)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
        mCreateTask.enqueue();

        singleProjectViewModel.fetchMembers();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        singleProjectViewModel.getLoadingResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case INIT:
                case LOADING:
                    break;
                case LOADED: {
                    Calendar minDate = Calendar.getInstance(), maxDate = Calendar.getInstance();
                    minDate.setTime(result.getResult().getStartDate());
                    maxDate.setTime(result.getResult().getDueDate());
                    datePickerBuilder.setMinDate(minDate);
                    datePickerBuilder.setMaxDate(maxDate);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
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
                case LOADED: {
                    samplePersonas.clear();
                    binding.swipeLayout.setRefreshing(false);
                    for (ProjectMember member : singleProjectViewModel.getLoadingMemberResult().getValue().getResult()) {
                        Persona persona = (Persona) createPersona(member.getUser().getDisplayName(), member.getUser().getMail(), "");
                        UserDataLookup.find(member.getUserId()).setOnRecordFoundListener(userRecord ->
                                persona.setAvatarImageBitmap(userRecord.getAvatar()));
                        samplePersonas.add(persona);
                    }
                    binding.peoplePickerSelect.setAvailablePersonas(samplePersonas);
                    List<Task.Priority> priorityList = new ArrayList<>();
                    priorityList.add(Task.Priority.Low);
                    priorityList.add(Task.Priority.Medium);
                    priorityList.add(Task.Priority.High);

                    binding.ddlPriority.setSourceData(Arrays.asList(Task.Priority.values()), Task.Priority::toString);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    binding.swipeLayout.setRefreshing(false);
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
}