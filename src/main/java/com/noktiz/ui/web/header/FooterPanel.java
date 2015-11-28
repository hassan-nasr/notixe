/*
 * FooterPanel.java
 *
 * Created on June 8, 2014, 2:25 PM
 */
 
package com.noktiz.ui.web.header;

import com.noktiz.ui.web.BasePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/** 
 *
 * @author hossein
 * @version 
 */

public final class FooterPanel extends BasePanel {

    public FooterPanel(String id, String text) {
        super(id);
        add(new Label("footerpanel_text", text));
    }

}
