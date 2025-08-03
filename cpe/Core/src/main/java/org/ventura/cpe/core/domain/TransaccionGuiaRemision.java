/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.domain;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Yosmel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "TRANSACCION_GUIA_REMISION")
@XmlRootElement
public class TransaccionGuiaRemision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NonNull
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
    private String TipoDocRelacionadoTrans;

    @Column(name = "TipoDocRelacionadoTransDesc")
    private String TipoDocRelacionadoTransDesc;

    @Column(name = "DocumentoRelacionadoTrans")
    private String DocumentoRelacionadoTrans;

    @Column(name = "IndicadorTransbordo")
    private String IndicadorTransbordo;

    @Column(name = "IndicadorTraslado")
    private String IndicadorTraslado;

    @Column(name = "IndicadorRetorno")
    private String IndicadorRetorno;

    @Column(name = "IndicadorRetornoVehiculo")
    private String IndicadorRetornoVehiculo;

    @Column(name = "IndicadorTrasladoTotal")
    private String IndicadorTrasladoTotal;

    @Column(name = "IndicadorRegistro")
    private String IndicadorRegistro;

    @Column(name = "NroRegistroMTC")
    private String NroRegistroMTC;

    @Column(name = "NombreApellidosConductor")
    private String NombreApellidosConductor;

    @Column(name = "UbigeoLlegada")
    private String UbigeoLlegada;

    @Column(name = "DireccionLlegada")
    private String DireccionLlegada;

    @Column(name = "TarjetaCirculacion")
    private String TarjetaCirculacion;

    @Column(name = "numeroPrecinto")
    private String numeroPrecinto;

    @Column(name = "numeroContenedor2")
    private String numeroContenedor2;

    @Column(name = "numeroPrecinto2")
    private String numeroPrecinto2;

    @Column(name = "DescripcionPuerto")
    private String DescripcionPuerto;

    @Column(name = "CodigoAereopuerto")
    private String CodigoAereopuerto;

    @Column(name = "DescripcionAereopuerto")
    private String DescripcionAereopuerto;
    /** Harol 29-03-2024 Entidades guia transportista*/
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

    @Override
    public String toString() {
        return "TransaccionGuiaRemision[ fEId=" + fEId + " ]";
    }

}
