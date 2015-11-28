package com.noktiz.domain.model;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.persistance.HSF;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 * Created by hasan on 9/13/14.
 */
public class BaseManager implements Serializable {


    public Session getCurrentSession() {
        return HSF.get().getCurrentSession();
    }

    public static void setDateString(Query namedQuery, String name, Date date) {
        String beforeDateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(date);
        namedQuery.setString(name, beforeDateString);
    }

    public ResultWithObject<Serializable> saveAsNew(BaseObject sat) {
        HSF.get().beginTransaction();
        ResultWithObject<Serializable> ans= new ResultWithObject<>(Boolean.TRUE);
        try {
            Serializable ret = getCurrentSession().save(sat);
            HSF.get().commitTransaction();
            ans.object=ret;
            return ans;
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            ans.result=false;
            return ans;
        }
    }

    public ResultWithObject update(BaseObject sat) {
        HSF.get().beginTransaction();
        try{
            getCurrentSession().saveOrUpdate(sat);
            HSF.get().commitTransaction();
        return new ResultWithObject(new Result(true),sat);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject(false);
        }
    }
    public ResultWithObject merge(BaseObject sat) {
        HSF.get().beginTransaction();
        try{
            Object merged = getCurrentSession().merge(sat);
            HSF.get().commitTransaction();
        return new ResultWithObject(new Result(true),merged);
        } catch (HibernateException ex) {
            HSF.get().roleback();
            Logger.getLogger(this.getClass()).info("HibernateException", ex);
            return new ResultWithObject(false);
        }
    }

    public void remove(BaseObject object) {
        object.setDeleted(true);
        update(object);
    }

    public BaseObject attach(BaseObject obj) {
        try {
            if (!getCurrentSession().contains(obj)) {
                obj = (BaseObject) getCurrentSession().merge(obj);
            }
            return obj;
        } catch (HibernateException ex) {
//            boolean contains = HSF.get().getCurrentSession().contains(user);
            obj =(BaseObject) getCurrentSession().load(obj.getClass(), obj.getId());
//            boolean contains1 = HSF.get().getCurrentSession().contains(user);
            return obj;
        }
    }
}
