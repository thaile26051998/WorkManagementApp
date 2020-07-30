package com.hcmus.easywork.ui.chat.video;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentSingleVideoCallBinding;
import com.hcmus.easywork.twilio.shared.TwilioServices;
import com.hcmus.easywork.twilio.shared.TwilioUIUtil;
import com.hcmus.easywork.twilio.video.RemoteParticipantListener;
import com.hcmus.easywork.twilio.video.TwilioRoomOptions;
import com.hcmus.easywork.twilio.video.TwilioVideoConnection;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.permission.FragmentCameraPermission;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.VideoTrack;

public class FragmentSingleVideoCall extends BaseFragment<FragmentSingleVideoCallBinding> {
    private static final String TAG = "SingleVideoCall";
    private Room room;
    private int savedVolumeControlStream;
    private SingleGroupViewModel groupViewModel;
    private TwilioRoomOptions twilioRoomOptions;
    private TwilioVideoConnection twilioVideoConnection;
    private MutableLiveData<Boolean> isAudioEnabled, isVideoEnabled;
    private MutableLiveData<String> caller;
    private int groupId = 0;
    private MutableLiveData<Boolean> isVideoCallReady;
    private boolean isComponentReady = false, isGroupReady = false;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_single_video_call;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedVolumeControlStream = activity.getVolumeControlStream();
        activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        twilioRoomOptions = new TwilioRoomOptions();
        twilioVideoConnection = new TwilioVideoConnection(activity);
        isAudioEnabled = new MutableLiveData<>(null);
        isVideoEnabled = new MutableLiveData<>(null);
        caller = new MutableLiveData<>(null);
        isVideoCallReady = new MutableLiveData<>(false);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.buttons.btnMute.setOnClickListener(l -> muteUnmute());
        binding.buttons.btnEndCall.setOnClickListener(l -> endCall());
        binding.buttons.btnToggleCamera.setOnClickListener(l -> toggleCamera());
        binding.btnSwitchCamera.setOnClickListener(l -> switchCamera());
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.get().observe(getViewLifecycleOwner(), group -> {
            groupId = group.getGroupId();
            // Because group ID is loaded from view model, it is observed before onResume.
            isGroupReady = true;
            isVideoCallReady.setValue(isComponentReady);
        });
        isAudioEnabled.observe(getViewLifecycleOwner(), enabled ->
                binding.callerVideoView.setAudioEnabled(enabled)
        );
        isVideoEnabled.observe(getViewLifecycleOwner(), enabled ->
                binding.callerVideoView.setVideoEnabled(enabled)
        );
        caller.observe(getViewLifecycleOwner(), callerName -> {
            if (callerName!= null){
                if (callerName.startsWith("TWID")) {
                    int id = Integer.parseInt(callerName.substring(4));
                    UserDataLookup.find(id).setOnRecordFoundListener(userRecord ->
                            binding.callerVideoView.setParticipantName(userRecord.getName()));
                } else {
                    binding.callerVideoView.setParticipantName(callerName);
                }
            }
        });
        isVideoCallReady.observe(getViewLifecycleOwner(), isReady -> {
            if (isReady) {
                connectToRoom();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!FragmentCameraPermission.hasPermissions(activity)) {
            getNavController().navigate(R.id.action_require_camera_permission);
        } else {
            twilioVideoConnection.onResume();
            twilioVideoConnection.addThumbnailViewRender(binding.myVideoView);
            // onResume is called after group ID has been observed.
            isComponentReady = true;
            isVideoCallReady.setValue(isGroupReady);
        }
    }

    @Override
    public void onPause() {
        twilioVideoConnection.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        activity.setVolumeControlStream(savedVolumeControlStream);
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
        }
        twilioVideoConnection.onDestroy();
        super.onDestroy();
    }

    private void connectToRoom() {
        if (groupId != 0) {
            room = TwilioServices.getInstance().connectToRoom(activity, groupId, twilioVideoConnection.getLocalAudioTrack(),
                    twilioVideoConnection.getLocalVideoTrack(), twilioRoomOptions, roomListener());
        }
    }

    private void muteUnmute() {
        boolean isEnabled = twilioVideoConnection.toggleAudio();
        TwilioUIUtil.setAudioIcon(binding.buttons.btnMute, isEnabled);
    }

    private void endCall() {
        if (room != null) {
            room.disconnect();
        }
        getNavController().popBackStack();
    }

    private void toggleCamera() {
        boolean toggled = twilioVideoConnection.toggleCamera();
        TwilioUIUtil.setCameraIcon(binding.buttons.btnToggleCamera, toggled);
    }

