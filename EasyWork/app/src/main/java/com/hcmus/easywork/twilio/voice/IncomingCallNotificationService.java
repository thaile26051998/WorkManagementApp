package com.hcmus.easywork.twilio.voice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hcmus.easywork.ActivityMain;
import com.hcmus.easywork.App;
import com.hcmus.easywork.R;
import com.hcmus.easywork.twilio.shared.Constants;
import com.twilio.voice.CallInvite;

public class IncomingCallNotificationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            CallInvite callInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            int notificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
            switch (action) {
                case Constants.ACTION_INCOMING_CALL:
                    handleIncomingCall(callInvite, notificationId);
                    break;
                case Constants.ACTION_ACCEPT:
                    accept(callInvite, notificationId);
                    break;
                case Constants.ACTION_REJECT: {
                    if (callInvite != null)
                        reject(callInvite, notificationId);
                    break;
                }
                case Constants.ACTION_CANCEL_CALL:
                    handleCancelledCall(intent);
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification(CallInvite callInvite, int notificationId, String channelId) {
        Intent intent = new Intent(this, ActivityMain.class)
                .setAction(Constants.ACTION_INCOMING_CALL_NOTIFICATION)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bundle extras = new Bundle();
        extras.putString(Constants.CALL_SID_KEY, callInvite.getCallSid());
        return buildNotification(callInvite.getFrom(), pendingIntent, extras, callInvite, notificationId, channelId);
    }

    private Notification buildNotification(String caller, PendingIntent pendingIntent, Bundle extras,
                                           final CallInvite callInvite, int notificationId, String channelId) {
        PendingIntent piRejectIntent = createPendingIntent(Constants.ACTION_REJECT, callInvite, notificationId);
        PendingIntent piAcceptIntent = createPendingIntent(Constants.ACTION_ACCEPT, callInvite, notificationId);

        NotificationCompat.Builder compatBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.baseline_call_end_24)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_voice_calling, caller))
                .setCategory(Notification.CATEGORY_CALL)
                .setExtras(extras)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_delete, getString(R.string.call_action_decline), piRejectIntent)
                .addAction(android.R.drawable.ic_menu_call, getString(R.string.call_action_answer), piAcceptIntent)
                .setFullScreenIntent(pendingIntent, true);
        return compatBuilder.build();
    }

    private PendingIntent createPendingIntent(String action, CallInvite callInvite, int notificationId) {
        Intent intent = new Intent(getApplicationContext(), this.getClass())
                .setAction(action)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        return PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void accept(CallInvite callInvite, int notificationId) {
        endForeground();
        Intent activeCallIntent = new Intent(this, ActivityMain.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
                .setAction(Constants.ACTION_ACCEPT);
        startActivity(activeCallIntent);
    }

    private void reject(CallInvite callInvite, int notificationId) {
        endForeground();
        callInvite.reject(getApplicationContext());
        Intent intent = new Intent(this, ActivityMain.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
                .setAction(Constants.ACTION_REJECT);
        startActivity(intent);
    }

    private void handleCancelledCall(Intent intent) {
        endForeground();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleIncomingCall(CallInvite callInvite, int notificationId) {
        setCallInProgressNotification(callInvite, notificationId);
        sendCallInviteToActivity(callInvite, notificationId);
    }

    private void endForeground() {
        stopForeground(true);
    }

    private void setCallInProgressNotification(CallInvite callInvite, int notificationId) {
        String channelId = isAppVisible() ? App.CHANNEL_VOICE_HIGH : App.CHANNEL_VOICE_LOW;
        startForeground(notificationId, createNotification(callInvite, notificationId, channelId));
    }

    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
        if (Build.VERSION.SDK_INT >= 29 && !isAppVisible()) {
            return;
        }
        Intent intent = new Intent(this, ActivityMain.class)
                .setAction(Constants.ACTION_INCOMING_CALL)
                .putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
                .putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner.get().getLifecycle()
                .getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }
}
