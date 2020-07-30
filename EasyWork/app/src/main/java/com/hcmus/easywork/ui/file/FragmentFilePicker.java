package com.hcmus.easywork.ui.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.hcmus.easywork.ui.file.MediaPicker.PERMISSIONS;
import static com.hcmus.easywork.ui.file.MediaPicker.REQUEST_CODE_PERMISSIONS;
import static com.hcmus.easywork.ui.file.MediaPicker.REQUEST_CODE_PICK_FILE;
import static com.hcmus.easywork.ui.file.MediaPicker.REQUEST_CODE_PICK_IMAGE;

public class FragmentFilePicker extends BottomSheetDialogFragment {
    private MediaPicker.OnImagePickedListener onImagePickedListener;
    private MediaPicker.OnFilePickedListener onFilePickedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasPermissions(requireContext())) sendRequest();
        else requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }

    private void sendRequest() {
        if (onImagePickedListener != null) {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
        }
        if (onFilePickedListener != null) {
            Intent pickFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pickFileIntent.setType("*/*");
            startActivityForResult(pickFileIntent, REQUEST_CODE_PICK_FILE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendRequest(); // Permission granted
            } else {
                dismiss(); // Permission denied
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_FILE: {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (onFilePickedListener != null && uri != null) {
                            onFilePickedListener.onFilePicked(uri);
                        }
                    }
                    break;
                }
                case REQUEST_CODE_PICK_IMAGE: {
                    if (data != null) {
                        Uri uri = data.getData();
                        if (onImagePickedListener != null && uri != null) {
                            onImagePickedListener.onImagePicked(uri);
                        }
                    }
                    break;
                }
            }
        }
        dismiss();
    }

    private static boolean hasPermissions(Context context) {
        if (context != null)
            for (String permission : PERMISSIONS)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
        return true;
    }

    void setOnFilePickedListener(MediaPicker.OnFilePickedListener onFilePickedListener) {
        this.onFilePickedListener = onFilePickedListener;
    }

    void setOnImagePickedListener(MediaPicker.OnImagePickedListener onImagePickedListener) {
        this.onImagePickedListener = onImagePickedListener;
    }
}