/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.hb;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author VSUser
 */
@Embeddable
public class TransaccionContractdocrefPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Basic(optional = false)
    @Column(name = "IdCampoUsuario")
    private int idCampoUsuario;

    public TransaccionContractdocrefPK() {
    }

    public TransaccionContractdocrefPK(String fEId, int idCampoUsuario) {
        this.fEId = fEId;
        this.idCampoUsuario = idCampoUsuario;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public int getIdCampoUsuario() {
        return idCampoUsuario;
    }

    public void setIdCampoUsuario(int idCampoUsuario) {
        this.idCampoUsuario = idCampoUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) idCampoUsuario;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionContractdocrefPK)) {
            return false;
        }
        TransaccionContractdocrefPK other = (TransaccionContractdocrefPK) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        if (this.idCampoUsuario != other.idCampoUsuario) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionContractdocrefPK[ fEId=" + fEId + ", idCampoUsuario=" + idCampoUsuario + " ]";
    }
    
}
