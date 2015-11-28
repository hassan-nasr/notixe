package com.noktiz.domain.entity.endorse;

import com.noktiz.domain.entity.User;
import com.noktiz.domain.entity.notifications.NotificationEndorse;
import com.noktiz.domain.model.BaseManager;
import com.noktiz.domain.model.Result;
import com.noktiz.domain.model.ResultException;
import com.noktiz.domain.model.UserFacade;
import com.noktiz.domain.persistance.HSF;
import org.hibernate.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 * Created by hasan on 2014-10-13.
 */
public class EndorseManager extends BaseManager {

    public Map<String, List<Endorse>> getMappedEndorses(UserFacade user) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("loadAllMyEndorses");
        query.setParameter("receiver", user.getUser());
        List<Endorse> result = query.list();
        Map<String, List<Endorse>> ret = new HashMap<>();
        user.refresh();

        for (String s : user.getUser().getApprovedEndorseContexts()) {
            ret.put(s, new ArrayList<Endorse>());
        }
        for (Endorse endorse : result) {
            List<Endorse> list = ret.get(endorse.getContext());
            if (list == null) {
                continue;
            }
            list.add(endorse);
        }
        return ret;
    }
    /*TODO: make it efficient*/

    public List<Endorse> getUnApprovedEndorsesNotEfficient(User user) {
        Query query = HSF.get().getCurrentSession().getNamedQuery("loadAllMyEndorses");
        query.setParameter("receiver", user);
        List<Endorse> result = query.list();
        List<Endorse> ret = new ArrayList<>();
        for (Endorse endorse : result) {
            if (user.getApprovedEndorseContexts().contains(endorse.getContext())) {
                continue;
            }
            ret.add(endorse);
        }
        return ret;
    }

    public Result addEndorse(Endorse newEndorse) throws ResultException {
        try {
            if (newEndorse.getContext() == null || newEndorse.getContext().isEmpty()) {
                throw new ResultException(new Result(new Result.Message("message.context_not_empty", Result.Message.Level.error), false), null);
            }
            Endorse possibleDuplicate = (Endorse) HSF.get().getCurrentSession().getNamedQuery("findDuplicateEndorse")
                    .setParameter("context", newEndorse.getContext())
                    .setParameter("receiver", newEndorse.getReceiver())
                    .setParameter("sender", newEndorse.getSender()).uniqueResult();
            if (possibleDuplicate == null) {
                saveAsNew(newEndorse);
                if (!newEndorse.getReceiver().equals(newEndorse.getSender())) {
                    NotificationEndorse notificationEndorse = new NotificationEndorse(newEndorse);
                    HSF.get().beginTransaction();
                    try {
                        notificationEndorse.create();
                        HSF.get().commitTransaction();
                    } catch (HibernateException ex) {
                        HSF.get().roleback();
                        Logger.getLogger(this.getClass()).info("HibernateException", ex);
                        return new Result(false);
                    }
                }
            } else {
                if (possibleDuplicate.isDeleted() == false) {
                    throw new ResultException(new Result(new Result.Message("message.already_endorse" , Result.Message.Level.warning, possibleDuplicate.getContext()), false), null);
                }
                possibleDuplicate.setDeleted(false);
                possibleDuplicate.setDate(newEndorse.getDate());
                newEndorse.setId(possibleDuplicate.getId());
                saveAsNew(possibleDuplicate);
            }
            if (newEndorse.getSender().equals(newEndorse.getReceiver())) {
                final UserFacade userFacade = new UserFacade(newEndorse.getSender());
                userFacade.approveEndorseContext(newEndorse);
                userFacade.save();

            }
            return new Result(new Result.Message("message.you_endorse_somebody", Result.Message.Level.success,newEndorse.getReceiver().getName(),newEndorse.getContext()), true);

        } catch (Exception e) {
            if (e instanceof ResultException) {
                throw e;
            }
            throw new ResultException(new Result(new Result.Message("message.can_not_add_endorse", Result.Message.Level.error), false), e);
        }
    }

}
