package com.hcmus.easywork.ui.project;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewStubProxy;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.ItemMemberBinding;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.ui.common.adapter.OnClickListener;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.views.PopupMenuDialog;

import java.util.List;

public class AdapterMember extends RecyclerListAdapter<ProjectMember, ItemMemberBinding> {
    private OnClickListener<ProjectMember> onClickSetLeaderListener;
    private OnClickListener<ProjectMember> onClickRemoveMemberListener;

    public AdapterMember(Context context) {
        super(context);
    }

    @Override
    public ItemMemberBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemMemberBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemMemberBinding> holder, int position) {
        ProjectMember projectMember = getItem(position);
        holder.binding.setMember(projectMember);
        holder.binding.avatar.setName(projectMember.getUser().getDisplayName());

        PopupMenuDialog popupMenuDialog = new PopupMenuDialog(context, R.menu.menu_member_option, holder.binding.avatar);
        popupMenuDialog.setViewGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        popupMenuDialog.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_remove_member) {
                if (onClickRemoveMemberListener != null) {
                    onClickRemoveMemberListener.onClick(projectMember, position);
                }
            }
            return true;
        });
        holder.binding.avatar.setOnClickListener(l -> popupMenuDialog.show());
    }

    @Override
    public void submitList(@Nullable List<ProjectMember> list) {
        super.submitList(list);
    }

    public void setOnClickSetLeaderListener(OnClickListener<ProjectMember> onClickSetLeaderListener) {
        this.onClickSetLeaderListener = onClickSetLeaderListener;
    }

    public void setOnClickRemoveMemberListener(OnClickListener<ProjectMember> onClickRemoveMemberListener) {
        this.onClickRemoveMemberListener = onClickRemoveMemberListener;
    }

    private void nullCheckInflateViewStub(ViewStubProxy proxy) {
        if (proxy.getViewStub() != null) {
            proxy.getViewStub().inflate();
        }
    }
}
