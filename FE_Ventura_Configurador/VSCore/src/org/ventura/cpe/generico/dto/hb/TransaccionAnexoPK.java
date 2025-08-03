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
public class TransaccionAnexoPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Basic(optional = false)
    @Column(name = "NroAnexo")
    private int nroAnexo;

    public TransaccionAnexoPK() {
    }

    public TransaccionAnexoPK(String fEId, int nroAnexo) {
        this.fEId = fEId;
        this.nroAnexo = nroAnexo;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public int getNroAnexo() {
        return nroAnexo;
    }

    public void setNroAnexo(int nroAnexo) {
        this.nroAnexo = nroAnexo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) nroAnexo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionAnexoPK)) {
            return false;
        }
        TransaccionAnexoPK other = (TransaccionAnexoPK) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        if (this.nroAnexo != other.nroAnexo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionAnexoPK[ fEId=" + fEId + ", nroAnexo=" + nroAnexo + " ]";
    }
    
}
