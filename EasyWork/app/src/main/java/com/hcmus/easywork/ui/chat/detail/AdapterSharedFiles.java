package com.hcmus.easywork.ui.chat.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.hcmus.easywork.R;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.databinding.ItemSharedFileBinding;
import com.hcmus.easywork.models.file.EwFile;

public class AdapterSharedFiles extends RecyclerListAdapter<EwFile, ItemSharedFileBinding> {
    private static final String TYPE_TEXT = "text/plain";
    private static final String TYPE_PHOTO_PNG = "image/png";
    private static final String TYPE_PHOTO_JPG = "image/jpg";
    private static final String TYPE_PHOTO_JPEG = "image/jpeg";

    public AdapterSharedFiles(Context context) {
        super(context);
    }

    @Override
    public ItemSharedFileBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemSharedFileBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemSharedFileBinding> holder, int position) {
        EwFile file = getItem(position);
        holder.binding.setFile(file);
        holder.binding.icon.setImageResource(getFileIconId(file.mType));
        holder.setOnClickListener(l -> setAdapterOnClick(file, position));
    }

    @DrawableRes
    private int getFileIconId(String fileType) {
        @DrawableRes int iconId;
        switch (fileType) {
            case TYPE_TEXT: {
                iconId = R.drawable.ic_file_type_text;
                break;
            }
            case TYPE_PHOTO_PNG:
            case TYPE_PHOTO_JPG:
            case TYPE_PHOTO_JPEG: {
                iconId = R.drawable.ic_file_type_photo;
                break;
            }
            default: {
                iconId = R.drawable.ic_file_type_general;
                break;
            }
        }
        return iconId;
    }
}
