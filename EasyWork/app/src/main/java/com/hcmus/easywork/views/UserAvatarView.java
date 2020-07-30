package com.hcmus.easywork.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.hcmus.easywork.models.Avatar;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.microsoft.officeuifabric.persona.AvatarView;

public class UserAvatarView extends AvatarView {
    public UserAvatarView(@NonNull Context context) {
        super(context);
    }

    public UserAvatarView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAvatarView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAvatar(Avatar avatar) {
        byte[] decodedBuffer = avatar.getData();
        Bitmap decodedBitmap = ImageLoadingLibrary.convertToBitMap(decodedBuffer);
        setAvatarUsingGlide(decodedBitmap);
    }

    public void setAvatarUsingGlide(Bitmap bitmap) {
        ImageLoadingLibrary.useContext(getContext())
                .load(bitmap).into(this);
    }

    @BindingAdapter("app:avatar")
    public static void setAvatar(UserAvatarView view, Avatar avatar) {
        if (avatar != null) {
            view.setAvatar(avatar);
        }
    }

    @BindingAdapter("app:avatar")
    public static void setAvatar(UserAvatarView view, Bitmap avatar) {
        if (avatar != null) {
            view.setAvatarUsingGlide(avatar);
        }
    }
}
