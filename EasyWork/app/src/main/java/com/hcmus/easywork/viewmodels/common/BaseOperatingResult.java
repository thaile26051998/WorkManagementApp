package com.hcmus.easywork.viewmodels.common;

public class BaseOperatingResult<T> {
    private OperatingState mState;
    private T mResult;
    private String mErrorMsg;

    public BaseOperatingResult() {
        this.mState = OperatingState.INIT;
        this.mResult = null;
        this.mErrorMsg = null;
    }

    public BaseOperatingResult(OperatingState state, T result, String errorMsg) {
        this.mState = state;
        this.mResult = result;
        this.mErrorMsg = errorMsg;
    }

    public OperatingState getState() {
        return this.mState;
    }

    public T getResult() {
        return this.mResult;
    }

    public String getErrorMessage() {
        return this.mErrorMsg;
    }
}
