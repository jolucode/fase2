/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionError;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * @author Percy
 */
public class TransaccionErrorJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(TransaccionError transaccionError) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(transaccionError);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionError(transaccionError.getFEId()) != null) {
                throw new PreexistingEntityException("TransaccionError " + transaccionError + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(TransaccionError transaccionError) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            transaccionError = em.merge(transaccionError);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = transaccionError.getFEId();
                if (findTransaccionError(id) == null) {
                    throw new NonexistentEntityException("The transaccionError with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionError transaccionError;
            try {
                transaccionError = em.getReference(TransaccionError.class, id);
                transaccionError.getFEId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionError with id " + id + " no longer exists.", enfe);
            }
            em.remove(transaccionError);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<TransaccionError> findTransaccionErrorEntities() {
        return findTransaccionErrorEntities(true, -1, -1);
    }

    public static List<TransaccionError> findTransaccionErrorEntities(int maxResults, int firstResult) {
        return findTransaccionErrorEntities(false, maxResults, firstResult);
    }

    private static List<TransaccionError> findTransaccionErrorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionError.class));
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

    public static TransaccionError findTransaccionError(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionError.class, id);
        } finally {
            em.close();
        }
    }

    public static int getTransaccionErrorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionError> rt = cq.from(TransaccionError.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
