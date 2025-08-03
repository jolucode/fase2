package org.ventura.cpe.core.wsclient.handler;

import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import org.apache.commons.lang3.StringUtils;
import org.ventura.cpe.core.config.IPDFCreatorConfig;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.exception.ErrorObj;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.wsclient.exception.ValidationException;
import org.w3c.dom.NodeList;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;
import un.unece.uncefact.data.specification.corecomponenttypeschemamodule._2.IdentifierType;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Esta clase contiene metodos para validar los documento UBL.
 *
 * @author Yosmel
 */
@Slf4j
public class ValidationHandler {

    private List<String> totalIDList = null;

    private List<String> typeOfCreditNoteList = null;

    private List<String> typeOfDebitNoteList = null;

    private List<String> refDocumentTypeList = null;

    private String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID que identifica al documento.
     */
    private ValidationHandler(String docUUID) {
        this.docUUID = docUUID;

        /*
         * Cargando ID de TOTALES
         */
        totalIDList = new ArrayList<String>();
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_1001);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_1002);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_1003);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_1004);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_1005);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_2001);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_2002);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_2003);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_2004);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_2005);
        totalIDList.add(IUBLConfig.ADDITIONAL_MONETARY_3001);

        /*
         * Cargando los codigos de los tipos de Nota de Credito
         */
        typeOfCreditNoteList = new ArrayList<String>();
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_01);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_02);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_03);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_04);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_05);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_06);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_07);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_08);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_09);
        typeOfCreditNoteList.add(IUBLConfig.CREDIT_NOTE_TYPE_10);

        /*
         * Cargando los codigos de los tipos de Nota de Debito
         */
        typeOfDebitNoteList = new ArrayList<String>();
        typeOfDebitNoteList.add(IUBLConfig.DEBIT_NOTE_TYPE_01);
        typeOfDebitNoteList.add(IUBLConfig.DEBIT_NOTE_TYPE_02);
        typeOfDebitNoteList.add(IUBLConfig.DEBIT_NOTE_TYPE_03);

        /*
         * Cargando los codigos de los tipos de Nota de Debito
         */
        refDocumentTypeList = new ArrayList<String>();
        refDocumentTypeList.add(IUBLConfig.DOC_INVOICE_CODE);
        refDocumentTypeList.add(IUBLConfig.DOC_BOLETA_CODE);
        refDocumentTypeList.add(IUBLConfig.DOC_MACHINE_TICKET_CODE);
        refDocumentTypeList.add(IUBLConfig.DOC_FINANCIAL_BANKS_CODE);
        refDocumentTypeList.add(IUBLConfig.DOC_BANK_INSURANCE_CODE);
        refDocumentTypeList.add(IUBLConfig.DOC_ISSUED_BY_AFP_CODE);
    } //ValidationHandler

    /**
     * Este metodo crea una nueva instancia de la clase ValidationHandler.
     *
     * @param docUUID UUID que identifica al documento.
     * @return Retorna una nueva instancia de la clase ValidationHandler.
     */
    public static synchronized ValidationHandler newInstance(String docUUID) {
        return new ValidationHandler(docUUID);
    } //newInstance

    /**
     * Este metodo verifica la informacion del identificador del documento, el
     * numero RUC del emisor y la fecha de emision.
     *
     * @param docIdentifier    El identificador del documento.
     * @param senderIdentifier El numero RUC del emisor.
     * @param issueDate        La fecha de emision.
     * @throws ValidationException
     */
    public void checkBasicInformation(String docIdentifier, String senderIdentifier, Date issueDate, String correoElectronico, String ccorreoElctroinico, boolean isContingencia) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+checkBasicInformation() [" + this.docUUID + "]");
        }
        /*
         * Validando identificador del documento
         */
        if (StringUtils.isBlank(docIdentifier)) {
            throw new ValidationException(IVenturaError.ERROR_514);
        }
        if (IUBLConfig.SERIE_CORRELATIVE_LENGTH < docIdentifier.length()) {
            throw new ValidationException(IVenturaError.ERROR_515);
        }
        if (!docIdentifier.contains("-")) {
            throw new ValidationException(IVenturaError.ERROR_516);
        }
        if (!docIdentifier.startsWith(IUBLConfig.REMISSION_GUIDE_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.INVOICE_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.BOLETA_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.PERCEPCION_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.RETENTION_SERIE_PREFIX)) {
            if (!isContingencia) throw new ValidationException(IVenturaError.ERROR_517);
        }
        try {
            Integer.valueOf(docIdentifier.substring(5));
        } catch (Exception e) {
            throw new ValidationException(IVenturaError.ERROR_518);
        }

        /*
         * Validando RUC del emisor electronico
         */
        if (IUBLConfig.DOC_RUC_LENGTH != senderIdentifier.length()) {
            throw new ValidationException(IVenturaError.ERROR_519);
        }
        try {
            Long.valueOf(senderIdentifier);
        } catch (Exception e) {
            throw new ValidationException(IVenturaError.ERROR_520);
        }

        /*
         * Validando la fecha de emision
         */
        if (null == issueDate) {
            throw new ValidationException(IVenturaError.ERROR_521);
        }
        if (null == correoElectronico || correoElectronico.isEmpty()) {
            throw new ValidationException(IVenturaError.ERROR_549);
        }
        if (null == ccorreoElctroinico || ccorreoElctroinico.isEmpty()) {
            throw new ValidationException(IVenturaError.ERROR_550);
        }
        if (log.isDebugEnabled()) {
            log.debug("-checkBasicInformation() [" + this.docUUID + "]");
        }
    } //checkBasicInformation

    /**
     * Este metodo verifica la informacion del identificador del documento de
     * COMUNICACION DE BAJA y RESUMEN DIARIO, el numero RUC del emisor y la
     * fecha de emision.
     *
     * @param docIdentifier    El identificador del documento.
     * @param senderIdentifier El numero RUC del emisor.
     * @param issueDate        La fecha de emision.
     * @throws ValidationException
     */
    public void checkBasicInformation2(String docIdentifier, String senderIdentifier, Date issueDate) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+checkBasicInformation2() [" + this.docUUID + "]");
        }
        /*
         * Validando identificador del documento
         */
        if (StringUtils.isBlank(docIdentifier)) {
            throw new ValidationException(IVenturaError.ERROR_529);
        }
        if (!docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.SUMMARY_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX_CPE)) {
            throw new ValidationException(IVenturaError.ERROR_530);
        }

        /*
         * Validando RUC del emisor electronico
         */
        if (IUBLConfig.DOC_RUC_LENGTH != senderIdentifier.length()) {
            throw new ValidationException(IVenturaError.ERROR_519);
        }
        try {
            Long.valueOf(senderIdentifier);
        } catch (Exception e) {
            throw new ValidationException(IVenturaError.ERROR_520);
        }

        /*
         * Validando la fecha de emision
         */
        if (null == issueDate) {
            throw new ValidationException(IVenturaError.ERROR_521);
        }
        if (log.isDebugEnabled()) {
            log.debug("-checkBasicInformation2() [" + this.docUUID + "]");
        }
    } //checkBasicInformation2

    public void checkBasicInformation3(String docIdentifier, String senderIdentifier, String issueDate) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+checkBasicInformation2() [" + this.docUUID + "]");
        }
        /*
         * Validando identificador del documento
         */
        if (StringUtils.isBlank(docIdentifier)) {
            throw new ValidationException(IVenturaError.ERROR_529);
        }
        if (!docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.SUMMARY_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX_CPE)) {
            throw new ValidationException(IVenturaError.ERROR_530);
        }

        /*
         * Validando RUC del emisor electronico
         */
        if (IUBLConfig.DOC_RUC_LENGTH != senderIdentifier.length()) {
            throw new ValidationException(IVenturaError.ERROR_519);
        }
        try {
            Long.valueOf(senderIdentifier);
        } catch (Exception e) {
            throw new ValidationException(IVenturaError.ERROR_520);
        }

        /*
         * Validando la fecha de emision
         */
        if (null == issueDate) {
            throw new ValidationException(IVenturaError.ERROR_521);
        }
        if (log.isDebugEnabled()) {
            log.debug("-checkBasicInformation2() [" + this.docUUID + "]");
        }
    } //checkBasicInformation2

    public void checkRetentionDocument(RetentionType retentionType) throws ValidationException {
        long startTime = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("+checkRetentionDocument() [" + this.docUUID + "]");
        }
        if (log.isDebugEnabled()) {
            log.debug("+checkRetentionDocument() [getSunatRetentionPercent]" + retentionType.getSunatRetentionPercent().getValue().length());
        }
        if (log.isDebugEnabled()) {
            log.debug("+checkRetentionDocument() [getSunatRetentionPercent]" + retentionType.getSunatRetentionPercent().getValue());
        }
        if (retentionType.getSunatRetentionSystemCode().getValue().length() != 2) {
            throw new ValidationException(IVenturaError.ERROR_540);
        }
        if (retentionType.getSunatRetentionPercent().getValue().length() != 1) {
            throw new ValidationException(IVenturaError.ERROR_541);
        }
        if (retentionType.getTotalInvoiceAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException(IVenturaError.ERROR_542);
        }
        if (retentionType.getTotalPaid().getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException(IVenturaError.ERROR_543);
        }
        Optional.of(retentionType).map(RetentionType::getReceiverParty).ifPresent(System.out::println);
        Optional.ofNullable(retentionType).map(RetentionType::getReceiverParty).map(PartyType::getPostalAddress).map(AddressType::getID).map(IdentifierType::getValue).ifPresent(System.out::println);
        Optional.ofNullable(retentionType.getAgentParty()).ifPresent(System.out::println);
        if (Optional.ofNullable(retentionType.getReceiverParty().getPostalAddress()).isPresent()) {
            if (retentionType.getReceiverParty().getPostalAddress().getID().getValue().compareTo("") == 0 || retentionType.getReceiverParty().getPostalAddress().getID().getValue().length() != 6) {
                throw new ValidationException(IVenturaError.ERROR_1100);
            }
        } else {
            throw new ValidationException(IVenturaError.ERROR_1100);
        }
        if (Optional.ofNullable(retentionType.getAgentParty().getPostalAddress()).isPresent()) {
            if (retentionType.getAgentParty().getPostalAddress().getID().getValue().compareTo("") == 0 || retentionType.getAgentParty().getPostalAddress().getID().getValue().length() != 6) {
                throw new ValidationException(IVenturaError.ERROR_1101);
            }
        } else {
            throw new ValidationException(IVenturaError.ERROR_1101);
        }
    }

    public void checkRemissionGuideDocument(DespatchAdviceType retentionType) throws ValidationException {
        long startTime = System.currentTimeMillis();
    }

    public void checkPerceptionDocument(PerceptionType perceptionType) throws ValidationException {
        long startTime = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("+checkPerceptionDocument() [" + this.docUUID + "]");
        }
        if (perceptionType.getSunatTotalCashed().getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException(IVenturaError.ERROR_531);
        }
        if (perceptionType.getTotalInvoiceAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new ValidationException(IVenturaError.ERROR_532);
        }
        if (perceptionType.getTotalInvoiceAmount().getValue().equals(BigDecimal.ZERO)) {
            throw new ValidationException(IVenturaError.ERROR_532);
        }
        if (perceptionType.getReceiverParty().getPostalAddress().getID().getValue().compareTo("") == 0 || perceptionType.getReceiverParty().getPostalAddress().getID().getValue().length() != 6) {
            throw new ValidationException(IVenturaError.ERROR_1000);
        }
        if (perceptionType.getAgentParty().getPostalAddress().getID().getValue().compareTo("") == 0 || perceptionType.getAgentParty().getPostalAddress().getID().getValue().length() != 6) {
            throw new ValidationException(IVenturaError.ERROR_1001);
        }
        for (int i = 0; i < perceptionType.getSunatPerceptionDocumentReference().size(); i++) {
            if (perceptionType.getSunatPerceptionDocumentReference().get(i).getPayment().getPaidAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
                throw new ValidationException(IVenturaError.ERROR_533);
            }
            if (perceptionType.getSunatPerceptionDocumentReference().get(i).getTotalInvoiceAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
                throw new ValidationException(IVenturaError.ERROR_534);
            }
            if (perceptionType.getSunatPerceptionDocumentReference().get(i).getSunatPerceptionInformation().getPerceptionAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
                throw new ValidationException(IVenturaError.ERROR_535);
            }
            if (perceptionType.getSunatPerceptionDocumentReference().get(i).getSunatPerceptionInformation().getSunatNetTotalCashed().getValue().compareTo(BigDecimal.ZERO) == 0) {
                throw new ValidationException(IVenturaError.ERROR_536);
            }
        }
    }
    /**
     * Este metodo realiza la verificacion de los TAG's del documento UBL de
     * tipo FACTURA.
     *
     * @param invoiceType Documento UBL de tipo FACTURA.
     * @throws ValidationException
     */
