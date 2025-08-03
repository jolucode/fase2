/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.LogTrans;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

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

    public static void edit(LogTrans logTrans) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            logTrans = em.merge(logTrans);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = logTrans.getKeylog();
                if (findLogTrans(id) == null) {
                    throw new NonexistentEntityException("The logTrans with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LogTrans logTrans;
            try {
                logTrans = em.getReference(LogTrans.class, id);
                logTrans.getKeylog();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The logTrans with id " + id + " no longer exists.", enfe);
            }
            em.remove(logTrans);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<LogTrans> findLogTransEntities() {
        return findLogTransEntities(true, -1, -1);
    }

    public static List<LogTrans> findLogTransEntities(int maxResults, int firstResult) {
        return findLogTransEntities(false, maxResults, firstResult);
    }

    private static List<LogTrans> findLogTransEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LogTrans.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
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

    public static int getLogTransCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LogTrans> rt = cq.from(LogTrans.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
