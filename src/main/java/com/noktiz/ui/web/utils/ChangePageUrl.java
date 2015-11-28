package com.noktiz.ui.web.utils;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hasan on 2014-12-10.
 */
public class ChangePageUrl  {
    public static String getScript(String s) {

//        String[] split = url.split("\\?");
//        if (split.length != 0 && false) {
//            String[] parts = split[0].split("/");
//            ArrayList partList= new ArrayList(Arrays.asList(parts));
//
//            String []newParts = s.split("/");
//            for(int i=0;i<newParts.length;i++){
//                if(newParts[i].equals("..") && partList.size()>0)
//                    partList.remove(partList.size()-1);
//                else{
//                    if(!newParts[i].equals("..")){
//                        partList.add(newParts[i]);
//                    }
//                }
//            }
//            StringBuilder newAddrBuilder = new StringBuilder();
//            for (String part : parts) {
//                newAddrBuilder.append(part).append("/");
//            }
//            newAddr= newAddrBuilder.toString();
//        } else {
//            newAddr =s;
//        }
        s=s.replaceAll("//","/");
        s=s.replaceAll("//","/");
        String script = String.format("history.pushState({ foo: \"bar\" }, \"page \", \"%s\");", s);
        return script;
    }
}
