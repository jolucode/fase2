package org.ventura.cpe.loaderbl;

import lombok.ToString;
import org.ventura.cpe.excepciones.VenturaExcepcion;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.util.Optional;

@ToString
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

    public String normalValue() throws VenturaExcepcion {
        value = Optional.ofNullable(value).orElseThrow(() -> new VenturaExcepcion("No se pudo cargar el valor del parametro."));
        encriptado = encriptado && canDecrypt(value);
        return encriptado ? Criptor.Desencriptar(value) : value;
    }

    private boolean canDecrypt(String text) {
        try {
            Criptor.Desencriptar(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
