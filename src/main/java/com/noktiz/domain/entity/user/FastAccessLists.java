//package com.noktiz.domain.entity.user;
//
//import com.noktiz.domain.entity.BaseObject;
//import com.noktiz.domain.entity.rate.RateContext;
//
//import javax.persistence.*;
//import java.util.List;
//
///**
// * Created by hasan on 2014-11-05.
// */
//
//@Entity
//public class FastAccessLists extends BaseObject{
//    Long id;
//    List<RateContext> hasAccessRateContexts;
//
//    @Override
//    @Id
//    @GeneratedValue
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    @ManyToMany
//    @JoinTable(
//            name="FastAccessList_rateContext",
//            joinColumns = @JoinColumn( name="user_id"),
//            inverseJoinColumns = @JoinColumn( name="rateContextId")
//    )
//    public List<RateContext> getHasAccessRateContexts() {
//        return hasAccessRateContexts;
//    }
//
//    public void setHasAccessRateContexts(List<RateContext> hasAccessRateContexts) {
//        this.hasAccessRateContexts = hasAccessRateContexts;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof FastAccessLists)) return false;
//
//        FastAccessLists that = (FastAccessLists) o;
//
//        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return id != null ? id.hashCode() : 0;
//    }
//
//    @Override
//    public String toString() {
//        return "FastAccessLists{" +
//                "id=" + id +
//                ", hasAccessRateContexts=" + hasAccessRateContexts +
//                '}';
//    }
//}
