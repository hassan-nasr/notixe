package com.noktiz.ui.web.theme;

import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePage;
import com.noktiz.ui.web.lang.LocalizationUtils;
import com.noktiz.ui.web.theme.bootstrap.Bootstrap;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Locale;

public class MyMetronic
{
    public void renderHeadResponsive(IHeaderResponse response, Locale local)
    {
        new Bootstrap().renderHeadResponsive(response);
//        response.render(JavaScriptHeaderItem.forReference(MyBootstrap.responsive()));
        if (isRTL(local)) {
            renderRTL(response);
        } else {
            renderLTR(response);
        }
    }

    private void renderRTL(IHeaderResponse response)
    {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-metro.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-metro-rtl.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/calibrate/style-metro-rtl.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-rtl.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/calibrate/style-rtl.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/style-responsive-rtl.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/print.css"), "print"));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/print-rtl.css"), "print"));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/pages/tasks.css")));
//        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/css/pages/tasks-rtl.css")));
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

    private void renderLTR(IHeaderResponse response)
    {
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
    /*private void renderLTROrg(IHeaderResponse response)
    {
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "css/style-metro.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "css/style.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "css/style-responsive.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "css/print.css"), "print"));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Global.class, "generate/css/thumbnail.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "css/pages/tasks.css")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Metronic.class, "plugins/gritter/css/jquery.gritter.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/breakpoints/breakpoints.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/jquery.blockui.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/jquery.cookie.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/excanvas.min.js"), null, null, true, null, "lt IE 9"));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/respond.min.js"), null, null, true, null, "lt IE 9"));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/gritter/js/jquery.gritter.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "plugins/jquery.pulsate.min.js")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Metronic.class, "scripts/app.js")));
        response.render(OnDomReadyHeaderItem.forScript("App.init();"));
    }*/

    private boolean isRTL(Locale local)
    {
        return BasePage.isRTL(local);
    }
}
