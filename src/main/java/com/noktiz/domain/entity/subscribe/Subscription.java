package com.noktiz.domain.entity.subscribe;

import com.noktiz.domain.entity.BaseObject;
import com.noktiz.domain.entity.User;

import javax.persistence.*;

/**
 * Created by hasan on 2014-11-06.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Subscription extends BaseObject {
    protected Long id;
    protected User user;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne()
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;

        Subscription that = (Subscription) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