//	public void checkInvoiceDocument(InvoiceType invoiceType) throws ValidationException
//	{
//		long startTime = System.currentTimeMillis();
//		if (log.isDebugEnabled()) {log.debug("+checkInvoiceDocument() [" + this.docUUID + "]");}
//		
//		boolean isOpGravadaLines = false;
//		boolean isOpInafectaLines = false;
//		boolean isOpExoneradaLines = false;
//		boolean isOpGratuitaLines = false;
//		boolean isTaxIGVLines = false;
//		boolean isTaxISCLines = false;
//		boolean isLineDiscount = false;
//		
//		List<InvoiceLineType> invoiceLines = invoiceType.getInvoiceLine();
//		for (InvoiceLineType iLine : invoiceLines)
//		{
//			for (TaxTotalType taxTotal : iLine.getTaxTotal())
//			{
//				TaxCategoryType taxCategory = taxTotal.getTaxSubtotal().get(0).getTaxCategory();
//				
//				if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID))
//				{
//					isTaxIGVLines = true;
//					
//					String exemptionReasonCode = taxCategory.getTaxExemptionReasonCode().getValue();
//					if (StringUtils.isNotBlank(exemptionReasonCode))
//					{
//						if (exemptionReasonCode.startsWith("1")) /* Op. Gravada */
//						{
//							isOpGravadaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("2")) /* Op. Exonerada */
//						{
//							isOpExoneradaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("3") || exemptionReasonCode.startsWith("4")) /* Op. Inafecta */
//						{
//							isOpInafectaLines = true;
//						}
//												
//						
//						/*
//						 * Cuando una operacion es NO ONEROSA debe terminar
//						 * en un numero.
//						 */
//						if (!exemptionReasonCode.endsWith("0"))
//						{
//							isOpGratuitaLines = true;
//						}
//					}
//				}
//				else if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID))
//				{
//					isTaxISCLines = true;
//				}
//			}
//			
//			if (null != iLine.getAllowanceCharge() && 0<iLine.getAllowanceCharge().size() && null != iLine.getAllowanceCharge().get(0).getAmount() 
//					&& null != iLine.getAllowanceCharge().get(0).getAmount().getValue())
//			{
//				isLineDiscount = true;
//			}
//		} //for
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Se extrajo los valores booleanos de los items.");}
//		
//		/* Validando Operacion GRAVADA */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando operacion GRAVADA.");}
//		//checkOpGravada(invoiceType.getUBLExtensions().getUBLExtension(), isOpGravadaLines);
//		
//		/* Validando Operacion INAFECTA */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando operacion INAFECTA.");}
//		//checkOpInafecta(invoiceType.getUBLExtensions().getUBLExtension(), isOpInafectaLines);
//			
//		/* Validando Operacion EXONERADA */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando operacion EXONERADA.");}
//		//checkOpExonerada(invoiceType.getUBLExtensions().getUBLExtension(), isOpExoneradaLines);
//		
//		/* Validando Operacion GRATUITA */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando operacion GRATUITA.");}
//		//checkOpGratuita(invoiceType.getUBLExtensions().getUBLExtension(), isOpGratuitaLines);
//			
//		/* Validando impuesto IGV */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando impuesto IGV.");}
//		checkTaxTotalIGV(invoiceType.getTaxTotal(), isTaxIGVLines);
//		
//		/* Validando impuesto ISC */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando impuesto ISC.");}
//		checkTaxTotalISC(invoiceType.getTaxTotal(), isTaxISCLines);
//		
//		/*
//		 * Validando el DESCUENTO DE LINEA
//		 * Si existe descuento a nivel de la LINEA, debe existir el TAG
//		 * de DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando DESCUENTO DE LINEA con DESCUENTO TOTAL.");}
//		checkLineDiscount(invoiceType.getUBLExtensions().getUBLExtension(), isLineDiscount);
//		
//		/*
//		 * Validando el DESCUENTO GLOBAL
//		 * Si existe descuento a nivel GLOBAL, debe existir el TAG de
//		 * DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando DESCUENTO GLOBAL con DESCUENTO TOTAL.");}
//		checkGlobalDiscount(invoiceType.getUBLExtensions().getUBLExtension(), invoiceType.getLegalMonetaryTotal().getAllowanceTotalAmount());
//		
//		/*
//		 * Validando MONTOS de TOTALES del comprobante de pago.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkInvoiceDocument() [" + this.docUUID + "] Validando MONTOS de TOTALES.");}
//		checkAdditionalMonetaryTotals(invoiceType.getUBLExtensions().getUBLExtension());
//		if (log.isDebugEnabled()) {log.debug("-checkInvoiceDocument() [" + this.docUUID + "] TIME: " + (System.currentTimeMillis() - startTime) + " ms");}
//	} //checkInvoiceDocument
    /**
     * Este metodo realiza la verificacion de los TAG's del documento UBL de
     * tipo BOLETA.
     *
     * @param boletaType Documento UBL de tipo BOLETA.
     * @throws ValidationException
     */
