package com.hcmus.easywork.viewmodels.common;

/**
 * Wrap state, response and error message
 *
 * @param <T> Type of response
 */
public class BaseLoadingResult<T> {
    private LoadingState mLoadingState;
    private T mResult;
    private String mErrorMsg;

    private BaseLoadingResult() {

    }

    public BaseLoadingResult(LoadingState state, T result, String errorMsg) {
        this();
        this.mLoadingState = state;
        this.mResult = result;
        this.mErrorMsg = errorMsg;
    }

    public LoadingState getLoadingState() {
        return this.mLoadingState;
    }

    public T getResult() {
        return this.mResult;
    }

    public String getErrorMessage() {
        return this.mErrorMsg;
    }
}
