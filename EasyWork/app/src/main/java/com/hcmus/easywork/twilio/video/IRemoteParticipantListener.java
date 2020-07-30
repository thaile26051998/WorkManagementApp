package com.hcmus.easywork.twilio.video;

import androidx.annotation.NonNull;

import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;

public interface IRemoteParticipantListener {
    void onAudioTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant,
                                    @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                    boolean isPublished);

    void onAudioTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant,
                                      @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                      @NonNull RemoteAudioTrack remoteAudioTrack,
                                      boolean isSubscribed);

    void onVideoTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant,
                                    @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                    boolean isPublished);

    void onVideoTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant,
                                      @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                      @NonNull RemoteVideoTrack remoteVideoTrack,
                                      boolean isSubscribed);

    void onDataTrackPublishChanged(@NonNull RemoteParticipant remoteParticipant,
                                   @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                   boolean isPublished);

    void onDataTrackSubscribeChanged(@NonNull RemoteParticipant remoteParticipant,
                                     @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                     @NonNull RemoteDataTrack remoteDataTrack,
                                     boolean isSubscribed);

    void onAudioTrackChanged(@NonNull RemoteParticipant remoteParticipant,
                             @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                             boolean isEnabled);

    void onVideoTrackChanged(@NonNull RemoteParticipant remoteParticipant,
                             @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                             boolean isEnabled);
}
