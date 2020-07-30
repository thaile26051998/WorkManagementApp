package com.hcmus.easywork.ui.file;

import android.Manifest;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MediaPicker {
    static final int
            REQUEST_CODE_PICK_FILE = 1,
            REQUEST_CODE_PICK_IMAGE = 2,
            REQUEST_CODE_PERMISSIONS = 1;
    static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Context mContext;
    private OnImagePickedListener onImagePickedListener;
    private OnFilePickedListener onFilePickedListener;

    public MediaPicker(Context context) {
        this.mContext = context;
    }

    public void apply() {
        FragmentFilePicker filePicker = new FragmentFilePicker();
        filePicker.setOnImagePickedListener(this.onImagePickedListener);
        filePicker.setOnFilePickedListener(this.onFilePickedListener);
        filePicker.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "MediaPicker");
    }

    public void setOnImagePickedListener(OnImagePickedListener onImagePickedListener) {
        this.onImagePickedListener = onImagePickedListener;
    }

    public void setOnFilePickedListener(OnFilePickedListener onFilePickedListener) {
        this.onFilePickedListener = onFilePickedListener;
    }

    public interface OnImagePickedListener {
        void onImagePicked(@NonNull Uri uri);
    }

    public interface OnFilePickedListener {
        void onFilePicked(@NonNull Uri uri);
    }
}
