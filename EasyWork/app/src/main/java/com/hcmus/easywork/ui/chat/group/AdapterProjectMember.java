package com.hcmus.easywork.ui.chat.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.databinding.ItemProjectMemberBinding;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.common.adapter.AdapterMultipleSelection;

import java.util.List;

public class AdapterProjectMember extends AdapterMultipleSelection<ProjectMember, ItemProjectMemberBinding> {
    public AdapterProjectMember(Context context) {
        super(context);
    }

    @Override
    public ItemProjectMemberBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemProjectMemberBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<ItemProjectMemberBinding> holder, int position) {
        ProjectMember member = getItem(position);
        holder.binding.setMember(member);
        holder.binding.avatar.setName(member.getUser().getDisplayName());
        holder.map(position);
        if (selectionTracker != null) {
            Long key = holder.getSelectionKey();
            boolean isSelected = selectionTracker.isSelected(key);
            holder.binding.icSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            holder.binding.getRoot().setActivated(isSelected);
        }
    }

    @Override
    public void submitList(@Nullable List<ProjectMember> list) {
        super.submitList(list);
        selectionTracker.clearSelection();
    }

    @Override
    public boolean multiSelectSupported() {
        return true;
    }
}
