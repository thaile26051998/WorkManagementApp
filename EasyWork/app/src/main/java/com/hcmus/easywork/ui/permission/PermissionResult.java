package com.hcmus.easywork.ui.permission;

public interface PermissionResult {
    String[] getRequiredPermissions();

    void onPermissionGranted();

    void onPermissionDenied();
}
