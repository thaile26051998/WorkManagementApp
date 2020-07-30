package com.hcmus.easywork.ui.chat.group;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentCreateGroupChatBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.viewmodels.chat.ConversationViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;

public class FragmentCreateGroupChat extends BaseFragment<FragmentCreateGroupChatBinding> {
    private SingleProjectViewModel singleProjectViewModel;
    private ConversationViewModel conversationViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_group_chat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleProjectViewModel = createViewModel(SingleProjectViewModel.class);
        conversationViewModel = createViewModel(ConversationViewModel.class);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnCreate.setOnClickListener(l -> createGroup());
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    private void createGroup() {
        String groupName = binding.edtGroupName.getEditableText().toString();
        String groupDescription = binding.edtGroupDescription.getEditableText().toString();
        if (groupName.isEmpty()) {
            makePopup("Required information missing");
            return;
        }
        Group group = new Group() {
            {
                setName(groupName);
                setDescription(groupDescription);
                setSingleChat(false);
            }
        };
        int projectId = singleProjectViewModel.getProjectId();
        if (projectId == 0) {
            makePopup("No project selected");
        } else {
            group.setProjectId(projectId);
            conversationViewModel.createGroup(group);
        }
    }
}
