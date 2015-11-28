/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.noktiz.ui.web.thread;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.ThreadFacade;
import com.noktiz.domain.model.ThreadFactory;
import com.noktiz.domain.model.UserFacade;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hossein
 */
public abstract class ThreadProvider implements IDataProvider<ThreadFacade>{
    UserFacade user;
    public ThreadProvider(UserFacade user) {
        this.user=user;
    }

    
    @Override
    public Iterator<? extends ThreadFacade> iterator(long first, long count) {
        List<ThreadFacade> myThreadList = getThreadFacade( first, count);
        return myThreadList.iterator();
    }

    protected abstract List<ThreadFacade> getThreadFacade(long first, long count);

    @Override
    public long size() {
        return user.numOfThreads();
    }

    @Override
    public IModel<ThreadFacade> model(ThreadFacade t) {
        return new Model<ThreadFacade>(t);
    }

    @Override
    public void detach() {
        
    }
    
}
