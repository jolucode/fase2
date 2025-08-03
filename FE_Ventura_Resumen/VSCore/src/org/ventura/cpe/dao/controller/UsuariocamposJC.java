/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dao.exceptions.IllegalOrphanException;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionLineasUsucampos;
import org.ventura.cpe.dto.hb.TransaccionUsucampos;
import org.ventura.cpe.dto.hb.Usuariocampos;

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

    public static Usuariocampos ReturnIdentity(Usuariocampos usuariocampos) throws PreexistingEntityException, Exception {
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
            return usuariocampos;
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

    public static void edit(Usuariocampos usuariocampos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuariocampos persistentUsuariocampos = em.find(Usuariocampos.class, usuariocampos.getId());
            List<TransaccionUsucampos> transaccionUsucamposListOld = persistentUsuariocampos.getTransaccionUsucamposList();
            List<TransaccionUsucampos> transaccionUsucamposListNew = usuariocampos.getTransaccionUsucamposList();
            List<TransaccionLineasUsucampos> transaccionLineasUsucamposListOld = persistentUsuariocampos.getTransaccionLineasUsucamposList();
            List<TransaccionLineasUsucampos> transaccionLineasUsucamposListNew = usuariocampos.getTransaccionLineasUsucamposList();
            List<String> illegalOrphanMessages = null;
            for (TransaccionUsucampos transaccionUsucamposListOldTransaccionUsucampos : transaccionUsucamposListOld) {
                if (!transaccionUsucamposListNew.contains(transaccionUsucamposListOldTransaccionUsucampos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TransaccionUsucampos " + transaccionUsucamposListOldTransaccionUsucampos + " since its usuariocampos field is not nullable.");
                }
            }
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListOldTransaccionLineasUsucampos : transaccionLineasUsucamposListOld) {
                if (!transaccionLineasUsucamposListNew.contains(transaccionLineasUsucamposListOldTransaccionLineasUsucampos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TransaccionLineasUsucampos " + transaccionLineasUsucamposListOldTransaccionLineasUsucampos + " since its usuariocampos field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TransaccionUsucampos> attachedTransaccionUsucamposListNew = new ArrayList<TransaccionUsucampos>();
            for (TransaccionUsucampos transaccionUsucamposListNewTransaccionUsucamposToAttach : transaccionUsucamposListNew) {
                transaccionUsucamposListNewTransaccionUsucamposToAttach = em.getReference(transaccionUsucamposListNewTransaccionUsucamposToAttach.getClass(), transaccionUsucamposListNewTransaccionUsucamposToAttach.getTransaccionUsucamposPK());
                attachedTransaccionUsucamposListNew.add(transaccionUsucamposListNewTransaccionUsucamposToAttach);
            }
            transaccionUsucamposListNew = attachedTransaccionUsucamposListNew;
            usuariocampos.setTransaccionUsucamposList(transaccionUsucamposListNew);
            List<TransaccionLineasUsucampos> attachedTransaccionLineasUsucamposListNew = new ArrayList<TransaccionLineasUsucampos>();
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListNewTransaccionLineasUsucamposToAttach : transaccionLineasUsucamposListNew) {
                transaccionLineasUsucamposListNewTransaccionLineasUsucamposToAttach = em.getReference(transaccionLineasUsucamposListNewTransaccionLineasUsucamposToAttach.getClass(), transaccionLineasUsucamposListNewTransaccionLineasUsucamposToAttach.getTransaccionLineasUsucamposPK());
                attachedTransaccionLineasUsucamposListNew.add(transaccionLineasUsucamposListNewTransaccionLineasUsucamposToAttach);
            }
            transaccionLineasUsucamposListNew = attachedTransaccionLineasUsucamposListNew;
            usuariocampos.setTransaccionLineasUsucamposList(transaccionLineasUsucamposListNew);
            usuariocampos = em.merge(usuariocampos);
            for (TransaccionUsucampos transaccionUsucamposListNewTransaccionUsucampos : transaccionUsucamposListNew) {
                if (!transaccionUsucamposListOld.contains(transaccionUsucamposListNewTransaccionUsucampos)) {
                    Usuariocampos oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos = transaccionUsucamposListNewTransaccionUsucampos.getUsuariocampos();
                    transaccionUsucamposListNewTransaccionUsucampos.setUsuariocampos(usuariocampos);
                    transaccionUsucamposListNewTransaccionUsucampos = em.merge(transaccionUsucamposListNewTransaccionUsucampos);
                    if (oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos != null && !oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos.equals(usuariocampos)) {
                        oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos.getTransaccionUsucamposList().remove(transaccionUsucamposListNewTransaccionUsucampos);
                        oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos = em.merge(oldUsuariocamposOfTransaccionUsucamposListNewTransaccionUsucampos);
                    }
                }
            }
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListNewTransaccionLineasUsucampos : transaccionLineasUsucamposListNew) {
                if (!transaccionLineasUsucamposListOld.contains(transaccionLineasUsucamposListNewTransaccionLineasUsucampos)) {
                    Usuariocampos oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos = transaccionLineasUsucamposListNewTransaccionLineasUsucampos.getUsuariocampos();
                    transaccionLineasUsucamposListNewTransaccionLineasUsucampos.setUsuariocampos(usuariocampos);
                    transaccionLineasUsucamposListNewTransaccionLineasUsucampos = em.merge(transaccionLineasUsucamposListNewTransaccionLineasUsucampos);
                    if (oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos != null && !oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos.equals(usuariocampos)) {
                        oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos.getTransaccionLineasUsucamposList().remove(transaccionLineasUsucamposListNewTransaccionLineasUsucampos);
                        oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos = em.merge(oldUsuariocamposOfTransaccionLineasUsucamposListNewTransaccionLineasUsucampos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuariocampos.getId();
                if (findUsuariocampos(id) == null) {
                    throw new NonexistentEntityException("The usuariocampos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuariocampos usuariocampos;
            try {
                usuariocampos = em.getReference(Usuariocampos.class, id);
                usuariocampos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuariocampos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TransaccionUsucampos> transaccionUsucamposListOrphanCheck = usuariocampos.getTransaccionUsucamposList();
            for (TransaccionUsucampos transaccionUsucamposListOrphanCheckTransaccionUsucampos : transaccionUsucamposListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuariocampos (" + usuariocampos + ") cannot be destroyed since the TransaccionUsucampos " + transaccionUsucamposListOrphanCheckTransaccionUsucampos + " in its transaccionUsucamposList field has a non-nullable usuariocampos field.");
            }
            List<TransaccionLineasUsucampos> transaccionLineasUsucamposListOrphanCheck = usuariocampos.getTransaccionLineasUsucamposList();
            for (TransaccionLineasUsucampos transaccionLineasUsucamposListOrphanCheckTransaccionLineasUsucampos : transaccionLineasUsucamposListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuariocampos (" + usuariocampos + ") cannot be destroyed since the TransaccionLineasUsucampos " + transaccionLineasUsucamposListOrphanCheckTransaccionLineasUsucampos + " in its transaccionLineasUsucamposList field has a non-nullable usuariocampos field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuariocampos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static List<Usuariocampos> findUsuariocamposEntities() {
        return findUsuariocamposEntities(true, -1, -1);
    }

    public static List<Usuariocampos> findUsuariocamposEntities(int maxResults, int firstResult) {
        return findUsuariocamposEntities(false, maxResults, firstResult);
    }

    private static List<Usuariocampos> findUsuariocamposEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuariocampos.class));
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

    public static Usuariocampos findUsuariocampos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuariocampos.class, id);
        } finally {
            em.close();
        }
    }

    public static int getUsuariocamposCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuariocampos> rt = cq.from(Usuariocampos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
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
