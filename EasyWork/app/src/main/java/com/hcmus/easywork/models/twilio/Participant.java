package com.hcmus.easywork.models.twilio;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.models.IComparableModel;
import com.twilio.video.AudioTrack;
import com.twilio.video.VideoRenderer;
import com.twilio.video.VideoTrack;

public class Participant implements IComparableModel<Participant> {
    public MutableLiveData<String> mParticipantName;
    private MutableLiveData<Boolean> mIsAudioEnabled;
    private MutableLiveData<Boolean> mIsVideoEnabled;
    private MutableLiveData<Boolean> mIsDominantSpeaking;
    private VideoTrack mVideoTrack;
    private AudioTrack mAudioTrack;
    private MutableLiveData<Boolean> mIsRendering;

    public Participant() {
        this.mIsAudioEnabled = new MutableLiveData<>(null);
        this.mIsVideoEnabled = new MutableLiveData<>(null);
        this.mIsDominantSpeaking = new MutableLiveData<>(false);
        this.mIsRendering = new MutableLiveData<>(null);
        mParticipantName = new MutableLiveData<>(null);
    }

    @Override
    public boolean isTheSame(@NonNull Participant item) {
        return mParticipantName.getValue() == item.mParticipantName.getValue();
    }

    public void setAudioEnabled(Boolean isEnabled) {
        this.mIsAudioEnabled.setValue(isEnabled);
    }

    public void setVideoEnabled(Boolean isEnabled) {
        this.mIsVideoEnabled.setValue(isEnabled);
    }

    public void setDominantSpeaking(boolean isSpeaking) {
        this.mIsDominantSpeaking.setValue(isSpeaking);
    }

    public LiveData<Boolean> getAudioEnabledState() {
        return this.mIsAudioEnabled;
    }

    public LiveData<Boolean> getVideoEnabledState() {
        return this.mIsVideoEnabled;
    }

    public LiveData<Boolean> getDominantSpeakingState() {
        return this.mIsDominantSpeaking;
    }

    public void removeRenderer(VideoRenderer renderer) {
        if (mVideoTrack != null) {
            mVideoTrack.removeRenderer(renderer);
        }
    }

    public VideoTrack getVideoTrack() {
        return this.mVideoTrack;
    }

    public void setVideoTrack(VideoTrack track) {
        this.mVideoTrack = track;
        setVideoEnabled(track.isEnabled());
        setRenderingState(true);
    }

    public AudioTrack getAudioTrack() {
        return this.mAudioTrack;
    }

    public void setAudioTrack(AudioTrack track) {
        this.mAudioTrack = track;
        setAudioEnabled(track.isEnabled());
    }

    public LiveData<Boolean> getRenderingState() {
        return this.mIsRendering;
    }

    public void setRenderingState(Boolean state) {
        this.mIsRendering.setValue(state);
    }

    @NonNull
    @Override
    public String toString() {
        return "Identity: " + mParticipantName.getValue() +
                ", \nAudioEnabled: " + mIsAudioEnabled.getValue() +
                ", VideoEnabled: " + mIsVideoEnabled.getValue() +
                ", \nisSpeaking: " + mIsDominantSpeaking.getValue() +
                ", VideoTrack is null: " + (mVideoTrack == null) +
                ", isRendering: " + mIsRendering.getValue();
    }
}
