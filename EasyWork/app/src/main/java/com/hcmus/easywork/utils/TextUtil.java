package com.hcmus.easywork.utils;

import android.content.Context;
import android.util.Patterns;

import com.hcmus.easywork.R;

public class TextUtil {
    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        //TODO: check upper cases, letters, ...
        int length = password.length();
        return length >= AuthConfig.passwordMinLength && length <= AuthConfig.passwordMaxLength;
    }

    public static String getInvalidPasswordText(Context context) {
        return context.getString(R.string.hint_password_requirement, AuthConfig.passwordMinLength, AuthConfig.passwordMaxLength);
    }
}
