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

    //GUIAS SUNAT REST
    @Column(name = "TipoDocRelacionadoTrans")
    private String tipoDocRelacionadoTrans;

    @Column(name = "TipoDocRelacionadoTransDesc")
    private String tipoDocRelacionadoTransDesc;

    @Column(name = "DocumentoRelacionadoTrans")
    private String documentoRelacionadoTrans;

    @Column(name = "IndicadorTransbordo")
    private String indicadorTransbordo;

    @Column(name = "IndicadorTraslado")
    private String indicadorTraslado;

    @Column(name = "IndicadorRetorno")
    private String  indicadorRetorno;

    @Column(name = "IndicadorRetornoVehiculo")
    private String indicadorRetornoVehiculo;

    @Column(name = "IndicadorTrasladoTotal")
    private String indicadorTrasladoTotal;

    @Column(name = "IndicadorRegistro")
    private String indicadorRegistro;

    @Column(name = "NroRegistroMTC")
    private String nroRegistroMTC;

    @Column(name = "NombreApellidosConductor")
    private String nombreApellidosConductor;

    @Column(name = "UbigeoLlegada")
    private String ubigeoLlegada;

    @Column(name = "DireccionLlegada")
    private String direccionLlegada;

    @Column(name = "TarjetaCirculacion")
    private String tarjetaCirculacion;

    @Column(name = "numeroPrecinto")
    private String numeroPrecinto;

    @Column(name = "numeroContenedor2")
    private String numeroContenedor2;

    @Column(name = "numeroPrecinto2")
    private String numeroPrecinto2;

    @Column(name = "DescripcionPuerto")
    private String descripcionPuerto;

    @Column(name = "CodigoAereopuerto")
    private String codigoAereopuerto;

    @Column(name = "DescripcionAereopuerto")
    private String descripcionAereopuerto;

    /** Harol 29-03-2024 Guia Transportista*/
    @Column(name = "GRT_TipoDocRemitente")
    private String GRT_TipoDocRemitente;

    @Column(name = "GRT_DocumentoRemitente")
    private String GRT_DocumentoRemitente;

    @Column(name = "GRT_NombreRazonRemitente")
    private String GRT_NombreRazonRemitente;

    @Column(name = "GRT_IndicadorPagadorFlete")
    private String GRT_IndicadorPagadorFlete;

    @Column(name = "GRT_TipoDocDestinatario")
    private String GRT_TipoDocDestinatario;

    @Column(name = "GRT_DocumentoDestinatario")
    private String GRT_DocumentoDestinatario;

    @Column(name = "GRT_NombreRazonDestinatario")
    private String GRT_NombreRazonDestinatario;

    @Column(name = "GRT_NumeroTUCEPrincipal")
    private String GRT_NumeroTUCEPrincipal;

    @Column(name = "GRT_EntidadEmisoraPrincipal")
    private String GRT_EntidadEmisoraPrincipal;

    @Column(name = "GRT_PlacaVehiculoSecundario")
    private String GRT_PlacaVehiculoSecundario;

    @Column(name = "GRT_NumeroTUCESecuendario")
    private String GRT_NumeroTUCESecuendario;

    @Column(name = "GRT_EntidadEmisoraSecundario")
    private String GRT_EntidadEmisoraSecundario;
    /** */

    @Column(name = "TicketRest")
    private String TicketRest;


    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionGuiaRemision() {
    }

    public String getTicketRest() {
        return TicketRest;
    }

    public void setTicketRest(String ticketRest) {
        TicketRest = ticketRest;
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

    public String getfEId() {
        return fEId;
    }

    public void setfEId(String fEId) {
        this.fEId = fEId;
    }

    public String getrUCTransporista() {
        return rUCTransporista;
    }

    public void setrUCTransporista(String rUCTransporista) {
        this.rUCTransporista = rUCTransporista;
    }

    public String getTipoDocRelacionadoTrans() {
        return tipoDocRelacionadoTrans;
    }

    public void setTipoDocRelacionadoTrans(String tipoDocRelacionadoTrans) {
        this.tipoDocRelacionadoTrans = tipoDocRelacionadoTrans;
    }

    public String getTipoDocRelacionadoTransDesc() {
        return tipoDocRelacionadoTransDesc;
    }

    public void setTipoDocRelacionadoTransDesc(String tipoDocRelacionadoTransDesc) {
        this.tipoDocRelacionadoTransDesc = tipoDocRelacionadoTransDesc;
    }

    public String getDocumentoRelacionadoTrans() {
        return documentoRelacionadoTrans;
    }

    public void setDocumentoRelacionadoTrans(String documentoRelacionadoTrans) {
        this.documentoRelacionadoTrans = documentoRelacionadoTrans;
    }

    public String getIndicadorTransbordo() {
        return indicadorTransbordo;
    }

    public void setIndicadorTransbordo(String indicadorTransbordo) {
        this.indicadorTransbordo = indicadorTransbordo;
    }

    public String getIndicadorTraslado() {
        return indicadorTraslado;
    }

    public void setIndicadorTraslado(String indicadorTraslado) {
        this.indicadorTraslado = indicadorTraslado;
    }

    public String getIndicadorRetorno() {
        return indicadorRetorno;
    }

    public void setIndicadorRetorno(String indicadorRetorno) {
        this.indicadorRetorno = indicadorRetorno;
    }

    public String getIndicadorRetornoVehiculo() {
        return indicadorRetornoVehiculo;
    }

    public void setIndicadorRetornoVehiculo(String indicadorRetornoVehiculo) {
        this.indicadorRetornoVehiculo = indicadorRetornoVehiculo;
    }

    public String getIndicadorTrasladoTotal() {
        return indicadorTrasladoTotal;
    }

    public void setIndicadorTrasladoTotal(String indicadorTrasladoTotal) {
        this.indicadorTrasladoTotal = indicadorTrasladoTotal;
    }

    public String getIndicadorRegistro() {
        return indicadorRegistro;
    }

    public void setIndicadorRegistro(String indicadorRegistro) {
        this.indicadorRegistro = indicadorRegistro;
    }

    public String getNroRegistroMTC() {
        return nroRegistroMTC;
    }

    public void setNroRegistroMTC(String nroRegistroMTC) {
        this.nroRegistroMTC = nroRegistroMTC;
    }

    public String getNombreApellidosConductor() {
        return nombreApellidosConductor;
    }

    public void setNombreApellidosConductor(String nombreApellidosConductor) {
        this.nombreApellidosConductor = nombreApellidosConductor;
    }

    public String getUbigeoLlegada() {
        return ubigeoLlegada;
    }

    public void setUbigeoLlegada(String ubigeoLlegada) {
        this.ubigeoLlegada = ubigeoLlegada;
    }

    public String getDireccionLlegada() {
        return direccionLlegada;
    }

    public void setDireccionLlegada(String direccionLlegada) {
        this.direccionLlegada = direccionLlegada;
    }

    public String getTarjetaCirculacion() {
        return tarjetaCirculacion;
    }

    public void setTarjetaCirculacion(String tarjetaCirculacion) {
        this.tarjetaCirculacion = tarjetaCirculacion;
    }

    public String getNumeroPrecinto() {
        return numeroPrecinto;
    }

    public void setNumeroPrecinto(String numeroPrecinto) {
        this.numeroPrecinto = numeroPrecinto;
    }

    public String getNumeroContenedor2() {
        return numeroContenedor2;
    }

    public void setNumeroContenedor2(String numeroContenedor2) {
        this.numeroContenedor2 = numeroContenedor2;
    }

    public String getNumeroPrecinto2() {
        return numeroPrecinto2;
    }

    public void setNumeroPrecinto2(String numeroPrecinto2) {
        this.numeroPrecinto2 = numeroPrecinto2;
    }

    public String getDescripcionPuerto() {
        return descripcionPuerto;
    }

    public void setDescripcionPuerto(String descripcionPuerto) {
        this.descripcionPuerto = descripcionPuerto;
    }

    public String getCodigoAereopuerto() {
        return codigoAereopuerto;
    }

    public void setCodigoAereopuerto(String codigoAereopuerto) {
        this.codigoAereopuerto = codigoAereopuerto;
    }

    public String getDescripcionAereopuerto() {
        return descripcionAereopuerto;
    }

    public void setDescripcionAereopuerto(String descripcionAereopuerto) {
        this.descripcionAereopuerto = descripcionAereopuerto;
    }
    /** Harol 29-03-2024 Guia Transportista*/
    public String getGRT_TipoDocRemitente() {
        return GRT_TipoDocRemitente;
    }

    public void setGRT_TipoDocRemitente(String GRT_TipoDocRemitente) {
        this.GRT_TipoDocRemitente = GRT_TipoDocRemitente;
    }

    public String getGRT_DocumentoRemitente() {
        return GRT_DocumentoRemitente;
    }

    public void setGRT_DocumentoRemitente(String GRT_DocumentoRemitente) {
        this.GRT_DocumentoRemitente = GRT_DocumentoRemitente;
    }

    public String getGRT_NombreRazonRemitente() {
        return GRT_NombreRazonRemitente;
    }

    public void setGRT_NombreRazonRemitente(String GRT_NombreRazonRemitente) {
        this.GRT_NombreRazonRemitente = GRT_NombreRazonRemitente;
    }

    public String getGRT_IndicadorPagadorFlete() {
        return GRT_IndicadorPagadorFlete;
    }

    public void setGRT_IndicadorPagadorFlete(String GRT_IndicadorPagadorFlete) {
        this.GRT_IndicadorPagadorFlete = GRT_IndicadorPagadorFlete;
    }

    public String getGRT_TipoDocDestinatario() {
        return GRT_TipoDocDestinatario;
    }

    public void setGRT_TipoDocDestinatario(String GRT_TipoDocDestinatario) {
        this.GRT_TipoDocDestinatario = GRT_TipoDocDestinatario;
    }

    public String getGRT_DocumentoDestinatario() {
        return GRT_DocumentoDestinatario;
    }

    public void setGRT_DocumentoDestinatario(String GRT_DocumentoDestinatario) {
        this.GRT_DocumentoDestinatario = GRT_DocumentoDestinatario;
    }

    public String getGRT_NombreRazonDestinatario() {
        return GRT_NombreRazonDestinatario;
    }

    public void setGRT_NombreRazonDestinatario(String GRT_NombreRazonDestinatario) {
        this.GRT_NombreRazonDestinatario = GRT_NombreRazonDestinatario;
    }

    public String getGRT_NumeroTUCEPrincipal() {
        return GRT_NumeroTUCEPrincipal;
    }

    public void setGRT_NumeroTUCEPrincipal(String GRT_NumeroTUCEPrincipal) {
        this.GRT_NumeroTUCEPrincipal = GRT_NumeroTUCEPrincipal;
    }

    public String getGRT_EntidadEmisoraPrincipal() {
        return GRT_EntidadEmisoraPrincipal;
    }

    public void setGRT_EntidadEmisoraPrincipal(String GRT_EntidadEmisoraPrincipal) {
        this.GRT_EntidadEmisoraPrincipal = GRT_EntidadEmisoraPrincipal;
    }

    public String getGRT_PlacaVehiculoSecundario() {
        return GRT_PlacaVehiculoSecundario;
    }

    public void setGRT_PlacaVehiculoSecundario(String GRT_PlacaVehiculoSecundario) {
        this.GRT_PlacaVehiculoSecundario = GRT_PlacaVehiculoSecundario;
    }

    public String getGRT_NumeroTUCESecuendario() {
        return GRT_NumeroTUCESecuendario;
    }

    public void setGRT_NumeroTUCESecuendario(String GRT_NumeroTUCESecuendario) {
        this.GRT_NumeroTUCESecuendario = GRT_NumeroTUCESecuendario;
    }

    public String getGRT_EntidadEmisoraSecundario() {
        return GRT_EntidadEmisoraSecundario;
    }

    public void setGRT_EntidadEmisoraSecundario(String GRT_EntidadEmisoraSecundario) {
        this.GRT_EntidadEmisoraSecundario = GRT_EntidadEmisoraSecundario;
    }
    /** */
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
