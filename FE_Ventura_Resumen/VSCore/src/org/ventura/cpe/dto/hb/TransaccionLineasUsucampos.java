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
@Table(name = "TRANSACCION_LINEAS_USUCAMPOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionLineasUsucampos.findAll", query = "SELECT t FROM TransaccionLineasUsucampos t"),
    @NamedQuery(name = "TransaccionLineasUsucampos.findByFEId", query = "SELECT t FROM TransaccionLineasUsucampos t WHERE t.transaccionLineasUsucamposPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionLineasUsucampos.findByNroOrden", query = "SELECT t FROM TransaccionLineasUsucampos t WHERE t.transaccionLineasUsucamposPK.nroOrden = :nroOrden"),
    @NamedQuery(name = "TransaccionLineasUsucampos.findByUSUCMPId", query = "SELECT t FROM TransaccionLineasUsucampos t WHERE t.transaccionLineasUsucamposPK.uSUCMPId = :uSUCMPId"),
    @NamedQuery(name = "TransaccionLineasUsucampos.findByValor", query = "SELECT t FROM TransaccionLineasUsucampos t WHERE t.valor = :valor")})
public class TransaccionLineasUsucampos implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionLineasUsucamposPK transaccionLineasUsucamposPK;
    @Column(name = "Valor")
    private String valor;
    @JoinColumn(name = "USUCMP_Id", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuariocampos usuariocampos;
    @JoinColumns({
        @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
        @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionLineas transaccionLineas;

    public TransaccionLineasUsucampos() {
    }

    public TransaccionLineasUsucampos(TransaccionLineasUsucamposPK transaccionLineasUsucamposPK) {
        this.transaccionLineasUsucamposPK = transaccionLineasUsucamposPK;
    }

    public TransaccionLineasUsucampos(String fEId, int nroOrden, int uSUCMPId) {
        this.transaccionLineasUsucamposPK = new TransaccionLineasUsucamposPK(fEId, nroOrden, uSUCMPId);
    }

    public TransaccionLineasUsucamposPK getTransaccionLineasUsucamposPK() {
        return transaccionLineasUsucamposPK;
    }

    public void setTransaccionLineasUsucamposPK(TransaccionLineasUsucamposPK transaccionLineasUsucamposPK) {
        this.transaccionLineasUsucamposPK = transaccionLineasUsucamposPK;
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

    public TransaccionLineas getTransaccionLineas() {
        return transaccionLineas;
    }

    public void setTransaccionLineas(TransaccionLineas transaccionLineas) {
        this.transaccionLineas = transaccionLineas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineasUsucamposPK != null ? transaccionLineasUsucamposPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineasUsucampos)) {
            return false;
        }
        TransaccionLineasUsucampos other = (TransaccionLineasUsucampos) object;
        if ((this.transaccionLineasUsucamposPK == null && other.transaccionLineasUsucamposPK != null) || (this.transaccionLineasUsucamposPK != null && !this.transaccionLineasUsucamposPK.equals(other.transaccionLineasUsucamposPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineasUsucampos[ transaccionLineasUsucamposPK=" + transaccionLineasUsucamposPK + " ]";
    }
    
}
