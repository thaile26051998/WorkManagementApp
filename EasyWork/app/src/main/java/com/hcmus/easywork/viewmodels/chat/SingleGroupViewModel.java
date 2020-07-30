package com.hcmus.easywork.viewmodels.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.GroupRepository;
import com.hcmus.easywork.data.repository.MessageRepository;
import com.hcmus.easywork.data.response.LikeResponse;
import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.chat.group.GroupMember;
import com.hcmus.easywork.models.chat.group.GroupMemberAPI;
import com.hcmus.easywork.models.file.EwFile;
import com.hcmus.easywork.viewmodels.common.BaseLoadingResult;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.BaseUserViewModel;
import com.hcmus.easywork.viewmodels.common.LoadingState;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * <p>
 * This ViewModel contains <b>a single instance</b> of currently opened conversation (group chat or single chat)
 * and <b>CRUD operations</b> to send/load/remove messages.
 * </p>
 */
public class SingleGroupViewModel extends BaseUserViewModel {
    private MutableLiveData<SendingResult> mSendingResult;
    private MutableLiveData<LoadingResult> mLoadingResult;
    private MutableLiveData<DeletingMessageResult> mDeletingMessageResult;
    private MutableLiveData<DeletingMemberResult> mDeletingMemberResult;
    private MutableLiveData<DeletingGroupResult> mDeletingGroupResult;
    private MutableLiveData<LoadingMembersResult> mLoadingMembersResult;
    private MutableLiveData<AddingMemberResult> mAddingMemberResult;
    private MutableLiveData<LoadingFilesResult> mLoadingFilesResult;
    private MutableLiveData<LoadingFileResult> mLoadingFileResult;
    private MutableLiveData<SendingFileResult> mSendingFileResult;
    private MutableLiveData<PinningMessageResult> mPinningMessageResult;
    private MutableLiveData<LoadingPinnedMessageResult> mLoadingPinnedMessageResult;
    private MutableLiveData<LikeMessageResult> mLikeMessageResult;
    private MutableLiveData<UnlikeMessageResult> mUnlikeMessageResult;


    // region Variables
    private MutableLiveData<Group> mGroup = new MutableLiveData<>();
    private MessageRepository messageRepository = new MessageRepository();
    private GroupRepository groupRepository = new GroupRepository();
    // endregion

    public SingleGroupViewModel() {
        mLoadingResult = new MutableLiveData<>(new LoadingResult(LoadingState.INIT, null, null));
        mSendingResult = new MutableLiveData<>(new SendingResult());
        mDeletingMessageResult = new MutableLiveData<>(new DeletingMessageResult());
        mDeletingMemberResult = new MutableLiveData<>(new DeletingMemberResult());
        mDeletingGroupResult = new MutableLiveData<>(new DeletingGroupResult());
        mLoadingMembersResult = new MutableLiveData<>(new LoadingMembersResult());
        mAddingMemberResult = new MutableLiveData<>(new AddingMemberResult());
        mLoadingFilesResult = new MutableLiveData<>(new LoadingFilesResult(LoadingState.INIT, null, null));
        mLoadingFileResult = new MutableLiveData<>(new LoadingFileResult());
        mSendingFileResult = new MutableLiveData<>(new SendingFileResult());
        mPinningMessageResult = new MutableLiveData<>(new PinningMessageResult());
        mLoadingPinnedMessageResult = new MutableLiveData<>(new LoadingPinnedMessageResult());
        mLikeMessageResult = new MutableLiveData<>(new LikeMessageResult());
        mUnlikeMessageResult = new MutableLiveData<>(new UnlikeMessageResult());
    }

    // region LiveData result
    public LiveData<SendingResult> getSendingResult() {
        return this.mSendingResult;
    }

    public LiveData<LoadingResult> getLoadingResult() {
        return this.mLoadingResult;
    }

    public LiveData<DeletingMessageResult> getDeletingMessageResult() {
        return this.mDeletingMessageResult;
    }

    public LiveData<DeletingMemberResult> getDeletingMemberResult() {
        return this.mDeletingMemberResult;
    }

    public LiveData<DeletingGroupResult> getDeletingGroupResult() {
        return this.mDeletingGroupResult;
    }

    public LiveData<LoadingMembersResult> getLoadingMembersResult() {
        return this.mLoadingMembersResult;
    }

    public LiveData<AddingMemberResult> getAddingMemberResult() {
        return this.mAddingMemberResult;
    }

