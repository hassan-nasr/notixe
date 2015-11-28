package com.noktiz.ui.web.lang;

public enum Language
{
    fa(Boolean.valueOf(true)),  eng(Boolean.valueOf(false));

    private Boolean isRTL;

    private Language(Boolean isRTL)
    {
        this.isRTL = isRTL;
    }

    public Boolean isRTL()
    {
        return this.isRTL;
    }

}
