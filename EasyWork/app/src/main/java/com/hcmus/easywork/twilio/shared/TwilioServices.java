package com.hcmus.easywork.twilio.shared;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.twilio.video.TwilioRoomOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Room;
import com.twilio.video.Video;
import com.twilio.voice.Call;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import java.util.Collections;
import java.util.HashMap;

public class TwilioServices {
    private static TwilioServices instance;
    private final TwilioImpl twilio;
    private OnTwilioServiceRegisteredListener registeredListener;
    private OnTwilioVideoServiceRegisteredListener videoRegisteredListener;
    private String twilioToken;
    private String twilioVideoToken;

    private TwilioServices() {
        twilio = new TwilioImpl();
    }

    public static synchronized TwilioServices getInstance() {
        if (instance == null) {
            instance = new TwilioServices();
        }
        return instance;
    }

    private void log(String log) {
        Log.println(Log.INFO, "TwilioServices", log + "\n");
    }

    public void register(@NonNull Activity activity, int userId, @NonNull OnTwilioServiceRegisteredListener listener) {
        this.registeredListener = listener;
        String identity = getTwilioIdentity(userId);
        twilio.getAccessToken(identity).enqueue(new ResponseManager.OnResponseListener<String>() {
            @Override
            public void onResponse(String twilioToken) {
                registerFirebase(activity, identity, twilioToken);
            }

            @Override
            public void onFailure(String errorMessage) {
                registeredListener.onFailure(errorMessage);
            }
        });
    }

    public void registerVideo(int userId, @NonNull OnTwilioVideoServiceRegisteredListener listener) {
        this.videoRegisteredListener = listener;
        String identity = getTwilioIdentity(userId);
        twilio.getVideoAccessToken(identity, null).enqueue(new ResponseManager.OnResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                videoRegisteredListener.onRegistered(identity, response);
                TwilioServices.this.twilioVideoToken = response;
            }

            @Override
            public void onFailure(String message) {
                videoRegisteredListener.onFailure(message);
            }
        });
    }

    private void registerFirebase(@NonNull Activity activity, String identity, String twilioToken) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(activity, instanceIdResult -> {
                    final String fcmToken = instanceIdResult.getToken();
                    registerVoice(identity, twilioToken, fcmToken);
                })
                .addOnFailureListener(activity, e -> {
                    String errorMessage = e.getLocalizedMessage();
                    registeredListener.onFailure(errorMessage);
                });
    }

    private void registerVoice(String identity, String twilioToken, String fcmToken) {
        Voice.register(twilioToken, Voice.RegistrationChannel.FCM, fcmToken, new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                TwilioServices.this.twilioToken = twilioToken;
                registeredListener.onRegistered(identity, fcmToken, accessToken);
                log(String.format("identity: %s, fcmToken: %s, twilioToken: %s", identity, fcmToken, twilioToken));
            }

            @Override
            public void onError(@NonNull RegistrationException registrationException, @NonNull String accessToken, @NonNull String fcmToken) {
                registeredListener.onFailure(registrationException.getLocalizedMessage());
            }
        });
    }

    public Call callToUserId(Context context, int userId, Call.Listener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("to", getTwilioIdentity(userId));
        ConnectOptions options = new ConnectOptions.Builder(twilioToken)
                .params(params)
                .build();
        return Voice.connect(context, options, listener);
    }

    public Room connectToRoom(Context context, int groupId, LocalAudioTrack audioTrack, LocalVideoTrack videoTrack,
                              TwilioRoomOptions roomOptions, Room.Listener listener) {
        com.twilio.video.ConnectOptions.Builder connectOptionsBuilder = new com.twilio.video.ConnectOptions.Builder(twilioVideoToken)
                .roomName(getTwilioRoom(groupId))
                .enableDominantSpeaker(true)
                .audioTracks(Collections.singletonList(audioTrack))
                .videoTracks(Collections.singletonList(videoTrack))
                .preferAudioCodecs(Collections.singletonList(roomOptions.getAudioCodec()))
                .preferVideoCodecs(Collections.singletonList(roomOptions.getVideoCodec()))
                .encodingParameters(roomOptions.getEncodingParameters())
                .enableAutomaticSubscription(roomOptions.isAutomaticSubscriptionEnabled());
        return Video.connect(context, connectOptionsBuilder.build(), listener);
    }

    private String getTwilioRoom(int groupId) {
        return "TWROOM" + groupId;
    }

    private String getTwilioIdentity(int userId) {
        return "TWID" + userId;
    }

    public interface OnTwilioServiceRegisteredListener {
        void onRegistered(String identity, String fcmToken, String twilioToken);

        void onFailure(String errorMessage);
    }

    public interface OnTwilioVideoServiceRegisteredListener {
        void onRegistered(String identity, String twilioToken);

        void onFailure(String errorMessage);
    }
}
