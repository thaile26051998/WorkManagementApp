package com.hcmus.easywork.ui.news;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewStubProxy;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.ItemNewsBinding;
import com.hcmus.easywork.databinding.StubProjectNameBinding;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.ui.common.adapter.OnClickListener;
import com.hcmus.easywork.ui.common.adapter.RecyclerListAdapter;
import com.hcmus.easywork.utils.UtilsView;
import com.hcmus.easywork.views.PopupMenuDialog;

import java.util.ArrayList;
import java.util.List;

public class AdapterNews extends RecyclerListAdapter<News, ItemNewsBinding> {
    private List<News> storedList = getStoredList();
    private OnClickListener<News> onClickMarkAsReadListener;
    private OnClickListener<News> onClickDeleteListener;

    public AdapterNews(Context context) {
        super(context);
    }

    @Override
    public ItemNewsBinding getBindingObject(LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        return ItemNewsBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerListViewHolder<ItemNewsBinding> holder, int position) {
        final News news = getItem(position);
        holder.binding.setItem(news);

        if (news.isNewsOfTask()) {
            holder.binding.headerTask.setVisibility(View.VISIBLE);
            holder.binding.headerProject.setVisibility(View.GONE);
        } else {
            holder.binding.headerTask.setVisibility(View.GONE);
            holder.binding.headerProject.setVisibility(View.VISIBLE);
        }

        UserDataLookup.find(news.getUser().getUserId()).setOnRecordFoundListener(userRecord ->
                holder.binding.avatar.setAvatarImageBitmap(userRecord.getAvatar()));

        PopupMenuDialog popupMenuDialog = new PopupMenuDialog(context, R.menu.menu_news, holder.binding.option);
        popupMenuDialog.setViewGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM);
        popupMenuDialog.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_mark_as_read: {
                    if (onClickMarkAsReadListener != null) {
                        onClickMarkAsReadListener.onClick(news, position);
                    }
                    break;
                }
                case R.id.action_delete_news: {
                    if (onClickDeleteListener != null) {
                        onClickDeleteListener.onClick(news, position);
                    }
                    break;
                }
            }
            return true;
        });
        holder.binding.option.setOnClickListener(l -> popupMenuDialog.show());

        holder.setOnClickListener(l -> {
            if (onClickListener != null) {
                onClickListener.onClick(news, position);
            }
        });

        if (news.isNewsOfTask()) {
//            ViewStubProxy stubHeaderTaskProxy = holder.binding.stubHeaderTask;
            ViewStubProxy stubProjectNameProxy = holder.binding.stubProjectName;
//            stubHeaderTaskProxy.setOnInflateListener(((stub, inflated) -> {
//                StubNewsTaskBinding binding = DataBindingUtil.bind(inflated);
//            }));

            stubProjectNameProxy.setOnInflateListener((stub, inflated) -> {
                StubProjectNameBinding binding = DataBindingUtil.bind(inflated);
                if (binding != null) {
                    binding.setName(news.getProject().getName());
                }
            });

//            UtilsView.inflateViewStub(stubHeaderTaskProxy);
            UtilsView.inflateViewStub(stubProjectNameProxy);
        }
//
//        if (!news.isNewsOfTask()) {
//            ViewStubProxy stubHeaderProjectProxy = holder.binding.stubHeaderProject;
//            stubHeaderProjectProxy.setOnInflateListener(((stub, inflated) -> {
//                StubNewsProjectBinding binding = DataBindingUtil.bind(inflated);
//            }));
//
//            UtilsView.inflateViewStub(stubHeaderProjectProxy);
//            return;
//        }
    }

    public void filter(News.NewsType newsType) {
        if (newsType == null) {
            submitList(getStoredList());
        } else {
            List<News> filterResult = new ArrayList<>();
            for (News item : storedList) {
                if (item.getNewsType().equals(newsType)) {
                    filterResult.add(item);
                }
            }
            submitList(filterResult);
        }
    }

    public void setOnClickMarkAsReadListener(OnClickListener<News> onClickMarkAsReadListener) {
        this.onClickMarkAsReadListener = onClickMarkAsReadListener;
    }

    public void setOnClickDeleteListener(OnClickListener<News> onClickDeleteListener) {
        this.onClickDeleteListener = onClickDeleteListener;
    }
}
