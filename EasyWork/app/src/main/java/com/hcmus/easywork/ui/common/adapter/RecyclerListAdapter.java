package com.hcmus.easywork.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.easywork.models.IComparableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this adapter instead of original <code>Recycler.Adapter</code> or <code>ListAdapter</code> for better performance and shorter code.
 *
 * @param <T> Type of item that works with this adapter. T must implements <code>IComparableModel&lt;T></code>
 * @param <B> ViewDataBinding class generated from item layout.
 */
public abstract class RecyclerListAdapter<T extends IComparableModel<T>, B extends ViewDataBinding>
        extends ListAdapter<T, RecyclerListAdapter.RecyclerListViewHolder<B>> {
    /**
     * Adapter context
     */
    protected Context context;
    /**
     * Adapter LayoutInflater
     */
    protected LayoutInflater inflater;
    /**
     * Adapter OnCLickListener
     */
    protected OnClickListener<T> onClickListener;
    /**
     * Stored List&lt;T> items used for filtering
     */
    protected List<T> mStoredList = new ArrayList<>();

    /**
     * Default constructor
     *
     * @param context Context
     */
    public RecyclerListAdapter(Context context) {
        super(new ItemDiffCallback<>());
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerListViewHolder<B> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        B binding = getBindingObject(inflater, parent, viewType);
        return new RecyclerListViewHolder<>(binding);
    }

    /**
     * Get ViewDataBinding object for {@link #onCreateViewHolder(ViewGroup, int)}
     *
     * @param inflater LayoutInflater
     * @param parent   ViewGroup
     * @param viewType viewType
     * @return instance of ViewDataBinding. For example: <code>ViewDataBinding.inflate(inflater, parent, false)</code>.
     */
    public abstract B getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType);

    /**
     * Set click listener event for adapter, including selected object
     *
     * @param onClickListener OnClickListener
     */
    public void setOnClickListener(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Get the stored data from adapter, only used for filtering
     *
     * @return List of items
     */
    public List<T> getStoredList() {
        return this.mStoredList;
    }

    /**
     * Set stored data to adapter without affecting the displayed data.
     *
     * @param list List of items
     */
    public void setStoredList(List<T> list) {
        this.mStoredList.clear();
        this.mStoredList.addAll(list);
    }

    /**
     * <p>Set data to adapter. This method is used if adapter supports <code>Filterable</code>.</p>
     * <p>Use {@link #submitList(List)} instead if adapter does not support <code>Filterable</code> for better performance.</p>
     *
     * @param list List of items
     */
    public void setList(List<T> list) {
        this.mStoredList.clear();
        this.mStoredList.addAll(list);
        submitList(list);
    }

    protected void setAdapterOnClick(T object, int position) {
        if (onClickListener != null) {
            onClickListener.onClick(object, position);
        }
    }

    public static class RecyclerListViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public B binding;

        public RecyclerListViewHolder(@NonNull B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setOnClickListener(View.OnClickListener l) {
            this.itemView.setOnClickListener(l);
        }
    }

    /**
     * DiffUtil class for async calculating differences between two lists for better performance.
     *
     * @param <T> <code>IComparableModel instance</code>
     */
    static class ItemDiffCallback<T extends IComparableModel<T>>
            extends DiffUtil.ItemCallback<T> {
        @Override
        public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            // Assert two items are from the same class instance
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            // Let objects return the result
            return oldItem.isTheSame(newItem);
        }
    }
}
