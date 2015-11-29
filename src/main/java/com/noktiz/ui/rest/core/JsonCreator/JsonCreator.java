package com.noktiz.ui.rest.core.JsonCreator;

import java.io.IOException;

/**
 * Created by arsalan on 10/10/15.
 */
public interface JsonCreator<T> {

    String getJson(T object) throws IOException;

}
