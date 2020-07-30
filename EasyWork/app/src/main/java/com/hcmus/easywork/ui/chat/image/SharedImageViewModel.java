package com.hcmus.easywork.ui.chat.image;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hcmus.easywork.models.file.EwFile;

public class SharedImageViewModel extends ViewModel {
    private MutableLiveData<EwFile> mFile;

    public SharedImageViewModel() {
        mFile = new MutableLiveData<>();
    }

    public void selectFile(EwFile file) {
        this.mFile.setValue(file);
    }

    public LiveData<EwFile> getFile() {
        return this.mFile;
    }
}
