/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.BdsMaestras;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author VsUser
 */
public class BdsMaestrasJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(BdsMaestras bdsMaestras) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(bdsMaestras);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBdsMaestras(bdsMaestras.getBDId()) != null) {
                throw new PreexistingEntityException("BdsMaestras " + bdsMaestras + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BdsMaestras bdsMaestras) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            bdsMaestras = em.merge(bdsMaestras);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = bdsMaestras.getBDId();
                if (findBdsMaestras(id) == null) {
                    throw new NonexistentEntityException("The bdsMaestras with id " + id + " no longer exists.");
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
            BdsMaestras bdsMaestras;
            try {
                bdsMaestras = em.getReference(BdsMaestras.class, id);
                bdsMaestras.getBDId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bdsMaestras with id " + id + " no longer exists.", enfe);
            }
            em.remove(bdsMaestras);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<BdsMaestras> findBdsMaestrasEntities() {
        return findBdsMaestrasEntities(true, -1, -1);
    }

    public List<BdsMaestras> findBdsMaestrasEntities(int maxResults, int firstResult) {
        return findBdsMaestrasEntities(false, maxResults, firstResult);
    }

    private List<BdsMaestras> findBdsMaestrasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BdsMaestras.class));
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

    public static BdsMaestras findBdsMaestras(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BdsMaestras.class, id);
        } finally {
            em.close();
        }
    }

    public int getBdsMaestrasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BdsMaestras> rt = cq.from(BdsMaestras.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public static List<BdsMaestras> findAllBDAnadidas() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createNamedQuery("BdsMaestras.findAll");
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
