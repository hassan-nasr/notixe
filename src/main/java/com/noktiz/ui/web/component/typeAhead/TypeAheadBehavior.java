package com.noktiz.ui.web.component.typeAhead;

//import ir.apache.wicket.bootstrap.Bootstrap;
//import ir.mersad.global.Global;
//import ir.mersad.utils.converters.FarsiStringConverter;
//import ir.mersad.utils.enums.Lang;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.noktiz.ui.web.theme.bootstrap.Bootstrap;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.template.PackageTextTemplate;

public abstract class TypeAheadBehavior
        extends AbstractAjaxBehavior
{
    private Options options;

    public TypeAheadBehavior()
    {
        this(new Options());
    }

    public TypeAheadBehavior(Options options)
    {
        this.options = options;
    }

    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);


        tag.put("autocomplete", "off");
//        tag.put("lang", this.options.getLang().getValue());
    }

    public void renderHead(Component component, IHeaderResponse response)
    {
        super.renderHead(component, response);
        String id = component.getMarkupId();
        component.setOutputMarkupId(true);
        new Bootstrap().renderHeadResponsive(response);
//        if (this.options.getLang() == Lang.Fa) {
//            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Global.class, "js/FarsiType.js")));
//        }
        response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(TypeAheadBehavior.class, "custom-bootstrap.css")));
        includeJavascript(response, id);
    }

    protected void includeJavascript(IHeaderResponse response, String markupId)
    {
        Map<String, CharSequence> params = new HashMap();
        params.put("requestType", "get");
        params.put("callbackUrl", getCallbackUrl());
        params.put("inputFieldId", markupId);
        params.put("numOfItmes", String.valueOf(this.options.getNumberOfItems()));
        params.put("inputLengthForCache", String.valueOf(this.options.getInputLengthForCache()));

        PackageTextTemplate typeAheadTemplate = new PackageTextTemplate(TypeAheadBehavior.class, "typeahead-template.jst");

        response.render(OnDomReadyHeaderItem.forScript(typeAheadTemplate.asString(params)));
    }

    public final void onRequest()
    {
        WebApplication app = (WebApplication)getComponent().getApplication();
        AjaxRequestTarget target = app.newAjaxRequestTarget(getComponent().getPage());

        RequestCycle requestCycle = RequestCycle.get();
        requestCycle.scheduleRequestHandlerAfterCurrent(target);

        String userInput = requestCycle.getRequest().getRequestParameters().getParameterValue("q").toOptionalString();



































        userInput = Strings.isEmpty(userInput) ? "" : userInput;
//        userInput = FarsiStringConverter.converToPersian(userInput);
        Collection<String> choices = getChoices(userInput);
        JSONArray jsonArray = new JSONArray(choices);
        String result = jsonArray.toString();


        requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler("application/json", "UTF-8", result));
    }

    protected abstract Collection<String> getChoices(String paramString);
}
