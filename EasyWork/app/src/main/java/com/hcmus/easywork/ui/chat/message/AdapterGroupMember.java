package com.hcmus.easywork.ui.chat.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.databinding.ItemGroupMemberBinding;
import com.hcmus.easywork.models.chat.group.GroupMember;

public class AdapterGroupMember extends RecyclerListAdapter<GroupMember, ItemGroupMemberBinding> {

    public AdapterGroupMember(Context context) {
        super(context);
    }

    @Override
    public ItemGroupMemberBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemGroupMemberBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemGroupMemberBinding> holder, int position) {
        GroupMember member = getItem(position);
        holder.binding.setMember(member);
    }
}
