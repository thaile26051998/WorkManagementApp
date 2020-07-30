package com.hcmus.easywork.ui.chat.voice;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.data.util.UserDataLookup;
import com.hcmus.easywork.databinding.FragmentSingleVoiceCallBinding;
import com.hcmus.easywork.twilio.shared.TwilioAudioDeviceSelector;
import com.hcmus.easywork.twilio.shared.TwilioUIUtil;
import com.hcmus.easywork.twilio.voice.TwilioVoiceCallViewModel;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;
import com.hcmus.easywork.ui.permission.FragmentMicrophonePermission;
import com.hcmus.easywork.utils.ImageLoadingLibrary;

public class FragmentSingleVoiceCall extends BaseFragment<FragmentSingleVoiceCallBinding> {
    private static final int onScreenFlags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

    private TwilioVoiceCallViewModel twilioVoiceCallViewModel;
    private TwilioAudioDeviceSelector twilioAudioDeviceSelector;
    private int savedVolumeControlStream;
    private MenuItem audioDeviceMenuItem;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_single_voice_call;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity.getWindow().addFlags(onScreenFlags);
    }

    @Override
    public void onDetach() {
        activity.getWindow().clearFlags(onScreenFlags);
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twilioVoiceCallViewModel = createViewModel(TwilioVoiceCallViewModel.class);
        twilioAudioDeviceSelector = new TwilioAudioDeviceSelector(activity);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                createExitPopup();
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(v -> createExitPopup());
        audioDeviceMenuItem = binding.toolbar.getMenu().findItem(R.id.menu_audio_device);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_audio_device) {
                twilioAudioDeviceSelector.showDevices();
                return true;
            }
            return false;
        });
        binding.buttons.btnEndCall.setOnClickListener(v -> endCall());
        binding.buttons.btnMute.setOnClickListener(v -> toggleAudio());
        binding.buttons.btnSpeaker.setOnClickListener(v -> twilioAudioDeviceSelector.showDevices());

        savedVolumeControlStream = activity.getVolumeControlStream();
        activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        twilioAudioDeviceSelector.start((selectedAudioDevice, iconId)
                -> audioDeviceMenuItem.setIcon(iconId));
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {
        twilioVoiceCallViewModel.getCallerId().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                UserDataLookup.find(integer).setOnRecordFoundListener(userRecord -> {
                    binding.toolbar.setTitle(userRecord.getName());
                    ImageLoadingLibrary.useContext(activity)
                            .load(userRecord.getAvatar())
                            .into(binding.avatar);
                });
            }
        });

        twilioVoiceCallViewModel.getCallState().observe(getViewLifecycleOwner(), voiceCallState -> {
            log("Voice call state in SingleVoiceCall: " + voiceCallState);
            switch (voiceCallState) {
                case INIT: {
                    break;
                }
                case WAITING: {
                    binding.chronometer.setText(R.string.twilio_voice_state_connecting);
                    break;
                }
                case CONNECT_FAILED: {
                    makePopup(R.string.twilio_voice_state_connect_failed);
                    binding.chronometer.setText(R.string.twilio_voice_state_ended);
                    twilioAudioDeviceSelector.deactivate();
                    getNavController().popBackStack();
                    twilioVoiceCallViewModel.onInit();
                    break;
                }
                case CONNECTED: {
                    twilioAudioDeviceSelector.activate();
                    binding.chronometer.setBase(SystemClock.elapsedRealtime());
                    binding.chronometer.start();
                    break;
                }
                case RECONNECTING: {
                    binding.chronometer.setText(R.string.twilio_voice_state_reconnecting);
                    break;
                }
                case RECONNECTED: {
                    binding.chronometer.setText(R.string.twilio_voice_state_reconnected);
                    break;
                }
                case DISCONNECTED: {
                    binding.chronometer.stop();
                    binding.chronometer.setText(R.string.twilio_voice_state_disconnected);
                    twilioAudioDeviceSelector.deactivate();
                    getNavController().popBackStack();
                    twilioVoiceCallViewModel.onInit();
                    break;
                }
                case CONNECT_QUALITY_CHANGED: {
                    makeSnack(R.string.twilio_voice_state_quality_changed);
                    break;
                }
            }
        });

        twilioVoiceCallViewModel.getAudioState().observe(getViewLifecycleOwner(), enabled ->
                TwilioUIUtil.setAudioIcon(binding.buttons.btnMute, enabled == null ? false : enabled));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!FragmentMicrophonePermission.hasPermissions(activity)) {
            getNavController().navigate(R.id.action_require_microphone_permission);
        }
    }

    @Override
    public void onDestroy() {
        twilioAudioDeviceSelector.stop();
        activity.setVolumeControlStream(savedVolumeControlStream);
        super.onDestroy();
    }

    private void createExitPopup() {
        createOkCancelDialog(R.string.twilio_confirm_exit_call,
                (dialog, which) -> endCall(),
                null).show();
    }

    private void endCall() {
        twilioVoiceCallViewModel.onDisconnected();
    }

    private void toggleAudio() {
        twilioVoiceCallViewModel.toggleAudio();
    }

    private void log(String logMessage) {
        Log.println(Log.INFO, "EW_SingleVoiceCall", logMessage);
    }
}
