package com.noktiz.domain.entity.user;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.persistance.HSF;
import com.noktiz.ui.web.auth.UserRoles;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Hossein on 3/29/2015.
 */
@Entity
@NamedQueries(
        @NamedQuery(name="Role.LoadByName",query="from Role as r where r.name=:name")
)
public class Role extends BaseObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private static HashMap<UserRoles.RoleEnum,Role> roleMap = new HashMap<>();
    public Role(String name) {
        this.name = name;
    }

    public Role() {
    }

    public static Role getRole(UserRoles.RoleEnum roleEnum){
        Role role = roleMap.get(roleEnum);
        if(role!=null){
            return role;
        }
        Query query = HSF.get().getCurrentSession().getNamedQuery("Role.LoadByName");
        query.setString("name",roleEnum.toString());
        role = (Role) query.uniqueResult();
        if(role!=null){
            roleMap.put(roleEnum,role);
        }
        return role;
    }
    public static void createRoles(){
        for (UserRoles.RoleEnum roleEnum : UserRoles.RoleEnum.values()) {
            if(getRole(roleEnum)==null){
                saveRole(roleEnum);
            }
        }
    }

    private static void saveRole(UserRoles.RoleEnum roleEnum) {
        Role role = new Role(roleEnum.toString());
        HSF.get().beginTransaction();
        try{
            HSF.get().getCurrentSession().persist(role);
        }catch (HibernateException ex){
            Logger.getLogger(Role.class).error("can not save new role",ex);
            HSF.get().roleback();
        }
        finally {
            HSF.get().commitTransaction();
        }
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Role)) {
            return false;
        }
        final Role other = (Role) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
