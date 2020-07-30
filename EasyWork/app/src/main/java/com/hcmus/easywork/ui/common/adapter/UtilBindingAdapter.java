package com.hcmus.easywork.ui.common.adapter;

import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hcmus.easywork.R;
import com.hcmus.easywork.models.Comment;
import com.hcmus.easywork.utils.DotsIndicatorDecoration;
import com.hcmus.easywork.utils.GridSpacingItemDecoration;
import com.hcmus.easywork.views.DateWidget;

import java.util.Date;

public class UtilBindingAdapter {

    @BindingAdapter(value = {"verticallyDivided"})
    public static void setVerticallyDivided(RecyclerView view, boolean verticallyDivided) {
        if (verticallyDivided) {
            view.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        }
    }

    @BindingAdapter(value = {"adapter"})
    public static void setRecyclerViewAdapter(RecyclerView view, RecyclerView.Adapter<?> adapter) {
        view.setAdapter(adapter);
    }

    @BindingAdapter({"snapHelp"})
    public static void setRecyclerViewSnapHelper(RecyclerView view, boolean snapHelper) {
        if (snapHelper) {
            // TODO: only use for project summary in home screen
            int radius = 8, dotsHeight = 32;
            //final int color = ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark);
            TypedValue typedValue = new TypedValue();
            view.getContext().getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
            int color = ContextCompat.getColor(view.getContext(), typedValue.resourceId);
            view.addItemDecoration(new DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color));
            new PagerSnapHelper().attachToRecyclerView(view);
        }
    }

    @BindingAdapter({"actor", "action"})
    public static void setCommentTitle(TextView view, String actor, Comment.Action action) {
        if (action != null && !actor.isEmpty()) {
            @StringRes int actionRes = 0;
            switch (action) {
                case attach: {
                    actionRes = R.string.comment_action_attach;
                    break;
                }
                case comment: {
                    actionRes = R.string.comment_action_comment;
                    break;
                }
                case create: {
                    actionRes = R.string.comment_action_create;
                    break;
                }
                case mention: {
                    actionRes = R.string.comment_action_comment;
                    break;
                }
            }
            String actionText = view.getContext().getString(actionRes);
            String title = view.getContext().getString(R.string.text_place_holder_two_params, actor, actionText);
            view.setText(title);
        }
    }

    @BindingAdapter({"bindDate"})
    public static void setBindDate(DateWidget widget, Date date) {
        if (date != null) {
            widget.setCurrentDate(date);
        }
    }

    @BindingAdapter({"readOnly"})
    public static void setEditTextReadonly(EditText editText, boolean readOnly) {
        if (readOnly) {
            editText.setInputType(EditorInfo.TYPE_NULL);
            editText.setTextIsSelectable(true);
            editText.setKeyListener(null);
        }
    }

    @BindingAdapter({"gridSpacing", "includeEdge"})
    public static void setGridSpacing(RecyclerView view, float gridSpacing, boolean includeEdge) {
        view.addItemDecoration(new GridSpacingItemDecoration((int) gridSpacing, includeEdge));
    }

    @BindingAdapter("isTwilioAvailable")
    public static void setIsTwilioAvailable(MaterialTextView view, Boolean isAvailable) {
        @DrawableRes int iconAvail;
        @ColorRes int colorAvail;
        if (isAvailable) {
            iconAvail = R.drawable.ic_twilio_available;
            colorAvail = R.color.twilio_available;
        } else {
            iconAvail = R.drawable.ic_twilio_unavailable;
            colorAvail = R.color.twilio_unavailable;
        }
        TextViewCompat.setCompoundDrawableTintList(view, ContextCompat.getColorStateList(view.getContext(), colorAvail));
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(view.getContext(), iconAvail), null);
    }
}
