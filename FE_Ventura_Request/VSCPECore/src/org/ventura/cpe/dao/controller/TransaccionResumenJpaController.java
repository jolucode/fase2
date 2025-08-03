/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.exceptions.IllegalOrphanException;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.dto.hb.TransaccionResumenLinea;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VS-LT-06
 */
public class TransaccionResumenJpaController implements Serializable {

    public TransaccionResumenJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TransaccionResumen transaccionResumen) throws PreexistingEntityException, Exception {
        if (transaccionResumen.getTransaccionResumenLineaList() == null) {
            transaccionResumen.setTransaccionResumenLineaList(new ArrayList<TransaccionResumenLinea>());
        }
        if (transaccionResumen.getTransaccionResumenLineaAnexoList() == null) {
            transaccionResumen.setTransaccionResumenLineaAnexoList(new ArrayList<TransaccionResumenLineaAnexo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumenLineaAnexo transaccionResumenLineaAnexo = transaccionResumen.getTransaccionResumenLineaAnexo();
            if (transaccionResumenLineaAnexo != null) {
                transaccionResumenLineaAnexo = em.getReference(transaccionResumenLineaAnexo.getClass(), transaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoPK());
                transaccionResumen.setTransaccionResumenLineaAnexo(transaccionResumenLineaAnexo);
            }
            List<TransaccionResumenLinea> attachedTransaccionResumenLineaList = new ArrayList<TransaccionResumenLinea>();
            for (TransaccionResumenLinea transaccionResumenLineaListTransaccionResumenLineaToAttach : transaccionResumen.getTransaccionResumenLineaList()) {
                transaccionResumenLineaListTransaccionResumenLineaToAttach = em.getReference(transaccionResumenLineaListTransaccionResumenLineaToAttach.getClass(), transaccionResumenLineaListTransaccionResumenLineaToAttach.getTransaccionResumenLineaPK());
                attachedTransaccionResumenLineaList.add(transaccionResumenLineaListTransaccionResumenLineaToAttach);
            }
            transaccionResumen.setTransaccionResumenLineaList(attachedTransaccionResumenLineaList);
            List<TransaccionResumenLineaAnexo> attachedTransaccionResumenLineaAnexoList = new ArrayList<TransaccionResumenLineaAnexo>();
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListTransaccionResumenLineaAnexoToAttach : transaccionResumen.getTransaccionResumenLineaAnexoList()) {
                transaccionResumenLineaAnexoListTransaccionResumenLineaAnexoToAttach = em.getReference(transaccionResumenLineaAnexoListTransaccionResumenLineaAnexoToAttach.getClass(), transaccionResumenLineaAnexoListTransaccionResumenLineaAnexoToAttach.getTransaccionResumenLineaAnexoPK());
                attachedTransaccionResumenLineaAnexoList.add(transaccionResumenLineaAnexoListTransaccionResumenLineaAnexoToAttach);
            }
            transaccionResumen.setTransaccionResumenLineaAnexoList(attachedTransaccionResumenLineaAnexoList);
            em.persist(transaccionResumen);
            if (transaccionResumenLineaAnexo != null) {
                TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaAnexo = transaccionResumenLineaAnexo.getTransaccionResumen();
                if (oldTransaccionResumenOfTransaccionResumenLineaAnexo != null) {
                    oldTransaccionResumenOfTransaccionResumenLineaAnexo.setTransaccionResumenLineaAnexo(null);
                    oldTransaccionResumenOfTransaccionResumenLineaAnexo = em.merge(oldTransaccionResumenOfTransaccionResumenLineaAnexo);
                }
                transaccionResumenLineaAnexo.setTransaccionResumen(transaccionResumen);
                transaccionResumenLineaAnexo = em.merge(transaccionResumenLineaAnexo);
            }
            for (TransaccionResumenLinea transaccionResumenLineaListTransaccionResumenLinea : transaccionResumen.getTransaccionResumenLineaList()) {
                TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaListTransaccionResumenLinea = transaccionResumenLineaListTransaccionResumenLinea.getTransaccionResumen();
                transaccionResumenLineaListTransaccionResumenLinea.setTransaccionResumen(transaccionResumen);
                transaccionResumenLineaListTransaccionResumenLinea = em.merge(transaccionResumenLineaListTransaccionResumenLinea);
                if (oldTransaccionResumenOfTransaccionResumenLineaListTransaccionResumenLinea != null) {
                    oldTransaccionResumenOfTransaccionResumenLineaListTransaccionResumenLinea.getTransaccionResumenLineaList().remove(transaccionResumenLineaListTransaccionResumenLinea);
                    oldTransaccionResumenOfTransaccionResumenLineaListTransaccionResumenLinea = em.merge(oldTransaccionResumenOfTransaccionResumenLineaListTransaccionResumenLinea);
                }
            }
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo : transaccionResumen.getTransaccionResumenLineaAnexoList()) {
                TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaAnexoListTransaccionResumenLineaAnexo = transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo.getTransaccionResumen();
                transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo.setTransaccionResumen(transaccionResumen);
                transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo = em.merge(transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo);
                if (oldTransaccionResumenOfTransaccionResumenLineaAnexoListTransaccionResumenLineaAnexo != null) {
                    oldTransaccionResumenOfTransaccionResumenLineaAnexoListTransaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoList().remove(transaccionResumenLineaAnexoListTransaccionResumenLineaAnexo);
                    oldTransaccionResumenOfTransaccionResumenLineaAnexoListTransaccionResumenLineaAnexo = em.merge(oldTransaccionResumenOfTransaccionResumenLineaAnexoListTransaccionResumenLineaAnexo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccionResumen(transaccionResumen.getIdTransaccion()) != null) {
                throw new PreexistingEntityException("TransaccionResumen " + transaccionResumen + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TransaccionResumen transaccionResumen) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumen persistentTransaccionResumen = em.find(TransaccionResumen.class, transaccionResumen.getIdTransaccion());
            TransaccionResumenLineaAnexo transaccionResumenLineaAnexoOld = persistentTransaccionResumen.getTransaccionResumenLineaAnexo();
            TransaccionResumenLineaAnexo transaccionResumenLineaAnexoNew = transaccionResumen.getTransaccionResumenLineaAnexo();
            List<TransaccionResumenLinea> transaccionResumenLineaListOld = persistentTransaccionResumen.getTransaccionResumenLineaList();
            List<TransaccionResumenLinea> transaccionResumenLineaListNew = transaccionResumen.getTransaccionResumenLineaList();
            List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexoListOld = persistentTransaccionResumen.getTransaccionResumenLineaAnexoList();
            List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexoListNew = transaccionResumen.getTransaccionResumenLineaAnexoList();
            List<String> illegalOrphanMessages = null;
            if (transaccionResumenLineaAnexoOld != null && !transaccionResumenLineaAnexoOld.equals(transaccionResumenLineaAnexoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain TransaccionResumenLineaAnexo " + transaccionResumenLineaAnexoOld + " since its transaccionResumen field is not nullable.");
            }
            for (TransaccionResumenLinea transaccionResumenLineaListOldTransaccionResumenLinea : transaccionResumenLineaListOld) {
                if (!transaccionResumenLineaListNew.contains(transaccionResumenLineaListOldTransaccionResumenLinea)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TransaccionResumenLinea " + transaccionResumenLineaListOldTransaccionResumenLinea + " since its transaccionResumen field is not nullable.");
                }
            }
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListOldTransaccionResumenLineaAnexo : transaccionResumenLineaAnexoListOld) {
                if (!transaccionResumenLineaAnexoListNew.contains(transaccionResumenLineaAnexoListOldTransaccionResumenLineaAnexo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TransaccionResumenLineaAnexo " + transaccionResumenLineaAnexoListOldTransaccionResumenLineaAnexo + " since its transaccionResumen field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (transaccionResumenLineaAnexoNew != null) {
                transaccionResumenLineaAnexoNew = em.getReference(transaccionResumenLineaAnexoNew.getClass(), transaccionResumenLineaAnexoNew.getTransaccionResumenLineaAnexoPK());
                transaccionResumen.setTransaccionResumenLineaAnexo(transaccionResumenLineaAnexoNew);
            }
            List<TransaccionResumenLinea> attachedTransaccionResumenLineaListNew = new ArrayList<TransaccionResumenLinea>();
            for (TransaccionResumenLinea transaccionResumenLineaListNewTransaccionResumenLineaToAttach : transaccionResumenLineaListNew) {
                transaccionResumenLineaListNewTransaccionResumenLineaToAttach = em.getReference(transaccionResumenLineaListNewTransaccionResumenLineaToAttach.getClass(), transaccionResumenLineaListNewTransaccionResumenLineaToAttach.getTransaccionResumenLineaPK());
                attachedTransaccionResumenLineaListNew.add(transaccionResumenLineaListNewTransaccionResumenLineaToAttach);
            }
            transaccionResumenLineaListNew = attachedTransaccionResumenLineaListNew;
            transaccionResumen.setTransaccionResumenLineaList(transaccionResumenLineaListNew);
            List<TransaccionResumenLineaAnexo> attachedTransaccionResumenLineaAnexoListNew = new ArrayList<TransaccionResumenLineaAnexo>();
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexoToAttach : transaccionResumenLineaAnexoListNew) {
                transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexoToAttach = em.getReference(transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexoToAttach.getClass(), transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexoToAttach.getTransaccionResumenLineaAnexoPK());
                attachedTransaccionResumenLineaAnexoListNew.add(transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexoToAttach);
            }
            transaccionResumenLineaAnexoListNew = attachedTransaccionResumenLineaAnexoListNew;
            transaccionResumen.setTransaccionResumenLineaAnexoList(transaccionResumenLineaAnexoListNew);
            transaccionResumen = em.merge(transaccionResumen);
            if (transaccionResumenLineaAnexoNew != null && !transaccionResumenLineaAnexoNew.equals(transaccionResumenLineaAnexoOld)) {
                TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaAnexo = transaccionResumenLineaAnexoNew.getTransaccionResumen();
                if (oldTransaccionResumenOfTransaccionResumenLineaAnexo != null) {
                    oldTransaccionResumenOfTransaccionResumenLineaAnexo.setTransaccionResumenLineaAnexo(null);
                    oldTransaccionResumenOfTransaccionResumenLineaAnexo = em.merge(oldTransaccionResumenOfTransaccionResumenLineaAnexo);
                }
                transaccionResumenLineaAnexoNew.setTransaccionResumen(transaccionResumen);
                transaccionResumenLineaAnexoNew = em.merge(transaccionResumenLineaAnexoNew);
            }
            for (TransaccionResumenLinea transaccionResumenLineaListNewTransaccionResumenLinea : transaccionResumenLineaListNew) {
                if (!transaccionResumenLineaListOld.contains(transaccionResumenLineaListNewTransaccionResumenLinea)) {
                    TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea = transaccionResumenLineaListNewTransaccionResumenLinea.getTransaccionResumen();
                    transaccionResumenLineaListNewTransaccionResumenLinea.setTransaccionResumen(transaccionResumen);
                    transaccionResumenLineaListNewTransaccionResumenLinea = em.merge(transaccionResumenLineaListNewTransaccionResumenLinea);
                    if (oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea != null && !oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea.equals(transaccionResumen)) {
                        oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea.getTransaccionResumenLineaList().remove(transaccionResumenLineaListNewTransaccionResumenLinea);
                        oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea = em.merge(oldTransaccionResumenOfTransaccionResumenLineaListNewTransaccionResumenLinea);
                    }
                }
            }
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo : transaccionResumenLineaAnexoListNew) {
                if (!transaccionResumenLineaAnexoListOld.contains(transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo)) {
                    TransaccionResumen oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo = transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo.getTransaccionResumen();
                    transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo.setTransaccionResumen(transaccionResumen);
                    transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo = em.merge(transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo);
                    if (oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo != null && !oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo.equals(transaccionResumen)) {
                        oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo.getTransaccionResumenLineaAnexoList().remove(transaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo);
                        oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo = em.merge(oldTransaccionResumenOfTransaccionResumenLineaAnexoListNewTransaccionResumenLineaAnexo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = transaccionResumen.getIdTransaccion();
                if (findTransaccionResumen(id) == null) {
                    throw new NonexistentEntityException("The transaccionResumen with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TransaccionResumen transaccionResumen;
            try {
                transaccionResumen = em.getReference(TransaccionResumen.class, id);
                transaccionResumen.getIdTransaccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccionResumen with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            TransaccionResumenLineaAnexo transaccionResumenLineaAnexoOrphanCheck = transaccionResumen.getTransaccionResumenLineaAnexo();
            if (transaccionResumenLineaAnexoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TransaccionResumen (" + transaccionResumen + ") cannot be destroyed since the TransaccionResumenLineaAnexo " + transaccionResumenLineaAnexoOrphanCheck + " in its transaccionResumenLineaAnexo field has a non-nullable transaccionResumen field.");
            }
            List<TransaccionResumenLinea> transaccionResumenLineaListOrphanCheck = transaccionResumen.getTransaccionResumenLineaList();
            for (TransaccionResumenLinea transaccionResumenLineaListOrphanCheckTransaccionResumenLinea : transaccionResumenLineaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TransaccionResumen (" + transaccionResumen + ") cannot be destroyed since the TransaccionResumenLinea " + transaccionResumenLineaListOrphanCheckTransaccionResumenLinea + " in its transaccionResumenLineaList field has a non-nullable transaccionResumen field.");
            }
            List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexoListOrphanCheck = transaccionResumen.getTransaccionResumenLineaAnexoList();
            for (TransaccionResumenLineaAnexo transaccionResumenLineaAnexoListOrphanCheckTransaccionResumenLineaAnexo : transaccionResumenLineaAnexoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TransaccionResumen (" + transaccionResumen + ") cannot be destroyed since the TransaccionResumenLineaAnexo " + transaccionResumenLineaAnexoListOrphanCheckTransaccionResumenLineaAnexo + " in its transaccionResumenLineaAnexoList field has a non-nullable transaccionResumen field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(transaccionResumen);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TransaccionResumen> findTransaccionResumenEntities() {
        return findTransaccionResumenEntities(true, -1, -1);
    }

    public List<TransaccionResumen> findTransaccionResumenEntities(int maxResults, int firstResult) {
        return findTransaccionResumenEntities(false, maxResults, firstResult);
    }

    private List<TransaccionResumen> findTransaccionResumenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TransaccionResumen.class));
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

    public TransaccionResumen findTransaccionResumen(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TransaccionResumen.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionResumenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TransaccionResumen> rt = cq.from(TransaccionResumen.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
