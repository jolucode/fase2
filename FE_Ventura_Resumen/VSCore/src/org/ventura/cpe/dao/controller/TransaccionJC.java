/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.excepciones.VenturaExcepcion;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Percy
 */
public class TransaccionJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(Transaccion transaccionCola) throws PreexistingEntityException, VenturaExcepcion {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(transaccionCola);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionCola(transaccionCola.getFEId()) != null) {
                throw new PreexistingEntityException("Transaccion " + transaccionCola + " ya existe.", ex);
            }
            throw new VenturaExcepcion(ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void createMasive(List<Transaccion> tcs) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (Transaccion tc : tcs) {
                try {
                    em.persist(tc);
                } catch (Exception ex) {
                    if (findTransaccionCola(tc.getFEId()) != null) {
                        throw new PreexistingEntityException("Transaccion " + tc + " ya existe.", ex);
                    }
                    throw ex;
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(Transaccion transaccionCola) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            transaccionCola = em.merge(transaccionCola);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = transaccionCola.getFEId();
                if (findTransaccionCola(id) == null) {
                    throw new NonexistentEntityException("The transaccionCola with id " + id + " no longer exists.");
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
            Transaccion transaccionCola;
            try {
                transaccionCola = em.getReference(Transaccion.class, id);
                transaccionCola.getFEId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionCola with id " + id + " no longer exists.", enfe);
            }
            em.remove(transaccionCola);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<Transaccion> findTransaccionColaEntities() {
        return findTransaccionColaEntities(true, -1, -1);
    }

    public static List<Transaccion> findTransaccionColaEntities(int maxResults, int firstResult) {
        return findTransaccionColaEntities(false, maxResults, firstResult);
    }

    private static List<Transaccion> findTransaccionColaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaccion.class));
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

    public static Transaccion findTransaccionCola(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaccion.class, id);
        } finally {
            em.close();
        }
    }

    public static int getTransaccionColaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaccion> rt = cq.from(Transaccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @return Las transacciones con estado [N]uevo, [E]nviado, [C]orregido y
     * Baja corre[G]ida
     */
    public static List<Transaccion> findDisponibles() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Transaccion.findDisponibles");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     *
     * @return Las transacciones con estado [R]ecibido,[P]endiente Respuesta
     * SUNAT y Con[S]ulta enviada
     */
    public static List<Transaccion> findPendientes() {
        EntityManager em = getEntityManager();

        try {
//            ExpressionBuilder emp = new ExpressionBuilder();
//            Expression exp = emp.get("fEEstado").in(new char[]{'R','P','S'});
//            Expression exp1 = exp.and(emp.get("dOCCodigo").notEqual("03"));
////            List employees = em.readAllObjects(Employee.class,
////                               exp.and(emp.get("salary").greaterThan(10000)));

            Query q = em.createNamedQuery("Transaccion.findPendientes");

            //Query q = em.createQuery(exp1.toString()getStatement().getQuery().getSQLString());
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public static List<Transaccion> findAll() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createNamedQuery("Transaccion.findAll");
            return q.getResultList();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static int findMaxSalto() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createNamedQuery("Transaccion.findMaxEncolamiento");
            Object object = q.getSingleResult();
            int iMaximoReg = 0;
            if(object != null){
                iMaximoReg = (Integer)object;
            }
            return iMaximoReg;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
