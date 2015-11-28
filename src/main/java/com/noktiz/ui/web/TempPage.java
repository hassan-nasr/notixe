/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web;

import com.noktiz.ui.web.friend.fb.FBPersonList;

/**
 *
 * @author hossein
 */
public final class TempPage extends BaseUserPage {

    public TempPage() {
        super("temp");
        FBPersonList vt= new FBPersonList("thread");
        add(vt);
    }
}
