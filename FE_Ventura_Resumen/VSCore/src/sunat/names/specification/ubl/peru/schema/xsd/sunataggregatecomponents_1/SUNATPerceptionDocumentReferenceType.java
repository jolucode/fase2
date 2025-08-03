package sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType;

/**
 * 
 * 				
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Information about a Consolidated Invoice Line.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Consolidated  Invoice Line&lt;/ccts:ObjectClass&gt;
 * 				&lt;/ccts:Component&gt;
 * </pre>
 * 
 * 			
 * 
 * <p>Java class for AdditionalInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdditionalInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}ID" minOccurs="0"/>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}IssueDate" minOccurs="0"/>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}TotalInvoiceAmount" minOccurs="0"/>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}Payment" minOccurs="0"/>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATPerceptionInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SUNATPerceptionDocumentReferenceType", propOrder = {
    "id",
    "issueDate",
    "totalInvoiceAmount",
    "payment",
    "sunatPerceptionInformation"
})
@XmlRootElement(name="SUNATPerceptionDocumentReference")
public class SUNATPerceptionDocumentReferenceType implements Serializable {
	@XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected IDType id;
	@XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
	protected IssueDateType issueDate;
	@XmlElement(name = "TotalInvoiceAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
	protected oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType totalInvoiceAmount;
	@XmlElement(name = "Payment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", required = true)
	protected PaymentType payment;
	@XmlElement(name = "SUNATPerceptionInformation", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
	protected SUNATPerceptionInformationType sunatPerceptionInformation;
	
	public IDType getId() {
		return id;
	}
	public void setId(IDType id) {
		this.id = id;
	}
	public IssueDateType getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(IssueDateType issueDate) {
		this.issueDate = issueDate;
	}
	public TotalInvoiceAmountType getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}
	public void setTotalInvoiceAmount(oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType totalInvoiceAmountType) {
		this.totalInvoiceAmount = totalInvoiceAmountType;
	}
	public PaymentType getPayment() {
		return payment;
	}
	public void setPayment(PaymentType payment) {
		this.payment = payment;
	}
	public SUNATPerceptionInformationType getSunatPerceptionInformation() {
		return sunatPerceptionInformation;
	}
	public void setSunatPerceptionInformation(
			SUNATPerceptionInformationType sunatPerceptionInformation) {
		this.sunatPerceptionInformation = sunatPerceptionInformation;
	}
	
	
}
