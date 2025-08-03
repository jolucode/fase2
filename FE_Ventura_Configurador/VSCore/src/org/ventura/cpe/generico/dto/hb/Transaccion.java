package org.ventura.cpe.generico.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="TRANSACCION")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Transaccion.findAll", query="SELECT t FROM Transaccion t")})
public class Transaccion
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Column(name="DocIdentidad_Nro")
  private String docIdentidadNro;
  @Column(name="DocIdentidad_Tipo")
  private String docIdentidadTipo;
  @Column(name="RazonSocial")
  private String razonSocial;
  @Column(name="NombreComercial")
  private String nombreComercial;
  @Column(name="PersonContacto")
  private String personContacto;
  @Column(name="EMail")
  private String eMail;
  @Column(name="Telefono")
  private String telefono;
  @Column(name="Telefono_1")
  private String telefono1;
  @Column(name="Web")
  private String web;
  @Column(name="DIR_Pais")
  private String dIRPais;
  @Column(name="DIR_Departamento")
  private String dIRDepartamento;
  @Column(name="DIR_Provincia")
  private String dIRProvincia;
  @Column(name="DIR_Distrito")
  private String dIRDistrito;
  @Column(name="DIR_Direccion")
  private String dIRDireccion;
  @Column(name="DIR_NomCalle")
  private String dIRNomCalle;
  @Column(name="DIR_NroCasa")
  private String dIRNroCasa;
  @Column(name="DIR_Ubigeo")
  private String dIRUbigeo;
  @Column(name="DIR_Urbanizacion")
  private String dIRUrbanizacion;
  @Column(name="SN_DocIdentidad_Nro")
  private String sNDocIdentidadNro;
  @Column(name="SN_DocIdentidad_Tipo")
  private String sNDocIdentidadTipo;
  @Column(name="SN_RazonSocial")
  private String sNRazonSocial;
  @Column(name="SN_NombreComercial")
  private String sNNombreComercial;
  @Column(name="SN_EMail")
  private String sNEMail;
  @Column(name="SN_SegundoNombre")
  private String sNSegundoNombre;
  @Column(name="SN_DIR_Pais")
  private String sNDIRPais;
  @Column(name="SN_DIR_Departamento")
  private String sNDIRDepartamento;
  @Column(name="SN_DIR_Provincia")
  private String sNDIRProvincia;
  @Column(name="SN_DIR_Distrito")
  private String sNDIRDistrito;
  @Column(name="SN_DIR_Direccion")
  private String sNDIRDireccion;
  @Column(name="SN_DIR_NomCalle")
  private String sNDIRNomCalle;
  @Column(name="SN_DIR_NroCasa")
  private String sNDIRNroCasa;
  @Column(name="SN_DIR_Ubigeo")
  private String sNDIRUbigeo;
  @Column(name="SN_DIR_Urbanizacion")
  private String sNDIRUrbanizacion;
  @Column(name="DOC_Serie")
  private String dOCSerie;
  @Column(name="DOC_Numero")
  private String dOCNumero;
  @Column(name="DOC_Id")
  private String dOCId;
  @Column(name="DOC_FechaEmision")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dOCFechaEmision;
  @Column(name="DOC_FechaVencimiento")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dOCFechaVencimiento;
  @Column(name="DOC_Dscrpcion")
  private String dOCDscrpcion;
  @Column(name="DOC_Codigo")
  private String dOCCodigo;
  @Column(name="DOC_MON_Nombre")
  private String dOCMONNombre;
  @Column(name="DOC_MON_Codigo")
  private String dOCMONCodigo;
  @Column(name="DOC_MontoTotal")
  private BigDecimal dOCMontoTotal;
  @Column(name="DOC_DescuentoTotal")
  private BigDecimal dOCDescuentoTotal;
  @Column(name="DOC_Importe")
  private BigDecimal dOCImporte;
  @Column(name="ANTICIPO_Monto")
  private BigDecimal aNTICIPOMonto;
  @Column(name="SUNAT_Transact")
  private String sUNATTransact;
  @Column(name="FE_Estado")
  private String fEEstado;
  @Column(name="FE_TipoTrans")
  private String fETipoTrans;
  @Id
  @Basic(optional=false)
  @Column(name="FE_Id")
  private String fEId;
  @Column(name="REFDOC_Tipo")
  private String rEFDOCTipo;
  @Column(name="REFDOC_Id")
  private String rEFDOCId;
  @Column(name="REFDOC_MotivCode")
  private String rEFDOCMotivCode;
  @Column(name="REFDOC_MotivDesc")
  private String rEFDOCMotivDesc;
  @Column(name="FE_Comentario")
  private String fEComentario;
  @Column(name="DOC_CondPago")
  private String dOCCondPago;
  @Column(name="RET_Regimen")
  private String rETRegimen;
  @Column(name="RET_Tasa")
  private String rETTasa;
  @Column(name="Observaciones")
  private String observaciones;
  @Column(name="ImportePagado")
  private BigDecimal importePagado;
  @Column(name="MonedaPagado")
  private String monedaPagado;
  @Column(name="EstadoRespuesta")
  private String estadoRespuesta;
  @Column(name="MensajeRespuesta")
  private String mensajeRespuesta;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionDocrefers> transaccionDocrefersList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionImpuestos> transaccionImpuestosList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionPropiedades> transaccionPropiedadesList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionComprobantePago> transaccionComprobantePagoList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionLineas> transaccionLineasList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionAnticipo> transaccionAnticipoList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionContractdocref> transaccionContractdocrefList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private List<TransaccionTotales> transaccionTotalesList;
  @OneToOne(cascade={javax.persistence.CascadeType.ALL}, mappedBy="transaccion")
  private TransaccionAnexo transaccionAnexo;
  
  public Transaccion() {}
  
  public Transaccion(String fEId)
  {
    this.fEId = fEId;
  }
  
  public String getDocIdentidadNro()
  {
    return this.docIdentidadNro;
  }
  
  public void setDocIdentidadNro(String docIdentidadNro)
  {
    this.docIdentidadNro = docIdentidadNro;
  }
  
  public String getDocIdentidadTipo()
  {
    return this.docIdentidadTipo;
  }
  
  public void setDocIdentidadTipo(String docIdentidadTipo)
  {
    this.docIdentidadTipo = docIdentidadTipo;
  }
  
  public String getRazonSocial()
  {
    return this.razonSocial;
  }
  
  public void setRazonSocial(String razonSocial)
  {
    this.razonSocial = razonSocial;
  }
  
  public String getNombreComercial()
  {
    return this.nombreComercial;
  }
  
  public void setNombreComercial(String nombreComercial)
  {
    this.nombreComercial = nombreComercial;
  }
  
  public String getPersonContacto()
  {
    return this.personContacto;
  }
  
  public void setPersonContacto(String personContacto)
  {
    this.personContacto = personContacto;
  }
  
  public String getEMail()
  {
    return this.eMail;
  }
  
  public void setEMail(String eMail)
  {
    this.eMail = eMail;
  }
  
  public String getTelefono()
  {
    return this.telefono;
  }
  
  public void setTelefono(String telefono)
  {
    this.telefono = telefono;
  }
  
  public String getTelefono1()
  {
    return this.telefono1;
  }
  
  public void setTelefono1(String telefono1)
  {
    this.telefono1 = telefono1;
  }
  
  public String getWeb()
  {
    return this.web;
  }
  
  public void setWeb(String web)
  {
    this.web = web;
  }
  
  public String getDIRPais()
  {
    return this.dIRPais;
  }
  
  public void setDIRPais(String dIRPais)
  {
    this.dIRPais = dIRPais;
  }
  
  public String getDIRDepartamento()
  {
    return this.dIRDepartamento;
  }
  
  public void setDIRDepartamento(String dIRDepartamento)
  {
    this.dIRDepartamento = dIRDepartamento;
  }
  
  public String getDIRProvincia()
  {
    return this.dIRProvincia;
  }
  
  public void setDIRProvincia(String dIRProvincia)
  {
    this.dIRProvincia = dIRProvincia;
  }
  
  public String getDIRDistrito()
  {
    return this.dIRDistrito;
  }
  
  public void setDIRDistrito(String dIRDistrito)
  {
    this.dIRDistrito = dIRDistrito;
  }
  
  public String getDIRDireccion()
  {
    return this.dIRDireccion;
  }
  
  public void setDIRDireccion(String dIRDireccion)
  {
    this.dIRDireccion = dIRDireccion;
  }
  
  public String getDIRNomCalle()
  {
    return this.dIRNomCalle;
  }
  
  public void setDIRNomCalle(String dIRNomCalle)
  {
    this.dIRNomCalle = dIRNomCalle;
  }
  
  public String getDIRNroCasa()
  {
    return this.dIRNroCasa;
  }
  
  public void setDIRNroCasa(String dIRNroCasa)
  {
    this.dIRNroCasa = dIRNroCasa;
  }
  
  public String getDIRUbigeo()
  {
    return this.dIRUbigeo;
  }
  
  public void setDIRUbigeo(String dIRUbigeo)
  {
    this.dIRUbigeo = dIRUbigeo;
  }
  
  public String getDIRUrbanizacion()
  {
    return this.dIRUrbanizacion;
  }
  
  public void setDIRUrbanizacion(String dIRUrbanizacion)
  {
    this.dIRUrbanizacion = dIRUrbanizacion;
  }
  
  public String getSNDocIdentidadNro()
  {
    return this.sNDocIdentidadNro;
  }
  
  public void setSNDocIdentidadNro(String sNDocIdentidadNro)
  {
    this.sNDocIdentidadNro = sNDocIdentidadNro;
  }
  
  public String getSNDocIdentidadTipo()
  {
    return this.sNDocIdentidadTipo;
  }
  
  public void setSNDocIdentidadTipo(String sNDocIdentidadTipo)
  {
    this.sNDocIdentidadTipo = sNDocIdentidadTipo;
  }
  
  public String getSNRazonSocial()
  {
    return this.sNRazonSocial;
  }
  
  public void setSNRazonSocial(String sNRazonSocial)
  {
    this.sNRazonSocial = sNRazonSocial;
  }
  
  public String getSNNombreComercial()
  {
    return this.sNNombreComercial;
  }
  
  public void setSNNombreComercial(String sNNombreComercial)
  {
    this.sNNombreComercial = sNNombreComercial;
  }
  
  public String getSNEMail()
  {
    return this.sNEMail;
  }
  
  public void setSNEMail(String sNEMail)
  {
    this.sNEMail = sNEMail;
  }
  
  public String getSNSegundoNombre()
  {
    return this.sNSegundoNombre;
  }
  
  public void setSNSegundoNombre(String sNSegundoNombre)
  {
    this.sNSegundoNombre = sNSegundoNombre;
  }
  
  public String getSNDIRPais()
  {
    return this.sNDIRPais;
  }
  
  public void setSNDIRPais(String sNDIRPais)
  {
    this.sNDIRPais = sNDIRPais;
  }
  
  public String getSNDIRDepartamento()
  {
    return this.sNDIRDepartamento;
  }
  
  public void setSNDIRDepartamento(String sNDIRDepartamento)
  {
    this.sNDIRDepartamento = sNDIRDepartamento;
  }
  
  public String getSNDIRProvincia()
  {
    return this.sNDIRProvincia;
  }
  
  public void setSNDIRProvincia(String sNDIRProvincia)
  {
    this.sNDIRProvincia = sNDIRProvincia;
  }
  
  public String getSNDIRDistrito()
  {
    return this.sNDIRDistrito;
  }
  
  public void setSNDIRDistrito(String sNDIRDistrito)
  {
    this.sNDIRDistrito = sNDIRDistrito;
  }
  
  public String getSNDIRDireccion()
  {
    return this.sNDIRDireccion;
  }
  
  public void setSNDIRDireccion(String sNDIRDireccion)
  {
    this.sNDIRDireccion = sNDIRDireccion;
  }
  
  public String getSNDIRNomCalle()
  {
    return this.sNDIRNomCalle;
  }
  
  public void setSNDIRNomCalle(String sNDIRNomCalle)
  {
    this.sNDIRNomCalle = sNDIRNomCalle;
  }
  
  public String getSNDIRNroCasa()
  {
    return this.sNDIRNroCasa;
  }
  
  public void setSNDIRNroCasa(String sNDIRNroCasa)
  {
    this.sNDIRNroCasa = sNDIRNroCasa;
  }
  
  public String getSNDIRUbigeo()
  {
    return this.sNDIRUbigeo;
  }
  
  public void setSNDIRUbigeo(String sNDIRUbigeo)
  {
    this.sNDIRUbigeo = sNDIRUbigeo;
  }
  
  public String getSNDIRUrbanizacion()
  {
    return this.sNDIRUrbanizacion;
  }
  
  public void setSNDIRUrbanizacion(String sNDIRUrbanizacion)
  {
    this.sNDIRUrbanizacion = sNDIRUrbanizacion;
  }
  
  public String getDOCSerie()
  {
    return this.dOCSerie;
  }
  
  public void setDOCSerie(String dOCSerie)
  {
    this.dOCSerie = dOCSerie;
  }
  
  public String getDOCNumero()
  {
    return this.dOCNumero;
  }
  
  public void setDOCNumero(String dOCNumero)
  {
    this.dOCNumero = dOCNumero;
  }
  
  public String getDOCId()
  {
    return this.dOCId;
  }
  
  public void setDOCId(String dOCId)
  {
    this.dOCId = dOCId;
  }
  
  public Date getDOCFechaEmision()
  {
    return this.dOCFechaEmision;
  }
  
  public void setDOCFechaEmision(Date dOCFechaEmision)
  {
    this.dOCFechaEmision = dOCFechaEmision;
  }
  
  public Date getDOCFechaVencimiento()
  {
    return this.dOCFechaVencimiento;
  }
  
  public void setDOCFechaVencimiento(Date dOCFechaVencimiento)
  {
    this.dOCFechaVencimiento = dOCFechaVencimiento;
  }
  
  public String getDOCDscrpcion()
  {
    return this.dOCDscrpcion;
  }
  
  public void setDOCDscrpcion(String dOCDscrpcion)
  {
    this.dOCDscrpcion = dOCDscrpcion;
  }
  
  public String getDOCCodigo()
  {
    return this.dOCCodigo;
  }
  
  public void setDOCCodigo(String dOCCodigo)
  {
    this.dOCCodigo = dOCCodigo;
  }
  
  public String getDOCMONNombre()
  {
    return this.dOCMONNombre;
  }
  
  public void setDOCMONNombre(String dOCMONNombre)
  {
    this.dOCMONNombre = dOCMONNombre;
  }
  
  public String getDOCMONCodigo()
  {
    return this.dOCMONCodigo;
  }
  
  public void setDOCMONCodigo(String dOCMONCodigo)
  {
    this.dOCMONCodigo = dOCMONCodigo;
  }
  
  public BigDecimal getDOCMontoTotal()
  {
    return this.dOCMontoTotal;
  }
  
  public void setDOCMontoTotal(BigDecimal dOCMontoTotal)
  {
    this.dOCMontoTotal = dOCMontoTotal;
  }
  
  public BigDecimal getDOCDescuentoTotal()
  {
    return this.dOCDescuentoTotal;
  }
  
  public void setDOCDescuentoTotal(BigDecimal dOCDescuentoTotal)
  {
    this.dOCDescuentoTotal = dOCDescuentoTotal;
  }
  
  public BigDecimal getDOCImporte()
  {
    return this.dOCImporte;
  }
  
  public void setDOCImporte(BigDecimal dOCImporte)
  {
    this.dOCImporte = dOCImporte;
  }
  
  public BigDecimal getANTICIPOMonto()
  {
    return this.aNTICIPOMonto;
  }
  
  public void setANTICIPOMonto(BigDecimal aNTICIPOMonto)
  {
    this.aNTICIPOMonto = aNTICIPOMonto;
  }
  
  public String getSUNATTransact()
  {
    return this.sUNATTransact;
  }
  
  public void setSUNATTransact(String sUNATTransact)
  {
    this.sUNATTransact = sUNATTransact;
  }
  
  public String getFEEstado()
  {
    return this.fEEstado;
  }
  
  public void setFEEstado(String fEEstado)
  {
    this.fEEstado = fEEstado;
  }
  
  public String getFETipoTrans()
  {
    return this.fETipoTrans;
  }
  
  public void setFETipoTrans(String fETipoTrans)
  {
    this.fETipoTrans = fETipoTrans;
  }
  
  public String getFEId()
  {
    return this.fEId;
  }
  
  public void setFEId(String fEId)
  {
    this.fEId = fEId;
  }
  
  public String getREFDOCTipo()
  {
    return this.rEFDOCTipo;
  }
  
  public void setREFDOCTipo(String rEFDOCTipo)
  {
    this.rEFDOCTipo = rEFDOCTipo;
  }
  
  public String getREFDOCId()
  {
    return this.rEFDOCId;
  }
  
  public void setREFDOCId(String rEFDOCId)
  {
    this.rEFDOCId = rEFDOCId;
  }
  
  public String getREFDOCMotivCode()
  {
    return this.rEFDOCMotivCode;
  }
  
  public void setREFDOCMotivCode(String rEFDOCMotivCode)
  {
    this.rEFDOCMotivCode = rEFDOCMotivCode;
  }
  
  public String getREFDOCMotivDesc()
  {
    return this.rEFDOCMotivDesc;
  }
  
  public void setREFDOCMotivDesc(String rEFDOCMotivDesc)
  {
    this.rEFDOCMotivDesc = rEFDOCMotivDesc;
  }
  
  public String getFEComentario()
  {
    return this.fEComentario;
  }
  
  public void setFEComentario(String fEComentario)
  {
    this.fEComentario = fEComentario;
  }
  
  public String getDOCCondPago()
  {
    return this.dOCCondPago;
  }
  
  public void setDOCCondPago(String dOCCondPago)
  {
    this.dOCCondPago = dOCCondPago;
  }
  
  public String getRETRegimen()
  {
    return this.rETRegimen;
  }
  
  public void setRETRegimen(String rETRegimen)
  {
    this.rETRegimen = rETRegimen;
  }
  
  public String getRETTasa()
  {
    return this.rETTasa;
  }
  
  public void setRETTasa(String rETTasa)
  {
    this.rETTasa = rETTasa;
  }
  
  public String getObservaciones()
  {
    return this.observaciones;
  }
  
  public void setObservaciones(String observaciones)
  {
    this.observaciones = observaciones;
  }
  
  public BigDecimal getImportePagado()
  {
    return this.importePagado;
  }
  
  public void setImportePagado(BigDecimal importePagado)
  {
    this.importePagado = importePagado;
  }
  
  public String getMonedaPagado()
  {
    return this.monedaPagado;
  }
  
  public void setMonedaPagado(String monedaPagado)
  {
    this.monedaPagado = monedaPagado;
  }
  
  public String getEstadoRespuesta()
  {
    return this.estadoRespuesta;
  }
  
  public void setEstadoRespuesta(String estadoRespuesta)
  {
    this.estadoRespuesta = estadoRespuesta;
  }
  
  public String getMensajeRespuesta()
  {
    return this.mensajeRespuesta;
  }
  
  public void setMensajeRespuesta(String mensajeRespuesta)
  {
    this.mensajeRespuesta = mensajeRespuesta;
  }
  
  @XmlTransient
  public List<TransaccionDocrefers> getTransaccionDocrefersList()
  {
    return this.transaccionDocrefersList;
  }
  
  public void setTransaccionDocrefersList(List<TransaccionDocrefers> transaccionDocrefersList)
  {
    this.transaccionDocrefersList = transaccionDocrefersList;
  }
  
  @XmlTransient
  public List<TransaccionImpuestos> getTransaccionImpuestosList()
  {
    return this.transaccionImpuestosList;
  }
  
  public void setTransaccionImpuestosList(List<TransaccionImpuestos> transaccionImpuestosList)
  {
    this.transaccionImpuestosList = transaccionImpuestosList;
  }
  
  @XmlTransient
  public List<TransaccionPropiedades> getTransaccionPropiedadesList()
  {
    return this.transaccionPropiedadesList;
  }
  
  public void setTransaccionPropiedadesList(List<TransaccionPropiedades> transaccionPropiedadesList)
  {
    this.transaccionPropiedadesList = transaccionPropiedadesList;
  }
  
  @XmlTransient
  public List<TransaccionComprobantePago> getTransaccionComprobantePagoList()
  {
    return this.transaccionComprobantePagoList;
  }
  
  public void setTransaccionComprobantePagoList(List<TransaccionComprobantePago> transaccionComprobantePagoList)
  {
    this.transaccionComprobantePagoList = transaccionComprobantePagoList;
  }
  
  @XmlTransient
  public List<TransaccionLineas> getTransaccionLineasList()
  {
    return this.transaccionLineasList;
  }
  
  public void setTransaccionLineasList(List<TransaccionLineas> transaccionLineasList)
  {
    this.transaccionLineasList = transaccionLineasList;
  }
  
  @XmlTransient
  public List<TransaccionAnticipo> getTransaccionAnticipoList()
  {
    return this.transaccionAnticipoList;
  }
  
  public void setTransaccionAnticipoList(List<TransaccionAnticipo> transaccionAnticipoList)
  {
    this.transaccionAnticipoList = transaccionAnticipoList;
  }
  
  @XmlTransient
  public List<TransaccionContractdocref> getTransaccionContractdocrefList()
  {
    return this.transaccionContractdocrefList;
  }
  
  public void setTransaccionContractdocrefList(List<TransaccionContractdocref> transaccionContractdocrefList)
  {
    this.transaccionContractdocrefList = transaccionContractdocrefList;
  }
  
  @XmlTransient
  public List<TransaccionTotales> getTransaccionTotalesList()
  {
    return this.transaccionTotalesList;
  }
  
  public void setTransaccionTotalesList(List<TransaccionTotales> transaccionTotalesList)
  {
    this.transaccionTotalesList = transaccionTotalesList;
  }
  
  public TransaccionAnexo getTransaccionAnexo()
  {
    return this.transaccionAnexo;
  }
  
  public void setTransaccionAnexo(TransaccionAnexo transaccionAnexo)
  {
    this.transaccionAnexo = transaccionAnexo;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.fEId != null ? this.fEId.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof Transaccion)) {
      return false;
    }
    Transaccion other = (Transaccion)object;
    if (((this.fEId == null) && (other.fEId != null)) || ((this.fEId != null) && (!this.fEId.equals(other.fEId)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "org.ventura.cpe.genero.hb.Transaccion[ fEId=" + this.fEId + " ]";
  }
}
