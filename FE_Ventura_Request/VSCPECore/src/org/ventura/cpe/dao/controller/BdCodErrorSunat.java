/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.controller;

import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.hb.CodErrorSunat;
import org.ventura.cpe.dto.hb.Transaccion;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * @author JoseLuisBecerra
 */
public class BdCodErrorSunat implements Serializable {

    public static EntityManager getEntityManager() {
        return HBPersistencia.getInstancia().createEntityManager();
    }


    public static CodErrorSunat findCodErrorSunat(String idCodError) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CodErrorSunat.class, idCodError);
        } finally {
            em.close();
        }
    }

}
