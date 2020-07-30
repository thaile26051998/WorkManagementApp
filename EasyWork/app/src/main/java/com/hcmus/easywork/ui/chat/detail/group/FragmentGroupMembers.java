package com.hcmus.easywork.ui.chat.detail.group;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionTracker;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentGroupMembersBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.chat.group.GroupMember;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.chat.group.AdapterProjectMember;
import com.hcmus.easywork.ui.chat.message.AdapterGroupMember;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentGroupMembers extends BaseFragment<FragmentGroupMembersBinding> {
    private SingleGroupViewModel groupViewModel;
    private AdapterGroupMember adapterGroupMember;
    private AdapterProjectMember adapterProjectMember;
    private SelectionTracker<Long> selectionTracker;
    private SingleProjectViewModel singleProjectViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_group_members;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        adapterGroupMember = new AdapterGroupMember(activity);
        adapterProjectMember = new AdapterProjectMember(activity) {
            @Override
            public boolean multiSelectSupported() {
                return true;
            }
        };
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(l -> getNavController().popBackStack());
        binding.btnAdd.setOnClickListener(l -> addMembers());
        binding.rvGroupMembers.setAdapter(adapterGroupMember);
        binding.rvProjectMembers.setAdapter(adapterProjectMember);

        selectionTracker = adapterProjectMember.createSelectionTracker("project_members", binding.rvProjectMembers);
        adapterProjectMember.setSelectionTracker(selectionTracker);

        groupViewModel.getAllMembers();
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.get().observe(getViewLifecycleOwner(), group ->
                singleProjectViewModel.setProjectId(group.getProjectId()));

        groupViewModel.getLoadingMembersResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT: {
                    break;
                }
                case DONE: {
                    adapterGroupMember.submitList(result.getResult());
                    int resultSize = result.getResult().size();
                    binding.toolbar.setSubtitle(getResources().
                            getQuantityString(R.plurals.group_members_count, resultSize, resultSize));
                    singleProjectViewModel.fetchMembers();
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
                case INIT:
                case LOADING: {
                    break;
                }
                case LOADED: {
                    // WARNING: bad way to filter members
                    List<ProjectMember> members = result.getResult();
                    List<GroupMember> groupMembers = adapterGroupMember.getCurrentList();
                    List<ProjectMember> toRemoveMembers = new ArrayList<>();
                    for (ProjectMember pm : members) {
                        for (GroupMember gm : groupMembers) {
                            if (pm.getUserId() == gm.getUserId()) {
                                toRemoveMembers.add(pm);
                            }
                        }
                    }
                    members.removeAll(toRemoveMembers);
                    adapterProjectMember.submitList(members);
                    break;
                }
                case FAILED: {
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });

        groupViewModel.getAddingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getState()) {
                case INIT:
                    break;
                case DONE: {
                    makeSnack("Added");
                    groupViewModel.getAllMembers();
                    singleProjectViewModel.fetchMembers();
                    break;
                }
                case FAILED: {
                    makeSnack(result.getErrorMessage());
                    break;
                }
            }
        });
    }

    private void addMembers() {
        if (selectionTracker.getSelection().size() == 0) {
            makePopup("No member selected");
            return;
        }
        List<ProjectMember> members = new ArrayList<>();
        for (Long aLong : selectionTracker.getSelection()) {
            int index = aLong.intValue();
            members.add(adapterProjectMember.getCurrentList().get(index));
        }
        for (ProjectMember m : members) {
            groupViewModel.addMember(m.getUserId());
        }
    }
}
