/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Yosmel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "TRANSACCION")
@XmlRootElement
@Entity
public class Transaccion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Transient
    public static List<TransaccionUsucampos> propiedades = new ArrayList<>();

    @Id
    @NonNull
    @Column(name = "FE_Id")
    private String fEId;

    @Column(name = "TipoOperacionSunat")
    private String tipoOperacionSunat;

    @Column(name = "SN_DIR_Pais_Descripcion")
    private String sNDIRPaisDescripcion;

    @Column(name = "FechaDOCRef")
    private String fechaDOCRef;

    @Column(name = "FechaVenDOCRef")
    private String fechaVenDOCRef;

    @Column(name = "DocIdentidad_Nro")
    private String docIdentidadNro;

    @Column(name = "DocIdentidad_Tipo")
    private String docIdentidadTipo;

    @Column(name = "RazonSocial")
    private String razonSocial;

    @Column(name = "NombreComercial")
    private String nombreComercial;

    @Column(name = "PersonContacto")
    private String personContacto;

    @Column(name = "EMail")
    private String eMail;

    @Column(name = "Telefono")
    private String telefono;

    @Column(name = "Telefono_1")
    private String telefono1;

    @Column(name = "Web")
    private String web;

    @Column(name = "DIR_Pais")
    private String dIRPais;

    @Column(name = "DIR_Departamento")
    private String dIRDepartamento;

    @Column(name = "DIR_Provincia")
    private String dIRProvincia;

    @Column(name = "DIR_Distrito")
    private String dIRDistrito;

    @Column(name = "DIR_Direccion")
    private String dIRDireccion;

    @Column(name = "DIR_NomCalle")
    private String dIRNomCalle;

    @Column(name = "DIR_NroCasa")
    private String dIRNroCasa;

    @Column(name = "DIR_Ubigeo")
    private String dIRUbigeo;

    @Column(name = "DIR_Urbanizacion")
    private String dIRUrbanizacion;

    @Column(name = "SN_DocIdentidad_Nro")
    private String sNDocIdentidadNro;

    @Column(name = "SN_DocIdentidad_Tipo")
    private String sNDocIdentidadTipo;

    @Column(name = "SN_RazonSocial")
    private String sNRazonSocial;

    @Column(name = "SN_NombreComercial")
    private String sNNombreComercial;

    @Column(name = "SN_EMail")
    private String sNEMail;

    @Column(name = "SN_EMail_Secundario")
    private String sNEMailSecundario;

    @Column(name = "SN_SegundoNombre")
    private String sNSegundoNombre;

    @Column(name = "SN_DIR_Pais")
    private String sNDIRPais;

    @Column(name = "SN_DIR_Departamento")
    private String sNDIRDepartamento;

    @Column(name = "SN_DIR_Provincia")
    private String sNDIRProvincia;

    @Column(name = "SN_DIR_Distrito")
    private String sNDIRDistrito;

    @Column(name = "SN_DIR_Direccion")
    private String sNDIRDireccion;

    @Column(name = "SN_DIR_NomCalle")
    private String sNDIRNomCalle;

    @Column(name = "SN_DIR_NroCasa")
    private String sNDIRNroCasa;

    @Column(name = "SN_DIR_Ubigeo")
    private String sNDIRUbigeo;

    @Column(name = "SN_DIR_Urbanizacion")
    private String sNDIRUrbanizacion;

    @Column(name = "DOC_Serie")
    private String dOCSerie;

    @Column(name = "DOC_Numero")
    private String dOCNumero;

    @Column(name = "DOC_Id")
    private String dOCId;

    @Column(name = "DOC_FechaEmision")
    @Temporal(TemporalType.DATE)
    private Date dOCFechaEmision;

    @Column(name = "DOC_FechaVencimiento")
    @Temporal(TemporalType.DATE)
    private Date dOCFechaVencimiento;

    @Column(name = "DOC_Dscrpcion")
    private String dOCDscrpcion;

    @Column(name = "DOC_Codigo")
    private String dOCCodigo;

    @Column(name = "DOC_MON_Nombre")
    private String dOCMONNombre;

    @Column(name = "DOC_MON_Codigo")
    private String dOCMONCodigo;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DOC_Descuento")
    private BigDecimal dOCDescuento;

    @Column(name = "DOC_PorDescuento")
    private BigDecimal dOCPorDescuento;

    @Column(name = "DOC_MontoTotal")
    private BigDecimal dOCMontoTotal;

    @Column(name = "DOC_DescuentoTotal")
    private BigDecimal dOCDescuentoTotal;

    @Column(name = "DOC_Importe")
    private BigDecimal dOCImporte;

    @Column(name = "DOC_ImporteTotal")
    private BigDecimal dOCImporteTotal;

    @Column(name = "ImporteDOCRef")
    private BigDecimal ImporteDOCRef;

    @Column(name = "DOC_MonPercepcion")
    private BigDecimal dOCMonPercepcion;

    @Column(name = "DOC_PorPercepcion")
    private BigDecimal dOCPorPercepcion;

    @Column(name = "MontoRetencion")
    private BigDecimal montoRetencion;

    @Column(name = "DOC_PorcImpuesto")
    private String dOCPorcImpuesto;

    @Column(name = "ANTICIPO_Id")
    private String aNTICIPOId;

    @Column(name = "ANTICIPO_Tipo")
    private String aNTICIPOTipo;

    @Column(name = "ANTICIPO_Monto")
    private BigDecimal aNTICIPOMonto;

    @Column(name = "ANTCIPO_Tipo_Doc_ID")
    private String aNTCIPOTipoDocID;

    @Column(name = "ANTICIPO_Nro_Doc_ID")
    private String aNTICIPONroDocID;

    @Column(name = "SUNAT_Transact")
    private String sUNATTransact;

    @Column(name = "FE_DocEntry")
    private Integer fEDocEntry;

    @Column(name = "FE_ObjectType")
    private String fEObjectType;

    @Column(name = "FE_Estado")
    private String fEEstado;

    //@NotNull(message = "El campo tipo de transacci\u00fen no debe ser nula")
    @Column(name = "FE_TipoTrans")
    private String fETipoTrans;

    @Column(name = "FE_DocNum")
    private Integer fEDocNum;

    @Column(name = "FE_FormSAP")
    private String fEFormSAP;

    @Column(name = "REFDOC_Tipo")
    private String rEFDOCTipo;

    @Column(name = "REFDOC_Id")
    private String rEFDOCId;

    @Column(name = "REFDOC_MotivCode")
    private String rEFDOCMotivCode;

    @Column(name = "REFDOC_MotivDesc")
    private String rEFDOCMotivDesc;

    @Column(name = "FE_Comentario")
    private String fEComentario;

    @Column(name = "FE_ErrCod")
    private String fEErrCod;

    @Column(name = "FE_ErrMsj")
    private String fEErrMsj;

    @Column(name = "FE_Errores")
    private Integer fEErrores;

    @Column(name = "FE_MaxSalto")
    private Integer fEMaxSalto;

    @Column(name = "FE_Saltos")
    private Integer fESaltos;

    @Column(name = "DOC_CondPago")
    private String dOCCondPago;

    @Column(name = "RET_Regimen")
    private String rETRegimen;

    @Column(name = "RET_Tasa")
    private String rETTasa;

    @Column(name = "Observaciones")
    private String observaciones;

    @Column(name = "ImportePagado")
    private BigDecimal importePagado;

    @Column(name = "MonedaPagado")
    private String monedaPagado;

    @Column(name = "DOC_OtrosCargos")
    private BigDecimal dOCOtrosCargos;

    @Column(name = "DOC_SinPercepcion")
    private BigDecimal dOCSinPercepcion;

    @Column(name = "DOC_ImpuestoTotal")
    private BigDecimal dOCImpuestoTotal;

    @Column(name = "CuentaDetraccion")
    private String cuentaDetraccion;

    @Column(name = "CodigoDetraccion")
    private String codigoDetraccion;

    @Column(name = "PorcDetraccion")
    private BigDecimal porcDetraccion;

    @Column(name = "MontoDetraccion")
    private BigDecimal montoDetraccion;

    @Column(name = "CodigoPago")
    private String codigoPago;

    //@NotNull(message = "La clave de la sociedad no puede ser nula.")
    @Column(name = "key_sociedad")
    private String keySociedad;

    @Column(name = "TicketBaja")
    private String ticketBaja;


    public BigDecimal getMontoRetencion() {
        return montoRetencion;
    }

    public void setMontoRetencion(BigDecimal montoRetencion) {
        this.montoRetencion = montoRetencion;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionImpuestos> transaccionImpuestosList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionLineas> transaccionLineas = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion" )
    private Set<TransaccionCuotas> transaccionCuotas = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionDocrefers> transaccionDocrefersList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionPropiedades> transaccionPropiedadesList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionComprobantePago> transaccionComprobantePagos = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionContractdocref> transaccionContractdocrefs = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionTotales> transaccionTotalesList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TransaccionAnticipo> transaccionAnticipoList = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", orphanRemoval = true)
    private List<TransaccionUsucampos> transaccionUsucamposList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private TransaccionGuiaRemision transaccionGuiaRemision;

    @Override
    public String toString() {
        return "Transaccion{" +
                "fEId='" + fEId + '\'' +
                ", tipoOperacionSunat='" + tipoOperacionSunat + '\'' +
                ", sNDIRPaisDescripcion='" + sNDIRPaisDescripcion + '\'' +
                ", fechaDOCRef='" + fechaDOCRef + '\'' +
                ", docIdentidadNro='" + docIdentidadNro + '\'' +
                ", docIdentidadTipo='" + docIdentidadTipo + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", nombreComercial='" + nombreComercial + '\'' +
                ", personContacto='" + personContacto + '\'' +
                ", eMail='" + eMail + '\'' +
                ", telefono='" + telefono + '\'' +
                ", telefono1='" + telefono1 + '\'' +
                ", web='" + web + '\'' +
                ", dIRPais='" + dIRPais + '\'' +
                ", dIRDepartamento='" + dIRDepartamento + '\'' +
                ", dIRProvincia='" + dIRProvincia + '\'' +
                ", dIRDistrito='" + dIRDistrito + '\'' +
                ", dIRDireccion='" + dIRDireccion + '\'' +
                ", dIRNomCalle='" + dIRNomCalle + '\'' +
                ", dIRNroCasa='" + dIRNroCasa + '\'' +
                ", dIRUbigeo='" + dIRUbigeo + '\'' +
                ", dIRUrbanizacion='" + dIRUrbanizacion + '\'' +
                ", sNDocIdentidadNro='" + sNDocIdentidadNro + '\'' +
                ", sNDocIdentidadTipo='" + sNDocIdentidadTipo + '\'' +
                ", sNRazonSocial='" + sNRazonSocial + '\'' +
                ", sNNombreComercial='" + sNNombreComercial + '\'' +
                ", sNEMail='" + sNEMail + '\'' +
                ", sNEMailSecundario='" + sNEMailSecundario + '\'' +
                ", sNSegundoNombre='" + sNSegundoNombre + '\'' +
                ", sNDIRPais='" + sNDIRPais + '\'' +
                ", sNDIRDepartamento='" + sNDIRDepartamento + '\'' +
                ", sNDIRProvincia='" + sNDIRProvincia + '\'' +
                ", sNDIRDistrito='" + sNDIRDistrito + '\'' +
                ", sNDIRDireccion='" + sNDIRDireccion + '\'' +
                ", sNDIRNomCalle='" + sNDIRNomCalle + '\'' +
                ", sNDIRNroCasa='" + sNDIRNroCasa + '\'' +
                ", sNDIRUbigeo='" + sNDIRUbigeo + '\'' +
                ", sNDIRUrbanizacion='" + sNDIRUrbanizacion + '\'' +
                ", dOCSerie='" + dOCSerie + '\'' +
                ", dOCNumero='" + dOCNumero + '\'' +
                ", dOCId='" + dOCId + '\'' +
                ", dOCFechaEmision=" + dOCFechaEmision +
                ", dOCFechaVencimiento=" + dOCFechaVencimiento +
                ", dOCDscrpcion='" + dOCDscrpcion + '\'' +
                ", dOCCodigo='" + dOCCodigo + '\'' +
                ", dOCMONNombre='" + dOCMONNombre + '\'' +
                ", dOCMONCodigo='" + dOCMONCodigo + '\'' +
                ", dOCDescuento=" + dOCDescuento +
                ", dOCPorDescuento=" + dOCPorDescuento +
                ", dOCMontoTotal=" + dOCMontoTotal +
                ", dOCDescuentoTotal=" + dOCDescuentoTotal +
                ", dOCImporte=" + dOCImporte +
                ", dOCImporteTotal=" + dOCImporteTotal +
                ", dOCMonPercepcion=" + dOCMonPercepcion +
                ", dOCPorPercepcion=" + dOCPorPercepcion +
                ", dOCPorcImpuesto='" + dOCPorcImpuesto + '\'' +
                ", aNTICIPOId='" + aNTICIPOId + '\'' +
                ", aNTICIPOTipo='" + aNTICIPOTipo + '\'' +
                ", aNTICIPOMonto=" + aNTICIPOMonto +
                ", aNTCIPOTipoDocID='" + aNTCIPOTipoDocID + '\'' +
                ", aNTICIPONroDocID='" + aNTICIPONroDocID + '\'' +
                ", sUNATTransact='" + sUNATTransact + '\'' +
                ", fEDocEntry=" + fEDocEntry +
                ", fEObjectType='" + fEObjectType + '\'' +
                ", fEEstado='" + fEEstado + '\'' +
                ", fETipoTrans='" + fETipoTrans + '\'' +
                ", fEDocNum=" + fEDocNum +
                ", fEFormSAP='" + fEFormSAP + '\'' +
                ", rEFDOCTipo='" + rEFDOCTipo + '\'' +
                ", rEFDOCId='" + rEFDOCId + '\'' +
                ", rEFDOCMotivCode='" + rEFDOCMotivCode + '\'' +
                ", rEFDOCMotivDesc='" + rEFDOCMotivDesc + '\'' +
                ", fEComentario='" + fEComentario + '\'' +
                ", fEErrCod='" + fEErrCod + '\'' +
                ", fEErrMsj='" + fEErrMsj + '\'' +
                ", fEErrores=" + fEErrores +
                ", fEMaxSalto=" + fEMaxSalto +
                ", fESaltos=" + fESaltos +
                ", dOCCondPago='" + dOCCondPago + '\'' +
                ", rETRegimen='" + rETRegimen + '\'' +
                ", rETTasa='" + rETTasa + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", importePagado=" + importePagado +
                ", monedaPagado='" + monedaPagado + '\'' +
                ", dOCOtrosCargos=" + dOCOtrosCargos +
                ", dOCSinPercepcion=" + dOCSinPercepcion +
                ", dOCImpuestoTotal=" + dOCImpuestoTotal +
                ", cuentaDetraccion='" + cuentaDetraccion + '\'' +
                ", codigoDetraccion='" + codigoDetraccion + '\'' +
                ", porcDetraccion=" + porcDetraccion +
                ", montoDetraccion=" + montoDetraccion +
                ", codigoPago='" + codigoPago + '\'' +
                ", keySociedad='" + keySociedad + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaccion that = (Transaccion) o;
        return Objects.equals(fEId, that.fEId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fEId);
    }
}
