package com.hcmus.easywork.ui.common;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewStubProxy;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.ItemCommentNewBinding;
import com.hcmus.easywork.databinding.StubCommentFileBinding;
import com.hcmus.easywork.databinding.StubCommentImageBinding;
import com.hcmus.easywork.databinding.StubCommentTextBinding;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.ui.common.adapter.OnClickListener;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.utils.UtilsView;
import com.hcmus.easywork.viewmodels.comment.CommentViewModel;
import com.hcmus.easywork.views.PopupMenuDialog;
import com.hcmus.easywork.views.TextInputDialog;

public class AdapterComment extends RecyclerListAdapter<Comment, ItemCommentNewBinding> {
    private OnClickListener<Comment> onClickDeleteListener;
    private OnClickListener<Comment> onSharedImageClickListener;
    private OnClickListener<Comment> onSharedFileClickListener;
    private CommentViewModel commentViewModel;

    public AdapterComment(Context context) {
        super(context);
        commentViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CommentViewModel.class);
    }

    @Override
    public ItemCommentNewBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemCommentNewBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemCommentNewBinding> holder, int position) {
        Comment comment = getItem(position);
        holder.binding.setItem(comment);

        UserDataLookup.find(comment.getUserId()).setOnRecordFoundListener(userRecord ->
                holder.binding.avatar.setAvatarUsingGlide(userRecord.getAvatar()));

        PopupMenuDialog popupMenuDialog = new PopupMenuDialog(context, R.menu.menu_comment_option, holder.binding.option);
        popupMenuDialog.setViewGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        popupMenuDialog.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete_comment) {
                if (onClickDeleteListener != null) {
                    onClickDeleteListener.onClick(comment, position);
                }
            }
            return true;
        });
        holder.binding.option.setOnClickListener(l -> popupMenuDialog.show());

        if (comment.getCommentType().equals(Comment.CommentType.ACTIVITY)) {
            holder.binding.option.setVisibility(View.GONE);

            ViewStubProxy textProxy = holder.binding.stubText;
            textProxy.setOnInflateListener(((stub, inflated) -> {
                StubCommentTextBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    binding.setComment(comment.getActor() + " " + comment.getContentEn());
                }
            }));
            UtilsView.inflateViewStub(textProxy);
            return;
        }

        if (comment.getCommentType().equals(Comment.CommentType.TEXT)) {
            ViewStubProxy textProxy = holder.binding.stubText;
            textProxy.setOnInflateListener(((stub, inflated) -> {
                StubCommentTextBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    binding.setComment(comment.getContentVi());
                    new TextInputDialog.Builder()
                            .setConnectedView(binding.getRoot())
                            .setOnTextSubmitted(text -> {
                                comment.setContentVi(text);
                                commentViewModel.editComment(comment.getCommentId(), text);
                            })
                            .bind();
                }
            }));
            UtilsView.inflateViewStub(textProxy);
            return;
        }

        if (comment.getCommentType().equals(Comment.CommentType.MENTION)) {
            ViewStubProxy textProxy = holder.binding.stubText;
            textProxy.setOnInflateListener(((stub, inflated) -> {
                StubCommentTextBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    binding.setComment(comment.getContentVi());
                    new TextInputDialog.Builder()
                            .setConnectedView(binding.getRoot())
                            .setOnTextSubmitted(text -> {
                                comment.setContentVi(text);
                                commentViewModel.editComment(comment.getCommentId(), text);
                            })
                            .bind();
                }
            }));
            UtilsView.inflateViewStub(textProxy);
            return;
        }

        if (comment.getCommentType().equals(Comment.CommentType.FILE) && getFileType(comment.getFileType()).equals("image")) {
            holder.binding.stubImage.setOnInflateListener(((stub, inflated) -> {
                StubCommentImageBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    ImageLoadingLibrary.useContext(context).load(comment.getFileData().getData()).into(binding.commentImage);
                }
            }));

            nullCheckInflateViewStub(holder.binding.stubImage);
            return;
        }

        if (comment.getCommentType().equals(Comment.CommentType.FILE) && !getFileType(comment.getFileType()).equals("image")) {
            holder.binding.stubFile.setOnInflateListener((stub, inflated) -> {
                StubCommentFileBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    binding.setFileName(comment.getFileName());
                    binding.setFileSize(comment.getFileSize());
                }

            });

            nullCheckInflateViewStub(holder.binding.stubFile);
        }
    }

    private void nullCheckInflateViewStub(ViewStubProxy proxy) {
        if (proxy.getViewStub() != null) {
            proxy.getViewStub().inflate();
        }
    }

    public String getFileType(String type) {
        if (type != null) {
            return type.substring(0, 5);
        }
        return null;
    }

    public void setOnClickDeleteListener(OnClickListener<Comment> onClickListener) {
        this.onClickDeleteListener = onClickListener;
    }

    public void setOnSharedImageClickListener(OnClickListener<Comment> listener) {
        this.onSharedImageClickListener = listener;
    }

    public void setOnSharedFileClickListener(OnClickListener<Comment> listener) {
        this.onSharedFileClickListener = listener;
    }
}
