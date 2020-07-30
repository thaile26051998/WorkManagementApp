package com.hcmus.easywork.models;

public class Authentication {
    private String email;
    private String password;

    private Authentication() {

    }

    public Authentication(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isNull() {
        return getEmail() == null || getPassword() == null;
    }
}
