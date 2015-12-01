package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.model.UserFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
public class RateContextView {
    Long rateContextId;
    String title;
    String description;
    RateContext.RateState rateState;
    List<String> invitedFriends;
    Integer responseCount;
    Date creationDate;
    Double averageRate;

    public RateContextView(RateContext rateContext, UserFacade requester) {
        rateContextId = rateContext.getId();
        title = rateContext.getTitle();
        description = rateContext.getDescription();
        rateState = rateContext.getRequireRate();
        if(requester.getUser().equals(rateContext.getUser())){
            invitedFriends  = new ArrayList<>();
            for (User user : rateContext.getInvitedPersons().getIncludeUsers()) {
                invitedFriends.add(user.getId().toString());
            }
            if(rateContext.getInvitedPersons().isIncludeMyTrustedFriends())
                invitedFriends.add("MY_TRUSTED");
        }
        creationDate = rateContext.getCreationDate();
    }

    public Long getRateContextId() {
        return rateContextId;
    }

    public void setRateContextId(Long rateContextId) {
        this.rateContextId = rateContextId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RateContext.RateState getRateState() {
        return rateState;
    }

    public void setRateState(RateContext.RateState rateState) {
        this.rateState = rateState;
    }

    public List<String> getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(List<String> invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public Integer getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(Integer responseCount) {
        this.responseCount = responseCount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }
}
