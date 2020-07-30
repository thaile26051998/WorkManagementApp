package com.hcmus.easywork.utils;

import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.databinding.BindingAdapter;

import com.hcmus.easywork.R;
import com.hcmus.easywork.models.News;

import java.util.Date;

public class TextBindingUtils {
    /**
     * Bind username to welcome message in home screen
     *
     * @param view TextView component
     * @param text String value
     */
    @BindingAdapter("username")
    public static void setUsername(TextView view, String text) {
        view.setText(view.getContext().getString(R.string.welcome_message, text));
    }

    /**
     * Bind content of notification
     *
     * @param view     TextView component
     * @param newType  Action of notification as @StringRes
     * @param actor    Name of the actor as String
     * @param workName Project name or task name as String
     */
    @BindingAdapter(value = {"newType", "actor", "workName"})
    public static void setNotification(TextView view, News.NewsType newType, String actor, String workName) {
        if (newType != null && !actor.isEmpty() && !workName.isEmpty()) {
            @StringRes int actionLabel = 0;
            switch (newType) {
                case NORMAL: {
                    actionLabel = R.string.placeholder_assign;
                    break;
                }
                case ASSIGN: {
                    actionLabel = R.string.placeholder_finished;
                    break;
                }
                case MENTION: {
                    actionLabel = R.string.placeholder_mentioned;
                    break;
                }
                case REMOVE: {
                    actionLabel = R.string.placeholder_mentioned;
                    break;
                }
            }
            String title = view.getContext().getString(actionLabel, actor, workName);
            UtilsTextView.setText(view, title);
        }
    }

    @BindingAdapter(value = {"memberCount"})
    public static void setMemberCount(TextView view, byte memberCount) {
        if (memberCount == 1) {
            view.setText(R.string.text_member_count_one);
        } else {
            view.setText(view.getContext().getString(R.string.text_member_count_more, (int) memberCount));
        }
    }

    @BindingAdapter(value = {"projectName"})
    public static void setProjectName(TextView view, String projectName) {
        view.setText(projectName);
    }

    @BindingAdapter(value = {"messageTime"})
    public static void setMessageTime(TextView view, Date time) {
        if (time != null) {
            view.setText(UtilsTime.toFormat(time, "hh:mm dd/M"));
        }
    }

    @BindingAdapter("dateTime")
    public static void setDateTime(TextView view, Date dateTime) {
        if (dateTime != null) {
            view.setText(UtilsTime.toFormat(dateTime, view.getContext().getString(R.string.format_standard_date)));
        }
    }
}
