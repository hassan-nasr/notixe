package com.noktiz.ui.web.rate;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.ui.web.friend.PersonListProvider;
import com.noktiz.ui.web.profile.ProfilePage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
* Created by hasan on 2014-12-02.
*/
public class AnsweredPerson extends PersonListProvider.IPerson<Rate> {

    private Rate rate;
    private boolean showImage;

    public Rate getRate() {
        return rate;
    }

    public AnsweredPerson(Rate rate1, boolean showImage) {
        this.rate = rate1;
        this.showImage = showImage;
        if (rate.getThread(new UserFacade(rate.getSender())) != null) {
            addAction(new ReplyAction(rate));
        }
    }

    @Override
    public Rate getObj() {
        return rate;
    }

    @Override
    public String getImageUrl() {
        if(!showImage)
            return null;
        return RequestCycle.get().urlFor(ImageManagement.getUserImageResourceReferece(),
                ImageManagement.getUserImageParameter(new UserFacade(rate.getReceiver()), ImageManagement.ImageSize.medium)).toString();

    }

    @Override
    public IModel<String> getBGClass() {
        return Model.of(" bg-green ");
    }

    @Override
    public String getImageLink() {
        return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid", rate.getReceiver().getId())).toString();
    }

    @Override
    public String getSubtitleLink() {
        return RequestCycle.get().urlFor(ProfilePage.class, new PageParameters().add("userid", rate.getReceiver().getId())).toString();
    }

    @Override
    public IModel<String> getTitle() {
//            if(rate.getComment()!=null)
        return Model.of(rate.getContext().getTitle());
//            return Model.of("");
    }

    @Override
    public IModel<Boolean> getSearchInSubtitle() {
        return Model.of(true);
    }

    @Override
    public Model<String> getSubTitle() {
        return Model.of(rate.getReceiver().getName());
    }

    @Override
    public IModel<String> getDesc() {
        return Model.of(rate.getComment());
    }

    @Override
    public boolean getStarsEnable() {
        return getRate().getRate() !=null &&  getRate().getRate()>0;
    }

    @Override
    public IModel<Double> getStars() {
        return Model.of(rate.getRate()!=null?rate.getRate().doubleValue():new Double(0));
    }

}
