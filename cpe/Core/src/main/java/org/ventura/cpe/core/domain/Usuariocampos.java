/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author Yosmel
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "USUARIOCAMPOS", uniqueConstraints = {@UniqueConstraint(name = "USUARIO_CAMPOS_NOMBRE", columnNames = {"Nombre"})})
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Usuariocampos.findLastId", query = "SELECT MAX(u.id) FROM Usuariocampos u")
})
public class Usuariocampos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Id", insertable = false)
    @GenericGenerator(name = "sequence_dep_id", strategy = "org.ventura.cpe.core.util.UsuarioCamposIdGenerator")
    @GeneratedValue(generator = "sequence_dep_id")
    private Integer id;

    @Column(name = "Nombre", unique = true)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionComprobantepagoUsuario> transaccionComprobantepagoUsuarios;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionContractdocref> transaccionContractdocrefs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionUsucampos> transaccionUsucampos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionLineasUsucampos> transaccionLineasUsucampos;

    @Override
    public String toString() {
        return "org.ventura.cpe.core.dto.hb.Usuariocampos[ id=" + id + " ]";
    }
}
