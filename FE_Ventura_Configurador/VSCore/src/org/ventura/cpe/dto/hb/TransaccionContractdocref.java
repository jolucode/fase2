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
@Table(name = "TRANSACCION_CONTRACTDOCREF")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionContractdocref.findAll", query = "SELECT t FROM TransaccionContractdocref t"),
    @NamedQuery(name = "TransaccionContractdocref.findByFEId", query = "SELECT t FROM TransaccionContractdocref t WHERE t.transaccionContractdocrefPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionContractdocref.findByUSUCMPId", query = "SELECT t FROM TransaccionContractdocref t WHERE t.transaccionContractdocrefPK.uSUCMPId = :uSUCMPId"),
    @NamedQuery(name = "TransaccionContractdocref.findByValor", query = "SELECT t FROM TransaccionContractdocref t WHERE t.valor = :valor")})
public class TransaccionContractdocref implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionContractdocrefPK transaccionContractdocrefPK;
    @Column(name = "Valor")
    private String valor;
    @JoinColumn(name = "USUCMP_Id", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuariocampos usuariocampos;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionContractdocref() {
    }

    public TransaccionContractdocref(TransaccionContractdocrefPK transaccionContractdocrefPK) {
        this.transaccionContractdocrefPK = transaccionContractdocrefPK;
    }

    public TransaccionContractdocref(String fEId, int uSUCMPId) {
        this.transaccionContractdocrefPK = new TransaccionContractdocrefPK(fEId, uSUCMPId);
    }

    public TransaccionContractdocrefPK getTransaccionContractdocrefPK() {
        return transaccionContractdocrefPK;
    }

    public void setTransaccionContractdocrefPK(TransaccionContractdocrefPK transaccionContractdocrefPK) {
        this.transaccionContractdocrefPK = transaccionContractdocrefPK;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Usuariocampos getUsuariocampos() {
        return usuariocampos;
    }

    public void setUsuariocampos(Usuariocampos usuariocampos) {
        this.usuariocampos = usuariocampos;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionContractdocrefPK != null ? transaccionContractdocrefPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionContractdocref)) {
            return false;
        }
        TransaccionContractdocref other = (TransaccionContractdocref) object;
        if ((this.transaccionContractdocrefPK == null && other.transaccionContractdocrefPK != null) || (this.transaccionContractdocrefPK != null && !this.transaccionContractdocrefPK.equals(other.transaccionContractdocrefPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionContractdocref[ transaccionContractdocrefPK=" + transaccionContractdocrefPK + " ]";
    }
    
}
