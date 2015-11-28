/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.entity.cred;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author sina
 */
@Embeddable
public class GoogleInfo implements Serializable {
    private String primary_email;
    private String google_id;
    private String google_url;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length=4096)
    private String google_access_token;

    public String getPrimary_email() {
        return primary_email;
    }

    public void setPrimary_email(String primary_email) {
        this.primary_email = primary_email;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public String getGoogle_access_token() {
        return google_access_token;
    }
    
    public void setGoogle_access_token(String google_access_token) {
        this.google_access_token = google_access_token;
    }

    public String getGoogle_url() {
        return google_url;
    }

    public void setGoogle_url(String google_url) {
        this.google_url = google_url;
    }
}
