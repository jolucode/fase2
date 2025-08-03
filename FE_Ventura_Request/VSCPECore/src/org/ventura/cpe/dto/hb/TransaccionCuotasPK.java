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
import java.util.Objects;

/**
 * @author VSUser
 */
@Embeddable
public class TransaccionCuotasPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;

    @Basic(optional = false)
    @Column(name = "NroOrden")
    private int nroOrden;

    public TransaccionCuotasPK() {
    }

    public TransaccionCuotasPK(String fEId, int nroOrden) {
        this.fEId = fEId;
        this.nroOrden = nroOrden;
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
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineasPK[ fEId=" + fEId + ", nroOrden=" + nroOrden + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionCuotasPK that = (TransaccionCuotasPK) o;
        return nroOrden == that.nroOrden && Objects.equals(fEId, that.fEId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fEId, nroOrden);
    }
}
