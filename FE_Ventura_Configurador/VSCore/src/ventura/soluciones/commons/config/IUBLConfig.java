package ventura.soluciones.commons.config;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Esta interfaz contiene las constantes validas establecidas por Sunat.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */

//
public interface IUBLConfig {

	public static final ArrayList<BigDecimal> lstImporteIGV = new ArrayList<BigDecimal>();

	/**
	 * Codigo de tipos de documentos
	 */
	public static final String ID_DOC_WITHOUT_RUC = "0";
	public static final String ID_DOC_DNI = "1";
	public static final String ID_DOC_FOREIGN_CARD = "4";
	public static final String ID_DOC_RUC = "6";
	public static final String ID_DOC_PASSPORT = "7";
	public static final String ID_DOC_DIPLOMATIC_ID = "A";

	public static final int DOC_RUC_LENGTH = 11;

	/**
	 * Codigo de pais
	 */
	public static final String COUNTRY_CODE = "PE";

	/**
	 * Version del estandar UBL utilizado en los documentos
	 */
	public static final String UBL_VERSION_ID = "2.0";

	/**
	 * Version del valor CustomizationIDType
	 */
	public static final String CUSTOMIZATION_ID = "1.0";

	/**
	 * Codigos que representan un tipo de documento UBL.
	 */
	public static final String DOC_INVOICE_CODE = "01";
	public static final String DOC_BOLETA_CODE = "03";
	public static final String DOC_CREDIT_NOTE_CODE = "07";
	public static final String DOC_RETENTION_CODE = "20";
	public static final String DOC_PERCEPTION_CODE = "40";
	public static final String DOC_DEBIT_NOTE_CODE = "08";
	public static final String DOC_SENDER_REMISSION_GUIDE_CODE = "09";
	public static final String DOC_MACHINE_TICKET_CODE = "12";
	public static final String DOC_FINANCIAL_BANKS_CODE = "13";
	public static final String DOC_BANK_INSURANCE_CODE = "18";
	public static final String DOC_ISSUED_BY_AFP_CODE = "31";
	public static final String DOC_TRANSPORT_REMISSION_GUIDE_CODE = "56";
	public static final String DOC_SUMMARY_DOCUMENT_CODE = "RC";
	public static final String DOC_VOIDED_DOCUMENT_CODE = "RA";

	/**
	 * Codigo de tipo de Nota de Credito - 01 : Anulacion de la operacion - 02 :
	 * Anulacion por error en el RUC - 03 : Correccion por error en la
	 * descripcion - 04 : Descuento global - 05 : Descuento por item - 06 :
	 * Devolucion total - 07 : Devolucion por item - 08 : Bonificacion - 09 :
	 * Disminucion en el valor - 10 : Otros conceptos
	 */
	public static final String CREDIT_NOTE_TYPE_01 = "01";
	public static final String CREDIT_NOTE_TYPE_02 = "02";
	public static final String CREDIT_NOTE_TYPE_03 = "03";
	public static final String CREDIT_NOTE_TYPE_04 = "04";
	public static final String CREDIT_NOTE_TYPE_05 = "05";
	public static final String CREDIT_NOTE_TYPE_06 = "06";
	public static final String CREDIT_NOTE_TYPE_07 = "07";
	public static final String CREDIT_NOTE_TYPE_08 = "08";
	public static final String CREDIT_NOTE_TYPE_09 = "09";
	public static final String CREDIT_NOTE_TYPE_10 = "10";

	/**
	 * Codigo de tipo de Nota de Debito - 01 : Intereses por mora - 02 : Aumento
	 * en el valor - 03 : Penalidades / otros conceptos
	 */
	public static final String DEBIT_NOTE_TYPE_01 = "01";
	public static final String DEBIT_NOTE_TYPE_02 = "02";
	public static final String DEBIT_NOTE_TYPE_03 = "03";

	/**
	 * Cantidad de caracteres del identificador del documento
	 */
	public static final int SERIE_CORRELATIVE_LENGTH = 13;

