/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.persistance;

import java.io.Serializable;
import org.hibernate.Session;

/**
 *
 * @author hossein
 */
public class SessionContainer implements Serializable{
    private Session session;
    private Integer trancCount=0;
    private boolean rolledback=false;
    public SessionContainer(Session session) {
        this.session = session;
    }

    public SessionContainer() {
    }

    public boolean isRolledback() {
        return rolledback;
    }

    public void setRolledback(boolean rolledback) {
        this.rolledback = rolledback;
    }

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * @return the trancCount
     */
    public Integer getTrancCount() {
        return trancCount;
    }

    /**
     * @param trancCount the trancCount to set
     */
    public void setTrancCount(Integer trancCount) {
        this.trancCount = trancCount;
    }
    
    public void increaseTrancCount(){
        trancCount++;
    }

    void decreaseTrancCount() {
        trancCount--;
    }
}