    public LiveData<LoadingFilesResult> getLoadingFilesResult() {
        return this.mLoadingFilesResult;
    }

    public LiveData<LoadingFileResult> getLoadingFileResult() {
        return this.mLoadingFileResult;
    }

    public LiveData<SendingFileResult> getSendingFileResult() {
        return this.mSendingFileResult;
    }

    public LiveData<PinningMessageResult> getPinningMessageResult() {
        return this.mPinningMessageResult;
    }

    public LiveData<LoadingPinnedMessageResult> getLoadingPinnedMessageResult() {
        return this.mLoadingPinnedMessageResult;
    }

    public LiveData<LikeMessageResult> getLikeMessageResult() {
        return this.mLikeMessageResult;
    }

    public LiveData<UnlikeMessageResult> getUnlikeMessageResult() {
        return this.mUnlikeMessageResult;
    }
    // endregion

    public void fetchMessage() {
        groupRepository.getMessages(getGroupId()).enqueue(new ResponseManager.OnResponseListener<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingResult.setValue(new LoadingResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void send(Message message) {
        message.setUserId(getUserId());
        message.setGroupId(getGroupId());
        message.setPinned(false);

        messageRepository.sendMessage(message).enqueue(new ResponseManager.OnResponseListener<Message>() {
            @Override
            public void onResponse(Message response) {
                mSendingResult.setValue(new SendingResult(OperatingState.DONE, response, null));
            }

            @Override
            public void onFailure(String message) {
                mSendingResult.setValue(new SendingResult(OperatingState.FAILED, null, message));
            }
        });
    }

    public void pinMessage(Message message) {
        groupRepository.pinMessage(message.getGroupId(), message.getId(), !message.isPinned())
                .enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
                    @Override
                    public void onResponse(MessageResponse response) {
                        mPinningMessageResult.setValue(new PinningMessageResult(OperatingState.DONE, response, null));
                        mPinningMessageResult.postValue(new PinningMessageResult());
                    }

                    @Override
                    public void onFailure(String message) {
                        mPinningMessageResult.setValue(new PinningMessageResult(OperatingState.FAILED, null, message));
                        mPinningMessageResult.postValue(new PinningMessageResult());
                    }
                });
    }

    public void getPinnedMessage() {
        groupRepository.getPinnedMessage(getGroupId()).enqueue(new ResponseManager.OnResponseListener<Message>() {
            @Override
            public void onResponse(Message response) {
                mLoadingPinnedMessageResult.setValue(new LoadingPinnedMessageResult(OperatingState.DONE, response, null));
                mLoadingPinnedMessageResult.postValue(new LoadingPinnedMessageResult());
            }

            @Override
            public void onFailure(String message) {
                if (message.contains("End of input")) {
                    mLoadingPinnedMessageResult.setValue(new LoadingPinnedMessageResult(OperatingState.DONE, null, null));
                } else {
                    mLoadingPinnedMessageResult.setValue(new LoadingPinnedMessageResult(OperatingState.FAILED, null, message));
                }
                mLoadingPinnedMessageResult.postValue(new LoadingPinnedMessageResult());
            }
        });
    }

    public void sendFile(File file, String mimeType) {
        Message message = new Message();
        message.setContent("");
        message.setUserId(getUserId());
        message.setGroupId(getGroupId());
        message.setPinned(false);
        messageRepository.sendMessage(message).enqueue(new ResponseManager.OnResponseListener<Message>() {
            @Override
            public void onResponse(Message response) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);

                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("files", file.getPath(), requestBody);
                messageRepository.sendFile(response.getId(), multipartBody).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
                    @Override
                    public void onResponse(MessageResponse response1) {
                        mSendingFileResult.setValue(new SendingFileResult(OperatingState.DONE, response1, null));
                        mSendingFileResult.postValue(new SendingFileResult());
                    }

                    @Override
                    public void onFailure(String message) {
                        // Failed to post file, delete empty message
                        messageRepository.deleteMessage(response.getId());
                        mSendingFileResult.setValue(new SendingFileResult(OperatingState.FAILED, null, message));
                        mSendingFileResult.postValue(new SendingFileResult());
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                mSendingFileResult.setValue(new SendingFileResult(OperatingState.FAILED, null, message));
                mSendingFileResult.postValue(new SendingFileResult());
            }
        });
    }

