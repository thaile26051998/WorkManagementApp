package com.hcmus.easywork.ui.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.hcmus.easywork.R;

public class FragmentMicrophonePermission extends FragmentPermission {
    private static String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

    @Override
    public String[] getRequiredPermissions() {
        return PERMISSIONS;
    }

    @Override
    public void onPermissionGranted() {
        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        getNavController().popBackStack();
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        getNavController().navigate(R.id.action_refuse_microphone_permission);
    }

    public static boolean hasPermissions(Context context) {
        if (context != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
