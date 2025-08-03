/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionResumenLinea;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaPK;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
public class TransaccionResumenLineaJC implements Serializable {

    public TransaccionResumenLineaJC(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TransaccionResumenLinea transaccionResumenLinea) throws PreexistingEntityException, Exception {
        if (transaccionResumenLinea.getTransaccionResumenLineaPK() == null) {
            transaccionResumenLinea.setTransaccionResumenLineaPK(new TransaccionResumenLineaPK());
        }
        transaccionResumenLinea.getTransaccionResumenLineaPK().setIdTransaccion(transaccionResumenLinea.getTransaccionResumen().getIdTransaccion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(transaccionResumenLinea);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionResumenLinea(transaccionResumenLinea.getTransaccionResumenLineaPK()) != null) {
                throw new PreexistingEntityException("TransaccionResumenLinea " + transaccionResumenLinea + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TransaccionResumenLinea transaccionResumenLinea) throws NonexistentEntityException, Exception {
        transaccionResumenLinea.getTransaccionResumenLineaPK().setIdTransaccion(transaccionResumenLinea.getTransaccionResumen().getIdTransaccion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            transaccionResumenLinea = em.merge(transaccionResumenLinea);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TransaccionResumenLineaPK id = transaccionResumenLinea.getTransaccionResumenLineaPK();
                if (findTransaccionResumenLinea(id) == null) {
                    throw new NonexistentEntityException("The transaccionResumenLinea with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TransaccionResumenLineaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumenLinea transaccionResumenLinea;
            try {
                transaccionResumenLinea = em.getReference(TransaccionResumenLinea.class, id);
                transaccionResumenLinea.getTransaccionResumenLineaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionResumenLinea with id " + id + " no longer exists.", enfe);
            }
            em.remove(transaccionResumenLinea);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TransaccionResumenLinea> findTransaccionResumenLineaEntities() {
        return findTransaccionResumenLineaEntities(true, -1, -1);
    }

    public List<TransaccionResumenLinea> findTransaccionResumenLineaEntities(int maxResults, int firstResult) {
        return findTransaccionResumenLineaEntities(false, maxResults, firstResult);
    }

    private List<TransaccionResumenLinea> findTransaccionResumenLineaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionResumenLinea.class));
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

    public TransaccionResumenLinea findTransaccionResumenLinea(TransaccionResumenLineaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionResumenLinea.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionResumenLineaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionResumenLinea> rt = cq.from(TransaccionResumenLinea.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
