package org.ventura.wsclient.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ventura.wsclient.exception.ValidationException;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.error.IVenturaError;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase contiene metodos para validar los documento UBL.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class ValidationHandler
{
	private final Logger logger = Logger.getLogger(ValidationHandler.class);
	
	private List<String> totalIDList = null;
	private List<String> typeOfCreditNoteList = null;
	private List<String> typeOfDebitNoteList = null;
	private List<String> refDocumentTypeList = null;
	private String docUUID;
	
	
	/**
	 * Constructor privado para evitar instancias.
	 * 
	 * @param docUUID
	 * 		UUID que identifica al documento.
	 */
	private ValidationHandler(String docUUID)
	{
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
	 * @param docUUID
	 * 		UUID que identifica al documento.
	 * @return
	 * 		Retorna una nueva instancia de la clase ValidationHandler.
	 */
	public static synchronized ValidationHandler newInstance(String docUUID)
	{
		return new ValidationHandler(docUUID);
	} //newInstance
	

	public void checkBasicInformation3(String docIdentifier, String senderIdentifier, String issueDate) throws ValidationException
	{
		if (logger.isDebugEnabled()) {logger.debug("+checkBasicInformation2() [" + this.docUUID + "]");}
		/*
		 * Validando identificador del documento
		 */
		if (StringUtils.isBlank(docIdentifier))
		{
			throw new ValidationException(IVenturaError.ERROR_529);
		}
		if (!docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.SUMMARY_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX_CPE) )
		{
			throw new ValidationException(IVenturaError.ERROR_530);
		}
		
		/*
		 * Validando RUC del emisor electronico
		 */
		if (IUBLConfig.DOC_RUC_LENGTH != senderIdentifier.length())
		{
			throw new ValidationException(IVenturaError.ERROR_519);
		}
		try
		{
			Long.valueOf(senderIdentifier);
		}
		catch (Exception e)
		{
			throw new ValidationException(IVenturaError.ERROR_520);
		}
		
		/*
		 * Validando la fecha de emision
		 */
		if (null == issueDate)
		{
			throw new ValidationException(IVenturaError.ERROR_521);
		}
		if (logger.isDebugEnabled()) {logger.debug("-checkBasicInformation2() [" + this.docUUID + "]");}
	} //checkBasicInformation2

} //ValidationHandler
