package com.hcmus.easywork.twilio.shared;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.hcmus.easywork.R;

public class TwilioUIUtil {
    private static Drawable getDrawable(View view, @DrawableRes int id) {
        return ContextCompat.getDrawable(view.getContext(), id);
    }

    public static void setAudioIcon(ImageView view, Boolean isEnabled) {
        if (isEnabled == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setImageDrawable(getDrawable(view, isEnabled
                    ? R.drawable.ic_call_mute
                    : R.drawable.ic_call_unmute));
        }
    }

    public static void setVideoIcon(ImageView view, Boolean isEnabled) {
        if (isEnabled == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setImageDrawable(getDrawable(view, isEnabled
                    ? R.drawable.ic_call_turn_off_camera
                    : R.drawable.ic_call_turn_on_camera));
        }
    }

    public static void setCameraIcon(ImageView view, boolean isEnabled) {
        view.setImageDrawable(getDrawable(view, isEnabled
                ? R.drawable.ic_call_turn_off_camera
                : R.drawable.ic_call_turn_on_camera));
    }
}
