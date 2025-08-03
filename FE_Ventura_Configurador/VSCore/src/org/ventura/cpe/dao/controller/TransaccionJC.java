/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dto.hb.Transaccion;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
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

    public static Transaccion findTransaccionCola(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaccion.class, id);
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

}