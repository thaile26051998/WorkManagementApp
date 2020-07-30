package com.hcmus.easywork.ui.chat.video;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentGroupVideoCallBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.models.twilio.Participant;
import com.hcmus.easywork.twilio.shared.TwilioServices;
import com.hcmus.easywork.twilio.shared.TwilioUIUtil;
import com.hcmus.easywork.twilio.video.RemoteParticipantListener;
import com.hcmus.easywork.twilio.video.TwilioRoomOptions;
import com.hcmus.easywork.twilio.video.TwilioVideoConnection;
import com.hcmus.easywork.ui.permission.FragmentCameraPermission;
import com.hcmus.easywork.viewmodels.chat.SingleGroupViewModel;
import com.twilio.video.LocalParticipant;
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

import java.util.ArrayList;
import java.util.List;

public class FragmentGroupVideoCall extends BaseFragment<FragmentGroupVideoCallBinding> {
    private Room room;
    private SingleGroupViewModel groupViewModel;
    private TwilioRoomOptions twilioRoomOptions;
    private TwilioVideoConnection twilioVideoConnection;
    private MutableLiveData<Boolean> isVideoCallReady;
    private Room.Listener roomListener;
    private RemoteParticipantListener remoteParticipantListener;
    private AdapterParticipant adapterParticipant;
    private int groupId = 0;
    private boolean isComponentReady = false, isGroupReady = false;
    private List<Participant> participants;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_group_video_call;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupViewModel = createViewModel(SingleGroupViewModel.class);
        twilioRoomOptions = new TwilioRoomOptions();
        twilioVideoConnection = new TwilioVideoConnection(activity);
        isVideoCallReady = new MutableLiveData<>(false);
        participants = new ArrayList<>();
        roomListener = getRoomListener();
        remoteParticipantListener = getRemoteParticipantListener();
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.buttons.btnMute.setOnClickListener(l -> toggleAudio());
        binding.buttons.btnEndCall.setOnClickListener(l -> endCall());
        binding.buttons.btnToggleCamera.setOnClickListener(l -> toggleCamera());
        binding.btnSwitchCamera.setOnClickListener(l -> switchCamera());

        adapterParticipant = new AdapterParticipant(activity, getViewLifecycleOwner());
        binding.rvParticipants.setAdapter(adapterParticipant);
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        groupViewModel.get().observe(getViewLifecycleOwner(), group -> {
            groupId = group.getGroupId();
            makePopup("Room ID: " + groupId);
            isGroupReady = true;
            isVideoCallReady.setValue(isComponentReady);
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
            //twilioVideoConnection.addThumbnailViewRender(binding.myVideoView);
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
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
        }
        twilioVideoConnection.onDestroy();
        super.onDestroy();
    }

    private void log(String logMsg) {
        Log.println(Log.INFO, "GroupVideoCall", logMsg);
    }

    // region Video call interaction
    private void toggleAudio() {
        boolean isEnabled = twilioVideoConnection.toggleAudio();
        TwilioUIUtil.setAudioIcon(binding.buttons.btnMute, isEnabled);
        if (participants.size() > 0) {
            participants.get(0).setAudioEnabled(isEnabled);
        }
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
        if (participants.size() > 0) {
            participants.get(0).setVideoEnabled(toggled);
        }
    }

