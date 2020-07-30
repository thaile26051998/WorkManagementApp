package com.hcmus.easywork.ui.chat.mention;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.databinding.ItemSuggestedPeopleBinding;

public class AdapterSuggestedPeople extends RecyclerListAdapter<SuggestedPeople, ItemSuggestedPeopleBinding> {

    public AdapterSuggestedPeople(Context context) {
        super(context);
    }

    @Override
    public ItemSuggestedPeopleBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemSuggestedPeopleBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemSuggestedPeopleBinding> holder, int position) {
        SuggestedPeople people = getItem(position);
        holder.binding.setName(people.getName());
        holder.setOnClickListener(l -> setAdapterOnClick(people, position));
    }
}
