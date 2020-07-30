package com.hcmus.easywork.twilio.video;

import androidx.annotation.NonNull;

import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;

public abstract class RemoteParticipantListener implements RemoteParticipant.Listener, IRemoteParticipantListener {
    @Override
    public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                      @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
        onAudioTrackPublishChanged(remoteParticipant, remoteAudioTrackPublication, true);
    }

    @Override
    public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                        @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
        onAudioTrackPublishChanged(remoteParticipant, remoteAudioTrackPublication, false);
    }

    @Override
    public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                       @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                       @NonNull RemoteAudioTrack remoteAudioTrack) {
        onAudioTrackSubscribeChanged(remoteParticipant, remoteAudioTrackPublication, remoteAudioTrack, true);
    }

    @Override
    public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                         @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication,
                                         @NonNull RemoteAudioTrack remoteAudioTrack) {
        onAudioTrackSubscribeChanged(remoteParticipant, remoteAudioTrackPublication, remoteAudioTrack, false);
    }

    @Override
    public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                      @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
        onVideoTrackPublishChanged(remoteParticipant, remoteVideoTrackPublication, true);
    }

    @Override
    public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                        @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
        onVideoTrackPublishChanged(remoteParticipant, remoteVideoTrackPublication, false);
    }

    @Override
    public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                       @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                       @NonNull RemoteVideoTrack remoteVideoTrack) {
        onVideoTrackSubscribeChanged(remoteParticipant, remoteVideoTrackPublication, remoteVideoTrack, true);
    }

    @Override
    public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                         @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication,
                                         @NonNull RemoteVideoTrack remoteVideoTrack) {
        onVideoTrackSubscribeChanged(remoteParticipant, remoteVideoTrackPublication, remoteVideoTrack, false);
    }

    @Override
    public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant,
                                     @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
        onDataTrackPublishChanged(remoteParticipant, remoteDataTrackPublication, true);
    }

    @Override
    public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant,
                                       @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {
        onDataTrackPublishChanged(remoteParticipant, remoteDataTrackPublication, false);
    }

    @Override
    public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant,
                                      @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                      @NonNull RemoteDataTrack remoteDataTrack) {
        onDataTrackSubscribeChanged(remoteParticipant, remoteDataTrackPublication, remoteDataTrack, true);
    }

    @Override
    public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant,
                                        @NonNull RemoteDataTrackPublication remoteDataTrackPublication,
                                        @NonNull RemoteDataTrack remoteDataTrack) {
        onDataTrackSubscribeChanged(remoteParticipant, remoteDataTrackPublication, remoteDataTrack, false);
    }

    @Override
    public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant,
                                    @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
        onAudioTrackChanged(remoteParticipant, remoteAudioTrackPublication, true);
    }

    @Override
    public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant,
                                     @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
        onAudioTrackChanged(remoteParticipant, remoteAudioTrackPublication, false);
    }

    @Override
    public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant,
                                    @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
        onVideoTrackChanged(remoteParticipant, remoteVideoTrackPublication, true);
    }

    @Override
    public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant,
                                     @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
        onVideoTrackChanged(remoteParticipant, remoteVideoTrackPublication, false);
    }
}
