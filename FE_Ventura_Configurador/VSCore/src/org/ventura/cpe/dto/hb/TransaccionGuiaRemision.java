/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_GUIA_REMISION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionGuiaRemision.findAll", query = "SELECT t FROM TransaccionGuiaRemision t"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByFEId", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.fEId = :fEId"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByCodigoPuerto", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.codigoPuerto = :codigoPuerto"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByNumeroContenedor", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.numeroContenedor = :numeroContenedor"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByTipoDocConductor", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.tipoDocConductor = :tipoDocConductor"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByDocumentoConductor", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.documentoConductor = :documentoConductor"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByPlacaVehiculo", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.placaVehiculo = :placaVehiculo"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByRUCTransporista", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.rUCTransporista = :rUCTransporista"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByTipoDOCTransportista", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.tipoDOCTransportista = :tipoDOCTransportista"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByNombreRazonTransportista", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.nombreRazonTransportista = :nombreRazonTransportista"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByFechaInicioTraslado", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.fechaInicioTraslado = :fechaInicioTraslado"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByModalidadTraslado", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.modalidadTraslado = :modalidadTraslado"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByNumeroBultos", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.numeroBultos = :numeroBultos"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByUnidadMedida", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.unidadMedida = :unidadMedida"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByPeso", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.peso = :peso"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByIndicadorTransbordoProgramado", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.indicadorTransbordoProgramado = :indicadorTransbordoProgramado"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByCodigoMotivo", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.codigoMotivo = :codigoMotivo"),
    @NamedQuery(name = "TransaccionGuiaRemision.findByDescripcionMotivo", query = "SELECT t FROM TransaccionGuiaRemision t WHERE t.descripcionMotivo = :descripcionMotivo")})
public class TransaccionGuiaRemision implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;
    @Column(name = "CodigoPuerto")
    private String codigoPuerto;
    @Column(name = "NumeroContenedor")
    private String numeroContenedor;
    @Column(name = "TipoDocConductor")
    private String tipoDocConductor;
    @Column(name = "DocumentoConductor")
    private String documentoConductor;
    @Column(name = "PlacaVehiculo")
    private String placaVehiculo;
    @Column(name = "RUCTransporista")
    private String rUCTransporista;
    @Column(name = "TipoDOCTransportista")
    private String tipoDOCTransportista;
    @Column(name = "NombreRazonTransportista")
    private String nombreRazonTransportista;
    @Column(name = "FechaInicioTraslado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicioTraslado;
    @Column(name = "ModalidadTraslado")
    private String modalidadTraslado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "NumeroBultos")
    private BigDecimal numeroBultos;
    @Column(name = "UnidadMedida")
    private String unidadMedida;
    @Column(name = "Peso")
    private BigDecimal peso;
    @Column(name = "IndicadorTransbordoProgramado")
    private String indicadorTransbordoProgramado;
    @Column(name = "CodigoMotivo")
    private String codigoMotivo;
    @Column(name = "DescripcionMotivo")
    private String descripcionMotivo;
    @Column(name = "LicenciaConducir")
    private String licenciaConducir;
    @Column(name = "DireccionPartida")
    private String direccionPartida;
    @Column(name = "UbigeoPartida")
    private String ubigeoPartida;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionGuiaRemision() {
    }

    public TransaccionGuiaRemision(String fEId) {
        this.fEId = fEId;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public String getCodigoPuerto() {
        return codigoPuerto;
    }

    public void setCodigoPuerto(String codigoPuerto) {
        this.codigoPuerto = codigoPuerto;
    }

    public String getNumeroContenedor() {
        return numeroContenedor;
    }

    public void setNumeroContenedor(String numeroContenedor) {
        this.numeroContenedor = numeroContenedor;
    }

    public String getTipoDocConductor() {
        return tipoDocConductor;
    }

    public void setTipoDocConductor(String tipoDocConductor) {
        this.tipoDocConductor = tipoDocConductor;
    }

    public String getDocumentoConductor() {
        return documentoConductor;
    }

    public void setDocumentoConductor(String documentoConductor) {
        this.documentoConductor = documentoConductor;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getRUCTransporista() {
        return rUCTransporista;
    }

    public void setRUCTransporista(String rUCTransporista) {
        this.rUCTransporista = rUCTransporista;
    }

    public String getTipoDOCTransportista() {
        return tipoDOCTransportista;
    }

    public void setTipoDOCTransportista(String tipoDOCTransportista) {
        this.tipoDOCTransportista = tipoDOCTransportista;
    }

    public String getNombreRazonTransportista() {
        return nombreRazonTransportista;
    }

    public void setNombreRazonTransportista(String nombreRazonTransportista) {
        this.nombreRazonTransportista = nombreRazonTransportista;
    }

    public Date getFechaInicioTraslado() {
        return fechaInicioTraslado;
    }

    public void setFechaInicioTraslado(Date fechaInicioTraslado) {
        this.fechaInicioTraslado = fechaInicioTraslado;
    }

    public String getModalidadTraslado() {
        return modalidadTraslado;
    }

    public void setModalidadTraslado(String modalidadTraslado) {
        this.modalidadTraslado = modalidadTraslado;
    }

    public BigDecimal getNumeroBultos() {
        return numeroBultos;
    }

    public void setNumeroBultos(BigDecimal numeroBultos) {
        this.numeroBultos = numeroBultos;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getIndicadorTransbordoProgramado() {
        return indicadorTransbordoProgramado;
    }

    public void setIndicadorTransbordoProgramado(String indicadorTransbordoProgramado) {
        this.indicadorTransbordoProgramado = indicadorTransbordoProgramado;
    }

    public String getCodigoMotivo() {
        return codigoMotivo;
    }

    public void setCodigoMotivo(String codigoMotivo) {
        this.codigoMotivo = codigoMotivo;
    }

    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }

    public void setDescripcionMotivo(String descripcionMotivo) {
        this.descripcionMotivo = descripcionMotivo;
    }

    public String getLicenciaConducir() {
        return licenciaConducir;
    }

    public void setLicenciaConducir(String licenciaConducir) {
        this.licenciaConducir = licenciaConducir;
    }

    public String getDireccionPartida() {
        return direccionPartida;
    }

    public void setDireccionPartida(String direccionPartida) {
        this.direccionPartida = direccionPartida;
    }

    public String getUbigeoPartida() {
        return ubigeoPartida;
    }

    public void setUbigeoPartida(String ubigeoPartida) {
        this.ubigeoPartida = ubigeoPartida;
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
        hash += (fEId != null ? fEId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionGuiaRemision)) {
            return false;
        }
        TransaccionGuiaRemision other = (TransaccionGuiaRemision) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionGuiaRemision[ fEId=" + fEId + " ]";
    }
    
}
