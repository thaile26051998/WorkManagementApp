package com.hcmus.easywork.twilio.video;

import com.hcmus.easywork.twilio.shared.Constants;
import com.twilio.video.AudioCodec;
import com.twilio.video.EncodingParameters;
import com.twilio.video.VideoCodec;

public class TwilioRoomOptions {
    private final AudioCodec audioCodec;
    private final VideoCodec videoCodec;
    private final EncodingParameters encodingParameters;
    private final boolean enableAutomaticSubscription;

    public TwilioRoomOptions() {
        audioCodec = TwilioAudioCodec.getAudioCodec(Constants.PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = TwilioVideoCodec.getVideoCodec(Constants.PREF_VIDEO_CODEC_DEFAULT);
        encodingParameters = TwilioEncodingParameters.getEncodingParameters();
        enableAutomaticSubscription = true;
    }

    public AudioCodec getAudioCodec() {
        return this.audioCodec;
    }

    public VideoCodec getVideoCodec() {
        return this.videoCodec;
    }

    public EncodingParameters getEncodingParameters() {
        return this.encodingParameters;
    }

    public boolean isAutomaticSubscriptionEnabled() {
        return this.enableAutomaticSubscription;
    }
}
