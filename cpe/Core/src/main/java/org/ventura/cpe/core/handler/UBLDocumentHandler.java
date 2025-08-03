package org.ventura.cpe.core.handler;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.*;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.UBLDocumentException;
import org.ventura.cpe.core.handler.basic.UBLBasicHandler;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.*;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;
import un.unece.uncefact.codelist.specification._54217._2001.CurrencyCodeContentType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.IdentifierType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.TextType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Esta clase HANDLER contiene metodos para generar objetos UBL, necesarios para
 * armar el documento UBL validado por Sunat.
 */
public class UBLDocumentHandler extends UBLBasicHandler {

    private final Logger logger = Logger.getLogger(UBLDocumentHandler.class);

    /**
     * Constructor privado para evitar la creacion de instancias usando el
     * constructor.
     *
     * @param identifier Identificador del objeto UBLDocumentHandler creado.
     */
    private UBLDocumentHandler(String identifier) {
        super(identifier);
    } //UBLDocumentHandler

    /**
     * Este metodo crea una nueva instancia de la clase UBLDocumentHandler.
     *
     * @param identifier Identificador del objeto UBLDocumentHandler creado.
     * @return Retorna una nueva instancia de la clase UBLDocumentHandler.
     */
    public static synchronized UBLDocumentHandler newInstance(String identifier) {
        return new UBLDocumentHandler(identifier);
    } //newInstance

    public InvoiceType generateInvoiceType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateInvoiceType() [" + this.identifier + "]");
        }
        InvoiceType invoiceType = null;
        try {
            /* Instanciar el objeto InvoiceType para la FACTURA */
            invoiceType = new InvoiceType();


            /* Agregar <Invoice><ext:UBLExtensions> */
            invoiceType.setUBLExtensions(getUBLExtensionsSigner());

            /* Agregar <Invoice><cbc:UBLVersionID> */
            invoiceType.setUBLVersionID(getUBLVersionID_2_1());

            /* Agregar <Invoice><cbc:CustomizationID> */
            invoiceType.setCustomizationID(getCustomizationID_2_0());

            /* Agregar <Invoice><cbc:ProfileID> */
            invoiceType.setProfileID(getProfileID(transaction.getTipoOperacionSunat()));
            Set<TransaccionContractdocref> transaccionContractdocrefList = transaction.getTransaccionContractdocrefs();
            Optional<TransaccionContractdocref> optional = transaccionContractdocrefList.parallelStream().filter(docRefer -> IUBLConfig.CONTRACT_DOC_REF_SELL_ORDER_CODE.equalsIgnoreCase(docRefer.getUsuariocampos().getNombre())).findAny();
            if (optional.isPresent()) {
                TransaccionContractdocref docRefer = optional.get();
                invoiceType.getContractDocumentReference().add(getContractDocumentReference(docRefer.getValor(), "OC"));
            }
//            if (!transaccionContractdocrefList.isEmpty()) {
//                for (TransaccionContractdocref transContractdocref : transaccionContractdocrefList) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS" + transContractdocref.getUsuariocampos().getNombre() + " :" + transContractdocref.getValor());
//                    }
//                    invoiceType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
//                }
//            }

            /* Agregar <Invoice><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoiceType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            invoiceType.setID(getID(transaction.getDOCId()));

            /* Agregar <Invoice><cbc:UUID> */
            invoiceType.setUUID(getUUID(this.identifier));

            /* Agregar <Invoice><cbc:IssueDate> */
            invoiceType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));

            /* Agregar <Invoice><cbc:IssueTime> */
            invoiceType.setIssueTime(getIssueTimeDefault());

            /* Agregar <Invoice><cbc:DueDate> */
            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                invoiceType.setDueDate(getDueDate(transaction.getDOCFechaVencimiento()));
            }

            /* Agregar <Invoice><cbc:InvoiceTypeCode> */
            invoiceType.setInvoiceTypeCode(getInvoiceTypeCode(transaction.getDOCCodigo(), transaction.getTipoOperacionSunat()));
            if (transaction.getTransaccionPropiedadesList().size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene PROPIEDADES.");
                }
                invoiceType.getNote().addAll(getNotesWithIfSentence(transaction.getTransaccionPropiedadesList()));
            }

            /* Agregar <Invoice><cbc:DocumentCurrencyCode> */
            invoiceType.setDocumentCurrencyCode(getDocumentCurrencyCode(transaction.getDOCMONCodigo()));

            /* Agregar <Invoice><cbc:LineCountNumeric> */
            Set<TransaccionLineas> transaccionLineas = transaction.getTransaccionLineas();
            invoiceType.setLineCountNumeric(getLineCountNumeric(transaccionLineas.size()));
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoiceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx LineCountNumeric(" + invoiceType.getLineCountNumeric() + ") xxxxxxxxxxxxxxxxxxx");
            }
            Set<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefs();
            for (TransaccionContractdocref contract : contractdocrefs) {
                if ("cu22".equalsIgnoreCase(contract.getUsuariocampos().getNombre())) {
                    String valorNote = contract.getValor();
                    Optional<String> optionalValor = Optional.ofNullable(valorNote).map(s -> s.isEmpty() ? null : s);
                    if (optionalValor.isPresent()) invoiceType.getNote().add(generateNote(valorNote));
                    break;
                }
            }
            for (TransaccionContractdocref contract : contractdocrefs) {
                if ("cu11".equalsIgnoreCase(contract.getUsuariocampos().getNombre())) {
                    String valorOrderRefence = contract.getValor();
                    Optional<String> optionalValor = Optional.ofNullable(valorOrderRefence).map(s -> s.isEmpty() ? null : s);
                    if (optionalValor.isPresent()) invoiceType.setOrderReference(generateOrderReferenceType(valorOrderRefence));
                    break;
                }
            }
            for (TransaccionContractdocref contract : contractdocrefs) {
                if ("cu08".equalsIgnoreCase(contract.getUsuariocampos().getNombre())) {
                    String valorDocumentReference = contract.getValor();
                    Optional<String> optionalValor = Optional.ofNullable(valorDocumentReference).map(s -> s.isEmpty() ? null : s);
                    if (optionalValor.isPresent()) invoiceType.getDespatchDocumentReference().add(generateDocumentReferenceType(valorDocumentReference));
                    break;
                }
            }
            BigDecimal monPercepcion = transaction.getDOCMonPercepcion();

            /*
             * Agregar las guias de remision
             *
             * <Invoice><cac:DespatchDocumentReference>
             */
            if (null != transaction.getTransaccionDocrefersList() && 0 < transaction.getTransaccionDocrefersList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene GUIAS DE REMISION.");
                }
                invoiceType.getDespatchDocumentReference().addAll(getDespatchDocumentReferences(transaction.getTransaccionDocrefersList()));
            }


            /*
             * Extraer la condicion de pago de ser el caso.
             */
 /*if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
             if (logger.isInfoEnabled()) {
             logger.info("generateInvoiceType() [" + this.identifier
             + "] Extraer la CONDICION DE PAGO.");
             }
             invoiceType.getContractDocumentReference().add(getContractDocumentReference(transaction.getDOCCondPago(),IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
             }*/

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
 /*if (logger.isDebugEnabled()) {
             logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
             }
             if (null != transaction.getTransaccionContractdocrefs() && 0 < transaction.getTransaccionContractdocrefs().size()) {
             for (TransaccionContractdocref transContractdocref : transaction.getTransaccionContractdocrefs()) {

             if (logger.isDebugEnabled()) {
             logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS" + transContractdocref.getUsuariocampos().getNombre() + " :" + transContractdocref.getValor());
             }
             invoiceType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
             }
             }*/
            /*
             * Agregar DEDUCCION DE ANTICIPOS
             */
            if (null != transaction.getANTICIPOMonto() && transaction.getANTICIPOMonto().compareTo(BigDecimal.ZERO) > 0 && null != transaction.getTransaccionAnticipoList() && 0 < transaction.getTransaccionAnticipoList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene informacion de ANTICIPOS.");
                }
                invoiceType.getAdditionalDocumentReference().addAll(getAdditionalDocumentReferences(transaction.getTransaccionAnticipoList(), transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo()));
            }
            //Esto es para CPPC
            boolean contieneCampoCppc = false;
            String valorCampoCppc = "";
            for (TransaccionContractdocref contract : contractdocrefs) {
                if ("cu38".equalsIgnoreCase(contract.getUsuariocampos().getNombre())) {
                    Optional<String> optionalCampo = Optional.ofNullable(contract.getValor()).map(valor -> valor.isEmpty() ? null : valor);
                    contieneCampoCppc = optionalCampo.isPresent();
                    if (optionalCampo.isPresent()) {
                        valorCampoCppc = optionalCampo.get();
                        break;
                    }
                }
            }
            if (contieneCampoCppc) {
                invoiceType.getAdditionalDocumentReference().add(generateCampoCppc(valorCampoCppc));
            }


            /* Agregar <Invoice><cac:Signature> */
            invoiceType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));

            /* Agregar <Invoice><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV21(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            invoiceType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <Invoice><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = getAccountingCustomerPartyV21(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRNomCalle(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getSNSegundoNombre(), transaction.getSNEMail());
            invoiceType.setAccountingCustomerParty(accountingCustomerParty);
            /*
             * Agregar DETRACCIONES
             */
            if (transaction.getMontoDetraccion().compareTo(BigDecimal.ZERO) > 0 && transaction.getPorcDetraccion().compareTo(BigDecimal.ZERO) > 0 && StringUtils.isNotBlank(transaction.getCuentaDetraccion())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene informacion de DETRACCIONES.");
                }
                invoiceType.getPaymentMeans().add(getPaymentMeans(transaction.getCuentaDetraccion(), transaction.getCodigoPago()));
                invoiceType.getPaymentTerms().add(getPaymentTerms(transaction.getCodigoDetraccion(), transaction.getMontoDetraccion(), transaction.getPorcDetraccion()));
            }

            /*
             * ANTICIPOS RELACIONADOS al comprobante de pago
             *
             * <Invoice><cac:PrepaidPayment>
             */
            if (null != transaction.getANTICIPOMonto() && transaction.getANTICIPOMonto().compareTo(BigDecimal.ZERO) > 0 && null != transaction.getTransaccionAnticipoList() && 0 < transaction.getTransaccionAnticipoList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene informacion de ANTICIPOS RELACIONADOS.");
                }
                invoiceType.getPrepaidPayment().addAll(getPrepaidPaymentV21(transaction.getTransaccionAnticipoList()));
            }
            /*
             * Agregar DESCUENTO GLOBAL
             */
            if (transaction.getDOCDescuento().compareTo(BigDecimal.ZERO) == 1) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] La transaccion contiene informacion de DESCUENTO GLOBAL");
                }
                invoiceType.getAllowanceCharge().add(getAllowanceCharge(false, transaction.getDOCPorDescuento(), transaction.getDOCDescuento(), transaction.getDOCImporteTotal(), transaction.getDOCMONCodigo(), "02"));
            }

            /* Agregar <Invoice><cac:TaxTotal> */
            invoiceType.getTaxTotal().add(getTaxTotalV21(transaction, transaction.getTransaccionImpuestosList(), transaction.getDOCImpuestoTotal(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoiceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TaxTotal - IMPUESTOS TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <Invoice><cac:LegalMonetaryTotal> */
            boolean noContainsFreeItem = false;
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                if (Objects.equals(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE, transaccionLinea.getPrecioRefCodigo())) {
                    noContainsFreeItem = true;
                    break;
                }
            }
            BigDecimal taxInclusiveAmount = transaction.getDOCSinPercepcion();
            BigDecimal payableAmount = transaction.getDOCMontoTotal();
            BigDecimal lineExtensionAmount = transaction.getDOCImporte();
            String socioDocIdentidad = transaction.getSNDocIdentidadTipo();
            BigDecimal otrosCargosValue = transaction.getDOCOtrosCargos();
            String formSap = transaction.getFEFormSAP();
            System.out.println("*************************************************************************");
            if (socioDocIdentidad.equalsIgnoreCase("0") && formSap.contains("exportacion")) {
                System.out.println("Entro a esta parte de la validacion");
                lineExtensionAmount = transaction.getDOCMontoTotal();
                taxInclusiveAmount = lineExtensionAmount;
                payableAmount = taxInclusiveAmount.add(otrosCargosValue);
            }
            System.out.println("*************************************************************************");
            invoiceType.setLegalMonetaryTotal(getMonetaryTotal(lineExtensionAmount, taxInclusiveAmount, noContainsFreeItem, otrosCargosValue, transaction.getANTICIPOMonto(), payableAmount, transaction.getDOCMONCodigo(), true));
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoiceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx LegalMonetaryTotal - IMPORTES TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <Invoice><cac:InvoiceLine> */
            invoiceType.getInvoiceLine().addAll(getAllInvoiceLines(transaccionLineas, transaction.getTransaccionPropiedadesList(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoiceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx InvoiceLines(" + invoiceType.getInvoiceLine().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
            if (Optional.ofNullable(monPercepcion).isPresent()) {
                if (monPercepcion.compareTo(BigDecimal.ZERO) > 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATPerceptionDocumentReferences() xxxxxxxxxxxxxxxxxxx");
                    }
                    invoiceType.setInvoiceTypeCode(createInvoiceTypeCode(transaction));
                    invoiceType.getNote().add(createNote("2000", "COMPROBANTE DE PERCEPCIÓN", null));
                    invoiceType.getPaymentTerms().add(createPaymentTerms(transaction));
                    invoiceType.getAllowanceCharge().add(createAllowanceChargeType(transaction));
                }
            }
        } catch (UBLDocumentException e) {
            logger.error("generateInvoiceType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateInvoiceType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_341, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateInvoiceType() [" + this.identifier + "]");
        }
        return invoiceType;
    } //generateInvoiceType

    private DocumentReferenceType generateCampoCppc(String valorCampoCppc) {
        DocumentReferenceType invoiceDocumentReference = new DocumentReferenceType();
        DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
        documentTypeCode.setValue(valorCampoCppc);
        invoiceDocumentReference.setDocumentTypeCode(documentTypeCode);
        return invoiceDocumentReference;
    }

    private OrderReferenceType generateOrderReferenceType(String valor) {
        OrderReferenceType orderReferenceType = new OrderReferenceType();
        IDType idType = new IDType();
        idType.setValue(valor);
        orderReferenceType.setID(idType);
        return orderReferenceType;
    }

    private DocumentReferenceType generateDocumentReferenceType(String valor) {
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        DocumentTypeCodeType codeType = new DocumentTypeCodeType();
        codeType.setValue("09");
        IDType idType = new IDType();
        idType.setValue(valor);
        documentReferenceType.setID(idType);
        documentReferenceType.setDocumentTypeCode(codeType);
        return documentReferenceType;
    }

    private NoteType generateNote(String valor) {
        NoteType noteType = new NoteType();
        noteType.setValue(valor);
        return noteType;
    }

    public InvoiceType generateBoletaType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateBoletaType() [" + this.identifier + "]");
        }
        InvoiceType boletaType = null;
        try {
            /* Instanciar el objeto InvoiceType para la BOLETA */
            boletaType = new InvoiceType();
            /* Agregar <Invoice><ext:UBLExtensions> */
            boletaType.setUBLExtensions(getUBLExtensionsSigner());
            /* Agregar <Invoice><cbc:UBLVersionID> */
            boletaType.setUBLVersionID(getUBLVersionID_2_1());
            /* Agregar <Invoice><cbc:CustomizationID> */
            boletaType.setCustomizationID(getCustomizationID_2_0());
            /* Agregar <Invoice><cbc:ProfileID> */
            boletaType.setProfileID(getProfileID(transaction.getTipoOperacionSunat()));
            /* Agregar <Invoice><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            boletaType.setID(getID(transaction.getDOCId()));
            /* Agregar <Invoice><cbc:UUID> */
            boletaType.setUUID(getUUID(this.identifier));
            /* Agregar <Invoice><cbc:IssueDate> */
            boletaType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));

            /* Agregar <Invoice><cbc:IssueTime> */
            boletaType.setIssueTime(getIssueTimeDefault());

            /* Agregar <Invoice><cbc:DueDate> */
            if (null != transaction.getDOCFechaVencimiento()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier + "] La transaccion contiene FECHA DE VENCIMIENTO.");
                }
                boletaType.setDueDate(getDueDate(transaction.getDOCFechaVencimiento()));
            }

            /* Agregar <Invoice><cbc:InvoiceTypeCode> */
            boletaType.setInvoiceTypeCode(getInvoiceTypeCode(transaction.getDOCCodigo(), transaction.getTipoOperacionSunat()));
            if (!transaction.getTransaccionPropiedadesList().isEmpty()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier + "] La transaccion contiene PROPIEDADES.");
                }
                boletaType.getNote().addAll(getNotes(transaction.getTransaccionPropiedadesList()));
            }

            /* Agregar <Invoice><cbc:DocumentCurrencyCode> */
            boletaType.setDocumentCurrencyCode(getDocumentCurrencyCode(transaction.getDOCMONCodigo()));

            /* Agregar <Invoice><cbc:LineCountNumeric> */
            Set<TransaccionLineas> transaccionLineas = transaction.getTransaccionLineas();
            boletaType.setLineCountNumeric(getLineCountNumeric(transaccionLineas.size()));
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx LineCountNumeric(" + boletaType.getLineCountNumeric() + ") xxxxxxxxxxxxxxxxxxx");
            }
            /*
             * Agregar las guias de remision
             *
             * <Invoice><cac:DespatchDocumentReference>
             */
            if (null != transaction.getTransaccionDocrefersList() && 0 < transaction.getTransaccionDocrefersList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier + "] La transaccion contiene GUIAS DE REMISION.");
                }
                boletaType.getDespatchDocumentReference().addAll(getDespatchDocumentReferences(transaction.getTransaccionDocrefersList()));
            }

            /*
             * Extraer la condicion de pago de ser el caso.
             */
            if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateInvoiceType() [" + this.identifier + "] Extraer la CONDICION DE PAGO.");
                }
                boletaType.getContractDocumentReference().add(getContractDocumentReference(transaction.getDOCCondPago(), IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
            }

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
            }
            Set<TransaccionContractdocref> transaccionContractdocrefList = transaction.getTransaccionContractdocrefs();
            if (!transaccionContractdocrefList.isEmpty()) {
                for (TransaccionContractdocref transContractdocref : transaccionContractdocrefList) {
                    boletaType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
                }
            }

            /*
             * Agregar DEDUCCION DE ANTICIPOS
             */
            if (null != transaction.getANTICIPOMonto() && transaction.getANTICIPOMonto().compareTo(BigDecimal.ZERO) > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier + "] La transaccion contiene informacion de ANTICIPOS.");
                }
                boletaType.getAdditionalDocumentReference().addAll(getAdditionalDocumentReferences(transaction.getTransaccionAnticipoList(), transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo()));
            }

            /* Agregar <Invoice><cac:Signature> */
            boletaType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));

            /* Agregar <Invoice><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV21(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            boletaType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <Invoice><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = getAccountingCustomerPartyV21(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRNomCalle(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getSNSegundoNombre(), transaction.getSNEMail());
            boletaType.setAccountingCustomerParty(accountingCustomerParty);

            /*
             * ANTICIPOS RELACIONADOS al comprobante de pago
             *
             * <Invoice><cac:PrepaidPayment>
             */
            if (null != transaction.getANTICIPOMonto() && transaction.getANTICIPOMonto().compareTo(BigDecimal.ZERO) > 0 && null != transaction.getTransaccionAnticipoList() && 0 < transaction.getTransaccionAnticipoList().size()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateBoletaType() [" + this.identifier + "] La transaccion contiene informacion de ANTICIPOS RELACIONADOS.");
                }
                boletaType.getPrepaidPayment().addAll(getPrepaidPaymentV21(transaction.getTransaccionAnticipoList()));
            }

            /* Agregar <Invoice><cac:TaxTotal> */
            boletaType.getTaxTotal().add(getTaxTotalV21(transaction, transaction.getTransaccionImpuestosList(), transaction.getDOCImpuestoTotal(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TaxTotal - IMPUESTOS TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <Invoice><cac:LegalMonetaryTotal> */
            boolean noContainsFreeItem = false;
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                if (Objects.equals(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE, transaccionLinea.getPrecioRefCodigo())) {
                    noContainsFreeItem = true;
                    break;
                }
            }
            boletaType.setLegalMonetaryTotal(getMonetaryTotal(transaction.getDOCImporte(), transaction.getDOCSinPercepcion(), noContainsFreeItem, transaction.getDOCOtrosCargos(), transaction.getANTICIPOMonto(), transaction.getDOCMontoTotal(), transaction.getDOCMONCodigo(), true));
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx LegalMonetaryTotal - IMPORTES TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <Invoice><cac:InvoiceLine> */
            boletaType.getInvoiceLine().addAll(getAllBoletaLines(transaccionLineas, transaction.getTransaccionPropiedadesList(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx InvoiceLines(" + boletaType.getInvoiceLine().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (UBLDocumentException e) {
            logger.error("generateBoletaType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateBoletaType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_342, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateBoletaType() [" + this.identifier + "]");
        }
        return boletaType;
    } //generateBoletaType

    public CreditNoteType generateCreditNoteType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateCreditNoteType() [" + this.identifier + "]");
        }
        CreditNoteType creditNoteType = null;
        try {
            /* Instanciar el objeto CreditNoteType para la NOTA DE CREDITO */
            creditNoteType = new CreditNoteType();

            /* Agregar <CreditNote><ext:UBLExtensions> */
            creditNoteType.setUBLExtensions(getUBLExtensionsSigner());

            /* Agregar <CreditNote><cbc:UBLVersionID> */
            creditNoteType.setUBLVersionID(getUBLVersionID_2_1());

            /* Agregar <CreditNote><cbc:CustomizationID> */
            creditNoteType.setCustomizationID(getCustomizationID_2_0());

            /* Agregar <CreditNote><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNoteType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            creditNoteType.setID(getID(transaction.getDOCId()));

            /* Agregar <CreditNote><cbc:UUID> */
            creditNoteType.setUUID(getUUID(this.identifier));

            /* Agregar <CreditNote><cbc:IssueDate> */
            creditNoteType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));

            /* Agregar <CreditNote><cbc:IssueTime> */
            creditNoteType.setIssueTime(getIssueTimeDefault());
            if (transaction.getTransaccionPropiedadesList().size() > 0) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateCreditNoteType() [" + this.identifier + "] La transaccion contiene PROPIEDADES.");
                }
                creditNoteType.getNote().addAll(getNotes(transaction.getTransaccionPropiedadesList()));
            }

            /* Agregar <CreditNote><cbc:DocumentCurrencyCode> */
            creditNoteType.setDocumentCurrencyCode(getDocumentCurrencyCode(transaction.getDOCMONCodigo()));

            /* Agregar <CreditNote><cac:DiscrepancyResponse> */
            creditNoteType.getDiscrepancyResponse().add(getDiscrepancyResponse(transaction.getREFDOCMotivCode(), IUBLConfig.LIST_NAME_CREDIT_NOTE_TYPE, IUBLConfig.URI_CATALOG_09, transaction.getREFDOCMotivDesc(), transaction.getREFDOCId()));

            /* Agregar <CreditNote><cac:BillingReference> */
            creditNoteType.getBillingReference().add(getBillingReference(transaction.getREFDOCId(), transaction.getREFDOCTipo()));


            /*
             * Agregar las guias de remision
             *
             * <Invoice><cac:DespatchDocumentReference>
             */
