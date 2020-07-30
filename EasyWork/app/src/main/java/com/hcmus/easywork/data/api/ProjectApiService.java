package com.hcmus.easywork.data.api;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.AuthenticatedApiService;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.project.ProjectMember;

import java.util.List;

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

public interface ProjectApiService extends AuthenticatedApiService {
    @GET("projects")
    Call<List<Project>> getProjects(@Query("userID") int userId);

    @POST("projects")
    Call<Project> createProject(@Body Project project);

    @GET("tasks/projectAndroid/{id}")
    Call<List<Task>> getProjectTasks(@Path("id") int id);

    @FormUrlEncoded
    @POST("projects/{id}/members")
    Call<MessageResponse> addProjectMember(@Path("id") int id,
                                           @Field("userID") int userID,
                                           @Field("role") int role);

    @DELETE("projects/{id}/members/{uid}")
    Call<MessageResponse> removeMember(@Path("id") int id, @Path("uid") int memberId);

    @GET("projects/{id}/members/{uid}")
    Call<ProjectMember> getSingleProjectMember(@Path("id") int id, @Path("uid") int memberId);

    @GET("projects/{id}/members")
    Call<List<ProjectMember>> getAllProjectMember(@Path("id") int id);

    @DELETE("projects/{id}/members/{uid}")
    Call<MessageResponse> leaveProject(@Path("id") int id, @Path("uid") int uId);

    @GET("projects/{id}")
    Call<Project> getProject(@Path("id") int id);

    @GET("projects/{id}/members")
    Call<List<ProjectMember>> getAllMembers(@Path("id") int projectId);

    @GET("projects/archive")
    Call<List<Project>> getArchivedProject(@Query("userID") int userId);

    @FormUrlEncoded
    @PUT("projects/{id}")
    Call<MessageResponse> toggleArchiveProject(@Path("id") int projectID, @Field("archive") boolean archive);

    @PUT("projects/{id}/archive")
    Call<MessageResponse> archiveProject(@Path("id") int projectId);

    @PUT("projects/{id}")
    Call<MessageResponse> updateProject(@Path("id") int projectId, @Body Project project);

    @PUT("projects/{id}/members/{uid}")
    Call<MessageResponse> updateProjectMember(@Path("id") int projectId, @Path("uid") int uId, @Body ProjectMember member);

    @GET("projects/{id}/groups")
    Call<List<Group>> getGroups(@Path("id") int projectId);
}
