package com.noktiz.domain.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by hasan on 7/21/14.
 */
public class Utils {
    public static String getHTML(URL url) throws IOException {
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        rd.close();

        return result;
    }
}
