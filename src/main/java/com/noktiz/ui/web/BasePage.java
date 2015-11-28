package com.noktiz.ui.web;

import com.noktiz.domain.system.SystemConfigManager;
import com.noktiz.ui.web.header.FooterPanel;
import com.noktiz.ui.web.theme.MyMetronic;
import com.noktiz.ui.web.utils.UAgentInfo;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.*;

/**
 * Created by hasan on 9/12/14.
 */
public class BasePage extends WebPage {



    public boolean isMobile=new UAgentInfo(WebSession.get().getClientInfo().getUserAgent(),"").isMobilePhone;
    public BasePage(WebMarkupContainer footer) {
        super();
        WebMarkupContainer body = new TransparentWebMarkupContainer("body");
        add(body);
        String boxMode="  ";
        if(!isMobile)
            body.add(new AttributeAppender("class",boxMode));
        body.add(new AttributeAppender("class", " " + getBodyStyleClass()));

        if(footer != null)
            add(footer);
        else
            add(new FooterPanel("footerpanel", " © "+new GregorianCalendar().get(Calendar.YEAR)+ SystemConfigManager.getCurrentConfig().getAppName()+" Inc."));
    }

    protected String getBodyStyleClass() {
        return "";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(BasePage.class,"BasePage.css")));
//        new Bootstrap().renderHeadResponsive(response);
//        new Bootstrap().renderHeadPlain(response);
//        new Metronic().renderHeadResponsive(response);
        new MyMetronic().renderHeadResponsive(response,getSession().getLocale());
//        forBootstrap(response) ;
//        fromMetronic(response);

        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/plugins/glyphicons/css/glyphicons.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/plugins/glyphicons_halflings/css/halflings.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/css/pages/blog.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"transparent.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/plugins/bootstrap-switch/3/css/bootstrap3/bootstrap-switch-nospace.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,"assets/plugins/bootstrap-switch/3/js/bootstrap-switch.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/apple/apple.css")));
        renderCss(response, "assets/noktiz/notixe-style", getSession());
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/noktiz/color.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,"assets/noktiz/light-theme.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class,"assets/plugins/jQuery-slimScroll-1.3.0/jquery.slimscroll.js")));
        response.render(new OnDomReadyHeaderItem("$('.switch').bootstrapSwitch()"));

    }

    public static void renderCss(IHeaderResponse response, final String pathWithOutSuffix, Session session) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class,
                pathWithOutSuffix +(isRTL(session.getLocale())?"-rtl":"")+ ".css")));
    }


    private void forBootstrap(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/css/bootstrap-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/js/bootstrap.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/css/jasny-bootstrap.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/css/jasny-bootstrap-responsive.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/bootstrap/jasnyBootstrap/js/jasny-bootstrap.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/fontawesome/css/font-awesome.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/bootstrap/fontawesome/css/font-awesome-ie7.css"), null, null, "lt IE 9"));
    }

    private void fromMetronic(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-metro.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/print.css"), "print"));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Global.class, "generate/css/thumbnail.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/pages/tasks.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/gritter/css/jquery.gritter.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/breakpoints/breakpoints.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery.blockui.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery.cookie.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/excanvas.min.js"), null, null, true, null, "lt IE 9"));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/respond.min.js"), null, null, true, null, "lt IE 9"));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/gritter/js/jquery.gritter.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery.pulsate.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/scripts/app.js")));
        response.render(OnDomReadyHeaderItem.forScript("App.init();"));
    
    }
    public boolean isEqual(String s,Boolean b){
        if(s==null || !Boolean.valueOf(s).equals(b)){
            return false;
        }
        return true;
    }

    public boolean isRTL() {
        return isRTL(getSession().getLocale());
    }
    public static Boolean isRTL(Locale locale){
        if(true)return false;
        return "fa".equals(locale .getLanguage());
    }
}
