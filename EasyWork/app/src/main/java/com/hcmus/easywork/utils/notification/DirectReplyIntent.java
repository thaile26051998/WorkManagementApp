package com.hcmus.easywork.utils.notification;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.RemoteInput;

import com.hcmus.easywork.App;

public class DirectReplyIntent extends IntentService {
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String KEY_NOTIFY_ID = "key_notify_id";

    public DirectReplyIntent() {
        super("DirectReplyIntent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CharSequence directReply = getMessageText(intent);
        if (directReply != null) {
            int notifyId = intent.getIntExtra(KEY_NOTIFY_ID, -1);
            Notification repliedNotification =
                    new ConversationNotificationBuilder(this, notifyId).get()
                            .setContentText("Received: " + directReply)
                            .build();

            App.getNotificationManager(this).cancel(notifyId);
            App.getNotificationManager(this).notify(notifyId, repliedNotification);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private CharSequence getMessageText(Intent intent) {
        // Decode the reply text
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}
