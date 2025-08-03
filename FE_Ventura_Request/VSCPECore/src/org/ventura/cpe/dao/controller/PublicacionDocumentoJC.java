/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.PublicacionDocumento;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * @author VSUser
 */
public class PublicacionDocumentoJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(PublicacionDocumento publicacionDocumento) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(publicacionDocumento);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPublicacionDocumento(publicacionDocumento.getFEId()) != null) {
                throw new PreexistingEntityException("PublicacionDocumento " + publicacionDocumento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(PublicacionDocumento publicacionDocumento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            publicacionDocumento = em.merge(publicacionDocumento);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = publicacionDocumento.getFEId();
                if (findPublicacionDocumento(id) == null) {
                    throw new NonexistentEntityException("The publicacionDocumento with id " + id + " no longer exists.");
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
            PublicacionDocumento publicacionDocumento;
            try {
                publicacionDocumento = em.getReference(PublicacionDocumento.class, id);
                publicacionDocumento.getFEId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The publicacionDocumento with id " + id + " no longer exists.", enfe);
            }
            em.remove(publicacionDocumento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<PublicacionDocumento> findPublicacionDocumentoEntities() {
        return findPublicacionDocumentoEntities(true, -1, -1);
    }

    public static List<PublicacionDocumento> findPublicacionDocumentoEntities(int maxResults, int firstResult) {
        return findPublicacionDocumentoEntities(false, maxResults, firstResult);
    }

    private static List<PublicacionDocumento> findPublicacionDocumentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PublicacionDocumento.class));
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

    public static PublicacionDocumento findPublicacionDocumento(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PublicacionDocumento.class, id);
        } finally {
            em.close();
        }
    }

    public static int getPublicacionDocumentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PublicacionDocumento> rt = cq.from(PublicacionDocumento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static List<PublicacionDocumento> findPendientes() {
        EntityManager em = getEntityManager();

        try {

            Query q = em.createNamedQuery("PublicacionDocumento.findAll");

            return q.getResultList();
        } finally {
            em.close();
        }
    }

}
