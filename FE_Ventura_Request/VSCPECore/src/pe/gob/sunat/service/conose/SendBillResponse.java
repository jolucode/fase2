
package pe.gob.sunat.service.conose;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applicationResponse" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" form="unqualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "applicationResponse"
})
@XmlRootElement(name = "sendBillResponse")
public class SendBillResponse {

    protected byte[] applicationResponse;

    /**
     * Obtiene el valor de la propiedad applicationResponse.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getApplicationResponse() {
        return applicationResponse;
    }

    /**
     * Define el valor de la propiedad applicationResponse.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setApplicationResponse(byte[] value) {
        this.applicationResponse = value;
    }

}
