package com.hcmus.easywork.twilio.video;

import com.twilio.video.EncodingParameters;

public class TwilioEncodingParameters {
    public static EncodingParameters getEncodingParameters() {
        return new EncodingParameters(0, 0);
    }
}
