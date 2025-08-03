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
public class TransaccionContractdocrefPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;

    @Basic(optional = false)
    @Column(name = "USUCMP_Id")
    private int uSUCMPId;

    public TransaccionContractdocrefPK() {
    }

    public TransaccionContractdocrefPK(String fEId, int uSUCMPId) {
        this.fEId = fEId;
        this.uSUCMPId = uSUCMPId;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public int getUSUCMPId() {
        return uSUCMPId;
    }

    public void setUSUCMPId(int uSUCMPId) {
        this.uSUCMPId = uSUCMPId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        hash += (int) uSUCMPId;
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
        if (this.uSUCMPId != other.uSUCMPId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionContractdocrefPK[ fEId=" + fEId + ", uSUCMPId=" + uSUCMPId + " ]";
    }

}
