package org.ventura.cpe.core.pdfcreator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.ReglasIdiomasDoc;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.domain.TransaccionContractdocref;
import org.ventura.cpe.core.domain.TransaccionTotales;
import org.ventura.cpe.core.entidades.ListaSociedades;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.PDFReportException;
import org.ventura.cpe.core.pdfcreator.handler.PDFGenerateHandler;
import org.ventura.cpe.core.repository.ReglasIdiomaRepository;
import org.ventura.cpe.core.wrapper.UBLDocumentWRP;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFCreator {

    private final PDFGenerateHandler pdfHandler;

    private final ReglasIdiomaRepository repository;

    private final AppProperties properties;

    private final SAPBOService sapboService;

    private String docUUID;

    public Optional<byte[]> createPDFDocument(Object ublDocument, String documentCode, UBLDocumentWRP wrp, Transaccion transaction) {
        byte[] pdfBytes = null;
        List<TransaccionTotales> transaccionTotales = new ArrayList<>(transaction.getTransaccionTotalesList());
        try {
            //UTILIZANDO HASH MAP DE ENTIDADES
            String idsociedad = transaction.getKeySociedad();
            ListaSociedades sociedades = sapboService.getListaSociedades().get(idsociedad);
            String logoSociedad = null;
            logoSociedad = sociedades.getLogoSociedad();
            //properties.getEmisorElectronico().getSenderLogo()
            if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_INVOICE_CODE);
                if (!rutaDOCSelected.isEmpty()) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getInvoiceReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }

                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la FACTURA.");
                }
                pdfHandler.setDocUUID(this.docUUID);
                pdfBytes = pdfHandler.generateInvoicePDF(wrp);
                log.info("[{}] Se genero el PDF de la FACTURA.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_BOLETA_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getBoletaReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la BOLETA.");
                }
                pdfBytes = pdfHandler.generateBoletaPDF(wrp);
                log.info("[{}] Se genero el PDF de la BOLETA.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_CREDIT_NOTE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getCreditNoteReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE CREDITO.");
                }
                pdfBytes = pdfHandler.generateCreditNotePDF(wrp, transaccionTotales);
                log.info("[{}] Se genero el PDF de la NOTA DE CREDITO.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_DEBIT_NOTE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getDebitNoteReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE DEBITO.");
                }
                pdfBytes = pdfHandler.generateDebitNotePDF(wrp, transaccionTotales);
                log.info("[" + this.docUUID + "] Se genero el PDF de la NOTA DE DEBITO.");
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_PERCEPTION_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getPerceptionReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                pdfBytes = pdfHandler.generatePerceptionPDF(wrp);
                log.info("[" + this.docUUID + "] Se genero el PDF del COMPROBANTE DE PERCEPCIón.");

            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_RETENTION_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getRetentionReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE DEBITO.");
                }
                pdfBytes = pdfHandler.generateRetentionPDF(wrp);
                log.info("[" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                String rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                } else {
                    pdfHandler.setConfiguration(properties.getPdf().getRemissionGuideReportPath(), properties.getPdf().getLegendSubReportPath(), logoSociedad, properties.getEmisorElectronico().getRs());
                }
                if (log.isInfoEnabled()) {
                    log.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la GUIA DE REMISION.");
                }
                pdfBytes = pdfHandler.generateDespatchAdvicePDF(wrp);
                log.info("[" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
            } else {
                log.error("createPDFDocument() [" + this.docUUID + "] " + IVenturaError.ERROR_460.getMessage());
                throw new ConfigurationException(IVenturaError.ERROR_460.getMessage());
            }
        } catch (PDFReportException e) {
            log.error("createPDFDocument() [" + this.docUUID + "] PDFReportException -->" + ExceptionUtils.getStackTrace(e));
        } catch (ConfigurationException e) {
            log.error("createPDFDocument() [" + this.docUUID + "] propertiesException -->" + ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error("createPDFDocument() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return Optional.ofNullable(pdfBytes);
    }


    private String cargarAnalizarReglasFormatoDOC(Transaccion transaction, String documentCode) {
        List<ReglasIdiomasDoc> listReglasIdiomasDoc = repository.findAll();
        for (ReglasIdiomasDoc reglaIdiomasDoc : listReglasIdiomasDoc) {
            String codeDOC = reglaIdiomasDoc.getFETipoDOC();
            if (codeDOC.equalsIgnoreCase(documentCode)) {
                Object campoFE = reglaIdiomasDoc.getFECampoDOC();
                Object operadorFE = reglaIdiomasDoc.getFEOperador();
                Object valorComparativo = reglaIdiomasDoc.getFEValorComparativo();
                Object documentSelect = reglaIdiomasDoc.getFEDOCFinal();
                boolean flagValidadorRegla = processObjectTransaction(transaction, campoFE, operadorFE, valorComparativo);
                if (flagValidadorRegla) {
                    return documentSelect.toString();
                }
            }
        }
        return "";
    }

    private boolean processObjectTransaction(Transaccion transaction, Object campoFE, Object operadorFE, Object valorComparativo) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            Method m;
            Object resultCampoFE = null;
            if (campoFE.toString().startsWith("U_")) {
                Set<TransaccionContractdocref> transaccionContractdocrefs = transaction.getTransaccionContractdocrefs();
                for (TransaccionContractdocref transaccionContract : transaccionContractdocrefs) {
                    if (campoFE.equals(transaccionContract.getUsuariocampos().getNombre())) {
                        resultCampoFE = transaccionContract.getValor();
                    }
                }
            } else {
                m = transaction.getClass().getMethod("get" + campoFE);
                resultCampoFE = m.invoke(transaction);
            }
            Object result = null;
            if (resultCampoFE != null) {
                if (resultCampoFE instanceof String) {
                    if (operadorFE.toString().equals("!=")) {
                        result = !resultCampoFE.equals(valorComparativo);
                    } else if (operadorFE.toString().equals("==")) {
                        result = resultCampoFE.equals(valorComparativo);
                    }
                } else if ((resultCampoFE instanceof Integer) || (resultCampoFE instanceof Double) || (resultCampoFE instanceof BigDecimal)) {
                    String expression = resultCampoFE.toString() + operadorFE + valorComparativo;
                    result = engine.eval(expression);
                } else if (resultCampoFE instanceof Date) {
                    Date valorComparativoFormat = new Date(valorComparativo.toString());
                    if (operadorFE.toString().equals("<=")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) <= 0;
                    } else if (operadorFE.toString().equals("<")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) < 0;
                    } else if (operadorFE.toString().equals("==")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) == 0;
                    } else if (operadorFE.toString().equals(">=")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) >= 0;
                    } else if (operadorFE.toString().equals(">")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) > 0;
                    } else if (operadorFE.toString().equals("!=")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        String resultCampoFEFormat = dateFormat.format((Date) resultCampoFE);
                        String expression = resultCampoFEFormat + operadorFE + valorComparativo;
                        result = engine.eval(expression);
                    }
                } else {
                    System.out.println("Tipos de datos comparados no son correctos");
                }
            }
            if (result != null && result.equals(true)) {
                return true;
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ScriptException ex) {
            System.out.println("Error en campo '" + campoFE + "' : " + ex.getMessage());
            return false;
        }
        return false;
    }

    public void setDocUUID(String docUUID) {
        this.pdfHandler.setDocUUID(docUUID);
        this.docUUID = docUUID;
    }
}