//            if (null != transaction.getTransaccionDocrefersList()
//                    && 0 < transaction.getTransaccionDocrefersList().size()) {
//                if (logger.isInfoEnabled()) {
//                    logger.info("generateInvoiceType() ["
//                            + this.identifier
//                            + "] Verificar si existen GUIAS DE REMISION en la FACTURA.");
//                }
//                creditNoteType.getDespatchDocumentReference().addAll(
//                        getDespatchDocumentReferences(transaction
//                                .getTransaccionDocrefersList()));
//                if (logger.isDebugEnabled()) {
//                    logger.debug("generateInvoiceType() ["
//                            + this.identifier
//                            + "] Se agregaron ["
//                            + creditNoteType.getDespatchDocumentReference()
//                            .size() + "] guias de remision.");
//                }
//            }

            /*
             * Extraer la condicion de pago de ser el caso.
             */
//            if (StringUtils.isNotBlank(transaction.getDOCCondPago())) {
//                if (logger.isInfoEnabled()) {
//                    logger.info("generateInvoiceType() [" + this.identifier
//                            + "] Extraer la CONDICION DE PAGO.");
//                }
//                creditNoteType.getContractDocumentReference().add(
//                        getContractDocumentReference(
//                                transaction.getDOCCondPago(),
//                                IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));
//            }

            /*
             * Extraer la orden de venta y nombre del vendedor de ser el caso
             */
