package com.hcmus.easywork.data.util;

import androidx.annotation.NonNull;

import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.UserRepository;
import com.hcmus.easywork.utils.ImageLoadingLibrary;
import com.hcmus.easywork.viewmodels.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataLookup {
    private static List<Integer> mQueueId = new ArrayList<>();
    private static Map<Integer, UserRecord> mRecords = new HashMap<>();
    private static UserRepository userRepository = new UserRepository();

    public static RecordOperation find(int userId) {
        return new RecordOperation(userId);
    }

    public static class RecordOperation {
        private int mUserId;
        private OnRecordFoundListener mOnRecordFoundListener;

        public RecordOperation(int userId) {
            this.mUserId = userId;
        }

        public void setOnRecordFoundListener(@NonNull OnRecordFoundListener listener) {
            this.mOnRecordFoundListener = listener;
            boolean alreadyFound = mRecords.containsKey(mUserId);
            if (alreadyFound) {
                // If user found in the map, invoke value
                UserRecord record = mRecords.get(mUserId);
                mOnRecordFoundListener.onRecordFound(record);
            } else {
                // If user not found, check the queue
                // if found, wait to get from map, else send request
                boolean foundInQueue = mQueueId.contains(mUserId);
                if (foundInQueue) {
                    // wait

                } else {
                    // add to queue
                    mQueueId.add(mUserId);
                    // send request
                    userRepository.getUser(mUserId).enqueue(new ResponseManager.OnResponseListener<User>() {
                        @Override
                        public void onResponse(User response) {
                            UserRecord record = new UserRecord(response.getDisplayName(),
                                    ImageLoadingLibrary.convertToBitMap(response.getAvatar().getData()),
                                    response.getMail());
                            mRecords.put(mUserId, record);
                            mOnRecordFoundListener.onRecordFound(record);
                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                }
            }
        }
    }

    public interface OnRecordFoundListener {
        void onRecordFound(UserRecord userRecord);
    }
}
