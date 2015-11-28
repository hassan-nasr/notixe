package com.noktiz.ui.web.info;

import com.noktiz.ui.web.BasePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import javax.enterprise.inject.Model;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hasan on 2014-11-20.
 */
public class PopupInfo extends BasePanel {
    public PopupInfo(String id, IModel text, String place, String iconClass) {
        super(id);
        WebMarkupContainer wmc = new WebMarkupContainer("info");
        add(wmc);
        wmc.add(new AttributeModifier("data-content", text));
        initRest(place, iconClass, wmc);
    }

    public void initRest(String place, String iconClass, WebMarkupContainer wmc) {
        if (!Arrays.asList(new String[]{"top", "bottom", "left", "right"}).contains(place)) {
            place="right";
        }
        wmc.add(new AttributeModifier("data-placement",place));
        wmc.add(new AttributeModifier("data-placement",place));
        WebMarkupContainer icon = new WebMarkupContainer("icon");
        wmc.add(icon);
        if(iconClass == null){
            iconClass = "icon-info-sign";
        }
        icon.add(new AttributeModifier("class",iconClass));
    }

    public PopupInfo(String id, String text, String place, String iconClass) {
        super(id);
        WebMarkupContainer wmc = new WebMarkupContainer("info");
        add(wmc);
        wmc.add(new AttributeModifier("data-content", text));
        initRest(place, iconClass, wmc);
    }
}
