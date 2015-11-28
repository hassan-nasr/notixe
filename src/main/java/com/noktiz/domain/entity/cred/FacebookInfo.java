package com.noktiz.domain.entity.cred;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by hasan on 8/19/14.
 */
@Embeddable
public class FacebookInfo implements Serializable {
    private String facebook_id;
    private String facebook_access_token;

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public String getFacebook_access_token() {
        return facebook_access_token;
    }

    public void setFacebook_access_token(String facebook_access_token) {
        this.facebook_access_token = facebook_access_token;
    }
}
