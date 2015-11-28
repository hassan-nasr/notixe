package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.User;

import javax.persistence.ManyToOne;

/**
 * Created by hasan on 2014-11-03.
 */
public class PredefinedRateContext extends RateContext {

    @ManyToOne
    PredefinedQuestion questions;

    public PredefinedRateContext() {
    }

    public PredefinedRateContext(PredefinedQuestion questions, User user) {
        super("", "",user);
    }

    @Override
    public String getTitle() {
        return questions.getTitle();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n" + questions.getDescription();
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public void setTitle(String title) {
        throw new UnsupportedOperationException();
    }
}
