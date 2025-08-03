/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.ReglasIdiomasDoc;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;

/**
 * @author VSUser
 */
public class ReglasIdiomasDocJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(ReglasIdiomasDoc reglasIdiomasDoc) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(reglasIdiomasDoc);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findReglasIdiomasDoc(reglasIdiomasDoc.getFEKey()) != null) {
                throw new PreexistingEntityException("ReglasIdiomasDoc " + reglasIdiomasDoc + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(ReglasIdiomasDoc reglasIdiomasDoc) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            reglasIdiomasDoc = em.merge(reglasIdiomasDoc);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reglasIdiomasDoc.getFEKey();
                if (findReglasIdiomasDoc(id) == null) {
                    throw new NonexistentEntityException("The reglasIdiomasDoc with id " + id + " no longer exists.");
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
            ReglasIdiomasDoc reglasIdiomasDoc;
            try {
                reglasIdiomasDoc = em.getReference(ReglasIdiomasDoc.class, id);
                reglasIdiomasDoc.getFEKey();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reglasIdiomasDoc with id " + id + " no longer exists.", enfe);
            }
            em.remove(reglasIdiomasDoc);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroyAllReglas() throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.clear();
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<ReglasIdiomasDoc> findReglasIdiomasDocEntities() {
        return findReglasIdiomasDocEntities(true, -1, -1);
    }

    public static List<ReglasIdiomasDoc> findReglasIdiomasDocEntities(int maxResults, int firstResult) {
        return findReglasIdiomasDocEntities(false, maxResults, firstResult);
    }

    private static List<ReglasIdiomasDoc> findReglasIdiomasDocEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ReglasIdiomasDoc.class));
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

    public static ReglasIdiomasDoc findReglasIdiomasDoc(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ReglasIdiomasDoc.class, id);
        } finally {
            em.close();
        }
    }

    public static List<ReglasIdiomasDoc> findAllReglas() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createNamedQuery("ReglasIdiomasDoc.findAllReglas");
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
