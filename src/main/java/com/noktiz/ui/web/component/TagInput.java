package com.noktiz.ui.web.component;

import com.noktiz.ui.web.Application;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Created by hasan on 2014-12-11.
 */
public class TagInput extends Panel{
    private final String addText;
    //    IModel<String> content;
    TextField field;
    int height=100;
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "assets/plugins/jquery-tags-input/jquery.tagsinput.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "assets/plugins/jquery-tags-input/jquery.tagsinput.js")));
        response.render(new OnDomReadyHeaderItem(String.format("$('#"+field.getMarkupId()+"').tagsInput({width: 'auto'," +
                "      defaultText:'"+addText+"',\n" +
                "      minChars:0,\n" +
                "      height:'"+height+"px',\n" +
                "      autocomplete: {selectFirst: false },\n" +
                "      'hide':true,\n" +
                "      'delimiter':' ',\n" +
                "      'unique':true,\n" +
                "      removeWithBackspace:true,\n" +
                "      placeholderColor:'#5bc0de',\n" +
                "      autosize: true,\n" +
                "      comfortZone: 100,\n" +
                "      inputPadding: 6*2});",field.getMarkupId())));
    }

    public TagInput(String id,String addText, final IModel<String> content1) {
        super(id,content1);
        this.addText = addText;
//        if(content1 == null)
//        this.content = content1;
        field = new TextField<>("tagInput",content1);
        add(field);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
