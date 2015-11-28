package com.noktiz.domain.i18n;

import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.persistance.HSF;

import javax.persistence.NamedQuery;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by hassan on 3/27/2015.
 */
public class LocalManager extends BaseManager {
    public List<Local> loadActiveLocals(){
        org.hibernate.Query query = HSF.get().getCurrentSession().getNamedQuery("loadActiveLocals");
        return query.list();
    }
}
