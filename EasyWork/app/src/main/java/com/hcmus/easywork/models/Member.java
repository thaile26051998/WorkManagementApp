package com.hcmus.easywork.models;

import androidx.annotation.NonNull;

import com.hcmus.easywork.viewmodels.auth.User;

import com.google.gson.annotations.SerializedName;
import com.microsoft.officeuifabric.persona.Persona;

public class Member implements IComparableModel<Member> {
    @SerializedName("projectID")
    private int projectId;
    @SerializedName("userID")
    private int userId;
    @SerializedName("role")
    private int role = 0;
    @SerializedName("user")
    private User user;
    private Persona persona;

    public Member() {
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    private String name;

    public Member(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isTheSame(@NonNull Member item) {
        return this.getName().equals(item.getName());
    }
}
