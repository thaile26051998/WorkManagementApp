package com.hcmus.easywork.viewmodels.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.NewsRepository;
import com.hcmus.easywork.models.News;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.util.List;

public class NewsViewModel extends BaseUserViewModel {
    private MutableLiveData<LoadingNewsResult> mLoadingNewsResult;
    private MutableLiveData<MarkAsReadNewsResult> mMarkAsReadNewsResult;
    private MutableLiveData<DeletingNewsResult> mDeletingNewsResult;
    private MutableLiveData<MarkAllAsReadNewsResult> mMarkAllAsReadNewsResult;
    private MutableLiveData<DeletingAllNewsResult> mDeletingAllNewsResult;
    private MutableLiveData<CreatingNewsResult> mCreatingNewsResult;
    private NewsRepository newsRepository;


    public NewsViewModel() {
        newsRepository = new NewsRepository();
        mLoadingNewsResult = new MutableLiveData<>(LoadingNewsResult.INITIALIZED);
        mMarkAsReadNewsResult = new MutableLiveData<>(new MarkAsReadNewsResult());
        mMarkAllAsReadNewsResult = new MutableLiveData<>(new MarkAllAsReadNewsResult());
        mDeletingNewsResult = new MutableLiveData<>(new DeletingNewsResult());
        mDeletingAllNewsResult = new MutableLiveData<>(new DeletingAllNewsResult());
        mCreatingNewsResult = new MutableLiveData<>(new CreatingNewsResult());
    }

    public void getNews() {
        this.mLoadingNewsResult.setValue(LoadingNewsResult.LOADING);
        this.newsRepository.getNews(getUserId()).enqueue(new ResponseManager.OnResponseListener<List<News>>() {
            @Override
            public void onResponse(List<News> response) {
                mLoadingNewsResult.setValue(LoadingNewsResult.createLoadedResult(response));
            }

            @Override
            public void onFailure(String message) {
                mLoadingNewsResult.setValue(LoadingNewsResult.createFailedResult(message));
            }
        });
    }

    public void markAsReadNews(int newsId) {
        newsRepository.markAsReadNews(newsId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mMarkAsReadNewsResult.setValue(new MarkAsReadNewsResult(OperatingState.DONE, response, null));
                } else {
                    mMarkAsReadNewsResult.setValue(new MarkAsReadNewsResult(OperatingState.FAILED, null, result));
                }
                mMarkAsReadNewsResult.postValue(new MarkAsReadNewsResult());
            }

            @Override
            public void onFailure(String message) {
                mMarkAsReadNewsResult.setValue(new MarkAsReadNewsResult(OperatingState.FAILED, null, message));
                mMarkAsReadNewsResult.postValue(new MarkAsReadNewsResult());
            }
        });
    }

    public void markAllAsReadNews() {
        newsRepository.markAllAsReadNews(getUserId()).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mMarkAllAsReadNewsResult.setValue(new MarkAllAsReadNewsResult(OperatingState.DONE, response, null));
                } else {
                    mMarkAllAsReadNewsResult.setValue(new MarkAllAsReadNewsResult(OperatingState.FAILED, null, result));
                }
                mMarkAllAsReadNewsResult.postValue(new MarkAllAsReadNewsResult());
            }

            @Override
            public void onFailure(String message) {
                mMarkAllAsReadNewsResult.setValue(new MarkAllAsReadNewsResult(OperatingState.FAILED, null, message));
                mMarkAllAsReadNewsResult.postValue(new MarkAllAsReadNewsResult());
            }
        });
    }

    public void deleteNews(int newsId) {
        newsRepository.deleteNews(newsId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingNewsResult.setValue(new DeletingNewsResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingNewsResult.setValue(new DeletingNewsResult(OperatingState.FAILED, null, result));
                }
                mDeletingNewsResult.postValue(new DeletingNewsResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingNewsResult.setValue(new DeletingNewsResult(OperatingState.FAILED, null, message));
                mDeletingNewsResult.postValue(new DeletingNewsResult());
            }
        });
    }

    public void deleteAllNews() {
        newsRepository.deleteAllNews(getUserId()).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingAllNewsResult.setValue(new DeletingAllNewsResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingAllNewsResult.setValue(new DeletingAllNewsResult(OperatingState.FAILED, null, result));
                }
                mDeletingAllNewsResult.postValue(new DeletingAllNewsResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingAllNewsResult.setValue(new DeletingAllNewsResult(OperatingState.FAILED, null, message));
                mDeletingAllNewsResult.postValue(new DeletingAllNewsResult());
            }
        });
    }

    public void createNews(News news) {
        newsRepository.createNews(news).enqueue(new ResponseManager.OnResponseListener<News>() {
            @Override
            public void onResponse(News response) {
                mCreatingNewsResult.setValue(new CreatingNewsResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mCreatingNewsResult.setValue(new CreatingNewsResult(OperatingState.DONE, null, message));
            }
        });
    }

    public LiveData<LoadingNewsResult> getLoadingNewsResult() {
        return this.mLoadingNewsResult;
    }

    public LiveData<MarkAsReadNewsResult> getMarkAsReadNewsResult() {
        return this.mMarkAsReadNewsResult;
    }

    public LiveData<MarkAllAsReadNewsResult> getMarkAllAsReadNewsResult() {
        return this.mMarkAllAsReadNewsResult;
    }

    public LiveData<DeletingNewsResult> getDeletingNewsResult() {
        return this.mDeletingNewsResult;
    }

    public LiveData<DeletingAllNewsResult> getDeletingAllNewsResult() {
        return this.mDeletingAllNewsResult;
    }

    public LiveData<CreatingNewsResult> getCreatingNewsResult() {
        return this.mCreatingNewsResult;
    }

    public static class LoadingNewsResult extends BaseLoadingResult<List<News>> {
        public LoadingNewsResult(LoadingState state, List<News> result, String errorMsg) {
            super(state, result, errorMsg);
        }

        public static NewsViewModel.LoadingNewsResult createLoadedResult(List<News> newsList) {
            return new LoadingNewsResult(LoadingState.LOADED, newsList, null);
        }

        public static NewsViewModel.LoadingNewsResult createFailedResult(String errorMsg) {
            return new NewsViewModel.LoadingNewsResult(LoadingState.FAILED, null, errorMsg);
        }

        public static final LoadingNewsResult INITIALIZED = new LoadingNewsResult(LoadingState.INIT, null, null);
        public static final LoadingNewsResult LOADING = new LoadingNewsResult(LoadingState.LOADING, null, null);
    }

    public static class MarkAsReadNewsResult extends BaseOperatingResult<MessageResponse> {
        public MarkAsReadNewsResult() {
            super();
        }

        public MarkAsReadNewsResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class MarkAllAsReadNewsResult extends BaseOperatingResult<MessageResponse> {
        public MarkAllAsReadNewsResult() {
            super();
        }

        public MarkAllAsReadNewsResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class DeletingNewsResult extends BaseOperatingResult<MessageResponse> {
        public DeletingNewsResult() {
            super();
        }

        public DeletingNewsResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class DeletingAllNewsResult extends BaseOperatingResult<MessageResponse> {
        public DeletingAllNewsResult() {
            super();
        }

        public DeletingAllNewsResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class CreatingNewsResult extends BaseOperatingResult<News> {
        public CreatingNewsResult() {
            super();
        }

        public CreatingNewsResult(OperatingState state, News result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
