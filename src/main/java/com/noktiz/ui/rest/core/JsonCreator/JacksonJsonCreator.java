package com.noktiz.ui.rest.core.JsonCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;

import java.io.IOException;

//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.SerializationConfig;

/**
 * Created by arsalan on 10/10/15.
 */
public class JacksonJsonCreator implements JsonCreator {

    @Override
    public String getJson(Object object) {

        ObjectMapper objMapper=new ObjectMapper();
        objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        try {
            return objMapper.writeValueAsString(object);
        } catch (IOException e) {
            Logger.getLogger(this.getClass()).error("unable to create json", e);
            throw  new RuntimeException(e);
        }
    }
}
