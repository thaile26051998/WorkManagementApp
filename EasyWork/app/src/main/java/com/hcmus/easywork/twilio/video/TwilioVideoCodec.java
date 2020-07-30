package com.hcmus.easywork.twilio.video;

import com.twilio.video.H264Codec;
import com.twilio.video.VideoCodec;
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

public class TwilioVideoCodec {
    public static VideoCodec getVideoCodec(String codecName) {
        switch (codecName) {
            case Vp8Codec.NAME:
                return new Vp8Codec(false);
            case H264Codec.NAME:
                return new H264Codec();
            case Vp9Codec.NAME:
                return new Vp9Codec();
            default:
                return new Vp8Codec();
        }
    }
}
