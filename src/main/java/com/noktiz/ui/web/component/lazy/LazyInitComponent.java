package com.noktiz.ui.web.component.lazy;

/**
 * Created by hasan on 2014-12-26.
 */
public interface LazyInitComponent {
    public void init();

    public default boolean canReInit(){
        return false;
    }
}
