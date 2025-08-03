package ventura.soluciones.commons.handler;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TotalInvoiceAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.dto.hb.TransaccionComprobantePago;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.dto.hb.TransaccionResumenLinea;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.*;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.IdentifierType;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.UBLDocumentException;
import ventura.soluciones.commons.exception.error.IVenturaError;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Esta clase HANDLER contiene metodos para generar objetos UBL, necesarios para
 * armar el documento UBL validado por Sunat.
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
     * @param identifier   El identificador (numero RUC) del contribuyente.
     * @param socialReason La razon social del contribuyente.
     * @param signerName   El nombre del firmante (puede ser un valor
     *                     identificador).
     * @return Retorna el objeto SignatureType con los datos del contribuyente.
     * @throws UBLDocumentException
     */
    public SignatureType generateSignature(String identifier, String socialReason, String signerName) throws UBLDocumentException {
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
     * @param identifier     El identificador (numero de RUC) del contribuyente
     *                       emisor.
     * @param identifierType El tipo de identificador del contribuyente emisor.
     * @param socialReason   La razon social del contribuyente emisor.
     * @param commercialName El nombre comercial del contribuyente emisor.
     * @param fiscalAddress  La direccion fiscal del contribuyente emisor.
     * @param department     El nombre del departamento del contribuyente emisor.
     * @param province       El nombre de la provincia del contribuyente emisor.
     * @param district       El nombre del distrito del contribuyente emisor.
     * @param ubigeo         El codigo de ubigeo del contribuyente emisor.
     * @param countryCode    El codigo de pais del contribuyente emisor.
     * @param contactName    El nombre del contacto emisor.
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
     * @param identifier     El identificador del contribuyente receptor.
     * @param identifierType El tipo de identificador del contribuyente
     *                       receptor.
     * @param socialReason   La razon social del contribuyente receptor.
     * @param commercialName El nombre comercial del contribuyente receptor.
     * @param fiscalAddress  La direccion fiscal del contribuyente receptor.
     * @param department     El nombre del departamento del contribuyente receptor.
     * @param province       El nombre de la provincia del contribuyente receptor.
     * @param district       El nombre del distrito del contribuyente receptor.
     * @param ubigeo         El codigo de ubigeo del contribuyente receptor.
     * @param countryCode    El codigo de pais del contribuyente receptor.
     * @param contactName    El nombre del contacto del contribuyente receptor.
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

    /**
     * Este metodo genera el objeto PartyType que es utilizado para contener los
     * datos del emisor y receptor electronico y armar el documento UBL.
     *
     * @param socialReasonValue   La razon social del contribuyente (emisor o
     *                            receptor).
     * @param commercialNameValue El nombre comercial del contribuyente (emisor
     *                            o receptor).
     * @param fiscalAddressValue  La direccion fiscal del contribuyente (emisor o
     *                            receptor).
     * @param departmentValue     El nombre del departamento del contribuyente
     *                            (emisor o receptor).
     * @param provinceValue       El nombre de la provincia del contribuyente (emisor
     *                            o receptor).
     * @param districtValue       El nombre del distrito del contribuyente (emisor o
     *                            receptor).
     * @param ubigeoValue         El codigo de ubigeo del contribuyente (emisor o
     *                            receptor).
     * @param countryCodeValue    El codigo de pais del contribuyente (emisor o
     *                            receptor).
     * @param contactNameValue    El nombre del contacto.
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

    /**
     * Este metodo coloca la informacion adicional ingresada referente al
     * contribuyente en un objeto JAXB (InvoiceType, CreditNoteType,
     * DebitNoteType).
     *
     * @param ublDocument                El objeto JAXB.
     * @param signatureType              El objeto SignatureType que contiene los datos de la
     *                                   firma.
     * @param accountingSupplierParty    El objeto SupplierPartyType que contiene
     *                                   la informacion del emisor electronico.
     * @param accountingCustomerParty    El objeto CustomerPartyType que contiene
     *                                   la informacion del receptor electronico.
     * @param documentSerie              La serie del documento UBL.
     * @param documentCorrelative        El numero correlativo del documento UBL.
     * @param relatedDocumentCorrelative El numero correlativo del documento
     *                                   relacionado. (Solo para CreditNoteType y DebitNoteType).
     * @param docUUID                    El identificador del documento.
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
     * @param ublDocument             El objeto JAXB.
     * @param signatureType           El objeto SignatureType que contiene los datos de la
     *                                firma.
     * @param accountingSupplierParty El objeto SupplierPartyType que contiene
     *                                la informacion del emisor electronico.
     * @param documentType            El tipo de documento.
     * @param documentCorrelative     El numero correlativo del documento UBL.
     * @param documentNumberMap       El objeto MAP que contiene los numeros de los
     *                                documentos a ingresar.
     * @param docUUID                 El identificador del documento.
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
     * @param pattern   Patron de la fecha.
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
    public ApplicationResponseType generateConstancyDummy(String docUUID) throws Exception {
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

    private List<SummaryDocumentsLineType> getAllSummaryDocumentLinesV2(List<TransaccionResumenLinea> transactionLines) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllSummaryDocumentLines() [" + this.identifier + "] ");
        }
        List<SummaryDocumentsLineType> summaryDocumentLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                summaryDocumentLineList = new ArrayList<>(transactionLines.size());
                for (TransaccionResumenLinea transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() ["
                                + this.identifier + "] IdLinea: " + transLine.getTransaccionResumenLineaPK().getIdLinea() + "\nTipoDocument: " + transLine.getTipoDocumento() + "\tNumeroSerie: " + transLine.getNumeroSerie() + "\tCorrelativoInicio: "
                                + transLine.getNumeroCorrelativoInicio() + "\tCorrelativoFin: " + transLine.getNumeroCorrelativoFin() + "\nImporteTotal: " + transLine.getImporteTotal() + "\tOpGravadas: " + transLine.getTotalOPGravadas()
                                + "\tOpExoneradas: " + transLine.getTotalOPExoneradas() + "\tOpInafecatas: " + transLine.getTotalOPInafectas() + "\nOtrosCargos: " + transLine.getImporteOtrosCargos() + "\tIGV: " + transLine.getTotaIGV() + "\tISC: "
                                + transLine.getTotalISC() + "\tCodMoneda: " + transLine.getCodMoneda());
                    }
                    SummaryDocumentsLineType summaryDocumentLine = new SummaryDocumentsLineType();

                    /* Agregar <sac:SummaryDocumentsLine><cbc:LineID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el LINEID.");
                    }
                    LineIDType lineID = new LineIDType();
                    lineID.setValue(String.valueOf(transLine.getTransaccionResumenLineaPK().getIdLinea()));
                    summaryDocumentLine.setLineID(lineID);

                    /*
                     * Agregar la SERIE a la que hace referencia los documentos
                     * de esta LINEA.
                     * <sac:SummaryDocumentsLine><sac:DocumentSerialID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando la SERIE a la LINEA.");
                    }

                    IDType iDType3 = new IDType();
                    iDType3.setValue(transLine.getNumeroSerie() + "-" + transLine.getNumeroCorrelativo());
                    summaryDocumentLine.setiD(iDType3);

                    /*
                     * Agregar el codigo del tipo de documento de la LINEA
                     * <sac:SummaryDocumentsLine><cbc:DocumentTypeCode>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el CODIGO del tipo de documento.");
                    }
                    DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                    documentTypeCode.setValue(transLine.getTipoDocumento());
                    summaryDocumentLine.setDocumentTypeCode(documentTypeCode);

                    CustomerPartyType customerPartyType = new CustomerPartyType();
                    CustomerAssignedAccountIDType customerAssignedAccountIDType = new CustomerAssignedAccountIDType();
                    customerAssignedAccountIDType.setValue(transLine.getDocIdentidad());
                    AdditionalAccountIDType additionalAccountIDType = new AdditionalAccountIDType();
                    additionalAccountIDType.setValue(transLine.getTipoDocIdentidad());
                    customerPartyType.setCustomerAssignedAccountID(customerAssignedAccountIDType);
                    customerPartyType.getAdditionalAccountID().add(additionalAccountIDType);
                    summaryDocumentLine.setCustomerParty(customerPartyType);

                    if (!transLine.getDocIdModificado().isEmpty()) {
                        BillingReferenceType billingReferenceType = new BillingReferenceType();
                        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
                        IDType iDType = new IDType();
                        iDType.setValue(transLine.getDocIdModificado());
                        documentReferenceType.setID(iDType);
                        DocumentTypeCodeType documentTypeCodeType = new DocumentTypeCodeType();
                        documentTypeCodeType.setValue(transLine.getTipoDocIdentidadModificado());
                        documentReferenceType.setDocumentTypeCode(documentTypeCodeType);
                        billingReferenceType.setInvoiceDocumentReference(documentReferenceType);
                        summaryDocumentLine.setBillingReference(billingReferenceType);
                    }

                    if (transLine.getMontoPercepcion().compareTo(BigDecimal.ZERO) > 0) {
                        SUNATPerceptionSummaryDocumentReference sUNATPerceptionSummaryDocumentReferenceType = new SUNATPerceptionSummaryDocumentReference();
                        SUNATPerceptionSystemCodeType sUNATPerceptionSystemCodeType = new SUNATPerceptionSystemCodeType();
                        sUNATPerceptionSystemCodeType.setValue((transLine.getRegimenPercepcion()));
                        sUNATPerceptionSummaryDocumentReferenceType.setsUNATPerceptionSystemCode(sUNATPerceptionSystemCodeType);

                        SUNATPerceptionPercentType sUNATPerceptionPercentType = new SUNATPerceptionPercentType();
                        sUNATPerceptionPercentType.setValue(((transLine.getTasaPercepcion()).setScale(2, BigDecimal.ROUND_HALF_UP)).toString());
                        sUNATPerceptionSummaryDocumentReferenceType.setsUNATPerceptionPercent(sUNATPerceptionPercentType);

                        TotalInvoiceAmountType totalInvoiceAmountType = new TotalInvoiceAmountType();
                        totalInvoiceAmountType.setValue((transLine.getMontoPercepcion()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType.valueOf(transLine.getCodMoneda()));
                        sUNATPerceptionSummaryDocumentReferenceType.setTotalInvoiceAmount(totalInvoiceAmountType);

                        SUNATTotalCashedType sUNATTotalCashedType = new SUNATTotalCashedType();
                        sUNATTotalCashedType.setValue((transLine.getMontoTotalCobrar().add(transLine.getMontoPercepcion())).setScale(2, BigDecimal.ROUND_HALF_UP));
                        sUNATTotalCashedType.setCurrencyID(CurrencyCodeContentType.valueOf(transLine.getCodMoneda()));
                        sUNATPerceptionSummaryDocumentReferenceType.setsUNATTotalCashed(sUNATTotalCashedType);

                        TaxableAmountType taxableAmountType = new TaxableAmountType();
                        taxableAmountType.setValue(transLine.getMontoTotalCobrar().setScale(2, BigDecimal.ROUND_HALF_UP));
                        taxableAmountType.setCurrencyID(CurrencyCodeContentType.valueOf(transLine.getCodMoneda()));

                        sUNATPerceptionSummaryDocumentReferenceType.setTaxableAmount(taxableAmountType);
                        summaryDocumentLine.setSUNATPerceptionSummaryDocumentReference(sUNATPerceptionSummaryDocumentReferenceType);

                    }

                    StatusType statusType = new StatusType();
                    ConditionCodeType conditionCodeType = new ConditionCodeType();
                    conditionCodeType.setValue(transLine.getEstado());
                    statusType.setConditionCode(conditionCodeType);
                    summaryDocumentLine.setStatus(statusType);
//                    StatusType statusType = new StatusType();
//                    ConditionCodeType conditionCodeType = new ConditionCodeType();
//                    conditionCodeType.setValue(transLine.getEstado());
//                    statusType.setConditionCode(conditionCodeType);
                    /*
                     * Agregar el INICIO del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:StartDocumentNumberID>
                     */
                    /*if (logger.isDebugEnabled()) {
                     logger.debug("getAllSummaryDocumentLines() ["
                     + this.identifier
                     + "] Agregando el INICIO del CORRELATIVO.");
                     }
                     IdentifierType startDocumentNumberID = new IdentifierType();
                     startDocumentNumberID.setValue(transLine
                     .getNumeroCorrelativoInicio());
                     summaryDocumentLine
                     .setStartDocumentNumberID(startDocumentNumberID);
                     */
                    /*
                     * Agregar el FIN del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:EndDocumentNumberID>
                     */
                    /*if (logger.isDebugEnabled()) {
                     logger.debug("getAllSummaryDocumentLines() ["
                     + this.identifier
                     + "] Agregando el FIN del CORRELATIVO.");
                     }
                     IdentifierType endDocumentNumberID = new IdentifierType();
                     endDocumentNumberID.setValue(transLine
                     .getNumeroCorrelativoFin());
                     summaryDocumentLine
                     .setEndDocumentNumberID(endDocumentNumberID);
                     */
                    /*
                     * Agregar el MONTO TOTAL de la LINEA
                     * <sac:SummaryDocumentsLine><sac:TotalAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el MONTO TOTAL de la LINEA.");
                    }
//                    AmountType totalAmount = new AmountType();
//                    totalAmount.setValue(transLine.getImporteTotal().setScale(
//                            2, RoundingMode.HALF_UP));
//                    totalAmount.setCurrencyID(CurrencyCodeContentType
//                            .valueOf(transLine.getCodMoneda()));
//                    summaryDocumentLine.setTotalAmount(totalAmount);

                    sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.TotalInvoiceAmountType totalInvoiceAmountType = new sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.TotalInvoiceAmountType();
                    totalInvoiceAmountType.setValue(transLine.getImporteTotal().setScale(2, RoundingMode.HALF_UP));
                    totalInvoiceAmountType.setCurrencyID(CurrencyCodeContentType.valueOf(transLine.getCodMoneda()));
                    summaryDocumentLine.setTotalAmount(totalInvoiceAmountType);
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
                    if (transLine.getTotalOPGravadas().compareTo(BigDecimal.ZERO) > 0) {
                        summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPGravadas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_GRAVADO));
                    }

                    if (transLine.getTotalOPExoneradas().compareTo(BigDecimal.ZERO) > 0) {
                        summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPExoneradas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_EXONERADO));
                    }
                    if (transLine.getTotalOPInafectas().compareTo(BigDecimal.ZERO) > 0) {
                        summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPInafectas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_INAFECTO));
                    }

                    if (transLine.getTotalOPGratuitas().compareTo(BigDecimal.ZERO) > 0) {
                        summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPGratuitas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_GRATUITAS));
                    }
                    /*
                     * Agregar Sumatoria de Otros Cargos
                     * <sac:SummaryDocumentsLine><cac:AllowanceCharge>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando sumatoria de OTROS CARGOS.");
                    }
                    if (transLine.getImporteOtrosCargos().compareTo(BigDecimal.ZERO) > 0) {
                        summaryDocumentLine.getAllowanceCharge().add(getAllowanceCharge(transLine.getImporteOtrosCargos(), transLine.getCodMoneda(), true));
                    }
                    /* Agregar MONTOS de IMPUESTOS IGV e ISC */
                    summaryDocumentLine.getTaxTotal().add(getTaxTotalForSummary(transLine.getTotaIGV(), transLine.getCodMoneda(), IUBLConfig.TAX_TOTAL_IGV_ID, IUBLConfig.TAX_TOTAL_IGV_NAME, IUBLConfig.TAX_TOTAL_IGV_CODE));
                    summaryDocumentLine.getTaxTotal().add(getTaxTotalForSummary(transLine.getTotalISC(), transLine.getCodMoneda(), IUBLConfig.TAX_TOTAL_ISC_ID, IUBLConfig.TAX_TOTAL_ISC_NAME, IUBLConfig.TAX_TOTAL_ISC_CODE));
                    summaryDocumentLineList.add(summaryDocumentLine);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllSummaryDocumentLines() [" + this.identifier + "]");
        }
        return summaryDocumentLineList;
    } // getAllSummaryDocumentLines

    private CustomizationIDType getCustomizationID11() {
        CustomizationIDType customizationID = new CustomizationIDType();
        customizationID.setValue(IUBLConfig.CUSTOMIZATION_ID1);
        return customizationID;
    } // getCustomizationID

    public SummaryDocumentsType generateSummaryDocumentsTypeV2(TransaccionResumen transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateSummaryDocumentsType() [" + this.identifier + "]");
        }
        SummaryDocumentsType summaryDocumentsType = null;
        String idTransaccion = transaction.getIdTransaccion();
        try {
            /* Instanciar objeto SummaryDocumentsType para el resumen diario */
            summaryDocumentsType = new SummaryDocumentsType();
            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() [" + this.identifier + "] Agregando TAG para colocar la FIRMA.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();

            ublExtensions.getUBLExtension().add(getUBLExtensionSigner());
            summaryDocumentsType.setUBLExtensions(ublExtensions);

            /* Agregar <SummaryDocuments><cbc:UBLVersionID> */
            summaryDocumentsType.setUBLVersionID(getUBLVersionID());

            /* Agregar <SummaryDocuments><cbc:CustomizationID> */
            summaryDocumentsType.setCustomizationID(getCustomizationID11());

            /* Agregar <SummaryDocuments><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() [" + this.identifier + "] Agregando IdTransaccion: " + idTransaccion);
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(idTransaccion);
            summaryDocumentsType.setID(idDocIdentifier);
            /* Agregar <SummaryDocuments><cbc:ReferenceDate> */
            System.out.println();
            System.out.println("*****************************************************************************************************************************");
            System.out.println(transaction.getFechaEmision());
            ReferenceDateType referenceDate2 = getReferenceDate2(transaction.getFechaEmision());
            System.out.println(referenceDate2);
            summaryDocumentsType.setReferenceDate(referenceDate2);
            /* Agregar <SummaryDocuments><cbc:IssueDate> */
            System.out.println(transaction.getFechaGeneracion());
            summaryDocumentsType.setIssueDate(getIssueDate8(transaction.getFechaGeneracion()));
            System.out.println("*****************************************************************************************************************************");
            System.out.println();
            /* Agregar <SummaryDocuments><cac:Signature> */
            SignatureType signature = generateSignature(transaction.getNumeroRuc(), transaction.getRazonSocial(), signerName);
            List<SignatureType> lstSignature = new ArrayList<>();
            lstSignature.add(signature);
            summaryDocumentsType.getSignature().addAll(lstSignature);
            /* Agregar <SummaryDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(transaction.getNumeroRuc(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            summaryDocumentsType.setAccountingSupplierParty(accountingSupplierParty);
            /* Agregar <SummaryDocuments><sac:SummaryDocumentsLine> */
            summaryDocumentsType.getSummaryDocumentsLine().addAll(getAllSummaryDocumentLinesV2(transaction.getTransaccionResumenLineaList()));
        } catch (UBLDocumentException e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_346, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateSummaryDocumentsType() [" + this.identifier + "]");
        }
        return summaryDocumentsType;
    } // generateSummaryDocumentsType

    private UBLVersionIDType getUBLVersionIDv21() {
        UBLVersionIDType ublVersionID = new UBLVersionIDType();
        ublVersionID.setValue(IUBLConfig.UBL_VERSION_ID_21);

        return ublVersionID;
    } // getUBLVersionID

    private CustomizationIDType getCustomizationID20() {
        CustomizationIDType customizationID = new CustomizationIDType();
        customizationID.setValue(IUBLConfig.CUSTOMIZATION_ID20);

        return customizationID;
    } // getCustomizationID

    public SummaryDocumentsType generateSummaryDocumentsType(TransaccionResumen transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateSummaryDocumentsType() [" + this.identifier + "]");
        }
        SummaryDocumentsType summaryDocumentsType = null;

        try {
            /* Instanciar objeto SummaryDocumentsType para el resumen diario */
            summaryDocumentsType = new SummaryDocumentsType();

            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() [" + this.identifier + "] Agregando TAG para colocar la FIRMA.");
            }
            UBLExtensionsType ublExtensions = new UBLExtensionsType();
            ublExtensions.getUBLExtension().add(getUBLExtensionSigner());
            summaryDocumentsType.setUBLExtensions(ublExtensions);

            /* Agregar <SummaryDocuments><cbc:UBLVersionID> */
            summaryDocumentsType.setUBLVersionID(getUBLVersionIDv21());

            /* Agregar <SummaryDocuments><cbc:CustomizationID> */
            summaryDocumentsType.setCustomizationID(getCustomizationID20());

            /* Agregar <SummaryDocuments><cbc:ID> */
            if (logger.isDebugEnabled()) {
                logger.debug("generateSummaryDocumentsType() [" + this.identifier + "] Agregando IdTransaccion: " + transaction.getIdTransaccion());
            }
            IDType idDocIdentifier = new IDType();
            idDocIdentifier.setValue(transaction.getIdTransaccion());
            summaryDocumentsType.setID(idDocIdentifier);

            /* Agregar <SummaryDocuments><cbc:ReferenceDate> */
            summaryDocumentsType.setReferenceDate(getReferenceDate2(transaction.getFechaEmision()));

            /* Agregar <SummaryDocuments><cbc:IssueDate> */
            summaryDocumentsType.setIssueDate(getIssueDate8(transaction.getFechaGeneracion()));

            /* Agregar <SummaryDocuments><cac:Signature> */
            SignatureType signature = generateSignature(transaction.getNumeroRuc(), transaction.getRazonSocial(), signerName);
            summaryDocumentsType.getSignature().add(signature);

            /* Agregar <SummaryDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = generateAccountingSupplierParty(transaction.getNumeroRuc(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(),
                    transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            summaryDocumentsType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <SummaryDocuments><sac:SummaryDocumentsLine> */
            summaryDocumentsType.getSummaryDocumentsLine().addAll(getAllSummaryDocumentLines(transaction.getTransaccionResumenLineaList()));
        } catch (UBLDocumentException e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateSummaryDocumentsType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_346, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateSummaryDocumentsType() [" + this.identifier + "]");
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

    private IssueDateType getIssueDate(Date issueDateValue) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        IssueDateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new IssueDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] " + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private IssueDateType getIssueDate8(String issueDateValue_1) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        System.out.println("************************************************************************************************************************");
        System.out.println("Mostrando la fecha a ver si esta bien de baja");
        LocalDate issueDateValue;
        try {
            issueDateValue = LocalDate.parse(issueDateValue_1, DateTimeFormatter.ofPattern(IUBLConfig.ISSUEDATE_FORMAT));
        } catch (Exception e) {
            issueDateValue = LocalDate.parse(issueDateValue_1, DateTimeFormatter.ofPattern(IUBLConfig.DOCUMENT_DATE_FORMAT));
        }
        System.out.println(issueDateValue);
        System.out.println("************************************************************************************************************************");
        IssueDateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            System.out.println(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            System.out.println(datatypeFact.newXMLGregorianCalendar(issueDateValue.toString()));
            System.out.println("************************************************************************************************************************");
            /* Agregando la fecha de emision <cbc:IssueDate> */

            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new IssueDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(issueDateValue.toString()));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            logger.error("getIssueDate() [" + this.identifier + "] " + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private DateType getIssueDate4(Date issueDateValue) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        DateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new DateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
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

    private PaidDateType getIssueDate2(Date issueDateValue) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        PaidDateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new PaidDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
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

    private SUNATPerceptionDateType getIssueDate3(Date issueDateValue) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        SUNATPerceptionDateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new SUNATPerceptionDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] " + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private SUNATRetentionDateType getIssueDate5(Date issueDateValue) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getIssueDate() [" + this.identifier + "]");
        }
        SUNATRetentionDateType issueDateType = null;
        try {
            if (null == issueDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_312);
            }
            SimpleDateFormat sdf = new SimpleDateFormat(IUBLConfig.ISSUEDATE_FORMAT);
            String date = sdf.format(issueDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            /* Agregando la fecha de emision <cbc:IssueDate> */
            issueDateType = new SUNATRetentionDateType();
            issueDateType.setValue(datatypeFact.newXMLGregorianCalendar(date));
        } catch (UBLDocumentException e) {
            logger.error("getIssueDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getIssueDate() [" + this.identifier + "] " + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_313);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getIssueDate() [" + this.identifier + "]");
        }
        return issueDateType;
    } // getIssueDate

    private ReferenceDateType getReferenceDate2(String referenceDateValue_1) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getReferenceDate() [" + this.identifier + "]");
        }
        System.out.println("************************************************************************************************************************");
        System.out.println("Mostrando la fecha a ver si esta bien de la fecha de referencia");
        LocalDate referenceDateValue;
        try {
            referenceDateValue = LocalDate.parse(referenceDateValue_1, DateTimeFormatter.ofPattern(IUBLConfig.REFERENCEDATE_FORMAT));
        } catch (Exception e) {
            referenceDateValue = LocalDate.parse(referenceDateValue_1, DateTimeFormatter.ofPattern(IUBLConfig.DOCUMENT_DATE_FORMAT));
        }
        ReferenceDateType referenceDateType = null;
        try {
            if (null == referenceDateValue) {
                throw new UBLDocumentException(IVenturaError.ERROR_340);
            }
            System.out.println(referenceDateValue);
            DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
            System.out.println(datatypeFact.newXMLGregorianCalendar(referenceDateValue.toString()));
            System.out.println("************************************************************************************************************************");
            /* Agregando la fecha de emision <cbc:IssueDate> */
            referenceDateType = new ReferenceDateType();
            referenceDateType.setValue(datatypeFact.newXMLGregorianCalendar(referenceDateValue.toString()));
        } catch (UBLDocumentException e) {
            logger.error("getReferenceDate() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            logger.error("getReferenceDate() [" + this.identifier + "] " + IVenturaError.ERROR_313.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_347);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getReferenceDate() [" + this.identifier + "]");
        }
        return referenceDateType;
    } // getReferenceDate

    private TaxSchemeType getTaxScheme(String taxTotalID, String taxTotalName, String taxTotalCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getTaxScheme() taxTotalID: " + taxTotalID + " taxTotalName: " + taxTotalName + " taxTotalCode: " + taxTotalCode);
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

    private List<SummaryDocumentsLineType> getAllSummaryDocumentLines(List<TransaccionResumenLinea> transactionLines) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllSummaryDocumentLines() [" + this.identifier + "] ");
        }
        List<SummaryDocumentsLineType> summaryDocumentLineList = null;

        if (null != transactionLines && !transactionLines.isEmpty()) {
            try {
                summaryDocumentLineList = new ArrayList<>(transactionLines.size());
                for (TransaccionResumenLinea transLine : transactionLines) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Extrayendo informacion del item...");
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] IdLinea: " + transLine.getTransaccionResumenLineaPK().getIdLinea() + "\nTipoDocument: " + transLine.getTipoDocumento() + "\tNumeroSerie: "
                                + transLine.getNumeroSerie() + "\tCorrelativoInicio: " + transLine.getNumeroCorrelativoInicio() + "\tCorrelativoFin: " + transLine.getNumeroCorrelativoFin() + "\nImporteTotal: " + transLine.getImporteTotal()
                                + "\tOpGravadas: " + transLine.getTotalOPGravadas() + "\tOpExoneradas: " + transLine.getTotalOPExoneradas() + "\tOpInafecatas: " + transLine.getTotalOPInafectas() + "\nOtrosCargos: "
                                + transLine.getImporteOtrosCargos() + "\tIGV: " + transLine.getTotaIGV() + "\tISC: " + transLine.getTotalISC() + "\tCodMoneda: " + transLine.getCodMoneda());
                    }
                    SummaryDocumentsLineType summaryDocumentLine = new SummaryDocumentsLineType();
                    /* Agregar <sac:SummaryDocumentsLine><cbc:LineID> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el LINEID.");
                    }
                    LineIDType lineID = new LineIDType();
                    lineID.setValue(String.valueOf(transLine.getTransaccionResumenLineaPK().getIdLinea()));
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
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando la SERIE a la LINEA.");
                    }
                    IdentifierType documentSerialID = new IdentifierType();
                    documentSerialID.setValue(transLine.getNumeroSerie());
                    summaryDocumentLine.setDocumentSerialID(documentSerialID);

                    /*
                     * Agregar el INICIO del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:StartDocumentNumberID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el INICIO del CORRELATIVO.");
                    }
                    IdentifierType startDocumentNumberID = new IdentifierType();
                    startDocumentNumberID.setValue(transLine.getNumeroCorrelativoInicio());
                    summaryDocumentLine.setStartDocumentNumberID(startDocumentNumberID);
                    /*
                     * Agregar el FIN del CORRELATIVO
                     * <sac:SummaryDocumentsLine><sac:EndDocumentNumberID>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el FIN del CORRELATIVO.");
                    }
                    IdentifierType endDocumentNumberID = new IdentifierType();
                    endDocumentNumberID.setValue(transLine.getNumeroCorrelativoFin());
                    summaryDocumentLine.setEndDocumentNumberID(endDocumentNumberID);
                    /*
                     * Agregar el MONTO TOTAL de la LINEA
                     * <sac:SummaryDocumentsLine><sac:TotalAmount>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando el MONTO TOTAL de la LINEA.");
                    }//                    AmountType totalAmount = new AmountType();
//                    totalAmount.setValue(transLine.getImporteTotal().setScale(
//                            2, RoundingMode.HALF_UP));
//                    totalAmount.setCurrencyID(CurrencyCodeContentType
//                            .valueOf(transLine.getCodMoneda()));
//                    summaryDocumentLine.setTotalAmount(totalAmount);
                    /*
                     * Agregar MONTOS de los tipos de operaciones GRAVADAS,
                     * EXONERADAS E INAFECTAS
                     * <sac:SummaryDocumentsLine><sac:BillingPayment>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando MONTOS DE tipos de OPERACIONES.");
                    }
                    summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPGravadas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_GRAVADO));
                    summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPExoneradas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_EXONERADO));
                    summaryDocumentLine.getBillingPayment().add(getBillingPayment(transLine.getTotalOPInafectas(), transLine.getCodMoneda(), IUBLConfig.INSTRUCTION_ID_INAFECTO));
                    /*
                     * Agregar Sumatoria de Otros Cargos
                     * <sac:SummaryDocumentsLine><cac:AllowanceCharge>
                     */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSummaryDocumentLines() [" + this.identifier + "] Agregando sumatoria de OTROS CARGOS.");
                    }
                    summaryDocumentLine.getAllowanceCharge().add(getAllowanceCharge(transLine.getImporteOtrosCargos(), transLine.getCodMoneda(), true));
                    /* Agregar MONTOS de IMPUESTOS IGV e ISC */
                    summaryDocumentLine.getTaxTotal().add(getTaxTotalForSummary(transLine.getTotaIGV(), transLine.getCodMoneda(), IUBLConfig.TAX_TOTAL_IGV_ID, IUBLConfig.TAX_TOTAL_IGV_NAME, IUBLConfig.TAX_TOTAL_IGV_CODE));
                    summaryDocumentLine.getTaxTotal().add(getTaxTotalForSummary(transLine.getTotalISC(), transLine.getCodMoneda(), IUBLConfig.TAX_TOTAL_ISC_ID, IUBLConfig.TAX_TOTAL_ISC_NAME, IUBLConfig.TAX_TOTAL_ISC_CODE));
                    summaryDocumentLineList.add(summaryDocumentLine);
                }
            } catch (UBLDocumentException e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
                logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
                throw new UBLDocumentException(IVenturaError.ERROR_320);
            }
        } else {
            logger.error("getAllSummaryDocumentLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllSummaryDocumentLines() [" + this.identifier + "]");
        }
        return summaryDocumentLineList;
    } // getAllSummaryDocumentLines

    private AllowanceChargeType getAllowanceCharge(BigDecimal amountVal, String currencyCode, boolean chargeIndicatorVal) throws UBLDocumentException {
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
            logger.error("getAllowanceCharge() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_327.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_327);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllowanceCharge() [" + this.identifier + "]");
        }
        return allowanceCharge;
    } // getAllowanceCharge

    private PaymentType getBillingPayment(BigDecimal amount, String currencyCode, String operationType) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getBillingPayment() [" + this.identifier + "] amount: " + amount + " operationType: " + operationType);
        }
        PaymentType paymentType = null;

        try {
            /*
             * Agregar
             * <sac:SummaryDocumentsLine><sac:BillingPayment><cbc:PaidAmount>
             */
            PaidAmountType paidAmount = new PaidAmountType();
            paidAmount.setValue(amount.setScale(2, RoundingMode.HALF_UP));
            paidAmount.setCurrencyID(CurrencyCodeContentType.valueOf(currencyCode));

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
            logger.error("getBillingPayment() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_349.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_349);
        }
        return paymentType;
    } // getBillingPayment

    private TaxTotalType getTaxTotalForSummary(BigDecimal taxAmountValue, String currencyCode, String schemeID, String schemeName, String schemeCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getTaxTotalForSummary() [" + this.identifier + "] taxAmountValue: " + taxAmountValue + " schemeID: " + schemeID);
        }
        TaxTotalType taxTotal = null;
        try {
            taxTotal = new TaxTotalType();

            /* Agregar <cac:TaxTotal><cbc:TaxAmount> */
            TaxAmountType taxAmount = new TaxAmountType();
            taxAmount.setValue(taxAmountValue.setScale(2, RoundingMode.HALF_UP));
            taxAmount.setCurrencyID(CurrencyCodeContentType.valueOf(currencyCode));
            TaxSubtotalType taxSubTotal = new TaxSubtotalType();
            /* Agregar <cac:TaxTotal><cac:TaxSubtotal><cbc:TaxAmount> */
            taxSubTotal.setTaxAmount(taxAmount);
            /* Agregar <cac:TaxTotal><cac:TaxSubtotal><cac:TaxCategory> */
            TaxCategoryType taxCategory = new TaxCategoryType();
            taxCategory.setTaxScheme(getTaxScheme(schemeID, schemeName, schemeCode));
            taxSubTotal.setTaxCategory(taxCategory);
            /*
             * Agregar los TAG's
             */
            taxTotal.setTaxAmount(taxAmount);
            taxTotal.getTaxSubtotal().add(taxSubTotal);
        } catch (Exception e) {
            logger.error("getTaxTotalForSummary() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_350.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_350);
        }
        return taxTotal;
    } // getTaxTotalForSummary

} // UBLDocumentHandler

