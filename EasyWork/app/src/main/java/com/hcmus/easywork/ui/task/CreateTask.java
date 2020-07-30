package com.hcmus.easywork.ui.task;

import com.hcmus.easywork.data.old.BaseRepository;
import com.hcmus.easywork.models.Task;

/**
 * TODO: move to TaskViewModel
 */
@Deprecated
public class CreateTask extends BaseRepository<Task> {
    public CreateTask(){

    }

    public void createTask(Task task){
        this.call = getTokenRequiredApiService().createTask(task);
    }
}