//	public void checkBoletaDocument(InvoiceType boletaType) throws ValidationException
//	{
//		long startTime = System.currentTimeMillis();
//		if (log.isDebugEnabled()) {log.debug("+checkBoletaDocument() [" + this.docUUID + "]");}
//		
//		boolean isOpGravadaLines = false;
//		boolean isOpInafectaLines = false;
//		boolean isOpExoneradaLines = false;
//		boolean isOpGratuitaLines = false;
//		boolean isTaxIGVLines = false;
//		boolean isTaxISCLines = false;
//		boolean isLineDiscount = false;
//		
//		List<InvoiceLineType> boletaLines = boletaType.getInvoiceLine();
//		for (InvoiceLineType bLine : boletaLines)
//		{
//			for (TaxTotalType taxTotal : bLine.getTaxTotal())
//			{
//				TaxCategoryType taxCategory = taxTotal.getTaxSubtotal().get(0).getTaxCategory();
//				
//				if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID))
//				{
//					isTaxIGVLines = true;
//					
//					String exemptionReasonCode = taxCategory.getTaxExemptionReasonCode().getValue();
//					if (StringUtils.isNotBlank(exemptionReasonCode))
//					{
//						if (exemptionReasonCode.startsWith("1")) /* Op. Gravada */
//						{
//							isOpGravadaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("2")) /* Op. Exonerada */
//						{
//							isOpExoneradaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("3") || exemptionReasonCode.startsWith("4")) /* Op. Inafecta */
//						{
//							isOpInafectaLines = true;
//						}
//						
//						/*
//						 * Cuando una operacion es NO ONEROSA debe terminar
//						 * en un numero.
//						 */
//						if (!exemptionReasonCode.endsWith("0"))
//						{
//							isOpGratuitaLines = true;
//						}
//					}
//				}
//				else if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID))
//				{
//					isTaxISCLines = true;
//				}
//			}
//			
//			if (null != bLine.getAllowanceCharge() && 0<bLine.getAllowanceCharge().size() && null != bLine.getAllowanceCharge().get(0).getAmount() 
//					&& null != bLine.getAllowanceCharge().get(0).getAmount().getValue())
//			{
//				isLineDiscount = true;
//			}
//		} //for
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Se extrajo los valores booleanos de los items.");}
//
//		/* Validando Operacion GRAVADA */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando operacion GRAVADA.");}
//		//checkOpGravada(boletaType.getUBLExtensions().getUBLExtension(), isOpGravadaLines);
//		
//		/* Validando Operacion INAFECTA */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando operacion INAFECTA.");}
//		//checkOpInafecta(boletaType.getUBLExtensions().getUBLExtension(), isOpInafectaLines);
//			
//		/* Validando Operacion EXONERADA */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando operacion EXONERADA.");}
//		//checkOpExonerada(boletaType.getUBLExtensions().getUBLExtension(), isOpExoneradaLines);
//		
//		/* Validando Operacion GRATUITA */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando operacion GRATUITA.");}
//		//checkOpGratuita(boletaType.getUBLExtensions().getUBLExtension(), isOpGratuitaLines);
//			
//		/* Validando impuesto IGV */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando impuesto IGV.");}
//		checkTaxTotalIGV(boletaType.getTaxTotal(), isTaxIGVLines);
//		
//		/* Validando impuesto ISC */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando impuesto ISC.");}
//		checkTaxTotalISC(boletaType.getTaxTotal(), isTaxISCLines);
//		
//		/*
//		 * Validando el DESCUENTO DE LINEA
//		 * Si existe descuento a nivel de la LINEA, debe existir el TAG
//		 * de DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando DESCUENTO DE LINEA con DESCUENTO TOTAL.");}
//		checkLineDiscount(boletaType.getUBLExtensions().getUBLExtension(), isLineDiscount);
//		
//		/*
//		 * Validando el DESCUENTO GLOBAL
//		 * Si existe descuento a nivel GLOBAL, debe existir el TAG de
//		 * DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando DESCUENTO GLOBAL con DESCUENTO TOTAL.");}
//		checkGlobalDiscount(boletaType.getUBLExtensions().getUBLExtension(), boletaType.getLegalMonetaryTotal().getAllowanceTotalAmount());
//		
//		/*
//		 * Validando MONTOS de TOTALES del comprobante de pago.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkBoletaDocument() [" + this.docUUID + "] Validando MONTOS de TOTALES.");}
//		checkAdditionalMonetaryTotals(boletaType.getUBLExtensions().getUBLExtension());
//		if (log.isDebugEnabled()) {log.debug("-checkBoletaDocument() [" + this.docUUID + "] TIME: " + (System.currentTimeMillis() - startTime) + " ms");}
//	} //checkBoletaDocument
    /**
     * Este metodo realiza la verificacion de los TAG's del documento UBL de
     * tipo NOTA DE CREDITO.
     *
     * @param creditNoteType Documento UBL de tipo NOTA DE CREDITO.
     * @throws ValidationException
     */
