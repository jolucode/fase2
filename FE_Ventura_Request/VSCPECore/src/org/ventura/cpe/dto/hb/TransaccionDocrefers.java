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
@Table(name = "TRANSACCION_DOCREFERS")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionDocrefers.findAll", query = "SELECT t FROM TransaccionDocrefers t"),
        @NamedQuery(name = "TransaccionDocrefers.findByFEId", query = "SELECT t FROM TransaccionDocrefers t WHERE t.transaccionDocrefersPK.fEId = :fEId"),
        @NamedQuery(name = "TransaccionDocrefers.findByLineId", query = "SELECT t FROM TransaccionDocrefers t WHERE t.transaccionDocrefersPK.lineId = :lineId"),
        @NamedQuery(name = "TransaccionDocrefers.findByTipo", query = "SELECT t FROM TransaccionDocrefers t WHERE t.tipo = :tipo"),
        @NamedQuery(name = "TransaccionDocrefers.findById", query = "SELECT t FROM TransaccionDocrefers t WHERE t.id = :id")})
public class TransaccionDocrefers implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionDocrefersPK transaccionDocrefersPK;

    @Column(name = "Tipo")
    private String tipo;

    @Column(name = "Id")
    private String id;

    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionDocrefers() {
    }

    public TransaccionDocrefers(TransaccionDocrefersPK transaccionDocrefersPK) {
        this.transaccionDocrefersPK = transaccionDocrefersPK;
    }

    public TransaccionDocrefers(String fEId, int lineId) {
        this.transaccionDocrefersPK = new TransaccionDocrefersPK(fEId, lineId);
    }

    public TransaccionDocrefersPK getTransaccionDocrefersPK() {
        return transaccionDocrefersPK;
    }

    public void setTransaccionDocrefersPK(TransaccionDocrefersPK transaccionDocrefersPK) {
        this.transaccionDocrefersPK = transaccionDocrefersPK;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        hash += (transaccionDocrefersPK != null ? transaccionDocrefersPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionDocrefers)) {
            return false;
        }
        TransaccionDocrefers other = (TransaccionDocrefers) object;
        if ((this.transaccionDocrefersPK == null && other.transaccionDocrefersPK != null) || (this.transaccionDocrefersPK != null && !this.transaccionDocrefersPK.equals(other.transaccionDocrefersPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionDocrefers[ transaccionDocrefersPK=" + transaccionDocrefersPK + " ]";
    }

}
