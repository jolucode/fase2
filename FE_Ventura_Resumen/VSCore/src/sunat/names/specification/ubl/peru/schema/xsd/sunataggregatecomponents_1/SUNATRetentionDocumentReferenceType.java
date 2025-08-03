package sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;



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
 * <p>Java class for AdditionalInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdditionalInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}ID" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}IssueDate" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}TotalInvoiceAmount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}Payment" minOccurs="0"/>
 *         &lt;element ref="{urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1}SUNATRetentionInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SUNATRetentionDocumentReferenceType", propOrder = {
    "id",
    "issueDate",
    "totalInvoiceAmount",
    "payment",
    "sunatRetentionInformation"
})
@XmlRootElement(name="SUNATRetentionDocumentReference")
public class SUNATRetentionDocumentReferenceType {
	
	@XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected IDType id;
	@XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
	protected IssueDateType issueDate;
	@XmlElement(name = "TotalInvoiceAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
	protected oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType totalInvoiceAmount;
	@XmlElement(name = "Payment", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2", required = true)
	protected PaymentType payment;
	@XmlElement(name = "SUNATRetentionInformation", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
	protected SUNATRetentionInformationType sunatRetentionInformation;
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
	public oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}
	public void setTotalInvoiceAmount(
			oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType totalInvoiceAmount) {
		this.totalInvoiceAmount = totalInvoiceAmount;
	}
	public PaymentType getPayment() {
		return payment;
	}
	public void setPayment(PaymentType payment) {
		this.payment = payment;
	}
	public SUNATRetentionInformationType getSunatRetentionInformation() {
		return sunatRetentionInformation;
	}
	public void setSunatRetentionInformation(
			SUNATRetentionInformationType sunatRetentionInformation) {
		this.sunatRetentionInformation = sunatRetentionInformation;
	}
}
