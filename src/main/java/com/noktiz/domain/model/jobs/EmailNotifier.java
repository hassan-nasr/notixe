/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model.jobs;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.BaseNotification;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.email.EmailCreator;
import com.noktiz.domain.model.email.IEmailProvider;
import com.noktiz.domain.model.image.ImageManagement;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.Application;
import java.util.List;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.core.request.mapper.BookmarkableMapper;
import org.apache.wicket.core.request.mapper.ResourceReferenceMapper;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.AbstractMapper;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.response.NullResponse;
import org.apache.wicket.util.ValueProvider;
import org.hibernate.HibernateException;
import org.hibernate.StaleObjectStateException;

/**
 *
 * @author Hossein
 */
public class EmailNotifier extends TimerTask {

//    @Override
//    public void run() {
//        User user = null;
//        final MockWebRequest request = new MockWebRequest(null);
//        final NullResponse response = NullResponse.getInstance();
//        org.apache.wicket.Application application = MailApplication.get();
//        application.setName("mailer");
//        ThreadContext.setApplication(application);
//        application.initApplication();
//
//        RequestCycle rc = application.createRequestCycle(request, response);
//        ThreadContext.setRequestCycle(rc);
////        RequestCycle rc = new RequestCycle(new RequestCycleContext(request, response, new UrlResourceReferenceMapper(), new DefaultExceptionMapper()));
//        HSF.get().onBeginRequest(rc);
//        while (true) {
//            List<BaseNotification> notifications = BaseNotification.getEmailNotification(user);
//            if (notifications == null) {
//                break;
//            }
//            if (notifications.isEmpty()) {
//                continue;
//            }
//            IEmailProvider.IEmail email = createNotificationEmail(notifications);
//            boolean send = IEmailProvider.get().
//                    send(notifications.get(0).getOwner().getEmail(), email);
//            if (send) {
//                HSF.get().beginTransaction();
//                try {
//                    for (BaseNotification baseNotification : notifications) {
//                        baseNotification.markAsSent();
//                    }
//                } finally {
//                    try {
//                        HSF.get().commitTransaction();
//                    } catch (StaleObjectStateException ex) {
//                    }
//                }
//            }
//        }
//        HSF.get().onEndRequest(rc);
//        ThreadContext.detach();
//    }
    @Override
    public void run() {
        RequestCycle rc = new RequestCycle(new RequestCycleContext(new MockWebRequest(null), NullResponse.getInstance(), new BookmarkableMapper(), new DefaultExceptionMapper()));
        HSF.get().onBeginRequest(rc);
        User user = null;
        ResourceReference uirr = ImageManagement.getUserImageResourceReferece();
        rc.urlFor(uirr, null);        
        while (true) {
            List<BaseNotification> notifications = BaseNotification.getEmailNotification(user);
            if (notifications == null) {
                break;
            }
            if (notifications.isEmpty()) {
                continue;
            }
            user=notifications.get(0).getOwner();
            IEmailProvider.IEmail email = createNotificationEmail(notifications);
            boolean send = IEmailProvider.get().
                    send(notifications.get(0).getOwner().getEmail(), email);
            if (send) {
                HSF.get().beginTransaction();
                try {
                    for (BaseNotification baseNotification : notifications) {
                        baseNotification.markAsSent();
                    }
                    HSF.get().commitTransaction();
                } catch (HibernateException ex) {
                    HSF.get().roleback();
                    Logger.getLogger(this.getClass()).info("HibernateException", ex);
                }
            }
        }
        HSF.get().onEndRequest(rc);
    }

    private IEmailProvider.IEmail createNotificationEmail(List<BaseNotification> notifications) {
        return EmailCreator.createWeeklyEmail(notifications);
    }
}
