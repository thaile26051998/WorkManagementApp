package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.api.MessageApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.response.LikeResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.file.EwFile;

import java.util.List;

import okhttp3.MultipartBody;

public class MessageRepository extends AuthenticatedRepository<MessageApiService> {
    public MessageRepository() {

    }

    public ResponseManager<Message> sendMessage(Message message) {
        return new ResponseManager<>(getApi().sendMessage(message));
    }

    public ResponseManager<MessageResponse> deleteMessage(int messageId) {
        return new ResponseManager<>(getApi().deleteMessage(messageId));
    }

    public ResponseManager<MessageResponse> sendFile(int messageId, MultipartBody.Part file) {
        return new ResponseManager<>(getApi().sendFile(messageId, file));
    }

    public ResponseManager<List<EwFile>> getFile(int messageId) {
        return new ResponseManager<>(getApi().getFile(messageId));
    }

    public ResponseManager<LikeResponse> likeMessage(int userId, int messId) {
        return new ResponseManager<>(getApi().likeMessage(userId, messId, "like"));
    }

    public ResponseManager<MessageResponse> unlikeMessage(int userId, int messId) {
        return new ResponseManager<>(getApi().unlikeMessage(userId, messId));
    }

    @Override
    protected Class<MessageApiService> getApiClass() {
        return MessageApiService.class;
    }
}
