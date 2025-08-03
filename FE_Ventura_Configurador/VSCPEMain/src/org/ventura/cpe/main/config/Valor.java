package org.ventura.cpe.main.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Valor {

    private Boolean encriptado;

    private String value;

    public Boolean getEncriptado() {
        return encriptado;
    }

    @XmlAttribute
    public void setEncriptado(Boolean encriptado) {
        this.encriptado = encriptado;
    }

    public String getValue() {
        return value;
    }

    @XmlValue
    public void setValue(String value) {
        this.value = value;
    }

    public String normalValue() {
        return encriptado ? Criptor.Desencriptar(value) : value;
    }
}
