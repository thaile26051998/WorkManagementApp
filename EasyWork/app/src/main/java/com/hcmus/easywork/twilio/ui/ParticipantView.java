package com.hcmus.easywork.twilio.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hcmus.easywork.databinding.LayoutParticipantViewBinding;
import com.hcmus.easywork.twilio.shared.TwilioUIUtil;
import com.twilio.video.VideoView;

public class ParticipantView extends ConstraintLayout {
    private LayoutParticipantViewBinding binding;
    private Context mContext;

    // region Attributes
    private String mParticipantName;
    private Boolean mIsAudioEnabled, mIsVideoEnabled;
    private boolean mIsDominantSpeaking;
    // endregion

    public ParticipantView(Context context) {
        this(context, null);
    }

    public ParticipantView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParticipantView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = LayoutParticipantViewBinding.inflate(inflater, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public VideoView getVideoView() {
        return binding.videoView;
    }

    public String getParticipantName() {
        return this.mParticipantName;
    }

    public void setParticipantName(@Nullable String name) {
        this.mParticipantName = name;
        if (name == null) {
            binding.participantName.setVisibility(GONE);
        } else {
            binding.participantName.setVisibility(VISIBLE);
            binding.participantName.setText(mParticipantName);
        }
    }

    public Boolean isAudioEnabled() {
        return this.mIsAudioEnabled;
    }

    public Boolean isVideoEnabled() {
        return this.mIsVideoEnabled;
    }

    public boolean isDominantSpeaking() {
        return this.mIsDominantSpeaking;
    }

    public void setDominantSpeaking(boolean isSpeaking) {
        this.mIsDominantSpeaking = isSpeaking;
        binding.isDominantSpeaking.setVisibility(isSpeaking ? VISIBLE : GONE);
    }

    public void setAudioEnabled(Boolean isEnabled) {
        this.mIsAudioEnabled = isEnabled;
        TwilioUIUtil.setAudioIcon(binding.isAudioEnabled, isEnabled);
    }

    public void setVideoEnabled(Boolean isEnabled) {
        this.mIsVideoEnabled = isEnabled;
        TwilioUIUtil.setVideoIcon(binding.isVideoEnabled, isEnabled);
    }

    public void clear() {
        getVideoView().clearImage();
    }
}
