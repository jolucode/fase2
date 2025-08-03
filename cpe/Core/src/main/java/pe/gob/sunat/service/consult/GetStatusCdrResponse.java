
package pe.gob.sunat.service.consult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for getStatusCdrResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getStatusCdrResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="statusCdr" type="{http://service.sunat.gob.pe}statusResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStatusCdrResponse", propOrder = {
    "statusCdr"
})
public class GetStatusCdrResponse
{
    protected StatusResponse statusCdr;
    
    
    /**
     * Gets the value of the statusCdr property.
     * 
     * @return
     *     possible object is
     *     {@link StatusResponse }
     *     
     */
    public StatusResponse getStatusCdr()
    {
        return statusCdr;
    } //getStatusCdr
    
    /**
     * Sets the value of the statusCdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusResponse }
     *     
     */
    public void setStatusCdr(StatusResponse value)
    {
        this.statusCdr = value;
    } //setStatusCdr

} //GetStatusCdrResponse
