/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author joseLuis
 */
@Data
@Table(name = "TRANSACCION_CUOTAS")
@XmlRootElement
@Entity
public class TransaccionCuotas implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionLineasPK transaccionLineasPK;

    @Column(name = "Cuota")
    private String cuota;

    @Column(name = "FechaCuota")
    @Temporal(TemporalType.DATE)
    private Date fechaCuota;

    @Column(name = "MontoCuota")
    private BigDecimal montoCuota;

    @Column(name = "FechaEmision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Column(name = "FormaPago")
    private String formaPago;

    @ManyToOne()
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    private Transaccion transaccion;

    public TransaccionCuotas() {
    }

    public TransaccionCuotas(TransaccionLineasPK transaccionLineasPK, String cuota, Date fechaCuota, BigDecimal montoCuota, Date fechaEmision, String formaPago, Transaccion transaccion) {
        this.transaccionLineasPK = transaccionLineasPK;
        this.cuota = cuota;
        this.fechaCuota = fechaCuota;
        this.montoCuota = montoCuota;
        this.fechaEmision = fechaEmision;
        this.formaPago = formaPago;
        this.transaccion = transaccion;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public TransaccionLineasPK getTransaccionLineasPK() {
        return transaccionLineasPK;
    }

    public void setTransaccionLineasPK(TransaccionLineasPK transaccionLineasPK) {
        this.transaccionLineasPK = transaccionLineasPK;
    }

    public String getCuota() {
        return cuota;
    }

    public void setCuota(String cuota) {
        this.cuota = cuota;
    }

    public Date getFechaCuota() {
        return fechaCuota;
    }

    public void setFechaCuota(Date fechaCuota) {
        this.fechaCuota = fechaCuota;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
}