//            if (logger.isDebugEnabled()) {
//                logger.debug("generateInvoiceType() [" + this.identifier + "] CAMPOS PERSONALIZADOS ");
//            }
//            
//            if (null != transaction.getTransaccionContractdocrefs() && 0 < transaction.getTransaccionContractdocrefs().size()) {
//                for (TransaccionContractdocref transContractdocref : transaction.getTransaccionContractdocrefs()) {
//                    creditNoteType.getContractDocumentReference().add(getContractDocumentReference(transContractdocref.getValor(), transContractdocref.getUsuariocampos().getNombre()));
//                }
//            }
            /* Agregar <CreditNote><cac:Signature> */
            creditNoteType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));

            /* Agregar <CreditNote><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV21(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            creditNoteType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <CreditNote><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = getAccountingCustomerPartyV21(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRNomCalle(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getSNSegundoNombre(), transaction.getSNEMail());
            creditNoteType.setAccountingCustomerParty(accountingCustomerParty);

            /* Agregar <CreditNote><cac:TaxTotal> */
            creditNoteType.getTaxTotal().add(getTaxTotalV21(transaction, transaction.getTransaccionImpuestosList(), transaction.getDOCImpuestoTotal(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TaxTotal - IMPUESTOS TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <CreditNote><cac:LegalMonetaryTotal> */
            creditNoteType.setLegalMonetaryTotal(getMonetaryTotal(transaction.getDOCImporte(), transaction.getDOCSinPercepcion(), false, transaction.getDOCOtrosCargos(), null, transaction.getDOCMontoTotal(), transaction.getDOCMONCodigo(), false));
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx LegalMonetaryTotal - IMPORTES TOTALES xxxxxxxxxxxxxxxxxxx");
            }
            BigDecimal monPercepcion = transaction.getDOCMonPercepcion();
            if (Optional.ofNullable(monPercepcion).isPresent()) {
                if (monPercepcion.compareTo(BigDecimal.ZERO) > 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATPerceptionDocumentReferences() xxxxxxxxxxxxxxxxxxx");
                    }
                    creditNoteType.getNote().add(createNote("2000", "COMPROBANTE DE PERCEPCIÓN", null));
                    creditNoteType.getAllowanceCharge().add(createAllowanceChargeType(transaction));
                }
            }

            /*
             * En el TAG LineExtensionAmount es donde va el SUBTOTAL segun SUNAT, por lo tanto:
             * transaction.getDOCImporte() = SUBTOTAL
             * ¡¡CONSULTAR!!
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateCreditNoteType() [" + this.identifier + "] Obteniendo el SUBTOTAL");
            }
            BigDecimal subtotalValue = getSubtotalValueFromTransaction(transaction.getTransaccionTotalesList());
            if (subtotalValue == null) {
                subtotalValue = BigDecimal.ZERO;
            }
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx Subtotal: " + subtotalValue);
            }
//
//            /* Agregar el SUBTOTAL en este TAG */
//            PayableRoundingAmountType payableRoundingAmount = new PayableRoundingAmountType();
//            payableRoundingAmount.setValue(subtotalValue);
//            payableRoundingAmount.setCurrencyID(CurrencyCodeContentType.valueOf(transaction.getDOCMONCodigo()).value());
//
//            creditNoteType.getLegalMonetaryTotal().setPayableRoundingAmount(payableRoundingAmount);


            /* Agregar <CreditNote><cac:CreditNoteLine> */
            creditNoteType.getCreditNoteLine().addAll(getAllCreditNoteLines(transaction.getTransaccionLineas(), transaction.getTransaccionPropiedadesList(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx CreditNoteLines(" + creditNoteType.getCreditNoteLine().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (UBLDocumentException e) {
            logger.error("generateCreditNoteType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateCreditNoteType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_343, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateCreditNoteType() [" + this.identifier + "]");
        }
        return creditNoteType;
    } //generateCreditNoteType

    public DebitNoteType generateDebitNoteType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateDebitNoteType() [" + this.identifier + "]");
        }
        DebitNoteType debitNoteType = null;
        try {
            /* Instanciar el objeto DebitNoteType para la NOTA DE DEBITO */
            debitNoteType = new DebitNoteType();


            /* Agregar <DebitNote><ext:UBLExtensions> */
            debitNoteType.setUBLExtensions(getUBLExtensionsSigner());

            /* Agregar <DebitNote><cbc:UBLVersionID> */
            debitNoteType.setUBLVersionID(getUBLVersionID_2_1());

            /* Agregar <DebitNote><cbc:CustomizationID> */
            debitNoteType.setCustomizationID(getCustomizationID_2_0());

            /* Agregar <DebitNote><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateDebitNoteType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            debitNoteType.setID(getID(transaction.getDOCId()));

            /* Agregar <DebitNote><cbc:UUID> */
            debitNoteType.setUUID(getUUID(this.identifier));

            /* Agregar <DebitNote><cbc:IssueDate> */
            debitNoteType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));

            /* Agregar <DebitNote><cbc:IssueTime> */
            debitNoteType.setIssueTime(getIssueTimeDefault());
            if (!transaction.getTransaccionPropiedadesList().isEmpty()) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateDebitNoteType() [" + this.identifier + "] La transaccion contiene PROPIEDADES.");
                }
                debitNoteType.getNote().addAll(getNotes(transaction.getTransaccionPropiedadesList()));
            }

            /* Agregar <DebitNote><cbc:DocumentCurrencyCode> */
            debitNoteType.setDocumentCurrencyCode(getDocumentCurrencyCode(transaction.getDOCMONCodigo()));

            /* Agregar <DebitNote><cac:DiscrepancyResponse> */
            debitNoteType.getDiscrepancyResponse().add(getDiscrepancyResponse(transaction.getREFDOCMotivCode(), IUBLConfig.LIST_NAME_DEBIT_NOTE_TYPE, IUBLConfig.URI_CATALOG_10, transaction.getREFDOCMotivDesc(), transaction.getREFDOCId()));

            /* Agregar <DebitNote><cac:BillingReference> */
            debitNoteType.getBillingReference().add(getBillingReference(transaction.getREFDOCId(), transaction.getREFDOCTipo()));

            /* Agregar <DebitNote><cac:Signature> */
            debitNoteType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));

            /* Agregar <DebitNote><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV21(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            debitNoteType.setAccountingSupplierParty(accountingSupplierParty);

            /* Agregar <DebitNote><cac:AccountingCustomerParty> */
            CustomerPartyType accountingCustomerParty = getAccountingCustomerPartyV21(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRNomCalle(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getSNSegundoNombre(), transaction.getSNEMail());
            debitNoteType.setAccountingCustomerParty(accountingCustomerParty);

            /* Agregar <DebitNote><cac:TaxTotal> */
            debitNoteType.getTaxTotal().add(getTaxTotalV21(transaction, transaction.getTransaccionImpuestosList(), transaction.getDOCImpuestoTotal(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateDebitNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TaxTotal - IMPUESTOS TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /* Agregar <DebitNote><cac:RequestedMonetaryTotal> */
            debitNoteType.setRequestedMonetaryTotal(getMonetaryTotal(transaction.getDOCImporte(), transaction.getDOCSinPercepcion(), false, transaction.getDOCOtrosCargos(), null, transaction.getDOCMontoTotal(), transaction.getDOCMONCodigo(), false));
            if (logger.isInfoEnabled()) {
                logger.info("generateDebitNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx RequestedMonetaryTotal - IMPORTES TOTALES xxxxxxxxxxxxxxxxxxx");
            }

            /*
             * En el TAG LineExtensionAmount es donde va el SUBTOTAL segun SUNAT, por lo tanto:
             * transaction.getDOCImporte() = SUBTOTAL
             * ¡¡CONSULTAR!!
             */
            if (logger.isDebugEnabled()) {
                logger.debug("generateDebitNoteType() [" + this.identifier + "] Obteniendo el SUBTOTAL");
            }
            BigDecimal subtotalValue = getSubtotalValueFromTransaction(transaction.getTransaccionTotalesList());
            if (subtotalValue == null) {
                subtotalValue = BigDecimal.ZERO;
            }
            if (logger.isInfoEnabled()) {
                logger.info("generateDebitNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx Subtotal: " + subtotalValue);
            }
//            /* Agregar el SUBTOTAL en este TAG */
//            PayableRoundingAmountType payableRoundingAmount = new PayableRoundingAmountType();
//            payableRoundingAmount.setValue(subtotalValue);
//            payableRoundingAmount.setCurrencyID(CurrencyCodeContentType.valueOf(transaction.getDOCMONCodigo()).value());
//            debitNoteType.getRequestedMonetaryTotal().setPayableRoundingAmount(payableRoundingAmount);

            /* Agregar items <DebitNote><cac:DebitNoteLine> */
            debitNoteType.getDebitNoteLine().addAll(getAllDebitNoteLines(transaction.getTransaccionLineas(), transaction.getTransaccionPropiedadesList(), transaction.getDOCMONCodigo()));
            if (logger.isInfoEnabled()) {
                logger.info("generateDebitNoteType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx DebitNoteLines(" + debitNoteType.getDebitNoteLine().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (UBLDocumentException e) {
            logger.error("generateDebitNoteType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateDebitNoteType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_344, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateDebitNoteType() [" + this.identifier + "]");
        }
        return debitNoteType;
    } //generateDebitNoteType

    public PerceptionType generatePerceptionType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generatePerceptionType() [" + this.identifier + "]");
        }
        PerceptionType perceptionType = null;
        try {
            /* Instanciar el objeto PerceptionType para la PERCEPCION */
            perceptionType = new PerceptionType();


            /* Agregar <Perception><ext:UBLExtensions> */
            perceptionType.setUblExtensions(getUBLExtensionsSigner());

            /* Agregar <Perception><cbc:UBLVersionID> */
            perceptionType.setUblVersionID(getUBLVersionID_2_0());

            /* Agregar <Perception><cbc:CustomizationID> */
            perceptionType.setCustomizationID(getCustomizationID_1_0());

            /* Agregar <Perception><cac:Signature> */
            perceptionType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));

            /* Agregar <Perception><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            perceptionType.setId(getID(transaction.getDOCId()));

            /* Agregar <Perception><cbc:IssueDate> */
            perceptionType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));

            /* Agregar <Perception><cac:AgentParty> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx AgentParty - EMISOR xxxxxxxxxxxxxxxxxxx");
            }
            PartyType agentParty = getAgentParty(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            perceptionType.setAgentParty(agentParty);

            /* Agregar <Perception><cac:ReceiverParty> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx ReceiverParty - RECEPTOR xxxxxxxxxxxxxxxxxxx");
            }
            PartyType receiverParty = getAgentParty(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRDireccion(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getPersonContacto(), transaction.getSNEMail());
            perceptionType.setReceiverParty(receiverParty);

            /* Agregar <Perception><sac:SUNATPerceptionSystemCode> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATPerceptionSystemCode - REGIMEN DE PERCEPCION xxxxxxxxxxxxxxxxxxx");
            }
            SUNATPerceptionSystemCodeType sunatPerceptionSystemCode = new SUNATPerceptionSystemCodeType();
            sunatPerceptionSystemCode.setValue(transaction.getRETRegimen());
            perceptionType.setSunatPerceptionSystemCode(sunatPerceptionSystemCode);

            /* Agregar <Perception><sac:SUNATPerceptionPercent> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATRetentionPercent - TASA DE PERCEPCION xxxxxxxxxxxxxxxxxxx");
            }
            SUNATPerceptionPercentType sunatPerceptionPercent = new SUNATPerceptionPercentType();
            sunatPerceptionPercent.setValue(transaction.getRETTasa());
            perceptionType.setSunatPerceptionPercent(sunatPerceptionPercent);

            /* Agregar <Perception><cbc:Note> */
            if (StringUtils.isNotBlank(transaction.getObservaciones())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generatePerceptionType() [" + this.identifier + "] Agregando Observaciones: " + transaction.getObservaciones());
                }
                perceptionType.setNote(getNote(transaction.getObservaciones()));
            }

            /* Agregar <Perception><cbc:TotalInvoiceAmount> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TotalInvoiceAmount - IMPORTE TOTAL PERCIBIDO xxxxxxxxxxxxxxxxxxx");
            }
            perceptionType.setTotalInvoiceAmount(getTotalInvoiceAmount(transaction.getDOCMontoTotal(), transaction.getDOCMONCodigo()));

            /* Agregar <Perception><sac:SUNATTotalCashed> */
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATTotalCashed - IMPORTE TOTAL COBRADO xxxxxxxxxxxxxxxxxxx");
            }
            SUNATTotalCashedType sunatTotalCashed = new SUNATTotalCashedType();
            sunatTotalCashed.setValue(transaction.getImportePagado().setScale(2, BigDecimal.ROUND_HALF_UP));
            sunatTotalCashed.setCurrencyID(CurrencyCodeContentType.valueOf(transaction.getMonedaPagado()).value());
            perceptionType.setSunatTotalCashed(sunatTotalCashed);

            /* Agregar <Perception><sac:SUNATPerceptionDocumentReference> */
            perceptionType.getSunatPerceptionDocumentReference().addAll(getAllSUNATPerceptionDocumentReferences(transaction.getTransaccionComprobantePagos()));
            if (logger.isInfoEnabled()) {
                logger.info("generatePerceptionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATPerceptionDocumentReferences(" + perceptionType.getSunatPerceptionDocumentReference().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (UBLDocumentException e) {
            logger.error("generatePerceptionType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generatePerceptionType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_351, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generatePerceptionType() [" + this.identifier + "]");
        }
        return perceptionType;
    } //generatePerceptionType

    public RetentionType generateRetentionType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateRetentionType() [" + this.identifier + "]");
        }
        RetentionType retentionType = null;
        try {
            /* Instanciar el objeto RetentionType para la RETENCION */
            retentionType = new RetentionType();
            /* Agregar <Retention><ext:UBLExtensions> */
            retentionType.setUblExtensions(getUBLExtensionsSigner());
            /* Agregar <Retention><cbc:UBLVersionID> */
            retentionType.setUblVersionID(getUBLVersionID_2_0());
            /* Agregar <Retention><cbc:CustomizationID> */
            retentionType.setCustomizationID(getCustomizationID_1_0());
            /* Agregar <Retention><cac:Signature> */
            retentionType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));
            /* Agregar <Retention><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            retentionType.setId(getID(transaction.getDOCId()));
            /* Agregar <Retention><cbc:IssueDate> */
            retentionType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));
            /* Agregar <Retention><cac:AgentParty> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx AgentParty - EMISOR xxxxxxxxxxxxxxxxxxx");
            }
            PartyType agentParty = getAgentParty(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial(), transaction.getDIRDireccion(), transaction.getDIRDepartamento(), transaction.getDIRProvincia(), transaction.getDIRDistrito(), transaction.getDIRUbigeo(), transaction.getDIRPais(), transaction.getPersonContacto(), transaction.getEMail());
            retentionType.setAgentParty(agentParty);
            /* Agregar <Retention><cac:ReceiverParty> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx ReceiverParty - RECEPTOR xxxxxxxxxxxxxxxxxxx");
            }
            PartyType receiverParty = getReceiverParty(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial(), transaction.getSNNombreComercial(), transaction.getSNDIRDireccion(), transaction.getSNDIRDepartamento(), transaction.getSNDIRProvincia(), transaction.getSNDIRDistrito(), transaction.getSNDIRUbigeo(), transaction.getSNDIRPais(), transaction.getPersonContacto(), transaction.getSNEMail());
            retentionType.setReceiverParty(receiverParty);
            /* Agregar <Retention><sac:SUNATRetentionSystemCode> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATRetentionSystemCode - REGIMEN DE RETENCION xxxxxxxxxxxxxxxxxxx");
            }
            SUNATRetentionSystemCodeType sunatRetentionSystemCode = new SUNATRetentionSystemCodeType();
            sunatRetentionSystemCode.setValue(transaction.getRETRegimen());
            retentionType.setSunatRetentionSystemCode(sunatRetentionSystemCode);
            /* Agregar <Retention><sac:SUNATRetentionPercent> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATRetentionPercent - TASA DE RETENCION xxxxxxxxxxxxxxxxxxx");
            }
            SUNATRetentionPercentType sunatRetentionPercent = new SUNATRetentionPercentType();
            sunatRetentionPercent.setValue(transaction.getRETTasa());
            retentionType.setSunatRetentionPercent(sunatRetentionPercent);
            /* Agregar <Retention><cbc:Note> */
            retentionType.setNote(getNote(transaction.getObservaciones()));
            /* Agregar <Retention><cbc:TotalInvoiceAmount> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx TotalInvoiceAmount - IMPORTE TOTAL RETENIDO xxxxxxxxxxxxxxxxxxx");
            }
            retentionType.setTotalInvoiceAmount(getTotalInvoiceAmount(transaction.getDOCMontoTotal(), transaction.getDOCMONCodigo()));
            /* Agregar <Retention><sac:SUNATTotalPaid> */
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATTotalPaid - IMPORTE TOTAL PAGADO xxxxxxxxxxxxxxxxxxx");
            }
            SUNATTotalPaid sunatTotalPaid = new SUNATTotalPaid();
            sunatTotalPaid.setValue(transaction.getImportePagado().setScale(2, BigDecimal.ROUND_HALF_UP));
            sunatTotalPaid.setCurrencyID(CurrencyCodeContentType.valueOf(transaction.getMonedaPagado()).value());
            retentionType.setTotalPaid(sunatTotalPaid);
            /* Agregar <Retention><sac:SUNATRetentionDocumentReference> */
            retentionType.getSunatRetentionDocumentReference().addAll(getAllSUNATRetentionDocumentReferences(transaction.getTransaccionComprobantePagos()));
            if (logger.isInfoEnabled()) {
                logger.info("generateRetentionType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx SUNATRetentionDocumentReferences(" + retentionType.getSunatRetentionDocumentReference().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (UBLDocumentException e) {
            logger.error("generateRetentionType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateRetentionType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_352, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateRetentionType() [" + this.identifier + "]");
        }
        return retentionType;
    } //generateRetentionType

    public DespatchAdviceType generateDespatchAdviceType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateDespatchAdviceType() [" + this.identifier + "]");
        }
        DespatchAdviceType despatchAdviceType = null;
        try {
            /* Instanciar el objeto DespatchAdviceType para la GUIA DE REMISION */
            despatchAdviceType = new DespatchAdviceType();
            /* Agregar <DespatchAdvice><ext:UBLExtensions> */
            despatchAdviceType.setUBLExtensions(getUBLExtensionsSigner());
            /* Agregar <<DespatchAdvice><cbc:UBLVersionID> */
            despatchAdviceType.setUBLVersionID(getUBLVersionID_2_1());
            /* Agregar <<DespatchAdvice><cbc:CustomizationID> */
            despatchAdviceType.setCustomizationID(getCustomizationID_1_0());
            /* Agregar <DespatchAdvice><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateDespatchAdviceType() [" + this.identifier + "] Agregando DOC_Id: " + transaction.getDOCId());
            }
            despatchAdviceType.setID(getID(transaction.getDOCId()));
            /* Agregar <DespatchAdvice><cbc:IssueDate> */
            despatchAdviceType.setIssueDate(getIssueDate(transaction.getDOCFechaEmision()));
            /* Agregar <DespatchAdvice><cbc:IssueTime> */
            despatchAdviceType.setIssueTime(getIssueTimeDefault());
            /* Agregar <DespatchAdvice><cbc:DespatchAdviceTypeCode> */
            despatchAdviceType.setDespatchAdviceTypeCode(getDespatchAdviceTypeCode(transaction.getDOCCodigo()));

            if(transaction.getTransaccionGuiaRemision().getCodigoMotivo()!=null || transaction.getTransaccionGuiaRemision().getCodigoMotivo().isEmpty()){
                if(transaction.getTransaccionGuiaRemision().getCodigoMotivo().equals("08"))
                despatchAdviceType.setAdditionalDocumentReference(generateDocumentReferenceTypeGuias(
                        transaction.getTransaccionDocrefersList().stream().filter(o -> o.getId().equals(transaction.getDOCId())).findAny().get()));
            }

            /* Agregar <DespatchAdvice><cbc:NoteType> */
            if (StringUtils.isNotBlank(transaction.getObservaciones())) {
                if (logger.isInfoEnabled()) {
                    logger.info("generateDespatchAdviceType() [" + this.identifier + "] Agregando Observaciones: " + transaction.getObservaciones());
                }
                despatchAdviceType.getNote().add(getNote(transaction.getObservaciones()));
            }
            /* Agregar <DespatchAdvice><cac:Signature> */
            despatchAdviceType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));
            /* Agregar <DespatchAdvice><cac:DespatchSupplierParty> */
            despatchAdviceType.setDespatchSupplierParty(getDespatchSupplierParty(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial()));
            if (logger.isInfoEnabled()) {
                logger.info("generateDespatchAdviceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx DespatchSupplierParty - DATOS DEL REMITENTE xxxxxxxxxxxxxxxxxxx");
            }
            /* Agregar <DespatchAdvice><cac:DeliveryCustomerParty> */
            despatchAdviceType.setDeliveryCustomerParty(getDeliveryCustomerParty(transaction.getSNDocIdentidadNro(), transaction.getSNDocIdentidadTipo(), transaction.getSNRazonSocial()));
            if (logger.isInfoEnabled()) {
                logger.info("generateDespatchAdviceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx DespatchSupplierParty - DATOS DEL DESTINATARIO xxxxxxxxxxxxxxxxxxx");
            }
            /* Agregar <DespatchAdvice><cac:Shipment> */
            despatchAdviceType.setShipment(getShipment(transaction.getTransaccionGuiaRemision(), transaction.getSNDIRUbigeo(), transaction.getSNDIRDireccion()));
            /* Agregar <DespatchAdvice><cac:DespatchLine> */
            despatchAdviceType.getDespatchLine().addAll(getAllDespatchLines(transaction.getTransaccionLineas()));
            if (logger.isInfoEnabled()) {
                logger.info("generateDespatchAdviceType() [" + this.identifier + "] xxxxxxxxxxxxxxxxxxx DespatchLines(" + despatchAdviceType.getDespatchLine().size() + ") xxxxxxxxxxxxxxxxxxx");
            }
        } catch (Exception e) {
            logger.error("generateDespatchAdviceType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_369, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateDespatchAdviceType() [" + this.identifier + "]");
        }
        return despatchAdviceType;
    } //generateDespatchAdviceType


    private DocumentReferenceType generateDocumentReferenceTypeGuias(TransaccionDocrefers docrefers) {
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        DocumentTypeCodeType codeType = new DocumentTypeCodeType();
        codeType.setValue(docrefers.getTipo());
        IDType idType = new IDType();
        idType.setValue(docrefers.getId());
        documentReferenceType.setID(idType);
        documentReferenceType.setDocumentTypeCode(codeType);
        return documentReferenceType;
    }


    public VoidedDocumentsType generateVoidedDocumentType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateVoidedDocumentType() [" + this.identifier + "]");
        }
        VoidedDocumentsType voidedDocumentType = null;
        try {
            /* Instanciar el objeto VoidedDocumentsType para la COMUNICACION DE BAJA */
            voidedDocumentType = new VoidedDocumentsType();
            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            voidedDocumentType.setUBLExtensions(getUBLExtensionsSigner());
            /* Agregar <VoidedDocuments><cbc:UBLVersionID> */
            voidedDocumentType.setUBLVersionID(getUBLVersionID_2_0());
            /* Agregar <VoidedDocuments><cbc:CustomizationID> */
            voidedDocumentType.setCustomizationID(getCustomizationID_1_0());
            /* Agregar <VoidedDocuments><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateVoidedDocumentType() [" + this.identifier + "] Agregando ANTICIPOId: " + transaction.getANTICIPOId());
            }
            voidedDocumentType.setID(getID(transaction.getANTICIPOId()));
            /* Agregar <VoidedDocuments><cbc:ReferenceDate> */
//            LocalDateTime date = LocalDateTime.now() /*LocalDateTime.of(2019, Month.OCTOBER, 3, 1, 1)*/;
            Date fecha = new Date();
            voidedDocumentType.setReferenceDate(getReferenceDate(transaction.getDOCFechaEmision()));
            /* Agregar <VoidedDocuments><cbc:IssueDate> */
            voidedDocumentType.setIssueDate(getIssueDate(fecha));
            /* Agregar <VoidedDocuments><cac:Signature> */
            voidedDocumentType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));
            /* Agregar <VoidedDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV20(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial());
            voidedDocumentType.setAccountingSupplierParty(accountingSupplierParty);
            /* Agregar item <VoidedDocuments><sac:VoidedDocumentsLine> */
            {
                /*
                 * Para este caso solo se agrega un solo ITEM, porque se esta
                 * dando de BAJA una transaccion.
                 */
                VoidedDocumentsLineType voidedDocumentLine = new VoidedDocumentsLineType();
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><cbc:LineID> */
                LineIDType lineID = new LineIDType();
                lineID.setValue("1");
                voidedDocumentLine.setLineID(lineID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><cbc:DocumentTypeCode> */
                DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                documentTypeCode.setValue(transaction.getDOCCodigo());
                voidedDocumentLine.setDocumentTypeCode(documentTypeCode);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentSerialID> */
                IdentifierType documentSerialID = new IdentifierType();
                documentSerialID.setValue(transaction.getDOCSerie());
                voidedDocumentLine.setDocumentSerialID(documentSerialID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentNumberID> */
                IdentifierType documentNumberID = new IdentifierType();
                documentNumberID.setValue(transaction.getDOCNumero());
                voidedDocumentLine.setDocumentNumberID(documentNumberID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:VoidReasonDescription> */
                TextType voidReasonDescription = new TextType();
                voidReasonDescription.setValue(transaction.getFEComentario());
                voidedDocumentLine.setVoidReasonDescription(voidReasonDescription);
                voidedDocumentType.getVoidedDocumentsLine().add(voidedDocumentLine);
            }
        } catch (UBLDocumentException e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateVoidedDocumentType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_345, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateVoidedDocumentType() [" + this.identifier + "]");
        }
        return voidedDocumentType;
    } //generateVoidedDocumentType

    public VoidedDocumentsType generateReversionDocumentType(Transaccion transaction, String signerName) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateReversionDocumentType() [" + this.identifier + "]");
        }
        VoidedDocumentsType voidedDocumentType = null;
        try {
            /*
             * Instanciar el objeto VoidedDocumentsType para el RESUMEN DIARIO DE
             * REVERSIONES DEL CRE
             */
            voidedDocumentType = new VoidedDocumentsType();
            /* Agregar <VoidedDocuments><ext:UBLExtensions> */
            voidedDocumentType.setUBLExtensions(getUBLExtensionsSigner());
            /* Agregar <VoidedDocuments><cbc:UBLVersionID> */
            voidedDocumentType.setUBLVersionID(getUBLVersionID_2_0());
            /* Agregar <VoidedDocuments><cbc:CustomizationID> */
            voidedDocumentType.setCustomizationID(getCustomizationID_1_0());
            /* Agregar <VoidedDocuments><cbc:ID> */
            if (logger.isInfoEnabled()) {
                logger.info("generateReversionDocumentType() [" + this.identifier + "] Agregando ANTICIPOId: " + transaction.getANTICIPOId());
            }
            voidedDocumentType.setID(getID(transaction.getANTICIPOId().replace("RA", "RR")));
            /* Agregar <VoidedDocuments><cbc:ReferenceDate> */
            voidedDocumentType.setReferenceDate(getReferenceDate(transaction.getDOCFechaEmision()));
            /* Agregar <VoidedDocuments><cbc:IssueDate> */
            voidedDocumentType.setIssueDate(getIssueDate(new Date()));
            /* Agregar <VoidedDocuments><cac:Signature> */
            voidedDocumentType.getSignature().add(getSignature(transaction.getDocIdentidadNro(), transaction.getRazonSocial(), signerName));
            /* Agregar <VoidedDocuments><cac:AccountingSupplierParty> */
            SupplierPartyType accountingSupplierParty = getAccountingSupplierPartyV20(transaction.getDocIdentidadNro(), transaction.getDocIdentidadTipo(), transaction.getRazonSocial(), transaction.getNombreComercial());
            voidedDocumentType.setAccountingSupplierParty(accountingSupplierParty);
            /* Agregar item <VoidedDocuments><sac:VoidedDocumentsLine> */
            {
                VoidedDocumentsLineType voidedDocumentLine = new VoidedDocumentsLineType();
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><cbc:LineID> */
                LineIDType lineID = new LineIDType();
                lineID.setValue("1");
                voidedDocumentLine.setLineID(lineID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><cbc:DocumentTypeCode> */
                DocumentTypeCodeType documentTypeCode = new DocumentTypeCodeType();
                documentTypeCode.setValue(transaction.getDOCCodigo());
                voidedDocumentLine.setDocumentTypeCode(documentTypeCode);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentSerialID> */
                IdentifierType documentSerialID = new IdentifierType();
                documentSerialID.setValue(transaction.getDOCSerie());
                voidedDocumentLine.setDocumentSerialID(documentSerialID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:DocumentNumberID> */
                IdentifierType documentNumberID = new IdentifierType();
                documentNumberID.setValue(transaction.getDOCNumero());
                voidedDocumentLine.setDocumentNumberID(documentNumberID);
                /* Agregar <VoidedDocuments><sac:VoidedDocumentsLine><sac:VoidReasonDescription> */
                TextType voidReasonDescription = new TextType();
                voidReasonDescription.setValue(transaction.getFEComentario());
                voidedDocumentLine.setVoidReasonDescription(voidReasonDescription);
                voidedDocumentType.getVoidedDocumentsLine().add(voidedDocumentLine);
            }
        } catch (UBLDocumentException e) {
            logger.error("generateReversionDocumentType() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateReversionDocumentType() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_345, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateReversionDocumentType() [" + this.identifier + "]");
        }
        return voidedDocumentType;
    } //generateReversionDocumentType

    private List<InvoiceLineType> getAllInvoiceLines(Set<TransaccionLineas> transaccionLineasList, Set<TransaccionPropiedades> transaccionPropiedadesList, String currencyCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllInvoiceLines() [" + this.identifier + "] transaccionLineasList: " + transaccionLineasList + " currencyCode: " + currencyCode);
        }
        if (null == transaccionLineasList || transaccionLineasList.isEmpty()) {
            logger.error("getAllInvoiceLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<InvoiceLineType> invoiceLineList = new ArrayList<>();
        try {
            for (TransaccionLineas transaccionLinea : transaccionLineasList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Extrayendo informacion del item...");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "]\n" + "NroOrden: " + transaccionLinea.getTransaccionLineasPK().getNroOrden() + "\n" + "Cantidad: " + transaccionLinea.getCantidad() + "\tUnidad: " + transaccionLinea.getUnidad() + "\tUnidadSunat: " + transaccionLinea.getUnidadSunat() + "\tTotalLineaSinIGV: " + transaccionLinea.getTotalLineaSinIGV() + "\n" + "PrecioRefCodigo: " + transaccionLinea.getPrecioRefCodigo() + "\tPrecioIGV: " + transaccionLinea.getPrecioIGV() + "\tPrecioRefMonto: " + transaccionLinea.getPrecioRefMonto() + "\n" + "DCTOMonto: " + transaccionLinea.getDSCTOMonto() + "\tDescripcion: " + transaccionLinea.getDescripcion() + "\tCodArticulo: " + transaccionLinea.getCodArticulo());
                }
                InvoiceLineType invoiceLine = new InvoiceLineType();
                /* Agregar <cac:InvoiceLine><cbc:ID> */
                invoiceLine.setID(getID(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));
                /*
                 * Agregar UNIDAD DE MEDIDA segun SUNAT
                 * <cac:InvoiceLine><cbc:InvoicedQuantity>
                 */
                invoiceLine.setInvoicedQuantity(getInvoicedQuantity(transaccionLinea.getCantidad(), transaccionLinea.getUnidadSunat()));
                /*
                 * Agregar UNIDAD DE MEDIDA segun VENTURA
                 * <cac:InvoiceLine><cbc:Note>
                 */
                if (StringUtils.isNotBlank(transaccionLinea.getUnidad())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando UNIDAD DE MEDIDA (VENTURA).");
                    }
                    invoiceLine.getNote().add(getNote(transaccionLinea.getUnidad()));
                }
                /* Agregar <cac:InvoiceLine><cbc:LineExtensionAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando TOTAL_LINEA_SIN_IGV(" + transaccionLinea.getTotalLineaSinIGV() + ") - TAG LineExtensionAmount.");
                }
                invoiceLine.setLineExtensionAmount(getLineExtensionAmount(transaccionLinea.getTotalLineaSinIGV(), currencyCode));
                /*
                 * Op. Onerosa:     tiene precio unitario
                 * Op. No Onerosa:  tiene valor referencial
                 *
                 * Agregar PRECIO UNITARIO / VALOR REFERENCIAL
                 * <cac:InvoiceLine><cac:PricingReference>
                 */
                if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando PRECIO_IGV(" + transaccionLinea.getPrecioIGV() + ") - TAG PricingReference.");
                    }
                    invoiceLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                } else if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando PRECIO_REF_MONTO(" + transaccionLinea.getPrecioRefMonto() + ") - TAG PricingReference.");
                    }
                    invoiceLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioRefMonto().setScale(IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                } else if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REGULATED_RATES)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando Otro Precio de venta(" + transaccionLinea.getPrecioRefCodigo() + ") - TAG PricingReference.");
                    }
                    invoiceLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioRefMonto().setScale(IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                }
                /*
                 * Agregar DETRACCIONES
                 *
                 * SERVICIO DE TRANSPORTE DE CARGA
                 */
                if (transaccionLinea.getTransaccion().getCodigoDetraccion().equals("027")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando DETRACCION - Servicio de Transporte de Carga.");
                    }
                    invoiceLine.getDelivery().add(getDeliveryForLine(transaccionLinea.getCodUbigeoDestino(), transaccionLinea.getDirecDestino(), transaccionLinea.getCodUbigeoOrigen(), transaccionLinea.getDirecOrigen(), transaccionLinea.getDetalleViaje(), transaccionLinea.getValorCargaEfectiva(), transaccionLinea.getValorCargaUtil(), transaccionLinea.getValorTransporte(), transaccionLinea.getConfVehicular(), transaccionLinea.getCUtilVehiculo(), transaccionLinea.getCEfectivaVehiculo(), transaccionLinea.getValorRefTM(), transaccionLinea.getValorPreRef(), transaccionLinea.getFactorRetorno()));
                }

                /*
                 * Agregar DESCUENTO DE LINEA
                 *
                 * ChargeIndicatorType:
                 *      El valor FALSE representa que es un DESCUENTO DE LINEA
                 *
                 * <cac:InvoiceLine><cac:AllowanceCharge>
                 */
                if (null != transaccionLinea.getDSCTOMonto() && transaccionLinea.getDSCTOMonto().compareTo(BigDecimal.ZERO) > 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando DSCTO_MONTO(" + transaccionLinea.getDSCTOMonto() + ") DSCTO_PORCENTAJE(" + transaccionLinea.getDSCTOPorcentaje() + ") TOTAL_BRUTO(" + transaccionLinea.getTotalBruto() + ") - TAG AllowanceCharge.");
                    }
                    invoiceLine.getAllowanceCharge().add(getAllowanceCharge(false, transaccionLinea.getDSCTOPorcentaje(), transaccionLinea.getDSCTOMonto(), transaccionLinea.getTotalBruto(), currencyCode, "00"));
                }

                /*
                 * Agregar IMPUESTOS DE LINEA
                 * <cac:InvoiceLine><cac:TaxTotal>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando IMPUESTOS DE LINEA.");
                }
                invoiceLine.getTaxTotal().add(getTaxTotalLineV21(transaccionLinea.getTransaccionLineaImpuestosList(), transaccionLinea.getLineaImpuesto(), currencyCode));

                /* Agregar <cac:InvoiceLine><cac:Item> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando DESCRIPCION[" + transaccionLinea.getDescripcion() + "] COD_ARTICULO[" + transaccionLinea.getCodArticulo() + "] COD_SUNAT[" + transaccionLinea.getCodSunat() + "] COD_PROD_GS1[" + transaccionLinea.getCodProdGS1() + "] - TAG Item.");
                }
                invoiceLine.setItem(getItemForLine(transaccionLinea.getDescripcion(), transaccionLinea.getCodArticulo(), transaccionLinea.getCodSunat(), transaccionLinea.getCodProdGS1(), transaccionPropiedadesList));

                /*
                 * Agregar VALOR UNITARIO
                 * <cac:InvoiceLine><cac:Price>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando VALOR UNITARIO - TAG Price.");
                }
                invoiceLine.setPrice(getPriceForLine(transaccionLinea.getTransaccionLineasBillrefList(), currencyCode));
                invoiceLineList.add(invoiceLine);
            } //for
            for (int i = 0; i < IUBLConfig.lstImporteIGV.size(); i++) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Totales con IGV: " + IUBLConfig.lstImporteIGV.get(i));
                }
            }
        } catch (UBLDocumentException e) {
            logger.error("getAllInvoiceLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllInvoiceLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllInvoiceLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllInvoiceLines() [" + this.identifier + "]");
        }
        return invoiceLineList;
    } //getAllInvoiceLines

    private List<InvoiceLineType> getAllBoletaLines(Set<TransaccionLineas> transaccionLineasList, Set<TransaccionPropiedades> transaccionPropiedadesList, String currencyCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllBoletaLines() [" + this.identifier + "] transaccionLineasList: " + transaccionLineasList + " currencyCode: " + currencyCode);
        }
        if (null == transaccionLineasList || transaccionLineasList.isEmpty()) {
            logger.error("getAllBoletaLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<InvoiceLineType> boletaLineList = new ArrayList<>(transaccionLineasList.size());
        try {
            // IUBLConfig.lstImporteIGV = new ArrayList<BigDecimal>();
            for (TransaccionLineas transaccionLinea : transaccionLineasList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "] Extrayendo informacion del item...");
                }
                String precioRefCodigo = transaccionLinea.getPrecioRefCodigo();
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "]\n" + "NroOrden: " + transaccionLinea.getTransaccionLineasPK().getNroOrden() + "\n" + "Cantidad: " + transaccionLinea.getCantidad() + "\tUnidad: " + transaccionLinea.getUnidad() + "\tUnidadSunat: " + transaccionLinea.getUnidadSunat() + "\tTotalLineaSinIGV: " + transaccionLinea.getTotalLineaSinIGV() + "\tTotalLineaConIGV: " + transaccionLinea.getTotalLineaConIGV() + "\n" + "PrecioRefCodigo: " + precioRefCodigo + "\tPrecioIGV: " + transaccionLinea.getPrecioIGV() + "\tPrecioRefMonto: " + transaccionLinea.getPrecioRefMonto() + "\n" + "DCTOMonto: " + transaccionLinea.getDSCTOMonto() + "\tDescripcion: " + transaccionLinea.getDescripcion() + "\tCodArticulo: " + transaccionLinea.getCodArticulo());
                }
                InvoiceLineType boletaLine = new InvoiceLineType();

                /* Agregar <cac:InvoiceLine><cbc:ID> */
                boletaLine.setID(getID(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));

                /*
                 * Agregar UNIDAD DE MEDIDA segun SUNAT
                 * <cac:InvoiceLine><cbc:InvoicedQuantity>
                 */
                boletaLine.setInvoicedQuantity(getInvoicedQuantity(transaccionLinea.getCantidad(), transaccionLinea.getUnidadSunat()));

                /*
                 * Agregar UNIDAD DE MEDIDA segun VENTURA
                 * <cac:InvoiceLine><cbc:Note>
                 */
                if (StringUtils.isNotBlank(transaccionLinea.getUnidad())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando UNIDAD DE MEDIDA (VENTURA).");
                    }
                    boletaLine.getNote().add(getNote(transaccionLinea.getUnidad()));
                }

                /* Agregar <cac:InvoiceLine><cbc:LineExtensionAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando TOTAL_LINEA_SIN_IGV(" + transaccionLinea.getTotalLineaSinIGV() + ") - TAG LineExtensionAmount.");
                }
                boletaLine.setLineExtensionAmount(getLineExtensionAmount(transaccionLinea.getTotalLineaSinIGV(), currencyCode));

                /*
                 * Op. Onerosa:     tiene precio unitario
                 * Op. No Onerosa:  tiene valor referencial
                 *
                 * Agregar PRECIO UNITARIO / VALOR REFERENCIAL
                 * <cac:InvoiceLine><cac:PricingReference>
                 */
                if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                    boletaLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                } else if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                    boletaLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioRefMonto().setScale(IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                }
