package com.noktiz.ui.web.behavior;

import com.google.gson.Gson;
import com.noktiz.ui.web.Application;
import com.noktiz.ui.web.BasePage;
import javafx.geometry.Pos;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hossein on 12/23/2014.
 */
public class IntroBehavior extends Behavior {
    String text;
    int number;
    Position p;

    public IntroBehavior(String text, int number, Position p) {
        this.text = text;
        this.number = number;
        this.p = p;
    }

    /**
     * for preventing to rerender at each time page rerenderd
     * @param component
     * @return
     */
    @Override
    public boolean isTemporary(Component component) {
        return true;
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);
        component.setOutputMarkupId(true);
        List<Intro> intros = component.getRequestCycle().getMetaData(Application.introKey);
        if (intros == null) {
            intros = new ArrayList<>();
            component.getRequestCycle().setMetaData(Application.introKey, intros);
        }
        if(p.equals(p.front)){
            intros.add(new Intro(number, new StringResourceModel(text, component,null), p,null));
        }
        else {
            intros.add(new Intro(number, new StringResourceModel(text, component, null), p, component));
        }
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        if(!((BasePage)component.getPage()).isMobile){
            renderIntroJs(response);
        }
    }
    private void renderIntroJs(IHeaderResponse response) {

        List<IntroBehavior.Intro> intros = RequestCycle.get().getMetaData(Application.introKey);
        if(intros==null || intros.isEmpty()){
            return;
        }
        Collections.sort(intros, new Comparator<Intro>() {
            @Override
            public int compare(IntroBehavior.Intro o1, IntroBehavior.Intro o2) {
                return Integer.compare(o1.number, o2.number);
            }
        });
        List<IntroBehavior.IntroJson> jsonObj= new ArrayList<>();
        for (IntroBehavior.Intro intro : intros) {
            if(intro.component==null){
                jsonObj.add(new IntroBehavior.IntroJson((String)intro.text.getObject()
                        ,intro.position, "null"));
            }
            else{
                jsonObj.add(new IntroBehavior.IntroJson((String)intro.text.getObject()
                        ,intro.position, new StringBuilder().append("#").append(intro.component.getMarkupId()).toString()));
            }
        }
        Gson g= new Gson();
        String jsonStr = g.toJson(jsonObj);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/introjs/intro.js")));
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/introjs/introjs.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(IntroBehavior.class,"IntroBehavior.js")));
        response.render(OnDomReadyHeaderItem.forScript(new StringBuilder().append("enableIntro(").append(jsonStr).append(")").toString()));
        response.render(OnDomReadyHeaderItem.forScript("startIntro()"));
        intros.clear();


    }

    public static enum Position {
        left, right, top, down, auto,front;
    }

    public class Intro {
        public int number;
        public IModel text;
        public Position position;
        public Component component;

        Intro(int number, IModel text, Position position,Component component) {
            this.number = number;
            this.text = text;
            this.position = position;
            this.component=component;
        }
    }
    public static class IntroJson{
        public String intro;
        public Position position;
        public String element;

        public IntroJson(String intro, Position position, String element) {
            this.intro = intro;
            this.position = position;
            this.element = element;
        }
    }
}
