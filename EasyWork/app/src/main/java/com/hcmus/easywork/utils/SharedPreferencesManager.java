package com.hcmus.easywork.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hcmus.easywork.models.Authentication;

public class SharedPreferencesManager {
    private static SharedPreferencesManager mSharedPreferencesManager;
    private static SharedPreferences mSharedPreferences;
    private static final String SHARED_PREFERENCES_TOKEN = "TOKEN_PREF";
    private static final String EMAIL = "EMAIL_PREF";
    private static final String PASSWORD = "PASSWORD_PREF";

    private SharedPreferences.Editor editor;

    private SharedPreferencesManager() {
        editor = mSharedPreferences.edit();
        editor.apply();
    }

    public static void initialize(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_TOKEN, Context.MODE_PRIVATE);
        mSharedPreferencesManager = new SharedPreferencesManager();
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_TOKEN, Context.MODE_PRIVATE);
        if (mSharedPreferencesManager == null)
            mSharedPreferencesManager = new SharedPreferencesManager();
        return mSharedPreferencesManager;
    }

    public void setAccessTokenValue(String accessToken) {
        editor.putString(SHARED_PREFERENCES_TOKEN, accessToken);
        editor.commit();
    }

    public void setAuthenticationValue(String email, String password) {
        editor.putString(EMAIL, email);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public String getAccessTokenValue() {
        return mSharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null);
    }

    public Authentication getAuthenticationValue() {
        return new Authentication(mSharedPreferences.getString(EMAIL, null), mSharedPreferences.getString(PASSWORD, null));
    }

    public static String getAccessToken() {
        return mSharedPreferences.getString(SHARED_PREFERENCES_TOKEN, null);
    }

    public static SharedPreferencesManager getInstance() {
        return mSharedPreferencesManager;
    }

    public void clear() {
        editor.clear().apply();
    }
}
