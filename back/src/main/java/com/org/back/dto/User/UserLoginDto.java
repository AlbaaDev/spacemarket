package com.org.back.dto.User;

public class UserLoginDto {
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    private String token;
    private long expiresIn;

    public String getUsername() {
        return email;
    }

    public void setUserName(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName =  lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public UserLoginDto setToken(String token) {
        this.token = token;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserLoginDto setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
