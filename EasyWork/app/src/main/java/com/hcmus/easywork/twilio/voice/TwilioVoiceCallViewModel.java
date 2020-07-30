package com.hcmus.easywork.twilio.voice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TwilioVoiceCallViewModel extends ViewModel {
    private MutableLiveData<VoiceCallState> mVoiceCallState = new MutableLiveData<>(VoiceCallState.INIT);
    private MutableLiveData<Integer> mCallerId = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isAudioMuted = new MutableLiveData<>(null);

    public LiveData<VoiceCallState> getCallState() {
        return this.mVoiceCallState;
    }

    public LiveData<Integer> getCallerId() {
        return this.mCallerId;
    }

    public void setCallerId(String id) {
        try {
            int userId = Integer.parseInt(id.substring(4)); // TWID___
            mCallerId.setValue(userId);
        } catch (NumberFormatException nfe) {
            mCallerId.setValue(null);
        }
    }

    public LiveData<Boolean> getAudioState() {
        return this.isAudioMuted;
    }

    // region Call states
    public void onInit() {
        this.mVoiceCallState.postValue(VoiceCallState.INIT);
    }

    public void onRinging() {
        this.mVoiceCallState.setValue(VoiceCallState.WAITING);
    }

    public void onConnectFailure() {
        this.mVoiceCallState.setValue(VoiceCallState.CONNECT_FAILED);
    }

    public void onConnected() {
        this.mVoiceCallState.setValue(VoiceCallState.CONNECTED);
    }

    public void onReconnecting() {
        this.mVoiceCallState.setValue(VoiceCallState.RECONNECTING);
    }

    public void onReconnected() {
        this.mVoiceCallState.setValue(VoiceCallState.RECONNECTED);
    }

    public void onDisconnected() {
        this.mVoiceCallState.setValue(VoiceCallState.DISCONNECTED);
    }
    // endregion

    public void onCallQualityWarningsChanged() {
        this.mVoiceCallState.setValue(VoiceCallState.CONNECT_QUALITY_CHANGED);
    }

    public void callTo(int userId) {
        this.mCallerId.setValue(userId);
        this.mVoiceCallState.setValue(VoiceCallState.WAITING);
    }

    public void resetCaller() {
        this.mCallerId.setValue(null);
    }

    public void toggleAudio() {
        Boolean value = this.isAudioMuted.getValue();
        this.isAudioMuted.setValue(value != null && !value);
    }

    public enum VoiceCallState {
        INIT, WAITING, CONNECT_FAILED, CONNECTED,
        RECONNECTING, RECONNECTED, DISCONNECTED, CONNECT_QUALITY_CHANGED
    }
}
