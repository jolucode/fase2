/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.controller.exceptions.NonexistentEntityException;
import org.ventura.cpe.dto.hb.PublicardocWs;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public static PublicardocWs findPublicardocWs(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PublicardocWs.class, id);
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
