package com.noktiz.domain.entity.rate;

import com.noktiz.domain.persistance.HSF;
import org.apache.log4j.Logger;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.response.NullResponse;

import java.util.TimerTask;

/**
 * Created by hasan on 2014-11-12.
 */
public class RateInviteBackGroundJob extends TimerTask {

    RateContextManager rateContextManager = new RateContextManager();
    @Override
    public void run() {
        try {
            while (true) {
                RequestCycle rc = null;
                try {
                    rc = new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper()));
                    HSF.get().onBeginRequest(rc);

                    boolean more = rateContextManager.sendAutomaticInvites(10);
                    if(!more)
                        break;
                }finally {
                    HSF.get().onEndRequest(rc);
                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Logger.getLogger(this.getClass()).error(e);
        }
    }


}
