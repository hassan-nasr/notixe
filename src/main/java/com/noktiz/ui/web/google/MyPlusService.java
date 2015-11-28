/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.google;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.plus.Plus;
import java.io.Serializable;

/**
 *
 * @author LABOOOOX
 */
public class MyPlusService extends Plus implements Serializable {

    public MyPlusService(HttpTransport ht, JsonFactory jf, HttpRequestInitializer hri) {
        super(ht, jf, hri);
    }
}
