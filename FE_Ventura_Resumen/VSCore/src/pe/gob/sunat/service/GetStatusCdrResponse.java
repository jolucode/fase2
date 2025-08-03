
package pe.gob.sunat.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="statusCdr" type="{http://service.sunat.gob.pe}statusCdrResponse" minOccurs="0" form="unqualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "statusCdr"
})
@XmlRootElement(name = "getStatusCdrResponse")
public class GetStatusCdrResponse {

    protected StatusCdrResponse statusCdr;

    /**
     * Obtiene el valor de la propiedad statusCdr.
     * 
     * @return
     *     possible object is
     *     {@link StatusCdrResponse }
     *     
     */
    public StatusCdrResponse getStatusCdr() {
        return statusCdr;
    }

    /**
     * Define el valor de la propiedad statusCdr.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCdrResponse }
     *     
     */
    public void setStatusCdr(StatusCdrResponse value) {
        this.statusCdr = value;
    }

}
