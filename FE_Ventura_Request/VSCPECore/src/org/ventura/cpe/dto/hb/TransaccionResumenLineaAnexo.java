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
@Table(name = "TRANSACCION_RESUMEN_LINEA_ANEXO")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findAll", query = "SELECT t FROM TransaccionResumenLineaAnexo t"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByIdTransaccion", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.transaccionResumenLineaAnexoPK.idTransaccion = :idTransaccion"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByIdLinea", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.transaccionResumenLineaAnexoPK.idLinea = :idLinea"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByDocEntry", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.docEntry = :docEntry"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByTipoTransaccion", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.tipoTransaccion = :tipoTransaccion"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findBySn", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.sn = :sn"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByObjcType", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.objcType = :objcType"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByTipoDocumento", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.tipoDocumento = :tipoDocumento"),
        @NamedQuery(name = "TransaccionResumenLineaAnexo.findByTipoEstado", query = "SELECT t FROM TransaccionResumenLineaAnexo t WHERE t.tipoEstado = :tipoEstado")})
public class TransaccionResumenLineaAnexo implements Serializable {

    @Column(name = "Serie")
    private String serie;

    @Column(name = "Correlativo")
    private String correlativo;

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionResumenLineaAnexoPK transaccionResumenLineaAnexoPK;

    @Column(name = "DocEntry")
    private String docEntry;

    @Column(name = "TipoTransaccion")
    private String tipoTransaccion;

    @Column(name = "SN")
    private String sn;

    @Column(name = "ObjcType")
    private String objcType;

    @Column(name = "TipoDocumento")
    private String tipoDocumento;

    @Column(name = "TipoEstado")
    private String tipoEstado;

    @JoinColumn(name = "Id_Transaccion", referencedColumnName = "Id_Transaccion", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TransaccionResumen transaccionResumen;

    public TransaccionResumenLineaAnexo() {
    }

    public TransaccionResumenLineaAnexo(TransaccionResumenLineaAnexoPK transaccionResumenLineaAnexoPK) {
        this.transaccionResumenLineaAnexoPK = transaccionResumenLineaAnexoPK;
    }

    public TransaccionResumenLineaAnexo(String idTransaccion, int idLinea) {
        this.transaccionResumenLineaAnexoPK = new TransaccionResumenLineaAnexoPK(idTransaccion, idLinea);
    }

    public TransaccionResumenLineaAnexoPK getTransaccionResumenLineaAnexoPK() {
        return transaccionResumenLineaAnexoPK;
    }

    public void setTransaccionResumenLineaAnexoPK(TransaccionResumenLineaAnexoPK transaccionResumenLineaAnexoPK) {
        this.transaccionResumenLineaAnexoPK = transaccionResumenLineaAnexoPK;
    }

    public String getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(String docEntry) {
        this.docEntry = docEntry;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getObjcType() {
        return objcType;
    }

    public void setObjcType(String objcType) {
        this.objcType = objcType;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public TransaccionResumen getTransaccionResumen() {
        return transaccionResumen;
    }

    public void setTransaccionResumen(TransaccionResumen transaccionResumen) {
        this.transaccionResumen = transaccionResumen;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionResumenLineaAnexoPK != null ? transaccionResumenLineaAnexoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionResumenLineaAnexo)) {
            return false;
        }
        TransaccionResumenLineaAnexo other = (TransaccionResumenLineaAnexo) object;
        if ((this.transaccionResumenLineaAnexoPK == null && other.transaccionResumenLineaAnexoPK != null) || (this.transaccionResumenLineaAnexoPK != null && !this.transaccionResumenLineaAnexoPK.equals(other.transaccionResumenLineaAnexoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexo[ transaccionResumenLineaAnexoPK=" + transaccionResumenLineaAnexoPK + " ]";
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(String correlativo) {
        this.correlativo = correlativo;
    }

}
