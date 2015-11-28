package com.noktiz.ui.web;

import com.noktiz.domain.entity.PersonalInfo;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.TimeManager;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.ui.web.auth.UserSession;
import com.noktiz.ui.web.component.NotificationFeedbackPanel;
import com.noktiz.ui.web.theme.MyMetronic;
import com.noktiz.ui.web.utils.UAgentInfo;

import java.text.DateFormat;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by hasan on 9/25/14.
 */
public class BasePanel extends Panel {

    protected boolean isMobile = new UAgentInfo(WebSession.get().getClientInfo().getUserAgent(), "").isMobilePhone;

    public BasePanel( String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {

//        response.render(CssHeaderItem.forReference(new CssResourceReference(BasePage.class, "BasePage.css")));
//        new Bootstrap().renderHeadResponsive(response);
//        new Bootstrap().renderHeadPlain(response);
//        new Metronic().renderHeadResponsive(response);
        new MyMetronic().renderHeadResponsive(response,getSession().getLocale());
//        forBootstrap(response) ;
//        fromMetronic(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/plugins/glyphicons/css/glyphicons.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/plugins/glyphicons_halflings/css/halflings.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/css/pages/blog.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/uniform/css/uniform.default.css")));

//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/uniform/jquery.uniform.js")));
//        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "src/bootstrap-datepicker/js/bootstrap-datepicker.js")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "src/bootstrap-datepicker/css/datepicker.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "transparent.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/plugins/bootstrap-switch/3/css/bootstrap3/bootstrap-switch-nospace.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,
                "assets/plugins/bootstrap-switch/3/js/bootstrap-switch.js")));

        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/apple/apple.css")));
        BasePage.renderCss(response, "assets/noktiz/notixe-style", getSession());
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                "assets/noktiz/color.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,
                "assets/plugins/jQuery-slimScroll-1.3.0/jquery.slimscroll.js")));
        initialJS(response);
    }

    private void initialJS(IHeaderResponse response) {
        String pageTitle = getPageTitle();
        if(pageTitle!=null) {
            response.render(new OnDomReadyHeaderItem(
                    "document.title='"+ StringEscapeUtils.escapeJavaScript(pageTitle)+"'"));
        }
    }

    public BasePanel(String id) {
        super(id);
//        if (((UserSession) getSession()).getUser() != null) {
//            ((UserSession) getSession()).getUser().refresh();
//        }

    feedbackPanel = new NotificationFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
    }

    UserSession getUserSession() {
        if (getSession() instanceof UserSession) {
            return (UserSession) getSession();
        }
        return null;
    }

    public UserFacade getUserInSite() {
        UserSession userSession = getUserSession();
        if (userSession == null) {
            return null;
        }
        return userSession.getUser();
    }

    protected NotificationFeedbackPanel feedbackPanel;
    public String getFormattedUserDate(Date d){
        if(d==null){
            return null;
        }
        return getFormattedUserDate(d, TimeManager.bestFormatFor(d));
    }

    public String getFormattedUserDate(Date d, String format) {
        if (d == null) {
            return null;
        }
        Integer time = (Integer) getSession().getAttribute("timeZoneMinutes");
        if (time == null) {
            time = 0;
        }
        DateTimeZone dateTimeZone;
        final String userTimezone = getUserInSite().getPersonalInfo().getTimezone();
        if (userTimezone.equals(PersonalInfo.automaticTimezone)) {
            dateTimeZone = DateTimeZone.forOffsetHoursMinutes(time / 60, time % 60);
        } else {
            try {
                dateTimeZone = DateTimeZone.forTimeZone(new SimpleTimeZone(0, userTimezone));
            }catch(Exception e){
                dateTimeZone = DateTimeZone.forOffsetHoursMinutes(time / 60, time % 60);
            }
        }
        DateTime dRemote;
        dRemote = new DateTime(d.getTime(), dateTimeZone);

//        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return (dRemote.toString(format, getSession().getLocale()));
    }

    public DateTime getUserDate(Date d) {
        if (d == null) {
            return null;
        }
        Integer time = (Integer) getSession().getAttribute("timeZoneMinutes");
        if (time == null) {
            time = 0;
        }
        DateTime dRemote = new DateTime(d.getTime(), DateTimeZone.forOffsetHoursMinutes(time / 60, time % 60));
        return dRemote;
    }

    public String getPageTitle(){
        return null;
    }

}
