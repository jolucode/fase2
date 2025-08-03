/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionLineasUsucampos;
import org.ventura.cpe.dto.hb.TransaccionUsucampos;
import org.ventura.cpe.dto.hb.Usuariocampos;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Percy
 */
public class UsuariocamposJC implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }

    public static void create(Usuariocampos usuariocampos) throws PreexistingEntityException, Exception {
        if (usuariocampos.getTransaccionUsucamposList() == null) {
            usuariocampos.setTransaccionUsucamposList(new ArrayList<TransaccionUsucampos>());
        }
        if (usuariocampos.getTransaccionLineasUsucamposList() == null) {
            usuariocampos.setTransaccionLineasUsucamposList(new ArrayList<TransaccionLineasUsucampos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TransaccionUsucampos> attachedTransaccionUsucamposList = new ArrayList<TransaccionUsucampos>();
            for (TransaccionUsucampos transaccionUsucamposListTransaccionUsucamposToAttach : usuariocampos.getTransaccionUsucamposList()) {
                transaccionUsucamposListTransaccionUsucamposToAttach = em.getReference(transaccionUsucamposListTransaccionUsucamposToAttach.getClass(), transaccionUsucamposListTransaccionUsucamposToAttach.getTransaccionUsucamposPK());
                attachedTransaccionUsucamposList.add(transaccionUsucamposListTransaccionUsucamposToAttach);
            }
            usuariocampos.setTransaccionUsucamposList(attachedTransaccionUsucamposList);
            List<TransaccionLineasUsucampos> attachedTransaccionLineasUsucamposList = new ArrayList<TransaccionLineasUsucampos>();
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListTransaccionLineasUsucamposToAttach : usuariocampos.getTransaccionLineasUsucamposList()) {
                transaccionLineasUsucamposListTransaccionLineasUsucamposToAttach = em.getReference(transaccionLineasUsucamposListTransaccionLineasUsucamposToAttach.getClass(), transaccionLineasUsucamposListTransaccionLineasUsucamposToAttach.getTransaccionLineasUsucamposPK());
                attachedTransaccionLineasUsucamposList.add(transaccionLineasUsucamposListTransaccionLineasUsucamposToAttach);
            }
            usuariocampos.setTransaccionLineasUsucamposList(attachedTransaccionLineasUsucamposList);
            em.persist(usuariocampos);
            for (TransaccionUsucampos transaccionUsucamposListTransaccionUsucampos : usuariocampos.getTransaccionUsucamposList()) {
                Usuariocampos oldUsuariocamposOfTransaccionUsucamposListTransaccionUsucampos = transaccionUsucamposListTransaccionUsucampos.getUsuariocampos();
                transaccionUsucamposListTransaccionUsucampos.setUsuariocampos(usuariocampos);
                transaccionUsucamposListTransaccionUsucampos = em.merge(transaccionUsucamposListTransaccionUsucampos);
                if (oldUsuariocamposOfTransaccionUsucamposListTransaccionUsucampos != null) {
                    oldUsuariocamposOfTransaccionUsucamposListTransaccionUsucampos.getTransaccionUsucamposList().remove(transaccionUsucamposListTransaccionUsucampos);
                    oldUsuariocamposOfTransaccionUsucamposListTransaccionUsucampos = em.merge(oldUsuariocamposOfTransaccionUsucamposListTransaccionUsucampos);
                }
            }
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListTransaccionLineasUsucampos : usuariocampos.getTransaccionLineasUsucamposList()) {
                Usuariocampos oldUsuariocamposOfTransaccionLineasUsucamposListTransaccionLineasUsucampos = transaccionLineasUsucamposListTransaccionLineasUsucampos.getUsuariocampos();
                transaccionLineasUsucamposListTransaccionLineasUsucampos.setUsuariocampos(usuariocampos);
                transaccionLineasUsucamposListTransaccionLineasUsucampos = em.merge(transaccionLineasUsucamposListTransaccionLineasUsucampos);
                if (oldUsuariocamposOfTransaccionLineasUsucamposListTransaccionLineasUsucampos != null) {
                    oldUsuariocamposOfTransaccionLineasUsucamposListTransaccionLineasUsucampos.getTransaccionLineasUsucamposList().remove(transaccionLineasUsucamposListTransaccionLineasUsucampos);
                    oldUsuariocamposOfTransaccionLineasUsucamposListTransaccionLineasUsucampos = em.merge(oldUsuariocamposOfTransaccionLineasUsucamposListTransaccionLineasUsucampos);
                }
            }
            em.getTransaction().commit();

        } catch (Exception ex) {
            if (findUsuariocampos(usuariocampos.getId()) != null) {
                throw new PreexistingEntityException("Usuariocampos " + usuariocampos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static Usuariocampos findUsuariocampos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuariocampos.class, id);
        } finally {
            em.close();
        }
    }

    public static int getIdByNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Usuariocampos.findByNombre");
            q.setParameter("nombre", nombre);
            List lista = q.getResultList();
            if (lista.size() > 0) {
                return ((Usuariocampos) lista.get(0)).getId();
            }
        } finally {
            em.close();
        }
        return 0;
    }

}