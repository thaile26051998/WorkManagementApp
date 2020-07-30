package com.hcmus.easywork.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.easywork.models.IComparableModel;

public abstract class AdapterMultipleSelection<T extends IComparableModel<T>, B extends ViewDataBinding>
        extends ListAdapter<T, AdapterMultipleSelection.ViewHolder<B>> {
    // region Variables
    protected Context context;
    protected LayoutInflater layoutInflater;
    protected SelectionTracker<Long> selectionTracker;
    protected OnClickListener<T> onClickListener;
    // endregion

    public AdapterMultipleSelection(Context context) {
        super(new ItemDiffCallback<>());
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    // region Override RecyclerView
    public abstract B getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType);

    @NonNull
    @Override
    public ViewHolder<B> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        B binding = getBindingObject(layoutInflater, parent, viewType);
        return new AdapterMultipleSelection.ViewHolder<>(binding);
    }

    public void setOnClickListener(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
    }
    // endregion

    public static class ViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {
        public B binding;
        private ItemDetails details;

        public ViewHolder(@NonNull B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void map(int position) {
            details = new ItemDetails(position);
        }

        public ItemDetails getDetails() {
            return this.details;
        }

        public Long getSelectionKey() {
            return this.details.getSelectionKey();
        }

        public void setOnClickListener(View.OnClickListener l) {
            this.itemView.setOnClickListener(l);
        }
    }

    private static class ItemDiffCallback<T extends IComparableModel<T>>
            extends DiffUtil.ItemCallback<T> {
        @Override
        public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.isTheSame(newItem);
        }
    }

    public void setSelectionTracker(SelectionTracker<Long> tracker) {
        this.selectionTracker = tracker;
    }

    public SelectionTracker<Long> createSelectionTracker(String selectionId, RecyclerView recyclerView) {
        SelectionTracker.Builder<Long> builder = new SelectionTracker.Builder<>(
                selectionId, recyclerView,
                new KeyProvider(),
                new DetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        );
        return builder.withSelectionPredicate(new Predicate()).build();
    }

    /**
     * Determine if user can perform multi-selection on RecyclerView
     *
     * @return true or false
     */
    public abstract boolean multiSelectSupported();

    private class DetailsLookup extends ItemDetailsLookup<Long> {
        private RecyclerView recyclerView;

        public DetailsLookup(RecyclerView view) {
            this.recyclerView = view;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                if (viewHolder instanceof AdapterMultipleSelection.ViewHolder) {
                    return (((ViewHolder<B>) viewHolder)).getDetails();
                }
            }
            return null;
        }
    }

    private static class KeyProvider extends ItemKeyProvider<Long> {
        protected KeyProvider() {
            super(ItemKeyProvider.SCOPE_MAPPED);
        }

        @Nullable
        @Override
        public Long getKey(int position) {
            return ((long) position);
        }

        @Override
        public int getPosition(@NonNull Long key) {
            long value = key;
            return ((int) value);
        }
    }

    private class Predicate extends SelectionTracker.SelectionPredicate<Long> {
        @Override
        public boolean canSetStateForKey(@NonNull Long key, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSetStateAtPosition(int position, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSelectMultiple() {
            return multiSelectSupported();
        }
    }

    private static class ItemDetails extends ItemDetailsLookup.ItemDetails<Long> {
        private long position;

        public ItemDetails(int position) {
            this.position = position;
        }

        @Override
        public int getPosition() {
            return ((int) position);
        }

        @Nullable
        @Override
        public Long getSelectionKey() {
            return position;
        }

        @Override
        public boolean inSelectionHotspot(@NonNull MotionEvent e) {
            return true;
        }
    }


}
