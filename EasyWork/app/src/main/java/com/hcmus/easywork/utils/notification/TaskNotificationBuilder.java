package com.hcmus.easywork.utils.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.hcmus.easywork.App;
import com.hcmus.easywork.R;

public class TaskNotificationBuilder extends BaseNotificationBuilder {
    public TaskNotificationBuilder(@NonNull Context context) {
        super(context, App.CHANNEL_TASKS);
        this.builder.setSmallIcon(R.drawable.ic_nav_tasks);
        Intent intentDismiss = new Intent();
        intentDismiss.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentDismiss, flags);
        NotificationCompat.Action.Builder actionBuilder = new NotificationCompat.Action.Builder(R.drawable.outline_delete_24, "Dismiss", pendingIntent);
        this.builder.addAction(actionBuilder.build());
    }
}
