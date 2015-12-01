package com.noktiz.ui.rest.services.ws;

import com.noktiz.domain.entity.rate.Rate;
import com.noktiz.domain.entity.rate.RateContext;
import com.noktiz.domain.entity.rate.RateContextManager;
import com.noktiz.domain.entity.rate.RateManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.rest.services.BaseWS;
import com.noktiz.ui.rest.services.response.RateView;
import com.noktiz.ui.rest.services.response.SimpleResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 30/11/2015.
 */
@Path("/")
public class RateWS extends BaseWS  {
    @GET
    @Path("/rates")
    @Produces("application/json")
    public String rate(@QueryParam("rateContextId")Long rateContextId, @QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        List<RateView> ret = new ArrayList<>();
        RateManager rateManager = new RateManager();
        if(rateContextId == null){
            final List<Rate> rates = rateManager.loadLastRatesOfUser(getUserInSite().getUser(), from, count, false);
            for (Rate rate : rates) {
                ret.add(new RateView(rate,getUserInSite()));
            }
        }
        else{
            final RateContext rateContext = new RateContextManager().load(rateContextId);
            if(!rateContext.getUser().equals(getUserInSite().getUser()))
                return createSimpleResponse(SimpleResponse.Status.Failed, "Unpasteurized");
            final List<Rate> rates =rateManager.loadRatesOfContext(rateContext, from, count);
            for (Rate rate : rates) {
                ret.add(new RateView(rate,getUserInSite()));
            }
        }
        return getJsonCreator().getJson(ret);
    }
    @GET
    @Path("/ratings")
    @Produces("application/json")
    public String ratings(@QueryParam("rateContextId")Long rateContextId, @QueryParam("from") Integer from, @QueryParam("count") Integer count){
        UserFacade userInSite = getUserInSite();
        if(userInSite == null)
            return createSimpleResponse(SimpleResponse.Status.Failed,"Unauthenticated");
        if(from == null)
            from =0;
        if(count  == null)
            count = 50;
        List<RateView> ret = new ArrayList<>();
        RateManager rateManager = new RateManager();
        if(rateContextId == null){
            final List<Rate> rates = rateManager.loadLastRatesDoneByUser(getUserInSite(), from, count);
            for (Rate rate : rates) {
                ret.add(new RateView(rate,getUserInSite()));
            }
        }
        else{
            final RateContext rateContext = new RateContextManager().load(rateContextId);
            final List<Rate> rates =rateManager.loadLastRatesDoneByUserInContext(getUserInSite(),rateContext, from, count);
            for (Rate rate : rates) {
                ret.add(new RateView(rate,getUserInSite()));
            }
        }
        return getJsonCreator().getJson(ret);
    }


}
