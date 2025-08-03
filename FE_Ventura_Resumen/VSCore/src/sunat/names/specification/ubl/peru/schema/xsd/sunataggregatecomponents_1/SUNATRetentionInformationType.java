package sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExchangeRateType;



/**
 * 
 *         
 * <pre>
 * &lt;?xml version="1.0" encoding="ISO-8859-1" standalone="no"?&gt;&lt; xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:Retention-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:sac="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Retention. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;The document used to request payment.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Retention&lt;/ccts:ObjectClass&gt;
 *         &lt;/ccts:Component&gt;
 * </pre>
 * 
 *       
 * 
 * <p>Java class for InvoiceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RetentionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>

 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATRetentionAmount" minOccurs="0"/>
 *		   &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATRetentionDate" minOccurs="0"/>   
 *		   &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATNetTotalPaid" minOccurs="0"/>
 *		   &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}ExchangeRate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SUNATRetentionInformationType", propOrder = {
    "sunatRetentionAmount",
    "sunatRetentionDate",
    "sunatNetTotalCashed",
    "exchangeRate"
})
@XmlRootElement(name = "SUNATRetentionInformation")

public class SUNATRetentionInformationType {
	
	@XmlElement(name = "SUNATRetentionAmount", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATRetentionAmountType sunatRetentionAmount;

	@XmlElement(name = "SUNATRetentionDate", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATRetentionDateType sunatRetentionDate;
	
	@XmlElement(name = "SUNATNetTotalPaid", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATNetTotalPaidType sunatNetTotalCashed;
	
	@XmlElement(name = "ExchangeRate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2",required = true)
	protected ExchangeRateType exchangeRate;

	public SUNATRetentionAmountType getSunatRetentionAmount() {
		return sunatRetentionAmount;
	}

	public void setSunatRetentionAmount(
			SUNATRetentionAmountType sunatRetentionAmount) {
		this.sunatRetentionAmount = sunatRetentionAmount;
	}

	public SUNATRetentionDateType getSunatRetentionDate() {
		return sunatRetentionDate;
	}

	public void setSunatRetentionDate(SUNATRetentionDateType sunatRetentionDate) {
		this.sunatRetentionDate = sunatRetentionDate;
	}

	
	public SUNATNetTotalPaidType getSunatNetTotalCashed() {
		return sunatNetTotalCashed;
	}

	public void setSunatNetTotalCashed(SUNATNetTotalPaidType sunatNetTotalCashed) {
		this.sunatNetTotalCashed = sunatNetTotalCashed;
	}
	
	public ExchangeRateType getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRateType exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

}
