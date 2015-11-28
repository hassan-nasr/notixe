package com.noktiz.domain.entity;

import com.noktiz.domain.entity.social.SocialConnection;
import com.noktiz.domain.persistance.HSF;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Created by hasan on 7/18/14.
 */


@MappedSuperclass
//@FilterDef(name="deleteState", parameters=@ParamDef( name="deleteState", type="boolean" ) ,defaultCondition = "false")
//@Filters( {
//        @Filter(name="loadNotDeleted", condition=":{alias}.deleted = 0",deduceAliasInjectionPoints = false),
//        @Filter(name="loadAll", condition="")
//} )
public abstract class BaseObject<T> implements Serializable {
    @Transient
    public abstract Long getId();

    private boolean deleted=false;

    @Version
    private Long version=1l;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public abstract String toString() ;

    @Override
    public abstract boolean equals(Object obj) ;

    @Override
    public abstract int hashCode() ;

    public T refresh(){
        T ans=(T)this;
        try {
            if (!getCurrentSession().contains(this)) {
                try {
                    if (this.getId() == null) {
                        return (T) this;
                    }
                }catch (Exception  e){
                    return null;
                }
                ans=(T) getCurrentSession().merge(this);
            }
            return ans;
        } catch (StaleObjectStateException ex) {
//            boolean contains = HSF.get().getCurrentSession().contains(user);
             return (T)getCurrentSession().load(this.getClass(),this.getId());
//            boolean contains1 = HSF.get().getCurrentSession().contains(user);
        }
    };
    public static Session getCurrentSession() {
        return HSF.get().getCurrentSession();
    }
}
