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
 * @author VS-LT-06
 */
@Embeddable
public class TransaccionComprobantepagoUsuarioPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "Id")
    private int id;

    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;

    @Basic(optional = false)
    @Column(name = "NroOrden")
    private int nroOrden;

    public TransaccionComprobantepagoUsuarioPK() {
    }

    public TransaccionComprobantepagoUsuarioPK(int id, String fEId, int nroOrden) {
        this.id = id;
        this.fEId = fEId;
        this.nroOrden = nroOrden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public int getNroOrden() {
        return nroOrden;
    }

    public void setNroOrden(int nroOrden) {
        this.nroOrden = nroOrden;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) nroOrden;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionComprobantepagoUsuarioPK)) {
            return false;
        }
        TransaccionComprobantepagoUsuarioPK other = (TransaccionComprobantepagoUsuarioPK) object;
        if (this.id != other.id) {
            return false;
        }
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        if (this.nroOrden != other.nroOrden) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionComprobantepagoUsuarioPK[ id=" + id + ", fEId=" + fEId + ", nroOrden=" + nroOrden + " ]";
    }

}