    public void likeMessage(int messId) {
        messageRepository.likeMessage(getUserId(), messId).enqueue(new ResponseManager.OnResponseListener<LikeResponse>() {
            @Override
            public void onResponse(LikeResponse response) {
                mLikeMessageResult.setValue(new LikeMessageResult(OperatingState.DONE, response, null));
                mLikeMessageResult.postValue(new LikeMessageResult());
            }

            @Override
            public void onFailure(String message) {
                mLikeMessageResult.setValue(new LikeMessageResult(OperatingState.FAILED, null, message));
                mLikeMessageResult.postValue(new LikeMessageResult());
            }
        });
    }

    public void unlikeMessage(int messId) {
        messageRepository.unlikeMessage(getUserId(), messId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                mUnlikeMessageResult.setValue(new UnlikeMessageResult(OperatingState.DONE, response, null));
                mUnlikeMessageResult.postValue(new UnlikeMessageResult());
            }

            @Override
            public void onFailure(String message) {
                mUnlikeMessageResult.setValue(new UnlikeMessageResult(OperatingState.FAILED, null, message));
                mUnlikeMessageResult.postValue(new UnlikeMessageResult());
            }
        });
    }

    public void deleteMessage(int messageId) {
        messageRepository.deleteMessage(messageId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingMessageResult.setValue(new DeletingMessageResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingMessageResult.setValue(new DeletingMessageResult(OperatingState.FAILED, null, result));
                }
                mDeletingMessageResult.postValue(new DeletingMessageResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingMessageResult.setValue(new DeletingMessageResult(OperatingState.FAILED, null, message));
                mDeletingMessageResult.postValue(new DeletingMessageResult());
            }
        });
    }

    public void refresh() {
        groupRepository.getGroup(getGroupId()).enqueue(new ResponseManager.OnResponseListener<Group>() {
            @Override
            public void onResponse(Group response) {
                mGroup.setValue(response);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void getAllMembers() {
        groupRepository.getAllMembers(getGroupId()).enqueue(new ResponseManager.OnResponseListener<List<GroupMember>>() {
            @Override
            public void onResponse(List<GroupMember> response) {
                mLoadingMembersResult.setValue(new LoadingMembersResult(OperatingState.DONE, response, null));
                mLoadingMembersResult.postValue(new LoadingMembersResult());
            }

            @Override
            public void onFailure(String message) {
                mLoadingMembersResult.setValue(new LoadingMembersResult(OperatingState.FAILED, null, message));
                mLoadingMembersResult.postValue(new LoadingMembersResult());
            }
        });
    }

    public void addMember(int userId) {
        this.groupRepository.addMember(getGroupId(), userId, false, mGroup.getValue() == null ? 0 : mGroup.getValue().getProjectId())
                .enqueue(new ResponseManager.OnResponseListener<GroupMemberAPI>() {
                    @Override
                    public void onResponse(GroupMemberAPI response) {
                        mAddingMemberResult.setValue(new AddingMemberResult(OperatingState.DONE, response, null));
                    }

                    @Override
                    public void onFailure(String message) {
                        mAddingMemberResult.setValue(new AddingMemberResult(OperatingState.FAILED, null, message));
                    }
                });
    }

    public void getAllFiles() {
        mLoadingFilesResult.setValue(new LoadingFilesResult(LoadingState.LOADING, null, null));
        this.groupRepository.getAllFiles(getGroupId()).enqueue(new ResponseManager.OnResponseListener<List<EwFile>>() {
            @Override
            public void onResponse(List<EwFile> response) {
                mLoadingFilesResult.setValue(new LoadingFilesResult(LoadingState.LOADED, response, null));
            }

            @Override
            public void onFailure(String message) {
                mLoadingFilesResult.setValue(new LoadingFilesResult(LoadingState.FAILED, null, message));
            }
        });
    }

    public void getFile(int messageId) {
        messageRepository.getFile(messageId).enqueue(new ResponseManager.OnResponseListener<List<EwFile>>() {
            @Override
            public void onResponse(List<EwFile> response) {
                if (!response.isEmpty()) {
                    mLoadingFileResult.setValue(new LoadingFileResult(OperatingState.DONE, response.get(0), null));
                    mLoadingFileResult.postValue(new LoadingFileResult());
                }
            }

            @Override
            public void onFailure(String message) {
                mLoadingFileResult.setValue(new LoadingFileResult(OperatingState.FAILED, null, message));
                mLoadingFileResult.postValue(new LoadingFileResult());
            }
        });
    }

    public void leaveGroup() {
        deleteMember(getUserId());
    }

    public void deleteMember(int userId) {
        groupRepository.deleteMember(getGroupId(), userId).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingMemberResult.setValue(new DeletingMemberResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingMemberResult.setValue(new DeletingMemberResult(OperatingState.FAILED, null, result));
                }
                mDeletingMemberResult.postValue(new DeletingMemberResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingMemberResult.setValue(new DeletingMemberResult(OperatingState.FAILED, null, message));
                mDeletingMemberResult.postValue(new DeletingMemberResult());
            }
        });
    }

    public void archiveGroup() {
        groupRepository.deleteGroup(getGroupId()).enqueue(new ResponseManager.OnResponseListener<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                String result = response.getMessage();
                if (result.contains("successfully")) {
                    mDeletingGroupResult.setValue(new DeletingGroupResult(OperatingState.DONE, response, null));
                } else {
                    mDeletingGroupResult.setValue(new DeletingGroupResult(OperatingState.FAILED, null, result));
                }
                mDeletingGroupResult.postValue(new DeletingGroupResult());
            }

            @Override
            public void onFailure(String message) {
                mDeletingGroupResult.setValue(new DeletingGroupResult(OperatingState.FAILED, null, message));
                mDeletingGroupResult.postValue(new DeletingGroupResult());
            }
        });
    }

    // region undefined
    public void setGroup(Group group) {
        this.mGroup.setValue(group);
    }

    public LiveData<Group> get() {
        return this.mGroup;
    }

    private int getGroupId() {
        return this.mGroup.getValue() != null ?
                this.mGroup.getValue().getGroupId() : 0;
    }

    public void setGroupId(int groupId) {
        groupRepository.getGroup(groupId).enqueue(new ResponseManager.OnResponseListener<Group>() {
            @Override
            public void onResponse(Group response) {
                mGroup.setValue(response);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
    // endregion

    public static class SendingResult extends BaseOperatingResult<Message> {
        public SendingResult() {
            super();
        }

        public SendingResult(OperatingState state, Message result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingResult extends BaseLoadingResult<List<Message>> {
        public LoadingResult(LoadingState state, List<Message> result, String errorMsg) {
            super(state, result, errorMsg);
        }

    }

    public static class DeletingMessageResult extends BaseOperatingResult<MessageResponse> {
        public DeletingMessageResult() {
            super();
        }

        public DeletingMessageResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class DeletingMemberResult extends BaseOperatingResult<MessageResponse> {
        public DeletingMemberResult() {
            super();
        }

        public DeletingMemberResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class DeletingGroupResult extends BaseOperatingResult<MessageResponse> {
        public DeletingGroupResult() {
            super();
        }

        public DeletingGroupResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingMembersResult extends BaseOperatingResult<List<GroupMember>> {
        public LoadingMembersResult() {
            super();
        }

        public LoadingMembersResult(OperatingState state, List<GroupMember> result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class AddingMemberResult extends BaseOperatingResult<GroupMemberAPI> {
        public AddingMemberResult() {
            super();
        }

        public AddingMemberResult(OperatingState state, GroupMemberAPI result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingFilesResult extends BaseLoadingResult<List<EwFile>> {

        public LoadingFilesResult(LoadingState state, List<EwFile> result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingFileResult extends BaseOperatingResult<EwFile> {
        public LoadingFileResult() {
            super();
        }

        public LoadingFileResult(OperatingState state, EwFile result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class SendingFileResult extends BaseOperatingResult<MessageResponse> {
        public SendingFileResult() {
            super();
        }

        public SendingFileResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class PinningMessageResult extends BaseOperatingResult<MessageResponse> {
        public PinningMessageResult() {
            super();
        }

        public PinningMessageResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LoadingPinnedMessageResult extends BaseOperatingResult<Message> {
        public LoadingPinnedMessageResult() {
            super();
        }

        public LoadingPinnedMessageResult(OperatingState state, Message result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class LikeMessageResult extends BaseOperatingResult<LikeResponse> {
        public LikeMessageResult() {
            super();
        }

        public LikeMessageResult(OperatingState state, LikeResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }

    public static class UnlikeMessageResult extends BaseOperatingResult<MessageResponse> {
        public UnlikeMessageResult() {
            super();
        }

        public UnlikeMessageResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
