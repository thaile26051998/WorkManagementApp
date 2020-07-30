package com.hcmus.easywork.ui.chat.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewStubProxy;

import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.MessageRepository;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.ItemMessageBinding;
import com.hcmus.easywork.databinding.StubMessageFileBinding;
import com.hcmus.easywork.databinding.StubMessageImageBinding;
import com.hcmus.easywork.databinding.StubMessageTimeBinding;
import com.hcmus.easywork.ui.common.adapter.OnLongClickListener;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.file.EwFile;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.utils.UtilsView;

import java.util.List;

public class AdapterMessage extends RecyclerListAdapter<Message, ItemMessageBinding> {
    private OnLongClickListener<Message> onLongClickListener;
    private OnSharedImageClickListener onSharedImageClickListener;
    private OnSharedFileClickListener onSharedFileClickListener;
    private MessageRepository messageRepository;

    public AdapterMessage(Context context) {
        super(context);
        messageRepository = new MessageRepository();
    }

    @Override
    public ItemMessageBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemMessageBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemMessageBinding> holder, int position) {
        Message message = getItem(position);
        holder.binding.setMessage(message);

        if (message.getContent() == null || message.getContent().equals("")) {
            messageRepository.getFile(message.getId()).enqueue(new ResponseManager.OnResponseListener<List<EwFile>>() {
                @Override
                public void onResponse(List<EwFile> response) {
                    if (!response.isEmpty()) {
                        EwFile ewFile = response.get(0);
                        // If shared file is image
                        if (ewFile.mType.toLowerCase().contains("image")) {
                            inflateStubImage(holder, ewFile);
                        } else {
                            inflateStubFile(holder, ewFile);
                        }
                    }
                }

                @Override
                public void onFailure(String message) {

                }
            });
        } else {
            holder.binding.message.setVisibility(View.VISIBLE);
        }

        UserDataLookup.find(message.getUserId()).setOnRecordFoundListener(userRecord -> {
            holder.binding.name.setText(userRecord.getName());
            holder.binding.avatar.setAvatarUsingGlide(userRecord.getAvatar());
        });

        holder.setOnClickListener(l -> {
            setAdapterOnClick(message, position);
            // Display time
            ViewStubProxy timeProxy = holder.binding.stubTime;
            timeProxy.setOnInflateListener((stub, inflated) -> {
                StubMessageTimeBinding timeBinding = DataBindingUtil.bind(inflated);
                if (timeBinding != null)
                    timeBinding.setTime(message.getTime());
            });
            if (timeProxy.isInflated()) {
                UtilsView.toggleVisibility(timeProxy.getRoot());
            } else {
                UtilsView.inflateViewStub(timeProxy);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (onLongClickListener != null) {
                onLongClickListener.onLongClick(message, position);
            }
            return true;
        });

        // Display like badge
//        ViewStubProxy likeProxy = holder.binding.stubLike;
//        if (message.isPinned()) {
//            if (likeProxy.isInflated()) {
//                UtilsView.show(likeProxy.getRoot());
//            } else {
//                nullCheckInflate(likeProxy);
//            }
//        } else {
//            if (likeProxy.isInflated())
//                UtilsView.hide(likeProxy.getRoot());
//        }
    }

    private void inflateStubImage(RecyclerListViewHolder<ItemMessageBinding> holder, EwFile file) {
        ViewStubProxy imageProxy = holder.binding.stubImage;
        imageProxy.setOnInflateListener((stub, inflated) -> {
            StubMessageImageBinding imageBinding = DataBindingUtil.bind(inflated);
            if (imageBinding != null) {
                ImageLoadingLibrary.useContext(context).load(file.mData.data).into(imageBinding.commentImage);
                imageBinding.commentImage.setOnClickListener(l -> {
                    if (onSharedImageClickListener != null) {
                        onSharedImageClickListener.onClick(file);
                    }
                });
            }
        });
        UtilsView.inflateViewStub(imageProxy);
    }

    private void inflateStubFile(RecyclerListViewHolder<ItemMessageBinding> holder, EwFile file) {
        ViewStubProxy fileProxy = holder.binding.stubFile;
        fileProxy.setOnInflateListener((stub, inflated) -> {
            StubMessageFileBinding fileBinding = DataBindingUtil.bind(inflated);
            if (fileBinding != null) {
                fileBinding.setFile(file);
                fileBinding.view.setOnClickListener(l -> {
                    if (onSharedFileClickListener != null) {
                        onSharedFileClickListener.onClick(file);
                    }
                });
            }
        });
        UtilsView.inflateViewStub(fileProxy);
    }

    public void setOnLongClickListener(OnLongClickListener<Message> onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnSharedImageClickListener(OnSharedImageClickListener listener) {
        this.onSharedImageClickListener = listener;
    }

    public void setOnSharedFileClickListener(OnSharedFileClickListener listener) {
        this.onSharedFileClickListener = listener;
    }
}
