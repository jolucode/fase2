/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionResumen;

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
public class TransaccionResumenJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(TransaccionResumen transaccionResumen) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(transaccionResumen);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionResumen(transaccionResumen.getIdTransaccion()) != null) {
                throw new PreexistingEntityException("TransaccionResumen " + transaccionResumen + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(TransaccionResumen transaccionResumen) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            transaccionResumen = em.merge(transaccionResumen);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = transaccionResumen.getIdTransaccion();
                if (findTransaccionResumen(id) == null) {
                    throw new NonexistentEntityException("The transaccionResumen with id " + id + " no longer exists.");
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
            TransaccionResumen transaccionResumen;
            try {
                transaccionResumen = em.getReference(TransaccionResumen.class, id);
                transaccionResumen.getIdTransaccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionResumen with id " + id + " no longer exists.", enfe);
            }
            em.remove(transaccionResumen);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<TransaccionResumen> findTransaccionResumenEntities() {
        return findTransaccionResumenEntities(true, -1, -1);
    }

    public static List<TransaccionResumen> findTransaccionResumenEntities(int maxResults, int firstResult) {
        return findTransaccionResumenEntities(false, maxResults, firstResult);
    }

    private static List<TransaccionResumen> findTransaccionResumenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionResumen.class));
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

    public static TransaccionResumen findTransaccionResumen(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionResumen.class, id);
        } finally {
            em.close();
        }
    }

    public static int getTransaccionResumenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionResumen> rt = cq.from(TransaccionResumen.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static List<TransaccionResumen> findPendientes() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("TransaccionResumen.findPendiente");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static List<TransaccionResumen> findPendientesConsulta() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("TransaccionResumen.findAllConsulta");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    
    
}
