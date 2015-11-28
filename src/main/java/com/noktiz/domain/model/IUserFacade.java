/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.domain.model;

import com.noktiz.domain.entity.Friendship;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author hossein
 * @param <UF> type of implemented user facade
 * @param <T> type of user class
 */
public interface IUserFacade<UF extends IUserFacade,T> extends Serializable {
    
    T getUser();

    Result addFriend(UF friend);

    void block(UF blocker);

    boolean doesPasswordMatch(String password);


    Date getBirthdate();

    String getEmail();

    List<Friendship> getFriends();

    String getFirstName();

    Date getJoinDate();

    String getLastName();

    List<ThreadFacade> getMyThreadList(long first, long count);

    String getName();

    long numOfThreads();

    void setBirthdate(Date birthdate);

    void setEmail(String email);

    void setFirstName(String fristName);

    void setJoinDate(Date joinDate);

    void setLastName(String lastName);

    Result startThread(UF receiver, String title, String text);

}
