package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.ResultException;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.thread.ThreadPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * Created by hasan on 9/26/14.
 */
public class ReplyAction extends PersonListProvider.IPerson.IAction {
    Rate rate;
    private RateManager rateManager = new RateManager();

    public ReplyAction(Rate rate) {
        this.rate = rate;
    }

    @Override
    public IModel<String> getActionTitle() {
        if (rate.getThread() == null)
            return Model.of(new StringResourceModel("reply", null).getObject());
        else
            return Model.of(new StringResourceModel("showThread", null).getObject());
    }

    @Override
    public boolean onAction(AjaxRequestTarget art, Component caller) {
        if (rate.getThread() == null) {
            try {
                rateManager.createReplyThread(rate);
            } catch (ResultException e) {
                e.getResult().displayInWicket(caller);
                return true;
            }
        }
        caller.setResponsePage(ThreadPage.class, ThreadPage.getThreadPageParam(rate.getThread()));
        return true;
    }

}