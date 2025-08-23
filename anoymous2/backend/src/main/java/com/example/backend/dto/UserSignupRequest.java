package com.example.backend.dto;

public class UserSignupRequest {
    private String email;
    private String password;

    public UserSignupRequest() {}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