    private void switchCamera() {
        twilioVideoConnection.switchCamera(new TwilioVideoConnection.OnSwitchCameraListener() {
            @Override
            public void onSwitch(boolean isBackCamera) {
                binding.myVideoView.setMirror(isBackCamera);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        caller.setValue(remoteParticipant.getIdentity());
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication = remoteParticipant.getRemoteVideoTracks().get(0);
            if (remoteVideoTrackPublication.isTrackSubscribed() && remoteVideoTrackPublication.getRemoteVideoTrack() != null) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }
        remoteParticipant.setListener(remoteParticipantListener());
    }

    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        twilioVideoConnection.addThumbnailViewRender(binding.myVideoView);
        videoTrack.addRenderer(binding.callerVideoView.getVideoView());
    }

    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getIdentity().equals(caller.getValue())) {
            return;
        }
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication trackPublication = remoteParticipant.getRemoteVideoTracks().get(0);
            if (trackPublication.isTrackSubscribed() && trackPublication.getRemoteVideoTrack() != null) {
                removeParticipantVideo(trackPublication.getRemoteVideoTrack());
            }
            binding.callerVideoView.clear();
        }
        caller.setValue(null);
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeRenderer(binding.callerVideoView.getVideoView());
    }

    private void log(String logMsg) {
        Log.println(Log.INFO, TAG, logMsg);
    }

    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onDominantSpeakerChanged(@NonNull Room room, @Nullable RemoteParticipant remoteParticipant) {
                if (remoteParticipant != null) {
                    log("Dominant speaker: " + remoteParticipant.getIdentity());
                    String dominantSpeaker = remoteParticipant.getIdentity();
                    if (dominantSpeaker.equals(caller.getValue())) {
                        binding.callerVideoView.setDominantSpeaking(true);
                    }
                } else {
                    log("Unknown dominant speaker");
                    binding.callerVideoView.setDominantSpeaking(false);
                }
            }

            @Override
            public void onConnected(@NonNull Room room) {
                log("roomListener, onConnected");
                twilioVideoConnection.addLocalParticipant(room.getLocalParticipant());
                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                log("roomListener, onReconnecting");
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                log("roomListener, onReconnected");
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException e) {
                log("roomListener, onConnectFailure" + e.getLocalizedMessage());
            }

            @Override
            public void onDisconnected(@NonNull Room room, TwilioException e) {
                log("roomListener, onDisconnected");
                twilioVideoConnection.removeLocalParticipant();
                FragmentSingleVideoCall.this.room = null;
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                log("roomListener, onParticipantConnected");
                addRemoteParticipant(remoteParticipant);
                makeSnack(remoteParticipant.getIdentity() + " has joined");
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                log("roomListener, onParticipantDisconnected: " + remoteParticipant.getIdentity());
                removeRemoteParticipant(remoteParticipant);
                makeSnack(remoteParticipant.getIdentity() + " has left");
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {
                log("roomListener, onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {
                log("roomListener, onRecordingStopped");
            }
        };
    }

    private RemoteParticipantListener remoteParticipantListener() {
        return new RemoteParticipantListener() {
            @Override
            public void onAudioTrackPublishChanged(@NonNull RemoteParticipant participant,
                                                   @NonNull RemoteAudioTrackPublication trackPublication, boolean isPublished) {
                log("onAudioTrackPublishChanged");
            }

            @Override
            public void onAudioTrackSubscribeChanged(@NonNull RemoteParticipant participant, @NonNull RemoteAudioTrackPublication trackPublication,
                                                     @NonNull RemoteAudioTrack audioTrack, boolean isSubscribed) {
                log("onAudioTrackSubscribeChanged: " + isSubscribed);
                isAudioEnabled.setValue(isSubscribed ? audioTrack.isEnabled() : null);
            }

            @Override
            public void onVideoTrackPublishChanged(@NonNull RemoteParticipant participant,
                                                   @NonNull RemoteVideoTrackPublication trackPublication, boolean isPublished) {
                log("onVideoTrackPublishChanged");
            }

            @Override
            public void onVideoTrackSubscribeChanged(@NonNull RemoteParticipant participant, @NonNull RemoteVideoTrackPublication trackPublication,
                                                     @NonNull RemoteVideoTrack videoTrack, boolean isSubscribed) {
                log("onVideoTrackSubscribeChanged: " + isSubscribed);
                if (isSubscribed) {
                    addRemoteParticipantVideo(videoTrack);
                    isVideoEnabled.setValue(videoTrack.isEnabled());
                } else {
                    removeParticipantVideo(videoTrack);
                    isVideoEnabled.setValue(null);
                }
            }

            @Override
            public void onDataTrackPublishChanged(@NonNull RemoteParticipant participant,
                                                  @NonNull RemoteDataTrackPublication trackPublication, boolean isPublished) {
                log("onDataTrackPublishChanged");
            }

            @Override
            public void onDataTrackSubscribeChanged(@NonNull RemoteParticipant participant, @NonNull RemoteDataTrackPublication trackPublication,
                                                    @NonNull RemoteDataTrack dataTrack, boolean isSubscribed) {
                log("onDataTrackSubscribeChanged");
            }

            @Override
            public void onAudioTrackChanged(@NonNull RemoteParticipant participant,
                                            @NonNull RemoteAudioTrackPublication trackPublication, boolean isEnabled) {
                log("onAudioTrackChanged: " + isEnabled);
                isAudioEnabled.setValue(isEnabled);
            }

            @Override
            public void onVideoTrackChanged(@NonNull RemoteParticipant participant,
                                            @NonNull RemoteVideoTrackPublication trackPublication, boolean isEnabled) {
                log("onVideoTrackChanged: " + isEnabled);
                isVideoEnabled.setValue(isEnabled);
            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant participant, @NonNull RemoteAudioTrackPublication trackPublication,
                                                       @NonNull TwilioException twilioException) {
                log("onAudioTrackSubscriptionFailed: " + twilioException.getCode() + ", " + twilioException.getMessage());
            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant participant, @NonNull RemoteVideoTrackPublication trackPublication,
                                                       @NonNull TwilioException twilioException) {
                log("onVideoTrackSubscriptionFailed: " + twilioException.getCode() + ", " + twilioException.getMessage());
                makeSnack(String.format("Failed to subscribe to %s video track", participant.getIdentity()));
            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant participant, @NonNull RemoteDataTrackPublication trackPublication,
                                                      @NonNull TwilioException twilioException) {
                log("onDataTrackSubscriptionFailed: " + twilioException.getCode() + ", " + twilioException.getMessage());
            }
        };
    }
}
