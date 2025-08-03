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
import java.util.Date;
import java.util.List;

/**
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_LINEAS")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionLineas.findAll", query = "SELECT t FROM TransaccionLineas t"),
        @NamedQuery(name = "TransaccionLineas.findByFEId", query = "SELECT t FROM TransaccionLineas t WHERE t.transaccionLineasPK.fEId = :fEId"),
        @NamedQuery(name = "TransaccionLineas.findByNroOrden", query = "SELECT t FROM TransaccionLineas t WHERE t.transaccionLineasPK.nroOrden = :nroOrden"),
        @NamedQuery(name = "TransaccionLineas.findByDSCTOPorcentaje", query = "SELECT t FROM TransaccionLineas t WHERE t.dSCTOPorcentaje = :dSCTOPorcentaje"),
        @NamedQuery(name = "TransaccionLineas.findByDSCTOMonto", query = "SELECT t FROM TransaccionLineas t WHERE t.dSCTOMonto = :dSCTOMonto"),
        @NamedQuery(name = "TransaccionLineas.findByDescripcion", query = "SELECT t FROM TransaccionLineas t WHERE t.descripcion = :descripcion"),
        @NamedQuery(name = "TransaccionLineas.findByPrecioRefMonto", query = "SELECT t FROM TransaccionLineas t WHERE t.precioRefMonto = :precioRefMonto"),
        @NamedQuery(name = "TransaccionLineas.findByPrecioRefCodigo", query = "SELECT t FROM TransaccionLineas t WHERE t.precioRefCodigo = :precioRefCodigo"),
        @NamedQuery(name = "TransaccionLineas.findByCantidad", query = "SELECT t FROM TransaccionLineas t WHERE t.cantidad = :cantidad"),
        @NamedQuery(name = "TransaccionLineas.findByUnidad", query = "SELECT t FROM TransaccionLineas t WHERE t.unidad = :unidad")})
public class TransaccionLineas implements Serializable {

    @Column(name = "CodSunat")
    private String codSunat;

    @Column(name = "CodProdGS1")
    private String codProdGS1;


    @Column(name = "CodUbigeoOrigen")
    private String codUbigeoOrigen;

    @Column(name = "DirecOrigen")
    private String direcOrigen;

    @Column(name = "CodUbigeoDestino")
    private String codUbigeoDestino;

    @Column(name = "DirecDestino")
    private String direcDestino;

    @Column(name = "DetalleViaje")
    private String detalleViaje;

    @Column(name = "ValorTransporte")
    private BigDecimal valorTransporte;

    @Column(name = "ValorCargaEfectiva")
    private BigDecimal valorCargaEfectiva;

    @Column(name = "ValorCargaUtil")
    private BigDecimal valorCargaUtil;

    @Column(name = "ConfVehicular")
    private String confVehicular;

    @Column(name = "CUtilVehiculo")
    private BigDecimal cUtilVehiculo;

    @Column(name = "CEfectivaVehiculo")
    private BigDecimal cEfectivaVehiculo;

    @Column(name = "ValorRefTM")
    private BigDecimal valorRefTM;

    @Column(name = "ValorPreRef")
    private BigDecimal valorPreRef;

    @Column(name = "FactorRetorno")
    private String factorRetorno;


    @Column(name = "NombreEmbarcacion")
    private String nombreEmbarcacion;

    @Column(name = "TipoEspecieVendida")
    private String tipoEspeciaVendida;


    @Column(name = "LugarDescarga")
    private String lugarDescarga;

    @Temporal(TemporalType.DATE)
    @Column(name = "FechaDescarga")
    private Date fechaDescarga;

    @Column(name = "CantidadEspecieVendida")
    private BigDecimal cantidadEspecieVendida;


    @Column(name = "TotalLineaConIGV")
    private BigDecimal totalLineaConIGV;

    @Column(name = "UnidadSunat")
    private String unidadSunat;

    @Column(name = "CodArticulo")
    private String codArticulo;

    @Column(name = "PrecioIGV")
    private BigDecimal precioIGV;

    @Column(name = "PrecioDscto")
    private BigDecimal precioDscto;

    @Column(name = "TotalLineaSinIGV")
    private BigDecimal totalLineaSinIGV;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineasBillref> transaccionLineasBillrefList;

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionLineasPK transaccionLineasPK;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DSCTO_Porcentaje")
    private BigDecimal dSCTOPorcentaje;

    @Column(name = "DSCTO_Monto")
    private BigDecimal dSCTOMonto;

    @Column(name = "Descripcion")
    private String descripcion;

    @Column(name = "PrecioRef_Monto")
    private BigDecimal precioRefMonto;

    @Column(name = "PrecioRef_Codigo")
    private String precioRefCodigo;

    @Column(name = "Cantidad")
    private BigDecimal cantidad;

    @Column(name = "Unidad")
    private String unidad;

    @Column(name = "TotalBruto")
    private BigDecimal totalBruto;

    @Column(name = "LineaImpuesto")
    private BigDecimal lineaImpuesto;

    //GUIAS REST SUNAT
    @Column(name = "SubPartida")
    private String subPartida;

    @Column(name = "IndicadorBien")
    private String indicadorBien;

    @Column(name = "Numeracion")
    private String numeracion;

    @Column(name = "NumeroSerie")
    private String numeroSerie;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineaImpuestos> transaccionLineaImpuestosList;

    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineasUsucampos> transaccionLineasUsucamposList;

    /*
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionLineas")
    private List<TransaccionLineasDetracciones> transaccionLineasDetraccionesList;
*/
    public TransaccionLineas() {
    }

    public TransaccionLineas(TransaccionLineasPK transaccionLineasPK) {
        this.transaccionLineasPK = transaccionLineasPK;
    }

    public TransaccionLineas(String fEId, int nroOrden) {
        this.transaccionLineasPK = new TransaccionLineasPK(fEId, nroOrden);
    }

    public TransaccionLineasPK getTransaccionLineasPK() {
        return transaccionLineasPK;
    }

    public void setTransaccionLineasPK(TransaccionLineasPK transaccionLineasPK) {
        this.transaccionLineasPK = transaccionLineasPK;
    }

    public BigDecimal getDSCTOPorcentaje() {
        return dSCTOPorcentaje;
    }

    public void setDSCTOPorcentaje(BigDecimal dSCTOPorcentaje) {
        this.dSCTOPorcentaje = dSCTOPorcentaje;
    }

    public BigDecimal getDSCTOMonto() {
        return dSCTOMonto;
    }

    public void setDSCTOMonto(BigDecimal dSCTOMonto) {
        this.dSCTOMonto = dSCTOMonto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioRefMonto() {
        return precioRefMonto;
    }

    public void setPrecioRefMonto(BigDecimal precioRefMonto) {
        this.precioRefMonto = precioRefMonto;
    }

    public String getPrecioRefCodigo() {
        return precioRefCodigo;
    }

    public void setPrecioRefCodigo(String precioRefCodigo) {
        this.precioRefCodigo = precioRefCodigo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getTotalLineaConIGV() {
        return totalLineaConIGV;
    }

    public void setTotalLineaConIGV(BigDecimal totalLineaConIGV) {
        this.totalLineaConIGV = totalLineaConIGV;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getLineaImpuesto() {
        return lineaImpuesto;
    }

    public void setLineaImpuesto(BigDecimal lineaImpuesto) {
        this.lineaImpuesto = lineaImpuesto;
    }

    public String getSubPartida() {
        return subPartida;
    }

    public void setSubPartida(String subPartida) {
        this.subPartida = subPartida;
    }

    public String getIndicadorBien() {
        return indicadorBien;
    }

    public void setIndicadorBien(String indicadorBien) {
        this.indicadorBien = indicadorBien;
    }

    public String getNumeracion() {
        return numeracion;
    }

    public void setNumeracion(String numeracion) {
        this.numeracion = numeracion;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    @XmlTransient
    public List<TransaccionLineaImpuestos> getTransaccionLineaImpuestosList() {
        return transaccionLineaImpuestosList;
    }

    public void setTransaccionLineaImpuestosList(List<TransaccionLineaImpuestos> transaccionLineaImpuestosList) {
        this.transaccionLineaImpuestosList = transaccionLineaImpuestosList;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @XmlTransient
    public List<TransaccionLineasUsucampos> getTransaccionLineasUsucamposList() {
        return transaccionLineasUsucamposList;
    }

    public void setTransaccionLineasUsucamposList(List<TransaccionLineasUsucampos> transaccionLineasUsucamposList) {
        this.transaccionLineasUsucamposList = transaccionLineasUsucamposList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineasPK != null ? transaccionLineasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineas)) {
            return false;
        }
        TransaccionLineas other = (TransaccionLineas) object;
        if ((this.transaccionLineasPK == null && other.transaccionLineasPK != null) || (this.transaccionLineasPK != null && !this.transaccionLineasPK.equals(other.transaccionLineasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionLineas[ transaccionLineasPK=" + transaccionLineasPK + " ]";
    }

    @XmlTransient
    public List<TransaccionLineasBillref> getTransaccionLineasBillrefList() {
        return transaccionLineasBillrefList;
    }

    public void setTransaccionLineasBillrefList(List<TransaccionLineasBillref> transaccionLineasBillrefList) {
        this.transaccionLineasBillrefList = transaccionLineasBillrefList;
    }

    public BigDecimal getPrecioIGV() {
        return precioIGV;
    }

    public void setPrecioIGV(BigDecimal precioIGV) {
        this.precioIGV = precioIGV;
    }

    public BigDecimal getTotalLineaSinIGV() {
        return totalLineaSinIGV;
    }

    public void setTotalLineaSinIGV(BigDecimal totalLineaSinIGV) {
        this.totalLineaSinIGV = totalLineaSinIGV;
    }

    /**
     * @return the precioDscto
     */
    public BigDecimal getPrecioDscto() {
        return precioDscto;
    }

    /**
     * @param precioDscto the precioDscto to set
     */
    public void setPrecioDscto(BigDecimal precioDscto) {
        this.precioDscto = precioDscto;
    }

    public String getCodArticulo() {
        return codArticulo;
    }

    public void setCodArticulo(String codArticulo) {
        this.codArticulo = codArticulo;
    }

    public String getUnidadSunat() {
        return unidadSunat;
    }

    public void setUnidadSunat(String unidadSunat) {
        this.unidadSunat = unidadSunat;
    }

    public String getCodSunat() {
        return codSunat;
    }

    public void setCodSunat(String codSunat) {
        this.codSunat = codSunat;
    }

    public String getCodProdGS1() {
        return codProdGS1;
    }

    public void setCodProdGS1(String codProdGS1) {
        this.codProdGS1 = codProdGS1;
    }

    public String getCodUbigeoOrigen() {
        return codUbigeoOrigen;
    }

    public void setCodUbigeoOrigen(String codUbigeoOrigen) {
        this.codUbigeoOrigen = codUbigeoOrigen;
    }

    public String getDirecOrigen() {
        return direcOrigen;
    }

    public void setDirecOrigen(String direcOrigen) {
        this.direcOrigen = direcOrigen;
    }

    public String getCodUbigeoDestino() {
        return codUbigeoDestino;
    }

    public void setCodUbigeoDestino(String codUbigeoDestino) {
        this.codUbigeoDestino = codUbigeoDestino;
    }

    public String getDirecDestino() {
        return direcDestino;
    }

    public void setDirecDestino(String direcDestino) {
        this.direcDestino = direcDestino;
    }

    public String getDetalleViaje() {
        return detalleViaje;
    }

    public void setDetalleViaje(String detalleViaje) {
        this.detalleViaje = detalleViaje;
    }

    public BigDecimal getValorTransporte() {
        return valorTransporte;
    }

    public void setValorTransporte(BigDecimal valorTransporte) {
        this.valorTransporte = valorTransporte;
    }

    public BigDecimal getValorCargaEfectiva() {
        return valorCargaEfectiva;
    }

    public void setValorCargaEfectiva(BigDecimal valorCargaEfectiva) {
        this.valorCargaEfectiva = valorCargaEfectiva;
    }

    public BigDecimal getValorCargaUtil() {
        return valorCargaUtil;
    }

    public void setValorCargaUtil(BigDecimal valorCargaUtil) {
        this.valorCargaUtil = valorCargaUtil;
    }

    public String getConfVehicular() {
        return confVehicular;
    }

    public void setConfVehicular(String confVehicular) {
        this.confVehicular = confVehicular;
    }

    public BigDecimal getcUtilVehiculo() {
        return cUtilVehiculo;
    }

    public void setcUtilVehiculo(BigDecimal cUtilVehiculo) {
        this.cUtilVehiculo = cUtilVehiculo;
    }

    public BigDecimal getcEfectivaVehiculo() {
        return cEfectivaVehiculo;
    }

    public void setcEfectivaVehiculo(BigDecimal cEfectivaVehiculo) {
        this.cEfectivaVehiculo = cEfectivaVehiculo;
    }

    public BigDecimal getValorRefTM() {
        return valorRefTM;
    }

    public void setValorRefTM(BigDecimal valorRefTM) {
        this.valorRefTM = valorRefTM;
    }

    public BigDecimal getValorPreRef() {
        return valorPreRef;
    }

    public void setValorPreRef(BigDecimal valorPreRef) {
        this.valorPreRef = valorPreRef;
    }

    public String getFactorRetorno() {
        return factorRetorno;
    }

    public void setFactorRetorno(String factorRetorno) {
        this.factorRetorno = factorRetorno;
    }


    public String getNombreEmbarcacion() {
        return nombreEmbarcacion;
    }

    public void setNombreEmbarcacion(String nombreEmbarcacion) {
        this.nombreEmbarcacion = nombreEmbarcacion;
    }

    public String getTipoEspeciaVendida() {
        return tipoEspeciaVendida;
    }

    public void setTipoEspeciaVendida(String tipoEspeciaVendida) {
        this.tipoEspeciaVendida = tipoEspeciaVendida;
    }

    public String getLugarDescarga() {
        return lugarDescarga;
    }

    public void setLugarDescarga(String lugarDescarga) {
        this.lugarDescarga = lugarDescarga;
    }

    public BigDecimal getCantidadEspecieVendida() {
        return cantidadEspecieVendida;
    }

    public void setCantidadEspecieVendida(BigDecimal cantidadEspecieVendida) {
        this.cantidadEspecieVendida = cantidadEspecieVendida;
    }

    public BigDecimal getdSCTOPorcentaje() {
        return dSCTOPorcentaje;
    }

    public void setdSCTOPorcentaje(BigDecimal dSCTOPorcentaje) {
        this.dSCTOPorcentaje = dSCTOPorcentaje;
    }

    public BigDecimal getdSCTOMonto() {
        return dSCTOMonto;
    }

    public void setdSCTOMonto(BigDecimal dSCTOMonto) {
        this.dSCTOMonto = dSCTOMonto;
    }

    public Date getFechaDescarga() {
        return fechaDescarga;
    }

    public void setFechaDescarga(Date fechaDescarga) {
        this.fechaDescarga = fechaDescarga;
    }
}
