package com.hcmus.easywork.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.R;
import com.hcmus.easywork.databinding.FragmentPreferencesBinding;
import com.hcmus.easywork.ui.common.fragment.BaseFragment;

public class FragmentPreferences extends BaseFragment<FragmentPreferencesBinding> {
    @Override
    public int getLayoutId() {
        return R.layout.fragment_preferences;
    }

    @Override
    public void onBasedViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.toolbar.setNavigationOnClickListener(l -> getNavController().popBackStack());
        binding.btnSettings.setOnClickListener(v -> openSettings());
    }

    @Override
    public void onBasedActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    private void openSettings() {
        String packageName = activity.getPackageName();
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
        }
        startActivity(intent);
    }
}
