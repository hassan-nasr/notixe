package com.noktiz.domain.i18n;

import com.noktiz.domain.entity.BaseObject;

import javax.persistence.*;
import java.util.Locale;

/**
 * Created by hassan on 3/27/2015.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "loadActiveLocals", query = "from Local where active=true")
})
public class Local extends BaseObject{
    private String Name;
    private String localId;
    private Long id;
    private Boolean active;
    private String language;
    private String country;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Local local = (Local) o;

        if (id != null ? !id.equals(local.id) : local.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Local{" +
                "Name='" + Name + '\'' +
                ", localId='" + localId + '\'' +
                ", id=" + id +
                '}';
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Transient
    public Locale getLocale() {
        if(language == null)
            return new Locale("en");
        return new Locale(language, country);
    }
}
