package ventura.soluciones.commons.handler;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.LocationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.dto.hb.*;
import org.w3c.dom.Document;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.AdditionalInformationType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.*;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.IdentifierType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.TextType;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.UBLDocumentException;
import ventura.soluciones.commons.exception.error.IVenturaError;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Esta clase HANDLER contiene metodos para generar objetos UBL, necesarios para
 * armar el documento UBL validado por Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class UBLDocumentHandler {

    private final Logger logger = Logger.getLogger(UBLDocumentHandler.class);

    /* Campos Adicionales */
    /*
     * 
     * */
    /* Identificador del objeto */
    private String identifier;

    /**
     * Constructor privado para evitar la creacion de instancias usando el
     * constructor.
     *
     * @param identifier Identificador del objeto UBLDocumentHandler creado.
     */
    private UBLDocumentHandler(String identifier) {
        this.identifier = identifier;
    } // UBLDocumentHandler

    /**
     * Este metodo crea una nueva instancia de la clase UBLDocumentHandler.
     *
     * @param identifier Identificador del objeto UBLDocumentHandler creado.
     * @return Retorna una nueva instancia de la clase UBLDocumentHandler.
     */
    public static synchronized UBLDocumentHandler newInstance(String identifier) {
        return new UBLDocumentHandler(identifier);
    } // newInstance

    /**
     * Este metodo genera el objeto SignatureType, que es utilizado para armar
     * el documento UBL.
     *
     * @param identifier El identificador (numero RUC) del contribuyente.
     * @param socialReason La razon social del contribuyente.
     * @param signerName El nombre del firmante (puede ser un valor
     * identificador).
     * @return Retorna el objeto SignatureType con los datos del contribuyente.
     * @throws UBLDocumentException
     */
    public SignatureType generateSignature(String identifier,
            String socialReason, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateSignature() [" + this.identifier + "]");
        }
        SignatureType signature = null;

        try {
            /* <cac:Signature><cbc:ID> */
            IDType idSigner = new IDType();
            idSigner.setValue(signerName);

            /* <cac:Signature><cac:SignatoryParty> */
            PartyType signatoryParty = new PartyType();
            {
                PartyIdentificationType partyIdentification = new PartyIdentificationType();
                IDType idRUC = new IDType();
                idRUC.setValue(identifier);
                partyIdentification.setID(idRUC);
                signatoryParty.getPartyIdentification()
                        .add(partyIdentification);

                PartyNameType partyName = new PartyNameType();
                NameType name = new NameType();
                name.setValue(socialReason);
                partyName.setName(name);
                signatoryParty.getPartyName().add(partyName);
            }

            /* <cac:Signature><cac:DigitalSignatureAttachment> */
            AttachmentType digitalSignatureAttachment = new AttachmentType();
            {
                ExternalReferenceType externalReference = new ExternalReferenceType();
                URIType uri = new URIType();
                uri.setValue("#" + signerName);
                externalReference.setURI(uri);

                digitalSignatureAttachment
                        .setExternalReference(externalReference);
            }

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            signature = new SignatureType();
            signature.setID(idSigner);
            signature.setSignatoryParty(signatoryParty);
            signature.setDigitalSignatureAttachment(digitalSignatureAttachment);
        } catch (Exception e) {
            throw new UBLDocumentException(IVenturaError.ERROR_301);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateSignature() [" + this.identifier
                    + "] signature: " + signature);
        }
        return signature;
    } // generateSignature

    /**
     * Este metodo genera el objeto SupplierPartyType que es utilizado para
     * contener los datos del emisor electronico y armar el documento UBL.
     *
     * @param identifier El identificador (numero de RUC) del contribuyente
     * emisor.
     * @param identifierType El tipo de identificador del contribuyente emisor.
     * @param socialReason La razon social del contribuyente emisor.
     * @param commercialName El nombre comercial del contribuyente emisor.
     * @param fiscalAddress La direccion fiscal del contribuyente emisor.
     * @param department El nombre del departamento del contribuyente emisor.
     * @param province El nombre de la provincia del contribuyente emisor.
     * @param district El nombre del distrito del contribuyente emisor.
     * @param ubigeo El codigo de ubigeo del contribuyente emisor.
     * @param countryCode El codigo de pais del contribuyente emisor.
     * @param contactName El nombre del contacto emisor.
     * @param electronicMail El correo electronico del emisor.
     * @return Retorna el objeto SupplierPartyType con los datos del
     * contribuyente emisor.
     * @throws UBLDocumentException
     */
    public SupplierPartyType generateAccountingSupplierParty(String identifier,
            String identifierType, String socialReason, String commercialName,
            String fiscalAddress, String department, String province,
            String district, String ubigeo, String countryCode,
            String contactName, String electronicMail)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateAccountingSupplierParty() ["
                    + this.identifier + "]");
        }
        SupplierPartyType accountingSupplierParty = null;

        try {

            if (logger.isDebugEnabled()) {
                logger.debug("+CustomerAssignedAccountIDType() ["
                        + this.identifier + "]");
            }

            /* <cac:AccountingSupplierParty><cbc:CustomerAssignedAccountID> */
            CustomerAssignedAccountIDType customerAssignedAccountID = new CustomerAssignedAccountIDType();
            customerAssignedAccountID.setValue(identifier);

            if (logger.isDebugEnabled()) {
                logger.debug("+AdditionalAccountIDType() ["
                        + this.identifier + "]");
            }

            /* <cac:AccountingSupplierParty><cbc:AdditionalAccountID> */
            AdditionalAccountIDType additionalAccountID = new AdditionalAccountIDType();
            additionalAccountID.setValue(identifierType);

            if (logger.isDebugEnabled()) {
                logger.debug("+PartyType() ["
                        + this.identifier + "]");
            }

            /* <cac:AccountingSupplierParty><cac:Party> */
            PartyType party = generateParty(socialReason, commercialName,
                    fiscalAddress, department, province, district, ubigeo,
                    countryCode, contactName, electronicMail);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            accountingSupplierParty = new SupplierPartyType();
            accountingSupplierParty
                    .setCustomerAssignedAccountID(customerAssignedAccountID);
            accountingSupplierParty.getAdditionalAccountID().add(
                    additionalAccountID);
            accountingSupplierParty.setParty(party);
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("-generateAccountingSupplierParty() ["
                        + this.identifier + "] accountingSupplierParty: "
                        + e.getMessage());
            }

            throw new UBLDocumentException(IVenturaError.ERROR_302);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateAccountingSupplierParty() ["
                    + this.identifier + "] accountingSupplierParty: "
                    + accountingSupplierParty);
        }
        return accountingSupplierParty;
    } // generateAccountingSupplierParty

    public SupplierPartyType generateAccountingSupplierPartyV2(
            String identifier, String identifierType, String socialReason,
            String commercialName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateAccountingSupplierParty() ["
                    + this.identifier + "]");
        }
        SupplierPartyType accountingSupplierParty = null;

        try {
            /* <cac:AccountingSupplierParty><cbc:CustomerAssignedAccountID> */
            CustomerAssignedAccountIDType customerAssignedAccountID = new CustomerAssignedAccountIDType();
            customerAssignedAccountID.setValue(identifier);

            /* <cac:AccountingSupplierParty><cbc:AdditionalAccountID> */
            AdditionalAccountIDType additionalAccountID = new AdditionalAccountIDType();
            additionalAccountID.setValue(identifierType);

            /* <cac:AccountingSupplierParty><cac:Party> */
            PartyType party = generatePartyV2(socialReason, commercialName);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            accountingSupplierParty = new SupplierPartyType();
            accountingSupplierParty
                    .setCustomerAssignedAccountID(customerAssignedAccountID);
            accountingSupplierParty.getAdditionalAccountID().add(
                    additionalAccountID);
            accountingSupplierParty.setParty(party);
        } catch (Exception e) {
            throw new UBLDocumentException(IVenturaError.ERROR_302);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateAccountingSupplierParty() ["
                    + this.identifier + "] accountingSupplierParty: "
                    + accountingSupplierParty);
        }
        return accountingSupplierParty;
    } // generateAccountingSupplierParty

    public List<SUNATRetentionDocumentReferenceType> generateDocumentReferenceV2(
            List<TransaccionComprobantePago> tscp) throws UBLDocumentException {

        List<SUNATRetentionDocumentReferenceType> lst = new ArrayList<SUNATRetentionDocumentReferenceType>();

        for (int i = 0; i < tscp.size(); i++) {
            SUNATRetentionDocumentReferenceType documentReferenceType = new SUNATRetentionDocumentReferenceType();

            IDType objIdType = new IDType();
            /**
             * ****************** <cbc:ID schemeID=></cbc:ID>
             * ****************************
             */
            objIdType.setSchemeID(tscp.get(i).getDOCTipo());
            objIdType.setValue(tscp.get(i).getDOCNumero());

            documentReferenceType.setId(objIdType);

            /**
             * ********************** <cbc:IssueDate></cbc:IssueDate>
             * ***************************************************
             */
            documentReferenceType.setIssueDate(getIssueDate(tscp.get(i)
                    .getDOCFechaEmision()));

            /**
             * ****
             * <cbc:TotalInvoiceAmount currencyID></cbc:TotalInvoiceAmount> *
             */
            TotalInvoiceAmountType totalInvoiceAmountType = new TotalInvoiceAmountType();
            totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(i).getDOCMoneda()));
            totalInvoiceAmountType.setValue(tscp.get(i).getDOCImporte().setScale(2, BigDecimal.ROUND_HALF_UP));

            documentReferenceType.setTotalInvoiceAmount(totalInvoiceAmountType);
            /**
             * ****************** <cac:Payment></cac:Payment>
             * **************************
             */

            SUNATRetentionInformationType sunatRetentionInformationType = new SUNATRetentionInformationType();

            PaymentType paymentType = new PaymentType();
            paymentType.setPaidDate(getIssueDate2(tscp.get(i).getPagoFecha()));

            IDType numero = new IDType();
            numero.setValue(String.valueOf(tscp.get(i).getPagoNumero()));
            paymentType.setID(numero);
            PaidAmountType objPaidAmountType = new PaidAmountType();
            objPaidAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(i).getPagoMoneda()));
            objPaidAmountType.setValue(tscp.get(i).getPagoImporteSR().setScale(2, BigDecimal.ROUND_HALF_UP));
            paymentType.setPaidAmount(objPaidAmountType);

            documentReferenceType.setPayment(paymentType);

            SUNATRetentionAmountType objPerceptionAmountType = new SUNATRetentionAmountType();
            objPerceptionAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(i).getCPMoneda()));
            objPerceptionAmountType.setValue(tscp.get(i).getCPImporte().setScale(2, BigDecimal.ROUND_HALF_UP));

            sunatRetentionInformationType
                    .setSunatRetentionAmount(objPerceptionAmountType);
            /**
             * ******************************************************************************************
             */
            SUNATNetTotalPaidType objCashedType = new SUNATNetTotalPaidType();
            objCashedType.setCurrencyID(CurrencyCodeContentType.valueOf(tscp
                    .get(0).getCPMonedaMontoNeto()));
            objCashedType.setValue(tscp.get(i).getCPImporteTotal().setScale(2, BigDecimal.ROUND_HALF_UP));

            sunatRetentionInformationType.setSunatNetTotalCashed(objCashedType);
            /**
             * ********************************************************************************************
             */

            sunatRetentionInformationType
                    .setSunatRetentionDate(getIssueDate5(tscp.get(i)
                                    .getCPFecha()));

            /**
             * **********************************************************************
             */
            /**
             * **********************************************************************************************
             */
            ExchangeRateType exchangeRateType = new ExchangeRateType();

            SourceCurrencyCodeType currencyCodeType = new SourceCurrencyCodeType();
            currencyCodeType.setValue(tscp.get(i).getTCMonedaRef());

            exchangeRateType.setSourceCurrencyCode(currencyCodeType);

            TargetCurrencyCodeType targetCurrencyCodeType = new TargetCurrencyCodeType();
            targetCurrencyCodeType.setValue(tscp.get(i).getTCMonedaObj());

            exchangeRateType.setTargetCurrencyCode(targetCurrencyCodeType);

            CalculationRateType calculationRateType = new CalculationRateType();
            calculationRateType.setValue(tscp.get(i).getTCFactor().setScale(3));

            exchangeRateType.setCalculationRate(calculationRateType);

            exchangeRateType.setDate(getIssueDate4(tscp.get(i).getTCFecha()));

            sunatRetentionInformationType.setExchangeRate(exchangeRateType);
            /**
             * ***********************************************************************************************
             */

            documentReferenceType
                    .setSunatRetentionInformation(sunatRetentionInformationType);
            lst.add(documentReferenceType);

        }

        return lst;

    }

    public List<SUNATPerceptionDocumentReferenceType> generateDocumentReference(
            List<TransaccionComprobantePago> tscp) throws UBLDocumentException {

        List<SUNATPerceptionDocumentReferenceType> lst = new ArrayList<SUNATPerceptionDocumentReferenceType>();

        SUNATPerceptionDocumentReferenceType documentReferenceType = new SUNATPerceptionDocumentReferenceType();

        for (int i = 0; i < tscp.size(); i++) {

            IDType objIdType = new IDType();
            if (logger.isInfoEnabled()) {
                logger.info("SUNATPerceptionDocumentReference() ");
            }

            if (logger.isInfoEnabled()) {
                logger.info("SUNATPerceptionDocumentReference() ID - "
                        + tscp.get(i).getDOCTipo() + " - "
                        + tscp.get(i).getDOCNumero());
            }
            /**
             * ****************** <cbc:ID schemeID=></cbc:ID>
             * ****************************
             */
            objIdType.setSchemeID(tscp.get(i).getDOCTipo());
            objIdType.setValue(tscp.get(i).getDOCNumero());

            documentReferenceType.setId(objIdType);

            /**
             * ********************** <cbc:IssueDate></cbc:IssueDate>
             * ***************************************************
             */
            if (logger.isInfoEnabled()) {
                logger.info("SUNATPerceptionDocumentReference()  - ISSUEDATE "
                        + tscp.get(i).getDOCFechaEmision());
            }
            documentReferenceType.setIssueDate(getIssueDate(tscp.get(i)
                    .getDOCFechaEmision()));

            /**
             * ****
             * <cbc:TotalInvoiceAmount currencyID></cbc:TotalInvoiceAmount> *
             */
            if (logger.isInfoEnabled()) {
                logger.info("SUNATPerceptionDocumentReference()  - TOTALAMOUNT "
                        + tscp.get(i).getDOCImporte());
            }
            TotalInvoiceAmountType totalInvoiceAmountType = new TotalInvoiceAmountType();
            totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(i).getDOCMoneda()));
            totalInvoiceAmountType.setValue(tscp.get(i).getDOCImporte().setScale(2));

            documentReferenceType.setTotalInvoiceAmount(totalInvoiceAmountType);
            /**
             * ****************** <cac:Payment></cac:Payment>
             * **************************
             */
            if (logger.isInfoEnabled()) {
                logger.info("Payment()  - PaidDate "
                        + tscp.get(i).getPagoFecha());
            }
            PaymentType paymentType = new PaymentType();
            paymentType.setPaidDate(getIssueDate2(tscp.get(i).getPagoFecha()));

            if (logger.isInfoEnabled()) {
                logger.info("Payment()  - ID " + tscp.get(i).getPagoNumero());
            }
            IDType numero = new IDType();
            numero.setValue(String.valueOf(tscp.get(i).getPagoNumero()));
            paymentType.setID(numero);

            if (logger.isInfoEnabled()) {
                logger.info("Payment()  - PaidAmountT "
                        + tscp.get(i).getPagoMoneda());
            }
            PaidAmountType objPaidAmountType = new PaidAmountType();
            objPaidAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(i).getPagoMoneda()));
            objPaidAmountType.setValue(tscp.get(i).getPagoImporteSR().setScale(2));
            paymentType.setPaidAmount(objPaidAmountType);

            documentReferenceType.setPayment(paymentType);
            /**
             * **********************************************************************
             */

            SUNATPerceptionInformationType objPerceptionInformationType = new SUNATPerceptionInformationType();

            /**
             **
             * **********************
             * <sac:SUNATPerceptionInformation></sac:SUNATPerceptionInformation>
             * ************************************************
             */
            /**
             **
             * ****************************
             * <sac:SUNATPerceptionAmount
             * currencyID></sac:SUNATPerceptionAmount>
             * *****************************************
             */
            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - SUNATPerceptionAmount "
                        + tscp.get(0).getCPImporte());
            }
            SUNATPerceptionAmountType objPerceptionAmountType = new SUNATPerceptionAmountType();
            objPerceptionAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(tscp.get(0).getCPMoneda()));
            objPerceptionAmountType.setValue(tscp.get(0).getCPImporte().setScale(2));

            objPerceptionInformationType
                    .setPerceptionAmount(objPerceptionAmountType);

            /**
             * *****************************************
             * <sac:SUNATNetTotalCashed currencyID></sac:SUNATNetTotalCashed>
             * ***************************************************************
             */
            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - SUNATNetTotalCashed "
                        + tscp.get(i).getCPMonedaMontoNeto());
            }
            SUNATNetTotalCashedType objCashedType = new SUNATNetTotalCashedType();
            objCashedType.setCurrencyID(CurrencyCodeContentType.valueOf(tscp
                    .get(i).getCPMonedaMontoNeto()));
            objCashedType.setValue(tscp.get(i).getCPImporteTotal().setScale(2));

            objPerceptionInformationType.setSunatNetTotalCashed(objCashedType);

            /**
             * *****************************************
             * <sac:SUNATPerceptionDate></sac:SUNATPerceptionDate>
             * ***************************************************************
             */
            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - SUNATNetTotalCashed "
                        + tscp.get(i).getCPFecha());
            }
            objPerceptionInformationType.setPerceptionDate(getIssueDate3(tscp
                    .get(i).getCPFecha()));

            /**
             * ************************************
             * <cac:ExchangeRate></cac:ExchangeRate>
             * ****************************************************************
             */
            ExchangeRateType exchangeRateType = new ExchangeRateType();

            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - SourceCurrencyCode "
                        + tscp.get(i).getTCMonedaRef());
            }

            SourceCurrencyCodeType currencyCodeType = new SourceCurrencyCodeType();
            currencyCodeType.setValue(tscp.get(i).getTCMonedaRef());
            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - targetCurrencyCode "
                        + tscp.get(i).getTCMonedaObj());
            }

            TargetCurrencyCodeType targetCurrencyCodeType = new TargetCurrencyCodeType();
            targetCurrencyCodeType.setValue(tscp.get(i).getTCMonedaObj());

            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - CalculationRate "
                        + tscp.get(i).getTCFactor());
            }

            CalculationRateType calculationRateType = new CalculationRateType();
            calculationRateType.setValue(tscp.get(i).getTCFactor());

            exchangeRateType.setSourceCurrencyCode(currencyCodeType);
            exchangeRateType.setTargetCurrencyCode(targetCurrencyCodeType);
            exchangeRateType.setCalculationRate(calculationRateType);
            if (logger.isInfoEnabled()) {
                logger.info("sac:SUNATPerceptionInformation()  - CPFecha"
                        + tscp.get(i).getTCFecha());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = sdf.format(tscp.get(i).getTCFecha());
            XMLGregorianCalendar xmlCal = null;
            try {
                xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        date);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }

            DateType dateType = new DateType();

            dateType.setValue(xmlCal);

            exchangeRateType.setDate(dateType);

            objPerceptionInformationType.setExchangeRate(exchangeRateType);

            documentReferenceType
                    .setSunatPerceptionInformation(objPerceptionInformationType);
            lst.add(documentReferenceType);

        }
        return lst;

    }

    /**
     * Este metodo genera el objeto CustomerPartyType que es utilizado para
     * contener los datos del emisor electronico y armar el documento UBL.
     *
     * @param identifier El identificador del contribuyente receptor.
     * @param identifierType El tipo de identificador del contribuyente
     * receptor.
     * @param socialReason La razon social del contribuyente receptor.
     * @param commercialName El nombre comercial del contribuyente receptor.
     * @param fiscalAddress La direccion fiscal del contribuyente receptor.
     * @param department El nombre del departamento del contribuyente receptor.
     * @param province El nombre de la provincia del contribuyente receptor.
     * @param district El nombre del distrito del contribuyente receptor.
     * @param ubigeo El codigo de ubigeo del contribuyente receptor.
     * @param countryCode El codigo de pais del contribuyente receptor.
     * @param contactName El nombre del contacto del contribuyente receptor.
     * @param electronicMail El correo electronico del contribuyente receptor.
     * @return Retorna el objeto CustomerPartyType con los datos del
     * contribuyente receptor.
     * @throws UBLDocumentException
     */
    public CustomerPartyType generateAccountingCustomerParty(String identifier,
            String identifierType, String socialReason, String commercialName,
            String fiscalAddress, String department, String province,
            String district, String ubigeo, String countryCode,
            String contactName, String electronicMail)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateAccountingCustomerParty() ["
                    + this.identifier + "]");
        }
        CustomerPartyType accountingCustomerParty = null;

        try {
            /* <cac:AccountingCustomerParty><cbc:CustomerAssignedAccountID> */
            CustomerAssignedAccountIDType customerAssignedAccountID = new CustomerAssignedAccountIDType();
            customerAssignedAccountID.setValue(identifier);

            /* <cac:AccountingCustomerParty><cbc:AdditionalAccountID> */
            AdditionalAccountIDType additionalAccountID = new AdditionalAccountIDType();
            additionalAccountID.setValue(identifierType);

            /* <cac:AccountingCustomerParty><cac:Party> */
            PartyType party = generateParty(socialReason, commercialName,
                    fiscalAddress, department, province, district, ubigeo,
                    countryCode, contactName, electronicMail);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            accountingCustomerParty = new CustomerPartyType();
            accountingCustomerParty
                    .setCustomerAssignedAccountID(customerAssignedAccountID);
            accountingCustomerParty.getAdditionalAccountID().add(
                    additionalAccountID);
            accountingCustomerParty.setParty(party);
        } catch (Exception e) {
            logger.error("generateAccountingCustomerParty() ["
                    + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_303.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_303);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateAccountingCustomerParty() ["
                    + this.identifier + "] accountingCustomerParty: "
                    + accountingCustomerParty);
        }
        return accountingCustomerParty;
    } // generateAccountingCustomerParty

    private CustomerPartyType generateAccountingCustomerPartyV2(
            String identifier, String identifierType, String fullname,
            String fiscalAddress, String department, String province,
            String district, String contactName, String electronicMail)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateAccountingCustomerPartyV2() ["
                    + this.identifier + "]");
        }
        CustomerPartyType accountingCustomerParty = null;

        try {
            /* <cac:AccountingCustomerParty><cbc:CustomerAssignedAccountID> */
            CustomerAssignedAccountIDType customerAssignedAccountID = new CustomerAssignedAccountIDType();
            customerAssignedAccountID.setValue(identifier);

            /* <cac:AccountingCustomerParty><cbc:AdditionalAccountID> */
            AdditionalAccountIDType additionalAccountID = new AdditionalAccountIDType();
            additionalAccountID.setValue(identifierType);

            /* <cac:AccountingCustomerParty><cac:Party> */
            PartyType party = generatePartyByBoleta(fullname, fiscalAddress,
                    department, province, district, contactName, electronicMail);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            accountingCustomerParty = new CustomerPartyType();
            accountingCustomerParty
                    .setCustomerAssignedAccountID(customerAssignedAccountID);
            accountingCustomerParty.getAdditionalAccountID().add(
                    additionalAccountID);
            accountingCustomerParty.setParty(party);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("generateAccountingCustomerPartyV2() ["
                    + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_303.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_303);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateAccountingCustomerPartyV2() ["
                    + this.identifier + "] accountingCustomerParty: "
                    + accountingCustomerParty);
        }
        return accountingCustomerParty;
    } // generateAccountingSupplierPartyV2

    /**
     * Este metodo genera el objeto PartyType que es utilizado para contener los
     * datos del emisor y receptor electronico y armar el documento UBL.
     *
     * @param socialReasonValue La razon social del contribuyente (emisor o
     * receptor).
     * @param commercialNameValue El nombre comercial del contribuyente (emisor
     * o receptor).
     * @param fiscalAddressValue La direccion fiscal del contribuyente (emisor o
     * receptor).
     * @param departmentValue El nombre del departamento del contribuyente
     * (emisor o receptor).
     * @param provinceValue El nombre de la provincia del contribuyente (emisor
     * o receptor).
     * @param districtValue El nombre del distrito del contribuyente (emisor o
     * receptor).
     * @param ubigeoValue El codigo de ubigeo del contribuyente (emisor o
     * receptor).
     * @param countryCodeValue El codigo de pais del contribuyente (emisor o
     * receptor).
     * @param contactNameValue El nombre del contacto.
     * @param electronicMailValue El correo electronico del contacto.
     * @return Retorna el objeto PartyType con los datos del contribuyente
     * emisor o receptor electronico.
     * @throws Exception
     */
    private PartyType generateParty(String socialReasonValue,
            String commercialNameValue, String fiscalAddressValue,
            String departmentValue, String provinceValue, String districtValue,
            String ubigeoValue, String countryCodeValue,
            String contactNameValue, String electronicMailValue)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateParty() [" + this.identifier + "]");
        }
        PartyType party = new PartyType();

        /* <cac:Party><cac:PartyName> */
        if (StringUtils.isNotBlank(commercialNameValue)) {
            PartyNameType partyName = new PartyNameType();
            NameType name = new NameType();
            name.setValue(commercialNameValue);
            partyName.setName(name);

            party.getPartyName().add(partyName);
        }

        /* <cac:Party><cac:PostalAddress> */
        if (StringUtils.isNotBlank(fiscalAddressValue)) {
            AddressType postalAddress = null;

            /* <cac:Party><cac:PostalAddress><cbc:ID> */
            IDType id = new IDType();
            id.setValue(ubigeoValue);

            /* <cac:Party><cac:PostalAddress><cbc:StreetName> */
            StreetNameType streetName = new StreetNameType();
            streetName.setValue(fiscalAddressValue);

            /* <cac:Party><cac:PostalAddress><cbc:CityName> */
            CityNameType cityName = new CityNameType();
            cityName.setValue(provinceValue);

            /* <cac:Party><cac:PostalAddress><cbc:CountrySubentity> */
            CountrySubentityType countrySubentity = new CountrySubentityType();
            countrySubentity.setValue(departmentValue);

            /* <cac:Party><cac:PostalAddress><cbc:District> */
            DistrictType district = new DistrictType();
            district.setValue(districtValue);

            /* <cac:Party><cac:PostalAddress><cac:Country> */
            CountryType country = new CountryType();
            IdentificationCodeType identificationCode = new IdentificationCodeType();
            identificationCode.setValue(countryCodeValue);
            country.setIdentificationCode(identificationCode);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            postalAddress = new AddressType();
            postalAddress.setID(id);
            postalAddress.setStreetName(streetName);
            postalAddress.setCityName(cityName);
            postalAddress.setCountrySubentity(countrySubentity);
            postalAddress.setDistrict(district);
            postalAddress.setCountry(country);

            party.setPostalAddress(postalAddress);
        }

        /* <cac:Party><cac:PartyLegalEntity> */
        PartyLegalEntityType partyLegalEntity = null;
        {
            partyLegalEntity = new PartyLegalEntityType();

            RegistrationNameType registrationName = new RegistrationNameType();
            registrationName.setValue(socialReasonValue);
            partyLegalEntity.setRegistrationName(registrationName);

            List<PartyLegalEntityType> listaEntityTypes = new ArrayList<>();
            listaEntityTypes.add(partyLegalEntity);

            party.setPartyLegalEntity(listaEntityTypes);
        }

        /* <cac:Party><cac:Contact> */
        if (StringUtils.isNotBlank(electronicMailValue)) {
            ContactType contact = new ContactType();

            ElectronicMailType electronicMail = new ElectronicMailType();
            electronicMail.setValue(electronicMailValue);
            contact.setElectronicMail(electronicMail);

            if (StringUtils.isNotBlank(contactNameValue)) {
                NameType name = new NameType();
                name.setValue(contactNameValue);
                contact.setName(name);
            }

            party.setContact(contact);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-generateParty() [" + this.identifier + "]");
        }
        return party;
    } // generateParty

    private PartyType generatePartyV2(String socialReasonValue,
            String commercialNameValue) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateParty() [" + this.identifier + "]");
        }
        PartyType party = new PartyType();

        /* <cac:Party><cac:PartyName> */
        if (StringUtils.isNotBlank(commercialNameValue)) {
            PartyNameType partyName = new PartyNameType();
            NameType name = new NameType();
            name.setValue(commercialNameValue);
            partyName.setName(name);

            party.getPartyName().add(partyName);
        }

        /* <cac:Party><cac:PartyLegalEntity> */
        PartyLegalEntityType partyLegalEntity = null;
        {
            partyLegalEntity = new PartyLegalEntityType();

            RegistrationNameType registrationName = new RegistrationNameType();
            registrationName.setValue(socialReasonValue);
            partyLegalEntity.setRegistrationName(registrationName);

            List<PartyLegalEntityType> listaEntityTypes = new ArrayList<>();
            listaEntityTypes.add(partyLegalEntity);

            party.setPartyLegalEntity(listaEntityTypes);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-generateParty() [" + this.identifier + "]");
        }
        return party;
    } // generateParty

    private PartyType generatePartyPerception(String identifier,
            String socialReasonValue, String commercialNameValue,
            String fiscalAddressValue, String departmentValue,
            String provinceValue, String districtValue, String ubigeoValue,
            String countryCodeValue, String contactNameValue,
            String electronicMailValue, String tipoDocId) throws Exception {

        contactNameValue = retornarLleno(contactNameValue);
        electronicMailValue = retornarLleno(electronicMailValue);

        if (logger.isDebugEnabled()) {
            logger.debug("+generateParty() [" + this.identifier + "]");
        }
        PartyType party = new PartyType();

        PartyIdentificationType partyIdentification = new PartyIdentificationType();
        IDType idRUC = new IDType();
        idRUC.setValue(identifier);
        idRUC.setSchemeID(tipoDocId);
        partyIdentification.setID(idRUC);
        party.getPartyIdentification().add(partyIdentification);

        /* <cac:Party><cac:PartyName> */
        if (StringUtils.isNotBlank(commercialNameValue)) {
            PartyNameType partyName = new PartyNameType();
            NameType name = new NameType();
            name.setValue(commercialNameValue);
            partyName.setName(name);

            party.getPartyName().add(partyName);
        }

        /* <cac:Party><cac:PostalAddress> */
        if (StringUtils.isNotBlank(fiscalAddressValue)) {
            AddressType postalAddress = null;

            /* <cac:Party><cac:PostalAddress><cbc:ID> */
            IDType id = new IDType();
            id.setValue(ubigeoValue);

            /* <cac:Party><cac:PostalAddress><cbc:StreetName> */
            StreetNameType streetName = new StreetNameType();
            streetName.setValue(fiscalAddressValue);

            /* <cac:Party><cac:PostalAddress><cbc:CityName> */
            CityNameType cityName = new CityNameType();
            cityName.setValue(provinceValue);

            /* <cac:Party><cac:PostalAddress><cbc:CountrySubentity> */
            CountrySubentityType countrySubentity = new CountrySubentityType();
            countrySubentity.setValue(departmentValue);

            /* <cac:Party><cac:PostalAddress><cbc:District> */
            DistrictType district = new DistrictType();
            district.setValue(districtValue);

            /* <cac:Party><cac:PostalAddress><cac:Country> */
            CountryType country = new CountryType();
            IdentificationCodeType identificationCode = new IdentificationCodeType();
            identificationCode.setValue(countryCodeValue);
            country.setIdentificationCode(identificationCode);

            /*
             * Armar el objeto con sus respectivos TAG's
             */
            postalAddress = new AddressType();
            postalAddress.setID(id);
            postalAddress.setStreetName(streetName);
            postalAddress.setCityName(cityName);
            postalAddress.setCountrySubentity(countrySubentity);
            postalAddress.setDistrict(district);
            postalAddress.setCountry(country);

            party.setPostalAddress(postalAddress);
        }

        /* <cac:Party><cac:PartyLegalEntity> */
        PartyLegalEntityType partyLegalEntity = null;
        {
            partyLegalEntity = new PartyLegalEntityType();

            RegistrationNameType registrationName = new RegistrationNameType();
            registrationName.setValue(socialReasonValue);
            partyLegalEntity.setRegistrationName(registrationName);

            List<PartyLegalEntityType> listaEntityTypes = new ArrayList<>();
            listaEntityTypes.add(partyLegalEntity);

            party.setPartyLegalEntity(listaEntityTypes);
        }

        /* <cac:Party><cac:Contact> */
        if (StringUtils.isNotBlank(electronicMailValue)) {
            ContactType contact = new ContactType();

            ElectronicMailType electronicMail = new ElectronicMailType();
            electronicMail.setValue(electronicMailValue);
            contact.setElectronicMail(electronicMail);

            if (StringUtils.isNotBlank(contactNameValue)) {
                NameType name = new NameType();
                name.setValue(contactNameValue);
                contact.setName(name);
            }

            party.setContact(contact);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-generateParty() [" + this.identifier + "]");
        }
        return party;
    } // generateParty

    /**
     * Este metodo genera el objeto PartyType que es utilizado para contener los
     * datos del receptor electronico y armar el documento UBL.
     *
     * @param fullnameValue Los apellidos y nombres del contribuyente receptor.
     * @param fiscalAddressValue La direccion fiscal del contribuyente receptor.
     * @param departmentValue El nombre del departamento del contribuyente
     * receptor.
     * @param provinceValue El nombre de la provincia del contribuyente
     * receptor.
     * @param districtValue El nombre del distrito del contribuyente receptor.
     * @param contactNameValue El nombre del contacto del contribuyente
     * receptor.
     * @param electronicMailValue El correo electronico del contribuyente
     * receptor.
     * @return Retorna el objeto PartyType con los datos del contribuyente
     * emisor electronico.
     * @throws Exception
     */
    private PartyType generatePartyByBoleta(String fullnameValue,
            String fiscalAddressValue, String departmentValue,
            String provinceValue, String districtValue,
            String contactNameValue, String electronicMailValue)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+generatePartyByBoleta() [" + this.identifier + "]");
        }
        PartyType party = new PartyType();

        /* <cac:Party><cac:PhysicalLocation> */
        if (StringUtils.isNotBlank(fiscalAddressValue)) {
            String descriptionValue = fiscalAddressValue + " - "
                    + districtValue + " - " + provinceValue + " - "
                    + departmentValue;

            LocationType physicalLocation = new LocationType();
            DescriptionType description = new DescriptionType();
            description.setValue(descriptionValue);
            physicalLocation.setDescription(description);

            party.setPhysicalLocation(physicalLocation);
        }

        /* <cac:Party><cac:PartyLegalEntity> */
        PartyLegalEntityType partyLegalEntity = null;
        {
            partyLegalEntity = new PartyLegalEntityType();

            RegistrationNameType registrationName = new RegistrationNameType();
            registrationName.setValue(fullnameValue);
            partyLegalEntity.setRegistrationName(registrationName);

            List<PartyLegalEntityType> listaEntityTypes = new ArrayList<>();
            listaEntityTypes.add(partyLegalEntity);

            party.setPartyLegalEntity(listaEntityTypes);
        }

        /* <cac:Party><cac:Contact> */
        if (StringUtils.isNotBlank(electronicMailValue)) {
            ContactType contact = new ContactType();

            ElectronicMailType electronicMail = new ElectronicMailType();
            electronicMail.setValue(electronicMailValue);
            contact.setElectronicMail(electronicMail);

            if (StringUtils.isNotBlank(contactNameValue)) {
                NameType name = new NameType();
                name.setValue(contactNameValue);
                contact.setName(name);
            }

            party.setContact(contact);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-generatePartyByBoleta() [" + this.identifier + "]");
        }
        return party;
    } // generatePartyByBoleta

    /**
     * Este metodo coloca la informacion adicional ingresada referente al
     * contribuyente en un objeto JAXB (InvoiceType, CreditNoteType,
     * DebitNoteType).
     *
     * @param ublDocument El objeto JAXB.
     * @param signatureType El objeto SignatureType que contiene los datos de la
     * firma.
     * @param accountingSupplierParty El objeto SupplierPartyType que contiene
     * la informacion del emisor electronico.
     * @param accountingCustomerParty El objeto CustomerPartyType que contiene
     * la informacion del receptor electronico.
     * @param documentSerie La serie del documento UBL.
     * @param documentCorrelative El numero correlativo del documento UBL.
     * @param relatedDocumentCorrelative El numero correlativo del documento
     * relacionado. (Solo para CreditNoteType y DebitNoteType).
     * @param docUUID El identificador del documento.
     * @return Retorna el objeto JAXB (Documento UBL) con la informacion
     * agregada del caso de prueba.
     * @throws UBLDocumentException
     */
    public Object putInformationInUBLDocument(Object ublDocument,
            SignatureType signatureType,
            SupplierPartyType accountingSupplierParty,
            CustomerPartyType accountingCustomerParty, String documentSerie,
            String documentCorrelative, String relatedDocumentCorrelative,
            String docUUID) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+putInformationInUBLDocument() [" + this.identifier
                    + "] [" + docUUID + "]");
        }
        Object outputDocument = null;

        try {
            /* <cbd:ID> */
            IDType idDocument = new IDType();

            /* <cbc:IssueDate> */
            IssueDateType issueDate = new IssueDateType();
            {
                String issueDateValue = formatDateToString(Calendar
                        .getInstance().getTime(), IUBLConfig.ISSUEDATE_FORMAT);
                issueDate.setValue(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(issueDateValue));
            }

            if (ublDocument instanceof InvoiceType) {
                /* Agregar la serie y el correlativo al ID */
                idDocument.setValue(documentSerie + "-" + documentCorrelative);

                InvoiceType invoiceType = (InvoiceType) ublDocument;

                invoiceType.setID(null);
                invoiceType.setIssueDate(null);
                invoiceType.getSignature().clear();

                /* Agregando los valores genericos del documento */
                invoiceType.setID(idDocument);
                invoiceType.setIssueDate(issueDate);
                invoiceType.getSignature().add(signatureType);

                /* Agregando la informacion de emisor y receptor */
                if (invoiceType.getInvoiceTypeCode().getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.error("putInformationInUBLDocument() ["
                                + this.identifier + "] [" + docUUID
                                + "] Colocando informacion en FACTURA.");
                    }
                    invoiceType.setAccountingSupplierParty(null);
                    invoiceType
                            .setAccountingSupplierParty(accountingSupplierParty);

                    invoiceType.setAccountingCustomerParty(null);
                    invoiceType
                            .setAccountingCustomerParty(accountingCustomerParty);
                } else if (invoiceType.getInvoiceTypeCode().getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.error("putInformationInUBLDocument() ["
                                + this.identifier + "] [" + docUUID
                                + "] Colocando informacion en BOLETA.");
                    }

                    /*
                     * NOTA: Para el caso de BOLETA solo se ingresara el
                     * contribuyente emisor, porque el receptor quedara con la
                     * informacion puesta en el TEMPLATE.
                     */
                    invoiceType.setAccountingSupplierParty(null);
                    invoiceType
                            .setAccountingSupplierParty(accountingSupplierParty);
                } else {
                    logger.error("putInformationInUBLDocument() ["
                            + this.identifier + "] [" + docUUID + "] "
                            + IVenturaError.ERROR_305.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_305);
                }

                /* Pasando la informacion al objeto de SALIDA */
                outputDocument = invoiceType;
            } else if (ublDocument instanceof CreditNoteType) {
                /* Agregar la serie y el correlativo al ID */
                idDocument.setValue(documentSerie + "-" + documentCorrelative);

                /* Crear objeto ReferenceIDType */
                ReferenceIDType referenceID = new ReferenceIDType();
                referenceID.setValue(documentSerie + "-"
                        + relatedDocumentCorrelative);

                /* Crear objeto IDType */
                IDType idReference = new IDType();
                idReference.setValue(documentSerie + "-"
                        + relatedDocumentCorrelative);

                CreditNoteType creditNoteType = (CreditNoteType) ublDocument;

                creditNoteType.setID(null);
                creditNoteType.setIssueDate(null);
                creditNoteType.getSignature().clear();
                creditNoteType.getDiscrepancyResponse().get(0)
                        .setReferenceID(null);
                creditNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().setID(null);

                /* Agregando los valores genericos del documento */
                creditNoteType.setID(idDocument);
                creditNoteType.setIssueDate(issueDate);
                creditNoteType.getSignature().add(signatureType);
                creditNoteType.getDiscrepancyResponse().get(0)
                        .setReferenceID(referenceID);
                creditNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().setID(idReference);

                /* Agregando la informacion de emisor y receptor */
                if (creditNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().getDocumentTypeCode()
                        .getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putInformationInUBLDocument() ["
                                + this.identifier
                                + "] ["
                                + docUUID
                                + "] Colocando informacion en NOTA DE CREDITO referente a una FACTURA.");
                    }
                    creditNoteType.setAccountingSupplierParty(null);
                    creditNoteType
                            .setAccountingSupplierParty(accountingSupplierParty);

                    creditNoteType.setAccountingCustomerParty(null);
                    creditNoteType
                            .setAccountingCustomerParty(accountingCustomerParty);
                } else if (creditNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().getDocumentTypeCode()
                        .getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putInformationInUBLDocument() ["
                                + this.identifier
                                + "] ["
                                + docUUID
                                + "] Colocando informacion en NOTA DE CREDITO referente a una BOLETA.");
                    }

                    /*
                     * NOTA: Para el caso de BOLETA solo se ingresara el
                     * contribuyente emisor, porque el receptor quedara con la
                     * informacion puesta en el TEMPLATE.
                     */
                    creditNoteType.setAccountingSupplierParty(null);
                    creditNoteType
                            .setAccountingSupplierParty(accountingSupplierParty);
                } else {
                    logger.error("putInformationInUBLDocument01() ["
                            + this.identifier + "] [" + docUUID + "] "
                            + IVenturaError.ERROR_305.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_308);
                }

                /* Pasando la informacion al objeto de SALIDA */
                outputDocument = creditNoteType;
            } else if (ublDocument instanceof DebitNoteType) {
                /* Agregar la serie y el correlativo al ID */
                idDocument.setValue(documentSerie + "-" + documentCorrelative);

                /* Crear objeto ReferenceIDType */
                ReferenceIDType referenceID = new ReferenceIDType();
                referenceID.setValue(documentSerie + "-"
                        + relatedDocumentCorrelative);

                /* Crear objeto IDType */
                IDType idReference = new IDType();
                idReference.setValue(documentSerie + "-"
                        + relatedDocumentCorrelative);

                DebitNoteType debitNoteType = (DebitNoteType) ublDocument;

                debitNoteType.setID(null);
                debitNoteType.setIssueDate(null);
                debitNoteType.getSignature().clear();
                debitNoteType.getDiscrepancyResponse().get(0)
                        .setReferenceID(null);
                debitNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().setID(null);

                /* Agregando los valores genericos del documento */
                debitNoteType.setID(idDocument);
                debitNoteType.setIssueDate(issueDate);
                debitNoteType.getSignature().add(signatureType);
                debitNoteType.getDiscrepancyResponse().get(0)
                        .setReferenceID(referenceID);
                debitNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().setID(idReference);

                /* Agregando la informacion de emisor y receptor */
                if (debitNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().getDocumentTypeCode()
                        .getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putInformationInUBLDocument() ["
                                + this.identifier
                                + "] ["
                                + docUUID
                                + "] Colocando informacion en NOTA DE DEBITO referente a una FACTURA.");
                    }
                    debitNoteType.setAccountingSupplierParty(null);
                    debitNoteType
                            .setAccountingSupplierParty(accountingSupplierParty);

                    debitNoteType.setAccountingCustomerParty(null);
                    debitNoteType
                            .setAccountingCustomerParty(accountingCustomerParty);
                } else if (debitNoteType.getBillingReference().get(0)
                        .getInvoiceDocumentReference().getDocumentTypeCode()
                        .getValue()
                        .equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("putInformationInUBLDocument() ["
                                + this.identifier
                                + "] ["
                                + docUUID
                                + "] Colocando informacion en NOTA DE DEBITO referente a una BOLETA.");
                    }

                    /*
                     * NOTA: Para el caso de BOLETA solo se ingresara el
                     * contribuyente emisor, porque el receptor quedara con la
                     * informacion puesta en el TEMPLATE.
                     */
                    debitNoteType.setAccountingSupplierParty(null);
                    debitNoteType
                            .setAccountingSupplierParty(accountingSupplierParty);
                } else {
                    logger.error("putInformationInUBLDocument01() ["
                            + this.identifier + "] [" + docUUID + "] "
                            + IVenturaError.ERROR_305.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_308);
                }

                /* Pasando la informacion al objeto de SALIDA */
                outputDocument = debitNoteType;
            } else {
                logger.error("putInformationInUBLDocument01() ["
                        + this.identifier + "] [" + docUUID + "] "
                        + IVenturaError.ERROR_309.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_309);
            }
        } catch (Exception e) {
            logger.error("putInformationInUBLDocument01() [" + this.identifier
                    + "] [" + docUUID + "] Exception(" + e.getClass().getName()
                    + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_307);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-putInformationInUBLDocument01() [" + this.identifier
                    + "] [" + docUUID + "]");
        }
        return outputDocument;
    } // putInformationInUBLDocument

    /**
     * Este metodo coloca la informacion adicional ingresada referenta al
     * contribuyente en un objeto JAXB SummaryDocumentsType o
     * VoidedDocumentsType.
     *
     * @param ublDocument El objeto JAXB.
     * @param signatureType El objeto SignatureType que contiene los datos de la
     * firma.
     * @param accountingSupplierParty El objeto SupplierPartyType que contiene
     * la informacion del emisor electronico.
     * @param documentType El tipo de documento.
     * @param documentCorrelative El numero correlativo del documento UBL.
     * @param documentNumberMap El objeto MAP que contiene los numeros de los
     * documentos a ingresar.
     * @param docUUID El identificador del documento.
     * @return Retorna el objeto JAXB con la informacion agregada del caso de
     * prueba.
     * @throws UBLDocumentException
     */
    public Object putInformationInSummaryAndVoidedDocument(Object ublDocument,
            SignatureType signatureType,
            SupplierPartyType accountingSupplierParty, String documentType,
            String documentCorrelative, Map<String, String> documentNumberMap,
            String docUUID) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+putInformationInSummaryDocument() ["
                    + this.identifier + "] [" + docUUID + "]");
        }
        Object outputDocument = null;

        try {
            /* <cbd:ID> */
            IDType idDocument = new IDType();

            /* <cbc:ReferenceDate> */
            ReferenceDateType referenceDate = new ReferenceDateType();

            /* <cbc:IssueDate> */
            IssueDateType issueDate = new IssueDateType();

            {
                String dateInString = formatDateToString(Calendar.getInstance()
                        .getTime(), IUBLConfig.ISSUEDATE_FORMAT);

                referenceDate.setValue(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(dateInString));
                issueDate.setValue(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(dateInString));
            }

            if (ublDocument instanceof SummaryDocumentsType) {
                /* Agregar la serie y el correlativo al ID */
                String dateInString = formatDateToString(Calendar.getInstance()
                        .getTime(), IUBLConfig.DOCUMENT_DATE_FORMAT);
                idDocument.setValue(documentType + "-" + dateInString + "-"
                        + documentCorrelative);

                SummaryDocumentsType summaryDocumentsType = (SummaryDocumentsType) ublDocument;

                summaryDocumentsType.setID(null);
                summaryDocumentsType.setReferenceDate(null);
                summaryDocumentsType.setIssueDate(null);
                summaryDocumentsType.getSignature().clear();
                summaryDocumentsType.setAccountingSupplierParty(null);

                /* Agregando los valores genericos del documento */
                summaryDocumentsType.setID(idDocument);
                summaryDocumentsType.setReferenceDate(referenceDate);
                summaryDocumentsType.setIssueDate(issueDate);
                summaryDocumentsType.getSignature().add(signatureType);
                summaryDocumentsType
                        .setAccountingSupplierParty(accountingSupplierParty);

                /*
                 * Agregando la informacion a las lineas del documento.
                 * SummaryDocumentsLineType
                 */
                List<SummaryDocumentsLineType> summaryLines = summaryDocumentsType
                        .getSummaryDocumentsLine();
                for (SummaryDocumentsLineType line : summaryLines) {
                    if (line.getDocumentSerialID().getValue()
                            .equalsIgnoreCase(IUBLConfig.SERIE_BB11)) {
                        if (line.getDocumentTypeCode().getValue()
                                .equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                            IdentifierType startDocumentNumberID = new IdentifierType();
                            startDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_03_START));
                            line.setStartDocumentNumberID(startDocumentNumberID);

                            IdentifierType endDocumentNumberID = new IdentifierType();
                            endDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_03_END));
                            line.setEndDocumentNumberID(endDocumentNumberID);
                        } else if (line
                                .getDocumentTypeCode()
                                .getValue()
                                .equalsIgnoreCase(
                                        IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                            IdentifierType startDocumentNumberID = new IdentifierType();
                            startDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_07_START));
                            line.setStartDocumentNumberID(startDocumentNumberID);

                            IdentifierType endDocumentNumberID = new IdentifierType();
                            endDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_07_END));
                            line.setEndDocumentNumberID(endDocumentNumberID);
                        } else if (line
                                .getDocumentSerialID()
                                .getValue()
                                .equalsIgnoreCase(
                                        IUBLConfig.DOC_DEBIT_NOTE_CODE)) {
                            IdentifierType startDocumentNumberID = new IdentifierType();
                            startDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_08_START));
                            line.setStartDocumentNumberID(startDocumentNumberID);

                            IdentifierType endDocumentNumberID = new IdentifierType();
                            endDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB11_08_END));
                            line.setEndDocumentNumberID(endDocumentNumberID);
                        }
                    } else if (line.getDocumentSerialID().getValue()
                            .equalsIgnoreCase(IUBLConfig.SERIE_BB12)) {
                        if (line.getDocumentTypeCode().getValue()
                                .equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                            IdentifierType startDocumentNumberID = new IdentifierType();
                            startDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB12_03_START));
                            line.setStartDocumentNumberID(startDocumentNumberID);

                            IdentifierType endDocumentNumberID = new IdentifierType();
                            endDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB12_03_END));
                            line.setEndDocumentNumberID(endDocumentNumberID);
                        } else if (line
                                .getDocumentTypeCode()
                                .getValue()
                                .equalsIgnoreCase(
                                        IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                            IdentifierType startDocumentNumberID = new IdentifierType();
                            startDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB12_07_START));
                            line.setStartDocumentNumberID(startDocumentNumberID);

                            IdentifierType endDocumentNumberID = new IdentifierType();
                            endDocumentNumberID.setValue(documentNumberMap
                                    .get(IUBLConfig.DOC_NUMBER_BB12_07_END));
                            line.setEndDocumentNumberID(endDocumentNumberID);
                        }
                    }
                } // for

                /* Pasando la informacion al objeto de SALIDA */
                outputDocument = summaryDocumentsType;
            } else if (ublDocument instanceof VoidedDocumentsType) {
                /* Agregar la serie y el correlativo al ID */
                String dateInString = formatDateToString(Calendar.getInstance()
                        .getTime(), IUBLConfig.DOCUMENT_DATE_FORMAT);
                idDocument.setValue(documentType + "-" + dateInString + "-"
                        + documentCorrelative);

                VoidedDocumentsType voidedDocumentsType = (VoidedDocumentsType) ublDocument;

                voidedDocumentsType.setID(null);
                voidedDocumentsType.setReferenceDate(null);
                voidedDocumentsType.setIssueDate(null);
                voidedDocumentsType.getSignature().clear();
                voidedDocumentsType.setAccountingSupplierParty(null);

                /* Agregando los valores genericos del documento */
                voidedDocumentsType.setID(idDocument);
                voidedDocumentsType.setReferenceDate(referenceDate);
                voidedDocumentsType.setIssueDate(issueDate);
                voidedDocumentsType.getSignature().add(signatureType);
                voidedDocumentsType
                        .setAccountingSupplierParty(accountingSupplierParty);

                /*
                 * Agregando la informacion a las lineas del documento.
                 * SummaryDocumentsLineType
                 * 
                 * Son 5 lineas de items
                 */
                {
                    IdentifierType documentNumberID_01 = new IdentifierType();
                    documentNumberID_01.setValue(documentNumberMap
                            .get(IUBLConfig.DOC_NUMBER_FF11_01));
                    voidedDocumentsType.getVoidedDocumentsLine().get(0)
                            .setDocumentNumberID(documentNumberID_01);

                    IdentifierType documentNumberID_02 = new IdentifierType();
                    documentNumberID_02.setValue(documentNumberMap
                            .get(IUBLConfig.DOC_NUMBER_FF11_02));
                    voidedDocumentsType.getVoidedDocumentsLine().get(1)
                            .setDocumentNumberID(documentNumberID_02);

                    IdentifierType documentNumberID_03 = new IdentifierType();
                    documentNumberID_03.setValue(documentNumberMap
                            .get(IUBLConfig.DOC_NUMBER_FF11_03));
                    voidedDocumentsType.getVoidedDocumentsLine().get(2)
                            .setDocumentNumberID(documentNumberID_03);

                    IdentifierType documentNumberID_04 = new IdentifierType();
                    documentNumberID_04.setValue(documentNumberMap
                            .get(IUBLConfig.DOC_NUMBER_FF11_04));
                    voidedDocumentsType.getVoidedDocumentsLine().get(3)
                            .setDocumentNumberID(documentNumberID_04);

                    IdentifierType documentNumberID_05 = new IdentifierType();
                    documentNumberID_05.setValue(documentNumberMap
                            .get(IUBLConfig.DOC_NUMBER_FF11_05));
                    voidedDocumentsType.getVoidedDocumentsLine().get(4)
                            .setDocumentNumberID(documentNumberID_05);
                }

                /* Pasando la informacion al objeto de SALIDA */
                outputDocument = voidedDocumentsType;
            } else {
                logger.error("putInformationInSummaryDocument() ["
                        + this.identifier + "] [" + docUUID + "] "
                        + IVenturaError.ERROR_309.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_309);
            }
        } catch (Exception e) {
            logger.error("putInformationInSummaryDocument() ["
                    + this.identifier + "] [" + docUUID + "] Exception("
                    + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_307);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-putInformationInSummaryDocument() ["
                    + this.identifier + "] [" + docUUID + "]");
        }
        return outputDocument;
    } // putInformationInSummaryDocument

    /**
     * Este metodo formatea una fecha al formato String.
     *
     * @param inputDate Fecha de entrada.
     * @param pattern Patron de la fecha.
     * @return Retorna una fecha en String.
     */
    public String formatDateToString(Date inputDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(inputDate);
    } // formatDateToString

    /**
     * Este metodo genera una constancia de recepcion ficticia, que simula el
     * CDR original.
     *
     * @param docUUID Identificador del documento.
     * @return
     */
    public ApplicationResponseType generateConstancyDummy(String docUUID)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateConstancyDummy()");
        }
        ApplicationResponseType applicationResponse = null;

        try {
            /* Crear objeto ApplicationResponseType */
            applicationResponse = new ApplicationResponseType();

            DocumentResponseType documentResponse = new DocumentResponseType();
            {
                ResponseType response = new ResponseType();

                ResponseCodeType responseCode = new ResponseCodeType();
                responseCode.setValue("0");

                DescriptionType description = new DescriptionType();
                description.setValue("Documento DUMMY");

                response.setResponseCode(responseCode);
                response.getDescription().add(description);

                documentResponse.setResponse(response);
            }

            applicationResponse.getDocumentResponse().add(documentResponse);
        } catch (Exception e) {
            logger.error("generateConstancyDummy() ERROR: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateConstancyDummy()");
        }
        return applicationResponse;
    } // generateConstancyDummy

    public String retornarLleno(String palabra) {
        if (palabra.equalsIgnoreCase("")) {
            return "-";
        } else {
            return palabra;
        }

    }

    public RetentionType generateRetentionType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateRetentionType() [" + this.identifier + "]");
        }
        RetentionType retentionType = null;

        try {

            /* Instanciar objeto PerceptionType para la Retencion */
            retentionType = new RetentionType();

            /* Agregar <Retention><cbc:CustomizationID> */
            retentionType.setCustomizationID(getCustomizationID());

            /* Agregar Firma */
            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {

                /* Agregar TAG para colocar la FIRMA */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                retentionType.setUblExtensions(ublExtensions);
            }

            /* Agregar <Retention><cbc:UBLVersionID> */
            retentionType.setUblVersionID(getUBLVersionID());

            /* Agregar <Retention><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }

            SignatureType signature
                    = generateSignature(transaction.getDocIdentidadNro(),
                            transaction.getRazonSocial(), signerName);
            retentionType.getSignature().add(signature);

            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            retentionType.setId(idDocIdentifier);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando IssueDate: " + transaction.getDOCFechaEmision());
            }

            /* Agregar <Retention><cbc:IssueDate> */
            retentionType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando AgentParty: ");
            }

            /* Agregar <Perception><cac:AgentParty> */
            retentionType.setAgentParty(generatePartyPerception(transaction
                    .getDocIdentidadNro(), transaction.getRazonSocial(),
                    transaction.getNombreComercial(), transaction
                    .getDIRDireccion(), transaction
                    .getDIRDepartamento(), transaction
                    .getDIRProvincia(), transaction.getDIRDistrito(),
                    transaction.getDIRUbigeo(), transaction
                    .getDIRPais(), transaction.getPersonContacto(),
                    transaction.getEMail(), transaction.getDocIdentidadTipo()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando ReceiverParty: ");
            }

            /* Agregar <Perception><cac:ReceiverParty> */
            retentionType.setReceiverParty(generatePartyPerception(transaction
                    .getSNDocIdentidadNro(), transaction.getSNRazonSocial(),
                    transaction.getSNNombreComercial(), transaction
                    .getSNDIRDireccion(), transaction
                    .getSNDIRDepartamento(), transaction
                    .getSNDIRProvincia(), transaction
                    .getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getPersonContacto(),
                    transaction.getSNEMail(), transaction
                    .getSNDocIdentidadTipo()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando sunatRetentionSystemCodeType: ");
            }

            SUNATRetentionSystemCodeType sunatRetentionSystemCodeType = new SUNATRetentionSystemCodeType();
            sunatRetentionSystemCodeType.setValue(transaction.getRETRegimen());
            retentionType
                    .setSunatRetentionSystemCode(sunatRetentionSystemCodeType);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando sunatRetentionPercentType: " + transaction.getRETTasa());
            }

            SUNATRetentionPercentType sunatRetentionPercentType = new SUNATRetentionPercentType();
            sunatRetentionPercentType.setValue(transaction.getRETTasa());
            retentionType.setSunatRetentionPercent(sunatRetentionPercentType);

            NoteType dueDateNote = new NoteType();
            dueDateNote.setValue(transaction.getObservaciones());
            retentionType.setNote(dueDateNote);
            BigDecimal bg = transaction.getDOCMontoTotal();
            bg = bg.setScale(2, BigDecimal.ROUND_HALF_UP);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando totalInvoiceAmountType: ");
            }

            TotalInvoiceAmountType totalInvoiceAmountType = new TotalInvoiceAmountType();
            totalInvoiceAmountType.setValue(bg);
            totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType.valueOf(transaction.getDOCMONCodigo()));
            retentionType.setTotalInvoiceAmount(totalInvoiceAmountType);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando totalPaidType: ");
            }

            SUNATTotalPaid totalPaidType = new SUNATTotalPaid();
            totalPaidType.setValue(transaction.getImportePagado().setScale(2, BigDecimal.ROUND_HALF_UP));
            totalPaidType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(transaction.getMonedaPagado()));
            retentionType.setTotalPaid(totalPaidType);
            retentionType
                    .setSunatRetentionDocumentReference(generateDocumentReferenceV2(transaction
                                    .getTransaccionComprobantePagoList()));

        } catch (UBLDocumentException e) {
            logger.error("generateRetentionType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateRetentionType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_352, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateRetentionType() [" + this.identifier + "]");
        }

        return retentionType;

    }

    public PerceptionType generatePerceptionType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generatePerceptionType() [" + this.identifier + "]");
        }
        PerceptionType perceptionType = null;

        try {
            /* Instanciar objeto PerceptionType para la percepcion */
            perceptionType = new PerceptionType();

            /* Agregar <Invoice><cbc:UBLVersionID> */
            perceptionType.setUblVersionID(getUBLVersionID());

            /* Agregar <Invoice><cbc:CustomizationID> */
            perceptionType.setCustomizationID(getCustomizationID());

            /* Agregar Firma */
            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {

                /*
                 * Agregar TAG para colocar la FIRMA
                 * <CreditNote><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                perceptionType.setUblExtensions(ublExtensions);

                /*
                 * SignatureType signature =
                 * generateSignature(transaction.getDocIdentidadNro(),
                 * transaction.getRazonSocial(), signerName);
                 * perceptionType.getSignature().add(signature);
                 */
            }

            /* Agregar <Perception><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            perceptionType.setId(idDocIdentifier);

            /* Agregar <Perception><cbc:IssueDate> */
            perceptionType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            perceptionType.getSignature().add(signature);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() - AgentParty ");
            }
            /* Agregar <Perception><cac:AgentParty> */
            perceptionType.setAgentParty(generatePartyPerception(transaction
                    .getDocIdentidadNro(), transaction.getRazonSocial(),
                    transaction.getNombreComercial(), transaction
                    .getDIRDireccion(), transaction
                    .getDIRDepartamento(), transaction
                    .getDIRProvincia(), transaction.getDIRDistrito(),
                    transaction.getDIRUbigeo(), transaction
                    .getDIRPais(), transaction.getPersonContacto(),
                    transaction.getEMail(), transaction.getDocIdentidadTipo()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() - ReceiverParty ");
            }
            /* Agregar <Perception><cac:ReceiverParty> */
            perceptionType.setReceiverParty(generatePartyPerception(transaction
                    .getSNDocIdentidadNro(), transaction.getSNRazonSocial(),
                    transaction.getSNNombreComercial(), transaction
                    .getSNDIRDireccion(), transaction
                    .getSNDIRDepartamento(), transaction
                    .getSNDIRProvincia(), transaction
                    .getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getPersonContacto(),
                    transaction.getSNEMail(), transaction
                    .getSNDocIdentidadTipo()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() - SunatPerceptionSystemCode"
                        + transaction.getRETRegimen());
            }
            SUNATPerceptionSystemCodeType objSunatPerceptionSystemCodeType = new SUNATPerceptionSystemCodeType();
            objSunatPerceptionSystemCodeType.setValue(transaction
                    .getRETRegimen());
            perceptionType
                    .setSunatPerceptionSystemCode(objSunatPerceptionSystemCodeType);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() - SUNATPerceptionPercent"
                        + transaction.getRETTasa());
            }
            SUNATPerceptionPercentType objPerceptionPercentType = new SUNATPerceptionPercentType();
            objPerceptionPercentType.setValue(transaction.getRETTasa()
                    .toString());
            perceptionType.setSunatPerceptionPercent(objPerceptionPercentType);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() - Note  "
                        + transaction.getObservaciones());
            }
            NoteType dueDateNote = new NoteType();
            dueDateNote.setValue(transaction.getObservaciones());
            perceptionType.setNote(dueDateNote);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() -  TotalInvoiceAmount "
                        + transaction.getDOCMontoTotal());
            }
            BigDecimal bg = transaction.getDOCMontoTotal();
            bg = bg.setScale(2, BigDecimal.ROUND_HALF_UP);
            TotalInvoiceAmountType totalInvoiceAmountType = new TotalInvoiceAmountType();
            totalInvoiceAmountType.setValue(bg);
            totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(transaction.getDOCMONCodigo()));
            perceptionType.setTotalInvoiceAmount(totalInvoiceAmountType);

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() -  SUNATNetTotalCashed "
                        + transaction.getImportePagado());
            }
            SUNATNetTotalCashedType sunatNetTotalCashedType = new SUNATNetTotalCashedType();
            sunatNetTotalCashedType.setValue(transaction.getImportePagado().setScale(2, BigDecimal.ROUND_HALF_UP));
            sunatNetTotalCashedType.setCurrencyID(CurrencyCodeContentType
                    .valueOf(transaction.getMonedaPagado()));
            perceptionType.setSunatTotalCashed(sunatNetTotalCashedType);

            perceptionType
                    .setSunatPerceptionDocumentReference(generateDocumentReference(transaction
                                    .getTransaccionComprobantePagoList()));

            if (logger.isDebugEnabled()) {
                logger.debug("generatePerceptionType() -  Acabo");
            }
        } catch (UBLDocumentException e) {
            logger.error("generatePerceptionType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generatePerceptionType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_351, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generatePerceptionType() [" + this.identifier + "]");
        }
        return perceptionType;

    }

    public InvoiceType generateInvoiceType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateInvoiceType() [" + this.identifier + "]");
        }
        InvoiceType invoiceType = null;

        try {
            /* Instanciar objeto InvoiceType para la factura */
            invoiceType = new InvoiceType();

            /* Agregar <Invoice><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier
                        + "] Agregando TOTALES Y PROPIEDADES.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {
                /*
                 * Agregar Informacion de TOTALES y PROPIEDADES
                 * <Invoice><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent
                 * ><sac:AdditionalInformation>
                 */
                ublExtensions.getUBLExtension().add(
                        getUBLExtensionTotalAndProperty(
                                transaction.getTransaccionTotalesList(),
                                transaction.getTransaccionPropiedadesList(),
                                transaction.getTransaccionGuiasList(),
                                transaction.getSUNATTransact()));

                /*
                 * Agregar TAG para colocar la FIRMA
                 * <Invoice><ext:UBLExtensions>
                 * <ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                invoiceType.setUBLExtensions(ublExtensions);
            }

            /* Agregar <Invoice><cbc:UBLVersionID> */
            invoiceType.setUBLVersionID(getUBLVersionID());

            /* Agregar <Invoice><cbc:CustomizationID> */
            invoiceType.setCustomizationID(getCustomizationID());

            /* Agregar <Invoice><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            invoiceType.setID(idDocIdentifier);

            /* Agregar <Invoice><cbc:UUID> */
            invoiceType.setUUID(getUUID(this.identifier));

            /* Agregar <Invoice><cbc:IssueDate> */
            invoiceType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            /* Agregar <Invoice><cbc:InvoiceTypeCode> */
            invoiceType.setInvoiceTypeCode(getInvoiceTypeCode(transaction
                    .getDOCCodigo()));

            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier
                            + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                /*
                 * Agregar una nota para colocar la fecha de vencimiento.
                 */
                NoteType dueDateNote = new NoteType();
                dueDateNote.setValue(getDueDateValue(transaction
                        .getDOCFechaVencimiento()));
                invoiceType.getNote().add(dueDateNote);
            }

            /* Agregar <Invoice><cbc:DocumentCurrencyCode> */
            invoiceType.setDocumentCurrencyCode(getDocumentCurrencyCode(
                    transaction.getDOCMONNombre(),
                    transaction.getDOCMONCodigo()));

            /*
             * Agregar las guias de remision
             * 
             * <Invoice><cac:DespatchDocumentReference>
             */
            if (null != transaction.getTransaccionDocrefersList()
                    && 0 < transaction.getTransaccionDocrefersList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() ["
                            + this.identifier
                            + "] Verificar si existen GUIAS DE REMISION en la FACTURA.");
                }

                invoiceType.getDespatchDocumentReference().addAll(
                        getDespatchDocumentReferences(transaction
                                .getTransaccionDocrefersList()));

                if (logger.isDebugEnabled()) {
                    logger.debug("generateInvoiceType() [" + this.identifier
                            + "] Se agregaron ["
                            + invoiceType.getDespatchDocumentReference().size()
                            + "] guias de remision.");
                }
            }

            /*
             * Extraer la condicion de pago de ser el caso.
             */
            if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier
                            + "] Extraer la CONDICION DE PAGO.");
                }
                invoiceType.getContractDocumentReference().add(
                        getContractDocumentReference(
                                transaction.getDOCCondPago(),
                                IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
            }

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
            }
            if (null != transaction.getTransaccionContractdocrefList() && 0 < transaction.getTransaccionContractdocrefList().size()) {
                for (TransaccionContractdocref transContractdocref : transaction.getTransaccionContractdocrefList()) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS" + transContractdocref.getUsuariocampos().getNombre() + " :" + transContractdocref.getValor());
                    }
                    invoiceType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] SignatureType");
            }

            /* Agregar <Invoice><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            invoiceType.getSignature().add(signature);

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDocIdentidadNro() + "] getDocIdentidadNro()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDocIdentidadTipo() + "] getDocIdentidadTipo()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getRazonSocial() + "] getRazonSocial(),");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getNombreComercial() + "] getNombreComercial()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRDireccion() + "] getDIRDireccion()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRDepartamento() + "] getDIRDepartamento()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRProvincia() + "] getDIRProvincia()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRDistrito() + "] getDIRDistrito()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRUbigeo() + "] getDIRUbigeo()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRPais() + "] getDIRPais()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRPais() + "] getDIRPais()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getPersonContacto() + "] getPersonContacto()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getEMail() + "] getEMail()");
            }

            /* Agregar <Invoice><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial(),
                    transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(),
                    transaction.getDIRProvincia(),
                    transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(),
                    transaction.getPersonContacto(), transaction.getEMail());
            invoiceType.setAccountingSupplierParty(accountingSupplierParty);

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] CustomerPartyType");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDocIdentidadNro() + "] getDocIdentidadNro()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDocIdentidadTipo() + "] getDocIdentidadTipo()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNRazonSocial() + "] getRazonSocial(),");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNNombreComercial() + "] getNombreComercial()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDIRDireccion() + "] getDIRDireccion()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDIRDepartamento() + "] getDIRDepartamento()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDIRProvincia() + "] getDIRProvincia()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDIRDistrito() + "] getDIRDistrito()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getSNDIRUbigeo() + "] getDIRUbigeo()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRPais() + "] getDIRPais()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getDIRPais() + "] getDIRPais()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getPersonContacto() + "] getPersonContacto()");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + transaction.getEMail() + "] getEMail()");
            }

            /* Agregar <Invoice><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = generateAccountingCustomerParty(
                    transaction.getSNDocIdentidadNro(),
                    transaction.getSNDocIdentidadTipo(),
                    transaction.getSNRazonSocial(),
                    transaction.getSNNombreComercial(),
                    transaction.getSNDIRNomCalle(),
                    transaction.getSNDIRDepartamento(),
                    transaction.getSNDIRProvincia(),
                    transaction.getSNDIRDistrito(), transaction
                    .getSNDIRUbigeo(),
                    transaction.getSNDIRPais(),
                    transaction.getSNSegundoNombre(), transaction.getSNEMail());
            invoiceType.setAccountingCustomerParty(accountingCustomerParty);

            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] PaymentType");
            }

            /*
             * Anticipos relacionados al comprobante de pago
             * <Invoice><cac:PrepaidPayment>
             */
            if (null != transaction.getANTICIPOMonto()
                    && transaction.getANTICIPOMonto()
                    .compareTo(BigDecimal.ZERO) > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() ["
                            + this.identifier
                            + "] La transaccion contiene informacion de ANTICIPO.");
                }
                List<PaymentType> prepaidPayment = generatePrepaidPaymentV2(
                        transaction.getTransaccionAnticipoList());
                invoiceType.getPrepaidPayment().addAll(prepaidPayment);
            }

            /* Agregar impuestos <Invoice><cac:TaxTotal> */
            invoiceType.getTaxTotal().addAll(
                    getAllTaxTotal(transaction.getTransaccionImpuestosList()));

            /*
             * Agregar - Importe (Total de Op.Gravada, Op.Inafecta,
             * Op.Exonerada) - Importe total de la Factura - Descuento GLOBAL de
             * la Factura - Monto total de anticipos
             */
            invoiceType.setLegalMonetaryTotal(getMonetaryTotal(
                    transaction.getDOCImporte(),
                    transaction.getDOCMontoTotal(),
                    transaction.getDOCDescuento(),
                    transaction.getANTICIPOMonto(),
                    transaction.getDOCMONCodigo()));

            /* Agregar items <Invoice><cac:InvoiceLine> */
            invoiceType.getInvoiceLine().addAll(
                    getAllInvoiceLines(transaction.getTransaccionLineasList(),
                            transaction.getDOCMONCodigo()));

        } catch (UBLDocumentException e) {
            logger.error("generateInvoiceType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateInvoiceType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_341, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateInvoiceType() [" + this.identifier + "]");
        }
        return invoiceType;
    } // generateInvoiceType

    public DespatchAdviceType generateDespatchAdviceType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateDespatchAdviceType() [" + this.identifier + "]");
        }

        DespatchAdviceType despatchAdviceType = null;

        try {

            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {


                /*
                 * Agregar TAG para colocar la FIRMA
                 * <Invoice><ext:UBLExtensions>
                 * <ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                despatchAdviceType.setUBLExtensions(ublExtensions);

            }
            /* Agregar <<DespatchAdvice><cbc:UBLVersionID> */
            despatchAdviceType.setUBLVersionID(getUBLVersionID());

            /* Agregar <<DespatchAdvice><cbc:CustomizationID> */
            despatchAdviceType.setCustomizationID(getCustomizationID());

            /* Agregar <Invoice><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateDespatchAdviceType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            despatchAdviceType.setID(idDocIdentifier);

            /* Agregar <DespatchAdvice><cbc:UUID> */
            despatchAdviceType.setUUID(getUUID(this.identifier));

            /* Agregar <DespatchAdvice><cbc:IssueDate> */
            despatchAdviceType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            /* Agregar <DespatchAdvice><cbc:DespatchAdviceTypeCode> */
            despatchAdviceType.setDespatchAdviceTypeCode(getDespatchAdviceTypeCode(transaction.getDOCCodigo()));

            /* Agregar <DespatchAdvice><cbc:NoteType> */
            NoteType noteType = new NoteType();
            noteType.setValue(null);
            despatchAdviceType.getNote().add(noteType);

            /* Agregar <DespatchAdvice><cac:Signature> */
            SignatureType signature = generateSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName);
            despatchAdviceType.getSignature().add(signature);

            despatchAdviceType.setDespatchSupplierParty(null);

        } catch (Exception e) {
            logger.error("generateDespatchAdviceType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_342, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateDespatchAdviceType() [" + this.identifier + "]");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateBoletaType() [" + this.identifier + "]");
        }
        return despatchAdviceType;
    }

    public InvoiceType generateBoletaType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateBoletaType() [" + this.identifier + "]");
        }
        InvoiceType boletaType = null;

        try {
            /* Instanciar objeto InvoiceType para la boleta */
            boletaType = new InvoiceType();

            /* Agregar <Invoice><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateBoletaType() [" + this.identifier
                        + "] Agregando TOTALES Y PROPIEDADES.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {
                /*
                 * Agregar Informacion de TOTALES y PROPIEDADES
                 * <Invoice><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent
                 * ><sac:AdditionalInformation>
                 */
                ublExtensions.getUBLExtension().add(
                        getUBLExtensionTotalAndProperty(
                                transaction.getTransaccionTotalesList(),
                                transaction.getTransaccionPropiedadesList(), transaction.getTransaccionGuiasList(),
                                transaction.getSUNATTransact()));

                /*
                 * Agregar TAG para colocar la FIRMA
                 * <Invoice><ext:UBLExtensions>
                 * <ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                boletaType.setUBLExtensions(ublExtensions);
            }

            /* Agregar <Invoice><cbc:UBLVersionID> */
            boletaType.setUBLVersionID(getUBLVersionID());

            /* Agregar <Invoice><cbc:CustomizationID> */
            boletaType.setCustomizationID(getCustomizationID());

            /* Agregar <Invoice><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateBoletaType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            boletaType.setID(idDocIdentifier);

            /* Agregar <Invoice><cbc:UUID> */
            boletaType.setUUID(getUUID(this.identifier));

            /* Agregar <Invoice><cbc:IssueDate> */
            boletaType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            /* Agregar <Invoice><cbc:InvoiceTypeCode> */
            boletaType.setInvoiceTypeCode(getInvoiceTypeCode(transaction
                    .getDOCCodigo()));

            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier
                            + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                /*
                 * Agregar una nota para colocar la fecha de vencimiento.
                 */
                NoteType dueDateNote = new NoteType();
                dueDateNote.setValue(getDueDateValue(transaction
                        .getDOCFechaVencimiento()));
                boletaType.getNote().add(dueDateNote);
            }

            /* Agregar <Invoice><cbc:DocumentCurrencyCode> */
            boletaType.setDocumentCurrencyCode(getDocumentCurrencyCode(
                    transaction.getDOCMONNombre(),
                    transaction.getDOCMONCodigo()));

            /*
             * Extraer la condicion de pago de ser el caso.
             */
            if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier
                            + "] Extraer la CONDICION DE PAGO.");
                }
                boletaType.getContractDocumentReference().add(
                        getContractDocumentReference(
                                transaction.getDOCCondPago(),
                                IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
            }

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
            }
            if (null != transaction.getTransaccionContractdocrefList() && 0 < transaction.getTransaccionContractdocrefList().size()) {
                for (TransaccionContractdocref transContractdocref : transaction.getTransaccionContractdocrefList()) {
                    boletaType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
                }
            }


            /* Agregar <Invoice><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            boletaType.getSignature().add(signature);

            /* Agregar <Invoice><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial(),
                    transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(),
                    transaction.getDIRProvincia(),
                    transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(),
                    transaction.getPersonContacto(), transaction.getEMail());
            boletaType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <Invoice><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = generateAccountingCustomerParty(
                    transaction.getSNDocIdentidadNro(),
                    transaction.getSNDocIdentidadTipo(),
                    transaction.getSNRazonSocial(),
                    transaction.getSNNombreComercial(),
                    transaction.getSNDIRNomCalle(),
                    transaction.getSNDIRDepartamento(),
                    transaction.getSNDIRProvincia(),
                    transaction.getSNDIRDistrito(), transaction
                    .getSNDIRUbigeo(),
                    transaction.getSNDIRPais(),
                    transaction.getSNSegundoNombre(), transaction.getSNEMail());
            boletaType.setAccountingCustomerParty(accountingCustomerParty);

            if (null != transaction.getANTICIPOMonto()
                    && transaction.getANTICIPOMonto()
                    .compareTo(BigDecimal.ZERO) > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() ["
                            + this.identifier
                            + "] La transaccion contiene informacion de ANTICIPO.");
                }
                List<PaymentType> prepaidPayment = generatePrepaidPaymentV2(
                        transaction.getTransaccionAnticipoList());
                boletaType.getPrepaidPayment().addAll(prepaidPayment);
            }

            /*
             * Agregar las guias de remision
             * 
             * <Invoice><cac:DespatchDocumentReference>
             */
            if (null != transaction.getTransaccionDocrefersList()
                    && 0 < transaction.getTransaccionDocrefersList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() ["
                            + this.identifier
                            + "] Verificar si existen GUIAS DE REMISION en la FACTURA.");
                }

                boletaType.getDespatchDocumentReference().addAll(
                        getDespatchDocumentReferences(transaction
                                .getTransaccionDocrefersList()));

                if (logger.isDebugEnabled()) {
                    logger.debug("generateInvoiceType() [" + this.identifier
                            + "] Se agregaron ["
                            + boletaType.getDespatchDocumentReference().size()
                            + "] guias de remision.");
                }
            }

            /* Agregar impuestos <Invoice><cac:TaxTotal> */
            boletaType.getTaxTotal().addAll(
                    getAllTaxTotal(transaction.getTransaccionImpuestosList()));

            /*
             * Agregar - Importe (Total de Op.Gravada, Op.Inafecta,
             * Op.Exonerada) - Importe total de la Factura - Descuento GLOBAL de
             * la Factura - Monto total de anticipos
             */
            boletaType.setLegalMonetaryTotal(getMonetaryTotal(
                    transaction.getDOCImporte(),
                    transaction.getDOCMontoTotal(),
                    transaction.getDOCDescuento(),
                    transaction.getANTICIPOMonto(),
                    transaction.getDOCMONCodigo()));

            /* Agregar items <Invoice><cac:InvoiceLine> */
            boletaType.getInvoiceLine().addAll(
                    getAllBoletaLines(transaction.getTransaccionLineasList(),
                            transaction.getDOCMONCodigo()));
        } catch (UBLDocumentException e) {
            logger.error("generateBoletaType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateBoletaType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_342, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateBoletaType() [" + this.identifier + "]");
        }
        return boletaType;
    } // generateBoletaType

    public CreditNoteType generateCreditNoteType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateCreditNoteType() [" + this.identifier + "]");
        }
        CreditNoteType creditNoteType = null;

        try {
            /* Instanciar objeto CreditNoteType para la nota de credito */
            creditNoteType = new CreditNoteType();

            /* Agregar <CreditNote><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateCreditNoteType() [" + this.identifier
                        + "] Agregando TOTALES Y PROPIEDADES.");
            }
            BigDecimal subTotalValue = getSubtotalValueFromTransaction(transaction
                    .getTransaccionTotalesList());

            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {
                /*
                 * Agregar Informacion de TOTALES y PROPIEDADES
                 * <CreditNote><ext:
                 * UBLExtensions><ext:UBLExtension><ext:ExtensionContent
                 * ><sac:AdditionalInformation>
                 */
                ublExtensions.getUBLExtension().add(
                        getUBLExtensionTotalAndPropertyForNCND(
                                transaction.getTransaccionTotalesList(),
                                transaction.getTransaccionPropiedadesList(), transaction.getTransaccionGuiasList(),
                                transaction.getSUNATTransact()));

                if (subTotalValue.compareTo(BigDecimal.ZERO) != 0) {
                    TransaccionTotales tt = new TransaccionTotales();
                    TransaccionTotalesPK ttpk = new TransaccionTotalesPK();
                    ttpk.setFEId(transaction.getFEId());
                    ttpk.setId("1005");
                    tt.setMonto(subTotalValue);
                    tt.setTransaccionTotalesPK(ttpk);
                    transaction.getTransaccionTotalesList().add(tt);
                }

                /*
                 * Agregar TAG para colocar la FIRMA
                 * <CreditNote><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                creditNoteType.setUBLExtensions(ublExtensions);
            }

            /* Agregar <CreditNote><cbc:UBLVersionID> */
            creditNoteType.setUBLVersionID(getUBLVersionID());

            /* Agregar <CreditNote><cbc:CustomizationID> */
            creditNoteType.setCustomizationID(getCustomizationID());

            /* Agregar <CreditNote><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateCreditNoteType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            creditNoteType.setID(idDocIdentifier);

            /* Agregar <CreditNote><cbc:UUID> */
            creditNoteType.setUUID(getUUID(this.identifier));

            /* Agregar <CreditNote><cbc:IssueDate> */
            creditNoteType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateCreditNoteType() [" + this.identifier
                            + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                /*
                 * Agregar una nota para colocar la fecha de vencimiento.
                 */
                NoteType dueDateNote = new NoteType();
                dueDateNote.setValue(getDueDateValue(transaction
                        .getDOCFechaVencimiento()));
                creditNoteType.getNote().add(dueDateNote);
            }

            /* Agregar <CreditNote><cbc:DocumentCurrencyCode> */
            creditNoteType.setDocumentCurrencyCode(getDocumentCurrencyCode(
                    transaction.getDOCMONNombre(),
                    transaction.getDOCMONCodigo()));

            /*
             * Agregar las guias de remision
             * 
             * <Invoice><cac:DespatchDocumentReference>
             */
            if (null != transaction.getTransaccionDocrefersList()
                    && 0 < transaction.getTransaccionDocrefersList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() ["
                            + this.identifier
                            + "] Verificar si existen GUIAS DE REMISION en la FACTURA.");
                }
                creditNoteType.getDespatchDocumentReference().addAll(
                        getDespatchDocumentReferences(transaction
                                .getTransaccionDocrefersList()));
                if (logger.isDebugEnabled()) {
                    logger.debug("generateInvoiceType() ["
                            + this.identifier
                            + "] Se agregaron ["
                            + creditNoteType.getDespatchDocumentReference()
                            .size() + "] guias de remision.");
                }
            }

            /*
             * Extraer la condicion de pago de ser el caso.
             */
            if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier
                            + "] Extraer la CONDICION DE PAGO.");
                }
                creditNoteType.getContractDocumentReference().add(
                        getContractDocumentReference(
                                transaction.getDOCCondPago(),
                                IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
            }

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
            }

            if (null != transaction.getTransaccionContractdocrefList() && 0 < transaction.getTransaccionContractdocrefList().size()) {
                for (TransaccionContractdocref transContractdocref : transaction.getTransaccionContractdocrefList()) {
                    creditNoteType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
                }
            }


            /* Agregar <CreditNote><cac:DiscrepancyResponse> */
            creditNoteType.getDiscrepancyResponse().add(
                    getDiscrepancyResponse(transaction.getREFDOCId(),
                            transaction.getREFDOCMotivCode(),
                            transaction.getREFDOCMotivDesc()));

            /* Agregar <CreditNote><cac:BillingReference> */
            creditNoteType.getBillingReference().add(
                    getBillingReference(transaction.getREFDOCId(),
                            transaction.getREFDOCTipo()));

            /* Agregar <CreditNote><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            creditNoteType.getSignature().add(signature);

            /* Agregar <CreditNote><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial(),
                    transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(),
                    transaction.getDIRProvincia(),
                    transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(),
                    transaction.getPersonContacto(), transaction.getEMail());
            creditNoteType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <CreditNote><cac:AccountingCustomerParty> */
            if (transaction.getDOCId().startsWith(
                    IUBLConfig.INVOICE_SERIE_PREFIX)) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateCreditNoteType() [" + this.identifier
                            + "] Generando RECEPTOR de FACTURA.");
                }

                CustomerPartyType accountingCustomerParty = generateAccountingCustomerParty(
                        transaction.getSNDocIdentidadNro(),
                        transaction.getSNDocIdentidadTipo(),
                        transaction.getSNRazonSocial(),
                        transaction.getSNNombreComercial(),
                        transaction.getSNDIRNomCalle(),
                        transaction.getSNDIRDepartamento(),
                        transaction.getSNDIRProvincia(),
                        transaction.getSNDIRDistrito(), transaction
                        .getSNDIRUbigeo(),
                        transaction.getSNDIRPais(),
                        transaction.getSNSegundoNombre(), transaction.getSNEMail());
                creditNoteType.setAccountingCustomerParty(accountingCustomerParty);

            } else if (transaction.getDOCId().startsWith(
                    IUBLConfig.BOLETA_SERIE_PREFIX)) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateCreditNoteType() [" + this.identifier
                            + "] Generando RECEPTOR de BOLETA.");
                }
                CustomerPartyType accountingCustomerParty = generateAccountingCustomerPartyV2(
                        transaction.getSNDocIdentidadNro(),
                        transaction.getSNDocIdentidadTipo(),
                        transaction.getSNRazonSocial(),
                        transaction.getSNDIRNomCalle(),
                        transaction.getSNDIRDepartamento(),
                        transaction.getSNDIRProvincia(),
                        transaction.getSNDIRDistrito(),
                        transaction.getSNSegundoNombre(),
                        transaction.getSNEMail());
                creditNoteType.setAccountingCustomerParty(accountingCustomerParty);
            } else {
                logger.error("generateCreditNoteType() [" + this.identifier
                        + "] ERROR: " + IVenturaError.ERROR_339.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_339);
            }

            /* Agregar impuestos <CreditNote><cac:TaxTotal> */
            creditNoteType.getTaxTotal().addAll(
                    getAllTaxTotal(transaction.getTransaccionImpuestosList()));

            /*
             * Agregar - Importe (Total de Op.Gravada, Op.Inafecta,
             * Op.Exonerada) - Importe total de la Factura - Descuento GLOBAL de
             * la Factura - NO EXISTE ANTICIPOS
             */
            creditNoteType.setLegalMonetaryTotal(getMonetaryTotal(
                    transaction.getDOCImporte(),
                    transaction.getDOCMontoTotal(),
                    transaction.getDOCDescuento(), null,
                    transaction.getDOCMONCodigo()));

            /* Agregar el SUBTOTAL en este TAG */
            PayableRoundingAmountType payableRoundingAmount = new PayableRoundingAmountType();
            if (subTotalValue == null) {
                subTotalValue = BigDecimal.ZERO;
            }
            payableRoundingAmount.setValue(subTotalValue);
            payableRoundingAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(transaction.getDOCMONCodigo()));
            creditNoteType.getLegalMonetaryTotal().setPayableRoundingAmount(
                    payableRoundingAmount);

            /* Agregar items <CreditNote><cac:CreditNoteLine> */
            creditNoteType.getCreditNoteLine().addAll(
                    getAllCreditNoteLines(
                            transaction.getTransaccionLineasList(),
                            transaction.getDOCMONCodigo()));
        } catch (UBLDocumentException e) {
            logger.error("generateCreditNoteType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateCreditNoteType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_343, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateCreditNoteType() [" + this.identifier + "]");
        }
        return creditNoteType;
    } // generateCreditNoteType

    public DebitNoteType generateDebitNoteType(Transaccion transaction,
            String signerName) throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+generateDebitNoteType() [" + this.identifier + "]");
        }
        DebitNoteType debitNoteType = null;

        try {
            /* Instanciar objeto DebitNoteType para la nota de debito */
            debitNoteType = new DebitNoteType();

            /* Agregar <DebitNote><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateDebitNoteType() [" + this.identifier
                        + "] Agregando TOTALES Y PROPIEDADES.");
            }
            BigDecimal subTotalValue = getSubtotalValueFromTransaction(transaction
                    .getTransaccionTotalesList());

            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            {
                /*
                 * Agregar Informacion de TOTALES y PROPIEDADES
                 * <DebitNote><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent
                 * ><sac:AdditionalInformation>
                 */
                ublExtensions.getUBLExtension().add(
                        getUBLExtensionTotalAndPropertyForNCND(
                                transaction.getTransaccionTotalesList(),
                                transaction.getTransaccionPropiedadesList(), transaction.getTransaccionGuiasList(),
                                transaction.getSUNATTransact()));

                if (subTotalValue.compareTo(BigDecimal.ZERO) != 0) {
                    TransaccionTotales tt = new TransaccionTotales();
                    TransaccionTotalesPK ttpk = new TransaccionTotalesPK();
                    ttpk.setFEId(transaction.getFEId());
                    ttpk.setId("1005");
                    tt.setMonto(subTotalValue);
                    tt.setTransaccionTotalesPK(ttpk);
                    transaction.getTransaccionTotalesList().add(tt);
                }

                /*
                 * Agregar TAG para colocar la FIRMA
                 * <DebitNote><ext:UBLExtensions
                 * ><ext:UBLExtension><ext:ExtensionContent>
                 */
                ublExtensions.getUBLExtension().add(getUBLExtensionSigner());

                debitNoteType.setUBLExtensions(ublExtensions);
            }

            /* Agregar <DebitNote><cbc:UBLVersionID> */
            debitNoteType.setUBLVersionID(getUBLVersionID());

            /* Agregar <DebitNote><cbc:CustomizationID> */
            debitNoteType.setCustomizationID(getCustomizationID());

            /* Agregar <DebitNote><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateDebitNoteType() [" + this.identifier
                        + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getDOCId());
            debitNoteType.setID(idDocIdentifier);

            /* Agregar <DebitNote><cbc:UUID> */
            debitNoteType.setUUID(getUUID(this.identifier));

            /* Agregar <DebitNote><cbc:IssueDate> */
            debitNoteType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaEmision()));

            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateDebitNoteType() [" + this.identifier
                            + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                /*
                 * Agregar una nota para colocar la fecha de vencimiento.
                 */
                NoteType dueDateNote = new NoteType();
                dueDateNote.setValue(getDueDateValue(transaction
                        .getDOCFechaVencimiento()));
                debitNoteType.getNote().add(dueDateNote);
            }

            /* Agregar <DebitNote><cbc:DocumentCurrencyCode> */
            debitNoteType.setDocumentCurrencyCode(getDocumentCurrencyCode(
                    transaction.getDOCMONNombre(),
                    transaction.getDOCMONCodigo()));

            /* Agregar <DebitNote><cac:DiscrepancyResponse> */
            debitNoteType.getDiscrepancyResponse().add(
                    getDiscrepancyResponse(transaction.getREFDOCId(),
                            transaction.getREFDOCMotivCode(),
                            transaction.getREFDOCMotivDesc()));

            /* Agregar <DebitNote><cac:BillingReference> */
            debitNoteType.getBillingReference().add(
                    getBillingReference(transaction.getREFDOCId(),
                            transaction.getREFDOCTipo()));

            /* Agregar <DebitNote><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            debitNoteType.getSignature().add(signature);

            /* Agregar <DebitNote><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial(),
                    transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(),
                    transaction.getDIRProvincia(),
                    transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(),
                    transaction.getPersonContacto(), transaction.getEMail());
            debitNoteType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <DebitNote><cac:AccountingCustomerParty> */
            if (transaction.getDOCId().startsWith(
                    IUBLConfig.INVOICE_SERIE_PREFIX)) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateDebitNoteType() [" + this.identifier
                            + "] Generando RECEPTOR de FACTURA.");
                }
                CustomerPartyType accountingCustomerParty = generateAccountingCustomerParty(
                        transaction.getSNDocIdentidadNro(),
                        transaction.getSNDocIdentidadTipo(),
                        transaction.getSNRazonSocial(),
                        transaction.getSNNombreComercial(),
                        transaction.getSNDIRNomCalle(),
                        transaction.getSNDIRDepartamento(),
                        transaction.getSNDIRProvincia(),
                        transaction.getSNDIRDistrito(), transaction
                        .getSNDIRUbigeo(),
                        transaction.getSNDIRPais(),
                        transaction.getSNSegundoNombre(),
                        transaction.getSNEMail());
                debitNoteType
                        .setAccountingCustomerParty(accountingCustomerParty);
            } else if (transaction.getDOCId().startsWith(
                    IUBLConfig.BOLETA_SERIE_PREFIX)) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateDebitNoteType() [" + this.identifier
                            + "] Generando RECEPTOR de BOLETA.");
                }

                CustomerPartyType accountingCustomerParty = generateAccountingCustomerPartyV2(
                        transaction.getSNDocIdentidadNro(),
                        transaction.getSNDocIdentidadTipo(),
                        transaction.getSNRazonSocial(),
                        transaction.getSNDIRNomCalle(),
                        transaction.getSNDIRDepartamento(),
                        transaction.getSNDIRProvincia(),
                        transaction.getSNDIRDistrito(),
                        transaction.getSNSegundoNombre(),
                        transaction.getSNEMail());

                debitNoteType
                        .setAccountingCustomerParty(accountingCustomerParty);
            } else {
                logger.error("generateDebitNoteType() [" + this.identifier
                        + "] ERROR: " + IVenturaError.ERROR_339.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_339);
            }

            /* Agregar impuestos <DebitNote><cac:TaxTotal> */
            debitNoteType.getTaxTotal().addAll(
                    getAllTaxTotal(transaction.getTransaccionImpuestosList()));

            /*
             * Agregar - Importe (Total de Op.Gravada, Op.Inafecta,
             * Op.Exonerada) - Importe total de la Factura - Descuento GLOBAL de
             * la Factura - NO EXISTE ANTICIPOS
             */
            debitNoteType.setRequestedMonetaryTotal(getMonetaryTotal(
                    transaction.getDOCImporte(),
                    transaction.getDOCMontoTotal(),
                    transaction.getDOCDescuento(), null,
                    transaction.getDOCMONCodigo()));

            /* Agregar el SUBTOTAL en este TAG */
            PayableRoundingAmountType payableRoundingAmount = new PayableRoundingAmountType();
            payableRoundingAmount.setValue(subTotalValue);
            payableRoundingAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(transaction.getDOCMONCodigo()));
            debitNoteType.getRequestedMonetaryTotal().setPayableRoundingAmount(
                    payableRoundingAmount);

            /* Agregar items <DebitNote><cac:DebitNoteLine> */
            debitNoteType.getDebitNoteLine().addAll(
                    getAllDebitNoteLines(
                            transaction.getTransaccionLineasList(),
                            transaction.getDOCMONCodigo()));
        } catch (UBLDocumentException e) {
            logger.error("generateDebitNoteType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateDebitNoteType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_344, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateDebitNoteType() [" + this.identifier + "]");
        }
        return debitNoteType;
    } // generateDebitNoteType

    public VoidedDocumentsType generateReversionDocumentType(
            Transaccion transaction, String signerName)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateVoidedDocumentType() [" + this.identifier
                    + "]");
        }
        VoidedDocumentsType voidedDocumentType = null;

        try {
            /*
             * Instanciar objeto VoidedDocumentsType para la comunicacion de
             * baja
             */
            voidedDocumentType = new VoidedDocumentsType();

            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateVoidedDocumentType() [" + this.identifier
                        + "] Agregando TAG para colocar la FIRMA.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();

            ublExtensions.getUBLExtension().add(getUBLExtensionSigner());
            voidedDocumentType.setUBLExtensions(ublExtensions);

            /* Agregar <VoidedDocuments><cbc:UBLVersionID> */
            voidedDocumentType.setUBLVersionID(getUBLVersionID());

            /* Agregar <VoidedDocuments><cbc:CustomizationID> */
            voidedDocumentType.setCustomizationID(getCustomizationID());

            /* Agregar <VoidedDocuments><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateVoidedDocumentType() [" + this.identifier
                        + "] Agregando Identificador: "
                        + transaction.getANTICIPOId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getANTICIPOId().replace("RA", "RR"));
            voidedDocumentType.setID(idDocIdentifier);

            /* Agregar <VoidedDocuments><cbc:ReferenceDate> */
            voidedDocumentType.setReferenceDate(getReferenceDate(transaction
                    .getDOCFechaEmision()));

            /* Agregar <VoidedDocuments><cbc:IssueDate> */
            voidedDocumentType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaVencimiento()));

            /* Agregar <VoidedDocuments><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            voidedDocumentType.getSignature().add(signature);

            /* Agregar <VoidedDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierPartyV2(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial());
            voidedDocumentType
                    .setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar item <VoidedDocuments><sac:VoidedDocumentsLine> */
            {
                /*
                 * Para este caso solo se agrega un solo ITEM, porque se esta
                 * dando de BAJA una transaccion.
                 */
                VoidedDocumentsLineType voidedDocumentLine = new VoidedDocumentsLineType();

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><cbc:LineID>
                 */
                LineIDType lineID = new LineIDType();
                lineID.setValue("1");
                voidedDocumentLine.setLineID(lineID);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><cbc:DocumentTypeCode
                 * >
                 */
                DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                documentTypeCode.setValue(transaction.getDOCCodigo());
                voidedDocumentLine.setDocumentTypeCode(documentTypeCode);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentSerialID
                 * >
                 */
                IdentifierType documentSerialID = new IdentifierType();
                documentSerialID.setValue(transaction.getDOCSerie());
                voidedDocumentLine.setDocumentSerialID(documentSerialID);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentNumberID
                 * >
                 */
                IdentifierType documentNumberID = new IdentifierType();
                documentNumberID.setValue(transaction.getDOCNumero());
                voidedDocumentLine.setDocumentNumberID(documentNumberID);

                /*
                 * Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:
                 * VoidReasonDescription>
                 */
                TextType voidReasonDescription = new TextType();
                voidReasonDescription.setValue(transaction.getFEComentario());
                voidedDocumentLine
                        .setVoidReasonDescription(voidReasonDescription);

                /*
                 * Agregar el ITEM del documento
                 */
                voidedDocumentType.getVoidedDocumentsLine().add(
                        voidedDocumentLine);
            }
        } catch (UBLDocumentException e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_345, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateVoidedDocumentType() [" + this.identifier
                    + "]");
        }
        return voidedDocumentType;
    } // generateVoidedDocumentType

    public VoidedDocumentsType generateVoidedDocumentType(
            Transaccion transaction, String signerName)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateVoidedDocumentType() [" + this.identifier
                    + "]");
        }
        VoidedDocumentsType voidedDocumentType = null;

        try {
            /*
             * Instanciar objeto VoidedDocumentsType para la comunicacion de
             * baja
             */
            voidedDocumentType = new VoidedDocumentsType();

            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateVoidedDocumentType() [" + this.identifier
                        + "] Agregando TAG para colocar la FIRMA.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();

            ublExtensions.getUBLExtension().add(getUBLExtensionSigner());
            voidedDocumentType.setUBLExtensions(ublExtensions);

            /* Agregar <VoidedDocuments><cbc:UBLVersionID> */
            voidedDocumentType.setUBLVersionID(getUBLVersionID());

            /* Agregar <VoidedDocuments><cbc:CustomizationID> */
            voidedDocumentType.setCustomizationID(getCustomizationID());

            /* Agregar <VoidedDocuments><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateVoidedDocumentType() [" + this.identifier
                        + "] Agregando Identificador: "
                        + transaction.getANTICIPOId());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getANTICIPOId());
            voidedDocumentType.setID(idDocIdentifier);

            /* Agregar <VoidedDocuments><cbc:ReferenceDate> */
            voidedDocumentType.setReferenceDate(getReferenceDate(transaction
                    .getDOCFechaEmision()));

            /* Agregar <VoidedDocuments><cbc:IssueDate> */
            voidedDocumentType.setIssueDate(getIssueDate(transaction
                    .getDOCFechaVencimiento()));

            /* Agregar <VoidedDocuments><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getDocIdentidadNro(),
                    transaction.getRazonSocial(), signerName);
            voidedDocumentType.getSignature().add(signature);

            /* Agregar <VoidedDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierPartyV2(
                    transaction.getDocIdentidadNro(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial());
            voidedDocumentType
                    .setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar item <VoidedDocuments><sac:VoidedDocumentsLine> */
            {
                /*
                 * Para este caso solo se agrega un solo ITEM, porque se esta
                 * dando de BAJA una transaccion.
                 */
                VoidedDocumentsLineType voidedDocumentLine = new VoidedDocumentsLineType();

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><cbc:LineID>
                 */
                LineIDType lineID = new LineIDType();
                lineID.setValue("1");
                voidedDocumentLine.setLineID(lineID);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><cbc:DocumentTypeCode
                 * >
                 */
                DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                documentTypeCode.setValue(transaction.getDOCCodigo());
                voidedDocumentLine.setDocumentTypeCode(documentTypeCode);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentSerialID
                 * >
                 */
                IdentifierType documentSerialID = new IdentifierType();
                documentSerialID.setValue(transaction.getDOCSerie());
                voidedDocumentLine.setDocumentSerialID(documentSerialID);

                /*
                 * Agregar
                 * <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentNumberID
                 * >
                 */
                IdentifierType documentNumberID = new IdentifierType();
                documentNumberID.setValue(transaction.getDOCNumero());
                voidedDocumentLine.setDocumentNumberID(documentNumberID);

                /*
                 * Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:
                 * VoidReasonDescription>
                 */
                TextType voidReasonDescription = new TextType();
                voidReasonDescription.setValue(transaction.getFEComentario());
                voidedDocumentLine
                        .setVoidReasonDescription(voidReasonDescription);

                /*
                 * Agregar el ITEM del documento
                 */
                voidedDocumentType.getVoidedDocumentsLine().add(
                        voidedDocumentLine);
            }
        } catch (UBLDocumentException e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_345, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateVoidedDocumentType() [" + this.identifier
                    + "]");
        }
        return voidedDocumentType;
    }

    public SummaryDocumentsType generateSummaryDocumentsType(
            TransaccionResumen transaction, String signerName)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateSummaryDocumentsType() [" + this.identifier
                    + "]");
        }
        SummaryDocumentsType summaryDocumentsType = null;

        try {
            /* Instanciar objeto SummaryDocumentsType para el resumen diario */
            summaryDocumentsType = new SummaryDocumentsType();

            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() ["
                        + this.identifier
                        + "] Agregando TAG para colocar la FIRMA.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();

            ublExtensions.getUBLExtension().add(getUBLExtensionSigner());
            summaryDocumentsType.setUBLExtensions(ublExtensions);

            /* Agregar <SummaryDocuments><cbc:UBLVersionID> */
            summaryDocumentsType.setUBLVersionID(getUBLVersionID());

            /* Agregar <SummaryDocuments><cbc:CustomizationID> */
            summaryDocumentsType.setCustomizationID(getCustomizationID());

            /* Agregar <SummaryDocuments><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() ["
                        + this.identifier + "] Agregando IdTransaccion: "
                        + transaction.getIdTransaccion());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getIdTransaccion());
            summaryDocumentsType.setID(idDocIdentifier);

            /* Agregar <SummaryDocuments><cbc:ReferenceDate> */
            summaryDocumentsType.setReferenceDate(getReferenceDate2(transaction
                    .getFechaEmision()));

            /* Agregar <SummaryDocuments><cbc:IssueDate> */
            summaryDocumentsType.setIssueDate(getIssueDate8(transaction
                    .getFechaGeneracion()));

            /* Agregar <SummaryDocuments><cac:Signature> */
            SignatureType signature = generateSignature(
                    transaction.getNumeroRuc(), transaction.getRazonSocial(),
                    signerName);
            summaryDocumentsType.getSignature().add(signature);

            /* Agregar <SummaryDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(
                    transaction.getNumeroRuc(),
                    transaction.getDocIdentidadTipo(),
                    transaction.getRazonSocial(),
                    transaction.getNombreComercial(),
                    transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(),
                    transaction.getDIRProvincia(),
                    transaction.getDIRDistrito(), transaction.getDIRUbigeo()
                    .toString(), transaction.getDIRPais(),
                    transaction.getPersonContacto(), transaction.getEMail());
            summaryDocumentsType
                    .setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <SummaryDocuments><sac:SummaryDocumentsLine> */
            summaryDocumentsType.getSummaryDocumentsLine().addAll(
                    getAllSummaryDocumentLines(transaction
                            .getTransaccionResumenLineaList()));
        } catch (UBLDocumentException e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_346, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateSummaryDocumentsType() [" + this.identifier
                    + "]");
        }
        return summaryDocumentsType;
    } // generateSummaryDocumentsType

    public UBLExtensionType getUBLExtensionSigner() {
        UBLExtensionType ublExtensionSigner = new UBLExtensionType();
        ExtensionContentType extensionContent = new ExtensionContentType();
        ublExtensionSigner.setExtensionContent(extensionContent);

        return ublExtensionSigner;
    } // getUBLExtensionSigner

    private UBLVersionIDType getUBLVersionID() {
        UBLVersionIDType ublVersionID = new UBLVersionIDType();
        ublVersionID.setValue(IUBLConfig.UBL_VERSION_ID);

        return ublVersionID;
    } // getUBLVersionID

    private CustomizationIDType getCustomizationID() {
        CustomizationIDType customizationID = new CustomizationIDType();
        customizationID.setValue(IUBLConfig.CUSTOMIZATION_ID);

        return customizationID;
    } // getCustomizationID

    private UBLExtensionType getUBLExtensionTotalAndProperty(
            List<TransaccionTotales> transactionTotalList,
            List<TransaccionPropiedades> transactionPropertyList,
            List<TransaccionGuias> transactionGuiaList,
            String sunatTransactionID) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] transactionTotalList: "
                    + transactionTotalList + " transactionPropertyList: "
                    + transactionPropertyList + " sunatTransactionID: "
                    + sunatTransactionID);
        }
        UBLExtensionType ublExtension = null;

        try {
            ublExtension = new UBLExtensionType();

            AdditionalInformationType additionalInformation = new AdditionalInformationType();

            if (null == transactionTotalList) {
                throw new UBLDocumentException(IVenturaError.ERROR_330);
            } else {
                if (StringUtils.isNotBlank(sunatTransactionID)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getUBLExtensionTotalAndProperty() ["
                                + this.identifier
                                + "] Existe valor SUNATTransaction.");
                    }
                    SUNATTransactionType sunatTransaction = new SUNATTransactionType();
                    IDType id = new IDType();
                    id.setValue(sunatTransactionID.trim());
                    sunatTransaction.setID(id);

                    additionalInformation.setSUNATTransaction(sunatTransaction);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("getUBLExtensionTotalAndProperty() ["
                            + this.identifier
                            + "] Agregando informacion de TOTALES.");
                }

                for (TransaccionTotales transactionTotal : transactionTotalList) {
                    AdditionalMonetaryTotalType additionalMonetaryTotal = new AdditionalMonetaryTotalType();

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation
                     * ><sac:AdditionalMonetaryTotal><cbc:ID>
                     */
                    IDType id = new IDType();
                    id.setValue(transactionTotal.getTransaccionTotalesPK()
                            .getId());

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation
                     * ><sac:AdditionalMonetaryTotal><cbc:PayableAmount>
                     */
                    PayableAmountType payableAmount = new PayableAmountType();
                    payableAmount
                            .setValue(transactionTotal
                                    .getMonto()
                                    .setScale(
                                            IUBLConfig.DECIMAL_ADDITIONAL_MONETARY_TOTAL_PAYABLE_AMOUNT,
                                            RoundingMode.HALF_UP));
                    payableAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(transactionTotal.getTransaccion()
                                    .getDOCMONCodigo()));

                    /* Agregar ID y PayableAmount al objeto */
                    additionalMonetaryTotal.setID(id);
                    additionalMonetaryTotal.setPayableAmount(payableAmount);

                    /* Agregar el objeto AdditionalMonetaryTotalType a la lista */
                    additionalInformation.getAdditionalMonetaryTotal().add(
                            additionalMonetaryTotal);
                }
            }

            if (null == transactionPropertyList) {
                throw new UBLDocumentException(IVenturaError.ERROR_331);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("getUBLExtensionTotalAndProperty() ["
                            + this.identifier
                            + "] Agregando informacion de PROPIEDADES.");
                }
                for (TransaccionPropiedades transactionProperty : transactionPropertyList) {
                    AdditionalPropertyType additionalProperty = new AdditionalPropertyType();

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation><sac:AdditionalProperty><cbc:ID>
                     */
                    IDType id = new IDType();
                    id.setValue(transactionProperty
                            .getTransaccionPropiedadesPK().getId());

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation><sac:AdditionalProperty><cbc:Value>
                     */
                    ValueType value = new ValueType();
                    value.setValue(transactionProperty.getValor());

                    /* Agregar ID y Value al objeto */
                    additionalProperty.setID(id);
                    additionalProperty.setValue(value);

                    /* Agregar el objeto AdditionalPropertyType a la lista */
                    additionalInformation.getAdditionalProperty().add(
                            additionalProperty);
                }

                for (TransaccionGuias transactionGuia : transactionGuiaList) {

//                    if (!transactionGuiaList.isEmpty()) {
//
//                        SUNATEmbededDespatchAdviceType adviceType = new SUNATEmbededDespatchAdviceType();
//
//                        adviceType.setDeliveryAddress(returnAddressType(transactionGuia.getDestinoUbigeo(), transactionGuia.getDestinoCalle(), transactionGuia.getDestinoDistrito(), transactionGuia.getDestinoProvincia(), transactionGuia.getDestinoDepartamento(), transactionGuia.getDestinoPais()));
//                        adviceType.setOriginAddress(returnAddressType(transactionGuia.getOrigenUbigeo(), transactionGuia.getOrigenCalle(), transactionGuia.getOrigenDistrito(), transactionGuia.getOrigenProvincia(), transactionGuia.getOrigenDepartamento(), transactionGuia.getOrigenPais()));
//                        adviceType.setSUNATCarrierParty(returnSUNATCarrierPartyType(transactionGuia.getTransportistaDocIdentidad(), transactionGuia.getTransportistaDocIdTipo(), transactionGuia.getTransportistaRazonSocial()));
//                        adviceType.getSUNATRoadTransport().add(returnSUNATRoadTransportType(transactionGuia.getVehiculoMatricula(), transactionGuia.getVehiculoAutorizacion(), transactionGuia.getVehiculo()));
//                        //adviceType.(returnTransportModeCodeType(transactionGuia.getCodigoTransporte()));
//                        //adviceType.setGrossWeightMeasure(returnGrossWeightMeasureType(transactionGuia.getPesoTransporte(), transactionGuia.getUnidad()));
//                        //adviceType.setDriverParty(returnDriverPartyType(transactionGuia.getConductorDocumentoId()));
//
//                        additionalInformation.setSUNATEmbededDespatchAdvice(adviceType);
//
//                    }

                }
            }

            /* Colocar la informacion en el TAG UBLExtension */
            ExtensionContentType extensionContent = new ExtensionContentType();
            extensionContent.setAny((org.w3c.dom.Element) getExtensionContentNode(additionalInformation));
            ublExtension.setExtensionContent(extensionContent);
        } catch (UBLDocumentException e) {
            logger.error("getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] UBLDocumentException - ERROR: "
                    + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_328.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_328);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "]");
        }
        return ublExtension;
    } // getUBLExtensionTotalAndProperty

    private UBLExtensionType getUBLExtensionTotalAndPropertyForNCND(
            List<TransaccionTotales> transactionTotalList,
            List<TransaccionPropiedades> transactionPropertyList,
            List<TransaccionGuias> transactionGuiaList,
            String sunatTransactionID) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] transactionTotalList: "
                    + transactionTotalList + " transactionPropertyList: "
                    + transactionPropertyList + " sunatTransactionID: "
                    + sunatTransactionID);
        }
        UBLExtensionType ublExtension = null;

        try {
            ublExtension = new UBLExtensionType();

            AdditionalInformationType additionalInformation = new AdditionalInformationType();

            if (null == transactionTotalList) {
                throw new UBLDocumentException(IVenturaError.ERROR_330);
            } else {
                if (StringUtils.isNotBlank(sunatTransactionID)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getUBLExtensionTotalAndProperty() ["
                                + this.identifier
                                + "] Existe valor SUNATTransaction.");
                    }
                    SUNATTransactionType sunatTransaction = new SUNATTransactionType();
                    IDType id = new IDType();
                    id.setValue(sunatTransactionID.trim());
                    sunatTransaction.setID(id);

                    additionalInformation.setSUNATTransaction(sunatTransaction);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("getUBLExtensionTotalAndProperty() ["
                            + this.identifier
                            + "] Agregando informacion de TOTALES.");
                }

                for (TransaccionTotales transactionTotal : transactionTotalList) {
                    AdditionalMonetaryTotalType additionalMonetaryTotal = new AdditionalMonetaryTotalType();

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation
                     * ><sac:AdditionalMonetaryTotal><cbc:ID>
                     */
                    if (!transactionTotal.getTransaccionTotalesPK().getId().equalsIgnoreCase(IUBLConfig.ADDITIONAL_MONETARY_1005)) {

                        IDType id = new IDType();
                        id.setValue(transactionTotal.getTransaccionTotalesPK()
                                .getId());

                        /*
                         * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                         * AdditionalInformation
                         * ><sac:AdditionalMonetaryTotal><cbc:PayableAmount>
                         */
                        PayableAmountType payableAmount = new PayableAmountType();
                        payableAmount
                                .setValue(transactionTotal
                                        .getMonto()
                                        .setScale(
                                                IUBLConfig.DECIMAL_ADDITIONAL_MONETARY_TOTAL_PAYABLE_AMOUNT,
                                                RoundingMode.HALF_UP));
                        payableAmount.setCurrencyID(CurrencyCodeContentType
                                .valueOf(transactionTotal.getTransaccion()
                                        .getDOCMONCodigo()));

                        /* Agregar ID y PayableAmount al objeto */
                        additionalMonetaryTotal.setID(id);
                        additionalMonetaryTotal.setPayableAmount(payableAmount);

                        /* Agregar el objeto AdditionalMonetaryTotalType a la lista */
                        additionalInformation.getAdditionalMonetaryTotal().add(
                                additionalMonetaryTotal);

                    }

                }
            }

            if (null == transactionPropertyList) {
                throw new UBLDocumentException(IVenturaError.ERROR_331);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("getUBLExtensionTotalAndProperty() ["
                            + this.identifier
                            + "] Agregando informacion de PROPIEDADES.");
                }
                for (TransaccionPropiedades transactionProperty : transactionPropertyList) {
                    AdditionalPropertyType additionalProperty = new AdditionalPropertyType();

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation><sac:AdditionalProperty><cbc:ID>
                     */
                    IDType id = new IDType();
                    id.setValue(transactionProperty
                            .getTransaccionPropiedadesPK().getId());

                    /*
                     * Agregar <ext:UBLExtension><ext:ExtensionContent><sac:
                     * AdditionalInformation><sac:AdditionalProperty><cbc:Value>
                     */
                    ValueType value = new ValueType();
                    value.setValue(transactionProperty.getValor());

                    /* Agregar ID y Value al objeto */
                    additionalProperty.setID(id);
                    additionalProperty.setValue(value);

                    /* Agregar el objeto AdditionalPropertyType a la lista */
                    additionalInformation.getAdditionalProperty().add(
                            additionalProperty);
                }

