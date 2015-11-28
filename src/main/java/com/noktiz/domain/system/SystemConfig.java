package com.noktiz.domain.system;

import com.noktiz.domain.entity.BaseObject;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasan on 8/7/14.
 */
@Entity
public class SystemConfig extends BaseObject {
    private Long id;


    public static final String App_Name="App_Name";
    private Map<String, String> properties = new HashMap<>();

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SystemConfig{" +
                "id=" + id +
                ", property=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemConfig)) return false;

        SystemConfig that = (SystemConfig) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="prop_name")
    @Column(name="prop_value")
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> property) {
        this.properties= property;
    }

    @Transient
    public String getProperty(String key){
        if(properties == null)
            return null;
        return properties.get(key);
    }

    @Transient
    public String getAppName() {
        return properties.get(App_Name);
    }

    public Long getPropertyAsLong(String id) {
        return Long.parseLong(getProperty(id));
    }
    public static String FacebookFriendLoadPeriodInMin = "FacebookFriendLoadPeriodInMin";
    public static String GoogleFriendLoadPeriodInMin = "GoogleFriendLoadPeriodInMin";

    public Long getPropertyAsLong(String id, Long default1) {
        try {
            return Long.parseLong(getProperty(id));
        } catch (Exception e) {
            return default1;
        }

    }
}
