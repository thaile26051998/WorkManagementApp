package com.hcmus.easywork.ui.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.hcmus.easywork.R;

public class FragmentCameraPermission extends FragmentPermission {
    private static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    public static boolean hasPermissions(@NonNull Context context) {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

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
        getNavController().navigate(R.id.action_refuse_camera_permission);
    }
}
