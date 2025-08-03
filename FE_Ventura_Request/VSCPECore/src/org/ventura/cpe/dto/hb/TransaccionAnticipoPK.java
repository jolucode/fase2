/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author VSUser
 */
@Embeddable
public class TransaccionAnticipoPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;

    @Basic(optional = false)
    @Column(name = "NroAnticipo")
    private int nroAnticipo;

    public TransaccionAnticipoPK() {
    }

    public TransaccionAnticipoPK(String fEId, int nroAnticipo) {
        this.fEId = fEId;
        this.nroAnticipo = nroAnticipo;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public int getNroAnticipo() {
        return nroAnticipo;
    }

    public void setNroAnticipo(int nroAnticipo) {
        this.nroAnticipo = nroAnticipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) nroAnticipo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionAnticipoPK)) {
            return false;
        }
        TransaccionAnticipoPK other = (TransaccionAnticipoPK) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        if (this.nroAnticipo != other.nroAnticipo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionAnticipoPK[ fEId=" + fEId + ", nroAnticipo=" + nroAnticipo + " ]";
    }

}
