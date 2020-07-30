package com.hcmus.easywork;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.hcmus.easywork.databinding.ActivityMainBinding;
import com.hcmus.easywork.models.task.SingleTaskViewModel;
import com.hcmus.easywork.twilio.shared.Constants;
import com.hcmus.easywork.twilio.shared.SoundPoolManager;
import com.hcmus.easywork.twilio.shared.TwilioServices;
import com.hcmus.easywork.twilio.shared.TwilioViewModel;
import com.hcmus.easywork.twilio.voice.TwilioVoiceCallViewModel;
import com.hcmus.easywork.utils.SharedPreferencesManager;
import com.hcmus.easywork.viewmodels.auth.AuthenticationViewModel;
import com.hcmus.easywork.viewmodels.auth.SelfLoginException;
import com.hcmus.easywork.viewmodels.chat.ConversationViewModel;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.hcmus.easywork.viewmodels.news.NewsViewModel;
import com.hcmus.easywork.viewmodels.project.ProjectViewModel;
import com.hcmus.easywork.viewmodels.project.SingleProjectViewModel;
import com.hcmus.easywork.viewmodels.task.TaskViewModel;
import com.linkedin.android.spyglass.ui.MentionsEditText;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;

import java.util.Locale;
import java.util.Set;

public class ActivityMain extends AppCompatActivity
        implements NavController.OnDestinationChangedListener {
    private static final String TAG = "EW_ActivityMain";
    private ActivityMainBinding binding;
    private NavController navController;
    private AuthenticationViewModel authViewModel;
    // region Call
    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;
    private int identity = 0; //By default
    private NotificationManagerCompat notificationManagerCompat;
    private TwilioVoiceCallViewModel twilioVoiceCallViewModel;
    private final Call.Listener callListener = callListener();
    private TwilioViewModel twilioViewModel;
    // endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ActivityMain.this, R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        navController.addOnDestinationChangedListener(this);
        notificationManagerCompat = App.getNotificationManagerCompat(this);

        authViewModel = getViewModel(AuthenticationViewModel.class);
        if (SharedPreferencesManager.getAccessToken() == null) {
            getNavController().navigate(R.id.action_authenticate);
        } else {
            try {
                getNavController().navigate(R.id.action_authenticate);
                authViewModel.selfLogin();
            } catch (SelfLoginException e) {
                e.printStackTrace();
            }
        }

        twilioVoiceCallViewModel = getViewModel(TwilioVoiceCallViewModel.class);
        twilioViewModel = getViewModel(TwilioViewModel.class);
        setViewModel();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        switch (destination.getId()) {
            case R.id.nav_notification:
            case R.id.nav_conversations:
            case R.id.nav_tasks:
            case R.id.nav_projects: {
                binding.bottomNavView.setVisibility(View.VISIBLE);
                break;
            }
            default: {
                binding.bottomNavView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Clear focus for any EditText
        // Reference: https://gist.github.com/sc0rch/7c982999e5821e6338c25390f50d2993
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText && !(v instanceof MentionsEditText)) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setViewModel() {
        NewsViewModel newsViewModel = getViewModel(NewsViewModel.class);
//        newsViewModel.getLoadingNewsResult().observe(this, notifications -> {
//            int count = 0;
//            for (News item : notifications) {
//                if (!item.isWatched()) {
//                    count++;
//                }
//            }
//            if (count == 0) {
//                binding.bottomNavView.removeBadge(R.id.nav_notification);
//            } else {
//                binding.bottomNavView.getOrCreateBadge(R.id.nav_notification).setNumber(count);
//            }
//        });

        // TODO: Observe user's changes to update userId
        ProjectViewModel projectViewModel = getViewModel(ProjectViewModel.class);
        TaskViewModel taskViewModel = getViewModel(TaskViewModel.class);
        ConversationViewModel conversationViewModel = getViewModel(ConversationViewModel.class);
        SingleGroupViewModel singleGroupViewModel = getViewModel(SingleGroupViewModel.class);
        SingleProjectViewModel singleProjectViewModel = getViewModel(SingleProjectViewModel.class);
        SingleTaskViewModel singleTaskViewModel = getViewModel(SingleTaskViewModel.class);
        authViewModel.getUser().observe(this, user -> {
            if (user != null) {
                int userId = user.getUserId();
                projectViewModel.setUserId(userId);
                taskViewModel.setUserId(userId);
                conversationViewModel.setUserId(userId);
                singleGroupViewModel.setUserId(userId);
                singleProjectViewModel.setUserId(userId);
                singleTaskViewModel.setUserId(userId);
                newsViewModel.setUserId(userId);
                identity = user.getUserId();
                retrieveAccessToken();
            }
        });

        twilioVoiceCallViewModel.getCallerId().observe(this, integer -> {
            if (integer != null) {
                log("Make call to: " + integer);
                activeCall = TwilioServices.getInstance().callToUserId(ActivityMain.this, integer, callListener);
            }
        });

        twilioVoiceCallViewModel.getCallState().observe(this, voiceCallState -> {
            log("Voice call state in MainActivity: " + voiceCallState);
            switch (voiceCallState) {
                case INIT:
                case WAITING:
                case CONNECTED:
                case RECONNECTING:
                case RECONNECTED:
                case CONNECT_QUALITY_CHANGED: {
                    break;
                }
                case CONNECT_FAILED: {
                    twilioVoiceCallViewModel.resetCaller();
                    break;
                }
                case DISCONNECTED: {
                    if (activeCall != null) {
                        activeCall.disconnect();
                        activeCall = null;
                    }
                    SoundPoolManager.getInstance(this).playDisconnect();
                    twilioVoiceCallViewModel.resetCaller();
                    twilioVoiceCallViewModel.onInit();
                    break;
                }
            }
        });

        twilioVoiceCallViewModel.getAudioState().observe(this, aBoolean -> {
            if (activeCall != null) {
                activeCall.mute(aBoolean == null ? false : aBoolean);
            }
        });
    }

    private <T extends ViewModel> T getViewModel(Class<T> modelClass) {
        return new ViewModelProvider(this).get(modelClass);
    }

    @Override
    protected void onStop() {
        SoundPoolManager.getInstance(this).release();
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
            switch (action) {
                case Constants.ACTION_INCOMING_CALL:
                    handleIncomingCall();
                    break;
                case Constants.ACTION_INCOMING_CALL_NOTIFICATION:
                    showIncomingCallDialog();
                    break;
                case Constants.ACTION_CANCEL_CALL:
                    handleCancel();
                    break;
                case Constants.ACTION_FCM_TOKEN:
                    retrieveAccessToken();
                    break;
                case Constants.ACTION_ACCEPT:
                    answer();
                    break;
                case Constants.ACTION_REJECT: {
                    handleRejectCall();
                    twilioVoiceCallViewModel.onDisconnected();
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void handleIncomingCall() {
        String caller = activeCallInvite.getFrom();
        if (caller != null)
            twilioVoiceCallViewModel.setCallerId(caller);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showIncomingCallDialog();
        } else {
            if (isAppVisible()) {
                showIncomingCallDialog();
            }
        }
    }

    private void handleCancel() {
        SoundPoolManager.getInstance(this).stopRinging();
        notificationManagerCompat.cancel(activeCallNotificationId);
    }

    private void handleRejectCall() {
        SoundPoolManager.getInstance(this).stopRinging();
        notificationManagerCompat.cancel(activeCallNotificationId);
    }

    private void showIncomingCallDialog() {
        SoundPoolManager.getInstance(this).playRinging();
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner.get().getLifecycle()
                .getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }

    private void retrieveAccessToken() {
        TwilioServices.getInstance().register(this, identity, new TwilioServices.OnTwilioServiceRegisteredListener() {
            @Override
            public void onRegistered(String twIdentity, String fcmToken, String twilioToken) {
                twilioViewModel.setVoiceAvailable(true);
            }

            @Override
            public void onFailure(String message) {
                makePopup("Twilio service error: " + message);
                twilioViewModel.setVoiceAvailable(false);
            }
        });
        TwilioServices.getInstance().registerVideo(identity, new TwilioServices.OnTwilioVideoServiceRegisteredListener() {
            @Override
            public void onRegistered(String identity, String twilioToken) {
                twilioViewModel.setVideoAvailable(true);
            }

            @Override
            public void onFailure(String errorMessage) {
                makePopup("Twilio video service failed to register: " + errorMessage);
                twilioViewModel.setVideoAvailable(true);
            }
        });
    }

    public void makePopup(String message) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton(R.string.popup_action_ok, null)
                .show();
    }

    private void answer() {
        SoundPoolManager.getInstance(this).stopRinging();
        getNavController().navigate(R.id.action_open_single_voice_call);
        activeCall = activeCallInvite.accept(this, callListener);
        notificationManagerCompat.cancel(activeCallNotificationId);
    }

    private void log(String logMessage) {
        Log.println(Log.INFO, TAG, logMessage);
    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            @Override
            public void onRinging(@NonNull Call call) {
                log("Ringing");
                twilioVoiceCallViewModel.onRinging();
            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {
                String message = String.format(Locale.US,
                        "Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                makeSnack(message);
                log(message);
                twilioVoiceCallViewModel.onConnectFailure();
            }

            @Override
            public void onConnected(@NonNull Call call) {
                log("Connected");
                twilioVoiceCallViewModel.onConnected();
                log("Connected call from: " + call.getFrom());
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                log("onReconnecting");
                twilioVoiceCallViewModel.onReconnecting();
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                log("onReconnected");
                twilioVoiceCallViewModel.onReconnected();
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
                if (error != null) {
                    String message = String.format(Locale.US, "Call Error: %d, %s",
                            error.getErrorCode(), error.getMessage());
                    makeSnack(message);
                }
                log("Disconnected");
                twilioVoiceCallViewModel.onDisconnected();
            }

            public void onCallQualityWarningsChanged(@NonNull Call call,
                                                     @NonNull Set<Call.CallQualityWarning> currentWarnings,
                                                     @NonNull Set<Call.CallQualityWarning> previousWarnings) {
                currentWarnings.retainAll(previousWarnings);
                previousWarnings.removeAll(currentWarnings);
                String message = "Newly raised warnings: " + currentWarnings + " Clear warnings " + previousWarnings;
                makeSnack(message);
                log(message);
                twilioVoiceCallViewModel.onCallQualityWarningsChanged();
            }
        };
    }

    public void makeSnack(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setAction(R.string.snack_bar_dismiss, v1 -> {
                }).show();
    }

    public NavController getNavController() {
        return this.navController;
    }
}
