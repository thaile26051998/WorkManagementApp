package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.GroupApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.chat.group.GroupMember;
import com.hcmus.easywork.models.chat.group.GroupMemberAPI;
import com.hcmus.easywork.models.file.EwFile;

import java.util.List;

public class GroupRepository extends AuthenticatedRepository<GroupApiService> {
    public GroupRepository() {

    }

    public ResponseManager<Group> createGroup(Group group) {
        return new ResponseManager<>(getApi().createGroup(group));
    }

    public ResponseManager<List<Group>> getGroups(int userId) {
        return new ResponseManager<>(getApi().getAllGroups(userId));
    }

    public ResponseManager<Group> getGroup(int groupId) {
        return new ResponseManager<>(getApi().getGroup(groupId));
    }

    public ResponseManager<List<GroupMember>> getAllMembers(int groupId) {
        return new ResponseManager<>(getApi().getAllMembers(groupId));
    }

    public ResponseManager<List<Message>> getMessages(int groupId) {
        return new ResponseManager<>(getApi().getAllMessages(groupId));
    }

    public ResponseManager<MessageResponse> deleteMember(int groupId, int userId) {
        return new ResponseManager<>(getApi().deleteMember(groupId, userId));
    }

    public ResponseManager<MessageResponse> deleteGroup(int groupId) {
        return new ResponseManager<>(getApi().deleteGroup(groupId));
    }

    public ResponseManager<GroupMemberAPI> addMember(int groupId, int userID, boolean auth, int projectID) {
        return new ResponseManager<>(getApi().addMember(groupId, userID, auth, projectID));
    }

    public ResponseManager<List<EwFile>> getAllFiles(int groupId) {
        return new ResponseManager<>(getApi().getAllFiles(groupId));
    }

    public ResponseManager<MessageResponse> pinMessage(int groupId, int messageId, boolean isPinned) {
        return new ResponseManager<>(getApi().pinMessage(groupId, messageId, isPinned));
    }

    public ResponseManager<Message> getPinnedMessage(int groupId) {
        return new ResponseManager<>(getApi().getPinnedMessage(groupId));
    }

    @Override
    protected Class<GroupApiService> getApiClass() {
        return GroupApiService.class;
    }
}
