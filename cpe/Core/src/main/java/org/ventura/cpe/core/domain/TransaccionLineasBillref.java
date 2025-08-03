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

/**
 * @author VSUser
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "TRANSACCION_LINEAS_BILLREF")
@XmlRootElement
public class TransaccionLineasBillref implements Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    @EmbeddedId
    protected TransaccionLineasBillrefPK transaccionLineasBillrefPK;

    @Column(name = "AdtDocRef_Id")
    private String adtDocRefId;

    @Column(name = "AdtDocRef_SchemaId")
    private String adtDocRefSchemaId;

    @Column(name = "InvDocRef_DocTypeCode")
    private String invDocRefDocTypeCode;

    @Column(name = "InvDocRef_Id")
    private String invDocRefId;

    @JoinColumns({
            @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
            @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionLineas transaccionLineas;

    @Override
    public String toString() {
        return "TransaccionLineasBillref[ transaccionLineasBillrefPK=" + transaccionLineasBillrefPK + " ]";
    }

}
