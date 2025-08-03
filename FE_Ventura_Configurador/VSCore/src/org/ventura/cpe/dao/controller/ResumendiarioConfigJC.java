/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.ResumendiarioConfig;
import org.ventura.cpe.log.LoggerTrans;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.logging.Level;

/**
 *
 * @author VSUser
 */
public class ResumendiarioConfigJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(ResumendiarioConfig resumendiarioConfig) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(resumendiarioConfig);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findResumendiarioConfig(resumendiarioConfig.getFecha()) != null) {
                ResumendiarioConfigJC.edit(resumendiarioConfig);
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "Se actualizo el registro {0} con el ID {1}", new Object[]{resumendiarioConfig.getFecha(), resumendiarioConfig.getId()});
                throw new PreexistingEntityException("ResumendiarioConfig " + resumendiarioConfig + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void edit(ResumendiarioConfig resumendiarioConfig) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            resumendiarioConfig = em.merge(resumendiarioConfig);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = resumendiarioConfig.getFecha();
                if (findResumendiarioConfig(id) == null) {
                    throw new NonexistentEntityException("The resumendiarioConfig with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static ResumendiarioConfig findResumendiarioConfig(String id) {
        EntityManager em = getEntityManager();
        ResumendiarioConfig rsd = null;
        try {
            rsd = em.find(ResumendiarioConfig.class, id);
        } catch (Exception ex) {
            return rsd = null;
        } finally {
            em.close();
        }
        return rsd;
    }

}