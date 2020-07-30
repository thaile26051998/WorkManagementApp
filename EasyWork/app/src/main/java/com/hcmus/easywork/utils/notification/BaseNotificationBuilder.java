package com.hcmus.easywork.utils.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public abstract class BaseNotificationBuilder {
    protected NotificationCompat.Builder builder;

    public BaseNotificationBuilder(@NonNull Context context, @NonNull String channelId) {
        // Empty destination, put context and class as destination
        Intent intent = new Intent();
        // Unique requestId
        int requestId = (int) System.currentTimeMillis();
        // Flag to dismiss notification on touch
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId, intent, flags);

        builder = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder get() {
        return this.builder;
    }
}
