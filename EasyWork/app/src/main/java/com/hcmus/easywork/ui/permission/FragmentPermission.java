package com.hcmus.easywork.ui.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public abstract class FragmentPermission extends Fragment implements PermissionResult {
    public static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermissions()) {
            getNavController().popBackStack();
        } else {
            requestPermissions(getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean isGranted = true;
            for (int grantResult : grantResults) {
                isGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isGranted) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        }
    }

    public NavController getNavController() {
        return NavHostFragment.findNavController(this);
    }

    public boolean checkPermissions() {
        for (String permission : getRequiredPermissions()) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
