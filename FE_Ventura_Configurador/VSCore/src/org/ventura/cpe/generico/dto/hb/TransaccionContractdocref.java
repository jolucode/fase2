/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_CONTRACTDOCREF")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionContractdocref.findAll", query = "SELECT t FROM TransaccionContractdocref t"),
    @NamedQuery(name = "TransaccionContractdocref.findByFEId", query = "SELECT t FROM TransaccionContractdocref t WHERE t.transaccionContractdocrefPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionContractdocref.findByIdCampoUsuario", query = "SELECT t FROM TransaccionContractdocref t WHERE t.transaccionContractdocrefPK.idCampoUsuario = :idCampoUsuario"),
    @NamedQuery(name = "TransaccionContractdocref.findByValor", query = "SELECT t FROM TransaccionContractdocref t WHERE t.valor = :valor")})
public class TransaccionContractdocref implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionContractdocrefPK transaccionContractdocrefPK;
    @Column(name = "Valor")
    private String valor;
    @Column(name = "NombreCampo")
    private String nombreCampo;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionContractdocref() {
    }

    public TransaccionContractdocref(TransaccionContractdocrefPK transaccionContractdocrefPK) {
        this.transaccionContractdocrefPK = transaccionContractdocrefPK;
    }

    public TransaccionContractdocref(String fEId, int idCampoUsuario) {
        this.transaccionContractdocrefPK = new TransaccionContractdocrefPK(fEId, idCampoUsuario);
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
        return "org.ventura.cpe.generico.dto.hb.TransaccionContractdocref[ transaccionContractdocrefPK=" + transaccionContractdocrefPK + " ]";
    }
    
    
    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }
}
