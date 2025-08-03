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
@Table(name = "TRANSACCION_USUCAMPOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionUsucampos.findAll", query = "SELECT t FROM TransaccionUsucampos t"),
    @NamedQuery(name = "TransaccionUsucampos.findByFEId", query = "SELECT t FROM TransaccionUsucampos t WHERE t.transaccionUsucamposPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionUsucampos.findByUSUCMPId", query = "SELECT t FROM TransaccionUsucampos t WHERE t.transaccionUsucamposPK.uSUCMPId = :uSUCMPId"),
    @NamedQuery(name = "TransaccionUsucampos.findByValor", query = "SELECT t FROM TransaccionUsucampos t WHERE t.valor = :valor")})
public class TransaccionUsucampos implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionUsucamposPK transaccionUsucamposPK;
    @Column(name = "Valor")
    private String valor;
    @JoinColumn(name = "USUCMP_Id", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuariocampos usuariocampos;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionUsucampos() {
    }

    public TransaccionUsucampos(TransaccionUsucamposPK transaccionUsucamposPK) {
        this.transaccionUsucamposPK = transaccionUsucamposPK;
    }

    public TransaccionUsucampos(String fEId, int uSUCMPId) {
        this.transaccionUsucamposPK = new TransaccionUsucamposPK(fEId, uSUCMPId);
    }

    public TransaccionUsucamposPK getTransaccionUsucamposPK() {
        return transaccionUsucamposPK;
    }

    public void setTransaccionUsucamposPK(TransaccionUsucamposPK transaccionUsucamposPK) {
        this.transaccionUsucamposPK = transaccionUsucamposPK;
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
        hash += (transaccionUsucamposPK != null ? transaccionUsucamposPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionUsucampos)) {
            return false;
        }
        TransaccionUsucampos other = (TransaccionUsucampos) object;
        if ((this.transaccionUsucamposPK == null && other.transaccionUsucamposPK != null) || (this.transaccionUsucamposPK != null && !this.transaccionUsucamposPK.equals(other.transaccionUsucamposPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionUsucampos[ transaccionUsucamposPK=" + transaccionUsucamposPK + " ]";
    }
    
}
