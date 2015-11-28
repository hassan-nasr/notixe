/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.ui.web.behavior;

import com.noktiz.ui.web.component.interfaces.ISearchable;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Hossein
 */
public class SearchBehavior extends Behavior{
    private ISearchable iSearchable;

    public SearchBehavior(ISearchable personList) {
        this.iSearchable = personList;
    }

    
    @Override
    public void beforeRender(Component component) {
        super.beforeRender(component); //To change body of generated methods, choose Tools | Templates.
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response); //To change body of generated methods, choose Tools | Templates.
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(SearchBehavior.class, "SearchBehavior.js")));
        String searchText=component.getMarkupId();
        String searchContent= iSearchable.getSearchContentClass();
        String searchElement= iSearchable.getSearchElementClass();
        StringBuilder sb= new StringBuilder();
        sb.append("enableSearchFor('").append(searchText).append("','").append(searchContent).append("','").append(searchElement).append("');");
        response.render(new OnDomReadyHeaderItem(sb.toString()));
    }
    
    
    
}
