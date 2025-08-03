/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.PublicardocWs;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author VSUser
 */
public class PublicardocWsJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(PublicardocWs publicardocWs) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(publicardocWs);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPublicardocWs(publicardocWs.getFEId()) != null) {
                throw new PreexistingEntityException("PublicardocWs " + publicardocWs + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(PublicardocWs publicardocWs) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            publicardocWs = em.merge(publicardocWs);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = publicardocWs.getFEId();
                if (findPublicardocWs(id) == null) {
                    throw new NonexistentEntityException("The publicardocWs with id " + id + " no longer exists.");
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
            PublicardocWs publicardocWs;
            try {
                publicardocWs = em.getReference(PublicardocWs.class, id);
                publicardocWs.getFEId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The publicardocWs with id " + id + " no longer exists.", enfe);
            }
            em.remove(publicardocWs);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PublicardocWs> findPublicardocWsEntities() {
        return findPublicardocWsEntities(true, -1, -1);
    }

    public static List<PublicardocWs> findPublicardocWsEntities(int maxResults, int firstResult) {
        return findPublicardocWsEntities(false, maxResults, firstResult);
    }

    private static List<PublicardocWs> findPublicardocWsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PublicardocWs.class));
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

    public static PublicardocWs findPublicardocWs(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PublicardocWs.class, id);
        } finally {
            em.close();
        }
    }

    public static int getPublicardocWsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PublicardocWs> rt = cq.from(PublicardocWs.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public static List<PublicardocWs> findHabilitadas(Date fechaPublicacionPortal){
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createNamedQuery("PublicardocWs.findHabilitadas");
            q.setParameter("estadoPublicacion", 'A');
            q.setParameter("fechaPublicacionPortal", fechaPublicacionPortal);
            return q.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
}
