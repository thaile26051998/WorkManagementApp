package com.hcmus.easywork.data.repository;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.api.TaskApiService;
import com.hcmus.easywork.data.common.AuthenticatedRepository;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.models.Task;

import java.util.List;

public class TaskRepository extends AuthenticatedRepository<TaskApiService> {
    public TaskRepository() {

    }

    public ResponseManager<MessageResponse> deleteTask(int taskId) {
        return new ResponseManager<>(getApi().deleteTask(taskId));
    }

    public ResponseManager<List<Task>> getMyTasks(int userId) {
        return new ResponseManager<>(getApi().getMyTasks(userId));
    }

    public ResponseManager<List<Task>> getAllTasks(int userId) {
        return new ResponseManager<>(getApi().getAllTasksOfAllProjects(userId));
    }

    public ResponseManager<Task> getSingleTask(int taskId) {
        return new ResponseManager<>(getApi().getSingleTask(taskId));
    }

    public ResponseManager<MessageResponse> updateTask(int taskId, Task task) {
        return new ResponseManager<>(getApi().updateTask(taskId, task));
    }

    @Override
    protected Class<TaskApiService> getApiClass() {
        return TaskApiService.class;
    }
}
