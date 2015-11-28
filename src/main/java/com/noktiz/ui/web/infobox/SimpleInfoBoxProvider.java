package com.noktiz.ui.web.infobox;

import com.noktiz.ui.web.component.infobox.InfoBoxProvider;

/**
 * Created by Hossein on 12/6/2014.
 */
public class SimpleInfoBoxProvider extends InfoBoxProvider{
    private String imageUrl;
    private String titleKey;
    private String TextKey;
    private boolean imageEnable=true;

    public SimpleInfoBoxProvider(String imageUrl, String titleKey, String textKey) {
        this.imageUrl = imageUrl;
        this.titleKey = titleKey;
        TextKey = textKey;

    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getTitleResourceKey() {
        return titleKey;
    }

    @Override
    public String getTextResourceKey() {
        return TextKey;
    }

    public SimpleInfoBoxProvider addAction(IAction action){
        getActions().add(action);
        return this;
    }

    public void setImageEnable(boolean imageEnable) {
        this.imageEnable = imageEnable;
    }

    @Override
    public boolean getImageEnable() {
        return imageEnable;
    }
}
