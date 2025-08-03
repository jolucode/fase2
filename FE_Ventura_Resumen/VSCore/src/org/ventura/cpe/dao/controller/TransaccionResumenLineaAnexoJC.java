/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.IllegalOrphanException;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.controller.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexo;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexoPK;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author VS-LT-06
 */
public class TransaccionResumenLineaAnexoJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(TransaccionResumenLineaAnexo transaccionResumenLineaAnexo) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK() == null) {
            transaccionResumenLineaAnexo.setTransaccionResumenLineaAnexoPK(new TransaccionResumenLineaAnexoPK());
        }
        transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK().setIdTransaccion(transaccionResumenLineaAnexo.getTransaccionResumen().getIdTransaccion());
        List<String> illegalOrphanMessages = null;
        TransaccionResumen transaccionResumenOrphanCheck = transaccionResumenLineaAnexo.getTransaccionResumen();
        if (transaccionResumenOrphanCheck != null) {
            TransaccionResumenLineaAnexo oldTransaccionResumenLineaAnexoOfTransaccionResumen = transaccionResumenOrphanCheck.getTransaccionResumenLineaAnexo();
            if (oldTransaccionResumenLineaAnexoOfTransaccionResumen != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The TransaccionResumen " + transaccionResumenOrphanCheck + " already has an item of type TransaccionResumenLineaAnexo whose transaccionResumen column cannot be null. Please make another selection for the transaccionResumen field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumen transaccionResumen = transaccionResumenLineaAnexo.getTransaccionResumen();
            if (transaccionResumen != null) {
                transaccionResumen = em.getReference(transaccionResumen.getClass(), transaccionResumen.getIdTransaccion());
                transaccionResumenLineaAnexo.setTransaccionResumen(transaccionResumen);
            }
            em.persist(transaccionResumenLineaAnexo);
            if (transaccionResumen != null) {
                transaccionResumen.setTransaccionResumenLineaAnexo(transaccionResumenLineaAnexo);
                transaccionResumen = em.merge(transaccionResumen);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionResumenLineaAnexo(transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK()) != null) {
                throw new PreexistingEntityException("TransaccionResumenLineaAnexo " + transaccionResumenLineaAnexo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(TransaccionResumenLineaAnexo transaccionResumenLineaAnexo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK().setIdTransaccion(transaccionResumenLineaAnexo.getTransaccionResumen().getIdTransaccion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumenLineaAnexo persistentTransaccionResumenLineaAnexo = em.find(TransaccionResumenLineaAnexo.class, transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK());
            TransaccionResumen transaccionResumenOld = persistentTransaccionResumenLineaAnexo.getTransaccionResumen();
            TransaccionResumen transaccionResumenNew = transaccionResumenLineaAnexo.getTransaccionResumen();
            List<String> illegalOrphanMessages = null;
            if (transaccionResumenNew != null && !transaccionResumenNew.equals(transaccionResumenOld)) {
                TransaccionResumenLineaAnexo oldTransaccionResumenLineaAnexoOfTransaccionResumen = transaccionResumenNew.getTransaccionResumenLineaAnexo();
                if (oldTransaccionResumenLineaAnexoOfTransaccionResumen != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The TransaccionResumen " + transaccionResumenNew + " already has an item of type TransaccionResumenLineaAnexo whose transaccionResumen column cannot be null. Please make another selection for the transaccionResumen field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (transaccionResumenNew != null) {
                transaccionResumenNew = em.getReference(transaccionResumenNew.getClass(), transaccionResumenNew.getIdTransaccion());
                transaccionResumenLineaAnexo.setTransaccionResumen(transaccionResumenNew);
            }
            transaccionResumenLineaAnexo = em.merge(transaccionResumenLineaAnexo);
            if (transaccionResumenOld != null && !transaccionResumenOld.equals(transaccionResumenNew)) {
                transaccionResumenOld.setTransaccionResumenLineaAnexo(null);
                transaccionResumenOld = em.merge(transaccionResumenOld);
            }
            if (transaccionResumenNew != null && !transaccionResumenNew.equals(transaccionResumenOld)) {
                transaccionResumenNew.setTransaccionResumenLineaAnexo(transaccionResumenLineaAnexo);
                transaccionResumenNew = em.merge(transaccionResumenNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TransaccionResumenLineaAnexoPK id = transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK();
                if (findTransaccionResumenLineaAnexo(id) == null) {
                    throw new NonexistentEntityException("The transaccionResumenLineaAnexo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroy(TransaccionResumenLineaAnexoPK id,TransaccionResumen tr) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumenLineaAnexo transaccionResumenLineaAnexo;
            try {
                transaccionResumenLineaAnexo = em.getReference(TransaccionResumenLineaAnexo.class, id);
                transaccionResumenLineaAnexo.setTransaccionResumen(tr);
                transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionResumenLineaAnexo with id " + id + " no longer exists.", enfe);
            }
            TransaccionResumen transaccionResumen = transaccionResumenLineaAnexo.getTransaccionResumen();
            if (transaccionResumen != null) {
                transaccionResumen.setTransaccionResumenLineaAnexo(null);
                transaccionResumen = em.merge(transaccionResumen);
            }
            em.remove(transaccionResumenLineaAnexo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<TransaccionResumenLineaAnexo> findTransaccionResumenLineaAnexoEntities() {
        return findTransaccionResumenLineaAnexoEntities(true, -1, -1);
    }

    public static List<TransaccionResumenLineaAnexo> findTransaccionResumenLineaAnexoEntities(int maxResults, int firstResult) {
        return findTransaccionResumenLineaAnexoEntities(false, maxResults, firstResult);
    }

    private static List<TransaccionResumenLineaAnexo> findTransaccionResumenLineaAnexoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionResumenLineaAnexo.class));
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

    public static TransaccionResumenLineaAnexo findTransaccionResumenLineaAnexo(TransaccionResumenLineaAnexoPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionResumenLineaAnexo.class, id);
        } finally {
            em.close();
        }
    }

    public static int getTransaccionResumenLineaAnexoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionResumenLineaAnexo> rt = cq.from(TransaccionResumenLineaAnexo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static void getEliminarResumenLineaAnexo(String feid, int lineaid) {

        try {
            EntityManager em = getEntityManager();
            Query query = em.createQuery("DELETE FROM TransaccionResumenLineaAnexo trla WHERE trla.transaccionResumenLineaAnexoPK.idLinea= :p AND trla.transaccionResumenLineaAnexoPK.idTransaccion= :x");
            query.setParameter("p", lineaid);
            query.setParameter("x", feid);
            int queryResult = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("Ocurrio un incidente: " + e.getMessage());
        }

    }

    public static void deleteResumen(int idLinea, String idTransaccion) {
        EntityManager em = getEntityManager();
        int isSuccessful = em.createQuery("DELETE FROM TransaccionResumenLineaAnexo trla WHERE trla.transaccionResumenLineaAnexoPK.idLinea= :idLinea and trla.transaccionResumenLineaAnexoPK.idTransaccion= :idTransaccion")
                .setParameter("idLinea", idLinea)
                .setParameter("idTransaccion", idTransaccion)
                .executeUpdate();
        if (isSuccessful == 0) {
            System.out.println("Se eliminó correctamente");
        } else {
            System.out.println("No se eliminó");
        }
    }

}
