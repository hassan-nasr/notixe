package com.noktiz.ui.rest.core.auth;

import java.util.Date;
import java.util.Set;

/**
 * token data which is given to a client to identify the client in other services
 * <p>
 * Created by hassan on 03/11/2015.
 */
public class TokenData {
    String userId;
    Set<String> roles;
    Set<String> permissions;
    Date issueData;
    Date expireDate;


    public TokenData() {
    }

    public TokenData(String userId, Set<String> roles, Date issueData, Date expireDate, Set<String> permissions) {
        this.userId = userId;
        this.roles = roles;
        this.issueData = issueData;
        this.expireDate = expireDate;
        this.permissions = permissions;
    }

    /**
     * useful to know the related user
     *
     * @return user id
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * list of user roles that user have ( and also this client has granted access to use them)
     *
     * @return list of user roles;
     */
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * get the creation time of this token which is mostly useful for single sign out
     * for single sign out authentication server invalidates all token issued prior to a given time.
     *
     * @return
     */
    public Date getIssueData() {
        return issueData;
    }

    public void setIssueData(Date issueData) {
        this.issueData = issueData;
    }

    /**
     * expiration date of a token
     *
     * @return
     */
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * permissions granted to this token
     *
     * @return
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