    private void switchCamera() {
        twilioVideoConnection.switchCamera(new TwilioVideoConnection.OnSwitchCameraListener() {
            @Override
            public void onSwitch(boolean isBackCamera) {
                //binding.myVideoView.setMirror(isBackCamera);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void connectToRoom() {
        if (groupId != 0) {
            room = TwilioServices.getInstance().connectToRoom(activity, groupId, twilioVideoConnection.getLocalAudioTrack(),
                    twilioVideoConnection.getLocalVideoTrack(), twilioRoomOptions, roomListener);
        }
    }
    // endregion

    private Room.Listener getRoomListener() {
        return new Room.Listener() {
            @Override
            public void onDominantSpeakerChanged(@NonNull Room room, @Nullable RemoteParticipant remoteParticipant) {
                Participant p = findParticipant(remoteParticipant);
                if (p != null) {
                    // Indicate current speaker
                    p.setDominantSpeaking(true);
                    log("Dominant speaker: " + remoteParticipant.getIdentity());
                } else {
                    log("Dominant speaker is unknown or null");
                }
                // Clear other speakers
                for (Participant pi : participants) {
                    if (pi != p) {
                        pi.setDominantSpeaking(false);
                    }
                }
            }

            @Override
            public void onConnected(@NonNull Room room) {
                log("onConnected");
                showLocalParticipant(room.getLocalParticipant());
                loadRoomParticipants(room);
            }

            @Override
            public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onReconnected(@NonNull Room room) {

            }

            @Override
            public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
                log("onDisconnected");
                twilioVideoConnection.removeLocalParticipant();
                FragmentGroupVideoCall.this.room = null;
            }

            @Override
            public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                log("onParticipantConnected: " + remoteParticipant.getIdentity());
                remoteParticipant.setListener(remoteParticipantListener);
                loadRoomParticipants(room);
            }

            @Override
            public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
                log("onParticipantDisconnected: " + remoteParticipant.getIdentity());
                loadRoomParticipants(room);
            }

            @Override
            public void onRecordingStarted(@NonNull Room room) {

            }

            @Override
            public void onRecordingStopped(@NonNull Room room) {

            }
        };
    }

    private RemoteParticipantListener getRemoteParticipantListener() {
        return new RemoteParticipantListener() {
            @Override
            public void onAudioTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication trackPublication, boolean isPublished) {

            }

            @Override
            public void onAudioTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication trackPublication,
                                                     @NonNull RemoteAudioTrack audioTrack, boolean isSubscribed) {
                log("onAudioTrackSubscribeChanged, " + remoteParticipant.getIdentity() + " :" + isSubscribed);
                Participant participant = findParticipant(remoteParticipant);
                if (participant != null) {
                    if (isSubscribed) {
                        participant.setAudioTrack(audioTrack);
                    } else {
                        participant.setAudioEnabled(null);
                    }

                    participant.setAudioEnabled(isSubscribed ? audioTrack.isEnabled() : null);
                }
            }

