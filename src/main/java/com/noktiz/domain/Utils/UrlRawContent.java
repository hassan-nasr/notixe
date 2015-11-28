package com.noktiz.domain.Utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hasan on 8/7/14.
 */
public class UrlRawContent {
    private int responseCode;
    private String responseMessage;
    private ArrayList<Byte> content;

    public UrlRawContent(int responseCode, String responseMessage, ArrayList<Byte> content) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.content = content;
    }
    public UrlRawContent(URL url){
        HttpURLConnection conn;
        BufferedReader rd;
        ArrayList<Byte> temp = new ArrayList<>();
        Logger.getLogger(this.getClass()).debug("getting: " + url);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while(rd.ready()){
                temp.add((byte)rd.read());
            }
            content=temp;
        } catch (IOException e) {
            responseCode = 0;
            responseMessage="IO Exception";
            Logger.getLogger(this.getClass()).error(e);
        }

    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public ArrayList<Byte> getContent() {
        return content;
    }

    public void setContent(ArrayList<Byte> content) {
        this.content = content;
    }
}
