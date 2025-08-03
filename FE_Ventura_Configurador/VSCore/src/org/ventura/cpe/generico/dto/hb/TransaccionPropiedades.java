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
@Table(name = "TRANSACCION_PROPIEDADES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionPropiedades.findAll", query = "SELECT t FROM TransaccionPropiedades t"),
    @NamedQuery(name = "TransaccionPropiedades.findByFEId", query = "SELECT t FROM TransaccionPropiedades t WHERE t.transaccionPropiedadesPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionPropiedades.findById", query = "SELECT t FROM TransaccionPropiedades t WHERE t.transaccionPropiedadesPK.id = :id"),
    @NamedQuery(name = "TransaccionPropiedades.findByValor", query = "SELECT t FROM TransaccionPropiedades t WHERE t.valor = :valor")})
public class TransaccionPropiedades implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionPropiedadesPK transaccionPropiedadesPK;
    @Column(name = "Valor")
    private String valor;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionPropiedades() {
    }

    public TransaccionPropiedades(TransaccionPropiedadesPK transaccionPropiedadesPK) {
        this.transaccionPropiedadesPK = transaccionPropiedadesPK;
    }

    public TransaccionPropiedades(String fEId, String id) {
        this.transaccionPropiedadesPK = new TransaccionPropiedadesPK(fEId, id);
    }

    public TransaccionPropiedadesPK getTransaccionPropiedadesPK() {
        return transaccionPropiedadesPK;
    }

    public void setTransaccionPropiedadesPK(TransaccionPropiedadesPK transaccionPropiedadesPK) {
        this.transaccionPropiedadesPK = transaccionPropiedadesPK;
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
        hash += (transaccionPropiedadesPK != null ? transaccionPropiedadesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionPropiedades)) {
            return false;
        }
        TransaccionPropiedades other = (TransaccionPropiedades) object;
        if ((this.transaccionPropiedadesPK == null && other.transaccionPropiedadesPK != null) || (this.transaccionPropiedadesPK != null && !this.transaccionPropiedadesPK.equals(other.transaccionPropiedadesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionPropiedades[ transaccionPropiedadesPK=" + transaccionPropiedadesPK + " ]";
    }
    
}