	public static final String INVOICE_SERIE_PREFIX = "F";
	public static final String RETENTION_SERIE_PREFIX = "R";
	public static final String BOLETA_SERIE_PREFIX = "B";
	public static final String PERCEPCION_SERIE_PREFIX = "P";
	public static final String VOIDED_SERIE_PREFIX = "RA";
	public static final String SUMMARY_SERIE_PREFIX = "RC";
	public static final String VOIDED_SERIE_PREFIX_CPE = "RR";

	/**
	 * Parametros de los impuestos en UBL - ID : Identificador del impuesto -
	 * NAME : Nombre del impuesto - CODE : Codigo asignado al impuesto
	 */
	public static final String TAX_TOTAL_IGV_ID = "1000";
	public static final String TAX_TOTAL_ISC_ID = "2000";
        public static final String TAX_TOTAL_EXP_ID = "9995";
        public static final String TAX_TOTAL_GRT_ID = "9996";
        public static final String TAX_TOTAL_EXO_ID = "9997";
        public static final String TAX_TOTAL_INA_ID = "9998";
	public static final String TAX_TOTAL_OTH_ID = "9999";

	public static final String TAX_TOTAL_IGV_NAME = "IGV";
	public static final String TAX_TOTAL_ISC_NAME = "ISC";
	public static final String TAX_TOTAL_EXP_NAME = "EXP";
        public static final String TAX_TOTAL_GRT_NAME = "GRT";
        public static final String TAX_TOTAL_EXO_NAME = "EXO";
        public static final String TAX_TOTAL_INA_NAME = "INA";
        public static final String TAX_TOTAL_OTH_NAME = "OTROS";

	public static final String TAX_TOTAL_IGV_CODE = "VAT";
	public static final String TAX_TOTAL_ISC_CODE = "EXC";
	public static final String TAX_TOTAL_FRE_CODE = "FRE";
        public static final String TAX_TOTAL_VAT_CODE = "VAT";
        public static final String TAX_TOTAL_OTH_CODE = "OTH";

	/**
	 * Codigo de tipo de Valor Venta
	 */
	public static final String INSTRUCTION_ID_GRAVADO = "01";
	public static final String INSTRUCTION_ID_EXONERADO = "02";
	public static final String INSTRUCTION_ID_INAFECTO = "03";

	/**
	 * Tag's del AlternativeConditionPrice.
	 */
	public static final String ALTERNATIVE_CONDICION_UNIT_PRICE = "01";
	public static final String ALTERNATIVE_CONDICION_REFERENCE_VALUE = "02";

	/**
	 * Decimales en los tax del UBL
	 */
	public static final int DECIMAL_TAX_TOTAL_AMOUNT = 2;
	public static final int DECIMAL_TAX_TOTAL_IGV_PERCENT = 2;
	public static final int DECIMAL_PREPAID_PAYMENT_AMOUNT = 2;
	public static final int DECIMAL_LEGAL_MONERARY_LINEEXTENSION = 2;
	public static final int DECIMAL_LEGAL_MONETARY_PAYABLE_AMOUNT = 2;
	public static final int DECIMAL_LEGAL_MONETARY_ALLOWANCE_TOTAL_AMOUNT = 2;
	public static final int DECIMAL_LEGAL_MONETARY_TOTAL_PREPAID = 2;
	public static final int DECIMAL_LINE_QUANTITY = 10;
	public static final int DECIMAL_LINE_TAX_AMOUNT = 2;
	public static final int DECIMAL_LINE_TAX_IGV_PERCENT = 2;
	public static final int DECIMAL_LINE_UNIT_VALUE = 10;
	public static final int DECIMAL_LINE_EXTENSION_AMOUNT = 2;
	public static final int DECIMAL_LINE_UNIT_PRICE = 10;
	public static final int DECIMAL_LINE_REFERENCE_VALUE = 2;
	public static final int DECIMAL_ADDITIONAL_MONETARY_TOTAL_PAYABLE_AMOUNT = 2;

