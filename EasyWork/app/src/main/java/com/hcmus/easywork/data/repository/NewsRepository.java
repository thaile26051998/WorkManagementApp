package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.NewsApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.models.News;

import java.util.List;

public class NewsRepository extends AuthenticatedRepository<NewsApiService> {
    public NewsRepository() {

    }

    public ResponseManager<List<News>> getNews(int userId) {
        return new ResponseManager<>(getApi().getNews(userId));
    }

    public ResponseManager<MessageResponse> markAsReadNews(int newsId) {
        return new ResponseManager<>(getApi().markAsRead(newsId));
    }

    public ResponseManager<MessageResponse> markAllAsReadNews(int userId) {
        return new ResponseManager<>(getApi().markAllAsRead(userId));
    }

    public ResponseManager<MessageResponse> deleteNews(int newsId) {
        return new ResponseManager<>(getApi().deleteNews(newsId));
    }

    public ResponseManager<MessageResponse> deleteAllNews(int userId) {
        return new ResponseManager<>(getApi().deleteAllNews(userId));
    }

    public ResponseManager<News> createNews(News news) {
        return new ResponseManager<>(getApi().createNews(news));
    }

    @Override
    protected Class<NewsApiService> getApiClass() {
        return NewsApiService.class;
    }
}
