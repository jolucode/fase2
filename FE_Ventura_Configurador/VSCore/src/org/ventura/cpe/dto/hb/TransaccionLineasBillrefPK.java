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
 *
 * @author Percy
 */
@Embeddable
public class TransaccionLineasBillrefPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Basic(optional = false)
    @Column(name = "NroOrden")
    private int nroOrden;
    @Basic(optional = false)
    @Column(name = "LineId")
    private int lineId;

    public TransaccionLineasBillrefPK() {
    }

    public TransaccionLineasBillrefPK(String fEId, int nroOrden, int lineId) {
        this.fEId = fEId;
        this.nroOrden = nroOrden;
        this.lineId = lineId;
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

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) nroOrden;
        hash += (int) lineId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineasBillrefPK)) {
            return false;
        }
        TransaccionLineasBillrefPK other = (TransaccionLineasBillrefPK) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        if (this.nroOrden != other.nroOrden) {
            return false;
        }
        if (this.lineId != other.lineId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineasBillrefPK[ fEId=" + fEId + ", nroOrden=" + nroOrden + ", lineId=" + lineId + " ]";
    }
    
}
