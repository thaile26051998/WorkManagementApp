package com.hcmus.easywork.twilio.video;

import android.content.Context;

import androidx.annotation.Nullable;

import com.hcmus.easywork.twilio.shared.Constants;
import com.hcmus.easywork.twilio.video.TwilioCameraCapturerCompat;
import com.twilio.video.CameraCapturer;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.VideoView;

public class TwilioVideoConnection {
    private Context context;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private TwilioCameraCapturerCompat cameraCapturerCompat;
    private LocalParticipant localParticipant;

    private TwilioVideoConnection() {

    }

    public TwilioVideoConnection(Context context) {
        this();
        this.context = context;
    }

    public void onResume() {
        this.localAudioTrack = LocalAudioTrack.create(context, true, Constants.LOCAL_AUDIO_TRACK_NAME);
        this.cameraCapturerCompat = new TwilioCameraCapturerCompat(context, getAvailableCameraSource());
        this.localVideoTrack = LocalVideoTrack.create(context, true,
                cameraCapturerCompat.getVideoCapturer(), Constants.LOCAL_VIDEO_TRACK_NAME);

        if (localParticipant != null && localVideoTrack != null) {
            localParticipant.publishTrack(localVideoTrack);
        }
    }

    public void onPause() {
        if (localVideoTrack != null) {
            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }
            localVideoTrack.release();
            localVideoTrack = null;
        }
    }

    public void onDestroy() {
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }
        context = null;
    }

    public boolean toggleAudio() {
        boolean isEnabled = false;
        if (localAudioTrack != null) {
            isEnabled = !localAudioTrack.isEnabled();
            localAudioTrack.enable(isEnabled);
        }
        return isEnabled;
    }

    public boolean toggleCamera() {
        boolean isEnabled = false;
        if (localVideoTrack != null) {
            isEnabled = !localVideoTrack.isEnabled();
            localVideoTrack.enable(isEnabled);
        }
        return isEnabled;
    }

    public void switchCamera(@Nullable OnSwitchCameraListener listener) {
        if (cameraCapturerCompat != null) {
            CameraCapturer.CameraSource cameraSource = cameraCapturerCompat.getCameraSource();
            cameraCapturerCompat.switchCamera();
            boolean isBackCamera = (cameraSource == CameraCapturer.CameraSource.BACK_CAMERA);
            if (listener != null) {
                listener.onSwitch(isBackCamera);
            }
        } else {
            if (listener != null) {
                listener.onFailed();
            }
        }
    }

    public void addThumbnailViewRender(VideoView view) {
        if (localVideoTrack != null) {
            this.localVideoTrack.addRenderer(view);
        }
    }

    public void addLocalParticipant(LocalParticipant participant) {
        this.localParticipant = participant;
    }

    public void removeLocalParticipant() {
        this.localParticipant = null;
    }

    private CameraCapturer.CameraSource getAvailableCameraSource() {
        return (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA)) ?
                (CameraCapturer.CameraSource.FRONT_CAMERA) : (CameraCapturer.CameraSource.BACK_CAMERA);
    }

    public LocalAudioTrack getLocalAudioTrack() {
        return this.localAudioTrack;
    }

    public LocalVideoTrack getLocalVideoTrack() {
        return this.localVideoTrack;
    }

    public interface OnSwitchCameraListener {
        void onSwitch(boolean isBackCamera);

        void onFailed();
    }
}
