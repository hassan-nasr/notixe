package com.noktiz.ui.web.auth;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 * Created by Sina Labbaf on 2015-03-29.
 */
public class UserRoles extends Roles {
    static final public String NOKHODI="NOKHODI";

    public UserRoles() {
        super();
    }

    public UserRoles(String roles) {
        super(roles);
    }

    public UserRoles(String[] roles) {
        super(roles);
    }

    public static enum RoleEnum{
        USER,ADMIN
    }
}
