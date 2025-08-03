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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Perception-2" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Invoice. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;The document used to request payment.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Invoice&lt;/ccts:ObjectClass&gt;
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
 * &lt;complexType name="PerceptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>

 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATPerceptionAmount" minOccurs="0"/>
 *		   &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATPerceptionDate" minOccurs="0"/>   
 *		   &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATNetTotalCashed" minOccurs="0"/>
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
@XmlType(name = "SUNATPerceptionInformationType", propOrder = {
    "perceptionAmount",
    "perceptionDate",
    "sunatNetTotalCashed",
    "exchangeRate"
})
@XmlRootElement(name = "SUNATPerceptionInformation")

public class SUNATPerceptionInformationType {
	
	@XmlElement(name = "SUNATPerceptionAmount", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATPerceptionAmountType perceptionAmount;

	@XmlElement(name = "SUNATPerceptionDate", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATPerceptionDateType perceptionDate;
	
	@XmlElement(name = "SUNATNetTotalCashed", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1",required = true)
	protected SUNATNetTotalCashedType sunatNetTotalCashed;
	
	@XmlElement(name = "ExchangeRate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2",required = true)
	protected ExchangeRateType exchangeRate;
	
	public SUNATPerceptionAmountType getPerceptionAmount() {
		return perceptionAmount;
	}

	public void setPerceptionAmount(SUNATPerceptionAmountType perceptionAmount) {
		this.perceptionAmount = perceptionAmount;
	}

	public SUNATPerceptionDateType getPerceptionDate() {
		return perceptionDate;
	}

	public void setPerceptionDate(SUNATPerceptionDateType perceptionDate) {
		this.perceptionDate = perceptionDate;
	}

	public SUNATNetTotalCashedType getSunatNetTotalCashed() {
		return sunatNetTotalCashed;
	}

	public void setSunatNetTotalCashed(SUNATNetTotalCashedType sunatNetTotalCashed) {
		this.sunatNetTotalCashed = sunatNetTotalCashed;
	}

	public ExchangeRateType getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRateType exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

}