	public static final String CONTRACT_DOC_REF_PAYMENT_COND_CODE = "pay_cond";
	public static final String CONTRACT_DOC_REF_SELL_ORDER_CODE = "cu01";
	public static final String CONTRACT_DOC_REF_SELLER_CODE = "cu02";
	public static final String CONTRACT_DOC_REF_EXTRA_DOC = "cu03";

	/**
	 * Formato de la fecha de emision del documento UBL.
	 */
	public static final String ISSUEDATE_FORMAT = "yyyy-MM-dd";
	public static final String REFERENCEDATE_FORMAT = "yyyy-MM-dd";
	public static final String DUEDATE_FORMAT = "yyyy-MM-dd";

	public static final String HIDDEN_UVALUE = "hidden_uvalue";

	/**
	 * Formato de la fecha para el nombre del cocumento.
	 */
	public static final String DOCUMENT_DATE_FORMAT = "yyyyMMdd";

	public static final String SERIE_BB11 = "BB11";
	public static final String SERIE_BB12 = "BB12";

	public static final String ADDITIONAL_MONETARY_1001 = "1001";
	public static final String ADDITIONAL_MONETARY_1002 = "1002";
	public static final String ADDITIONAL_MONETARY_1003 = "1003";
	public static final String ADDITIONAL_MONETARY_1004 = "1004";
	public static final String ADDITIONAL_MONETARY_1005 = "1005";
	public static final String ADDITIONAL_MONETARY_2001 = "2001";
	public static final String ADDITIONAL_MONETARY_2002 = "2002";
	public static final String ADDITIONAL_MONETARY_2003 = "2003";
	public static final String ADDITIONAL_MONETARY_2004 = "2004";
	public static final String ADDITIONAL_MONETARY_2005 = "2005";
	public static final String ADDITIONAL_MONETARY_3001 = "3001";

	public static final String ADDITIONAL_PROPERTY_1000 = "1000";
	public static final String ADDITIONAL_PROPERTY_1002 = "1002";
	public static final String ADDITIONAL_PROPERTY_2000 = "2000";
	public static final String ADDITIONAL_PROPERTY_2001 = "2001";
	public static final String ADDITIONAL_PROPERTY_2002 = "2002";

	public static final String UBL_DIGESTVALUE_TAG = "ds:DigestValue";
	public static final String UBL_SIGNATUREVALUE_TAG = "ds:SignatureValue";
	public static final String UBL_SUNAT_TRANSACTION_TAG = "sac:SUNATTransaction";

	/**
	 * Valores para el RESUMEN DIARIO - StartDocumentNumberID -
	 * EndDocumentNumberID
	 */
	public static final String DOC_NUMBER_BB11_03_START = "BB11_03_START";
	public static final String DOC_NUMBER_BB11_03_END = "BB11_03_END";
	public static final String DOC_NUMBER_BB11_07_START = "BB11_07_START";
	public static final String DOC_NUMBER_BB11_07_END = "BB11_07_END";
	public static final String DOC_NUMBER_BB11_08_START = "BB11_08_START";
	public static final String DOC_NUMBER_BB11_08_END = "BB11_08_END";
	public static final String DOC_NUMBER_BB12_03_START = "BB12_03_START ";
	public static final String DOC_NUMBER_BB12_03_END = "BB12_03_END";
	public static final String DOC_NUMBER_BB12_07_START = "BB12_07_START";
	public static final String DOC_NUMBER_BB12_07_END = "BB12_07_END";

	/**
	 * Valores para la COMUNICACION DE BAJA
	 */
	public static final String DOC_NUMBER_FF11_01 = "FF11_1";
	public static final String DOC_NUMBER_FF11_02 = "FF11_2";
	public static final String DOC_NUMBER_FF11_03 = "FF11_3";
	public static final String DOC_NUMBER_FF11_04 = "FF11_4";
	public static final String DOC_NUMBER_FF11_05 = "FF11_5";

} // IUBLConfig
