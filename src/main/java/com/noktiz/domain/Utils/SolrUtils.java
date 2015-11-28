package com.noktiz.domain.Utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hassan on 4/20/2015.
 */
public class SolrUtils {
    static String[] replace = new String[]{"\\", "+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "~", "?", ":", "\"", ";"};
    static String[] to      = new String[]{"\\\\", "\\+", "\\-", "\\&", "\\|", "\\!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\\~", "\\?", "\\:", "\\\"", "\\;" };
    public static String escapeString(String in){
        return StringUtils.replaceEach(in, replace, to);
    }
}