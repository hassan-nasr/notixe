package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.Localized;

import javax.persistence.*;

/**
 * Created by hassan on 3/30/2015.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "loadAvailableQuestionLanguages", query = "from QuestionLanguage where active=true ")
})
public class QuestionLanguage extends BaseObject implements Localized {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String country;
    Boolean active;

    public QuestionLanguage(String lang, String country) {
        this.name = lang;
        this.country = country;
        active=true;
    }

    public QuestionLanguage() {
    }

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

        QuestionLanguage that = (QuestionLanguage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name+"-"+country;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String getKey() {
        return "Language."+getName()+(getCountry().isEmpty()?"":"."+getCountry());
    }
}
