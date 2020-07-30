package com.hcmus.easywork.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcmus.easywork.models.file.EwFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppFileWriter {
    private static AppFileWriter instance;
    private String fileName;
    private byte[] data;

    private AppFileWriter() {

    }

    public void setContent(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public void setContent(EwFile file) {
        setContent(file.mName, file.mData.data);
    }

    public void write(@NonNull OnResultListener listener) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
                listener.onWritten(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
                listener.onFailed(e.getMessage());
            }
        } else {
            listener.onFailed("File name duplicated");
        }
    }

    public static synchronized AppFileWriter getInstance() {
        if (instance == null)
            instance = new AppFileWriter();
        return instance;
    }

    @Nullable
    public static File getImageFileFromUri(Context context, Uri uri) {
        File file = null;
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String file_path = cursor.getString(column_index);
                file = new File(file_path);
            }
            cursor.close();
        }
        return file;
    }

    public interface OnResultListener {
        void onWritten(String fileName);

        void onFailed(String errorMessage);
    }
}
