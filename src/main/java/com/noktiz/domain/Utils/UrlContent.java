package com.noktiz.domain.Utils;

import com.noktiz.domain.system.SystemConfig;
import com.noktiz.domain.system.SystemConfigManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by hasan on 8/7/14.
 */
public class UrlContent {
    private int responseCode;
    private String responseMessage;
    private String content;

    public UrlContent(int responseCode, String responseMessage, String content) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.content = content;
    }
    public UrlContent(URL url){
        this(url, getDefaultProxyAddress());
    }

    public static Proxy getDefaultProxyAddress() {
        String proxyAddress = SystemConfigManager.getCurrentConfig().getProperty("proxyAddress");
        if(proxyAddress == null || proxyAddress.isEmpty())
            return null;
        int proxyPort = Integer.parseInt(SystemConfigManager.getCurrentConfig().getProperty("proxyPort"));

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress,proxyPort));
    }

    public UrlContent(URL url, Proxy proxy){
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        Logger.getLogger(this.getClass()).debug("getting: " + url);
        try {
            if(proxy!=null)
                conn = (HttpURLConnection) url.openConnection(proxy);
            else
                conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();

            content = result;
        } catch (IOException e) {
            responseCode = 0;
            responseMessage="IO Exception";
            content = e.getMessage();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
