/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noktiz.domain.model;

import com.noktiz.domain.entity.Block;
import com.noktiz.domain.entity.Message;
import com.noktiz.domain.entity.Thread;
import com.noktiz.domain.persistance.HSF;
import java.io.Serializable;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;

/**
 *
 * @author hossein
 */
public class BlockFacade implements Serializable {

//    static Result updateState(Block block) {
////        Session cs = HibernateSessionFactory.get().getCurrentSession();
//        Block load = Block.loadBlockedById(block.getId());
//        if (load.getThreads() == null || load.getThreads().isEmpty()) {
//
//            HSF.get().beginTransaction();
//            try {
//                for (Thread thread : load.getBlockedThread()) {
//                    thread.setBlockedBy(null);
//                    thread.setBlockedcompletely(null);
//                    thread.save();
//                    for (Message message : thread.getMessages()) {
//                        message.setBlock(null);
//                        message.save();
//}
//                }
//                load.delete();
//                HSF.get().commitTransaction();
//            } catch (HibernateException ex) {
//                HSF.get().roleback();
//                Logger.getLogger(BlockFacade.class).info("HibernateException", ex);
//                return new Result(false);
//            }
//        }
//        return new Result(true);
//    }
}
