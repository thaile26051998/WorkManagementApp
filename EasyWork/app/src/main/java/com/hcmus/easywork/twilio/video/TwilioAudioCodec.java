package com.hcmus.easywork.twilio.video;

import com.twilio.video.AudioCodec;
import com.twilio.video.G722Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;

public class TwilioAudioCodec {
    public static AudioCodec getAudioCodec(String codecName) {
        switch (codecName) {
            case IsacCodec.NAME:
                return new IsacCodec();
            case PcmaCodec.NAME:
                return new PcmaCodec();
            case PcmuCodec.NAME:
                return new PcmuCodec();
            case G722Codec.NAME:
                return new G722Codec();
            default:
                // include OpusCodec.NAME
                return new OpusCodec();
        }
    }
}
