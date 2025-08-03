/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_LINEAS_BILLREF")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionLineasBillref.findAll", query = "SELECT t FROM TransaccionLineasBillref t"),
        @NamedQuery(name = "TransaccionLineasBillref.findByFEId", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.transaccionLineasBillrefPK.fEId = :fEId"),
        @NamedQuery(name = "TransaccionLineasBillref.findByNroOrden", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.transaccionLineasBillrefPK.nroOrden = :nroOrden"),
        @NamedQuery(name = "TransaccionLineasBillref.findByLineId", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.transaccionLineasBillrefPK.lineId = :lineId"),
        @NamedQuery(name = "TransaccionLineasBillref.findByAdtDocRefId", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.adtDocRefId = :adtDocRefId"),
        @NamedQuery(name = "TransaccionLineasBillref.findByAdtDocRefSchemaId", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.adtDocRefSchemaId = :adtDocRefSchemaId"),
        @NamedQuery(name = "TransaccionLineasBillref.findByInvDocRefDocTypeCode", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.invDocRefDocTypeCode = :invDocRefDocTypeCode"),
        @NamedQuery(name = "TransaccionLineasBillref.findByInvDocRefId", query = "SELECT t FROM TransaccionLineasBillref t WHERE t.invDocRefId = :invDocRefId")})
public class TransaccionLineasBillref implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionLineasBillrefPK transaccionLineasBillrefPK;

    @Column(name = "AdtDocRef_Id")
    private String adtDocRefId;

    @Column(name = "AdtDocRef_SchemaId")
    private String adtDocRefSchemaId;

    @Column(name = "InvDocRef_DocTypeCode")
    private String invDocRefDocTypeCode;

    @Column(name = "InvDocRef_Id")
    private String invDocRefId;

    @JoinColumns({
            @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
            @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionLineas transaccionLineas;

    public TransaccionLineasBillref() {
    }

    public TransaccionLineasBillref(TransaccionLineasBillrefPK transaccionLineasBillrefPK) {
        this.transaccionLineasBillrefPK = transaccionLineasBillrefPK;
    }

    public TransaccionLineasBillref(String fEId, int nroOrden, int lineId) {
        this.transaccionLineasBillrefPK = new TransaccionLineasBillrefPK(fEId, nroOrden, lineId);
    }

    public TransaccionLineasBillrefPK getTransaccionLineasBillrefPK() {
        return transaccionLineasBillrefPK;
    }

    public void setTransaccionLineasBillrefPK(TransaccionLineasBillrefPK transaccionLineasBillrefPK) {
        this.transaccionLineasBillrefPK = transaccionLineasBillrefPK;
    }

    public String getAdtDocRefId() {
        return adtDocRefId;
    }

    public void setAdtDocRefId(String adtDocRefId) {
        this.adtDocRefId = adtDocRefId;
    }

    public String getAdtDocRefSchemaId() {
        return adtDocRefSchemaId;
    }

    public void setAdtDocRefSchemaId(String adtDocRefSchemaId) {
        this.adtDocRefSchemaId = adtDocRefSchemaId;
    }

    public String getInvDocRefDocTypeCode() {
        return invDocRefDocTypeCode;
    }

    public void setInvDocRefDocTypeCode(String invDocRefDocTypeCode) {
        this.invDocRefDocTypeCode = invDocRefDocTypeCode;
    }

    public String getInvDocRefId() {
        return invDocRefId;
    }

    public void setInvDocRefId(String invDocRefId) {
        this.invDocRefId = invDocRefId;
    }

    public TransaccionLineas getTransaccionLineas() {
        return transaccionLineas;
    }

    public void setTransaccionLineas(TransaccionLineas transaccionLineas) {
        this.transaccionLineas = transaccionLineas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineasBillrefPK != null ? transaccionLineasBillrefPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineasBillref)) {
            return false;
        }
        TransaccionLineasBillref other = (TransaccionLineasBillref) object;
        if ((this.transaccionLineasBillrefPK == null && other.transaccionLineasBillrefPK != null) || (this.transaccionLineasBillrefPK != null && !this.transaccionLineasBillrefPK.equals(other.transaccionLineasBillrefPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineasBillref[ transaccionLineasBillrefPK=" + transaccionLineasBillrefPK + " ]";
    }

}
