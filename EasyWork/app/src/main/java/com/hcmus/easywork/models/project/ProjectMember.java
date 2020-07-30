package com.hcmus.easywork.models.project;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;
import com.hcmus.easywork.models.member.Member;
import com.microsoft.officeuifabric.persona.Persona;

public class ProjectMember extends Member implements IComparableModel<ProjectMember> {
    // region Attributes
    @SerializedName("role")
    private boolean mIsLeader = false;
    private Persona persona;
    // endregion

    public ProjectMember() {

    }

    @Override
    public boolean isTheSame(@NonNull ProjectMember item) {
        return (this.getProjectId() == item.getProjectId()) &&
                (this.getUserId() == item.getUserId());
    }

    // region Getters
    public boolean isLeader() {
        return mIsLeader;
    }

    public Persona getPersona() {
        return persona;
    }


    // endregion

    // region Setters
    public void setLeader(boolean isLeader) {
        this.mIsLeader = isLeader;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    // endregion
}
