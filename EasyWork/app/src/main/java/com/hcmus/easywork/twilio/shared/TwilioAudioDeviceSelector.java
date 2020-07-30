package com.hcmus.easywork.twilio.shared;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hcmus.easywork.R;
import com.twilio.audioswitch.selection.AudioDevice;
import com.twilio.audioswitch.selection.AudioDeviceSelector;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class TwilioAudioDeviceSelector {
    private AudioDeviceSelector audioDeviceSelector;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private OnAudioDeviceChangedListener onAudioDeviceChangedListener;

    private TwilioAudioDeviceSelector() {

    }

    public TwilioAudioDeviceSelector(Context context) {
        this();
        audioDeviceSelector = new AudioDeviceSelector(context);
        alertDialogBuilder = new MaterialAlertDialogBuilder(context);
    }

    public void activate() {
        audioDeviceSelector.activate();
    }

    public void deactivate() {
        audioDeviceSelector.deactivate();
    }

    public void stop() {
        audioDeviceSelector.stop();
    }

    public void start(@NonNull OnAudioDeviceChangedListener listener) {
        this.onAudioDeviceChangedListener = listener;
        this.audioDeviceSelector.start((audioDevices, audioDevice) -> {
            updateAudioDeviceIcon(audioDevice);
            return Unit.INSTANCE;
        });
    }

    private void updateAudioDeviceIcon(AudioDevice device) {
        @DrawableRes int activeDeviceIcon;
        if (device instanceof AudioDevice.BluetoothHeadset) {
            activeDeviceIcon = R.drawable.ic_bluetooth_white_24dp;
        } else if (device instanceof AudioDevice.WiredHeadset) {
            activeDeviceIcon = R.drawable.ic_headset_mic_white_24dp;
        } else if (device instanceof AudioDevice.Earpiece) {
            activeDeviceIcon = R.drawable.ic_phonelink_ring_white_24dp;
        } else if (device instanceof AudioDevice.Speakerphone) {
            activeDeviceIcon = R.drawable.ic_volume_up_white_24dp;
        } else {
            activeDeviceIcon = R.drawable.outline_device_unknown_24;
        }
        if (onAudioDeviceChangedListener != null) {
            onAudioDeviceChangedListener.onChanged(device, activeDeviceIcon);
        }
    }

    public void showDevices() {
        AudioDevice selectedDevice = audioDeviceSelector.getSelectedAudioDevice();
        List<AudioDevice> availableAudioDevices = audioDeviceSelector.getAvailableAudioDevices();
        if (selectedDevice != null) {
            int index = availableAudioDevices.indexOf(selectedDevice);
            List<String> audioDeviceNames = new ArrayList<>();
            for (AudioDevice device : availableAudioDevices) {
                audioDeviceNames.add(device.getName());
            }
            alertDialogBuilder.setTitle("Select audio device")
                    .setSingleChoiceItems(audioDeviceNames.toArray(new CharSequence[0]),
                            index,
                            (dialog, which) -> {
                                dialog.dismiss();
                                AudioDevice selectedAudioDevice = availableAudioDevices.get(which);
                                //TODO: update
                                audioDeviceSelector.selectDevice(selectedAudioDevice);
                            });
            alertDialogBuilder.create().show();

        }
    }

    public interface OnAudioDeviceChangedListener {
        void onChanged(AudioDevice selectedAudioDevice, @DrawableRes int iconId);
    }
}
