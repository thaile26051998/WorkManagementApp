package com.hcmus.easywork.ui.chat.group;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionTracker;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentStartDirectMessageBinding;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.viewmodels.chat.ConversationViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

import java.util.ArrayList;

public class FragmentStartDirectMessage extends BaseFragment<FragmentStartDirectMessageBinding> {
    private SingleProjectViewModel singleProjectViewModel;
    private ConversationViewModel conversationViewModel;
    private AdapterProjectMember adapterProjectMember;
    private SelectionTracker<Long> selectionTracker;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_start_direct_message;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        conversationViewModel = createViewModel(ConversationViewModel.class);
        adapterProjectMember = new AdapterProjectMember(activity) {
            @Override
            public boolean multiSelectSupported() {
                return false;
            }
        };
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnStart.setOnClickListener(l -> startDirectMessage());

        binding.rvProjectMembers.setAdapter(adapterProjectMember);

        selectionTracker = adapterProjectMember.createSelectionTracker("project_members", binding.rvProjectMembers);
        adapterProjectMember.setSelectionTracker(selectionTracker);
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        singleProjectViewModel.getLoadingMemberResult().observe(getViewLifecycleOwner(), result -> {
            switch (result.getLoadingState()) {
                case LOADED: {
                    adapterProjectMember.submitList(result.getResult());
                    break;
                }
                case FAILED: {
                    adapterProjectMember.submitList(new ArrayList<>());
                    makePopup(result.getErrorMessage());
                    break;
                }
            }
        });
    }

    private void startDirectMessage() {
        if (selectionTracker.getSelection().size() == 0) {
            makePopup("No member selected");
            return;
        }
        int memberIndex = 0;
        for (Long aLong : selectionTracker.getSelection()) {
            memberIndex = aLong.intValue();
            break;
        }
        ProjectMember member = adapterProjectMember.getCurrentList().get(memberIndex);
        Group group = new Group() {
            {
                setName(member.getUser().getDisplayName());
                setSingleChat(true);
                setUserId(member.getUserId());
                setProjectId(member.getProjectId());
            }
        };
        conversationViewModel.createGroup(group);
    }
}