//                if (precioRefCodigo.equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando PRECIO_IGV(" + transaccionLinea.getPrecioIGV() + ") - TAG PricingReference.");
//                    }
//                    boletaLine.setPricingReference(getPricingReference(precioRefCodigo,
//                            transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode));
//                }
//                boletaLine.setPricingReference(getPricingReference(precioRefCodigo,
//                        transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode));

                /*
                 * Agregar DESCUENTO DE LINEA
                 *
                 * ChargeIndicatorType:
                 *      El valor FALSE representa que es un DESCUENTO DE LINEA
                 *
                 * <cac:InvoiceLine><cac:AllowanceCharge>
                 */
                if (null != transaccionLinea.getDSCTOMonto() && transaccionLinea.getDSCTOMonto().compareTo(BigDecimal.ZERO) == 1) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando DSCTO_MONTO(" + transaccionLinea.getDSCTOMonto() + ") DSCTO_PORCENTAJE(" + transaccionLinea.getDSCTOPorcentaje() + ") TOTAL_BRUTO(" + transaccionLinea.getTotalBruto() + ") - TAG AllowanceCharge.");
                    }
                    boletaLine.getAllowanceCharge().add(getAllowanceCharge(false, transaccionLinea.getDSCTOPorcentaje(), transaccionLinea.getDSCTOMonto(), transaccionLinea.getTotalBruto(), currencyCode, "00"));
                    //ESTO FUE LO QUE SE ENCONTRO EN EL CODIGO ¡¡CONFIRMAR!!