//	public void checkCreditNoteDocument(CreditNoteType creditNoteType) throws ValidationException
//	{
//		long startTime = System.currentTimeMillis();
//		if (log.isDebugEnabled()) {log.debug("+checkCreditNoteDocument() [" + this.docUUID + "]");}
//		
//		boolean isOpGravadaLines = false;
//		boolean isOpInafectaLines = false;
//		boolean isOpExoneradaLines = false;
//		boolean isOpGratuitaLines = false;
//		boolean isTaxIGVLines = false;
//		boolean isTaxISCLines = false;
//		
//		List<CreditNoteLineType> creditNoteLines = creditNoteType.getCreditNoteLine();
//		for (CreditNoteLineType cnLine : creditNoteLines)
//		{
//			for (TaxTotalType taxTotal : cnLine.getTaxTotal())
//			{
//				TaxCategoryType taxCategory = taxTotal.getTaxSubtotal().get(0).getTaxCategory();
//				
//				if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID))
//				{
//					isTaxIGVLines = true;
//					
//					String exemptionReasonCode = taxCategory.getTaxExemptionReasonCode().getValue();
//					if (StringUtils.isNotBlank(exemptionReasonCode))
//					{
//						if (exemptionReasonCode.startsWith("1")) /* Op. Gravada */
//						{
//							isOpGravadaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("2")) /* Op. Exonerada */
//						{
//							isOpExoneradaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("3") || exemptionReasonCode.startsWith("4")) /* Op. Inafecta */
//						{
//							isOpInafectaLines = true;
//						}
//						
//						/*
//						 * Cuando una operacion es NO ONEROSA debe terminar
//						 * en un numero.
//						 */
//						if (!exemptionReasonCode.endsWith("0"))
//						{
//							isOpGratuitaLines = true;
//						}
//					}
//				}
//				else if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID))
//				{
//					isTaxISCLines = true;
//				}
//			}
//		} //for
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Se extrajo los valores booleanos de los items.");}
//		
//		/* Validando Operacion GRAVADA */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando operacion GRAVADA.");}
//		//checkOpGravada(creditNoteType.getUBLExtensions().getUBLExtension(), isOpGravadaLines);
//		
//		/* Validando Operacion INAFECTA */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando operacion INAFECTA.");}
//		//checkOpInafecta(creditNoteType.getUBLExtensions().getUBLExtension(), isOpInafectaLines);
//			
//		/* Validando Operacion EXONERADA */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando operacion EXONERADA.");}
//		//checkOpExonerada(creditNoteType.getUBLExtensions().getUBLExtension(), isOpExoneradaLines);
//		
//		/* Validando Operacion GRATUITA */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando operacion GRATUITA.");}
//		//checkOpGratuita(creditNoteType.getUBLExtensions().getUBLExtension(), isOpGratuitaLines);
//			
//		/* Validando impuesto IGV */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando impuesto IGV.");}
//		checkTaxTotalIGV(creditNoteType.getTaxTotal(), isTaxIGVLines);
//		
//		/* Validando impuesto ISC */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocument() [" + this.docUUID + "] Validando impuesto ISC.");}
//		checkTaxTotalISC(creditNoteType.getTaxTotal(), isTaxISCLines);
//		
//		/*
//		 * Validando el DESCUENTO GLOBAL
//		 * Si existe descuento a nivel GLOBAL, debe existir el TAG de
//		 * DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocuments() [" + this.docUUID + "] Validando DESCUENTO GLOBAL con DESCUENTO TOTAL.");}
//		checkGlobalDiscount(creditNoteType.getUBLExtensions().getUBLExtension(), creditNoteType.getLegalMonetaryTotal().getAllowanceTotalAmount());
//		
//		/*
//		 * Validando MONTOS de TOTALES del comprobante de pago.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocuments() [" + this.docUUID + "] Validando MONTOS de TOTALES.");}
//		checkAdditionalMonetaryTotals(creditNoteType.getUBLExtensions().getUBLExtension());
//		
//		/*
//		 * Validando el tipo de NOTA DE CREDITO
//		 * 
//		 * Valido hasta: 10/2015 (MM/yyyy)
//		 * 
//		 * RESOLUCION DE SUPERINTENDENCIA N 185-2015/SUNAT
//		 * ANEXO G (ANEXO N8)	: CATALOGO DE CODIGOS
//		 * I. CATALOGO No.09: Codigos de Tipo de Nota de Credito Electronica
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocuments() [" + this.docUUID + "] Validando el TIPO DE NOTA DE CREDITO.");}
//		checkTypeOfCreditNote(creditNoteType.getDiscrepancyResponse().get(0).getResponseCode().getValue());
//		
//		/*
//		 * Validando el codigo del tipo de documento de refencia
//		 * 
//		 * Valido hasta: 10/2015 (MM/yyyy)
//		 * 
//		 * RESOLUCION DE SUPERINTENDENCIA N 185-2015/SUNAT
//		 * ANEXO G (ANEXO N8)	: CATALOGO DE CODIGOS
//		 * A. CATALOGO No.01: Codigo de Tipo de documento.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkCreditNoteDocuments() [" + this.docUUID + "] Validando el CODIGO del DOCUMENTO DE REFENCIA.");}
//		checkReferenceDocumentCode(creditNoteType.getBillingReference());
//		if (log.isDebugEnabled()) {log.debug("-checkCreditNoteDocument() [" + this.docUUID + "] TIME: " + (System.currentTimeMillis() - startTime) + " ms");}
//	} //checkCreditNoteDocument

    /**
     * Este metodo realiza la verificacion de los TAG's del documento UBL de
     * tipo NOTA DE DEBITO.
     *
     * @throws ValidationException
     */
