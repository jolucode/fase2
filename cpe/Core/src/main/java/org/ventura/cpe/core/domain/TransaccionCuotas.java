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
import java.util.Objects;

/**
 * @author joseLuis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "TRANSACCION_CUOTAS")
@XmlRootElement
public class TransaccionCuotas implements Serializable {

    private static final long serialVersionUID = 1L;

/*
    @Id
    @Column(name = "FE_Id")
    private String fEId;
*/

    @NonNull
    @EmbeddedId
    protected TransaccionCuotasPK transaccionLineasPK;


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

    //@JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id" , insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id" , insertable = false, updatable = false)
    private Transaccion transaccion;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionCuotas that = (TransaccionCuotas) o;
        return Objects.equals(transaccionLineasPK, that.transaccionLineasPK) && Objects.equals(cuota, that.cuota) && Objects.equals(fechaCuota, that.fechaCuota) && Objects.equals(montoCuota, that.montoCuota) && Objects.equals(fechaEmision, that.fechaEmision) && Objects.equals(formaPago, that.formaPago) && Objects.equals(transaccion, that.transaccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaccionLineasPK, cuota, fechaCuota, montoCuota, fechaEmision, formaPago, transaccion);
    }

    @Override
    public String toString() {
        return "TransaccionCuotas{" +
                "transaccionLineasPK=" + transaccionLineasPK +
                ", cuota='" + cuota + '\'' +
                ", fechaCuota=" + fechaCuota +
                ", montoCuota=" + montoCuota +
                ", fechaEmision=" + fechaEmision +
                ", formaPago='" + formaPago + '\'' +
                ", transaccion=" + transaccion +
                '}';
    }

}
