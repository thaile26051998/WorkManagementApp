package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.ProjectApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.models.Project;
import com.hcmus.easywork.models.Task;
import com.hcmus.easywork.models.chat.group.Group;
import com.hcmus.easywork.models.project.ProjectMember;

import java.util.List;

public class ProjectRepository extends AuthenticatedRepository<ProjectApiService> {
    public ProjectRepository() {

    }

    public ResponseManager<Project> create(Project project) {
        return new ResponseManager<>(getApi().createProject(project));
    }

    public ResponseManager<List<Project>> getAll(int userId) {
        return new ResponseManager<>(getApi().getProjects(userId));
    }

    public ResponseManager<List<Task>> getProjectTask(int projectId) {
        return new ResponseManager<>(getApi().getProjectTasks(projectId));
    }

    public ResponseManager<MessageResponse> addProjectMember(int projectId, int userId, int role) {
        return new ResponseManager<>(getApi().addProjectMember(projectId, userId, role));
    }

    public ResponseManager<MessageResponse> updateProjectMember(int projectId, int userId, ProjectMember member) {
        return new ResponseManager<>(getApi().updateProjectMember(projectId, userId, member));
    }

    public ResponseManager<MessageResponse> removeMember(int projectId, int memberId) {
        return new ResponseManager<>(getApi().removeMember(projectId, memberId));
    }

    public ResponseManager<List<ProjectMember>> getAllProjectMember(int projectId) {
        return new ResponseManager<>(getApi().getAllProjectMember(projectId));
    }

    public ResponseManager<ProjectMember> getSingleProjectMember(int projectId, int memberId) {
        return new ResponseManager<>(getApi().getSingleProjectMember(projectId, memberId));
    }

    public ResponseManager<MessageResponse> leaveProject(int projectId, int userId) {
        return new ResponseManager<>(getApi().leaveProject(projectId, userId));
    }

    public ResponseManager<Project> getProject(int projectId) {
        return new ResponseManager<>(getApi().getProject(projectId));
    }

    public ResponseManager<List<ProjectMember>> getAllMembers(int projectId) {
        return new ResponseManager<>(getApi().getAllMembers(projectId));
    }

    public ResponseManager<List<Project>> getArchiveProject(int userId) {
        return new ResponseManager<>(getApi().getArchivedProject(userId));
    }

    public ResponseManager<MessageResponse> toggleArchiveProject(int projectId, boolean archive) {
        return new ResponseManager<>(getApi().toggleArchiveProject(projectId, archive));
    }

    public ResponseManager<MessageResponse> archiveProject(int projectId) {
        return new ResponseManager<>(getApi().archiveProject(projectId));
    }

    public ResponseManager<MessageResponse> updateProject(int projectId, Project project) {
        return new ResponseManager<>(getApi().updateProject(projectId, project));
    }

    public ResponseManager<List<Group>> getGroups(int projectId) {
        return new ResponseManager<>(getApi().getGroups(projectId));
    }

    @Override
    protected Class<ProjectApiService> getApiClass() {
        return ProjectApiService.class;
    }
}
