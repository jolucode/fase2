/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.soluciones.pdfcreator.handler.creator;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.soluciones.commons.exception.PDFReportException;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.soluciones.pdfcreator.config.IPDFCreatorConfig;
import org.ventura.soluciones.pdfcreator.object.DespatchAdviceObject;
import org.ventura.utilidades.entidades.VariablesGlobales;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VS-LT-06
 */
public class PDFDespatchAdviceCreator extends DocumentCreator {

    private final Logger logger = Logger.getLogger(PDFDespatchAdviceCreator.class);

    private static PDFDespatchAdviceCreator instance = null;

    private JasperDesign iJasperDesign;

    private JasperReport iJasperReport;

    private Map<String, Object> parameterMap;

    private String legendSubReportPath;

    private PDFDespatchAdviceCreator(String despatchAdviceReportPath, String legendSubReportPath) throws PDFReportException {
        if (logger.isDebugEnabled()) {
            logger.debug("+PDFDespatchAdviceCreator() constructor");
        }
        try {
            File despatchAdviceTemplate = new File(despatchAdviceReportPath);
            if (!despatchAdviceTemplate.isFile()) {
                throw new FileNotFoundException(IVenturaError.ERROR_401.getMessage());
            }

            InputStream inputStream = new BufferedInputStream(new FileInputStream(despatchAdviceTemplate));

            /* Carga el template .jrxml */
            iJasperDesign = JRXmlLoader.load(inputStream);

            /* Compila el reporte */
            iJasperReport = JasperCompileManager.compileReport(iJasperDesign);

            /*
             * Guardando en la instancia la ruta del subreporte de
             * leyendas
             */
            this.legendSubReportPath = legendSubReportPath;
        } catch (FileNotFoundException e) {
            logger.error("PDFDespatchAdviceCreator() FileNotFoundException - ERROR: " + e.getMessage());
            logger.error("PDFDespatchAdviceCreator() FileNotFoundException -->" + ExceptionUtils.getStackTrace(e));
            throw new PDFReportException(e.getMessage());
        } catch (Exception e) {
            logger.error("PDFDespatchAdviceCreator() Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("PDFDespatchAdviceCreator() Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new PDFReportException(IVenturaError.ERROR_405);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-PDFDespatchAdviceCreator() constructor");
        }
    } //PDFDespatchAdviceCreator

    /**
     * Este metodo obtiene la instancia actual del objeto PDFInvoiceCreator.
     *
     * @param legendSubReportPath Ruta del subreporte de legendas.
     * @return Retorna la instancia de la clase PDFDespatchAdviceCreator.
     * @throws PDFReportException
     */
    public static PDFDespatchAdviceCreator getInstance(String despatchAdviceReportPath, String legendSubReportPath) throws PDFReportException {
        /*if (null == instance) {
         instance = new PDFInvoiceCreator(invoiceReportPath, legendSubReportPath);
         }*/
        instance = new PDFDespatchAdviceCreator(despatchAdviceReportPath, legendSubReportPath);
        return instance;
    } //getInstance

    /**
     * Este metodo crea un PDF que es la representacion impresa de la guia de
     * remisión electronica.
     *
     * @param despatchAdviceObject Objeto que contiene informacion de la
     *                             factura.
     * @param docUUID              Identificador unica de la factura.
     * @return Retorna un PDF en bytes.
     * @throws PDFReportException
     */
    public byte[] createDespatchAdvicePDF(DespatchAdviceObject despatchAdviceObject, String docUUID) throws PDFReportException {
        if (logger.isDebugEnabled()) {
            logger.debug("+createDespatchAdvicePDF() [" + docUUID + "]");
        }
        byte[] pdfDocument = null;

        if (null == despatchAdviceObject) {
            throw new PDFReportException(IVenturaError.ERROR_406);
        } else {
            try {
                /* Crea instancia del MAP */
                parameterMap = new HashMap<String, Object>();

                //================================================================================================
                //================================= AGREGANDO INFORMACION AL MAP =================================
                //================================================================================================
                parameterMap.put(IPDFCreatorConfig.CODIGO_EMBARQUE, despatchAdviceObject.getCodigoEmbarque());
                parameterMap.put(IPDFCreatorConfig.CODIGO_MOTIVO, despatchAdviceObject.getCodigoMotivoTraslado());
                parameterMap.put(IPDFCreatorConfig.DESCRIPCION_MOTIVO, despatchAdviceObject.getDescripcionMotivoTraslado());
                parameterMap.put(IPDFCreatorConfig.DIRECCION_DESTINO, despatchAdviceObject.getDireccionDestino());
                parameterMap.put(IPDFCreatorConfig.DIRECCION_PARTIDA, despatchAdviceObject.getDireccionPartida());
                parameterMap.put(IPDFCreatorConfig.DOCUMENTO_CONDUCTOR, despatchAdviceObject.getDocumentoConductor());
                parameterMap.put(IPDFCreatorConfig.FECHA_EMISION, despatchAdviceObject.getFechaEmision());
                parameterMap.put(IPDFCreatorConfig.FECHA_TRASLADO, despatchAdviceObject.getFechaTraslado());
                parameterMap.put(IPDFCreatorConfig.MODALIDAD_TRASLADO, despatchAdviceObject.getModalidadTraslado());
                parameterMap.put(IPDFCreatorConfig.NOMBRE_CONSUMIDOR, despatchAdviceObject.getNombreConsumidor());
                parameterMap.put(IPDFCreatorConfig.NOMBRE_EMISOR, despatchAdviceObject.getNombreEmisor());
                parameterMap.put(IPDFCreatorConfig.NOMBRE_TRANSPORTISTA, despatchAdviceObject.getNombreTransportista());
                parameterMap.put(IPDFCreatorConfig.NUMERO_BULTOS, despatchAdviceObject.getNumeroBultos());
                parameterMap.put(IPDFCreatorConfig.PLACA_VEHICULO, despatchAdviceObject.getPlacaVehiculo());
                parameterMap.put(IPDFCreatorConfig.RUC_TRANSPORTISTA, despatchAdviceObject.getRUCTransportista());
                parameterMap.put(IPDFCreatorConfig.TIPO_DOCUMENTO_CONDUCTOR, despatchAdviceObject.getTipoDocumentoConductor());
                parameterMap.put(IPDFCreatorConfig.TIPO_DOCUMENTO_TRANSPORTISTA, despatchAdviceObject.getTipoDocumentoTransportista());
                parameterMap.put(IPDFCreatorConfig.UNIDAD_MEDIDA_PESONETO, despatchAdviceObject.getUMPesoBruto());
                /** 04-03-2024  Harol Peso Guia Transportista*/
                parameterMap.put(IPDFCreatorConfig.PESONETO, despatchAdviceObject.getPesoBruto());
                /** */
                parameterMap.put(IPDFCreatorConfig.VALIDEZPDF, despatchAdviceObject.getValidezPDF());
                parameterMap.put(IPDFCreatorConfig.DOCUMENT_IDENTIFIER, despatchAdviceObject.getNumeroGuia());
                parameterMap.put(IPDFCreatorConfig.SENDER_RUC, despatchAdviceObject.getNumeroDocEmisor());
                parameterMap.put(IPDFCreatorConfig.SENDER_MAIL, despatchAdviceObject.getEmail());
                parameterMap.put(IPDFCreatorConfig.SENDER_TEL, despatchAdviceObject.getTelefono());
                parameterMap.put(IPDFCreatorConfig.SENDER_TEL_1, despatchAdviceObject.getTelefono1());
                parameterMap.put(IPDFCreatorConfig.SENDER_WEB, despatchAdviceObject.getPaginaWeb());
                parameterMap.put(IPDFCreatorConfig.COMMENTS, despatchAdviceObject.getObervaciones());
                parameterMap.put(IPDFCreatorConfig.RUC_CONSUMIDOR, despatchAdviceObject.getNumeroDocConsumidor());
                parameterMap.put(IPDFCreatorConfig.RUC_EMISOR, despatchAdviceObject.getNumeroDocEmisor());
                parameterMap.put(IPDFCreatorConfig.SENDER_LOGO_PATH, despatchAdviceObject.getSenderLogo());
                parameterMap.put(IPDFCreatorConfig.LICENCIA_CONDUCIR, despatchAdviceObject.getLicenciaConducir());
                parameterMap.put(IPDFCreatorConfig.CAMPOS_USUARIO_CAB, despatchAdviceObject.getDespatchAdvicePersonalizacion());

                /** 29-02-2024 Harol Guia Transportista*/
                parameterMap.put(IPDFCreatorConfig.GRT_DOCUMENTO_REMITENTE, despatchAdviceObject.getGrtDocumentoRemitente());
                parameterMap.put(IPDFCreatorConfig.GRT_NOMBRE_RAZON_REMITENTE, despatchAdviceObject.getGrtNombreRazonRemitente());
                parameterMap.put(IPDFCreatorConfig.GRT_DOCUMENTO_DESTINATARIO, despatchAdviceObject.getGrtDocumentoDestinatario());
                parameterMap.put(IPDFCreatorConfig.GRT_NOMBRE_RAZON_DESTINATARIO, despatchAdviceObject.getGrtNombreRazonDestinatario());
                parameterMap.put(IPDFCreatorConfig.NRO_REGISTRO_MTC, despatchAdviceObject.getNroRegistroMtc());
                parameterMap.put(IPDFCreatorConfig.SN_DOC_IDENTIDAD_NRO, despatchAdviceObject.getSnDocIdentidadNro());
                parameterMap.put(IPDFCreatorConfig.SN_RAZON_SOCIAL, despatchAdviceObject.getSnRazonSocial());
                parameterMap.put(IPDFCreatorConfig.NOMBRE_CONDUCTOR, despatchAdviceObject.getNombreConductor());

                parameterMap.put(IPDFCreatorConfig.INDICADOR_TRANSBORDO_PROGRAMADO, despatchAdviceObject.getIndicadorTransbordoProgramado());
                parameterMap.put(IPDFCreatorConfig.INDICADOR_RETORNO_VEHICULO_VACIO, despatchAdviceObject.getIndicadorRetornoVehiculoVacio());
                parameterMap.put(IPDFCreatorConfig.INDICADOR_TRANSPORTE_SUBCONTRATADO, despatchAdviceObject.getIndicadorTransporteSubcontratado());
                parameterMap.put(IPDFCreatorConfig.INDICADOR_RETORNO_VEHICULO_ENVASES_VACIO, despatchAdviceObject.getIndicadorRetornoVehiculoEnvasesVacio());
                parameterMap.put(IPDFCreatorConfig.GRT_INDICADOR_PAGADOR_FLETE, despatchAdviceObject.getGrtIndicadorPagadorFlete());

                parameterMap.put(IPDFCreatorConfig.GRT_NUMERO_TUCE_PRINCIPAL, despatchAdviceObject.getGrtNumeroTUCEPrincipal());
                parameterMap.put(IPDFCreatorConfig.GRT_ENTIDAD_EMISORA_PRINCIPAL, despatchAdviceObject.getGrtEntidadEmisoraPrincipal());
                parameterMap.put(IPDFCreatorConfig.GRT_PLACA_VEHICULO_SECUNDARIO, despatchAdviceObject.getGrtPlacaVehiculoSecundario());
                parameterMap.put(IPDFCreatorConfig.GRT_NUMERO_TUCE_SECUNDARIO, despatchAdviceObject.getGrtNumeroTUCESecuendario());
                parameterMap.put(IPDFCreatorConfig.GRT_ENTIDAD_EMISORA_SECUNDARIO, despatchAdviceObject.getGrtEntidadEmisoraSecundario());

                parameterMap.put(IPDFCreatorConfig.DOCUMENTO_RELACIONADO,despatchAdviceObject.getDocumentosRelacionados());
                /** */

                if (VariablesGlobales.Impresion.equalsIgnoreCase("Codigo QR")) {
                    parameterMap.put(IPDFCreatorConfig.CODEQR, despatchAdviceObject.getCodeQR());
                } else {
                    parameterMap.put(IPDFCreatorConfig.CODEQR, null);
                }
                /*
                 * Generar el reporte con la informacion de la factura
                 * electronica
                 */
                JasperPrint iJasperPrint = JasperFillManager.fillReport(iJasperReport, parameterMap,
                        new JRBeanCollectionDataSource(despatchAdviceObject.getItemListDynamic()));

                /*
                 * Exportar el reporte PDF en una ruta en DISCO
                 */
                String outputPath = USER_TEMPORARY_PATH + File.separator + docUUID + IPDFCreatorConfig.EE_PDF;
                JasperExportManager.exportReportToPdfFile(iJasperPrint, outputPath);
                if (logger.isInfoEnabled()) {
                    logger.info("createDespatchAdvicePDF() [" + docUUID + "] Se guardo el PDF en una ruta temportal: " + outputPath);
                }

                /*
                 * Convirtiendo el documento PDF generado en bytes.
                 */
                pdfDocument = convertFileInBytes(outputPath);
                if (logger.isInfoEnabled()) {
                    logger.info("createDespatchAdvicePDF() [" + docUUID + "] Se convirtio el PDF en bytes: " + pdfDocument);
                }
            } catch (Exception e) {
                logger.error("createDespatchAdvicePDF() [" + docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
                logger.error("createDespatchAdvicePDF() [" + docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
                throw new PDFReportException(IVenturaError.ERROR_441);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-createDespatchAdvicePDF() [" + docUUID + "]");
        }
        return pdfDocument;
    } //createInvoicePDF

}
