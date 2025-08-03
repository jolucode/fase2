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
 *
 * @author Percy
 */
@Entity
@Table(name = "TRANSACCION_ERROR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionError.findAll", query = "SELECT t FROM TransaccionError t"),
    @NamedQuery(name = "TransaccionError.findByFEId", query = "SELECT t FROM TransaccionError t WHERE t.fEId = :fEId"),
    @NamedQuery(name = "TransaccionError.findByDocentry", query = "SELECT t FROM TransaccionError t WHERE t.docentry = :docentry"),
    @NamedQuery(name = "TransaccionError.findByFEObjectType", query = "SELECT t FROM TransaccionError t WHERE t.fEObjectType = :fEObjectType"),
    @NamedQuery(name = "TransaccionError.findByFETipoTrans", query = "SELECT t FROM TransaccionError t WHERE t.fETipoTrans = :fETipoTrans"),
    @NamedQuery(name = "TransaccionError.findByErrCodigo", query = "SELECT t FROM TransaccionError t WHERE t.errCodigo = :errCodigo"),
    @NamedQuery(name = "TransaccionError.findByErrMensaje", query = "SELECT t FROM TransaccionError t WHERE t.errMensaje = :errMensaje")})
public class TransaccionError implements Serializable {
    @Column(name = "Docnum")
    private Integer docnum;
    @Column(name = "FE_FormSAP")
    private String fEFormSAP;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Column(name = "Docentry")
    private Integer docentry;
    @Column(name = "FE_ObjectType")
    private String fEObjectType;
    @Column(name = "FE_TipoTrans")
    private String fETipoTrans;
    @Column(name = "Err_Codigo")
    private Integer errCodigo;
    @Column(name = "Err_Mensaje")
    private String errMensaje;

    public TransaccionError() {
    }

    public TransaccionError(String fEId) {
        this.fEId = fEId;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public Integer getDocentry() {
        return docentry;
    }

    public void setDocentry(Integer docentry) {
        this.docentry = docentry;
    }

    public String getFEObjectType() {
        return fEObjectType;
    }

    public void setFEObjectType(String fEObjectType) {
        this.fEObjectType = fEObjectType;
    }

    public String getFETipoTrans() {
        return fETipoTrans;
    }

    public void setFETipoTrans(String fETipoTrans) {
        this.fETipoTrans = fETipoTrans;
    }

    public Integer getErrCodigo() {
        return errCodigo;
    }

    public void setErrCodigo(Integer errCodigo) {
        this.errCodigo = errCodigo;
    }

    public String getErrMensaje() {
        return errMensaje;
    }

    public void setErrMensaje(String errMensaje) {
        this.errMensaje = errMensaje;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEId != null ? fEId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionError)) {
            return false;
        }
        TransaccionError other = (TransaccionError) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionError[ fEId=" + fEId + " ]";
    }

    public Integer getDocnum() {
        return docnum;
    }

    public void setDocnum(Integer docnum) {
        this.docnum = docnum;
    }

    public String getFEFormSAP() {
        return fEFormSAP;
    }

    public void setFEFormSAP(String fEFormSAP) {
        this.fEFormSAP = fEFormSAP;
    }
    
}
