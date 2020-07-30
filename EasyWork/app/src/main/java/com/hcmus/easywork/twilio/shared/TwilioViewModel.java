package com.hcmus.easywork.twilio.shared;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TwilioViewModel extends ViewModel {
    public MutableLiveData<Boolean> isVideoAvailable = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isVoiceAvailable = new MutableLiveData<>(false);

    public void setVoiceAvailable(boolean available) {
        this.isVoiceAvailable.setValue(available);
    }

    public void setVideoAvailable(boolean available) {
        this.isVideoAvailable.setValue(available);
    }
}