//                    boletaLine.getAllowanceCharge().add(getAllowanceCharge(false, transaccionLinea.getDSCTOMonto(), 
//                            transaccionLinea.getDSCTOMonto(),transaccionLinea.getDSCTOMonto(), currencyCode));
                }

                /*
                 * Agregar IMPUESTOS DE LINEA
                 * <cac:InvoiceLine><cac:TaxTotal>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando IMPUESTOS DE LINEA.");
                }
                boletaLine.getTaxTotal().add(getTaxTotalLineV21(transaccionLinea.getTransaccionLineaImpuestosList(), transaccionLinea.getLineaImpuesto(), currencyCode));

                /* Agregar <cac:InvoiceLine><cac:Item> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando DESCRIPCION[" + transaccionLinea.getDescripcion() + "] COD_ARTICULO[" + transaccionLinea.getCodArticulo() + "] COD_SUNAT[" + transaccionLinea.getCodSunat() + "] COD_PROD_GS1[" + transaccionLinea.getCodProdGS1() + "] - TAG Item.");
                }
                boletaLine.setItem(getItemForLine(transaccionLinea.getDescripcion(), transaccionLinea.getCodArticulo(), transaccionLinea.getCodSunat(), transaccionLinea.getCodProdGS1(), transaccionPropiedadesList));

                /*
                 * Agregar VALOR UNITARIO
                 * <cac:InvoiceLine><cac:Price>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllBoletaLines() [" + this.identifier + "] Agregando VALOR UNITARIO - TAG Price.");
                }
                boletaLine.setPrice(getPriceForLine(transaccionLinea.getTransaccionLineasBillrefList(), currencyCode));
                boletaLineList.add(boletaLine);
            } //for
        } catch (UBLDocumentException e) {
            logger.error("getAllBoletaLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllBoletaLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllBoletaLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllBoletaLines() [" + this.identifier + "]");
        }
        return boletaLineList;
    } //getAllBoletaLines

    private List<CreditNoteLineType> getAllCreditNoteLines(Set<TransaccionLineas> transaccionLineasList, Set<TransaccionPropiedades> transaccionPropiedadesList, String currencyCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllCreditNoteLines() [" + this.identifier + "] transaccionLineasList: " + transaccionLineasList + " currencyCode: " + currencyCode);
        }
        if (null == transaccionLineasList || transaccionLineasList.isEmpty()) {
            logger.error("getAllCreditNoteLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<CreditNoteLineType> creditNoteLineList = new ArrayList<CreditNoteLineType>(transaccionLineasList.size());
        try {
            for (TransaccionLineas transaccionLinea : transaccionLineasList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Extrayendo informacion del item...");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "]\n" + "NroOrden: " + transaccionLinea.getTransaccionLineasPK().getNroOrden() + "\n" + "Cantidad: " + transaccionLinea.getCantidad() + "\tUnidad: " + transaccionLinea.getUnidad() + "\tUnidadSunat: " + transaccionLinea.getUnidadSunat() + "\tTotalLineaSinIGV: " + transaccionLinea.getTotalLineaSinIGV() + "\n" + "PrecioRefCodigo: " + transaccionLinea.getPrecioRefCodigo() + "\tPrecioIGV: " + transaccionLinea.getPrecioIGV() + "\tPrecioRefMonto: " + transaccionLinea.getPrecioRefMonto() + "\n" + "DCTOMonto: " + transaccionLinea.getDSCTOMonto() + "\tDescripcion: " + transaccionLinea.getDescripcion() + "\tCodArticulo: " + transaccionLinea.getCodArticulo());
                }
                CreditNoteLineType creditNoteLine = new CreditNoteLineType();

                /* Agregar <cac:CreditNoteLine><cbc:ID> */
                creditNoteLine.setID(getID(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));

                /*
                 * Agregar UNIDAD DE MEDIDA segun SUNAT
                 * <cac:CreditNoteLine><cbc:CreditedQuantity>
                 */
                creditNoteLine.setCreditedQuantity(getCreditedQuantity(transaccionLinea.getCantidad(), transaccionLinea.getUnidadSunat()));

                /*
                 * Agregar UNIDAD DE MEDIDA segun VENTURA
                 * <cac:CreditNoteLine><cbc:Note>
                 */
                if (StringUtils.isNotBlank(transaccionLinea.getUnidad())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Agregando UNIDAD DE MEDIDA (VENTURA).");
                    }
                    creditNoteLine.getNote().add(getNote(transaccionLinea.getUnidad()));
                }

                /* Agregar <cac:CreditNoteLine><cbc:LineExtensionAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Agregando TOTAL_LINEA_SIN_IGV(" + transaccionLinea.getTotalLineaSinIGV() + ") - TAG LineExtensionAmount.");
                }
                creditNoteLine.setLineExtensionAmount(getLineExtensionAmount(transaccionLinea.getTotalLineaSinIGV(), currencyCode));

                /*
                 * Op. Onerosa:     tiene precio unitario
                 * Op. No Onerosa:  tiene valor referencial
                 *
                 * Agregar PRECIO UNITARIO / VALOR REFERENCIAL
                 * <cac:CreditNoteLine><cac:PricingReference>
                 */
                if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                    creditNoteLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                } else if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                    creditNoteLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioRefMonto().setScale(IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                }

                /*
                 * Agregar IMPUESTOS DE LINEA
                 * <cac:CreditNoteLine><cac:TaxTotal>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Agregando IMPUESTOS DE LINEA.");
                }
                creditNoteLine.getTaxTotal().add(getTaxTotalLineV21(transaccionLinea.getTransaccionLineaImpuestosList(), transaccionLinea.getLineaImpuesto(), currencyCode));

                /* Agregar <cac:CreditNoteLine><cac:Item> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Agregando DESCRIPCION[" + transaccionLinea.getDescripcion() + "] COD_ARTICULO[" + transaccionLinea.getCodArticulo() + "] COD_SUNAT[" + transaccionLinea.getCodSunat() + "] COD_PROD_GS1[" + transaccionLinea.getCodProdGS1() + "] - TAG Item.");
                }
                creditNoteLine.setItem(getItemForLine(transaccionLinea.getDescripcion(), transaccionLinea.getCodArticulo(), transaccionLinea.getCodSunat(), transaccionLinea.getCodProdGS1(), transaccionPropiedadesList));

                /*
                 * Agregar VALOR UNITARIO
                 * <cac:CreditNoteLine><cac:Price>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllCreditNoteLines() [" + this.identifier + "] Agregando VALOR UNITARIO - TAG Price.");
                }
                creditNoteLine.setPrice(getPriceForLine(transaccionLinea.getTransaccionLineasBillrefList(), currencyCode));
                creditNoteLineList.add(creditNoteLine);
            } //for
        } catch (UBLDocumentException e) {
            logger.error("getAllCreditNoteLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllCreditNoteLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllCreditNoteLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllCreditNoteLines() [" + this.identifier + "]");
        }
        return creditNoteLineList;
    } //getAllCreditNoteLines

    private List<DebitNoteLineType> getAllDebitNoteLines(Set<TransaccionLineas> transaccionLineasList, Set<TransaccionPropiedades> transaccionPropiedadesList, String currencyCode) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllDebitNoteLines() [" + this.identifier + "] transaccionLineasList: " + transaccionLineasList + " currencyCode: " + currencyCode);
        }
        if (null == transaccionLineasList || transaccionLineasList.isEmpty()) {
            logger.error("getAllDebitNoteLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<DebitNoteLineType> debitNoteLineList = new ArrayList<>();
        try {
            for (TransaccionLineas transaccionLinea : transaccionLineasList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Extrayendo informacion del item...");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "]\n" + "NroOrden: " + transaccionLinea.getTransaccionLineasPK().getNroOrden() + "\n" + "Cantidad: " + transaccionLinea.getCantidad() + "\tUnidad: " + transaccionLinea.getUnidad() + "\tUnidadSunat: " + transaccionLinea.getUnidadSunat() + "\tTotalLineaSinIGV: " + transaccionLinea.getTotalLineaSinIGV() + "\n" + "PrecioRefCodigo: " + transaccionLinea.getPrecioRefCodigo() + "\tPrecioIGV: " + transaccionLinea.getPrecioIGV() + "\tPrecioRefMonto: " + transaccionLinea.getPrecioRefMonto() + "\n" + "DCTOMonto: " + transaccionLinea.getDSCTOMonto() + "\tDescripcion: " + transaccionLinea.getDescripcion() + "\tCodArticulo: " + transaccionLinea.getCodArticulo());
                }
                DebitNoteLineType debitNoteLine = new DebitNoteLineType();

                /* Agregar <cac:DebitNoteLine><cbc:ID> */
                debitNoteLine.setID(getID(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));

                /*
                 * Agregar UNIDAD DE MEDIDA segun SUNAT
                 * <cac:DebitNoteLine><cbc:DebitedQuantity>
                 */
                debitNoteLine.setDebitedQuantity(getDebitedQuantity(transaccionLinea.getCantidad(), transaccionLinea.getUnidadSunat()));

                /*
                 * Agregar UNIDAD DE MEDIDA segun VENTURA
                 * <cac:DebitNoteLine><cbc:Note>
                 */
                if (StringUtils.isNotBlank(transaccionLinea.getUnidad())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Agregando UNIDAD DE MEDIDA (VENTURA).");
                    }
                    debitNoteLine.getNote().add(getNote(transaccionLinea.getUnidad()));
                }

                /* Agregar <cac:DebitNoteLine><cbc:LineExtensionAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Agregando TOTAL_LINEA_SIN_IGV(" + transaccionLinea.getTotalLineaSinIGV() + ") - TAG LineExtensionAmount.");
                }
                debitNoteLine.setLineExtensionAmount(getLineExtensionAmount(transaccionLinea.getTotalLineaSinIGV(), currencyCode));

                /*
                 * Op. Onerosa:     tiene precio unitario
                 * Op. No Onerosa:  tiene valor referencial
                 *
                 * Agregar PRECIO UNITARIO / VALOR REFERENCIAL
                 * <cac:DebitNoteLine><cac:PricingReference>
                 */
                if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_UNIT_PRICE)) {
                    debitNoteLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioIGV().setScale(IUBLConfig.DECIMAL_LINE_UNIT_PRICE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                } else if (transaccionLinea.getPrecioRefCodigo().equalsIgnoreCase(IUBLConfig.ALTERNATIVE_CONDICION_REFERENCE_VALUE)) {
                    debitNoteLine.setPricingReference(getPricingReference(transaccionLinea.getPrecioRefCodigo(), transaccionLinea.getPrecioRefMonto().setScale(IUBLConfig.DECIMAL_LINE_REFERENCE_VALUE, RoundingMode.HALF_UP), currencyCode, transaccionLinea));
                }

                /*
                 * Agregar IMPUESTOS DE LINEA
                 * <cac:DebitNoteLine><cac:TaxTotal>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Agregando IMPUESTOS DE LINEA.");
                }
                debitNoteLine.getTaxTotal().add(getTaxTotalLineV21(transaccionLinea.getTransaccionLineaImpuestosList(), transaccionLinea.getLineaImpuesto(), currencyCode));

                /* Agregar <cac:DebitNoteLine><cac:Item> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Agregando DESCRIPCION[" + transaccionLinea.getDescripcion() + "] COD_ARTICULO[" + transaccionLinea.getCodArticulo() + "] COD_SUNAT[" + transaccionLinea.getCodSunat() + "] COD_PROD_GS1[" + transaccionLinea.getCodProdGS1() + "] - TAG Item.");
                }
                debitNoteLine.setItem(getItemForLine(transaccionLinea.getDescripcion(), transaccionLinea.getCodArticulo(), transaccionLinea.getCodSunat(), transaccionLinea.getCodProdGS1(), transaccionPropiedadesList));

                /*
                 * Agregar VALOR UNITARIO
                 * <cac:DebitNoteLine><cac:Price>
                 */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDebitNoteLines() [" + this.identifier + "] Agregando VALOR UNITARIO - TAG Price.");
                }
                debitNoteLine.setPrice(getPriceForLine(transaccionLinea.getTransaccionLineasBillrefList(), currencyCode));
                debitNoteLineList.add(debitNoteLine);
            } //for
        } catch (UBLDocumentException e) {
            logger.error("getAllDebitNoteLines() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllDebitNoteLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllDebitNoteLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllDebitNoteLines() [" + this.identifier + "]");
        }
        return debitNoteLineList;
    } //getAllDebitNoteLines

    private List<SUNATPerceptionDocumentReferenceType> getAllSUNATPerceptionDocumentReferences(Set<TransaccionComprobantePago> transaccionComprobantePagoList) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] transaccionComprobantePagoList: " + transaccionComprobantePagoList);
        }
        if (null == transaccionComprobantePagoList || transaccionComprobantePagoList.isEmpty()) {
            logger.error("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<SUNATPerceptionDocumentReferenceType> sunatPerceptionDocReferenceList = new ArrayList<SUNATPerceptionDocumentReferenceType>(transaccionComprobantePagoList.size());
        try {
            for (TransaccionComprobantePago transaccionComprobantePago : transaccionComprobantePagoList) {
                SUNATPerceptionDocumentReferenceType sunatPerceptionDocReference = new SUNATPerceptionDocumentReferenceType();

                /* Agregar <sac:SUNATPerceptionDocumentReference><cbc:ID> */
                sunatPerceptionDocReference.setId(getID(transaccionComprobantePago.getDOCNumero(), transaccionComprobantePago.getDOCTipo()));

                /* Agregar <sac:SUNATPerceptionDocumentReference><cbc:IssueDate> */
                sunatPerceptionDocReference.setIssueDate(getIssueDate(transaccionComprobantePago.getDOCFechaEmision()));

                /* Agregar <sac:SUNATPerceptionDocumentReference><cbc:TotalInvoiceAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando DOC_IMPORTE(" + transaccionComprobantePago.getDOCImporte() + ") DOC_MONEDA(" + transaccionComprobantePago.getDOCMoneda() + ") - TAG TotalInvoiceAmount.");
                }
                sunatPerceptionDocReference.setTotalInvoiceAmount(getTotalInvoiceAmount(transaccionComprobantePago.getDOCImporte(), transaccionComprobantePago.getDOCMoneda()));

                /* Agregar <sac:SUNATPerceptionDocumentReference><cac:Payment> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando PAGO_NUMERO(" + transaccionComprobantePago.getPagoNumero() + ") PAGO_IMPORTE_SR(" + transaccionComprobantePago.getPagoImporteSR() + ") PAGO_MONEDA(" + transaccionComprobantePago.getPagoMoneda() + ") PAGO_FECHA(" + transaccionComprobantePago.getPagoFecha() + ") - TAG Payment.");
                }
                sunatPerceptionDocReference.setPayment(getPaymentForLine(transaccionComprobantePago.getPagoNumero(), transaccionComprobantePago.getPagoImporteSR(), transaccionComprobantePago.getPagoMoneda(), transaccionComprobantePago.getPagoFecha()));

                /* Agregar <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation> */
                SUNATPerceptionInformationType sunatPerceptionInformation = new SUNATPerceptionInformationType();
                {
                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><sac:SUNATPerceptionAmount> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando CP_IMPORTE(" + transaccionComprobantePago.getCPImporte() + ") CP_MONEDA(" + transaccionComprobantePago.getCPMoneda() + ") - TAG SUNATPerceptionInformation_SUNATPerceptionAmount.");
                    }
                    SUNATPerceptionAmountType sunatPerceptionAmount = new SUNATPerceptionAmountType();
                    sunatPerceptionAmount.setValue(transaccionComprobantePago.getCPImporte().setScale(2, RoundingMode.HALF_UP));
                    sunatPerceptionAmount.setCurrencyID(CurrencyCodeContentType.valueOf(transaccionComprobantePago.getCPMoneda()).value());
                    sunatPerceptionInformation.setPerceptionAmount(sunatPerceptionAmount);

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><sac:SUNATPerceptionDate> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando CP_FECHA(" + transaccionComprobantePago.getCPFecha() + ") - TAG SUNATPerceptionInformation_SUNATPerceptionDate.");
                    }
                    sunatPerceptionInformation.setPerceptionDate(getSUNATPerceptionDate(transaccionComprobantePago.getCPFecha()));

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><sac:SUNATNetTotalCashed> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando CP_IMPORTE_TOTAL(" + transaccionComprobantePago.getCPImporteTotal() + ") CP_MONEDA_MONTO_NETO(" + transaccionComprobantePago.getCPMonedaMontoNeto() + ") - TAG SUNATPerceptionInformation_SUNATNetTotalCashed.");
                    }
                    SUNATNetTotalCashedType sunatNetTotalCashed = new SUNATNetTotalCashedType();
                    sunatNetTotalCashed.setValue(transaccionComprobantePago.getCPImporteTotal().setScale(2, RoundingMode.HALF_UP));
                    sunatNetTotalCashed.setCurrencyID(CurrencyCodeContentType.valueOf(transaccionComprobantePago.getCPMonedaMontoNeto()).value());
                    sunatPerceptionInformation.setSunatNetTotalCashed(sunatNetTotalCashed);

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><cac:ExchangeRate> */
                    ExchangeRateType exchangeRate = new ExchangeRateType();

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><cac:ExchangeRate><cbc:SourceCurrencyCode> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando TC_MONEDA_REF(" + transaccionComprobantePago.getTCMonedaRef() + ") - TAG SUNATPerceptionInformation_ExchangeRate_SourceCurrencyCode.");
                    }
                    SourceCurrencyCodeType sourceCurrencyCode = new SourceCurrencyCodeType();
                    sourceCurrencyCode.setValue(transaccionComprobantePago.getTCMonedaRef());
                    exchangeRate.setSourceCurrencyCode(sourceCurrencyCode);

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><cac:ExchangeRate><cbc:TargetCurrencyCode> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando TC_MONEDA_OBJ(" + transaccionComprobantePago.getTCMonedaObj() + ") - TAG SUNATPerceptionInformation_ExchangeRate_TargetCurrencyCode.");
                    }
                    TargetCurrencyCodeType targetCurrencyCode = new TargetCurrencyCodeType();
                    targetCurrencyCode.setValue(transaccionComprobantePago.getTCMonedaObj());
                    exchangeRate.setTargetCurrencyCode(targetCurrencyCode);

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><cac:ExchangeRate><cbc:CalculationRate> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando TC_FACTOR(" + transaccionComprobantePago.getTCFactor() + ") - TAG SUNATPerceptionInformation_ExchangeRate_CalculationRate.");
                    }
                    CalculationRateType calculationRate = new CalculationRateType();
                    calculationRate.setValue(transaccionComprobantePago.getTCFactor());
                    exchangeRate.setCalculationRate(calculationRate);

                    /* <sac:SUNATPerceptionDocumentReference><sac:SUNATPerceptionInformation><cac:ExchangeRate><cbc:Date> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Agregando TC_FECHA(" + transaccionComprobantePago.getTCFecha() + ") - TAG SUNATPerceptionInformation_ExchangeRate_Date.");
                    }
                    exchangeRate.setDate(getDate(transaccionComprobantePago.getTCFecha()));
                    sunatPerceptionInformation.setExchangeRate(exchangeRate);
                }
                sunatPerceptionDocReference.setSunatPerceptionInformation(sunatPerceptionInformation);
                sunatPerceptionDocReferenceList.add(sunatPerceptionDocReference);
            } //for
        } catch (UBLDocumentException e) {
            logger.error("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllSUNATPerceptionDocumentReferences() [" + this.identifier + "]");
        }
        return sunatPerceptionDocReferenceList;
    } //getAllSUNATPerceptionDocumentReferences

    public List<SUNATRetentionDocumentReferenceType> getAllSUNATRetentionDocumentReferences(Set<TransaccionComprobantePago> transaccionComprobantePagoList) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] transaccionComprobantePagoList: " + transaccionComprobantePagoList);
        }
        if (null == transaccionComprobantePagoList || transaccionComprobantePagoList.isEmpty()) {
            logger.error("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<SUNATRetentionDocumentReferenceType> sunatRetentionDocReferenceList = new ArrayList<SUNATRetentionDocumentReferenceType>(transaccionComprobantePagoList.size());
        try {
            for (TransaccionComprobantePago transaccionComprobantePago : transaccionComprobantePagoList) {
                SUNATRetentionDocumentReferenceType sunatRetentionDocReference = new SUNATRetentionDocumentReferenceType();

                /* Agregar <sac:SUNATRetentionDocumentReference><cbc:ID> */
                sunatRetentionDocReference.setId(getID(transaccionComprobantePago.getDOCNumero(), transaccionComprobantePago.getDOCTipo()));

                /* Agregar <sac:SUNATRetentionDocumentReference><cbc:IssueDate> */
                sunatRetentionDocReference.setIssueDate(getIssueDate(transaccionComprobantePago.getDOCFechaEmision()));

                /* Agregar <sac:SUNATRetentionDocumentReference><cbc:TotalInvoiceAmount> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando DOC_IMPORTE(" + transaccionComprobantePago.getDOCImporte() + ") DOC_MONEDA(" + transaccionComprobantePago.getDOCMoneda() + ") - TAG TotalInvoiceAmount.");
                }
                sunatRetentionDocReference.setTotalInvoiceAmount(getTotalInvoiceAmount(transaccionComprobantePago.getDOCImporte(), transaccionComprobantePago.getDOCMoneda()));

                /* Agregar <sac:SUNATRetentionDocumentReference><cac:Payment> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando PAGO_NUMERO(" + transaccionComprobantePago.getPagoNumero() + ") PAGO_IMPORTE_SR(" + transaccionComprobantePago.getPagoImporteSR() + ") PAGO_MONEDA(" + transaccionComprobantePago.getPagoMoneda() + ") PAGO_FECHA(" + transaccionComprobantePago.getPagoFecha() + ") - TAG Payment.");
                }
                sunatRetentionDocReference.setPayment(getPaymentForLine(transaccionComprobantePago.getPagoNumero(), transaccionComprobantePago.getPagoImporteSR(), transaccionComprobantePago.getPagoMoneda(), transaccionComprobantePago.getPagoFecha()));

                /* Agregar <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation> */
                SUNATRetentionInformationType sunatRetentionInformation = new SUNATRetentionInformationType();
                {
                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><sac:SUNATRetentionAmount> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando CP_IMPORTE(" + transaccionComprobantePago.getCPImporte() + ") CP_MONEDA(" + transaccionComprobantePago.getCPMoneda() + ") - TAG SUNATRetentionInformation_SUNATRetentionAmount.");
                    }
                    SUNATRetentionAmountType sunatRetentionAmount = new SUNATRetentionAmountType();
                    sunatRetentionAmount.setValue(transaccionComprobantePago.getCPImporte().setScale(2, RoundingMode.HALF_UP));
                    sunatRetentionAmount.setCurrencyID(CurrencyCodeContentType.valueOf(transaccionComprobantePago.getCPMoneda()).value());
                    sunatRetentionInformation.setSunatRetentionAmount(sunatRetentionAmount);

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><sac:SUNATRetentionDate> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando CP_FECHA(" + transaccionComprobantePago.getCPFecha() + ") - TAG SUNATRetentionInformation_SUNATRetentionDate.");
                    }
                    sunatRetentionInformation.setSunatRetentionDate(getSUNATRetentionDate(transaccionComprobantePago.getCPFecha()));

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><sac:SUNATNetTotalPaid> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando CP_IMPORTE_TOTAL(" + transaccionComprobantePago.getCPImporteTotal() + ") CP_MONEDA_MONTO_NETO(" + transaccionComprobantePago.getCPMonedaMontoNeto() + ") - TAG SUNATRetentionInformation_SUNATNetTotalPaid.");
                    }
                    SUNATNetTotalPaidType sunatNetTotalPaid = new SUNATNetTotalPaidType();
                    sunatNetTotalPaid.setValue(transaccionComprobantePago.getCPImporteTotal().setScale(2, RoundingMode.HALF_UP));
                    sunatNetTotalPaid.setCurrencyID(CurrencyCodeContentType.valueOf(transaccionComprobantePago.getCPMonedaMontoNeto()).value());
                    sunatRetentionInformation.setSunatNetTotalPaid(sunatNetTotalPaid);

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><cac:ExchangeRate> */
                    ExchangeRateType exchangeRate = new ExchangeRateType();

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><cac:ExchangeRate><cbc:SourceCurrencyCode> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando TC_MONEDA_REF(" + transaccionComprobantePago.getTCMonedaRef() + ") - TAG SUNATRetentionInformation_ExchangeRate_SourceCurrencyCode.");
                    }
                    SourceCurrencyCodeType sourceCurrencyCode = new SourceCurrencyCodeType();
                    sourceCurrencyCode.setValue(transaccionComprobantePago.getTCMonedaRef());
                    exchangeRate.setSourceCurrencyCode(sourceCurrencyCode);

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><cac:ExchangeRate><cbc:TargetCurrencyCode> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando TC_MONEDA_OBJ(" + transaccionComprobantePago.getTCMonedaObj() + ") - TAG SUNATRetentionInformation_ExchangeRate_TargetCurrencyCode.");
                    }
                    TargetCurrencyCodeType targetCurrencyCode = new TargetCurrencyCodeType();
                    targetCurrencyCode.setValue(transaccionComprobantePago.getTCMonedaObj());
                    exchangeRate.setTargetCurrencyCode(targetCurrencyCode);

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><cac:ExchangeRate><cbc:CalculationRate> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando TC_FACTOR(" + transaccionComprobantePago.getTCFactor() + ") - TAG SUNATRetentionInformation_ExchangeRate_CalculationRate.");
                    }
                    CalculationRateType calculationRate = new CalculationRateType();
                    calculationRate.setValue(transaccionComprobantePago.getTCFactor().setScale(3));
                    exchangeRate.setCalculationRate(calculationRate);

                    /* <sac:SUNATRetentionDocumentReference><sac:SUNATRetentionInformation><cac:ExchangeRate><cbc:Date> */
                    if (logger.isDebugEnabled()) {
                        logger.debug("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Agregando TC_FECHA(" + transaccionComprobantePago.getTCFecha() + ") - TAG SUNATRetentionInformation_ExchangeRate_Date.");
                    }
                    exchangeRate.setDate(getDate(transaccionComprobantePago.getTCFecha()));
                    sunatRetentionInformation.setExchangeRate(exchangeRate);
                }
                sunatRetentionDocReference.setSunatRetentionInformation(sunatRetentionInformation);
                sunatRetentionDocReferenceList.add(sunatRetentionDocReference);
            } //for 
        } catch (UBLDocumentException e) {
            logger.error("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllSUNATRetentionDocumentReferences() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllSUNATRetentionDocumentReferences() [" + this.identifier + "]");
        }
        return sunatRetentionDocReferenceList;
    } //getAllSUNATRetentionDocumentReferences

    private List<DespatchLineType> getAllDespatchLines(Set<TransaccionLineas> transaccionLineasList) throws UBLDocumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getAllDespatchLines() [" + this.identifier + "] transaccionLineasList: " + transaccionLineasList);
        }
        if (null == transaccionLineasList || transaccionLineasList.isEmpty()) {
            logger.error("getAllDespatchLines() [" + this.identifier + "] ERROR: " + IVenturaError.ERROR_319.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_319);
        }
        List<DespatchLineType> despatchLineList = new ArrayList<DespatchLineType>(transaccionLineasList.size());
        try {
            for (TransaccionLineas transaccionLinea : transaccionLineasList) {
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllDespatchLines() [" + this.identifier + "] Extrayendo informacion del item...");
                }
                DespatchLineType despatchLine = new DespatchLineType();
                /* <cac:DespatchLine><cbc:ID> */
                despatchLine.setID(getID(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));
                /* <cac:DespatchLine><cbc:Note> */
                despatchLine.getNote().add(getNote(transaccionLinea.getUnidad()));
                /* <cac:DespatchLine><cbc:DeliveredQuantity> */
                despatchLine.setDeliveredQuantity(getDeliveredQuantity(transaccionLinea.getCantidad(), transaccionLinea.getUnidadSunat()));
                /* <cac:DespatchLine><cac:OrderLineReference> */
                despatchLine.getOrderLineReference().add(getOrderLineReference(String.valueOf(transaccionLinea.getTransaccionLineasPK().getNroOrden())));
                /* <cac:DespatchLine><cac:Item> */
                if (logger.isDebugEnabled()) {
                    logger.debug("getAllInvoiceLines() [" + this.identifier + "] Agregando DESCRIPCION[" + transaccionLinea.getDescripcion() + "] COD_ARTICULO[" + transaccionLinea.getCodArticulo() + "] - TAG Item.");
                }
                despatchLine.setItem(getItemForLine(transaccionLinea.getDescripcion(), transaccionLinea.getCodArticulo()));
                despatchLineList.add(despatchLine);
            } //for
        } catch (Exception e) {
            logger.error("getAllDespatchLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") - ERROR: " + IVenturaError.ERROR_320.getMessage());
            logger.error("getAllDespatchLines() [" + this.identifier + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new UBLDocumentException(IVenturaError.ERROR_320);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getAllDespatchLines() [" + this.identifier + "]");
        }
        return despatchLineList;
    } //getAllDespatchLines

    private PaymentTermsType createPaymentTerms(Transaccion transaccion) {
        PaymentTermsType paymentTerms = new PaymentTermsType();
        IDType idType = new IDType();
        idType.setValue("Percepcion");
        paymentTerms.setID(idType);
        AmountType amountType = new AmountType();
        BigDecimal montoPercepcion = transaccion.getDOCMonPercepcion();
        BigDecimal importeTotal = transaccion.getDOCImporteTotal();
        BigDecimal totalPercepcion = montoPercepcion.add(importeTotal);
        amountType.setCurrencyID(CurrencyCodeContentType.valueOf(transaccion.getDOCMONCodigo()).value());
        amountType.setValue(totalPercepcion.setScale(2, BigDecimal.ROUND_HALF_UP));
        paymentTerms.setAmount(amountType);
        return paymentTerms;
    }

    private InvoiceTypeCodeType createInvoiceTypeCode(Transaccion transaccion) {
        InvoiceTypeCodeType invoiceTypeCode = new InvoiceTypeCodeType();
        invoiceTypeCode.setName("Tipo de Operacion");
        invoiceTypeCode.setListURI("urn:pe:gob:sunat:cpe:see:gen:catalogos:catalogo01");
        invoiceTypeCode.setListSchemeURI("urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo51");
        invoiceTypeCode.setListName("Tipo de Documento");
        invoiceTypeCode.setListID("2001");
        invoiceTypeCode.setListAgencyName("PE:SUNAT");
        invoiceTypeCode.setValue("01");
        return invoiceTypeCode;
    }

    private AllowanceChargeType createAllowanceChargeType(Transaccion transaccion) {
        AllowanceChargeType allowanceCharge = new AllowanceChargeType();
        ChargeIndicatorType chargeIndicator = new ChargeIndicatorType();
        chargeIndicator.setValue(true);
        allowanceCharge.setChargeIndicator(chargeIndicator);
        AllowanceChargeReasonCodeType chargeReasonCode = new AllowanceChargeReasonCodeType();
        chargeReasonCode.setListURI(IUBLConfig.URI_CATALOG_53);
        chargeReasonCode.setListName(IUBLConfig.CARGO_DESCUENTO_TEXT);
        chargeReasonCode.setListAgencyName(IUBLConfig.LIST_AGENCY_NAME_PE_SUNAT);
        chargeReasonCode.setValue("51");
        allowanceCharge.setAllowanceChargeReasonCode(chargeReasonCode);
        MultiplierFactorNumericType multiplierFactorNumeric = new MultiplierFactorNumericType();
        multiplierFactorNumeric.setValue(transaccion.getDOCPorPercepcion().setScale(3, BigDecimal.ROUND_HALF_UP));
        allowanceCharge.setMultiplierFactorNumeric(multiplierFactorNumeric);
        AmountType amountType = new AmountType();
        amountType.setCurrencyID(CurrencyCodeContentType.valueOf(transaccion.getDOCMONCodigo()).value());
        amountType.setValue(transaccion.getDOCMonPercepcion().setScale(2, BigDecimal.ROUND_HALF_UP));
        allowanceCharge.setAmount(amountType);
        BaseAmountType baseAmount = new BaseAmountType();
        baseAmount.setCurrencyID(CurrencyCodeContentType.valueOf(transaccion.getDOCMONCodigo()).value());
        baseAmount.setValue(transaccion.getDOCImporteTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
        allowanceCharge.setBaseAmount(baseAmount);
        return allowanceCharge;
    }

    private NoteType createNote(String languageLocaleId, String value, String id) {
        final NoteType note = new NoteType();
        Optional.ofNullable(id).ifPresent(identificador -> note.setLanguageID(identificador));
        note.setLanguageLocaleID(languageLocaleId);
        note.setValue(value);
        return note;
    }
} //UBLDocumentHandler
