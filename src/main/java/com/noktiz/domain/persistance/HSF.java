/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.persistance;

import java.util.HashMap;
import java.util.Map;

import com.noktiz.ui.web.lang.Language;
import com.noktiz.ui.web.lang.LocalizationUtils;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Hinernate Session Factory
 *
 * @author hossein
 */
public class HSF extends AbstractRequestCycleListener {

    private static Map<Thread, RequestCycle> requestCycles = new HashMap();
    private static SessionFactory sf;
    private static boolean offline;
    final MetaDataKey<SessionContainer> sessionMetaData = new MetaDataKey<SessionContainer>() {
    };
//    private Transaction transaction;

    static Session staticSession;
    private SessionContainer offlineSC;

    public Session getCurrentSession() {
        if(getRC() == null){
            if(staticSession ==null){
                staticSession = sf.openSession();
            }
            return staticSession;
        }
        SessionContainer metaData = getRC().getMetaData(sessionMetaData);
        if(!metaData.getSession().isOpen())
            getRC().setMetaData(sessionMetaData,new SessionContainer(sf.openSession()));
        return metaData.getSession();
    }

    public void beginTransaction() {
        SessionContainer sc;
        sc = getSC();
        if (sc.getTrancCount() == 0) {
            final Session currentSession = getCurrentSession();
            currentSession.beginTransaction();
        }
        sc.increaseTrancCount();
    }

    private SessionContainer getSC() {
        if(offline){
            if(offlineSC == null)
                offlineSC = new SessionContainer();
            return offlineSC;
        }
        return getRC().getMetaData(sessionMetaData);
    }

    public void commitTransaction() {
        if(getSC().isRolledback()){
//            getSC().decreaseTrancCount();
            throw new HibernateException("exception for chaining");
        }
        if (getSC().getTrancCount() == 1) {
            final Transaction transaction = getCurrentSession().getTransaction();
            if (!transaction.wasRolledBack() && transaction.isActive()) {
                try {
                    transaction.commit();
                } catch (HibernateException e) {
                    transaction.rollback();
                    throw e;
                }
            }
        }
        getSC().decreaseTrancCount();
        if (getSC().getTrancCount() < 0) {
            throw new RuntimeException("no transaction has been opened");
        }
    }
    static HSF inst;

    public static void configure(SessionFactory sf) {
        HSF.sf = sf;
        HSF.offline = false;
    }
    public static void configure(SessionFactory sf, boolean offline) {
        HSF.sf = sf;
        HSF.offline = offline;
    }

    public static HSF get() {
        if (inst == null) {
            inst = new HSF();
        }
        return inst;
    }

    private HSF() {
    }

    @Override
    public void onBeginRequest(RequestCycle cycle) {
        super.onBeginRequest(cycle);
        if (getRC() == null) {
            requestCycles.put(Thread.currentThread(), cycle);
        }
        cycle.setMetaData(LocalizationUtils.LanguageMetadata, Language.eng);
        final Session session = sf.openSession();
        cycle.setMetaData(sessionMetaData, new SessionContainer(session));
    }

    @Override
    public void onEndRequest(RequestCycle cycle) {
        super.onEndRequest(cycle);
        Session session = cycle.getMetaData(sessionMetaData).getSession();
//        session.getTransaction().commit();
        try {
            if (getSC().getTrancCount() != 0) {
                throw new RuntimeException("not all transacton are commited");
            }
        } finally {
            session.clear();
            session.close();
        }
        removeRE();
    }
    public boolean isRolledBack(){
        return getSC().isRolledback();
    }
    public void roleback() {
        getSC().setRolledback(true);
        if (getSC().getTrancCount() == 1) {
            final Transaction transaction = getCurrentSession().getTransaction();
            if (!transaction.wasRolledBack() && transaction.isActive()) {
                transaction.rollback();
            }
        }
        getSC().decreaseTrancCount();
        if (getSC().getTrancCount() < 0) {
            throw new RuntimeException("no transaction has been opened");
        }
    }

    private RequestCycle getRC() {
        final RequestCycle get = RequestCycle.get();
        if (get != null) {
            return get;
        }
        return requestCycles.get(Thread.currentThread());
    }

    private void removeRE() {
        requestCycles.remove(Thread.currentThread());
    }
}