            @Override
            public void onVideoTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant,
                                                   @NonNull RemoteVideoTrackPublication trackPublication, boolean isPublished) {

            }

            @Override
            public void onVideoTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication trackPublication,
                                                     @NonNull RemoteVideoTrack videoTrack, boolean isSubscribed) {
                log("onVideoTrackSubscribeChanged, " + remoteParticipant.getIdentity() + " :" + isSubscribed);
                Participant participant = findParticipant(remoteParticipant);
                if (participant != null) {
                    if (isSubscribed) {
                        participant.setVideoTrack(videoTrack);
                    } else {
                        participant.setRenderingState(false);
                        participant.setVideoEnabled(null);
                    }
                }
            }

            @Override
            public void onDataTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant,
                                                  @NonNull RemoteDataTrackPublication trackPublication, boolean isPublished) {

            }

            @Override
            public void onDataTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication trackPublication,
                                                    @NonNull RemoteDataTrack remoteDataTrack, boolean isSubscribed) {

            }

            @Override
            public void onAudioTrackChanged(@NonNull RemoteParticipant remoteParticipant,
                                            @NonNull RemoteAudioTrackPublication trackPublication, boolean isEnabled) {
                log("onAudioTrackChanged, " + remoteParticipant.getIdentity() + " : " + isEnabled);
                Participant participant = findParticipant(remoteParticipant);
                if (participant != null) {
                    participant.setAudioEnabled(isEnabled);
                }
            }

            @Override
            public void onVideoTrackChanged(@NonNull RemoteParticipant remoteParticipant,
                                            @NonNull RemoteVideoTrackPublication trackPublication, boolean isEnabled) {
                log("onVideoTrackChanged, " + remoteParticipant.getIdentity() + " : " + isEnabled);
                Participant participant = findParticipant(remoteParticipant);
                if (participant != null) {
                    participant.setVideoEnabled(isEnabled);
                }
            }

            @Override
            public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteAudioTrackPublication trackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                       @NonNull RemoteVideoTrackPublication trackPublication, @NonNull TwilioException twilioException) {

            }

            @Override
            public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant,
                                                      @NonNull RemoteDataTrackPublication trackPublication, @NonNull TwilioException twilioException) {

            }
        };
    }


    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        remoteParticipant.setListener(remoteParticipantListener);
        Participant participant = createParticipant(remoteParticipant);
        List<RemoteVideoTrackPublication> trackPublications = remoteParticipant.getRemoteVideoTracks();
        if (trackPublications.size() > 0) {
            RemoteVideoTrackPublication trackPublication = trackPublications.get(0);
            RemoteVideoTrack remoteVideoTrack = trackPublication.getRemoteVideoTrack();
            if (trackPublication.isTrackSubscribed() && remoteVideoTrack != null) {
                participant.setVideoTrack(remoteVideoTrack);
            }
        }
        participants.add(participant);
        log(participant.toString());
    }

    private VideoTrack getVideoTrack(RemoteParticipant p) {
        if (p.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication trackPublication = p.getRemoteVideoTracks().get(0);
            if (trackPublication.isTrackSubscribed() && trackPublication.getRemoteVideoTrack() != null) {
                return trackPublication.getRemoteVideoTrack();
            }
        }
        return null;
    }

    private VideoTrack getVideoTrack(LocalParticipant p) {
        return p.getVideoTracks().get(0).getVideoTrack();
    }

    @Nullable
    private Participant findParticipant(@Nullable RemoteParticipant rp) {
        if (rp != null) {
            String identity = rp.getIdentity();
            for (Participant p : participants) {
                if (identity.equals(p.mParticipantName.getValue())) {
                    return p;
                }
            }
        }
        return null;
    }

    private Participant createParticipant(@NonNull RemoteParticipant remoteParticipant) {
        Participant p = new Participant();
        p.mParticipantName.setValue(remoteParticipant.getIdentity());
        VideoTrack videoTrack = getVideoTrack(remoteParticipant);
        if (videoTrack != null) {
            p.setVideoTrack(videoTrack);
        }
        return p;
    }

    private Participant createParticipant(@NonNull LocalParticipant localParticipant) {
        Participant p = new Participant();
        p.mParticipantName.setValue(localParticipant.getIdentity());
        VideoTrack videoTrack = getVideoTrack(localParticipant);
        if (videoTrack != null) {
            p.setVideoTrack(videoTrack);
            log("RemoteVideoTrack for local is not null");
        } else {
            log("RemoteVideoTrack for local is null");
        }
        p.setVideoTrack(twilioVideoConnection.getLocalVideoTrack());
        p.setAudioTrack(twilioVideoConnection.getLocalAudioTrack());
        return p;
    }

    private void showLocalParticipant(LocalParticipant localParticipant) {
        twilioVideoConnection.addLocalParticipant(localParticipant);
    }

    private void loadRoomParticipants(@NonNull Room room) {
        // Clear existing participants
        participants.clear();
        // Load local participant
        LocalParticipant localParticipant = room.getLocalParticipant();
        if (localParticipant != null) {
            Participant p = createParticipant(localParticipant);
            participants.add(p);
            log(p.toString());
        }
        // Load remote participants
        List<RemoteParticipant> joinedParticipants = room.getRemoteParticipants();
        for (RemoteParticipant rp : joinedParticipants) {
            addRemoteParticipant(rp);
        }
        adapterParticipant.submitList(participants);
        adapterParticipant.notifyDataSetChanged();
    }

}
