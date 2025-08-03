/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;


import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author VSUser
 */

@Entity
@Table(name = "TRANSACCION")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Transaccion.findAll", query = "SELECT t FROM Transaccion t"),
        @NamedQuery(name = "Transaccion.findMaxEncolamiento", query = "SELECT MAX(t.fEMaxSalto) FROM Transaccion t"),
        @NamedQuery(name = "Transaccion.findByDocIdentidadNro", query = "SELECT t FROM Transaccion t WHERE t.docIdentidadNro = :docIdentidadNro"),
        @NamedQuery(name = "Transaccion.findByDocIdentidadTipo", query = "SELECT t FROM Transaccion t WHERE t.docIdentidadTipo = :docIdentidadTipo"),
        @NamedQuery(name = "Transaccion.findByRazonSocial", query = "SELECT t FROM Transaccion t WHERE t.razonSocial = :razonSocial"),
        @NamedQuery(name = "Transaccion.findByNombreComercial", query = "SELECT t FROM Transaccion t WHERE t.nombreComercial = :nombreComercial"),
        @NamedQuery(name = "Transaccion.findByPersonContacto", query = "SELECT t FROM Transaccion t WHERE t.personContacto = :personContacto"),
        @NamedQuery(name = "Transaccion.findByEMail", query = "SELECT t FROM Transaccion t WHERE t.eMail = :eMail"),
        @NamedQuery(name = "Transaccion.findByTelefono", query = "SELECT t FROM Transaccion t WHERE t.telefono = :telefono"),
        @NamedQuery(name = "Transaccion.findByTelefono1", query = "SELECT t FROM Transaccion t WHERE t.telefono1 = :telefono1"),
        @NamedQuery(name = "Transaccion.findByWeb", query = "SELECT t FROM Transaccion t WHERE t.web = :web"),
        @NamedQuery(name = "Transaccion.findByDIRPais", query = "SELECT t FROM Transaccion t WHERE t.dIRPais = :dIRPais"),
        @NamedQuery(name = "Transaccion.findByDIRDepartamento", query = "SELECT t FROM Transaccion t WHERE t.dIRDepartamento = :dIRDepartamento"),
        @NamedQuery(name = "Transaccion.findByDIRProvincia", query = "SELECT t FROM Transaccion t WHERE t.dIRProvincia = :dIRProvincia"),
        @NamedQuery(name = "Transaccion.findByDIRDistrito", query = "SELECT t FROM Transaccion t WHERE t.dIRDistrito = :dIRDistrito"),
        @NamedQuery(name = "Transaccion.findByDIRDireccion", query = "SELECT t FROM Transaccion t WHERE t.dIRDireccion = :dIRDireccion"),
        @NamedQuery(name = "Transaccion.findByDIRNomCalle", query = "SELECT t FROM Transaccion t WHERE t.dIRNomCalle = :dIRNomCalle"),
        @NamedQuery(name = "Transaccion.findByDIRNroCasa", query = "SELECT t FROM Transaccion t WHERE t.dIRNroCasa = :dIRNroCasa"),
        @NamedQuery(name = "Transaccion.findByDIRUbigeo", query = "SELECT t FROM Transaccion t WHERE t.dIRUbigeo = :dIRUbigeo"),
        @NamedQuery(name = "Transaccion.findByDIRUrbanizacion", query = "SELECT t FROM Transaccion t WHERE t.dIRUrbanizacion = :dIRUrbanizacion"),
        @NamedQuery(name = "Transaccion.findBySNDocIdentidadNro", query = "SELECT t FROM Transaccion t WHERE t.sNDocIdentidadNro = :sNDocIdentidadNro"),
        @NamedQuery(name = "Transaccion.findBySNDocIdentidadTipo", query = "SELECT t FROM Transaccion t WHERE t.sNDocIdentidadTipo = :sNDocIdentidadTipo"),
        @NamedQuery(name = "Transaccion.findBySNRazonSocial", query = "SELECT t FROM Transaccion t WHERE t.sNRazonSocial = :sNRazonSocial"),
        @NamedQuery(name = "Transaccion.findBySNNombreComercial", query = "SELECT t FROM Transaccion t WHERE t.sNNombreComercial = :sNNombreComercial"),
        @NamedQuery(name = "Transaccion.findBySNEMail", query = "SELECT t FROM Transaccion t WHERE t.sNEMail = :sNEMail"),
        @NamedQuery(name = "Transaccion.findBySNSegundoNombre", query = "SELECT t FROM Transaccion t WHERE t.sNSegundoNombre = :sNSegundoNombre"),
        @NamedQuery(name = "Transaccion.findBySNDIRPais", query = "SELECT t FROM Transaccion t WHERE t.sNDIRPais = :sNDIRPais"),
        @NamedQuery(name = "Transaccion.findBySNDIRDepartamento", query = "SELECT t FROM Transaccion t WHERE t.sNDIRDepartamento = :sNDIRDepartamento"),
        @NamedQuery(name = "Transaccion.findBySNDIRProvincia", query = "SELECT t FROM Transaccion t WHERE t.sNDIRProvincia = :sNDIRProvincia"),
        @NamedQuery(name = "Transaccion.findBySNDIRDistrito", query = "SELECT t FROM Transaccion t WHERE t.sNDIRDistrito = :sNDIRDistrito"),
        @NamedQuery(name = "Transaccion.findBySNDIRDireccion", query = "SELECT t FROM Transaccion t WHERE t.sNDIRDireccion = :sNDIRDireccion"),
        @NamedQuery(name = "Transaccion.findBySNDIRNomCalle", query = "SELECT t FROM Transaccion t WHERE t.sNDIRNomCalle = :sNDIRNomCalle"),
        @NamedQuery(name = "Transaccion.findBySNDIRNroCasa", query = "SELECT t FROM Transaccion t WHERE t.sNDIRNroCasa = :sNDIRNroCasa"),
        @NamedQuery(name = "Transaccion.findBySNDIRUbigeo", query = "SELECT t FROM Transaccion t WHERE t.sNDIRUbigeo = :sNDIRUbigeo"),
        @NamedQuery(name = "Transaccion.findBySNDIRUrbanizacion", query = "SELECT t FROM Transaccion t WHERE t.sNDIRUrbanizacion = :sNDIRUrbanizacion"),
        @NamedQuery(name = "Transaccion.findByDOCSerie", query = "SELECT t FROM Transaccion t WHERE t.dOCSerie = :dOCSerie"),
        @NamedQuery(name = "Transaccion.findByDOCNumero", query = "SELECT t FROM Transaccion t WHERE t.dOCNumero = :dOCNumero"),
        @NamedQuery(name = "Transaccion.findByDOCId", query = "SELECT t FROM Transaccion t WHERE t.dOCId = :dOCId"),
        @NamedQuery(name = "Transaccion.findByDOCFechaEmision", query = "SELECT t FROM Transaccion t WHERE t.dOCFechaEmision = :dOCFechaEmision"),
        @NamedQuery(name = "Transaccion.findByDOCFechaVencimiento", query = "SELECT t FROM Transaccion t WHERE t.dOCFechaVencimiento = :dOCFechaVencimiento"),
        @NamedQuery(name = "Transaccion.findByDOCDscrpcion", query = "SELECT t FROM Transaccion t WHERE t.dOCDscrpcion = :dOCDscrpcion"),
        @NamedQuery(name = "Transaccion.findByDOCCodigo", query = "SELECT t FROM Transaccion t WHERE t.dOCCodigo = :dOCCodigo"),
        @NamedQuery(name = "Transaccion.findByDOCMONNombre", query = "SELECT t FROM Transaccion t WHERE t.dOCMONNombre = :dOCMONNombre"),
        @NamedQuery(name = "Transaccion.findByDOCMONCodigo", query = "SELECT t FROM Transaccion t WHERE t.dOCMONCodigo = :dOCMONCodigo"),
        @NamedQuery(name = "Transaccion.findByDOCDescuento", query = "SELECT t FROM Transaccion t WHERE t.dOCDescuento = :dOCDescuento"),
        @NamedQuery(name = "Transaccion.findByDOCPorDescuento", query = "SELECT t FROM Transaccion t WHERE t.dOCPorDescuento = :dOCPorDescuento"),
        @NamedQuery(name = "Transaccion.findByDOCMontoTotal", query = "SELECT t FROM Transaccion t WHERE t.dOCMontoTotal = :dOCMontoTotal"),
        @NamedQuery(name = "Transaccion.findByDOCDescuentoTotal", query = "SELECT t FROM Transaccion t WHERE t.dOCDescuentoTotal = :dOCDescuentoTotal"),
        @NamedQuery(name = "Transaccion.findByDOCImporte", query = "SELECT t FROM Transaccion t WHERE t.dOCImporte = :dOCImporte"),
        @NamedQuery(name = "Transaccion.findByDOCImporteTotal", query = "SELECT t FROM Transaccion t WHERE t.dOCImporteTotal = :dOCImporteTotal"),
        @NamedQuery(name = "Transaccion.findByDOCPorcImpuesto", query = "SELECT t FROM Transaccion t WHERE t.dOCPorcImpuesto = :dOCPorcImpuesto"),
        @NamedQuery(name = "Transaccion.findByANTICIPOId", query = "SELECT t FROM Transaccion t WHERE t.aNTICIPOId = :aNTICIPOId"),
        @NamedQuery(name = "Transaccion.findByANTICIPOTipo", query = "SELECT t FROM Transaccion t WHERE t.aNTICIPOTipo = :aNTICIPOTipo"),
        @NamedQuery(name = "Transaccion.findByANTICIPOMonto", query = "SELECT t FROM Transaccion t WHERE t.aNTICIPOMonto = :aNTICIPOMonto"),
        @NamedQuery(name = "Transaccion.findByANTCIPOTipoDocID", query = "SELECT t FROM Transaccion t WHERE t.aNTCIPOTipoDocID = :aNTCIPOTipoDocID"),
        @NamedQuery(name = "Transaccion.findByANTICIPONroDocID", query = "SELECT t FROM Transaccion t WHERE t.aNTICIPONroDocID = :aNTICIPONroDocID"),
        @NamedQuery(name = "Transaccion.findBySUNATTransact", query = "SELECT t FROM Transaccion t WHERE t.sUNATTransact = :sUNATTransact"),
        @NamedQuery(name = "Transaccion.findByFEDocEntry", query = "SELECT t FROM Transaccion t WHERE t.fEDocEntry = :fEDocEntry"),
        @NamedQuery(name = "Transaccion.findByFEObjectType", query = "SELECT t FROM Transaccion t WHERE t.fEObjectType = :fEObjectType"),
        @NamedQuery(name = "Transaccion.findByFEEstado", query = "SELECT t FROM Transaccion t WHERE t.fEEstado = :fEEstado"),
        @NamedQuery(name = "Transaccion.findByFETipoTrans", query = "SELECT t FROM Transaccion t WHERE t.fETipoTrans = :fETipoTrans"),
        @NamedQuery(name = "Transaccion.findByFEId", query = "SELECT t FROM Transaccion t WHERE t.fEId = :fEId"),
        @NamedQuery(name = "Transaccion.findByFEDocNum", query = "SELECT t FROM Transaccion t WHERE t.fEDocNum = :fEDocNum"),
        @NamedQuery(name = "Transaccion.findByFEFormSAP", query = "SELECT t FROM Transaccion t WHERE t.fEFormSAP = :fEFormSAP"),
        @NamedQuery(name = "Transaccion.findByREFDOCTipo", query = "SELECT t FROM Transaccion t WHERE t.rEFDOCTipo = :rEFDOCTipo"),
        @NamedQuery(name = "Transaccion.findByREFDOCId", query = "SELECT t FROM Transaccion t WHERE t.rEFDOCId = :rEFDOCId"),
        @NamedQuery(name = "Transaccion.findByREFDOCMotivCode", query = "SELECT t FROM Transaccion t WHERE t.rEFDOCMotivCode = :rEFDOCMotivCode"),
        @NamedQuery(name = "Transaccion.findByREFDOCMotivDesc", query = "SELECT t FROM Transaccion t WHERE t.rEFDOCMotivDesc = :rEFDOCMotivDesc"),
        @NamedQuery(name = "Transaccion.findByFEComentario", query = "SELECT t FROM Transaccion t WHERE t.fEComentario = :fEComentario"),
        @NamedQuery(name = "Transaccion.findByFEErrCod", query = "SELECT t FROM Transaccion t WHERE t.fEErrCod = :fEErrCod"),
        @NamedQuery(name = "Transaccion.findByFEErrMsj", query = "SELECT t FROM Transaccion t WHERE t.fEErrMsj = :fEErrMsj"),
        @NamedQuery(name = "Transaccion.findByFEErrores", query = "SELECT t FROM Transaccion t WHERE t.fEErrores = :fEErrores"),
        @NamedQuery(name = "Transaccion.findByFEMaxSalto", query = "SELECT t FROM Transaccion t WHERE t.fEMaxSalto = :fEMaxSalto"),
        @NamedQuery(name = "Transaccion.findByFESaltos", query = "SELECT t FROM Transaccion t WHERE t.fESaltos = :fESaltos"),
        @NamedQuery(name = "Transaccion.findByDOCCondPago", query = "SELECT t FROM Transaccion t WHERE t.dOCCondPago = :dOCCondPago"),
        @NamedQuery(name = "Transaccion.findByRETRegimen", query = "SELECT t FROM Transaccion t WHERE t.rETRegimen = :rETRegimen"),
        @NamedQuery(name = "Transaccion.findByRETTasa", query = "SELECT t FROM Transaccion t WHERE t.rETTasa = :rETTasa"),
        @NamedQuery(name = "Transaccion.findByObservaciones", query = "SELECT t FROM Transaccion t WHERE t.observaciones = :observaciones"),
        @NamedQuery(name = "Transaccion.findByImportePagado", query = "SELECT t FROM Transaccion t WHERE t.importePagado = :importePagado"),
        @NamedQuery(name = "Transaccion.findByMonedaPagado", query = "SELECT t FROM Transaccion t WHERE t.monedaPagado = :monedaPagado"),
        @NamedQuery(name = "Transaccion.findByDOCOtrosCargos", query = "SELECT t FROM Transaccion t WHERE t.dOCOtrosCargos = :dOCOtrosCargos"),
        @NamedQuery(name = "Transaccion.findByDOCSinPercepcion", query = "SELECT t FROM Transaccion t WHERE t.dOCSinPercepcion = :dOCSinPercepcion"),
        @NamedQuery(name = "Transaccion.findByDOCImpuestoTotal", query = "SELECT t FROM Transaccion t WHERE t.dOCImpuestoTotal = :dOCImpuestoTotal"),
        @NamedQuery(name = "Transaccion.findByCuentaDetraccion", query = "SELECT t FROM Transaccion t WHERE t.cuentaDetraccion = :cuentaDetraccion"),
        @NamedQuery(name = "Transaccion.findByCodigoDetraccion", query = "SELECT t FROM Transaccion t WHERE t.codigoDetraccion = :codigoDetraccion"),
        @NamedQuery(name = "Transaccion.findByPorcDetraccion", query = "SELECT t FROM Transaccion t WHERE t.porcDetraccion = :porcDetraccion"),
        @NamedQuery(name = "Transaccion.findByMontoDetraccion", query = "SELECT t FROM Transaccion t WHERE t.montoDetraccion = :montoDetraccion"),
        @NamedQuery(name = "Transaccion.findByCodigoPago", query = "SELECT t FROM Transaccion t WHERE t.codigoPago = :codigoPago"),
        @NamedQuery(name = "Transaccion.findDisponibles", query = "SELECT t FROM Transaccion t WHERE t.fEEstado IN ('N','E','C','G','W') ORDER BY t.fEMaxSalto ASC"),
        @NamedQuery(name = "Transaccion.findPendientes", query = "SELECT t FROM Transaccion t WHERE t.fEEstado IN ('R','P','S') ORDER BY t.fEMaxSalto ASC")})