//	public void checkDebitNoteDocument(DebitNoteType debitNoteType) throws ValidationException
//	{
//		long startTime = System.currentTimeMillis();
//		if (log.isDebugEnabled()) {log.debug("+checkDebitNoteDocument() [" + this.docUUID + "]");}
//		
//		boolean isOpGravadaLines = false;
//		boolean isOpInafectaLines = false;
//		boolean isOpExoneradaLines = false;
//		boolean isOpGratuitaLines = false;
//		boolean isTaxIGVLines = false;
//		boolean isTaxISCLines = false;
//		
//		List<DebitNoteLineType> debitNoteLines = debitNoteType.getDebitNoteLine();
//		for (DebitNoteLineType dnLine : debitNoteLines)
//		{
//			for (TaxTotalType taxTotal : dnLine.getTaxTotal())
//			{
//				TaxCategoryType taxCategory = taxTotal.getTaxSubtotal().get(0).getTaxCategory();
//				
//				if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID))
//				{
//					isTaxIGVLines = true;
//					
//					String exemptionReasonCode = taxCategory.getTaxExemptionReasonCode().getValue();
//					if (StringUtils.isNotBlank(exemptionReasonCode))
//					{
//						if (exemptionReasonCode.startsWith("1")) /* Op. Gravada */
//						{
//							isOpGravadaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("2")) /* Op. Exonerada */
//						{
//							isOpExoneradaLines = true;
//						}
//						else if (exemptionReasonCode.startsWith("3") || exemptionReasonCode.startsWith("4")) /* Op. Inafecta */
//						{
//							isOpInafectaLines = true;
//						}
//						
//						/*
//						 * Cuando una operacion es NO ONEROSA debe terminar
//						 * en un numero.
//						 */
//						if (!exemptionReasonCode.endsWith("0"))
//						{
//							isOpGratuitaLines = true;
//						}
//					}
//				}
//				else if (taxCategory.getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID))
//				{
//					isTaxISCLines = true;
//				}
//			}
//		} //for
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Se extrajo los valores booleanos de los items.");}
//		
//		/* Validando Operacion GRAVADA */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando operacion GRAVADA.");}
//		//checkOpGravada(debitNoteType.getUBLExtensions().getUBLExtension(), isOpGravadaLines);
//		
//		/* Validando Operacion INAFECTA */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando operacion INAFECTA.");}
//		//checkOpInafecta(debitNoteType.getUBLExtensions().getUBLExtension(), isOpInafectaLines);
//			
//		/* Validando Operacion EXONERADA */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando operacion EXONERADA.");}
//		//checkOpExonerada(debitNoteType.getUBLExtensions().getUBLExtension(), isOpExoneradaLines);
//		
//		/* Validando Operacion GRATUITA */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando operacion GRATUITA.");}
//		//checkOpGratuita(debitNoteType.getUBLExtensions().getUBLExtension(), isOpGratuitaLines);
//			
//		/* Validando impuesto IGV */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando impuesto IGV.");}
//		checkTaxTotalIGV(debitNoteType.getTaxTotal(), isTaxIGVLines);
//		
//		/* Validando impuesto ISC */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando impuesto ISC.");}
//		checkTaxTotalISC(debitNoteType.getTaxTotal(), isTaxISCLines);
//		
//		/*
//		 * Validando el DESCUENTO GLOBAL
//		 * Si existe descuento a nivel GLOBAL, debe existir el TAG de
//		 * DESCUENTO TOTAL.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando DESCUENTO GLOBAL con DESCUENTO TOTAL.");}
//		checkGlobalDiscount(debitNoteType.getUBLExtensions().getUBLExtension(), debitNoteType.getRequestedMonetaryTotal().getAllowanceTotalAmount());
//		
//		/*
//		 * Validando MONTOS de TOTALES del comprobante de pago.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando MONTOS de TOTALES.");}
//		checkAdditionalMonetaryTotals(debitNoteType.getUBLExtensions().getUBLExtension());
//		
//		/*
//		 * Validando el tipo de NOTA DE CREDITO
//		 * 
//		 * Valido hasta: 10/2015 (MM/yyyy)
//		 * 
//		 * RESOLUCION DE SUPERINTENDENCIA N 185-2015/SUNAT
//		 * ANEXO G (ANEXO N8)	: CATALOGO DE CODIGOS
//		 * J. CATALOGO No.10: Codigos de Tipo de Nota de Debito Electronica
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando el TIPO DE NOTA DE CREDITO.");}
//		checkTypeOfDebitNote(debitNoteType.getDiscrepancyResponse().get(0).getResponseCode().getValue());
//		
//		/*
//		 * Validando el codigo del tipo de documento de refencia
//		 * 
//		 * Valido hasta: 10/2015 (MM/yyyy)
//		 * 
//		 * RESOLUCION DE SUPERINTENDENCIA N 185-2015/SUNAT
//		 * ANEXO G (ANEXO N8)	: CATALOGO DE CODIGOS
//		 * A. CATALOGO No.01: Codigo de Tipo de documento.
//		 */
//		if (log.isDebugEnabled()) {log.debug("checkDebitNoteDocument() [" + this.docUUID + "] Validando el CODIGO del DOCUMENTO DE REFENCIA.");}
//		checkReferenceDocumentCode(debitNoteType.getBillingReference());
//		if (log.isDebugEnabled()) {log.debug("-checkDebitNoteDocument() [" + this.docUUID + "] TIME: " + (System.currentTimeMillis() - startTime) + " ms");}
//	} //checkDebitNoteDocument
    private void checkOpGravada(List<UBLExtensionType> ublExtension, boolean isOpGravadaLines) throws ValidationException {
        boolean isGravadaTotal = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_1001);
        if (isOpGravadaLines) {
            if (!isGravadaTotal) {
                log.error("checkOpGravada() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_502.getMessage());
                throw new ValidationException(IVenturaError.ERROR_502);
            }
        } else {
            if (isGravadaTotal) {
                log.error("checkOpGravada() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_503.getMessage());
                throw new ValidationException(IVenturaError.ERROR_503);
            }
        }
    } //checkOpGravada

    private void checkOpInafecta(List<UBLExtensionType> ublExtension, boolean isOpInafectaLines) throws ValidationException {
        boolean isInafectaTotal = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_1002);
        if (isOpInafectaLines) {
            if (!isInafectaTotal) {
                log.error("checkOpInafecta() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_504.getMessage());
                throw new ValidationException(IVenturaError.ERROR_504);
            }
        } else {
            if (isInafectaTotal) {
                log.error("checkOpInafecta() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_505.getMessage());
                throw new ValidationException(IVenturaError.ERROR_505);
            }
        }
    } //checkOpInafecta

    private void checkOpExonerada(List<UBLExtensionType> ublExtension, boolean isOpExoneradaLines) throws ValidationException {
        boolean isExoneradaTotal = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_1003);
        if (isOpExoneradaLines) {
            if (!isExoneradaTotal) {
                log.error("checkOpExonerada() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_506.getMessage());
                throw new ValidationException(IVenturaError.ERROR_506);
            }
        } else {
            if (isExoneradaTotal) {
                log.error("checkOpExonerada() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_507.getMessage());
                throw new ValidationException(IVenturaError.ERROR_507);
            }
        }
    } //checkOpExonerada

    private void checkOpGratuita(List<UBLExtensionType> ublExtension, boolean isOpGratuitaLines) throws ValidationException {
        boolean isGratuitaTotal = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_1004);
        if (isOpGratuitaLines) {
            if (!isGratuitaTotal) {
                log.error("checkOpGratuita() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_508.getMessage());
                throw new ValidationException(IVenturaError.ERROR_508);
            }
        } else {
            if (isGratuitaTotal) {
                log.error("checkOpGratuita() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_509.getMessage());
                throw new ValidationException(IVenturaError.ERROR_509);
            }
        }
    } //checkOpGratuita

    private void checkTaxTotalIGV(List<TaxTotalType> taxTotalList, boolean isTaxIGVLines) throws ValidationException {
        boolean isTaxTotalIGV = false;
        for (TaxTotalType taxTotal : taxTotalList) {
            if (taxTotal.getTaxSubtotal().get(0).getTaxCategory().getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_IGV_ID)) {
                isTaxTotalIGV = true;
                break;
            }
        }
        if (isTaxIGVLines) {
            if (!isTaxTotalIGV) {
                log.error("checkTaxTotalIGV() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_510.getMessage());
                throw new ValidationException(IVenturaError.ERROR_510.getMessage());
            }
        } else {
            if (isTaxTotalIGV) {
                log.error("checkTaxTotalIGV() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_511.getMessage());
                throw new ValidationException(IVenturaError.ERROR_511.getMessage());
            }
        }
    } //checkTaxTotalIGV

    private void checkTaxTotalISC(List<TaxTotalType> taxTotalList, boolean isTaxISCLines) throws ValidationException {
        boolean isTaxTotalISC = false;
        for (TaxTotalType taxTotal : taxTotalList) {
            if (taxTotal.getTaxSubtotal().get(0).getTaxCategory().getTaxScheme().getID().getValue().equalsIgnoreCase(IUBLConfig.TAX_TOTAL_ISC_ID)) {
                isTaxTotalISC = true;
                break;
            }
        }
        if (isTaxISCLines) {
            if (!isTaxTotalISC) {
                log.error("checkTaxTotalISC() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_512.getMessage());
                throw new ValidationException(IVenturaError.ERROR_512.getMessage());
            }
        } else {
            if (isTaxTotalISC) {
                log.error("checkTaxTotalISC() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_513.getMessage());
                throw new ValidationException(IVenturaError.ERROR_513.getMessage());
            }
        }
    } //checkTaxTotalISC

    private void checkLineDiscount(List<UBLExtensionType> ublExtension, boolean isLineDiscount) throws ValidationException {
        /*
         * Se verifica que si existe descuento a nivel de los ITEMS, debe existir el TAG de
         * DESCUENTO TOTAL.
         */
        if (isLineDiscount) {
            boolean isGlobalDiscount = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_2005);
            if (!isGlobalDiscount) {
                log.error("checkLineDiscount() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_522.getMessage());
                throw new ValidationException(IVenturaError.ERROR_522);
            }
        }
    } //checkLineDiscount

    private void checkGlobalDiscount(List<UBLExtensionType> ublExtension, AllowanceTotalAmountType allowanceTotalAmount) throws ValidationException {
        /*
         * Se verifica que si existe descuento GLOBAL, debe existir el TAG de
         * DESCUENTO TOTAL.
         */
        if (null != allowanceTotalAmount && null != allowanceTotalAmount.getValue()) {
            boolean isTotalDiscount = getAdditionalMonetaryTotal(ublExtension, IUBLConfig.ADDITIONAL_MONETARY_2005);
            if (!isTotalDiscount) {
                log.error("checkLineDiscount() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_523.getMessage());
                throw new ValidationException(IVenturaError.ERROR_523);
            }
        }
    } //checkGlobalDiscount

    private void checkAdditionalMonetaryTotals(List<UBLExtensionType> ublExtension) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+-checkAdditionalMonetaryTotals() [" + this.docUUID + "]");
        }
        String idValue = null;
        try {
            NodeList parentNodeList = ublExtension.get(0).getExtensionContent().getAny().getChildNodes();
            for (int i = 0; i < parentNodeList.getLength(); i++) {
                if (StringUtils.isNotBlank(parentNodeList.item(i).getLocalName()) && parentNodeList.item(i).getLocalName().equalsIgnoreCase(IPDFCreatorConfig.UBL_ADDITIONAL_MONETARY_TOTAL)) {
                    NodeList nodeList = parentNodeList.item(i).getChildNodes();
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (StringUtils.isNotBlank(nodeList.item(j).getLocalName())) {
                            String nodeValue = nodeList.item(j).getTextContent();
                            if (nodeList.item(j).getLocalName().equalsIgnoreCase(IPDFCreatorConfig.UBL_ID)) {
                                idValue = nodeValue;
                            }
                        }
                    }
                    if (!checkAddiMonTotalID(idValue)) {
                        ErrorObj error = new ErrorObj(IVenturaError.ERROR_524.getId(), MessageFormat.format(IVenturaError.ERROR_524.getMessage(), idValue));
                        log.error("checkAdditionalMonetaryTotals() [" + this.docUUID + "] ERROR: " + error.getMessage());
                        throw new ValidationException(error);
                    }
                }
            } //for
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("checkAdditionalMonetaryTotals() [" + this.docUUID + "] ERROR: " + e.getMessage());
            throw new ValidationException(IVenturaError.ERROR_501);
        }
    } //checkAdditionalMonetaryTotals

    private boolean checkAddiMonTotalID(String idValue) {
        boolean flag = false;
        for (String totalID : this.totalIDList) {
            if (totalID.equalsIgnoreCase(idValue)) {
                flag = true;
                break;
            }
        }
        return flag;
    } //checkAddiMonTotalID

    private boolean getAdditionalMonetaryTotal(List<UBLExtensionType> ublExtension, String addiMonetaryValue) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+-getAdditionalMonetaryTotal() [" + this.docUUID + "] addiMonetaryValue: " + addiMonetaryValue);
        }
        String idValue = null;
        String payableAmountValue = null;
        boolean flag = false;
        try {
            NodeList parentNodeList = ublExtension.get(0).getExtensionContent().getAny().getChildNodes();
            for (int i = 0; i < parentNodeList.getLength(); i++) {
                if (StringUtils.isNotBlank(parentNodeList.item(i).getLocalName()) && parentNodeList.item(i).getLocalName().equalsIgnoreCase(IPDFCreatorConfig.UBL_ADDITIONAL_MONETARY_TOTAL)) {
                    NodeList nodeList = parentNodeList.item(i).getChildNodes();
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        if (StringUtils.isNotBlank(nodeList.item(j).getLocalName())) {
                            String nodeValue = nodeList.item(j).getTextContent();
                            if (nodeList.item(j).getLocalName().equalsIgnoreCase(IPDFCreatorConfig.UBL_ID)) {
                                idValue = nodeValue;
                            } else if (nodeList.item(j).getLocalName().equalsIgnoreCase(IPDFCreatorConfig.UBL_PAYABLE_AMOUNT)) {
                                payableAmountValue = nodeValue;
                            }
                        }
                    }
                    if (idValue.equalsIgnoreCase(addiMonetaryValue)) {
                        if (StringUtils.isNotBlank(payableAmountValue)) {
                            flag = true;
                            break;
                        }
                    }
                }
            } //for
        } catch (Exception e) {
            log.error("getAdditionalMonetaryTotal() [" + this.docUUID + "] ERROR: " + e.getMessage());
            throw new ValidationException(IVenturaError.ERROR_501);
        }
        return flag;
    } //getAdditionalMonetaryTotal

    private void checkTypeOfCreditNote(String value) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+-checkTypeOfCreditNote() [" + this.docUUID + "]");
        }
        boolean flag = false;
        for (String typeOfCn : this.typeOfCreditNoteList) {
            if (typeOfCn.equalsIgnoreCase(value)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            log.error("checkTypeOfCreditNote() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_525.getMessage());
            throw new ValidationException(IVenturaError.ERROR_525);
        }
    } //checkTypeOfCreditNote

    private void checkTypeOfDebitNote(String value) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+-checkTypeOfDebitNote() [" + this.docUUID + "]");
        }
        boolean flag = false;
        for (String typeOfDn : this.typeOfDebitNoteList) {
            if (typeOfDn.equalsIgnoreCase(value)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            log.error("checkTypeOfDebitNote() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_526.getMessage());
            throw new ValidationException(IVenturaError.ERROR_526);
        }
    } //checkTypeOfDebitNote

    private void checkReferenceDocumentCode(List<BillingReferenceType> billingReferenceList) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("+-checkReferenceDocumentCode() [" + this.docUUID + "]");
        }
        if (null != billingReferenceList && 0 < billingReferenceList.size()) {
            for (BillingReferenceType billingReference : billingReferenceList) {
                if (!checkDocumentType(billingReference.getInvoiceDocumentReference().getDocumentTypeCode().getValue())) {
                    log.error("checkReferenceDocumentCode() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_528.getMessage());
                    throw new ValidationException(IVenturaError.ERROR_528);
                }
            }
        } else {
            log.error("checkReferenceDocumentCode() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_527.getMessage());
            throw new ValidationException(IVenturaError.ERROR_527);
        }
    }

    private boolean checkDocumentType(String value) {
        boolean flag = false;
        for (String docType : this.refDocumentTypeList) {
            if (docType.equalsIgnoreCase(value)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
