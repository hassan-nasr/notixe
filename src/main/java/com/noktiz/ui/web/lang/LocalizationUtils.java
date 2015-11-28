package com.noktiz.ui.web.lang;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;

public class LocalizationUtils
{
    public static final MetaDataKey<Language> LanguageMetadata = new MetaDataKey() {};
    private static final LocalizationUtils instance = new LocalizationUtils();

    public static LocalizationUtils get()
    {
        return instance;
    }

    private LocalizationUtils() {}

    public Boolean isRTL()
    {
        Language lang = getLang();
        return lang != null ? lang.isRTL() : Boolean.TRUE;
    }

    public Language getLang()
    {
        return (Language)RequestCycle.get().getMetaData(LanguageMetadata);
    }

    public void setLang(Language lang)
    {
        RequestCycle.get().setMetaData(LanguageMetadata, lang);
    }
}
