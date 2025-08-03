/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.LogTrans;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 *
 * @author VSUser
 */
public class LogTransJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(LogTrans logTrans) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(logTrans);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLogTrans(logTrans.getKeylog()) != null) {
                throw new PreexistingEntityException("LogTrans " + logTrans + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static LogTrans findLogTrans(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LogTrans.class, id);
        } finally {
            em.close();
        }
    }

}
