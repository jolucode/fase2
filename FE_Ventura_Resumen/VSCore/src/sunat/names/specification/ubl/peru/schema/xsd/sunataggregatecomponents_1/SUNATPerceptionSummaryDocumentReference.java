/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType;

/**
 *
 * @author Vic Aguilar
 */

@XmlRootElement(name="SUNATPerceptionSummaryDocumentReference")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SUNATPerceptionSummaryDocumentReferenceType", propOrder = {
    "sUNATPerceptionSystemCode",
    "sUNATPerceptionPercent",
    "totalInvoiceAmount",
    "sUNATTotalCashed",
    "taxableAmount"
})
public class SUNATPerceptionSummaryDocumentReference implements Serializable{
 
    @XmlElement(name = "SUNATPerceptionSystemCode",namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
    protected SUNATPerceptionSystemCodeType sUNATPerceptionSystemCode;
    @XmlElement(name = "SUNATPerceptionPercent",namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
    protected SUNATPerceptionPercentType sUNATPerceptionPercent;
    @XmlElement(name = "TotalInvoiceAmount",namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected TotalInvoiceAmountType totalInvoiceAmount;
    @XmlElement(name = "SUNATTotalCashed",namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
    protected SUNATTotalCashedType sUNATTotalCashed;
    @XmlElement(name = "TaxableAmount",namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected TaxableAmountType taxableAmount;
    
        public TotalInvoiceAmountType getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(TotalInvoiceAmountType totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }

    public SUNATPerceptionSystemCodeType getsUNATPerceptionSystemCode() {
        return sUNATPerceptionSystemCode;
    }

    public void setsUNATPerceptionSystemCode(SUNATPerceptionSystemCodeType sUNATPerceptionSystemCode) {
        this.sUNATPerceptionSystemCode = sUNATPerceptionSystemCode;
    }

    public SUNATPerceptionPercentType getsUNATPerceptionPercent() {
        return sUNATPerceptionPercent;
    }

    public void setsUNATPerceptionPercent(SUNATPerceptionPercentType sUNATPerceptionPercent) {
        this.sUNATPerceptionPercent = sUNATPerceptionPercent;
    }

    public SUNATTotalCashedType getsUNATTotalCashed() {
        return sUNATTotalCashed;
    }

    public void setsUNATTotalCashed(SUNATTotalCashedType sUNATTotalCashed) {
        this.sUNATTotalCashed = sUNATTotalCashed;
    }

    public TaxableAmountType getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(TaxableAmountType taxableAmount) {
        this.taxableAmount = taxableAmount;
    }
    
}
