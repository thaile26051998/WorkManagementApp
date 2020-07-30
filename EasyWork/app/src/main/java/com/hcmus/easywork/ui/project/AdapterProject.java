package com.hcmus.easywork.ui.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.databinding.ItemProjectBinding;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.utils.CustomizedCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterProject extends RecyclerListAdapter<Project, ItemProjectBinding>
        implements Filterable, IProjectSortable {
    private List<Project> mStoredProjects = new ArrayList<>();

    public AdapterProject(Context context) {
        super(context);
    }

    @Override
    public ItemProjectBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemProjectBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemProjectBinding> holder, int position) {
        final Project project = getItem(position);
        holder.binding.setProject(project);
        holder.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(project, position);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchValue = constraint.toString();
                FilterResults filterResults = new FilterResults();
                if (searchValue.isEmpty()) {
                    filterResults.values = mStoredProjects;
                } else {
                    ArrayList<Project> filteredItems = new ArrayList<>();
                    for (Project item : mStoredProjects) {
                        if (item.getName().toLowerCase().contains(searchValue.toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                    filterResults.values = filteredItems;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Project> searchResult = ((ArrayList<Project>) results.values);
                submitList(searchResult);
            }
        };
    }

    @Override
    public void sortByName() {
        CustomizedCollections.sort(this.mStoredProjects, new CustomizedCollections.CollatorComparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return this.compareTo(o1.getName(), o2.getName());
            }
        });
        submitList(this.mStoredProjects);
        notifyDataSetChanged();
    }

    @Override
    public void sortByDueDate() {
        Collections.sort(this.mStoredProjects, (o1, o2) -> {
            if (o1.getDueDate() == null || o2.getDueDate() == null) return 0;
            return o1.getDueDate().compareTo(o2.getDueDate());
        });
        submitList(this.mStoredProjects);
        notifyDataSetChanged();
    }

    public void setList(List<Project> list) {
        this.mStoredProjects.clear();
        this.mStoredProjects.addAll(list);
        submitList(list);
    }
}
