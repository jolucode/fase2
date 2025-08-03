/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionBaja;
import org.ventura.wsclient.config.ApplicationConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * @author VSUser
 */
public class TransaccionBajaJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public void create(TransaccionBaja transaccionBaja) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(transaccionBaja);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionBaja(transaccionBaja.getFecha()) != null) {
                throw new PreexistingEntityException("TransaccionBaja " + transaccionBaja + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TransaccionBaja transaccionBaja) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            transaccionBaja = em.merge(transaccionBaja);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = transaccionBaja.getFecha();
                if (findTransaccionBaja(id) == null) {
                    throw new NonexistentEntityException("The transaccionBaja with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionBaja transaccionBaja;
            try {
                transaccionBaja = em.getReference(TransaccionBaja.class, id);
                transaccionBaja.getFecha();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionBaja with id " + id + " no longer exists.", enfe);
            }
            em.remove(transaccionBaja);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TransaccionBaja> findTransaccionBajaEntities() {
        return findTransaccionBajaEntities(true, -1, -1);
    }

    public List<TransaccionBaja> findTransaccionBajaEntities(int maxResults, int firstResult) {
        return findTransaccionBajaEntities(false, maxResults, firstResult);
    }

    private List<TransaccionBaja> findTransaccionBajaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionBaja.class));
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

    public TransaccionBaja findTransaccionBaja(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionBaja.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionBajaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionBaja> rt = cq.from(TransaccionBaja.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public static String idBaja() {
        String puerto = "";
        try {
            puerto=ApplicationConfiguration.getInstance().getConfiguration().getRepositorio().getPuerto();
        }
        catch (Exception e){ puerto = "Desconocido";}

        System.out.println("Puerto de repositorio es: "+ puerto);

        String ultimo = "";

        EntityManager em = getEntityManager();
        try {

            /*StoredProcedureQuery query = em.createNamedStoredProcedureQuery("bpvs_FE_Correlativo_Baja");
            List<Object> lst = query.getResultList();*/

            if (puerto.contains("3306")){
                List lst = em
                        .createNativeQuery("call bpvs_FE_Correlativo_Baja()")
                        .getResultList();
                Object[] objects = (Object[]) lst.get(0);
                ultimo = objects[2].toString();
            }
            if (puerto.contains("1433")){
                List lst = em
                        .createNativeQuery("exec \"bpvs_FE_Correlativo_Baja\"")
                        .getResultList();
                Object[] objects = (Object[]) lst.get(0);
                ultimo = objects[2].toString();
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            em.close();
        }
        return ultimo;
    }
}
