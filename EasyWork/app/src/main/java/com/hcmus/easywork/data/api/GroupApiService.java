package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.models.chat.Message;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.chat.group.GroupMember;
import com.hcmus.easywork.models.chat.group.GroupMemberAPI;
import com.hcmus.easywork.models.file.EwFile;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupApiService extends AuthenticatedApiService {
    @POST("groups/")
    Call<Group> createGroup(@Body Group group);

    @GET("users/{id}/groups")
    Call<List<Group>> getAllGroups(@Path("id") int userId);

    @GET("groups/{id}")
    Call<Group> getGroup(@Path("id") int groupId);

    @GET("groups/{id}/members")
    Call<List<GroupMember>> getAllMembers(@Path("id") int id);

    @DELETE("groups/{id}/members/{uid}")
    Call<MessageResponse> deleteMember(@Path("id") int id, @Path("uid") int uid);

    @DELETE("groups/{id}")
    Call<MessageResponse> deleteGroup(@Path("id") int id);

    @FormUrlEncoded
    @POST("groups/{id}/members")
    Call<GroupMemberAPI> addMember(@Path("id") int groupId,
                                   @Field("userID") int userID,
                                   @Field("auth") boolean auth,
                                   @Field("projectID") int projectID);

    @GET("groups/{id}/files")
    Call<List<EwFile>> getAllFiles(@Path("id") int groupId);

    @PUT("groups/{gid}/messages/{id}")
    Call<MessageResponse> pinMessage(@Path("gid") int groupId,
                                     @Path("id") int messageId,
                                     @Query("isPin") boolean isPinned);

    @GET("groups/{id}/pinnedMessage")
    Call<Message> getPinnedMessage(@Path("id") int groupId);

    // TODO: Update a group with id
    // router.put("/:id",groups.update);
    @PUT("groups/{id}")
    Call<ResponseBody> updateGroup();

    // TODO: delete all groups
    // router.delete("/",groups.deleteAll);
    @DELETE("groups/")
    Call<ResponseBody> deleteAllGroups();

    // TODO: get a member of group
    // router.get("/:id/members/:uid",groups.findOneMember);
    @GET("groups/{id}/member/{uid}")
    Call<ResponseBody> getMemberInGroup();

    // TODO: get all message of group
    // router.get("/:id/messages",groups.findAllMessages);
    @GET("groups/{id}/messages")
    Call<List<Message>> getAllMessages(@Path("id") int groupId);

    // TODO: update member
    // router.put("/:id/members/:uid",groups.updateMember);
    @PUT("groups/{id}/members/{uid}")
    Call<ResponseBody> updateMemberInGroup();

    // TODO: delete all member of group
    // router.delete("/:id/members",groups.deleteAllMembers);
    @DELETE("groups/{id}/members")
    Call<ResponseBody> deleteAllMembersInGroup();
}