//                for (TransaccionGuias transactionGuia : transactionGuiaList) {
//
//                    if (!transactionGuiaList.isEmpty()) {
//
//                        SUNATEmbededDespatchAdviceType adviceType = new SUNATEmbededDespatchAdviceType();
//
//                        adviceType.setDeliveryAddress(returnAddressType(transactionGuia.getDestinoUbigeo(), transactionGuia.getDestinoCalle(), transactionGuia.getDestinoDistrito(), transactionGuia.getDestinoProvincia(), transactionGuia.getDestinoDepartamento(), transactionGuia.getDestinoPais()));
//                        adviceType.setOriginAddress(returnAddressType(transactionGuia.getOrigenUbigeo(), transactionGuia.getOrigenCalle(), transactionGuia.getOrigenDistrito(), transactionGuia.getOrigenProvincia(), transactionGuia.getOrigenDepartamento(), transactionGuia.getOrigenPais()));
//                        adviceType.setSUNATCarrierParty(returnSUNATCarrierPartyType(transactionGuia.getTransportistaDocIdentidad(), transactionGuia.getTransportistaDocIdTipo(), transactionGuia.getTransportistaRazonSocial()));
//                        adviceType.getSUNATRoadTransport().add(returnSUNATRoadTransportType(transactionGuia.getVehiculoMatricula(), transactionGuia.getVehiculoAutorizacion(), transactionGuia.getVehiculo()));
//                        //adviceType.(returnTransportModeCodeType(transactionGuia.getCodigoTransporte()));
//                        //adviceType.setGrossWeightMeasure(returnGrossWeightMeasureType(transactionGuia.getPesoTransporte(), transactionGuia.getUnidad()));
//                        //adviceType.setDriverParty(returnDriverPartyType(transactionGuia.getConductorDocumentoId()));
//
//                        additionalInformation.setSUNATEmbededDespatchAdvice(adviceType);
//
//                    }
//
//                }
            }

            /* Colocar la informacion en el TAG UBLExtension */
            ExtensionContentType extensionContent = new ExtensionContentType();
            extensionContent.setAny((org.w3c.dom.Element) getExtensionContentNode(additionalInformation));
            ublExtension.setExtensionContent(extensionContent);
        } catch (UBLDocumentException e) {
            logger.error("getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] UBLDocumentException - ERROR: "
                    + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_328.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_328);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getUBLExtensionTotalAndProperty() ["
                    + this.identifier + "]");
        }
        return ublExtension;
    } // getUBLExtensionTotalAndProperty

    private GrossWeightMeasureType returnGrossWeightMeasureType(String peso, String unidad) {

        GrossWeightMeasureType gvmt = new GrossWeightMeasureType();
        gvmt.setUnitCode(unidad);
        BigDecimal bd = new BigDecimal(peso);
        gvmt.setValue(bd);

        return gvmt;

    }

    private DriverPartyType returnDriverPartyType(String identificacion) {

        DriverPartyType dpt = new DriverPartyType();

        PartyType pt = new PartyType();

        PartyIdentificationType pit = new PartyIdentificationType();

        IDType idt = new IDType();
        idt.setValue(identificacion);

        pit.setID(idt);

        List<PartyIdentificationType> listIdentification = new ArrayList<>();
        listIdentification.add(pit);

        pt.setPartyIdentification(listIdentification);

        dpt.setParty(pt);

        return dpt;

    }

    private TransportModeCodeType returnTransportModeCodeType(String value) {

        TransportModeCodeType tmct = new TransportModeCodeType();
        tmct.setValue(value);

        return tmct;

    }

    private SUNATRoadTransportType returnSUNATRoadTransportType(String licencia, String autorizacion, String marca) {

        SUNATRoadTransportType sunatrtt = new SUNATRoadTransportType();

        LicensePlateIDType lpidt = new LicensePlateIDType();
        lpidt.setValue(licencia);

        TransportAuthorizationCodeType tact = new TransportAuthorizationCodeType();
        tact.setValue(autorizacion);

        BrandNameType bnt = new BrandNameType();
        bnt.setValue(marca);

        sunatrtt.setLicensePlateID(lpidt);
        sunatrtt.setTransportAuthorizationCode(tact);
        sunatrtt.setBrandName(bnt);

        return sunatrtt;

    }

    private SUNATCarrierPartyType returnSUNATCarrierPartyType(String documIden, String documentTipoId, String name) {

        SUNATCarrierPartyType sunatcpt = new SUNATCarrierPartyType();

        CustomerAssignedAccountIDType caaidt = new CustomerAssignedAccountIDType();
        caaidt.setValue(documIden);

        AdditionalAccountIDType aaidt = new AdditionalAccountIDType();
        aaidt.setValue(documentTipoId);

        PartyLegalEntityType plet = new PartyLegalEntityType();

        RegistrationNameType rnt = new RegistrationNameType();
        rnt.setValue(name);

        plet.setRegistrationName(rnt);

        List<PartyLegalEntityType> lstPartyLegalEntityType = new ArrayList<>();
        lstPartyLegalEntityType.add(plet);

        PartyType pt = new PartyType();
        pt.setPartyLegalEntity(lstPartyLegalEntityType);

        sunatcpt.setCustomerAssignedAccountID(caaidt);
        sunatcpt.getAdditionalAccountID().add(aaidt);
        sunatcpt.setParty(pt);

        return sunatcpt;
    }

    private AddressType returnAddressType(String id, String streetName, String distrito, String provincia, String departamento, String pais) {

        AddressType at = new AddressType();

        IDType idx = new IDType();
        idx.setValue(id);

        StreetNameType streetNameType = new StreetNameType();
        streetNameType.setValue(streetName);

        CitySubdivisionNameType csnt = new CitySubdivisionNameType();
        csnt.setValue("Urb. Los Laureles");

        CityNameType cnt = new CityNameType();
        cnt.setValue(provincia);

        CountrySubentityType cst = new CountrySubentityType();
        cst.setValue(departamento);

        DistrictType dt = new DistrictType();
        dt.setValue(distrito);

        IdentificationCodeType ict = new IdentificationCodeType();
        ict.setValue(pais);

        CountryType ct = new CountryType();
        ct.setIdentificationCode(ict);

        at.setID(idx);
        at.setStreetName(streetNameType);
        at.setCitySubdivisionName(csnt);
        at.setCityName(cnt);
        at.setCountrySubentity(cst);
        at.setDistrict(dt);
        at.setCountry(ct);

        return at;

    }

    private org.w3c.dom.Node getExtensionContentNode(
            Object additionalInformationObj) throws UBLDocumentException {
        org.w3c.dom.Node node = null;

        try {
            DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
            Document document = docBuilder.newDocument();

            /* Generando proceso JAXB */
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(additionalInformationObj.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(additionalInformationObj, document);

            /* Extrayendo el NODO */
            node = document.getFirstChild();
        } catch (Exception e) {
            logger.error("getExtensionContentNode() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_329.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_329);
        }
        return node;
    } // getExtensionContentNode

    private UUIDType getUUID(String docUUID) {
        UUIDType uuid = new UUIDType();
        uuid.setValue(docUUID);

        return uuid;
    } // getUUID

    private InvoiceTypeCodeType getInvoiceTypeCode(String value) {
        InvoiceTypeCodeType invoiceTypeCode = new InvoiceTypeCodeType();
        invoiceTypeCode.setValue(value);

        return invoiceTypeCode;
    } // getInvoiceTypeCode

    private DespatchAdviceTypeCodeType getDespatchAdviceTypeCode(String value) {
        DespatchAdviceTypeCodeType despatchAdviceTypeCodeType = new DespatchAdviceTypeCodeType();
        despatchAdviceTypeCodeType.setValue(value);

        return despatchAdviceTypeCodeType;
    } // getInvoiceTypeCode

    private DocumentCurrencyCodeType getDocumentCurrencyCode(
            String currencyName, String currencyCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getDocumentCurrencyCode() currencyName: "
                    + currencyName + " currencyCode: " + currencyCode);
        }
        DocumentCurrencyCodeType documentCurrencyCode = new DocumentCurrencyCodeType();
        documentCurrencyCode.setName(currencyName);
        documentCurrencyCode.setValue(currencyCode);

        return documentCurrencyCode;
    } // getDocumentCurrencyCode

    private IssueDateType getIssueDate(Date issueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        IssueDateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new IssueDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private IssueDateType getIssueDate8(String issueDateValue_1)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date issueDateValue = null;
        try {
            issueDateValue = formatter.parse(issueDateValue_1);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        IssueDateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new IssueDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private DateType getIssueDate4(Date issueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        DateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new DateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private PaidDateType getIssueDate2(Date issueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        PaidDateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new PaidDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private SUNATPerceptionDateType getIssueDate3(Date issueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        SUNATPerceptionDateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new SUNATPerceptionDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private SUNATRetentionDateType getIssueDate5(Date issueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        SUNATRetentionDateType issueDateType = null;

        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new SUNATRetentionDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private ReferenceDateType getReferenceDate2(String referenceDateValue_1)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getReferenceDate() [" + this.identifier + "]");
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date referenceDateValue = null;
        try {
            referenceDateValue = formatter.parse(referenceDateValue_1);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        ReferenceDateType referenceDateType = null;

        try {
            if (null == referenceDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_340);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.REFERENCEDATE_FORMAT);
            String date = sdf.format(referenceDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            referenceDateType = new ReferenceDateType();
            referenceDateType.setValue(datatypeFact
                    .newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getReferenceDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getReferenceDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_347);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getReferenceDate() [" + this.identifier + "]");
        }
        return referenceDateType;
    } // getReferenceDate

    private ReferenceDateType getReferenceDate(Date referenceDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getReferenceDate() [" + this.identifier + "]");
        }

        ReferenceDateType referenceDateType = null;

        try {
            if (null == referenceDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_340);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.REFERENCEDATE_FORMAT);
            String date = sdf.format(referenceDateValue);

            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();

            /* Agregando la fecha de emision <cbc:IssueDate> */
            referenceDateType = new ReferenceDateType();
            referenceDateType.setValue(datatypeFact
                    .newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getReferenceDate() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getReferenceDate() [" + this.identifier + "] "
                    + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_347);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getReferenceDate() [" + this.identifier + "]");
        }
        return referenceDateType;
    } // getReferenceDate

    private String getDueDateValue(Date dueDateValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getDueDateValue() [" + this.identifier + "]");
        }
        String dueDate = null;

        try {
            if (null == dueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_314);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(
                    IUBLConfig.DUEDATE_FORMAT);
            dueDate = sdf.format(dueDateValue);
        } catch (UBLDocumentException e) {
            logger.error("getDueDateValue() UBLDocumentException - ERROR: "
                    + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getDueDateValue() [" + this.identifier + "] "
                    + IVenturaError.ERROR_315.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_315);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getDueDateValue() [" + this.identifier + "]");
        }
        return dueDate;
    } // getDueDateValue

    private List<PaymentType> generatePrepaidPaymentV2(
            List<TransaccionAnticipo> lstAnticipo) throws UBLDocumentException {

        List<PaymentType> prepaidPayment_2 = new ArrayList<PaymentType>();

        if (lstAnticipo != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("+generatePrepaidPayment() [" + this.identifier
                        + "]");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("+generatePrepaidPayment() [" + lstAnticipo.size()
                        + "]");
            }

            for (int i = 0; i < lstAnticipo.size(); i++) {

                PaymentType prepaidPayment = null;

                try {
                    /* Agregar <Invoice><cac:PrepaidPayment><cbc:PaidAmount> */
                    PaidAmountType paidAmount = new PaidAmountType();
                    paidAmount.setValue(lstAnticipo
                            .get(i)
                            .getAnticipoMonto()
                            .setScale(
                                    IUBLConfig.DECIMAL_PREPAID_PAYMENT_AMOUNT,
                                    RoundingMode.HALF_UP));
                    paidAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(lstAnticipo.get(i).getDOCMoneda()));

                    if (logger.isDebugEnabled()) {
                        logger.debug("+generatePrepaidPayment() ["
                                + lstAnticipo.get(i).getAnticipoMonto()
                                + lstAnticipo.get(i).getDOCMoneda() + "]");
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("+cac:PrepaidPayment() ["
                                + this.identifier + "]");
                    }

                    /* Agregar <Invoice><cac:PrepaidPayment><cbc:ID> */
                    IDType id = new IDType();
                    id.setValue(lstAnticipo.get(i).getAntiDOCSerieCorrelativo());
                    id.setSchemeID(lstAnticipo.get(i).getAntiDOCTipo());

                    if (logger.isDebugEnabled()) {
                        logger.debug("+generatePrepaidPayment() ["
                                + lstAnticipo.get(i)
                                .getAntiDOCSerieCorrelativo()
                                + lstAnticipo.get(i).getAntiDOCTipo() + "]");
                    }

                    /* Agregar <Invoice><cac:PrepaidPayment><cbc:InstructionID> */
                    InstructionIDType instructionID = new InstructionIDType();
                    instructionID.setValue(lstAnticipo.get(i).getDOCNumero());
                    instructionID.setSchemeID(lstAnticipo.get(i).getDOCTipo());

                    if (logger.isDebugEnabled()) {
                        logger.debug("+generatePrepaidPayment() ["
                                + lstAnticipo.get(i).getDOCNumero()
                                + lstAnticipo.get(i).getDOCTipo() + "]");
                    }

                    /*
                     * Agregar los tag's
                     */
                    prepaidPayment = new PaymentType();

                    prepaidPayment.setPaidAmount(paidAmount);
                    prepaidPayment.setID(id);
                    prepaidPayment.setInstructionID(instructionID);
                    prepaidPayment_2.add(prepaidPayment);

                } catch (Exception e) {
                    logger.error("generatePrepaidPayment() [" + this.identifier
                            + "] Exception(" + e.getClass().getName()
                            + ") - ERROR: " + e.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_336);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("-generatePrepaidPayment() ["
                            + this.identifier + "]");
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("-Genero correctamente el tag<cac:PrepaidPayment>  ["
                            + this.identifier + "]");
                }

            }

        }
        return prepaidPayment_2;
    } // generatePrepaidPayment

    private List<TaxTotalType> getAllTaxTotal(
            List<TransaccionImpuestos> taxTotalTransactions)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllTaxTotal() [" + this.identifier
                    + "] taxTotalTransactions: " + taxTotalTransactions);
        }
        List<TaxTotalType> taxTotalList = null;

        if (null != taxTotalTransactions && !taxTotalTransactions.isEmpty()) {
            try {
                taxTotalList = new ArrayList<TaxTotalType>();

                for (TransaccionImpuestos taxTotalTrans : taxTotalTransactions) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllTaxTotal() [" + this.identifier
                                + "] " + "\nTipoTributo: "
                                + taxTotalTrans.getTipoTributo() + "\tMonto: "
                                + taxTotalTrans.getMonto() + "\tPorcentaje: "
                                + taxTotalTrans.getPorcentaje() + "\nMoneda: "
                                + taxTotalTrans.getMoneda() + "\nTierRange: "
                                + taxTotalTrans.getTierRange()
                                + "\tTransaccion: "
                                + taxTotalTrans.getTransaccion());
                    }

                    TaxTotalType taxTotalType = new TaxTotalType();

                    /*
                     * Creando TaxAmountType
                     * <Invoice><cac:TaxTotal><cbc:TaxAmount>
                     * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount>
                     */
                    TaxAmountType taxAmountType = new TaxAmountType();
                    taxAmountType.setValue(taxTotalTrans.getMonto().setScale(
                            IUBLConfig.DECIMAL_TAX_TOTAL_AMOUNT,
                            RoundingMode.HALF_UP));
                    taxAmountType.setCurrencyID(CurrencyCodeContentType.valueOf(taxTotalTrans.getMoneda()));

                    taxTotalType.setTaxAmount(taxAmountType);

                    if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_IGV_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /* <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:Percent> */
                        PercentType percentType = new PercentType();
                        percentType
                                .setValue(taxTotalTrans
                                        .getPorcentaje()
                                        .setScale(
                                                IUBLConfig.DECIMAL_TAX_TOTAL_IGV_PERCENT,
                                                RoundingMode.HALF_UP));
                        taxSubtotalType.setPercent(percentType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_IGV_NAME,
                                    IUBLConfig.TAX_TOTAL_IGV_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_ISC_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_ISC_NAME,
                                    IUBLConfig.TAX_TOTAL_ISC_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_EXP_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_EXP_NAME,
                                    IUBLConfig.TAX_TOTAL_FRE_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_GRT_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_GRT_NAME,
                                    IUBLConfig.TAX_TOTAL_FRE_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_EXO_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_EXO_NAME,
                                    IUBLConfig.TAX_TOTAL_VAT_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_INA_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_INA_NAME,
                                    IUBLConfig.TAX_TOTAL_FRE_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTrans.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_OTH_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount
                         * >
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory
                         * >
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * <Invoice><cac:TaxTotal><cac:TaxSubtotal><cac:
                             * TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTrans.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_OTH_NAME,
                                    IUBLConfig.TAX_TOTAL_OTH_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else {
                        logger.error("getAllTaxTotal() [" + this.identifier
                                + "] " + IVenturaError.ERROR_318.getMessage());
                        throw new UBLDocumentException(IVenturaError.ERROR_318);
                    }

                    /* Agregando el impuesto a la lista */
                    taxTotalList.add(taxTotalType);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllTaxTotal() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllTaxTotal() [" + this.identifier + "] "
                        + IVenturaError.ERROR_317.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_317);
            }
        } else {
            logger.error("getAllTaxTotal() [" + this.identifier + "] "
                    + IVenturaError.ERROR_316.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_316);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllTaxTotal() [" + this.identifier + "]");
        }
        return taxTotalList;
    } // getAllTaxTotal

    private List<TaxTotalType> getAllTaxTotalLines(
            List<TransaccionLineaImpuestos> taxTotalTransactionLines)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllTaxTotalLines() [" + this.identifier + "]");
        }
        List<TaxTotalType> taxTotalLineList = null;

        if (null != taxTotalTransactionLines
                && !taxTotalTransactionLines.isEmpty()) {
            try {
                taxTotalLineList = new ArrayList<TaxTotalType>();

                for (TransaccionLineaImpuestos taxTotalTransLine : taxTotalTransactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllTaxTotalLines() ["
                                + this.identifier
                                + "] LineId: "
                                + taxTotalTransLine
                                .getTransaccionLineaImpuestosPK()
                                .getLineId()
                                + " NroOrden: "
                                + taxTotalTransLine
                                .getTransaccionLineaImpuestosPK()
                                .getNroOrden() + "\nTipoTributo: "
                                + taxTotalTransLine.getTipoTributo()
                                + "\tMonto: " + taxTotalTransLine.getMonto()
                                + "\tPorcentaje: "
                                + taxTotalTransLine.getPorcentaje()
                                + "\nMoneda: " + taxTotalTransLine.getMoneda()
                                + "\nTierRange: "
                                + taxTotalTransLine.getTierRange()
                                + "\tTransaccionLineas: "
                                + taxTotalTransLine.getTransaccionLineas());
                    }

                    TaxTotalType taxTotalType = new TaxTotalType();

                    /*
                     * Creando TaxAmountType
                     * <Invoice><cac:InvoiceLine><cac:TaxTotal><cbc:TaxAmount>
                     * <Invoice
                     * ><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal><
                     * cbc:TaxAmount>
                     */
                    TaxAmountType taxAmountType = new TaxAmountType();
                    taxAmountType.setValue(taxTotalTransLine.getMonto()
                            .setScale(IUBLConfig.DECIMAL_LINE_TAX_AMOUNT,
                                    RoundingMode.HALF_UP));
                    taxAmountType.setCurrencyID(CurrencyCodeContentType
                            .valueOf(taxTotalTransLine.getMoneda()));

                    taxTotalType.setTaxAmount(taxAmountType);

                    if (taxTotalTransLine.getTipoTributo().equalsIgnoreCase(
                            IUBLConfig.TAX_TOTAL_IGV_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cbc:TaxAmount>
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /* Agregar <Invoice><cac:InvoiceLine> */
                        PercentType percent = new PercentType();
                        percent.setValue(taxTotalTransLine
                                .getPorcentaje()
                                .setScale(
                                        IUBLConfig.DECIMAL_LINE_TAX_IGV_PERCENT,
                                        RoundingMode.HALF_UP));
                        taxSubtotalType.setPercent(percent);

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cac:TaxCategory>
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * Agregar
                             * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac
                             * :TaxSubtotal
                             * ><cac:TaxCategory><cbc:TaxExemptionReasonCode>
                             */
                            TaxExemptionReasonCodeType taxExemptionReasonCode = new TaxExemptionReasonCodeType();
                            taxExemptionReasonCode.setValue(taxTotalTransLine
                                    .getTipoAfectacion());
                            taxCategoryType
                                    .setTaxExemptionReasonCode(taxExemptionReasonCode);

                            /*
                             * Agregar
                             * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac
                             * :TaxSubtotal><cac:TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTransLine.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_IGV_NAME,
                                    IUBLConfig.TAX_TOTAL_IGV_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTransLine.getTipoTributo()
                            .equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cbc:TaxAmount>
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cac:TaxCategory>
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * Agregar
                             * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac
                             * :TaxSubtotal><cac:TaxCategory><cbc:TierRange>
                             */
                            TierRangeType tierRange = new TierRangeType();
                            tierRange
                                    .setValue(taxTotalTransLine.getTierRange());
                            taxCategoryType.setTierRange(tierRange);

                            /*
                             * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:
                             * TaxSubtotal><cac:TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTransLine.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_ISC_NAME,
                                    IUBLConfig.TAX_TOTAL_ISC_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else if (taxTotalTransLine.getTipoTributo()
                            .equalsIgnoreCase(IUBLConfig.TAX_TOTAL_OTH_ID)) {
                        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cbc:TaxAmount>
                         */
                        taxSubtotalType.setTaxAmount(taxAmountType);

                        /*
                         * Agregar
                         * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac:TaxSubtotal
                         * ><cac:TaxCategory>
                         */
                        TaxCategoryType taxCategoryType = new TaxCategoryType();
                        {
                            /*
                             * Agregar
                             * <Invoice><cac:InvoiceLine><cac:TaxTotal><cac
                             * :TaxSubtotal><cac:TaxCategory><cac:TaxScheme>
                             */
                            taxCategoryType.setTaxScheme(getTaxScheme(
                                    taxTotalTransLine.getTipoTributo(),
                                    IUBLConfig.TAX_TOTAL_OTH_NAME,
                                    IUBLConfig.TAX_TOTAL_OTH_CODE));

                            taxSubtotalType.setTaxCategory(taxCategoryType);
                        }

                        /* Agregando TaxSubtotal de tipo IGV */
                        taxTotalType.getTaxSubtotal().add(taxSubtotalType);
                    } else {
                        logger.error("getAllTaxTotal() [" + this.identifier
                                + "] " + IVenturaError.ERROR_318.getMessage());
                        throw new UBLDocumentException(IVenturaError.ERROR_324);
                    }

                    /* Agregando el impuesto de item a la lista */
                    taxTotalLineList.add(taxTotalType);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllTaxTotalLines() UBLDocumentException - ERROR: "
                        + e.getError().getId()
                        + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllTaxTotalLines() [" + this.identifier + "] "
                        + IVenturaError.ERROR_323.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_323);
            }
        } else {
            logger.error("getAllTaxTotalLines() [" + this.identifier + "] "
                    + IVenturaError.ERROR_322.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_322);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllTaxTotalLines() [" + this.identifier + "]");
        }
        return taxTotalLineList;
    } // getAllTaxTotalLines

    private TaxSchemeType getTaxScheme(String taxTotalID, String taxTotalName,
            String taxTotalCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getTaxScheme() taxTotalID: " + taxTotalID
                    + " taxTotalName: " + taxTotalName + " taxTotalCode: "
                    + taxTotalCode);
        }
        TaxSchemeType taxScheme = new TaxSchemeType();

        IDType id = new IDType();
        id.setValue(taxTotalID);

        NameType name = new NameType();
        name.setValue(taxTotalName);

        TaxTypeCodeType taxTypeCode = new TaxTypeCodeType();
        taxTypeCode.setValue(taxTotalCode);

        /* Agregando los tag's */
        taxScheme.setID(id);
        taxScheme.setName(name);
        taxScheme.setTaxTypeCode(taxTypeCode);

        return taxScheme;
    } // getTaxScheme

    private MonetaryTotalType getMonetaryTotal(BigDecimal amountValue,
            BigDecimal payableAmountVal, BigDecimal allowanceTotalAmountVal,
            BigDecimal prepaidTotalAmount, String currencyCode)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getLegalMonetaryTotal() [" + this.identifier + "]");
        }
        MonetaryTotalType legalMonetaryTotal = null;
        if (null != amountValue) {
            legalMonetaryTotal = new MonetaryTotalType();

            LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
            lineExtensionAmount.setValue(amountValue.setScale(
                    IUBLConfig.DECIMAL_LEGAL_MONERARY_LINEEXTENSION,
                    RoundingMode.HALF_UP));
            lineExtensionAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            /* Agregar el IMPORTE */
            legalMonetaryTotal.setLineExtensionAmount(lineExtensionAmount);
        } else {
            throw new UBLDocumentException(IVenturaError.ERROR_335);
        }

        if (null != payableAmountVal) {
            PayableAmountType payableAmount = new PayableAmountType();
            payableAmount.setValue(payableAmountVal.setScale(
                    IUBLConfig.DECIMAL_LEGAL_MONETARY_PAYABLE_AMOUNT,
                    RoundingMode.HALF_UP));
            payableAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            /* Agregar el IMPORTE TOTAL */
            legalMonetaryTotal.setPayableAmount(payableAmount);
        } else {
            throw new UBLDocumentException(IVenturaError.ERROR_321);
        }

        if (null != allowanceTotalAmountVal
                && allowanceTotalAmountVal.compareTo(BigDecimal.ZERO) == 1) {
            if (logger.isInfoEnabled()) {
                logger.info("getLegalMonetaryTotal() [" + this.identifier
                        + "] La transaccion tiene un DESCUENTO GLOBAL.");
            }
            AllowanceTotalAmountType allowanceTotalAmount = new AllowanceTotalAmountType();
            allowanceTotalAmount.setValue(allowanceTotalAmountVal.setScale(
                    IUBLConfig.DECIMAL_LEGAL_MONETARY_ALLOWANCE_TOTAL_AMOUNT,
                    RoundingMode.HALF_UP));
            allowanceTotalAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            /* Agregar el DESCUENTO TOTAL */
            legalMonetaryTotal.setAllowanceTotalAmount(allowanceTotalAmount);
        }

        if (null != prepaidTotalAmount
                && prepaidTotalAmount.compareTo(BigDecimal.ZERO) == 1) {
            if (logger.isInfoEnabled()) {
                logger.info("getLegalMonetaryTotal() [" + this.identifier
                        + "] La transaccion tiene un TOTAL DE ANTICIPOS.");
            }
            PrepaidAmountType prepaidAmount = new PrepaidAmountType();
            prepaidAmount.setValue(prepaidTotalAmount.setScale(
                    IUBLConfig.DECIMAL_LEGAL_MONETARY_TOTAL_PREPAID,
                    RoundingMode.HALF_UP));
            prepaidAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            /* Agregar el TOTAL DE ANTICIPOS */
            legalMonetaryTotal.setPrepaidAmount(prepaidAmount);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getLegalMonetaryTotal() [" + this.identifier + "]");
        }
        return legalMonetaryTotal;
    } // getLegalMonetaryTotal

    private List<InvoiceLineType> getAllInvoiceLines(
            List<TransaccionLineas> transactionLines, String currencyCode)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllInvoiceLines() [" + this.identifier
                    + "] transactionLines: " + transactionLines
                    + " currencyCode: " + currencyCode);
        }
        List<InvoiceLineType> invoiceLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                invoiceLineList = new ArrayList<InvoiceLineType>(
                        transactionLines.size());

                for (TransaccionLineas transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() ["
                                + this.identifier
                                + "] NroOrden: "
                                + transLine.getTransaccionLineasPK()
                                .getNroOrden() + "\nCantidad: "
                                + transLine.getCantidad() + "\tUnidad: "
                                + transLine.getUnidad() + "\tUnidadSunat: "
                                + transLine.getUnidadSunat()
                                + "\tTotalLineaSinIGV: "
                                + transLine.getTotalLineaSinIGV()
                                + "\nPrecioRefCodigo: "
                                + transLine.getPrecioRefCodigo()
                                + "\tPrecioIGV: " + transLine.getPrecioIGV()
                                + "\tPrecioRefMonto: "
                                + transLine.getPrecioRefMonto()
                                + "\nDCTOMonto: " + transLine.getDSCTOMonto()
                                + "\tDescripcion: "
                                + transLine.getDescripcion()
                                + "\tCodArticulo: "
                                + transLine.getCodArticulo());
                    }

                    InvoiceLineType invoiceLine = new InvoiceLineType();

                    /* Agregar <Invoice><cac:InvoiceLine><cbc:ID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando el ID.");
                    }
                    IDType idNumber = new IDType();
                    idNumber.setValue(String.valueOf(transLine
                            .getTransaccionLineasPK().getNroOrden()));
                    invoiceLine.setID(idNumber);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun SUNAT
                     * <Invoice><cac:InvoiceLine><cbc:InvoicedQuantity>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando CANTIDAD y UNIDAD SUNAT.");
                    }
                    InvoicedQuantityType invoicedQuantity = new InvoicedQuantityType();
                    invoicedQuantity.setValue(transLine.getCantidad().setScale(
                            IUBLConfig.DECIMAL_LINE_QUANTITY,
                            RoundingMode.HALF_UP));
                    invoicedQuantity.setUnitCode(transLine.getUnidadSunat());
                    invoiceLine.setInvoicedQuantity(invoicedQuantity);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun VENTURA
                     * <Invoice><cac:InvoiceLine><cbc:Note>
                     */
                    if (StringUtils.isNotBlank(transLine.getUnidad())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllInvoiceLines() ["
                                    + this.identifier
                                    + "] Agregando UNIDAD de VENTURA.");
                        }
                        NoteType note = new NoteType();
                        note.setValue(transLine.getUnidad());
                        invoiceLine.setNote(note);
                    }

                    /*
                     * Agregar
                     * <Invoice><cac:InvoiceLine><cbc:LineExtensionAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando TOTAL_LINEAS_SIN_IGV.");
                    }
                    LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
                    lineExtensionAmount.setValue(transLine
                            .getTotalLineaSinIGV().setScale(
                                    IUBLConfig.DECIMAL_LINE_EXTENSION_AMOUNT,
                                    RoundingMode.HALF_UP));
                    lineExtensionAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(currencyCode));
                    invoiceLine.setLineExtensionAmount(lineExtensionAmount);

                    /*
                     * - Op. Onerosa : tiene precio unitario - Op. No Onerosa :
                     * tiene valor referencial.
                     * 
                     * <Invoice><cac:InvoiceLine><cac:PricingReference>
                     */
                    if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllInvoiceLines() ["
                                    + this.identifier
                                    + "] PricingReference: PRECIO_UNITARIO");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioIGV()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_UNIT_PRICE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        invoiceLine.setPricingReference(pricingReference);
                    } else if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllInvoiceLines() ["
                                    + this.identifier
                                    + "] PricingReference: VALOR_REFERENCIAL");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();

                        /*
                         * El formato del precio unitario ser� a 2 decimales en
                         * esta opcion porque se entiende que debe ser un valor
                         * 0.00
                         */
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine.getPrecioIGV().setScale(2,
                                                        RoundingMode.HALF_UP),
                                                currencyCode,
                                                IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE));
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioRefMonto()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        invoiceLine.setPricingReference(pricingReference);
                    }

                    /*
                     * Agregar impuestos de linea
                     * <Invoice><cac:InvoiceLine><cac:AllowanceCharge>
                     */
                    if (null != transLine.getDSCTOMonto()
                            && transLine.getDSCTOMonto().compareTo(
                                    BigDecimal.ZERO) == 1) {
                        /*
                         * ChargeIndicatorType
                         * 
                         * El valor FALSE representa que es un descuento de
                         * ITEM.
                         */
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllInvoiceLines() ["
                                    + this.identifier
                                    + "] Agregando DESCUENTO_LINEA.");
                        }
                        invoiceLine.getAllowanceCharge().add(
                                getAllowanceCharge(transLine.getDSCTOMonto(),
                                        currencyCode, false));
                    }

                    /*
                     * Agregar impuestos de linea
                     * <Invoice><cac:InvoiceLine><cac:TaxTotal>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando IMPUESTO DE LINEA.");
                    }
                    invoiceLine.getTaxTotal().addAll(
                            getAllTaxTotalLines(transLine
                                    .getTransaccionLineaImpuestosList()));

                    /*
                     * Agregar DESCRIPCION y CODIGO DEL ITEM
                     * <Invoice><cac:InvoiceLine><cac:Item>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando DESCRIPCION["
                                + transLine.getDescripcion()
                                + "] y COD_ARTICULO["
                                + transLine.getCodArticulo() + "]");
                    }
                    invoiceLine.setItem(getItemForLine(
                            transLine.getDescripcion(),
                            transLine.getCodArticulo()));

                    /*
                     * Agregar el VALOR UNITARIO del item
                     * <Invoice><cac:InvoiceLine><cac:Price>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier
                                + "] Agregando VALOR UNITARIO.");
                    }
                    invoiceLine.setPrice(getPriceForLine(
                            transLine.getTransaccionLineasBillrefList(),
                            currencyCode));

                    invoiceLineList.add(invoiceLine);
                }
                for (int i = 0; i < IUBLConfig.lstImporteIGV.size(); i++) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Totales con IGV"
                                + IUBLConfig.lstImporteIGV.get(i));
                    }
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllInvoiceLines() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllInvoiceLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName()
                        + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllInvoiceLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName() + ") -->"
                        + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllInvoiceLines() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllInvoiceLines() [" + this.identifier + "]");
        }
        return invoiceLineList;
    } // getAllInvoiceLines

    private List<InvoiceLineType> getAllBoletaLines(
            List<TransaccionLineas> transactionLines, String currencyCode)
            throws UBLDocumentException {

        if (logger.isDebugEnabled()) {
            logger.debug("+getAllBoletaLines() [" + this.identifier
                    + "] transactionLines: " + transactionLines
                    + " currencyCode: " + currencyCode);
        }
        List<InvoiceLineType> boletaLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                boletaLineList = new ArrayList<InvoiceLineType>(
                        transactionLines.size());
                for (int i = 0; i < transactionLines.size(); i++) {
                    IUBLConfig.lstImporteIGV.add(i, transactionLines.get(i).getTotalLineaConIGV());
                    if (logger.isDebugEnabled()) {
                        logger.debug("getImportesConIGV()" + IUBLConfig.lstImporteIGV.get(i));
                    }
                }
                // IUBLConfig.lstImporteIGV = new ArrayList<BigDecimal>();
                for (TransaccionLineas transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() ["
                                + this.identifier
                                + "] NroOrden: "
                                + transLine.getTransaccionLineasPK()
                                .getNroOrden() + "\nCantidad: "
                                + transLine.getCantidad() + "\tUnidad: "
                                + transLine.getUnidad() + "\tUnidadSunat: "
                                + transLine.getUnidadSunat()
                                + "\tTotalLineaSinIGV: "
                                + transLine.getTotalLineaSinIGV()
                                + "\tTotalLineaConIGV: "
                                + transLine.getTotalLineaConIGV()
                                + "\nPrecioRefCodigo: "
                                + transLine.getPrecioRefCodigo()
                                + "\tPrecioIGV: " + transLine.getPrecioIGV()
                                + "\tPrecioRefMonto: "
                                + transLine.getPrecioRefMonto()
                                + "\nDCTOMonto: " + transLine.getDSCTOMonto()
                                + "\tDescripcion: "
                                + transLine.getDescripcion()
                                + "\tCodArticulo: "
                                + transLine.getCodArticulo());

                    }

                    InvoiceLineType boletaLine = new InvoiceLineType();

                    /* Agregar <Invoice><cac:InvoiceLine><cbc:ID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando el ID.");
                    }
                    IDType idNumber = new IDType();
                    idNumber.setValue(String.valueOf(transLine
                            .getTransaccionLineasPK().getNroOrden()));
                    boletaLine.setID(idNumber);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun SUNAT
                     * <Invoice><cac:InvoiceLine><cbc:InvoicedQuantity>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando CANTIDAD y UNIDAD SUNAT.");
                    }
                    InvoicedQuantityType invoicedQuantity = new InvoicedQuantityType();
                    invoicedQuantity.setValue(transLine.getCantidad().setScale(
                            IUBLConfig.DECIMAL_LINE_QUANTITY,
                            RoundingMode.HALF_UP));
                    invoicedQuantity.setUnitCode(transLine.getUnidadSunat());
                    boletaLine.setInvoicedQuantity(invoicedQuantity);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun VENTURA
                     * <Invoice><cac:InvoiceLine><cbc:Note>
                     */
                    if (StringUtils.isNotBlank(transLine.getUnidad())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllBoletaLines() ["
                                    + this.identifier
                                    + "] Agregando UNIDAD de VENTURA.");
                        }
                        NoteType note = new NoteType();
                        note.setValue(transLine.getUnidad());
                        boletaLine.setNote(note);
                    }

                    /*
                     * Agregar
                     * <Invoice><cac:InvoiceLine><cbc:LineExtensionAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando TOTAL_LINEAS_SIN_IGV.");
                    }
                    LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
                    lineExtensionAmount.setValue(transLine
                            .getTotalLineaSinIGV().setScale(
                                    IUBLConfig.DECIMAL_LINE_EXTENSION_AMOUNT,
                                    RoundingMode.HALF_UP));
                    lineExtensionAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(currencyCode));
                    boletaLine.setLineExtensionAmount(lineExtensionAmount);

                    /*
                     * - Op. Onerosa : tiene precio unitario - Op. No Onerosa :
                     * tiene valor referencial.
                     * 
                     * <Invoice><cac:InvoiceLine><cac:PricingReference>
                     */
                    if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllBoletaLines() ["
                                    + this.identifier
                                    + "] PricingReference: PRECIO_UNITARIO");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioIGV()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_UNIT_PRICE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        boletaLine.setPricingReference(pricingReference);
                    } else if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllBoletaLines() ["
                                    + this.identifier
                                    + "] PricingReference: VALOR_REFERENCIAL");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();

                        /*
                         * El formato del precio unitario sera a 2 decimales en
                         * esta opcion porque se entiende que debe ser un valor
                         * 0.00
                         */
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine.getPrecioIGV().setScale(2,
                                                        RoundingMode.HALF_UP),
                                                currencyCode,
                                                IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE));
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioRefMonto()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        boletaLine.setPricingReference(pricingReference);
                    }

                    /*
                     * Agregar impuestos de linea
                     * <Invoice><cac:InvoiceLine><cac:AllowanceCharge>
                     */
                    if (null != transLine.getDSCTOMonto()
                            && transLine.getDSCTOMonto().compareTo(
                                    BigDecimal.ZERO) == 1) {
                        /*
                         * ChargeIndicatorType
                         * 
                         * El valor FALSE representa que es un descuento de
                         * ITEM.
                         */
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllBoletaLines() ["
                                    + this.identifier
                                    + "] Agregando DESCUENTO_LINEA.");
                        }
                        boletaLine.getAllowanceCharge().add(
                                getAllowanceCharge(transLine.getDSCTOMonto(),
                                        currencyCode, false));
                    }

                    /*
                     * Agregar impuestos de linea
                     * <Invoice><cac:InvoiceLine><cac:TaxTotal>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando IMPUESTO DE LINEA.");
                    }
                    boletaLine.getTaxTotal().addAll(
                            getAllTaxTotalLines(transLine
                                    .getTransaccionLineaImpuestosList()));

                    /*
                     * Agregar DESCRIPCION y CODIGO DEL ITEM
                     * <Invoice><cac:InvoiceLine><cac:Item>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando DESCRIPCION["
                                + transLine.getDescripcion()
                                + "] y COD_ARTICULO["
                                + transLine.getCodArticulo() + "]");
                    }
                    boletaLine.setItem(getItemForLine(
                            transLine.getDescripcion(),
                            transLine.getCodArticulo()));

                    /*
                     * Agregar el VALOR UNITARIO del item
                     * <Invoice><cac:InvoiceLine><cac:Price>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier
                                + "] Agregando VALOR UNITARIO.");
                    }
                    boletaLine.setPrice(getPriceForLine(
                            transLine.getTransaccionLineasBillrefList(),
                            currencyCode));

                    boletaLineList.add(boletaLine);
                }

                for (int i = 0; i < IUBLConfig.lstImporteIGV.size(); i++) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLinesImporteIGV."
                                + IUBLConfig.lstImporteIGV.get(i));
                    }
                }

            } catch (UBLDocumentException e) {
                logger.error("getAllBoletaLines() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllBoletaLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName()
                        + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllBoletaLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName() + ") -->"
                        + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllBoletaLines() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllBoletaLines() [" + this.identifier + "]");
        }
        return boletaLineList;
    } // getAllBoletaLines

    private List<CreditNoteLineType> getAllCreditNoteLines(
            List<TransaccionLineas> transactionLines, String currencyCode)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllCreditNoteLines() [" + this.identifier
                    + "] transactionLines: " + transactionLines
                    + " currencyCode: " + currencyCode);
        }
        List<CreditNoteLineType> creditNoteLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                creditNoteLineList = new ArrayList<CreditNoteLineType>(
                        transactionLines.size());

                for (TransaccionLineas transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] NroOrden: "
                                + transLine.getTransaccionLineasPK()
                                .getNroOrden() + "\nCantidad: "
                                + transLine.getCantidad() + "\tUnidad: "
                                + transLine.getUnidad() + "\tUnidadSunat: "
                                + transLine.getUnidadSunat()
                                + "\tTotalLineaSinIGV: "
                                + transLine.getTotalLineaSinIGV()
                                + "\nPrecioRefCodigo: "
                                + transLine.getPrecioRefCodigo()
                                + "\tPrecioIGV: " + transLine.getPrecioIGV()
                                + "\tPrecioRefMonto: "
                                + transLine.getPrecioRefMonto()
                                + "\nDCTOMonto: " + transLine.getDSCTOMonto()
                                + "\tDescripcion: "
                                + transLine.getDescripcion()
                                + "\tCodArticulo: "
                                + transLine.getCodArticulo());
                    }

                    CreditNoteLineType creditNoteLine = new CreditNoteLineType();

                    /* Agregar <CreditNote><cac:CreditNoteLine><cbc:ID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier + "] Agregando el ID.");
                    }
                    IDType idNumber = new IDType();
                    idNumber.setValue(String.valueOf(transLine
                            .getTransaccionLineasPK().getNroOrden()));
                    creditNoteLine.setID(idNumber);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun SUNAT
                     * <CreditNote><cac:CreditNoteLine><cbc:CreditedQuantity>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] Agregando CANTIDAD y UNIDAD SUNAT.");
                    }
                    CreditedQuantityType creditedQuantity = new CreditedQuantityType();
                    creditedQuantity.setValue(transLine.getCantidad().setScale(
                            IUBLConfig.DECIMAL_LINE_QUANTITY,
                            RoundingMode.HALF_UP));
                    creditedQuantity.setUnitCode(transLine.getUnidadSunat());
                    creditNoteLine.setCreditedQuantity(creditedQuantity);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun VENTURA
                     * <CreditNote><cac:CreditNoteLine><cbc:Note>
                     */
                    if (StringUtils.isNotBlank(transLine.getUnidad())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllCreditNoteLines() ["
                                    + this.identifier
                                    + "] Agregando UNIDAD de VENTURA.");
                        }
                        NoteType note = new NoteType();
                        note.setValue(transLine.getUnidad());
                        creditNoteLine.setNote(note);
                    }

                    /*
                     * Agregar
                     * <CreditNote><cac:CreditNoteLine><cbc:LineExtensionAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] Agregando TOTAL_LINEAS_SIN_IGV.");
                    }
                    LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
                    lineExtensionAmount.setValue(transLine
                            .getTotalLineaSinIGV().setScale(
                                    IUBLConfig.DECIMAL_LINE_EXTENSION_AMOUNT,
                                    RoundingMode.HALF_UP));
                    lineExtensionAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(currencyCode));
                    creditNoteLine.setLineExtensionAmount(lineExtensionAmount);

                    /*
                     * - Op. Onerosa : tiene precio unitario - Op. No Onerosa :
                     * tiene valor referencial.
                     * 
                     * <CreditNote><cac:CreditNoteLine><cac:PricingReference>
                     */
                    if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllCreditNoteLines() ["
                                    + this.identifier
                                    + "] PricingReference: PRECIO_UNITARIO");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioIGV()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_UNIT_PRICE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        creditNoteLine.setPricingReference(pricingReference);
                    } else if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllCreditNoteLines() ["
                                    + this.identifier
                                    + "] PricingReference: VALOR_REFERENCIAL");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();

                        /*
                         * El formato del precio unitario sera a 2 decimales en
                         * esta opcion porque se entiende que debe ser un valor
                         * 0.00
                         */
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine.getPrecioIGV().setScale(2,
                                                        RoundingMode.HALF_UP),
                                                currencyCode,
                                                IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE));
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioRefMonto()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        creditNoteLine.setPricingReference(pricingReference);
                    }

                    /*
                     * Agregar impuestos de linea
                     * <CreditNote><cac:CreditNoteLine><cac:TaxTotal>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] Agregando IMPUESTO DE LINEA.");
                    }
                    creditNoteLine.getTaxTotal().addAll(
                            getAllTaxTotalLines(transLine
                                    .getTransaccionLineaImpuestosList()));

                    /*
                     * Agregar DESCRIPCION y CODIGO DEL ITEM
                     * <CreditNote><cac:CreditNoteLine><cac:Item>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier + "] Agregando DESCRIPCION["
                                + transLine.getDescripcion()
                                + "] y COD_ARTICULO["
                                + transLine.getCodArticulo() + "]");
                    }
                    creditNoteLine.setItem(getItemForLine(
                            transLine.getDescripcion(),
                            transLine.getCodArticulo()));

                    /*
                     * Agregar el VALOR UNITARIO del item
                     * <CreditNote><cac:CreditNoteLine><cac:Price>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() ["
                                + this.identifier
                                + "] Agregando VALOR UNITARIO.");
                    }
                    creditNoteLine.setPrice(getPriceForLine(
                            transLine.getTransaccionLineasBillrefList(),
                            currencyCode));

                    creditNoteLineList.add(creditNoteLine);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllCreditNoteLines() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllCreditNoteLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName()
                        + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllCreditNoteLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName() + ") -->"
                        + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllCreditNoteLines() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllCreditNoteLines() [" + this.identifier + "]");
        }
        return creditNoteLineList;
    } // getAllCreditNoteLines

    private List<DebitNoteLineType> getAllDebitNoteLines(
            List<TransaccionLineas> transactionLines, String currencyCode)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllDebitNoteLines() [" + this.identifier
                    + "] transactionLines: " + transactionLines
                    + " currencyCode: " + currencyCode);
        }
        List<DebitNoteLineType> debitNoteLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                debitNoteLineList = new ArrayList<DebitNoteLineType>(
                        transactionLines.size());

                for (TransaccionLineas transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] NroOrden: "
                                + transLine.getTransaccionLineasPK()
                                .getNroOrden() + "\nCantidad: "
                                + transLine.getCantidad() + "\tUnidad: "
                                + transLine.getUnidad() + "\tUnidadSunat: "
                                + transLine.getUnidadSunat()
                                + "\tTotalLineaSinIGV: "
                                + transLine.getTotalLineaSinIGV()
                                + "\nPrecioRefCodigo: "
                                + transLine.getPrecioRefCodigo()
                                + "\tPrecioIGV: " + transLine.getPrecioIGV()
                                + "\tPrecioRefMonto: "
                                + transLine.getPrecioRefMonto()
                                + "\nDCTOMonto: " + transLine.getDSCTOMonto()
                                + "\tDescripcion: "
                                + transLine.getDescripcion()
                                + "\tCodArticulo: "
                                + transLine.getCodArticulo());
                    }

                    DebitNoteLineType debitNoteLine = new DebitNoteLineType();

                    /* Agregar <DebitNote><cac:DebitNoteLine><cbc:ID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier + "] Agregando el ID.");
                    }
                    IDType idNumber = new IDType();
                    idNumber.setValue(String.valueOf(transLine
                            .getTransaccionLineasPK().getNroOrden()));
                    debitNoteLine.setID(idNumber);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun SUNAT
                     * <DebitNote><cac:DebitNoteLine><cbc:CreditedQuantity>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] Agregando CANTIDAD y UNIDAD SUNAT.");
                    }
                    DebitedQuantityType debitedQuantity = new DebitedQuantityType();
                    debitedQuantity.setValue(transLine.getCantidad().setScale(
                            IUBLConfig.DECIMAL_LINE_QUANTITY,
                            RoundingMode.HALF_UP));
                    debitedQuantity.setUnitCode(transLine.getUnidadSunat());
                    debitNoteLine.setDebitedQuantity(debitedQuantity);

                    /*
                     * Agregar UNIDAD DE MEDIDA segun VENTURA
                     * <DebitNote><cac:DebitNoteLine><cbc:Note>
                     */
                    if (StringUtils.isNotBlank(transLine.getUnidad())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllDebitNoteLines() ["
                                    + this.identifier
                                    + "] Agregando UNIDAD de VENTURA.");
                        }
                        NoteType note = new NoteType();
                        note.setValue(transLine.getUnidad());
                        debitNoteLine.setNote(note);
                    }

                    /*
                     * Agregar
                     * <DebitNote><cac:DebitNoteLine><cbc:LineExtensionAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] Agregando TOTAL_LINEAS_SIN_IGV.");
                    }
                    LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
                    lineExtensionAmount.setValue(transLine
                            .getTotalLineaSinIGV().setScale(
                                    IUBLConfig.DECIMAL_LINE_EXTENSION_AMOUNT,
                                    RoundingMode.HALF_UP));
                    lineExtensionAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(currencyCode));
                    debitNoteLine.setLineExtensionAmount(lineExtensionAmount);

                    /*
                     * - Op. Onerosa : tiene precio unitario - Op. No Onerosa :
                     * tiene valor referencial.
                     * 
                     * <DebitNote><cac:DebitNoteLine><cac:PricingReference>
                     */
                    if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllDebitNoteLines() ["
                                    + this.identifier
                                    + "] PricingReference: PRECIO_UNITARIO");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioIGV()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_UNIT_PRICE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        debitNoteLine.setPricingReference(pricingReference);
                    } else if (transLine.getPrecioRefCodigo().equalsIgnoreCase(
                            IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getAllDebitNoteLines() ["
                                    + this.identifier
                                    + "] PricingReference: VALOR_REFERENCIAL");
                        }
                        PricingReferenceType pricingReference = new PricingReferenceType();

                        /*
                         * El formato del precio unitario sera a 2 decimales en
                         * esta opcion porque se entiende que debe ser un valor
                         * 0.00
                         */
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine.getPrecioIGV().setScale(2,
                                                        RoundingMode.HALF_UP),
                                                currencyCode,
                                                IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE));
                        pricingReference
                                .getAlternativeConditionPrice()
                                .add(getAlternativeConditionPrice(
                                                transLine
                                                .getPrecioRefMonto()
                                                .setScale(
                                                        IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE,
                                                        RoundingMode.HALF_UP),
                                                currencyCode, transLine
                                                .getPrecioRefCodigo()));

                        debitNoteLine.setPricingReference(pricingReference);
                    }

                    /*
                     * Agregar impuestos de linea
                     * <DebitNote><cac:DebitNoteLine><cac:TaxTotal>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] Agregando IMPUESTO DE LINEA.");
                    }
                    debitNoteLine.getTaxTotal().addAll(
                            getAllTaxTotalLines(transLine
                                    .getTransaccionLineaImpuestosList()));

                    /*
                     * Agregar DESCRIPCION y CODIGO DEL ITEM
                     * <DebitNote><cac:DebitNoteLine><cac:Item>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier + "] Agregando DESCRIPCION["
                                + transLine.getDescripcion()
                                + "] y COD_ARTICULO["
                                + transLine.getCodArticulo() + "]");
                    }
                    debitNoteLine.setItem(getItemForLine(
                            transLine.getDescripcion(),
                            transLine.getCodArticulo()));

                    /*
                     * Agregar el VALOR UNITARIO del item
                     * <DebitNote><cac:DebitNoteLine><cac:Price>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() ["
                                + this.identifier
                                + "] Agregando VALOR UNITARIO.");
                    }
                    debitNoteLine.setPrice(getPriceForLine(
                            transLine.getTransaccionLineasBillrefList(),
                            currencyCode));

                    debitNoteLineList.add(debitNoteLine);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllDebitNoteLines() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllDebitNoteLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName()
                        + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllDebitNoteLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName() + ") -->"
                        + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllDebitNoteLines() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllDebitNoteLines() [" + this.identifier + "]");
        }
        return debitNoteLineList;
    } // getAllDebitNoteLines

    private List<SummaryDocumentsLineType> getAllSummaryDocumentLines(
            List<TransaccionResumenLinea> transactionLines)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllSummaryDocumentLines() [" + this.identifier
                    + "] ");
        }
        List<SummaryDocumentsLineType> summaryDocumentLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                summaryDocumentLineList = new ArrayList<SummaryDocumentsLineType>(
                        transactionLines.size());

                for (TransaccionResumenLinea transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] IdLinea: "
                                + transLine.getTransaccionResumenLineaPK()
                                .getIdLinea() + "\nTipoDocument: "
                                + transLine.getTipoDocumento()
                                + "\tNumeroSerie: "
                                + transLine.getNumeroSerie()
                                + "\tCorrelativoInicio: "
                                + transLine.getNumeroCorrelativoInicio()
                                + "\tCorrelativoFin: "
                                + transLine.getNumeroCorrelativoFin()
                                + "\nImporteTotal: "
                                + transLine.getImporteTotal()
                                + "\tOpGravadas: "
                                + transLine.getTotalOPGravadas()
                                + "\tOpExoneradas: "
                                + transLine.getTotalOPExoneradas()
                                + "\tOpInafecatas: "
                                + transLine.getTotalOPInafectas()
                                + "\nOtrosCargos: "
                                + transLine.getImporteOtrosCargos() + "\tIGV: "
                                + transLine.getTotaIGV() + "\tISC: "
                                + transLine.getTotalISC() + "\tCodMoneda: "
                                + transLine.getCodMoneda());
                    }

                    SummaryDocumentsLineType summaryDocumentLine = new SummaryDocumentsLineType();

                    /* Agregar <sac:SummaryDocumentsLine><cbc:LineID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier + "] Agregando el LINEID.");
                    }
                    LineIDType lineID = new LineIDType();
                    lineID.setValue(String.valueOf(transLine
                            .getTransaccionResumenLineaPK().getIdLinea()));
                    summaryDocumentLine.setLineID(lineID);

                    /*
                     * Agregar el codigo del tipo de documento de la LINEA
                     * <sac:SummaryDocumentsLine><cbc:DocumentTypeCode>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando el CODIGO del tipo de documento.");
                    }
                    DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                    documentTypeCode.setValue(transLine.getTipoDocumento());
                    summaryDocumentLine.setDocumentTypeCode(documentTypeCode);

                    /*
                     * Agregar la SERIE a la que hace referencia los documentos
                     * de esta LINEA.
                     * <sac:SummaryDocumentsLine><sac:DocumentSerialID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando la SERIE a la LINEA.");
                    }
                    IdentifierType documentSerialID = new IdentifierType();
                    documentSerialID.setValue(transLine.getNumeroSerie());
                    summaryDocumentLine.setDocumentSerialID(documentSerialID);

                    /*
                     * Agregar el INICIO del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:StartDocumentNumberID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando el INICIO del CORRELATIVO.");
                    }
                    IdentifierType startDocumentNumberID = new IdentifierType();
                    startDocumentNumberID.setValue(transLine
                            .getNumeroCorrelativoInicio());
                    summaryDocumentLine
                            .setStartDocumentNumberID(startDocumentNumberID);

                    /*
                     * Agregar el FIN del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:EndDocumentNumberID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando el FIN del CORRELATIVO.");
                    }
                    IdentifierType endDocumentNumberID = new IdentifierType();
                    endDocumentNumberID.setValue(transLine
                            .getNumeroCorrelativoFin());
                    summaryDocumentLine
                            .setEndDocumentNumberID(endDocumentNumberID);

                    /*
                     * Agregar el MONTO TOTAL de la LINEA
                     * <sac:SummaryDocumentsLine><sac:TotalAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando el MONTO TOTAL de la LINEA.");
                    }
                    AmountType totalAmount = new AmountType();
                    totalAmount.setValue(transLine.getImporteTotal().setScale(
                            2, RoundingMode.HALF_UP));
                    totalAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(transLine.getCodMoneda()));
                    summaryDocumentLine.setTotalAmount(totalAmount);

                    /*
                     * Agregar MONTOS de los tipos de operaciones GRAVADAS,
                     * EXONERADAS E INAFECTAS
                     * <sac:SummaryDocumentsLine><sac:BillingPayment>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando MONTOS DE tipos de OPERACIONES.");
                    }
                    summaryDocumentLine.getBillingPayment().add(
                            getBillingPayment(transLine.getTotalOPGravadas(),
                                    transLine.getCodMoneda(),
                                    IUBLConfig.INSTRUCTION_ID_GRAVADO));
                    summaryDocumentLine.getBillingPayment().add(
                            getBillingPayment(transLine.getTotalOPExoneradas(),
                                    transLine.getCodMoneda(),
                                    IUBLConfig.INSTRUCTION_ID_EXONERADO));
                    summaryDocumentLine.getBillingPayment().add(
                            getBillingPayment(transLine.getTotalOPInafectas(),
                                    transLine.getCodMoneda(),
                                    IUBLConfig.INSTRUCTION_ID_INAFECTO));

                    /*
                     * Agregar Sumatoria de Otros Cargos
                     * <sac:SummaryDocumentsLine><cac:AllowanceCharge>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier
                                + "] Agregando sumatoria de OTROS CARGOS.");
                    }
                    summaryDocumentLine.getAllowanceCharge().add(
                            getAllowanceCharge(
                                    transLine.getImporteOtrosCargos(),
                                    transLine.getCodMoneda(), true));

                    /* Agregar MONTOS de IMPUESTOS IGV e ISC */
                    summaryDocumentLine.getTaxTotal().add(
                            getTaxTotalForSummary(transLine.getTotaIGV(),
                                    transLine.getCodMoneda(),
                                    IUBLConfig.TAX_TOTAL_IGV_ID,
                                    IUBLConfig.TAX_TOTAL_IGV_NAME,
                                    IUBLConfig.TAX_TOTAL_IGV_CODE));
                    summaryDocumentLine.getTaxTotal().add(
                            getTaxTotalForSummary(transLine.getTotalISC(),
                                    transLine.getCodMoneda(),
                                    IUBLConfig.TAX_TOTAL_ISC_ID,
                                    IUBLConfig.TAX_TOTAL_ISC_NAME,
                                    IUBLConfig.TAX_TOTAL_ISC_CODE));

                    summaryDocumentLineList.add(summaryDocumentLine);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier
                        + "] UBLDocumentException - ERROR: "
                        + e.getError().getId() + "-"
                        + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName()
                        + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllSummaryDocumentLines() [" + this.identifier
                        + "] Exception(" + e.getClass().getName() + ") -->"
                        + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllSummaryDocumentLines() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllSummaryDocumentLines() [" + this.identifier
                    + "]");
        }
        return summaryDocumentLineList;
    } // getAllSummaryDocumentLines

    private AllowanceChargeType getAllowanceCharge(BigDecimal amountVal,
            String currencyCode, boolean chargeIndicatorVal)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllowanceCharge() [" + this.identifier + "]");
        }
        AllowanceChargeType allowanceCharge = null;

        try {
            allowanceCharge = new AllowanceChargeType();

            /*
             * Agregar
             * <Invoice><cac:InvoiceLine><cac:AllowanceCharge><cbc:ChargeIndicator
             * >
             */
            ChargeIndicatorType chargeIndicator = new ChargeIndicatorType();
            chargeIndicator.setValue(chargeIndicatorVal);

            /*
             * Agregar
             * <Invoice><cac:InvoiceLine><cac:AllowanceCharge><cbc:Amount>
             */
            AmountType amount = new AmountType();
            amount.setValue(amountVal.setScale(2, RoundingMode.HALF_UP));
            amount.setCurrencyID(CurrencyCodeContentType.valueOf(currencyCode));

            /* Agregar los tag's */
            allowanceCharge.setChargeIndicator(chargeIndicator);
            allowanceCharge.setAmount(amount);
        } catch (Exception e) {
            logger.error("getAllowanceCharge() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_327.getMessage());

            throw new UBLDocumentException(IVenturaError.ERROR_327);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllowanceCharge() [" + this.identifier + "]");
        }
        return allowanceCharge;
    } // getAllowanceCharge

    private ItemType getItemForLine(String descriptionVal, String articleCodeVal)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getItemForLine() [" + this.identifier + "]");
        }
        ItemType item = null;

        try {
            item = new ItemType();

            if (StringUtils.isNotBlank(descriptionVal)) {
                /* Agregar <Invoice><cac:InvoiceLine><cac:Item><cbc:Description> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getItemForLine() [" + this.identifier
                            + "] Agregando la DESCRIPCION del item.");
                }
                DescriptionType description = new DescriptionType();
                description.setValue(descriptionVal);

                item.getDescription().add(description);
            } else {
                throw new UBLDocumentException(IVenturaError.ERROR_325);
            }

            if (StringUtils.isNotBlank(articleCodeVal)) {
                /*
                 * Agregar <Invoice><cac:InvoiceLine><cac:Item><cac:
                 * SellersItemIdentification>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getItemForLine() [" + this.identifier
                            + "] Agregando el CODIGO DE ARTICULO.");
                }
                ItemIdentificationType sellersItemIdentification = new ItemIdentificationType();
                IDType id = new IDType();
                id.setValue(articleCodeVal);
                sellersItemIdentification.setID(id);

                item.setSellersItemIdentification(sellersItemIdentification);
            }
        } catch (UBLDocumentException e) {
            logger.error("getItemForLine() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getItemForLine() [" + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_326.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_326);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getItemForLine() [" + this.identifier + "]");
        }
        return item;
    } // getItemForLine

    private PriceType getPriceForLine(
            List<TransaccionLineasBillref> transBillReferenceList,
            String currencyCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getPriceForLine() [" + this.identifier + "]");
        }
        PriceType price = null;

        try {
            String unitValue = null;

            if (null != transBillReferenceList
                    && 0 < transBillReferenceList.size()) {
                for (TransaccionLineasBillref billReference : transBillReferenceList) {
                    if (billReference.getAdtDocRefSchemaId().equalsIgnoreCase(
                            IUBLConfig.HIDDEN_UVALUE)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getPriceForLine() ["
                                    + this.identifier + "] Se encontro el "
                                    + IUBLConfig.HIDDEN_UVALUE);
                        }
                        unitValue = billReference.getAdtDocRefId();
                        break;
                    }
                }

                if (StringUtils.isNotBlank(unitValue)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getPriceForLine() [" + this.identifier
                                + "] Agregando el VALOR UNITARIO al tag.");
                    }
                    price = new PriceType();

                    PriceAmountType priceAmount = new PriceAmountType();
                    priceAmount.setValue(BigDecimal.valueOf(
                            Double.valueOf(unitValue)).setScale(
                                    IUBLConfig.DECIMAL_LINE_UNIT_VALUE,
                                    RoundingMode.HALF_UP));
                    priceAmount.setCurrencyID(CurrencyCodeContentType
                            .valueOf(currencyCode));

                    price.setPriceAmount(priceAmount);
                } else {
                    logger.error("getPriceForLine() [" + this.identifier
                            + "] ERROR: "
                            + IVenturaError.ERROR_334.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_334);
                }
            } else {
                logger.error("getPriceForLine() [" + this.identifier
                        + "] ERROR: " + IVenturaError.ERROR_333.getMessage());
                throw new UBLDocumentException(IVenturaError.ERROR_333);
            }
        } catch (UBLDocumentException e) {
            logger.error("getPriceForLine() [" + this.identifier
                    + "] UBLDocumentException - ERROR: " + e.getError().getId()
                    + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getPriceForLine() [" + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_332.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_332);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getPriceForLine() [" + this.identifier + "]");
        }
        return price;
    } // getPriceForLine

    // private boolean checkOnerosaOperation(List<TransaccionLineaImpuestos>
    // taxTotalTransactionLines) throws UBLDocumentException
    // {
    // boolean flag = false;
    //
    // if (null != taxTotalTransactionLines && 0 <
    // taxTotalTransactionLines.size())
    // {
    // for (TransaccionLineaImpuestos taxTransactionLine :
    // taxTotalTransactionLines)
    // {
    // if
    // (taxTransactionLine.getTipoTributo().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID))
    // {
    // /*
    // * Si contiene un 0 en el segunda caracter es una operacion onerosa.
    // * - 10 : Gravado - Operacion Onerosa
    // * - 20 : Exonerado - Operacion Onerosa
    // * - 30 : Inafecto - Operacion Onerosa
    // */
    // if
    // (taxTransactionLine.getTipoAfectacion().substring(1).equalsIgnoreCase("0"))
    // {
    // flag = true; break;
    // }
    // }
    // }
    // }
    // else
    // {
    // logger.error("checkOnerosaOperation() ERROR: " +
    // IVenturaError.ERROR_322.getMessage());
    // throw new UBLDocumentException(IVenturaError.ERROR_322);
    // }
    // return flag;
    // } //checkOnerosaOperation
    private PriceType getAlternativeConditionPrice(BigDecimal value,
            String currencyCode, String type) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getAlternativeConditionPrice() value: " + value
                    + " currencyCode: " + currencyCode + " type: " + type);
        }
        PriceType alternativeConditionPrice = new PriceType();

        /*
         * <cac:PricingReference><cac:AlternativeConditionPrice><cbc:PriceAmount>
         */
        PriceAmountType priceAmount = new PriceAmountType();
        priceAmount.setValue(value);
        priceAmount
                .setCurrencyID(CurrencyCodeContentType.valueOf(currencyCode));

        /*
         * <cac:PricingReference><cac:AlternativeConditionPrice><cbc:PriceTypeCode
         * >
         */
        PriceTypeCodeType priceTypeCode = new PriceTypeCodeType();
        priceTypeCode.setValue(type);

        alternativeConditionPrice.setPriceAmount(priceAmount);
        alternativeConditionPrice.setPriceTypeCode(priceTypeCode);

        return alternativeConditionPrice;
    } // getAlternativeConditionPrice

    private ResponseType getDiscrepancyResponse(String referenceIDValue,
            String responseCodeValue, String descriptionValue)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getDiscrepancyResponse() [" + this.identifier
                    + "] referenceIDValue: " + referenceIDValue
                    + " responseCodeValue: " + responseCodeValue
                    + " descriptionValue: " + descriptionValue);
        }
        ResponseType discrepancyResponse = null;

        try {
            /* Agregar <cac:DiscrepancyResponse><cbc:ReferenceID> */
            ReferenceIDType referenceID = new ReferenceIDType();
            referenceID.setValue(referenceIDValue);

            /* Agregar <cac:DiscrepancyResponse><cbc:ResponseCode> */
            ResponseCodeType responseCode = new ResponseCodeType();
            responseCode.setValue(responseCodeValue);

            /* Agregar <cac:DiscrepancyResponse><cbc:Description> */
            DescriptionType description = new DescriptionType();
            description.setValue(descriptionValue);

            /* Agregar los TAG's */
            discrepancyResponse = new ResponseType();
            discrepancyResponse.setReferenceID(referenceID);
            discrepancyResponse.setResponseCode(responseCode);
            discrepancyResponse.getDescription().add(description);
        } catch (Exception e) {
            logger.error("getDiscrepancyResponse() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_337);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getDiscrepancyResponse() [" + this.identifier + "]");
        }
        return discrepancyResponse;
    } // getDiscrepancyResponse

    private BillingReferenceType getBillingReference(String referenceIDValue,
            String referenceDocType) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getBillingReference() [" + this.identifier
                    + "] referenceIDValue: " + referenceIDValue
                    + " referenceDocType: " + referenceDocType);
        }
        BillingReferenceType billingReference = null;

        try {
            /* <cac:BillingReference><cac:InvoiceDocumentReference> */
            DocumentReferenceType invoiceDocumentReference = new DocumentReferenceType();

            /*
             * Agregar
             * <cac:BillingReference><cac:InvoiceDocumentReference><cbc:ID>
             */
            IDType id = new IDType();
            id.setValue(referenceIDValue);
            invoiceDocumentReference.setID(id);

            /*
             * Agregar <cac:BillingReference><cac:InvoiceDocumentReference><cbc:
             * DocumentTypeCode>
             */
            DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
            documentTypeCode.setValue(referenceDocType);
            invoiceDocumentReference.setDocumentTypeCode(documentTypeCode);

            /* Agregar los TAG's */
            billingReference = new BillingReferenceType();
            billingReference
                    .setInvoiceDocumentReference(invoiceDocumentReference);
        } catch (Exception e) {
            logger.error("getBillingReference() [" + this.identifier
                    + "] Exception(" + e.getClass().getName() + ") - ERROR: "
                    + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_338);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getBillingReference() [" + this.identifier + "]");
        }
        return billingReference;
    } // getBillingReference

    private BigDecimal getSubtotalValueFromTransaction(
            List<TransaccionTotales> transactionTotalList)
            throws UBLDocumentException {
        BigDecimal subtotal = null;

        if (null != transactionTotalList && 0 < transactionTotalList.size()) {
            for (int i = 0; i < transactionTotalList.size(); i++) {
                if (transactionTotalList.get(i).getTransaccionTotalesPK()
                        .getId()
                        .equalsIgnoreCase(IUBLConfig.ADDITIONAL_MONETARY_1005)) {
                    subtotal = transactionTotalList.get(i).getMonto();
                    transactionTotalList.remove(i);
                    break;
                }
            }
            if (subtotal == null) {
                subtotal = BigDecimal.ZERO;
            }
        } else {
            logger.error("getSubtotalValueFromTransaction() ["
                    + this.identifier + "] ERROR: "
                    + IVenturaError.ERROR_330.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_330);
        }

        //if (null == subtotal) {
        //    logger.error("getSubtotalValueFromTransaction() ["
        //            + this.identifier + "] ERROR: "
        //            + IVenturaError.ERROR_348.getMessage());
        //    throw new UBLDocumentException(IVenturaError.ERROR_348);
        //}
        return subtotal;
    }

    private PaymentType getBillingPayment(BigDecimal amount,
            String currencyCode, String operationType)
            throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getBillingPayment() [" + this.identifier
                    + "] amount: " + amount + " operationType: "
                    + operationType);
        }
        PaymentType paymentType = null;

        try {
            /*
             * Agregar
             * <sac:SummaryDocumentsLine><sac:BillingPayment><cbc:PaidAmount>
             */
            PaidAmountType paidAmount = new PaidAmountType();
            paidAmount.setValue(amount.setScale(2, RoundingMode.HALF_UP));
            paidAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            /*
             * Agregar
             * <sac:SummaryDocumentsLine><sac:BillingPayment><cbc:InstructionID>
             */
            InstructionIDType instructionID = new InstructionIDType();
            instructionID.setValue(operationType);

            /*
             * Agregar los TAG's
             */
            paymentType = new PaymentType();
            paymentType.setPaidAmount(paidAmount);
            paymentType.setInstructionID(instructionID);
        } catch (Exception e) {
            logger.error("getBillingPayment() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_349.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_349);
        }
        return paymentType;
    } // getBillingPayment

    private TaxTotalType getTaxTotalForSummary(BigDecimal taxAmountValue,
            String currencyCode, String schemeID, String schemeName,
            String schemeCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getTaxTotalForSummary() [" + this.identifier
                    + "] taxAmountValue: " + taxAmountValue + " schemeID: "
                    + schemeID);
        }
        TaxTotalType taxTotal = null;

        try {
            taxTotal = new TaxTotalType();

            /* Agregar <cac:TaxTotal><cbc:TaxAmount> */
            TaxAmountType taxAmount = new TaxAmountType();
            taxAmount
                    .setValue(taxAmountValue.setScale(2, RoundingMode.HALF_UP));
            taxAmount.setCurrencyID(CurrencyCodeContentType
                    .valueOf(currencyCode));

            TaxSubtotalType taxSubTotal = new TaxSubtotalType();

            /* Agregar <cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount> */
            taxSubTotal.setTaxAmount(taxAmount);

            /* Agregar <cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory> */
            TaxCategoryType taxCategory = new TaxCategoryType();
            taxCategory.setTaxScheme(getTaxScheme(schemeID, schemeName,
                    schemeCode));
            taxSubTotal.setTaxCategory(taxCategory);

            /*
             * Agregar los TAG's
             */
            taxTotal.setTaxAmount(taxAmount);
            taxTotal.getTaxSubtotal().add(taxSubTotal);
        } catch (Exception e) {
            logger.error("getTaxTotalForSummary() [" + this.identifier
                    + "] ERROR: " + IVenturaError.ERROR_350.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_350);
        }
        return taxTotal;
    } // getTaxTotalForSummary

    private List<DocumentReferenceType> getDespatchDocumentReferences(
            List<TransaccionDocrefers> transaccionDocrefersList)
            throws UBLDocumentException {
        if (logger.isInfoEnabled()) {
            logger.info("+getDespatchDocumentReferences() [" + this.identifier
                    + "]");
        }
        List<DocumentReferenceType> despatchDocRefList = null;

        if (null != transaccionDocrefersList
                && 0 < transaccionDocrefersList.size()) {
            despatchDocRefList = new ArrayList<DocumentReferenceType>();

            for (TransaccionDocrefers transDocrefer : transaccionDocrefersList) {

                if (StringUtils.isNotBlank(transDocrefer.getId())) {
                    String[] remissionGuides = transDocrefer.getId().trim()
                            .split(",");

                    for (String rGuide : remissionGuides) {
                        DocumentReferenceType despatchDocumentReference = new DocumentReferenceType();

                        /* <cac:DespatchDocumentReference><cbc:ID> */
                        IDType id = new IDType();
                        id.setValue(rGuide.trim());
                        despatchDocumentReference.setID(id);

                        /*
                         * <cac:DespatchDocumentReference><cbc:DocumentTypeCode
                         * >
                         */
                        DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                        documentTypeCode
                                .setValue(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE);
                        despatchDocumentReference
                                .setDocumentTypeCode(documentTypeCode);

                        /* Agregar a la lista */
                        despatchDocRefList.add(despatchDocumentReference);
                    }
                } else {
                    logger.error("getDespatchDocumentReferences() ["
                            + this.identifier + "] ERROR: "
                            + IVenturaError.ERROR_0.getMessage());
                    throw new UBLDocumentException(IVenturaError.ERROR_0);
                }

            } // for
        }
        if (logger.isInfoEnabled()) {
            logger.info("-getDespatchDocumentReferences() [" + this.identifier
                    + "]");
        }
        return despatchDocRefList;
    } // getDespatchDocumentReferences

    private DocumentReferenceType getContractDocumentReference(String value,
            String code) {
        DocumentReferenceType contractDocumentReference = new DocumentReferenceType();

        IDType id = new IDType();
        id.setValue(value);
        contractDocumentReference.setID(id);

        DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
        documentTypeCode.setValue(code);
        contractDocumentReference.setDocumentTypeCode(documentTypeCode);

        return contractDocumentReference;
    } // getContractDocumentReference

} // UBLDocumentHandler

