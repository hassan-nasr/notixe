package com.noktiz.domain.entity.rate;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.persistance.HSF;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import javax.persistence.*;
import java.util.*;

/**
 * Created by hasan on 2014-11-03.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "loadActivePredefinedQuestions",query = "From PredefinedQuestion where active=true"),
        @NamedQuery(name = "loadPredefinedQuestions",query = "From PredefinedQuestion")
})
public class PredefinedQuestion extends BaseObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field
    Long id;
    @Field
    String description;
    @Field
    String category;
    @ManyToOne
    @Field
    QuestionLanguage language;
    @Field
    private String title;
    @Field
    boolean active = true;
    @Field
    @ManyToOne(fetch = FetchType.LAZY)
    User publisher;

    Date publishDate;

    Boolean anonymous = false;

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = StringUtils.abbreviate(category, 255);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PredefinedQuestion)) return false;

        PredefinedQuestion that = (PredefinedQuestion) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PredefinedQuestions{" +
                "id=" + id +
                ", question='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public static Map<String,List<PredefinedQuestion>> loadAll(){
        Map<String,List<PredefinedQuestion>> ret = new HashMap<>();
        List<PredefinedQuestion> activeQuestions = HSF.get().getCurrentSession().getNamedQuery("loadActivePredefinedQuestions").list();
        for (PredefinedQuestion activeQuestion : activeQuestions) {
            if(!ret.containsKey(activeQuestion.category))
                ret.put(activeQuestion.category,new ArrayList<PredefinedQuestion>());
            ret.get(activeQuestion.category).add(activeQuestion);
        }
        return ret;
    }

    public QuestionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(QuestionLanguage language) {
        this.language = language;
    }
}
