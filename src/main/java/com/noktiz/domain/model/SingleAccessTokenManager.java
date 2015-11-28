package com.noktiz.domain.model;

import com.noktiz.domain.entity.token.SingleAccessToken;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.persistance.HSF;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;

import java.util.Date;

/**
 * Created by hasan on 9/12/14.
 */
public class SingleAccessTokenManager extends BaseManager {
    public SingleAccessToken loadAccessTokenByToken(String token) throws NonUniqueObjectException{
        Query query = HSF.get().getCurrentSession().getNamedQuery("loadValidAccessToken");
        query.setParameter("token",token);
        query.setTimestamp("expDate",new Date());
        SingleAccessToken singleAccessToken = (SingleAccessToken) query.uniqueResult();
        return singleAccessToken;
    }
    public SingleAccessToken useToken(String token) throws ResultException {
        SingleAccessToken singleAccessToken = loadAccessTokenByToken(token);
        if(singleAccessToken == null || !singleAccessToken.use())
            throw new ResultException(new Result(new Result.Message("message.invalid_access_token", Result.Message.Level.error),false),null);
        HSF.get().getCurrentSession().update(singleAccessToken);
        return singleAccessToken;
    }
    public SingleAccessToken generateAccessToken(User user, SingleAccessToken.Type type,Integer validMinutes,Integer allowedUsedTimes){
        String token = RandomStringUtils.random(60,true,true);

        SingleAccessToken sat = new SingleAccessToken(user,token,type,validMinutes,allowedUsedTimes);

        saveAsNew(sat);

        return sat;
    }
}
