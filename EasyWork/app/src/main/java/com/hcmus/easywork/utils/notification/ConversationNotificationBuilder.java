package com.hcmus.easywork.utils.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.hcmus.easywork.App;
import com.hcmus.easywork.R;

public class ConversationNotificationBuilder extends BaseNotificationBuilder {
    public ConversationNotificationBuilder(@NonNull Context context, int notificationId) {
        super(context, App.CHANNEL_CONVERSATIONS);
        this.builder.setSmallIcon(R.drawable.ic_nav_conversations);
        if (Build.VERSION.SDK_INT >= 24) {
            Intent intent = new Intent(context, DirectReplyIntent.class);
            intent.putExtra(DirectReplyIntent.KEY_NOTIFY_ID, notificationId);
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, flags);
            RemoteInput remoteInput = new RemoteInput.Builder(DirectReplyIntent.KEY_TEXT_REPLY)
                    .setLabel(context.getString(R.string.hint_text_message)).build();

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_nav_conversations, context.getString(R.string.action_message_reply), pendingIntent).addRemoteInput(remoteInput).build();
            this.builder.addAction(action);
        }
    }
}
