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
 * @author Yosmel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "TRANSACCION_DOCREFERS")
@XmlRootElement
public class TransaccionDocrefers implements Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    @EmbeddedId
    protected TransaccionDocrefersPK transaccionDocrefersPK;

    @Column(name = "Tipo")
    private String tipo;

    @Column(name = "Id")
    private String id;

    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    @Override
    public String toString() {
        return "org.ventura.cpe.core.dto.hb.TransaccionDocrefers[ transaccionDocrefersPK=" + transaccionDocrefersPK + " ]";
    }

}
