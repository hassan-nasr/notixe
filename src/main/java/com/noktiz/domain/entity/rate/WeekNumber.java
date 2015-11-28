/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.entity.rate;

import com.noktiz.domain.persistance.HSF;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 *
 * @author Hossein
 */
@Entity
public class WeekNumber {
    @Id
    Long id;        
    Long weekNumber;
    public Long getCurrentWeekNumber(){
        WeekNumber load =(WeekNumber) HSF.get().getCurrentSession().load(WeekNumber.class, 1l);
        if(load==null){
            load=new WeekNumber();
            load.setId(1l);
            load.setWeekNumber(1l);
            
            try{
                HSF.get().beginTransaction();
                HSF.get().getCurrentSession().persist(load);
                HSF.get().commitTransaction();
                Logger.getLogger(WeekNumber.class).info("create week number of first week");
            }catch(HibernateException ex){
                Logger.getLogger(WeekNumber.class).fatal("can not save first week number",ex);
            }
        }
        return load.weekNumber;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Long weekNumber) {
        this.weekNumber = weekNumber;
    }

    @Override
    public String toString() {
        return "WeekNumber{" + "id=" + id + ", weekNumber=" + weekNumber + '}';
    }
    
}
