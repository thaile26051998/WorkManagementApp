package com.hcmus.easywork.ui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.databinding.ItemTaskBinding;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.utils.CustomizedCollections;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterTask extends RecyclerListAdapter<Task, ItemTaskBinding>
        implements Filterable, ITaskSortable {
    private List<Task> mStoredTasks = new ArrayList<>();

    public AdapterTask(Context context) {
        super(context);
    }

    @Override
    public ItemTaskBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemTaskBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemTaskBinding> holder, int position) {
        final Task task = getItem(position);
        holder.binding.setTask(task);
        if (task.getDueDate() != null) {
            SimpleDateFormat mDateFormat = new SimpleDateFormat(context.getString(R.string.format_standard_date), Locale.getDefault());

            holder.binding.deadline.setText(mDateFormat.format(task.getDueDate()));
        }
        holder.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(task, position);
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
                    filterResults.values = mStoredTasks;
                } else {
                    List<Task> filteredItems = new ArrayList<>();
                    for (Task item : mStoredTasks) {
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
                List<Task> searchResult = ((ArrayList<Task>) results.values);
                submitList(searchResult);
            }
        };
    }

    /**
     * Deprecated. Use {@link #setList(List)} instead
     *
     * @param list List
     */
    @Deprecated
    public void set(List<Task> list) {
        setList(list);
    }

    public void setList(List<Task> list) {
        this.mStoredTasks.clear();
        this.mStoredTasks.addAll(list);
        submitList(list);
    }

    public void filterState(@Nullable Task.State state) {
        if (state == null) {
            submitList(this.mStoredTasks);
        } else {
            ArrayList<Task> filteredList = new ArrayList<>();
            for (Task item : this.mStoredTasks) {
                if (item.getState() == state) {
                    filteredList.add(item);
                }
            }
            submitList(filteredList);
            notifyDataSetChanged();
        }
    }

    public void filterPriority(@Nullable Task.Priority priority) {
        if (priority == null) {
            submitList(this.mStoredTasks);
        } else {
            ArrayList<Task> filteredList = new ArrayList<>();
            for (Task item : this.mStoredTasks) {
                if (item.getPriority() == priority) {
                    filteredList.add(item);
                }
            }
            submitList(filteredList);
            notifyDataSetChanged();
        }
    }

    @Override
    public void sortByName() {
        CustomizedCollections.sort(this.mStoredTasks, new CustomizedCollections.CollatorComparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return this.compareTo(o1.getName(), o2.getName());
            }
        });
        submitList(this.mStoredTasks);
        notifyDataSetChanged();
    }

    @Override
    public void sortByDueDate() {
        CustomizedCollections.sort(this.mStoredTasks, new CustomizedCollections.CollatorComparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getDueDate().compareTo(o2.getDueDate());
            }
        });
        submitList(this.mStoredTasks);
        notifyDataSetChanged();
    }

    @Override
    public void sortByState() {
        // TODO: change state object for comparison
    }

    @Override
    public void sortByPriority() {
        // TODO: change priority object for comparison
    }
}
