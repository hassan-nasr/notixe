package com.noktiz.ui.web.component.typeAhead;

//import ir.mersad.utils.enums.Lang;
import java.io.Serializable;

public class Options
        implements Serializable
{
    private Integer inputLengthForCache;
    private Integer numberOfItems = Integer.valueOf(7);
//    private Lang lang = Lang.Eng;

    public Options(Integer inputLengthForCache)
    {
        this.inputLengthForCache = inputLengthForCache;
    }

    public Options()
    {
        this(Integer.valueOf(1));
    }

    public Integer getInputLengthForCache()
    {
        return this.inputLengthForCache;
    }

    public Options setInputLengthForCache(Integer inputLengthForCache)
    {
        this.inputLengthForCache = inputLengthForCache;
        return this;
    }

    public Integer getNumberOfItems()
    {
        return this.numberOfItems;
    }

    public Options setNumberOfItems(Integer numberOfItems)
    {
        this.numberOfItems = numberOfItems;
        return this;
    }

//    public Lang getLang()
//    {
//        return this.lang;
//    }

//    public Options setLang(Lang lang)
//    {
//        this.lang = lang;
//        return this;
//    }
}
