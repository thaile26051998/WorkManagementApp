package com.hcmus.easywork.viewmodels.member;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hcmus.easywork.data.response.MessageResponse;
import com.hcmus.easywork.data.common.ResponseManager;
import com.hcmus.easywork.data.repository.ProjectRepository;
import com.hcmus.easywork.models.project.ProjectMember;
import com.hcmus.easywork.viewmodels.common.BaseOperatingResult;
import com.hcmus.easywork.viewmodels.common.OperatingState;

import java.util.ArrayList;
import java.util.List;

public class MemberViewModel extends AndroidViewModel {
    private MutableLiveData<List<ProjectMember>> model = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ProjectMember> userMember = new MutableLiveData<>();
    private ProjectRepository projectRepository;
    private MutableLiveData<RemovingMemberResult> mRemovingMemberResult;

    public MemberViewModel(@NonNull Application application) {
        super(application);
        this.projectRepository = new ProjectRepository();
        mRemovingMemberResult = new MutableLiveData<>(new RemovingMemberResult());
    }

    public void fetch(int projectId) {
        this.projectRepository.getAllProjectMember(projectId).enqueue(new ResponseManager.OnResponseListener<List<ProjectMember>>() {
            @Override
            public void onResponse(List<ProjectMember> response) {
                model.setValue(new ArrayList<>(response));
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public LiveData<List<ProjectMember>> get() {
        return this.model;
    }

    public LiveData<ProjectMember> getUserMember() {
        return this.userMember;
    }

    public LiveData<RemovingMemberResult> getRemovingMemberResult(){
        return this.mRemovingMemberResult;
    }

    public void setUserMember(ProjectMember projectMember) {
        this.userMember.setValue(projectMember);
    }

    public void setModelValue(ArrayList<ProjectMember> memberArrayList){
        model.setValue(memberArrayList);
    }

    public static class RemovingMemberResult extends BaseOperatingResult<MessageResponse> {
        public RemovingMemberResult() {
            super();
        }

        public RemovingMemberResult(OperatingState state, MessageResponse result, String errorMsg) {
            super(state, result, errorMsg);
        }
    }
}