public class Transaccion implements Serializable {

    @Column(name = "TipoOperacionSunat")
    private String tipoOperacionSunat;

    @Column(name = "SN_DIR_Pais_Descripcion")
    private String sNDIRPaisDescripcion;

    @Column(name = "FechaDOCRef")
    private String fechaDOCRef;

    @Column(name = "FechaVenDOCRef")
    private String fechaVenDOCRef;

    @Column(name = "ImporteDOCRef")
    private BigDecimal ImporteDOCRef;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionAnticipo> transaccionAnticipoList;

    private static List<TransaccionUsucampos> propiedades;

    public static List<TransaccionUsucampos> getPropiedades() {
        if (propiedades == null) {
            propiedades = new LinkedList();
        }
        return propiedades;
    }

    private static final long serialVersionUID = 1L;

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date dOCFechaEmision;

    @Column(name = "DOC_FechaVencimiento")
    @Temporal(TemporalType.TIMESTAMP)
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

    @Column(name = "DOC_MonPercepcion")
    private BigDecimal dOCMonPercepcion;

    @Column(name = "DOC_PorPercepcion")
    private BigDecimal dOCPorPercepcion;

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

    @Column(name = "FE_TipoTrans")
    private String fETipoTrans;

    @Id
    @Basic(optional = false)
    @Column(name = "FE_Id")
    private String fEId;

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

