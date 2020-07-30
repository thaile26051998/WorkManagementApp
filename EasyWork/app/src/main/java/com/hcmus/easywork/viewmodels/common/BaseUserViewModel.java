package com.hcmus.easywork.viewmodels.common;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class  BaseUserViewModel extends ViewModel {
    protected MutableLiveData<Integer> mUserId = new MutableLiveData<>();

    public BaseUserViewModel() {

    }

    public void setUserId(int userId) {
        this.mUserId.setValue(userId);
    }

    public int getUserId() {
        return mUserId.getValue() != null ? mUserId.getValue() : 0;
    }
}
