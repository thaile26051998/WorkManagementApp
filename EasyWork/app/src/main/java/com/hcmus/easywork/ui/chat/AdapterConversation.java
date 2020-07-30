package com.hcmus.easywork.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.ItemConversationBinding;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.utils.CustomizedCollections;

import java.util.ArrayList;
import java.util.List;

public class AdapterConversation extends RecyclerListAdapter<Group, ItemConversationBinding>
        implements Filterable {
    private ProjectRepository projectRepository;
    private int userId = 0;

    public AdapterConversation(Context context) {
        super(context);
        projectRepository = new ProjectRepository();
    }

    @Override
    public Filter getFilter() {
        // TODO: implement filter
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchValue = constraint.toString();
                FilterResults filterResults = new FilterResults();
                if (searchValue.isEmpty()) {
                    filterResults.values = mStoredList;
                } else {
                    filterResults.values = CustomizedCollections.filter(mStoredList, object ->
                            object.getName().toLowerCase().contains(searchValue.toLowerCase()));
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Group> searchResult = ((ArrayList<Group>) results.values);
                submitList(searchResult);
            }
        };
    }

    @Override
    public ItemConversationBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemConversationBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemConversationBinding> holder, int position) {
        Group item = getItem(position);
        holder.binding.setItem(item);

        if (item.getUserId() != 0) {
            // Single chat, determine to load my info or partner info
            int idToLoad;
            if (userId == item.getCreatorId()) {
                idToLoad = item.getUserId();
            } else {
                idToLoad = item.getCreatorId();
            }
            UserDataLookup.find(idToLoad).setOnRecordFoundListener(userRecord -> {
                holder.binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
                item.setName(userRecord.getName());
                holder.binding.name.setText(userRecord.getName());
            });
        } else {
            holder.binding.avatar.setName(item.getName());
        }
        holder.setOnClickListener(l -> setAdapterOnClick(item, position));
        projectRepository.getProject(item.getProjectId())
                .enqueue(new ResponseManager.OnResponseListener<Project>() {
                    @Override
                    public void onResponse(Project response) {
                        holder.binding.projectName.setText(response.getName());
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