    @Transient
    private String ticketRest;

    public String getTicketRest() {
        return ticketRest;
    }

    public void setTicketRest(String ticketRest) {
        this.ticketRest = ticketRest;
    }

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

    @Column(name = "key_sociedad")
    private String keySociedad;

    @Column(name = "TicketBaja")
    private String ticket;

    @Column(name = "MontoRetencion")
    private BigDecimal montoRetencion;

    public BigDecimal getMontoRetencion() {
        return montoRetencion;
    }

    public void setMontoRetencion(BigDecimal montoRetencion) {
        this.montoRetencion = montoRetencion;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionImpuestos> transaccionImpuestosList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionLineas>     transaccionLineasList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion", fetch = FetchType.EAGER)
    private List<TransaccionDocrefers> transaccionDocrefersList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionPropiedades> transaccionPropiedadesList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionCuotas> transaccionCuotas ;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionComprobantePago> transaccionComprobantePagoList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionContractdocref> transaccionContractdocrefList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionTotales> transaccionTotalesList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private List<TransaccionUsucampos> transaccionUsucamposList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private TransaccionGuiaRemision transaccionGuiaRemision;

    public TransaccionGuiaRemision getTransaccionGuiaRemision() {
        return transaccionGuiaRemision;
    }

    public void setTransaccionGuiaRemision(TransaccionGuiaRemision transaccionGuiaRemision) {
        this.transaccionGuiaRemision = transaccionGuiaRemision;
    }

    public Transaccion() {
    }

    public Transaccion(String fEId) {
        this.fEId = fEId;
    }

    public String getDocIdentidadNro() {
        return docIdentidadNro;
    }

    public void setDocIdentidadNro(String docIdentidadNro) {
        this.docIdentidadNro = docIdentidadNro;
    }

    public String getDocIdentidadTipo() {
        return docIdentidadTipo;
    }

    public void setDocIdentidadTipo(String docIdentidadTipo) {
        this.docIdentidadTipo = docIdentidadTipo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getPersonContacto() {
        return personContacto;
    }

    public void setPersonContacto(String personContacto) {
        this.personContacto = personContacto;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getDIRPais() {
        return dIRPais;
    }

    public void setDIRPais(String dIRPais) {
        this.dIRPais = dIRPais;
    }

    public String getDIRDepartamento() {
        return dIRDepartamento;
    }

    public void setDIRDepartamento(String dIRDepartamento) {
        this.dIRDepartamento = dIRDepartamento;
    }

    public String getDIRProvincia() {
        return dIRProvincia;
    }

    public void setDIRProvincia(String dIRProvincia) {
        this.dIRProvincia = dIRProvincia;
    }

    public String getDIRDistrito() {
        return dIRDistrito;
    }

    public void setDIRDistrito(String dIRDistrito) {
        this.dIRDistrito = dIRDistrito;
    }

    public String getDIRDireccion() {
        return dIRDireccion;
    }

    public void setDIRDireccion(String dIRDireccion) {
        this.dIRDireccion = dIRDireccion;
    }

    public String getDIRNomCalle() {
        return dIRNomCalle;
    }

    public void setDIRNomCalle(String dIRNomCalle) {
        this.dIRNomCalle = dIRNomCalle;
    }

    public String getDIRNroCasa() {
        return dIRNroCasa;
    }

    public void setDIRNroCasa(String dIRNroCasa) {
        this.dIRNroCasa = dIRNroCasa;
    }

    public String getDIRUbigeo() {
        return dIRUbigeo;
    }

    public void setDIRUbigeo(String dIRUbigeo) {
        this.dIRUbigeo = dIRUbigeo;
    }

    public String getDIRUrbanizacion() {
        return dIRUrbanizacion;
    }

    public void setDIRUrbanizacion(String dIRUrbanizacion) {
        this.dIRUrbanizacion = dIRUrbanizacion;
    }

    public String getSNDocIdentidadNro() {
        return sNDocIdentidadNro;
    }

    public void setSNDocIdentidadNro(String sNDocIdentidadNro) {
        this.sNDocIdentidadNro = sNDocIdentidadNro;
    }

    public String getSNDocIdentidadTipo() {
        return sNDocIdentidadTipo;
    }

    public void setSNDocIdentidadTipo(String sNDocIdentidadTipo) {
        this.sNDocIdentidadTipo = sNDocIdentidadTipo;
    }

    public String getSNRazonSocial() {
        return sNRazonSocial;
    }

    public void setSNRazonSocial(String sNRazonSocial) {
        this.sNRazonSocial = sNRazonSocial;
    }

    public String getSNNombreComercial() {
        return sNNombreComercial;
    }

    public void setSNNombreComercial(String sNNombreComercial) {
        this.sNNombreComercial = sNNombreComercial;
    }

    public String getSNEMail() {
        return sNEMail;
    }

    public void setSNEMail(String sNEMail) {
        this.sNEMail = sNEMail;
    }

    public String getSNSegundoNombre() {
        return sNSegundoNombre;
    }

    public void setSNSegundoNombre(String sNSegundoNombre) {
        this.sNSegundoNombre = sNSegundoNombre;
    }

    public String getSNDIRPais() {
        return sNDIRPais;
    }

    public void setSNDIRPais(String sNDIRPais) {
        this.sNDIRPais = sNDIRPais;
    }

    public String getSNDIRDepartamento() {
        return sNDIRDepartamento;
    }

    public void setSNDIRDepartamento(String sNDIRDepartamento) {
        this.sNDIRDepartamento = sNDIRDepartamento;
    }

    public String getSNDIRProvincia() {
        return sNDIRProvincia;
    }

    public void setSNDIRProvincia(String sNDIRProvincia) {
        this.sNDIRProvincia = sNDIRProvincia;
    }

    public String getSNDIRDistrito() {
        return sNDIRDistrito;
    }

    public void setSNDIRDistrito(String sNDIRDistrito) {
        this.sNDIRDistrito = sNDIRDistrito;
    }

    public String getSNDIRDireccion() {
        return sNDIRDireccion;
    }

    public String getSNEMailSecundario() {
        return sNEMailSecundario;
    }

    public void setSNEMailSecundario(String sNEMailSecundario) {
        this.sNEMailSecundario = sNEMailSecundario;
    }

    public void setSNDIRDireccion(String sNDIRDireccion) {
        this.sNDIRDireccion = sNDIRDireccion;
    }

    public String getSNDIRNomCalle() {
        return sNDIRNomCalle;
    }

    public void setSNDIRNomCalle(String sNDIRNomCalle) {
        this.sNDIRNomCalle = sNDIRNomCalle;
    }

    public String getSNDIRNroCasa() {
        return sNDIRNroCasa;
    }

    public void setSNDIRNroCasa(String sNDIRNroCasa) {
        this.sNDIRNroCasa = sNDIRNroCasa;
    }

    public String getSNDIRUbigeo() {
        return sNDIRUbigeo;
    }

    public void setSNDIRUbigeo(String sNDIRUbigeo) {
        this.sNDIRUbigeo = sNDIRUbigeo;
    }

    public String getSNDIRUrbanizacion() {
        return sNDIRUrbanizacion;
    }

    public void setSNDIRUrbanizacion(String sNDIRUrbanizacion) {
        this.sNDIRUrbanizacion = sNDIRUrbanizacion;
    }

    public String getDOCSerie() {
        return dOCSerie;
    }

    public void setDOCSerie(String dOCSerie) {
        this.dOCSerie = dOCSerie;
    }

    public String getDOCNumero() {
        return dOCNumero;
    }

    public void setDOCNumero(String dOCNumero) {
        this.dOCNumero = dOCNumero;
    }

    public String getDOCId() {
        return dOCId;
    }

    public void setDOCId(String dOCId) {
        this.dOCId = dOCId;
    }

    public Date getDOCFechaEmision() {
        return dOCFechaEmision;
    }

    public void setDOCFechaEmision(Date dOCFechaEmision) {
        this.dOCFechaEmision = dOCFechaEmision;
    }

    public Date getDOCFechaVencimiento() {
        return dOCFechaVencimiento;
    }

    public void setDOCFechaVencimiento(Date dOCFechaVencimiento) {
        this.dOCFechaVencimiento = dOCFechaVencimiento;
    }

    public String getDOCDscrpcion() {
        return dOCDscrpcion;
    }

    public void setDOCDscrpcion(String dOCDscrpcion) {
        this.dOCDscrpcion = dOCDscrpcion;
    }

    public String getDOCCodigo() {
        return dOCCodigo;
    }

    public void setDOCCodigo(String dOCCodigo) {
        this.dOCCodigo = dOCCodigo;
    }

    public String getDOCMONNombre() {
        return dOCMONNombre;
    }

    public void setDOCMONNombre(String dOCMONNombre) {
        this.dOCMONNombre = dOCMONNombre;
    }

    public String getDOCMONCodigo() {
        return dOCMONCodigo;
    }

    public void setDOCMONCodigo(String dOCMONCodigo) {
        this.dOCMONCodigo = dOCMONCodigo;
    }

    public BigDecimal getDOCDescuento() {
        return dOCDescuento;
    }

    public void setDOCDescuento(BigDecimal dOCDescuento) {
        this.dOCDescuento = dOCDescuento;
    }

    public BigDecimal getDOCPorDescuento() {
        return dOCPorDescuento;
    }

    public void setDOCPorDescuento(BigDecimal dOCPorDescuento) {
        this.dOCPorDescuento = dOCPorDescuento;
    }

    public BigDecimal getDOCMontoTotal() {
        return dOCMontoTotal;
    }

    public void setDOCMontoTotal(BigDecimal dOCMontoTotal) {
        this.dOCMontoTotal = dOCMontoTotal;
    }

    public BigDecimal getDOCDescuentoTotal() {
        return dOCDescuentoTotal;
    }

    public void setDOCDescuentoTotal(BigDecimal dOCDescuentoTotal) {
        this.dOCDescuentoTotal = dOCDescuentoTotal;
    }

    public BigDecimal getDOCImporte() {
        return dOCImporte;
    }

    public void setDOCImporte(BigDecimal dOCImporte) {
        this.dOCImporte = dOCImporte;
    }

    public BigDecimal getDOCImporteTotal() {
        return dOCImporteTotal;
    }

    public void setDOCImporteTotal(BigDecimal dOCImporteTotal) {
        this.dOCImporteTotal = dOCImporteTotal;
    }

    public String getDOCPorcImpuesto() {
        return dOCPorcImpuesto;
    }

    public void setDOCPorcImpuesto(String dOCPorcImpuesto) {
        this.dOCPorcImpuesto = dOCPorcImpuesto;
    }

    public String getANTICIPOId() {
        return aNTICIPOId;
    }

    public void setANTICIPOId(String aNTICIPOId) {
        this.aNTICIPOId = aNTICIPOId;
    }

    public String getANTICIPOTipo() {
        return aNTICIPOTipo;
    }

    public void setANTICIPOTipo(String aNTICIPOTipo) {
        this.aNTICIPOTipo = aNTICIPOTipo;
    }

    public BigDecimal getANTICIPOMonto() {
        return aNTICIPOMonto;
    }

    public void setANTICIPOMonto(BigDecimal aNTICIPOMonto) {
        this.aNTICIPOMonto = aNTICIPOMonto;
    }

    public String getANTCIPOTipoDocID() {
        return aNTCIPOTipoDocID;
    }

    public void setANTCIPOTipoDocID(String aNTCIPOTipoDocID) {
        this.aNTCIPOTipoDocID = aNTCIPOTipoDocID;
    }

    public String getANTICIPONroDocID() {
        return aNTICIPONroDocID;
    }

    public void setANTICIPONroDocID(String aNTICIPONroDocID) {
        this.aNTICIPONroDocID = aNTICIPONroDocID;
    }

    public String getSUNATTransact() {
        return sUNATTransact;
    }

    public void setSUNATTransact(String sUNATTransact) {
        this.sUNATTransact = sUNATTransact;
    }

    public Integer getFEDocEntry() {
        return fEDocEntry;
    }

    public void setFEDocEntry(Integer fEDocEntry) {
        this.fEDocEntry = fEDocEntry;
    }

    public String getFEObjectType() {
        return fEObjectType;
    }

    public void setFEObjectType(String fEObjectType) {
        this.fEObjectType = fEObjectType;
    }

    public String getFEEstado() {
        return fEEstado;
    }

    public void setFEEstado(String fEEstado) {
        this.fEEstado = fEEstado;
    }

    public String getFETipoTrans() {
        return fETipoTrans;
    }

    public void setFETipoTrans(String fETipoTrans) {
        this.fETipoTrans = fETipoTrans;
    }

    public String getFEId() {
        return fEId;
    }

    public void setFEId(String fEId) {
        this.fEId = fEId;
    }

    public Integer getFEDocNum() {
        return fEDocNum;
    }

    public void setFEDocNum(Integer fEDocNum) {
        this.fEDocNum = fEDocNum;
    }

    public String getFEFormSAP() {
        return fEFormSAP;
    }

    public void setFEFormSAP(String fEFormSAP) {
        this.fEFormSAP = fEFormSAP;
    }

    public String getREFDOCTipo() {
        return rEFDOCTipo;
    }

    public void setREFDOCTipo(String rEFDOCTipo) {
        this.rEFDOCTipo = rEFDOCTipo;
    }

    public String getREFDOCId() {
        return rEFDOCId;
    }

    public void setREFDOCId(String rEFDOCId) {
        this.rEFDOCId = rEFDOCId;
    }

    public String getREFDOCMotivCode() {
        return rEFDOCMotivCode;
    }

    public void setREFDOCMotivCode(String rEFDOCMotivCode) {
        this.rEFDOCMotivCode = rEFDOCMotivCode;
    }

    public String getREFDOCMotivDesc() {
        return rEFDOCMotivDesc;
    }

    public void setREFDOCMotivDesc(String rEFDOCMotivDesc) {
        this.rEFDOCMotivDesc = rEFDOCMotivDesc;
    }

    public String getFEComentario() {
        return fEComentario;
    }

    public void setFEComentario(String fEComentario) {
        this.fEComentario = fEComentario;
    }

    public String getFEErrCod() {
        return fEErrCod;
    }

    public void setFEErrCod(String fEErrCod) {
        this.fEErrCod = fEErrCod;
    }

    public String getFEErrMsj() {
        return fEErrMsj;
    }

    public void setFEErrMsj(String fEErrMsj) {
        this.fEErrMsj = fEErrMsj;
    }

    public Integer getFEErrores() {
        return fEErrores;
    }

    public void setFEErrores(Integer fEErrores) {
        this.fEErrores = fEErrores;
    }

    public Integer getFEMaxSalto() {
        return fEMaxSalto;
    }

    public void setFEMaxSalto(Integer fEMaxSalto) {
        this.fEMaxSalto = fEMaxSalto;
    }

    public Integer getFESaltos() {
        return fESaltos;
    }

    public void setFESaltos(Integer fESaltos) {
        this.fESaltos = fESaltos;
    }

    public String getDOCCondPago() {
        return dOCCondPago;
    }

    public void setDOCCondPago(String dOCCondPago) {
        this.dOCCondPago = dOCCondPago;
    }

    public String getRETRegimen() {
        return rETRegimen;
    }

    public void setRETRegimen(String rETRegimen) {
        this.rETRegimen = rETRegimen;
    }

    public String getRETTasa() {
        return rETTasa;
    }

    public void setRETTasa(String rETTasa) {
        this.rETTasa = rETTasa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getImportePagado() {
        return importePagado;
    }

    public void setImportePagado(BigDecimal importePagado) {
        this.importePagado = importePagado;
    }

    public String getMonedaPagado() {
        return monedaPagado;
    }

    public void setMonedaPagado(String monedaPagado) {
        this.monedaPagado = monedaPagado;
    }

    public BigDecimal getDOCOtrosCargos() {
        return dOCOtrosCargos;
    }

    public void setDOCOtrosCargos(BigDecimal dOCOtrosCargos) {
        this.dOCOtrosCargos = dOCOtrosCargos;
    }

    public BigDecimal getDOCSinPercepcion() {
        return dOCSinPercepcion;
    }

    public void setDOCSinPercepcion(BigDecimal dOCSinPercepcion) {
        this.dOCSinPercepcion = dOCSinPercepcion;
    }

    public BigDecimal getDOCImpuestoTotal() {
        return dOCImpuestoTotal;
    }

    public void setDOCImpuestoTotal(BigDecimal dOCImpuestoTotal) {
        this.dOCImpuestoTotal = dOCImpuestoTotal;
    }

    public String getCuentaDetraccion() {
        return cuentaDetraccion;
    }

    public void setCuentaDetraccion(String cuentaDetraccion) {
        this.cuentaDetraccion = cuentaDetraccion;
    }

    public String getCodigoDetraccion() {
        return codigoDetraccion;
    }

    public void setCodigoDetraccion(String codigoDetraccion) {
        this.codigoDetraccion = codigoDetraccion;
    }

    public BigDecimal getPorcDetraccion() {
        return porcDetraccion;
    }

    public void setPorcDetraccion(BigDecimal porcDetraccion) {
        this.porcDetraccion = porcDetraccion;
    }

    public BigDecimal getMontoDetraccion() {
        return montoDetraccion;
    }

    public void setMontoDetraccion(BigDecimal montoDetraccion) {
        this.montoDetraccion = montoDetraccion;
    }

    public String getCodigoPago() {
        return codigoPago;
    }

    public void setCodigoPago(String codigoPago) {
        this.codigoPago = codigoPago;
    }

    public BigDecimal getdOCMonPercepcion() {
        return dOCMonPercepcion;
    }

    public String getKeySociedad() {
        return keySociedad;
    }

    public void setKeySociedad(String keySociedad) {
        this.keySociedad = keySociedad;
    }

    public void setdOCMonPercepcion(BigDecimal dOCMonPercepcion) {
        this.dOCMonPercepcion = dOCMonPercepcion;
    }

    public BigDecimal getdOCPorPercepcion() {
        return dOCPorPercepcion;
    }

    public void setdOCPorPercepcion(BigDecimal dOCPorPercepcion) {
        this.dOCPorPercepcion = dOCPorPercepcion;
    }

    @XmlTransient
    public List<TransaccionImpuestos> getTransaccionImpuestosList() {
        return transaccionImpuestosList;
    }

    public void setTransaccionImpuestosList(List<TransaccionImpuestos> transaccionImpuestosList) {
        this.transaccionImpuestosList = transaccionImpuestosList;
    }

    @XmlTransient
    public List<TransaccionLineas> getTransaccionLineasList() {
        return transaccionLineasList;
    }

    public void setTransaccionLineasList(List<TransaccionLineas> transaccionLineasList) {
        this.transaccionLineasList = transaccionLineasList;
    }

    @XmlTransient
    public List<TransaccionDocrefers> getTransaccionDocrefersList() {
        return transaccionDocrefersList;
    }

    public void setTransaccionDocrefersList(List<TransaccionDocrefers> transaccionDocrefersList) {
        this.transaccionDocrefersList = transaccionDocrefersList;
    }

    @XmlTransient
    public List<TransaccionPropiedades> getTransaccionPropiedadesList() {
        return transaccionPropiedadesList;
    }

    public void setTransaccionPropiedadesList(List<TransaccionPropiedades> transaccionPropiedadesList) {
        this.transaccionPropiedadesList = transaccionPropiedadesList;
    }



    @XmlTransient
    public List<TransaccionComprobantePago> getTransaccionComprobantePagoList() {
        return transaccionComprobantePagoList;
    }

    public void setTransaccionComprobantePagoList(List<TransaccionComprobantePago> transaccionComprobantePagoList) {
        this.transaccionComprobantePagoList = transaccionComprobantePagoList;
    }

    @XmlTransient
    public List<TransaccionCuotas> getTransaccionCuotas() {
        return transaccionCuotas;
    }

    public void setTransaccionCuotas(List<TransaccionCuotas> transaccionCuotas) {
        this.transaccionCuotas = transaccionCuotas;
    }

    @XmlTransient
    public List<TransaccionContractdocref> getTransaccionContractdocrefList() {
        return Optional.ofNullable(transaccionContractdocrefList).orElse(new ArrayList<>());
    }

    public void setTransaccionContractdocrefList(List<TransaccionContractdocref> transaccionContractdocrefList) {
        this.transaccionContractdocrefList = transaccionContractdocrefList;
    }

    @XmlTransient
    public List<TransaccionTotales> getTransaccionTotalesList() {
        return transaccionTotalesList;
    }

    public void setTransaccionTotalesList(List<TransaccionTotales> transaccionTotalesList) {
        this.transaccionTotalesList = transaccionTotalesList;
    }

    @XmlTransient
    public List<TransaccionUsucampos> getTransaccionUsucamposList() {
        return transaccionUsucamposList;
    }

    public void setTransaccionUsucamposList(List<TransaccionUsucampos> transaccionUsucamposList) {
        this.transaccionUsucamposList = transaccionUsucamposList;
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
        if (!(object instanceof Transaccion)) {
            return false;
        }
        Transaccion other = (Transaccion) object;
        if ((this.fEId == null && other.fEId != null) || (this.fEId != null && !this.fEId.equals(other.fEId))) {
            return false;
        }
        return true;
    }

    @XmlTransient
    public List<TransaccionAnticipo> getTransaccionAnticipoList() {
        return transaccionAnticipoList;
    }

    public void setTransaccionAnticipoList(List<TransaccionAnticipo> transaccionAnticipoList) {
        this.transaccionAnticipoList = transaccionAnticipoList;
    }

    public String getFechaDOCRef() {
        return fechaDOCRef;
    }

    public void setFechaDOCRef(String fechaDOCRef) {
        this.fechaDOCRef = fechaDOCRef;
    }

    public String getFechaVenDOCRef() {
        return this.fechaVenDOCRef;
    }

    public void setFechaVenDOCRef(String fechaVenDOCRef) {
        this.fechaVenDOCRef = fechaVenDOCRef;
    }



    public String getSNDIRPaisDescripcion() {
        return sNDIRPaisDescripcion;
    }

    public void setSNDIRPaisDescripcion(String sNDIRPaisDescripcion) {
        this.sNDIRPaisDescripcion = sNDIRPaisDescripcion;
    }

    public String getTipoOperacionSunat() {
        return tipoOperacionSunat;
    }

    public void setTipoOperacionSunat(String tipoOperacionSunat) {
        this.tipoOperacionSunat = tipoOperacionSunat;
    }

    public BigDecimal getImporteDOCRef() {
        return this.ImporteDOCRef;
    }

    public void setImporteDOCRef(BigDecimal importeDOCRef) {
        this.ImporteDOCRef = importeDOCRef;
    }


}
