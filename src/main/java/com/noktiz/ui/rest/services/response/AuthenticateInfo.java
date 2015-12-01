package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.User;

/**
 * Created by hassan on 02/11/2015.
 */
public class AuthenticateInfo {
    private String accessToken;
    private Long expireDate;
    private String email;
    private String userId;
    private String firstName;
    private User.Gender gender;
    private String lastName;


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setGender(User.Gender gender) {
        this.gender = gender;
    }

    public User.Gender getGender() {
        return gender;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }
}
