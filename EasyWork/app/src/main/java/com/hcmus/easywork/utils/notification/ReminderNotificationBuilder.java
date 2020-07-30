package com.hcmus.easywork.utils.notification;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcmus.easywork.App;
import com.hcmus.easywork.R;

public class ReminderNotificationBuilder extends BaseNotificationBuilder {
    public ReminderNotificationBuilder(@NonNull Context context) {
        super(context, App.CHANNEL_REMINDER);
        this.builder.setSmallIcon(R.drawable.ic_nav_conversations);
    }
}
