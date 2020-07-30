package com.hcmus.easywork;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.hcmus.easywork.utils.SharedPreferencesManager;
import com.hcmus.easywork.twilio.shared.Constants;

public class App extends Application {
    public static final String CHANNEL_TASKS = "channel_tasks";
    public static final String CHANNEL_PROJECTS = "channel_projects";
    public static final String CHANNEL_CONVERSATIONS = "channel_conversations";
    public static final String CHANNEL_REMINDER = "channel_reminder";
    public static final String CHANNEL_VOICE_HIGH = Constants.VOICE_CHANNEL_HIGH_IMPORTANCE;
    public static final String CHANNEL_VOICE_LOW = Constants.VOICE_CHANNEL_LOW_IMPORTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        SharedPreferencesManager.initialize(getApplicationContext());
    }

    private void createNotificationChannels() {
        NotificationManagerCompat notificationManagerCompat = getNotificationManagerCompat(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channelTasks = new NotificationChannel(CHANNEL_TASKS,
                    getString(R.string.channel_tasks), importance);
            channelTasks.setDescription(getString(R.string.channel_tasks_description));

            NotificationChannel channelProjects = new NotificationChannel(CHANNEL_PROJECTS,
                    getString(R.string.channel_projects), importance);
            channelProjects.setDescription(getString(R.string.channel_projects_description));

            NotificationChannel channelConversations = new NotificationChannel(CHANNEL_CONVERSATIONS,
                    getString(R.string.channel_conversations), importance);
            channelConversations.setDescription(getString(R.string.channel_conversations_description));

            NotificationChannel channelReminder = new NotificationChannel(CHANNEL_REMINDER,
                    getString(R.string.channel_reminder), importance);
            channelReminder.setDescription(getString(R.string.channel_reminder_description));

            NotificationChannel channelCallInviteHigh = new NotificationChannel(CHANNEL_VOICE_HIGH,
                    getString(R.string.channel_voice_high), NotificationManager.IMPORTANCE_HIGH);
            channelCallInviteHigh.setLightColor(Color.GREEN);
            channelCallInviteHigh.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationChannel channelCallInviteLow = new NotificationChannel(CHANNEL_VOICE_LOW,
                    getString(R.string.channel_voice_low), NotificationManager.IMPORTANCE_LOW);
            channelCallInviteLow.setLightColor(Color.GREEN);
            channelCallInviteLow.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            notificationManagerCompat.createNotificationChannel(channelTasks);
            notificationManagerCompat.createNotificationChannel(channelProjects);
            notificationManagerCompat.createNotificationChannel(channelConversations);
            notificationManagerCompat.createNotificationChannel(channelReminder);
            notificationManagerCompat.createNotificationChannel(channelCallInviteHigh);
            notificationManagerCompat.createNotificationChannel(channelCallInviteLow);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static NotificationManagerCompat getNotificationManagerCompat(Context context) {
        return NotificationManagerCompat.from(context);
    }
}
