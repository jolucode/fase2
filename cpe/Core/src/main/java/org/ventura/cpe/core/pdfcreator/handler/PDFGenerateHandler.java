package org.ventura.cpe.core.pdfcreator.handler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.PDF417;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxSubtotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.LineExtensionAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.TaxableAmountType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.*;
import org.ventura.cpe.core.entidades.TipoVersionUBL;
import org.ventura.cpe.core.exception.ErrorObj;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.PDFReportException;
import org.ventura.cpe.core.exception.UBLDocumentException;
import org.ventura.cpe.core.pdfcreator.handler.creator.*;
import org.ventura.cpe.core.pdfcreator.object.*;
import org.ventura.cpe.core.pdfcreator.object.legend.LegendObject;
import org.ventura.cpe.core.wrapper.UBLDocumentWRP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDFGenerateHandler extends PDFBasicGenerateHandler {

    private final AppProperties properties;

    /*
     * Ruta de los templates de reportes
     */
    private String documentReportPath;

    private String legendSubReportPath;

    /* Logo del emisor electronico */
    private String senderLogo;

    /*
     * Codigo de resolucion del emisor electronico
     */
    private String resolutionCode;

    public static InputStream generateQRCode(String qrCodeData, String filePath) {
        try {
            String charset = "utf-8"; // or "ISO-8859-1"
            Map hintMap = new HashMap();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
            createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);

            FileInputStream fis = new FileInputStream(filePath);
            InputStream is = fis;
            return is;

        } catch (WriterException | IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    public static InputStream generatePDF417Code(String qrCodeData, String filePath, int width, int height, int margin) {

        try {
            BitMatrix bitMatrixFromEncoder = null;
            PDF417 objPdf417 = new PDF417();
            objPdf417.generateBarcodeLogic(qrCodeData, 5);
            objPdf417.setEncoding(StandardCharsets.UTF_8);
            int aspectRatio = 4;
            byte[][] originalScale = objPdf417.getBarcodeMatrix().getScaledMatrix(1, aspectRatio);

            boolean rotated = false;
            if ((height > width) != (originalScale[0].length < originalScale.length)) {
                originalScale = rotateArray(originalScale);
                rotated = true;
            }

            int scaleX = width / originalScale[0].length;
            int scaleY = height / originalScale.length;

            int scale;
            if (scaleX < scaleY) {
                scale = scaleX;
            } else {
                scale = scaleY;
            }

            if (scale > 1) {
                byte[][] scaledMatrix = objPdf417.getBarcodeMatrix().getScaledMatrix(scale, scale * aspectRatio);
                if (rotated) {
                    scaledMatrix = rotateArray(scaledMatrix);
                }
                bitMatrixFromEncoder = bitMatrixFromBitArray(scaledMatrix, margin);
            }
            bitMatrixFromEncoder = bitMatrixFromBitArray(originalScale, margin);

            MatrixToImageWriter.writeToFile(bitMatrixFromEncoder, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));

            FileInputStream fis = new FileInputStream(filePath);
            InputStream is = fis;
            return is;

        } catch (WriterException | IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;

    }

    private static BitMatrix bitMatrixFromBitArray(byte[][] input, int margin) {
        // Creates the bit matrix with extra space for whitespace
        BitMatrix output = new BitMatrix(input[0].length + 2 * margin, input.length + 2 * margin);
        output.clear();
        for (int y = 0, yOutput = output.getHeight() - margin - 1; y < input.length; y++, yOutput--) {
            byte[] inputY = input[y];
            for (int x = 0; x < input[0].length; x++) {
                // Zero is white in the byte matrix
                if (inputY[x] == 1) {
                    output.set(x + margin, yOutput);
                }
            }
        }
        return output;
    }

    private static byte[][] rotateArray(byte[][] bitarray) {
        byte[][] temp = new byte[bitarray[0].length][bitarray.length];
        for (int ii = 0; ii < bitarray.length; ii++) {
            // This makes the direction consistent on screen when rotating the
            // screen;
            int inverseii = bitarray.length - ii - 1;
            for (int jj = 0; jj < bitarray[0].length; jj++) {
                temp[jj][inverseii] = bitarray[ii][jj];
            }
        }
        return temp;
    }

    public static void createQRCode(String qrCodeData, String filePath, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
    }

    /**
     * Este metodo guarda las ruta de los templates de reportes.
     *
     * @param documentReportPath  Ruta del template del documento, del cual se
     *                            generara el PDF.
     * @param legendSubReportPath Ruta del template del subreporte de legendas.
     * @param senderLogo          El logo del emisor electronico
     * @param resolutionCode      Codigo de resolucion del emisor electronico
     */
    public void setConfiguration(String documentReportPath, String legendSubReportPath, String senderLogo, String resolutionCode) {
        this.documentReportPath = documentReportPath;
        this.legendSubReportPath = legendSubReportPath;
        this.senderLogo = senderLogo;
        this.resolutionCode = resolutionCode;
    } // setConfiguration

    public byte[] generateRetentionPDF(UBLDocumentWRP retentionType) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateRetentionPDF() [" + this.docUUID + "]");
        }
        byte[] perceptionBytes = null;

        try {
            RetentionObject retentionObject = new RetentionObject();
            if (log.isDebugEnabled()) {
                log.debug("generateRetentionPDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            retentionObject.setDocumentIdentifier(retentionType.getRetentionType().getId().getValue());
            retentionObject.setIssueDate(formatIssueDate(retentionType.getRetentionType().getIssueDate().getValue()));

            /* Informacion de SUNATTransaction */
            if (log.isDebugEnabled()) {
                log.debug("generateRetentionPDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }

            retentionObject.setSenderSocialReason(retentionType.getRetentionType().getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue().toUpperCase());
            retentionObject.setSenderRuc(retentionType.getRetentionType().getAgentParty().getPartyIdentification().get(0).getID().getValue());
            retentionObject.setSenderFiscalAddress(retentionType.getRetentionType().getAgentParty().getPostalAddress().getStreetName().getValue());
            retentionObject.setSenderDepProvDist(formatDepProvDist(retentionType.getRetentionType().getAgentParty().getPostalAddress()));
            retentionObject.setSenderLogo(this.senderLogo);

            retentionObject.setComentarios(retentionType.getTransaccion().getFEComentario());
            retentionObject.setTel(retentionType.getTransaccion().getTelefono());
            retentionObject.setTel1(retentionType.getTransaccion().getTelefono1());
            retentionObject.setSenderMail(retentionType.getTransaccion().getEMail());
            retentionObject.setWeb(retentionType.getTransaccion().getWeb());
            retentionObject.setRegimenRET(retentionType.getTransaccion().getRETTasa());
            if (log.isDebugEnabled()) {
                log.debug("generateRetentionPDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            retentionObject.setReceiverSocialReason(retentionType.getRetentionType().getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue().toUpperCase());
            retentionObject.setReceiverRuc(retentionType.getRetentionType().getReceiverParty().getPartyIdentification().get(0).getID().getValue());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            retentionObject.setRetentionItems(getRetentionItems(retentionType.getRetentionType().getSunatRetentionDocumentReference(), retentionType.getRetentionType().getSunatRetentionPercent().getValue()));

            retentionObject.setTotalAmountValue(retentionType.getRetentionType().getTotalInvoiceAmount().getValue().toString());
            BigDecimal montoSoles = new BigDecimal("0.00");
            BigDecimal importeTotal = null;
            BigDecimal importeTotalDOC = new BigDecimal("0.00");
            BigDecimal importeDocumento = new BigDecimal("0.00");
            Set<TransaccionComprobantePago> transaccionComprobantePagos = retentionType.getTransaccion().getTransaccionComprobantePagos();
            int k = 0;
            for (TransaccionComprobantePago transaccionComprobantePago : transaccionComprobantePagos) {
                montoSoles = montoSoles.add(transaccionComprobantePago.getCPImporte().add(transaccionComprobantePago.getCPImporteTotal()));
                importeTotalDOC = importeTotalDOC.add(retentionType.getRetentionType().getSunatRetentionDocumentReference().get(k).getPayment().getPaidAmount().getValue());
                importeDocumento = importeDocumento.add(transaccionComprobantePago.getCPImporteTotal());
                k++;
            }
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                retentionObject.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                retentionObject.setValidezPDF("");
            }
            retentionObject.setMontoenSoles(montoSoles.toString());
            retentionObject.setMontoTotalDoc(importeTotalDOC.toString());
            retentionObject.setTotal_doc_value(importeDocumento.toString());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            Set<TransaccionPropiedades> transaccionPropiedades = retentionType.getTransaccion().getTransaccionPropiedadesList();
            for (TransaccionPropiedades transaccionPropiedad : transaccionPropiedades) {
                if (transaccionPropiedad.getTransaccionPropiedadesPK().getId().equalsIgnoreCase("1000")) {
                    retentionObject.setLetterAmountValue(transaccionPropiedad.getValor());
                }
            }
            List<WrapperItemObject> listaItem = new ArrayList<>();
            for (TransaccionComprobantePago transaccionComprobantePago : transaccionComprobantePagos) {
                List<TransaccionComprobantepagoUsuario> transaccionComprobantepagoUsuarios = transaccionComprobantePago.getTransaccionComprobantepagoUsuarioList();
                if (log.isDebugEnabled()) {
                    log.debug("generateRetentionPDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionComprobantePago.getTransaccionComprobantepagoUsuarioList().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                for (TransaccionComprobantepagoUsuario transaccionComprobantepagoUsuario : transaccionComprobantepagoUsuarios) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateRetentionPDF() [" + this.docUUID + "] Extrayendo Campos " + transaccionComprobantepagoUsuario.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(transaccionComprobantepagoUsuario.getUsuariocampos().getNombre(), transaccionComprobantepagoUsuario.getValor());
                    newlist.add(transaccionComprobantepagoUsuario.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateRetentionPDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);
            }
            retentionObject.setItemListDynamic(listaItem);

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            // retentionObject.setLegends(getLegendList(legendsMap));

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }
            String barcodeValue = generateBarcodeInfoV2(retentionType.getRetentionType().getId().getValue(), IUBLConfig.DOC_RETENTION_CODE, retentionObject.getIssueDate(), retentionType.getRetentionType().getTotalInvoiceAmount().getValue(), BigDecimal.ZERO, retentionType.getRetentionType().getAgentParty(), retentionType.getRetentionType().getReceiverParty(), retentionType.getRetentionType().getUblExtensions());
            if (log.isInfoEnabled()) {
                log.info("generateInvoicePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + retentionType.getRetentionType().getId().getValue() + ".png";
            File f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR");
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStream = generateQRCode(barcodeValue, rutaPath);
            retentionObject.setCodeQR(inputStream);
            f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417");
            rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + retentionType.getRetentionType().getId().getValue() + ".png";
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStreamPDF = generatePDF417Code(barcodeValue, rutaPath, 200, 200, 1);
            retentionObject.setBarcodeValue(inputStreamPDF);
            String digestValue = generateDigestValue(retentionType.getRetentionType().getUblExtensions());
            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] VALOR RESUMEN: \n" + digestValue);
            }
            retentionObject.setDigestValue(digestValue);
            retentionObject.setResolutionCodeValue(this.resolutionCode);

            /*
             * Generando el PDF de la FACTURA con la informacion recopilada.
             */
            perceptionBytes = PDFRetentionCreator.getInstance(this.documentReportPath, this.legendSubReportPath, properties).createRetentionPDF(retentionObject, docUUID);
        } catch (PDFReportException e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateInvoicePDF() [" + this.docUUID + "]");
        }
        return perceptionBytes;
    }

    public byte[] generatePerceptionPDF(UBLDocumentWRP perceptionType) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateInvoicePDF() [" + this.docUUID + "]");
        }
        byte[] perceptionBytes = null;

        try {
            PerceptionObject perceptionObj = new PerceptionObject();

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            perceptionObj.setDocumentIdentifier(perceptionType.getPerceptionType().getId().getValue());
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion de la fecha." + perceptionType.getPerceptionType().getIssueDate().getValue());
            }
            perceptionObj.setIssueDate(formatIssueDate(perceptionType.getPerceptionType().getIssueDate().getValue()));
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }
            perceptionObj.setSenderSocialReason(perceptionType.getPerceptionType().getAgentParty().getPartyLegalEntity().get(0).getRegistrationName().getValue().toUpperCase());
            perceptionObj.setSenderRuc(perceptionType.getPerceptionType().getAgentParty().getPartyIdentification().get(0).getID().getValue());
            perceptionObj.setSenderFiscalAddress(perceptionType.getPerceptionType().getAgentParty().getPostalAddress().getStreetName().getValue());
            perceptionObj.setSenderDepProvDist(formatDepProvDist(perceptionType.getPerceptionType().getAgentParty().getPostalAddress()));
            perceptionObj.setSenderLogo(this.senderLogo);
            perceptionObj.setTelValue(perceptionType.getTransaccion().getTelefono());
            perceptionObj.setTel2Value(perceptionType.getTransaccion().getTelefono1());
            perceptionObj.setWebValue(perceptionType.getTransaccion().getWeb());
            perceptionObj.setSenderMail(perceptionType.getTransaccion().getEMail());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            perceptionObj.setReceiverSocialReason(perceptionType.getPerceptionType().getReceiverParty().getPartyLegalEntity().get(0).getRegistrationName().getValue().toUpperCase());
            perceptionObj.setReceiverRuc(perceptionType.getPerceptionType().getReceiverParty().getPartyIdentification().get(0).getID().getValue());
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            perceptionObj.setPerceptionItems(getPerceptionItems(perceptionType.getPerceptionType().getSunatPerceptionDocumentReference(), new BigDecimal(perceptionType.getPerceptionType().getSunatPerceptionPercent().getValue())));
            List<WrapperItemObject> listaItem = new ArrayList<WrapperItemObject>();
            Set<TransaccionComprobantePago> transaccionComprobantePagos = perceptionType.getTransaccion().getTransaccionComprobantePagos();
            for (TransaccionComprobantePago transaccionComprobantePago : transaccionComprobantePagos) {
                if (log.isDebugEnabled()) {
                    log.debug("generatePerceptionPDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionComprobantePago.getTransaccionComprobantepagoUsuarioList().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                List<TransaccionComprobantepagoUsuario> comprobantepagoUsuarios = transaccionComprobantePago.getTransaccionComprobantepagoUsuarioList();
                for (TransaccionComprobantepagoUsuario comprobantepagoUsuario : comprobantepagoUsuarios) {
                    if (log.isDebugEnabled()) {
                        log.debug("generatePerceptionPDF() [" + this.docUUID + "] Extrayendo Campos " + comprobantepagoUsuario.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(comprobantepagoUsuario.getUsuariocampos().getNombre(), comprobantepagoUsuario.getValor());
                    newlist.add(comprobantepagoUsuario.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);
            }

            perceptionObj.setItemListDynamic(listaItem);

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo la informacion de PROPIEDADES (AdditionalProperty).");
            }
            // Map<String, LegendObject> legendsMap = new HashMap<String,
            // LegendObject>();

            perceptionObj.setTotalAmountValue(perceptionType.getPerceptionType().getTotalInvoiceAmount().getValue().toString());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            Transaccion transaccion = perceptionType.getTransaccion();
            Set<TransaccionPropiedades> transaccionPropiedades = transaccion.getTransaccionPropiedadesList();
            for (TransaccionPropiedades transaccionPropiedad : transaccionPropiedades) {
                if (transaccionPropiedad.getTransaccionPropiedadesPK().getId().equalsIgnoreCase("1000")) {
                    perceptionObj.setLetterAmountValue(transaccionPropiedad.getValor());
                }
            }
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                perceptionObj.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                perceptionObj.setValidezPDF("");
            }

            // LegendObject legendLetters =
            // legendsMap.get(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            // perceptionObj.setLetterAmountValue(legendLetters.getLegendValue());
            // legendsMap.remove(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }
            // String barcodeValue =
            // generateBarcodeInfoV2(perceptionType.getPerceptionType().getId().getValue(),
            // "40",
            // perceptionType.getPerceptionType().getIssueDate().toString(),
            // perceptionType.getPerceptionType().getTotalInvoiceAmount().getValue(),
            // perceptionType.getPerceptionType().getAgentParty(),
            // perceptionType.getPerceptionType().getReceiverParty(),
            // perceptionType.getPerceptionType().getUblExtensions());
            // if (log.isInfoEnabled()) {log.info("generateInvoicePDF() ["
            // + this.docUUID + "] BARCODE: \n" + barcodeValue);}
            // perceptionObj.setBarcodeValue(barcodeValue);

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            // perceptionObj.setLegends(getLegendList(legendsMap));

            Set<TransaccionPropiedades> transaccionPropiedadesList = perceptionType.getTransaccion().getTransaccionPropiedadesList();
            for (TransaccionPropiedades propiedades : transaccionPropiedadesList) {
                perceptionObj.setResolutionCodeValue(this.resolutionCode);
                perceptionObj.setImporteTexto(propiedades.getValor());
                break;
            }

            /*
             * Generando el PDF de la FACTURA con la informacion recopilada.
             */
            perceptionBytes = PDFPerceptionCreator.getInstance(this.documentReportPath, this.legendSubReportPath).createPerceptionPDF(perceptionObj, docUUID);
        } catch (PDFReportException e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateInvoicePDF() [" + this.docUUID + "]");
        }
        return perceptionBytes;
    } // generateInvoicePDF

    /**
     * Este metodo genera una representacion impresa de una FACTURA en formato
     * PDF, retornando un arreglo de bytes que contienen el PDF.
     *
     * @param invoiceType Objeto UBL que representa una FACTURA.
     * @return Retorna un arreglo de bytes que contiene el PDF.
     * @throws PDFReportException
     */
    public byte[] generateInvoicePDF(UBLDocumentWRP invoiceType) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateInvoicePDF() [" + this.docUUID + "]");
        }
        byte[] invoiceInBytes = null;
        try {
            InvoiceObject invoiceObj = new InvoiceObject();
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            invoiceObj.setDocumentIdentifier(invoiceType.getInvoiceType().getID().getValue());
            invoiceObj.setIssueDate(formatIssueDate(invoiceType.getInvoiceType().getIssueDate().getValue()));
            List<TaxTotalType> taxTotal = invoiceType.getInvoiceType().getTaxTotal();
            for (TaxTotalType taxTotalType : taxTotal) {
                List<TaxSubtotalType> taxSubtotal = taxTotalType.getTaxSubtotal();
                for (TaxSubtotalType taxSubtotalType : taxSubtotal) {
                    TaxCategoryType taxCategory = taxSubtotalType.getTaxCategory();
                    TaxableAmountType taxableAmount = taxSubtotalType.getTaxableAmount();
                    TaxSchemeType taxScheme = taxCategory.getTaxScheme();
                    NameType name = taxScheme.getName();
                    String nombreImpuesto = name.getValue();
                    if ("ICBPER".equalsIgnoreCase(nombreImpuesto)) {
                        invoiceObj.setImpuestoBolsa(taxableAmount.getValue().toString());
                        invoiceObj.setImpuestoBolsaMoneda(taxableAmount.getCurrencyID());
                    }
                }
            }
            invoiceObj.setFormSap(invoiceType.getTransaccion().getFEFormSAP());
            if (StringUtils.isNotBlank(invoiceType.getInvoiceType().getDocumentCurrencyCode().getName())) {
                invoiceObj.setCurrencyValue(invoiceType.getInvoiceType().getDocumentCurrencyCode().getName().toUpperCase());
            } else {
                invoiceObj.setCurrencyValue(invoiceType.getTransaccion().getDOCMONNombre().toUpperCase());
            }
            if (null != invoiceType.getInvoiceType().getNote() && 0 < invoiceType.getInvoiceType().getNote().size()) {
                invoiceObj.setDueDate(formatDueDate(invoiceType.getTransaccion().getDOCFechaVencimiento()));
                //invoiceObj.setDueDate(formatDueDate(invoiceType.getInvoiceType().getNote().get(0).getValue()));
            } else {
                invoiceObj.setDueDate(formatDueDate(invoiceType.getTransaccion().getDOCFechaVencimiento()));
            }
            /* Informacion de SUNATTransaction */
//            String sunatTransInfo = getSunatTransactionInfo(invoiceType.getInvoiceType().getUBLExtensions().getUBLExtension());
//            if (StringUtils.isNotBlank(sunatTransInfo)) {
//                invoiceObj.setSunatTransaction(sunatTransInfo);
//            }
            Set<TransaccionAnticipo> transaccionAnticipoList = invoiceType.getTransaccion().getTransaccionAnticipoList();
            String anticipos = transaccionAnticipoList.parallelStream().map(TransaccionAnticipo::getAntiDOCSerieCorrelativo).collect(Collectors.joining(" "));
            invoiceObj.setAnticipos(anticipos);
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo guias de remision.");
            }
            invoiceObj.setRemissionGuides(getRemissionGuides(invoiceType.getInvoiceType().getDespatchDocumentReference()));
            if (log.isInfoEnabled()) {
                log.info("generateInvoicePDF() [" + this.docUUID + "]============= condicion pago------");
            }
            invoiceObj.setPaymentCondition(invoiceType.getTransaccion().getDOCCondPago());
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }

            invoiceObj.setSenderSocialReason(invoiceType.getTransaccion().getRazonSocial());
            invoiceObj.setSenderRuc(invoiceType.getTransaccion().getDocIdentidadNro());
            invoiceObj.setSenderFiscalAddress(invoiceType.getTransaccion().getDIRNomCalle());
            invoiceObj.setSenderDepProvDist(invoiceType.getTransaccion().getDIRDistrito() + " " + invoiceType.getTransaccion().getDIRProvincia() + " " + invoiceType.getTransaccion().getDIRDepartamento());
            invoiceObj.setSenderContact(invoiceType.getTransaccion().getPersonContacto());
            invoiceObj.setSenderMail(invoiceType.getTransaccion().getEMail());
            invoiceObj.setSenderLogo(this.senderLogo);
            invoiceObj.setTelefono(invoiceType.getTransaccion().getTelefono());
            invoiceObj.setTelefono_1(invoiceType.getTransaccion().getTelefono1());
            invoiceObj.setWeb(invoiceType.getTransaccion().getWeb());
            invoiceObj.setPorcentajeIGV(invoiceType.getTransaccion().getDOCPorcImpuesto());
            invoiceObj.setComentarios(invoiceType.getTransaccion().getFEComentario());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            invoiceObj.setReceiverSocialReason(invoiceType.getTransaccion().getSNRazonSocial());
            invoiceObj.setReceiverRuc(invoiceType.getTransaccion().getSNDocIdentidadNro());
            invoiceObj.setReceiverFiscalAddress(invoiceType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + invoiceType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + invoiceType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + invoiceType.getTransaccion().getSNDIRDepartamento().toUpperCase());

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            // invoiceObj.setInvoiceItems(getInvoiceItems(invoiceType.getInvoiceType().getInvoiceLine()));

            String currencyCode = invoiceType.getInvoiceType().getDocumentCurrencyCode().getValue();

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de Percepcion.");
            }
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                invoiceObj.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                invoiceObj.setValidezPDF("");
            }

            BigDecimal percepctionAmount = null;
            BigDecimal perceptionPercentage = null;
            Set<TransaccionTotales> transaccionTotalesList = invoiceType.getTransaccion().getTransaccionTotalesList();
            for (TransaccionTotales transaccionTotales : transaccionTotalesList) {
                if (transaccionTotales.getTransaccionTotalesPK().getId().equalsIgnoreCase("2001")) {
                    percepctionAmount = transaccionTotales.getMonto();
                    perceptionPercentage = transaccionTotales.getPrcnt();
                    invoiceObj.setPerceptionAmount(currencyCode + " " + transaccionTotales.getMonto().toString());
                    invoiceObj.setPerceptionPercentage(transaccionTotales.getPrcnt().toString() + "%");
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de ISC.");
            }
            BigDecimal retentionpercentage = null;
            Set<TransaccionLineas> transaccionLineasList = invoiceType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLineas : transaccionLineasList) {
                Set<TransaccionLineaImpuestos> transaccionLineaImpuestosList = transaccionLineas.getTransaccionLineaImpuestosList();
                for (TransaccionLineaImpuestos transaccionLineaImpuestos : transaccionLineaImpuestosList) {
                    if (transaccionLineaImpuestos.getTipoTributo().equalsIgnoreCase("2000")) {
                        invoiceObj.setRetentionPercentage(transaccionLineaImpuestos.getPorcentaje().setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                        retentionpercentage = transaccionLineaImpuestos.getPorcentaje();
                        break;
                    }
                }
            }
            if (retentionpercentage == null) {
                invoiceObj.setRetentionPercentage(BigDecimal.ZERO.toString());
            }
            if (percepctionAmount == null) {
                invoiceObj.setPerceptionAmount(invoiceType.getInvoiceType().getDocumentCurrencyCode().getValue() + " 0.00");
            }
            if (perceptionPercentage == null) {
                invoiceObj.setPerceptionPercentage(perceptionPercentage.ZERO.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion de los MONTOS.");
            }
            BigDecimal prepaidAmount = null;
            /* Agregando el monto de ANTICIPO con valor NEGATIVO */
            if (null != invoiceType.getInvoiceType().getPrepaidPayment() && !invoiceType.getInvoiceType().getPrepaidPayment().isEmpty() && null != invoiceType.getInvoiceType().getPrepaidPayment().get(0).getPaidAmount()) {
                prepaidAmount = invoiceType.getInvoiceType().getLegalMonetaryTotal().getPrepaidAmount().getValue().negate();
                // invoiceType.getInvoiceType().getPrepaidPayment().get(0).getPaidAmount().getValue().negate();
                invoiceObj.setPrepaidAmountValue(getCurrencyV3(prepaidAmount, currencyCode));
            } else {
                invoiceObj.setPrepaidAmountValue(currencyCode + " 0.00");
                prepaidAmount = BigDecimal.ZERO;
            }

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos de LINEAS personalizados." + invoiceType.getTransaccion().getTransaccionLineas().size());
            }

            List<WrapperItemObject> listaItem = new ArrayList<>();
            Set<TransaccionLineas> transaccionLineas = invoiceType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                if (log.isDebugEnabled()) {
                    log.debug("generateInvoicePDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionLinea.getTransaccionLineasUsucampos().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<String, String>();
                List<String> newlist = new ArrayList<String>();
                Set<TransaccionLineasUsucampos> transaccionLineasUsucampos = transaccionLinea.getTransaccionLineasUsucampos();
                for (TransaccionLineasUsucampos lineasUsucampos : transaccionLineasUsucampos) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos " + lineasUsucampos.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(lineasUsucampos.getUsuariocampos().getNombre(), lineasUsucampos.getValor());
                    newlist.add(lineasUsucampos.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }

                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);

            }
            invoiceObj.setItemListDynamic(listaItem);
            for (int i = 0; i < invoiceObj.getItemListDynamic().size(); i++) {
                for (int j = 0; j < invoiceObj.getItemListDynamic().get(i).getLstDinamicaItem().size(); j++) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Columna " + j);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Contenido " + invoiceObj.getItemListDynamic().get(i).getLstDinamicaItem().get(j));
                    }
                }
            }
            BigDecimal subtotalValue = getTransaccionTotales(invoiceType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1005);
            if (null != invoiceType.getInvoiceType().getPrepaidPayment() && !invoiceType.getInvoiceType().getPrepaidPayment().isEmpty() && null != invoiceType.getInvoiceType().getPrepaidPayment().get(0).getPaidAmount()) {
                invoiceObj.setSubtotalValue(getCurrency(subtotalValue.add(prepaidAmount.multiply(BigDecimal.ONE.negate())), currencyCode));
            } else {
                invoiceObj.setSubtotalValue(getCurrency(subtotalValue, currencyCode));
            }
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos de usuarios personalizados." + invoiceType.getTransaccion().getTransaccionContractdocrefs().size());
            }
            Set<TransaccionContractdocref> transaccionContractdocrefs = invoiceType.getTransaccion().getTransaccionContractdocrefs();
            if (null != invoiceType.getTransaccion().getTransaccionContractdocrefs() && 0 < invoiceType.getTransaccion().getTransaccionContractdocrefs().size()) {
                Map<String, String> hashedMap = new HashMap<>();
                for (TransaccionContractdocref transaccionContractdocref : transaccionContractdocrefs) {
                    hashedMap.put(transaccionContractdocref.getUsuariocampos().getNombre(), transaccionContractdocref.getValor());
                    invoiceObj.setInvoicePersonalizacion(hashedMap);
                }
            }
//            if (null != invoiceType.getTransaccion()
//                    .getTransaccionContractdocrefs()
//                    && 0 < invoiceType.getTransaccion()
//                    .getTransaccionContractdocrefs().size()) {
//                Map<String, String> hashedMap = new HashMap<String, String>();
//                for (int i = 0; i < invoiceType.getTransaccion()
//                        .getTransaccionContractdocrefs().size(); i++) {
//                    hashedMap.put(invoiceType.getTransaccion()
//                            .getTransaccionContractdocrefs().get(i)
//                            .getUsuariocampos().getNombre(), invoiceType
//                            .getTransaccion()
//                            .getTransaccionContractdocrefs().get(i)
//                            .getValor());
//                }
//                invoiceObj.setInvoicePersonalizacion(hashedMap);
//            }
            BigDecimal igvValue = getTaxTotalValue2(invoiceType.getTransaccion().getTransaccionImpuestosList(), IUBLConfig.TAX_TOTAL_IGV_ID);
            invoiceObj.setIgvValue(getCurrency(igvValue, currencyCode));
            BigDecimal iscValue = getTaxTotalValue2(invoiceType.getTransaccion().getTransaccionImpuestosList(), IUBLConfig.TAX_TOTAL_ISC_ID);
            invoiceObj.setIscValue(getCurrency(iscValue, currencyCode));

            Optional<LineExtensionAmountType> optional = Optional.ofNullable(invoiceType.getInvoiceType().getLegalMonetaryTotal().getLineExtensionAmount());
            if (optional.isPresent()) {
                BigDecimal lineExtensionAmount = invoiceType.getInvoiceType().getLegalMonetaryTotal().getLineExtensionAmount().getValue();
                invoiceObj.setAmountValue(getCurrency(lineExtensionAmount, currencyCode));
            } else {
                invoiceObj.setAmountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }
            BigDecimal descuento = null;

            if (null != invoiceType.getInvoiceType().getLegalMonetaryTotal().getAllowanceTotalAmount() && null != invoiceType.getInvoiceType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue()) {
                invoiceObj.setDiscountValue(getCurrencyV3(invoiceType.getInvoiceType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue(), currencyCode));
                descuento = invoiceType.getInvoiceType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue();
            } else {
                invoiceObj.setDiscountValue(getCurrencyV3(BigDecimal.ZERO, currencyCode));
                descuento = BigDecimal.ZERO;
            }

            BigDecimal payableAmount = invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue();
            invoiceObj.setTotalAmountValue(getCurrency(payableAmount, currencyCode));

            BigDecimal gravadaAmount = getTransaccionTotales(invoiceType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1001);
            invoiceObj.setGravadaAmountValue(getCurrency(gravadaAmount, currencyCode));

            BigDecimal inafectaAmount = getTransaccionTotales(invoiceType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1002);
            invoiceObj.setInafectaAmountValue(getCurrency(inafectaAmount, currencyCode));

            BigDecimal exoneradaAmount = getTransaccionTotales(invoiceType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1003);
            invoiceObj.setExoneradaAmountValue(getCurrency(exoneradaAmount, currencyCode));

            BigDecimal gratuitaAmount = getTransaccionTotales(invoiceType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1004);
            if (!gratuitaAmount.equals(BigDecimal.ZERO)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateInvoicePDF() [" + this.docUUID + "] Existe Op. Gratuitas.");
                }
                invoiceObj.setGratuitaAmountValue(getCurrency(gratuitaAmount, currencyCode));
            }
            if (gratuitaAmount.equals(BigDecimal.ZERO)) {
                invoiceObj.setGratuitaAmountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }

            invoiceObj.setNuevoCalculo(getCurrency(subtotalValue.add(prepaidAmount.multiply(BigDecimal.ONE.negate()).add(prepaidAmount).subtract(descuento)), currencyCode));

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }

            String barcodeValue = generateBarCodeInfoString(invoiceType.getTransaccion().getDocIdentidadNro(), invoiceType.getTransaccion().getDOCCodigo(), invoiceType.getTransaccion().getDOCSerie(), invoiceType.getTransaccion().getDOCNumero(), taxTotal, invoiceObj.getIssueDate(), invoiceType.getTransaccion().getDOCMontoTotal().toString(), invoiceType.getTransaccion().getSNDocIdentidadTipo(), invoiceType.getTransaccion().getSNDocIdentidadNro(), invoiceType.getInvoiceType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (log.isInfoEnabled()) {
                log.info("generateInvoicePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "01" + File.separator + invoiceType.getInvoiceType().getID().getValue() + ".png";
            File f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "01");
            if (!f.exists()) {
                f.mkdirs();
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);
            invoiceObj.setCodeQR(inputStream);
            f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "01");
            rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "01" + File.separator + invoiceType.getInvoiceType().getID().getValue() + ".png";
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStreamPDF = generatePDF417Code(barcodeValue, rutaPath, 200, 200, 1);

            invoiceObj.setBarcodeValue(inputStreamPDF);
            String digestValue = generateDigestValue(invoiceType.getInvoiceType().getUBLExtensions());

            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] VALOR RESUMEN: \n" + digestValue);
            }

            invoiceObj.setDigestValue(digestValue);

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo la informacion de PROPIEDADES (AdditionalProperty).");
            }
            Map<String, LegendObject> legendsMap = null;
            String factura = properties.getVersionUBL().getFactura();
            if (factura.equals("21")) {
                legendsMap = getaddLeyends(invoiceType.getInvoiceType().getNote());
            } else if (TipoVersionUBL.factura.equals("20")) {
                legendsMap = getAdditionalProperties(invoiceType.getInvoiceType().getUBLExtensions().getUBLExtension());
            }
            /*
            Map<String, LegendObject> legendsMap = getAdditionalProperties(invoiceType
                    .getInvoiceType().getUBLExtensions().getUBLExtension());
             */

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            LegendObject legendLetters = legendsMap.get(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            invoiceObj.setLetterAmountValue(legendLetters.getLegendValue());
            legendsMap.remove(IUBLConfig.ADDITIONAL_PROPERTY_1000);

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            invoiceObj.setLegends(getLegendList(legendsMap));
            invoiceObj.setResolutionCodeValue(this.resolutionCode);
            /*
             * Generando el PDF de la FACTURA con la informacion recopilada.
             */
            invoiceInBytes = PDFInvoiceCreator.getInstance(this.documentReportPath, this.legendSubReportPath, properties).createInvoicePDF(invoiceObj, docUUID);
        } catch (PDFReportException e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateInvoicePDF() [" + this.docUUID + "]");
        }
        return invoiceInBytes;
    } // generateInvoicePDF

    /**
     * Este metodo genera una representacion impresa de una BOLETA en formato
     * PDF, retornando un arreglo de bytes que contienen el PDF.
     *
     * @return Retorna un arreglo de bytes que contiene el PDF.
     * @throws PDFReportException
     */
    public byte[] generateDespatchAdvicePDF(UBLDocumentWRP despatchAdvice) throws PDFReportException {

        if (log.isDebugEnabled()) {
            log.debug("+generateDespatchAdvicePDF() [" + this.docUUID + "]");
        }
        byte[] despatchInBytes = null;
        try {
            DespatchAdviceObject despatchAdviceObject = new DespatchAdviceObject();
            if (log.isDebugEnabled()) {
                log.debug("generateDespatchAdvicePDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            despatchAdviceObject.setCodigoEmbarque(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getCodigoPuerto());
            despatchAdviceObject.setCodigoMotivoTraslado(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getCodigoMotivo());
            despatchAdviceObject.setDescripcionMotivoTraslado(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getDescripcionMotivo());
            despatchAdviceObject.setDireccionDestino(despatchAdvice.getTransaccion().getSNDIRDireccion());
            despatchAdviceObject.setDireccionPartida(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getDireccionPartida());
            despatchAdviceObject.setDocumentoConductor(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getDocumentoConductor());
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaEmision = format.format(despatchAdvice.getTransaccion().getDOCFechaEmision());
            despatchAdviceObject.setFechaEmision(fechaEmision);
            String fechaInicioTraslado = format.format(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getFechaInicioTraslado());
            despatchAdviceObject.setFechaTraslado(fechaInicioTraslado);
            despatchAdviceObject.setModalidadTraslado(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getModalidadTraslado());
            despatchAdviceObject.setNombreConsumidor(despatchAdvice.getTransaccion().getSNRazonSocial());
            despatchAdviceObject.setNombreEmisor(despatchAdvice.getTransaccion().getRazonSocial());
            despatchAdviceObject.setNumeroBultos(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getNumeroBultos());
            despatchAdviceObject.setNumeroContenedor(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getNumeroContenedor());
            despatchAdviceObject.setNumeroGuia(despatchAdvice.getTransaccion().getDOCSerie() + "-" + despatchAdvice.getTransaccion().getDOCNumero());
            despatchAdviceObject.setObervaciones(despatchAdvice.getTransaccion().getObservaciones());
            despatchAdviceObject.setPesoBruto(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getPeso());
            despatchAdviceObject.setTipoDocumentoConductor(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getTipoDocConductor());
            despatchAdviceObject.setTipoDocumentoTransportista(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getTipoDOCTransportista());
            despatchAdviceObject.setUMPesoBruto(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getUnidadMedida());
            despatchAdviceObject.setNumeroDocConsumidor(despatchAdvice.getTransaccion().getSNDocIdentidadNro());
            despatchAdviceObject.setNumeroDocEmisor(despatchAdvice.getTransaccion().getDocIdentidadNro());

            if (despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getModalidadTraslado().equalsIgnoreCase("01")) {
                despatchAdviceObject.setRUCTransportista(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getRUCTransporista());
                despatchAdviceObject.setNombreTransportista(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getNombreRazonTransportista());
            } else {
                despatchAdviceObject.setPlacaVehiculo(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getPlacaVehiculo());
                despatchAdviceObject.setLicenciaConducir(despatchAdvice.getTransaccion().getTransaccionGuiaRemision().getLicenciaConducir());
            }

            List<WrapperItemObject> listaItem = new ArrayList<>();
            Set<TransaccionLineas> transaccionLineasList = despatchAdvice.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLineas : transaccionLineasList) {
                if (log.isDebugEnabled()) {
                    log.debug("generateDespatchAdvicePDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionLineas.getTransaccionLineasUsucampos().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                Set<TransaccionLineasUsucampos> transaccionLineasUsucampos = transaccionLineas.getTransaccionLineasUsucampos();
                for (TransaccionLineasUsucampos transaccionLineasUsucampo : transaccionLineasUsucampos) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos " + transaccionLineasUsucampo.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(transaccionLineasUsucampo.getUsuariocampos().getNombre(), transaccionLineasUsucampo.getValor());
                    newlist.add(transaccionLineasUsucampo.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);
            }
            Set<TransaccionContractdocref> transaccionContractdocrefs = despatchAdvice.getTransaccion().getTransaccionContractdocrefs();
            if (null != transaccionContractdocrefs && 0 < transaccionContractdocrefs.size()) {
                for (TransaccionContractdocref transaccionContractdocref : transaccionContractdocrefs) {
                    Map<String, String> hashedMap = new HashMap<>();
                    hashedMap.put(transaccionContractdocref.getUsuariocampos().getNombre(), transaccionContractdocref.getValor());
                    despatchAdviceObject.setDespatchAdvicePersonalizacion(hashedMap);
                }
            }

            despatchAdviceObject.setNumeroDocEmisor(despatchAdvice.getTransaccion().getDocIdentidadNro());
            despatchAdviceObject.setNumeroGuia(despatchAdvice.getAdviceType().getID().getValue());
            despatchAdviceObject.setTelefono(despatchAdvice.getTransaccion().getTelefono());
            despatchAdviceObject.setTelefono1(despatchAdvice.getTransaccion().getTelefono1());
            despatchAdviceObject.setEmail(despatchAdvice.getTransaccion().getEMail());
            despatchAdviceObject.setPaginaWeb(despatchAdvice.getTransaccion().getWeb());
            despatchAdviceObject.setObervaciones(despatchAdvice.getTransaccion().getObservaciones());
            despatchAdviceObject.setItemListDynamic(listaItem);
            for (int i = 0; i < despatchAdviceObject.getItemListDynamic().size(); i++) {
                for (int j = 0; j < despatchAdviceObject.getItemListDynamic().get(i).getLstDinamicaItem().size(); j++) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateDespatchAdvicePDF() [" + this.docUUID + "] Fila " + i + " Columna " + j);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Contenido " + despatchAdviceObject.getItemListDynamic().get(i).getLstDinamicaItem().get(j));
                    }
                }
            }
            despatchAdviceObject.setSenderLogo(this.senderLogo);
            despatchInBytes = PDFDespatchAdviceCreator.getInstance(this.documentReportPath, this.legendSubReportPath).createDespatchAdvicePDF(despatchAdviceObject, docUUID);
        } catch (PDFReportException e) {
            log.error("generateDespatchAdvicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateDespatchAdvicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateDespatchAdvicePDF() [" + this.docUUID + "]");
        }
        return despatchInBytes;
    }

    public byte[] generateBoletaPDF(UBLDocumentWRP boletaType) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateBoletaPDF() [" + this.docUUID + "]");
        }
        byte[] boletaInBytes = null;

        try {
            BoletaObject boletaObj = new BoletaObject();

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            boletaObj.setDocumentIdentifier(boletaType.getBoletaType().getID().getValue());
            boletaObj.setIssueDate(formatIssueDate(boletaType.getBoletaType().getIssueDate().getValue()));

            boletaObj.setFormSap(boletaType.getTransaccion().getFEFormSAP());

            if (StringUtils.isNotBlank(boletaType.getBoletaType().getDocumentCurrencyCode().getName())) {
                boletaObj.setCurrencyValue(boletaType.getBoletaType().getDocumentCurrencyCode().getName().toUpperCase());
            } else {
                boletaObj.setCurrencyValue(boletaType.getTransaccion().getDOCMONNombre().toUpperCase());
            }

            if (null != boletaType.getBoletaType().getNote() && 0 < boletaType.getBoletaType().getNote().size()) {
                boletaObj.setDueDate(formatDueDate(boletaType.getTransaccion().getDOCFechaVencimiento()));
            } else {
                boletaObj.setDueDate(formatDueDate(boletaType.getTransaccion().getDOCFechaVencimiento()));
                //boletaObj.setDueDate(IPDFCreatorConfig.EMPTY_VALUE);
            }

            /* Informacion de SUNATTransaction */
            String sunatTransInfo = getSunatTransactionInfo(boletaType.getBoletaType().getUBLExtensions().getUBLExtension());
            if (StringUtils.isNotBlank(sunatTransInfo)) {
                boletaObj.setSunatTransaction(sunatTransInfo);
            }

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo guias de remision.");
            }
            boletaObj.setRemissionGuides(getRemissionGuides(boletaType.getBoletaType().getDespatchDocumentReference()));

            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] Guias de remision: " + boletaObj.getRemissionGuides());
            }
            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "]============= remision");
            }
            boletaObj.setPaymentCondition(boletaType.getTransaccion().getDOCCondPago());
            // No se encontraron impuestos en uno de los items de la transaccion. 
            //boletaObj.setPaymentCondition(getContractDocumentReference(boletaType.getBoletaType().getContractDocumentReference(),
            // IUBLConfig.CONTRACT_DOC_REF_PAYMENT_COND_CODE));

            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] Condicion_pago: " + boletaObj.getPaymentCondition());
            }
            Set<TransaccionContractdocref> transaccionContractdocrefs = boletaType.getTransaccion().getTransaccionContractdocrefs();
            if (null != transaccionContractdocrefs && 0 < transaccionContractdocrefs.size()) {
                for (TransaccionContractdocref transaccionContractdocref : transaccionContractdocrefs) {
                    Map<String, String> hashedMap = new HashMap<>();
                    hashedMap.put(transaccionContractdocref.getUsuariocampos().getNombre(), transaccionContractdocref.getValor());
                    boletaObj.setInvoicePersonalizacion(hashedMap);
                }
            }

            List<WrapperItemObject> listaItem = new ArrayList<>();
            Set<TransaccionLineas> transaccionLineasList = boletaType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLineas : transaccionLineasList) {
                if (log.isDebugEnabled()) {
                    log.debug("generateBoletaPDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionLineas.getTransaccionLineasUsucampos().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                Set<TransaccionLineasUsucampos> transaccionLineasUsucampos = transaccionLineas.getTransaccionLineasUsucampos();
                for (TransaccionLineasUsucampos lineasUsucampos : transaccionLineasUsucampos) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos " + lineasUsucampos.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(lineasUsucampos.getUsuariocampos().getNombre(), lineasUsucampos.getValor());
                    newlist.add(lineasUsucampos.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);
            }
            boletaObj.setItemsDynamic(listaItem);
            for (int i = 0; i < boletaObj.getItemsDynamic().size(); i++) {
                for (int j = 0; j < boletaObj.getItemsDynamic().get(i).getLstDinamicaItem().size(); j++) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Columna " + j);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Contenido " + boletaObj.getItemsDynamic().get(i).getLstDinamicaItem().get(j));
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion de los MONTOS.");
            }
            String currencyCode = boletaType.getBoletaType().getDocumentCurrencyCode().getValue();

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de Percepcion.");
            }
            BigDecimal percepctionAmount = null;
            BigDecimal perceptionPercentage = null;
            Set<TransaccionTotales> transaccionTotalesList = boletaType.getTransaccion().getTransaccionTotalesList();
            for (TransaccionTotales transaccionTotales : transaccionTotalesList) {
                if (transaccionTotales.getTransaccionTotalesPK().getId().equalsIgnoreCase("2001")) {
                    percepctionAmount = transaccionTotales.getMonto();
                    perceptionPercentage = transaccionTotales.getPrcnt();
                    boletaObj.setPerceptionAmount(currencyCode + " " + transaccionTotales.getMonto().toString());
                    boletaObj.setPerceptionPercentage(transaccionTotales.getPrcnt().toString() + "%");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de ISC.");
            }
            BigDecimal retentionpercentage = null;
            Set<TransaccionLineas> transaccionLineas = boletaType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                Set<TransaccionLineaImpuestos> transaccionLineaImpuestosList = transaccionLinea.getTransaccionLineaImpuestosList();
                for (TransaccionLineaImpuestos transaccionLineaImpuestos : transaccionLineaImpuestosList) {
                    if (transaccionLineaImpuestos.getTipoTributo().equalsIgnoreCase("2000")) {
                        transaccionLineaImpuestos.getPorcentaje().setScale(1, BigDecimal.ROUND_HALF_UP);
                        retentionpercentage = transaccionLineaImpuestos.getPorcentaje();
                        break;
                    }
                }
            }
            if (retentionpercentage == null) {
                boletaObj.setPorcentajeISC(BigDecimal.ZERO.toString());
            }

            if (percepctionAmount == null) {
                boletaObj.setPerceptionAmount(boletaType.getBoletaType().getDocumentCurrencyCode().getValue() + " 0.00");
            }
            if (perceptionPercentage == null) {
                boletaObj.setPerceptionPercentage(BigDecimal.ZERO.toString());
            }

            BigDecimal prepaidAmount = null;
            /* Agregando el monto de ANTICIPO con valor NEGATIVO */

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo monto de Anticipo. " + boletaType.getTransaccion().getANTICIPOMonto());
            }

            if (null != boletaType.getBoletaType().getPrepaidPayment() && !boletaType.getBoletaType().getPrepaidPayment().isEmpty() && null != boletaType.getBoletaType().getPrepaidPayment().get(0).getPaidAmount()) {
                if (log.isDebugEnabled()) {
                    log.debug("generateBoletaPDF() [" + this.docUUID + "] Monto de Anticipo Mayor a 0. ");
                }

                prepaidAmount = boletaType.getBoletaType().getLegalMonetaryTotal().getPrepaidAmount().getValue().negate();
                // invoiceType.getInvoiceType().getPrepaidPayment().get(0).getPaidAmount().getValue().negate();
                boletaObj.setPrepaidAmountValue(getCurrencyV3(prepaidAmount, currencyCode));
            } else {
                boletaObj.setPrepaidAmountValue(currencyCode + " 0.00");
                prepaidAmount = BigDecimal.ZERO;
            }

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }
            boletaObj.setSenderSocialReason(boletaType.getTransaccion().getRazonSocial());
            boletaObj.setSenderRuc(boletaType.getTransaccion().getDocIdentidadNro());
            boletaObj.setSenderFiscalAddress(boletaType.getTransaccion().getDIRNomCalle());
            boletaObj.setSenderDepProvDist(boletaType.getTransaccion().getDIRDistrito() + " " + boletaType.getTransaccion().getDIRProvincia() + " " + boletaType.getTransaccion().getDIRDepartamento());
            boletaObj.setSenderContact(boletaType.getTransaccion().getPersonContacto());
            boletaObj.setSenderMail(boletaType.getTransaccion().getEMail());
            boletaObj.setSenderLogo(this.senderLogo);
            boletaObj.setTelefono(boletaType.getTransaccion().getTelefono());
            boletaObj.setTelefono_1(boletaType.getTransaccion().getTelefono1());
            boletaObj.setWeb(boletaType.getTransaccion().getWeb());
            boletaObj.setWeb(boletaType.getTransaccion().getWeb());
            boletaObj.setPorcentajeIGV(boletaType.getTransaccion().getDOCPorcImpuesto());
            boletaObj.setComentarios(boletaType.getTransaccion().getFEComentario());
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                boletaObj.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                boletaObj.setValidezPDF("");
            }
            Set<TransaccionAnticipo> anticipoList = boletaType.getTransaccion().getTransaccionAnticipoList();
            String anticipos = anticipoList.parallelStream().map(TransaccionAnticipo::getAntiDOCSerieCorrelativo).collect(Collectors.joining(" "));
//            String Anticipos = "";
//            for (int i = 0; i < boletaType.getTransaccion().getTransaccionAnticipoList().size(); i++) {
//                Anticipos.concat(boletaType.getTransaccion().getTransaccionAnticipoList().get(i).getAntiDOCSerieCorrelativo() + " ");
//            }
            boletaObj.setAnticipos(anticipos);

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            boletaObj.setReceiverFullname(boletaType.getTransaccion().getSNRazonSocial());
            boletaObj.setReceiverIdentifier(boletaType.getTransaccion().getSNDocIdentidadNro());
            boletaObj.setReceiverIdentifierType(boletaType.getTransaccion().getSNDocIdentidadTipo());
            boletaObj.setReceiverFiscalAddress(boletaType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + boletaType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + boletaType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + boletaType.getTransaccion().getSNDIRDepartamento().toUpperCase());
            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            // boletaObj.setBoletaItems(getBoletaItems(boletaType.getBoletaType().getInvoiceLine()));

            BigDecimal subtotalValue = getTransaccionTotales(boletaType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1005);
            if (null != boletaType.getBoletaType().getPrepaidPayment() && !boletaType.getBoletaType().getPrepaidPayment().isEmpty() && null != boletaType.getBoletaType().getPrepaidPayment().get(0).getPaidAmount()) {
                boletaObj.setSubtotalValue(getCurrency(subtotalValue.add(prepaidAmount.multiply(BigDecimal.ONE.negate())), currencyCode));
            } else {
                boletaObj.setSubtotalValue(getCurrency(subtotalValue, currencyCode));
            }

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos de usuarios personalizados." + boletaType.getTransaccion().getTransaccionContractdocrefs().size());
            }

            BigDecimal igvValue = getTaxTotalValue2(boletaType.getTransaccion().getTransaccionImpuestosList(), IUBLConfig.TAX_TOTAL_IGV_ID);
            boletaObj.setIgvValue(getCurrency(igvValue, currencyCode));

            BigDecimal iscValue = getTaxTotalValue(boletaType.getBoletaType().getTaxTotal(), IUBLConfig.TAX_TOTAL_ISC_ID);
            boletaObj.setIscValue(getCurrency(iscValue, currencyCode));
            boolean exist = Optional.ofNullable(boletaType.getBoletaType().getLegalMonetaryTotal().getLineExtensionAmount()).isPresent();
            BigDecimal lineExtensionAmount = exist ? boletaType.getBoletaType().getLegalMonetaryTotal().getLineExtensionAmount().getValue() : BigDecimal.ZERO;
            boletaObj.setAmountValue(getCurrency(lineExtensionAmount, currencyCode));
            if (null != boletaType.getBoletaType().getLegalMonetaryTotal().getAllowanceTotalAmount() && null != boletaType.getBoletaType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue()) {
                boletaObj.setDiscountValue(getCurrency(boletaType.getBoletaType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue(), currencyCode));
            } else {
                boletaObj.setDiscountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }
            BigDecimal payableAmount = boletaType.getBoletaType().getLegalMonetaryTotal().getPayableAmount().getValue();
            boletaObj.setTotalAmountValue(getCurrency(payableAmount, currencyCode));

            BigDecimal gravadaAmount = getTransaccionTotales(boletaType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1001);
            boletaObj.setGravadaAmountValue(getCurrency(gravadaAmount, currencyCode));

            BigDecimal inafectaAmount = getTransaccionTotales(boletaType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1002);
            boletaObj.setInafectaAmountValue(getCurrency(inafectaAmount, currencyCode));

            BigDecimal exoneradaAmount = getTransaccionTotales(boletaType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1003);
            boletaObj.setExoneradaAmountValue(getCurrency(exoneradaAmount, currencyCode));

            BigDecimal gratuitaAmount = getTransaccionTotales(boletaType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1004);
            if (!gratuitaAmount.equals(BigDecimal.ZERO)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateBoletaPDF() [" + this.docUUID + "] Existe Op. Gratuitas.");
                }
                boletaObj.setGratuitaAmountValue(getCurrency(gratuitaAmount, currencyCode));
            } else {
                boletaObj.setGratuitaAmountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }

            String barcodeValue = generateBarCodeInfoString(boletaType.getTransaccion().getDocIdentidadNro(), boletaType.getTransaccion().getDOCCodigo(), boletaType.getTransaccion().getDOCSerie(), boletaType.getTransaccion().getDOCNumero(), boletaType.getBoletaType().getTaxTotal(), boletaObj.getIssueDate(), boletaType.getTransaccion().getDOCMontoTotal().toString(), boletaType.getTransaccion().getSNDocIdentidadTipo(), boletaType.getTransaccion().getSNDocIdentidadNro(), boletaType.getBoletaType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);


            String rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "03" + File.separator + boletaType.getBoletaType().getID().getValue() + ".png";
            File f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "03");
            if (!f.exists()) {
                f.mkdirs();
            }

            InputStream inputStream = generateQRCode(barcodeValue, rutaPath);
            boletaObj.setCodeQR(inputStream);

            f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "03");
            rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "03" + File.separator + boletaType.getBoletaType().getID().getValue() + ".png";
            if (!f.exists()) {
                f.mkdirs();
            }
            InputStream inputStreamPDF = generatePDF417Code(barcodeValue, rutaPath, 200, 200, 1);

            boletaObj.setBarcodeValue(inputStreamPDF);
            String digestValue = generateDigestValue(boletaType.getBoletaType().getUBLExtensions());

            if (log.isInfoEnabled()) {
                log.info("generateBoletaPDF() [" + this.docUUID + "] VALOR RESUMEN: \n" + digestValue);
            }

            boletaObj.setDigestValue(digestValue);

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Extrayendo la informacion de PROPIEDADES (AdditionalProperty).");
            }

            Map<String, LegendObject> legendsMap = null;

            if (TipoVersionUBL.boleta.equals("21")) {
                legendsMap = getaddLeyends(boletaType.getBoletaType().getNote());
            } else if (TipoVersionUBL.boleta.equals("20")) {
                legendsMap = getAdditionalProperties(boletaType.getBoletaType().getUBLExtensions().getUBLExtension());
            }
            /*
            Map<String, LegendObject> legendsMap = getaddLeyends(boletaType.getBoletaType().getNote());
             */
            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            LegendObject legendLetters = legendsMap.get(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            boletaObj.setLetterAmountValue(legendLetters.getLegendValue());
            legendsMap.remove(IUBLConfig.ADDITIONAL_PROPERTY_1000);

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            boletaObj.setLegends(getLegendList(legendsMap));

            boletaObj.setResolutionCodeValue(this.resolutionCode);

            /*
             * Generando el PDF de la FACTURA con la informacion recopilada.
             */
            boletaInBytes = PDFBoletaCreator.getInstance(this.documentReportPath, this.legendSubReportPath, properties).createBoletaPDF(boletaObj, docUUID);
        } catch (PDFReportException e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateInvoicePDF() [" + this.docUUID + "]");
        }
        return boletaInBytes;
    } // generateBoletaPDF

    /**
     * Este metodo genera una representacion impresa de una NOTA DE CREDITO en
     * formato PDF, retornando un arreglo de bytes que contienen el PDF.
     *
     * @param creditNoteType Objeto UBL que representa una NOTA DE CREDITO.
     * @return Retorna un arreglo de bytes que contiene el PDF.
     * @throws PDFReportException
     */
    public byte[] generateCreditNotePDF(UBLDocumentWRP creditNoteType, List<TransaccionTotales> transaccionTotales) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateCreditNotePDF() [" + this.docUUID + "]");
        }
        byte[] creditNoteInBytes = null;

        try {
            CreditNoteObject creditNoteObj = new CreditNoteObject();

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            creditNoteObj.setDocumentIdentifier(creditNoteType.getCreditNoteType().getID().getValue());
            creditNoteObj.setIssueDate(formatIssueDate(creditNoteType.getCreditNoteType().getIssueDate().getValue()));

            if (StringUtils.isNotBlank(creditNoteType.getCreditNoteType().getDocumentCurrencyCode().getName())) {
                creditNoteObj.setCurrencyValue(creditNoteType.getCreditNoteType().getDocumentCurrencyCode().getName().toUpperCase());
            } else {
                creditNoteObj.setCurrencyValue(creditNoteType.getTransaccion().getDOCMONNombre().toUpperCase());
            }

            if (null != creditNoteType.getCreditNoteType().getNote() && 0 < creditNoteType.getCreditNoteType().getNote().size()) {
                creditNoteObj.setDueDate(formatDueDate(creditNoteType.getTransaccion().getDOCFechaVencimiento()));
            } else {
                creditNoteObj.setDueDate(formatDueDate(creditNoteType.getTransaccion().getDOCFechaVencimiento()));
            }

            /* Informacion de SUNATTransaction */
            String sunatTransInfo = getSunatTransactionInfo(creditNoteType.getCreditNoteType().getUBLExtensions().getUBLExtension());
            if (StringUtils.isNotBlank(sunatTransInfo)) {
                creditNoteObj.setSunatTransaction(sunatTransInfo);
            }

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo guias de remision.");
            }
            creditNoteObj.setRemissionGuides(getRemissionGuides(creditNoteType.getCreditNoteType().getDespatchDocumentReference()));

            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "] Guias de remision: " + creditNoteObj.getRemissionGuides());
            }
            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "]============= remision");
            }

            creditNoteObj.setPaymentCondition(creditNoteType.getTransaccion().getDOCCondPago());

            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "]============= remision");
            }
            creditNoteObj.setDateDocumentReference(creditNoteType.getTransaccion().getFechaDOCRef());

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo Campos de usuarios personalizados." + creditNoteType.getTransaccion().getTransaccionContractdocrefs().size());
            }
            Set<TransaccionContractdocref> transaccionContractdocrefs = creditNoteType.getTransaccion().getTransaccionContractdocrefs();
            if (null != transaccionContractdocrefs && 0 < transaccionContractdocrefs.size()) {
                for (TransaccionContractdocref transaccionContractdocref : transaccionContractdocrefs) {
                    Map<String, String> hashedMap = new HashMap<>();
                    hashedMap.put(transaccionContractdocref.getUsuariocampos().getNombre(), transaccionContractdocref.getValor());
                    creditNoteObj.setInvoicePersonalizacion(hashedMap);
                }
            }
            List<WrapperItemObject> listaItem = new ArrayList<>();
            Set<TransaccionLineas> transaccionLineas = creditNoteType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                if (log.isDebugEnabled()) {
                    log.debug("generateCreditNotePDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionLinea.getTransaccionLineasUsucampos().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                Set<TransaccionLineasUsucampos> transaccionLineasUsucampos = transaccionLinea.getTransaccionLineasUsucampos();
                for (TransaccionLineasUsucampos transaccionLineasUsucampo : transaccionLineasUsucampos) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos " + transaccionLineasUsucampo.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(transaccionLineasUsucampo.getUsuariocampos().getNombre(), transaccionLineasUsucampo.getValor());
                    newlist.add(transaccionLineasUsucampo.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);

            }
            creditNoteObj.setItemsListDynamic(listaItem);
            for (int i = 0; i < creditNoteObj.getItemsListDynamic().size(); i++) {
                for (int j = 0; j < creditNoteObj.getItemsListDynamic().get(i).getLstDinamicaItem().size(); j++) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Columna " + j);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Contenido " + creditNoteObj.getItemsListDynamic().get(i).getLstDinamicaItem().get(j));
                    }
                }
            }
            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "] Condicion_pago: " + creditNoteObj.getPaymentCondition());
            }
            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "]============= condicion pago------");
            }
            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion de la NOTA DE CREDITO");
            }
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                creditNoteObj.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                creditNoteObj.setValidezPDF("");
            }
            creditNoteObj.setTypeOfCreditNote(creditNoteType.getTransaccion().getREFDOCMotivCode());
            creditNoteObj.setDescOfCreditNote(creditNoteType.getCreditNoteType().getDiscrepancyResponse().get(0).getDescription().get(0).getValue().toUpperCase());
            creditNoteObj.setDocumentReferenceToCn(getDocumentReferenceValue(creditNoteType.getCreditNoteType().getBillingReference().get(0)));

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }
            creditNoteObj.setSenderSocialReason(creditNoteType.getTransaccion().getRazonSocial());
            creditNoteObj.setSenderRuc(creditNoteType.getTransaccion().getDocIdentidadNro());
            creditNoteObj.setSenderFiscalAddress(creditNoteType.getTransaccion().getDIRNomCalle());
            creditNoteObj.setSenderDepProvDist(creditNoteType.getTransaccion().getDIRDistrito() + " " + creditNoteType.getTransaccion().getDIRProvincia() + " " + creditNoteType.getTransaccion().getDIRDepartamento());
            creditNoteObj.setSenderContact(creditNoteType.getTransaccion().getPersonContacto());
            creditNoteObj.setSenderMail(creditNoteType.getTransaccion().getEMail());
            creditNoteObj.setSenderLogo(this.senderLogo);
            creditNoteObj.setWeb(creditNoteType.getTransaccion().getWeb());
            creditNoteObj.setPorcentajeIGV(creditNoteType.getTransaccion().getDOCPorcImpuesto());
            creditNoteObj.setComentarios(creditNoteType.getTransaccion().getFEComentario());

            creditNoteObj.setTelefono(creditNoteType.getTransaccion().getTelefono());
            creditNoteObj.setTelefono1(creditNoteType.getTransaccion().getTelefono1());

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            creditNoteObj.setReceiverRegistrationName(creditNoteType.getTransaccion().getSNRazonSocial());
            creditNoteObj.setReceiverIdentifier(creditNoteType.getTransaccion().getSNDocIdentidadNro());
            creditNoteObj.setReceiverIdentifierType(creditNoteType.getTransaccion().getSNDocIdentidadTipo());

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion de la percepción.");
            }

            BigDecimal percepctionAmount = null;
            BigDecimal perceptionPercentage = null;
            Set<TransaccionTotales> transaccionTotalesList = creditNoteType.getTransaccion().getTransaccionTotalesList();
            for (TransaccionTotales totales : transaccionTotalesList) {
                if (totales.getTransaccionTotalesPK().getId().equalsIgnoreCase("2001")) {
                    percepctionAmount = totales.getMonto();
                    perceptionPercentage = totales.getPrcnt();
                    creditNoteObj.setPerceptionAmount(creditNoteType.getCreditNoteType().getDocumentCurrencyCode().getValue() + " " + totales.getMonto().toString());
                    creditNoteObj.setPerceptionPercentage(totales.getPrcnt().toString() + "%");
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de ISC.");
            }
            BigDecimal retentionpercentage = null;
            Set<TransaccionLineas> transaccionLineasList = creditNoteType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas lineas : transaccionLineasList) {
                Set<TransaccionLineaImpuestos> transaccionLineaImpuestosList = lineas.getTransaccionLineaImpuestosList();
                for (TransaccionLineaImpuestos transaccionLineaImpuestos : transaccionLineaImpuestosList) {
                    if (transaccionLineaImpuestos.getTipoTributo().equalsIgnoreCase("2000")) {
                        creditNoteObj.setISCPercetange(transaccionLineaImpuestos.getPorcentaje().setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                        retentionpercentage = transaccionLineaImpuestos.getPorcentaje();
                        break;
                    }
                }
            }

            if (retentionpercentage == null) {
                creditNoteObj.setISCPercetange(BigDecimal.ZERO.toString());
            }

            if (percepctionAmount == null) {
                creditNoteObj.setPerceptionAmount(creditNoteType.getCreditNoteType().getDocumentCurrencyCode().getValue() + " 0.00");
            }
            if (perceptionPercentage == null) {
                creditNoteObj.setPerceptionPercentage(BigDecimal.ZERO.toString());
            }

            if (creditNoteType.getCreditNoteType().getID().getValue().startsWith(IUBLConfig.INVOICE_SERIE_PREFIX)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateCreditNotePDF() [" + this.docUUID + "] El receptor es de un documento afectado de tipo FACTURA.");
                }
                creditNoteObj.setReceiverFiscalAddress(creditNoteType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRDepartamento().toUpperCase());

            } else if (creditNoteType.getCreditNoteType().getID().getValue().startsWith(IUBLConfig.BOLETA_SERIE_PREFIX)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateCreditNotePDF() [" + this.docUUID + "] El receptor es de un documento afectado de tipo BOLETA.");
                }
                creditNoteObj.setReceiverFiscalAddress(creditNoteType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + creditNoteType.getTransaccion().getSNDIRDepartamento().toUpperCase());
            } else {
                log.error("generateCreditNotePDF() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_431.getMessage());
                throw new PDFReportException(IVenturaError.ERROR_431);
            }

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            // creditNoteObj.setCreditNoteItems(getCreditNoteItems(creditNoteType.getCreditNoteType().getCreditNoteLine()));

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion de los MONTOS.");
            }
            String currencyCode = creditNoteType.getCreditNoteType().getDocumentCurrencyCode().getValue();

            BigDecimal subtotalValue = getSubtotalValueFromTransaction(transaccionTotales, creditNoteObj.getDocumentIdentifier());
            creditNoteObj.setSubtotalValue(getCurrency(subtotalValue, currencyCode));
            BigDecimal igvValue = getTaxTotalValue2(creditNoteType.getTransaccion().getTransaccionImpuestosList(), IUBLConfig.TAX_TOTAL_IGV_ID);

            creditNoteObj.setIgvValue(getCurrency(igvValue, currencyCode));

            BigDecimal iscValue = getTaxTotalValue(creditNoteType.getCreditNoteType().getTaxTotal(), IUBLConfig.TAX_TOTAL_ISC_ID);
            creditNoteObj.setIscValue(getCurrency(iscValue, currencyCode));
//
//            BigDecimal lineExtensionAmount = creditNoteType.getCreditNoteType()
//                    .getLegalMonetaryTotal().getLineExtensionAmount()
//                    .getValue();
//            creditNoteObj.setAmountValue(getCurrency(lineExtensionAmount, currencyCode));

            if (null != creditNoteType.getCreditNoteType().getLegalMonetaryTotal().getAllowanceTotalAmount() && null != creditNoteType.getCreditNoteType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue()) {
                creditNoteObj.setDiscountValue(getCurrency(creditNoteType.getCreditNoteType().getLegalMonetaryTotal().getAllowanceTotalAmount().getValue(), currencyCode));
            } else {
                creditNoteObj.setDiscountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }

            BigDecimal payableAmount = creditNoteType.getCreditNoteType().getLegalMonetaryTotal().getPayableAmount().getValue();
            creditNoteObj.setTotalAmountValue(getCurrency(payableAmount, currencyCode));

            BigDecimal gravadaAmount = getTransaccionTotales(creditNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1001);
            creditNoteObj.setGravadaAmountValue(getCurrency(gravadaAmount, currencyCode));

            BigDecimal inafectaAmount = getTransaccionTotales(creditNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1002);
            creditNoteObj.setInafectaAmountValue(getCurrency(inafectaAmount, currencyCode));

            BigDecimal exoneradaAmount = getTransaccionTotales(creditNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1003);
            creditNoteObj.setExoneradaAmountValue(getCurrency(exoneradaAmount, currencyCode));

            BigDecimal gratuitaAmount = getTransaccionTotales(creditNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1004);
            if (!gratuitaAmount.equals(BigDecimal.ZERO)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateCreditNotePDF() [" + this.docUUID + "] Existe Op. Gratuitas.");
                }
                creditNoteObj.setGratuitaAmountValue(getCurrency(gratuitaAmount, currencyCode));
            } else {
                creditNoteObj.setGratuitaAmountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }

            String barcodeValue = generateBarCodeInfoString(creditNoteType.getTransaccion().getDocIdentidadNro(), creditNoteType.getTransaccion().getDOCCodigo(), creditNoteType.getTransaccion().getDOCSerie(), creditNoteType.getTransaccion().getDOCNumero(), creditNoteType.getCreditNoteType().getTaxTotal(), creditNoteObj.getIssueDate(), creditNoteType.getTransaccion().getDOCMontoTotal().toString(), creditNoteType.getTransaccion().getSNDocIdentidadTipo(), creditNoteType.getTransaccion().getSNDocIdentidadNro(), creditNoteType.getCreditNoteType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "07" + File.separator + creditNoteType.getCreditNoteType().getID().getValue() + ".png";
            File f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "07");
            if (!f.exists()) {
                f.mkdirs();
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);

            creditNoteObj.setCodeQR(inputStream);

            f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "07");
            rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "07" + File.separator + creditNoteType.getCreditNoteType().getID().getValue() + ".png";
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStreamPDF = generatePDF417Code(barcodeValue, rutaPath, 200, 200, 1);

            creditNoteObj.setBarcodeValue(inputStreamPDF);
            String digestValue = generateDigestValue(creditNoteType.getCreditNoteType().getUBLExtensions());

            if (log.isInfoEnabled()) {
                log.info("generateCreditNotePDF() [" + this.docUUID + "] VALOR RESUMEN: \n" + digestValue);
            }

            creditNoteObj.setDigestValue(digestValue);

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo la informacion de PROPIEDADES (AdditionalProperty).");
            }

            /*
            Map<String, LegendObject> legendsMap = getaddLeyends(creditNoteType.getCreditNoteType().getNote());
             */
            Map<String, LegendObject> legendsMap = null;

            if (TipoVersionUBL.notacredito.equals("21")) {
                legendsMap = getaddLeyends(creditNoteType.getCreditNoteType().getNote());
            } else if (TipoVersionUBL.notadebito.equals("20")) {
                legendsMap = getAdditionalProperties(creditNoteType.getCreditNoteType().getUBLExtensions().getUBLExtension());
            }

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            LegendObject legendLetters = legendsMap.get(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            creditNoteObj.setLetterAmountValue(legendLetters.getLegendValue());
            legendsMap.remove(IUBLConfig.ADDITIONAL_PROPERTY_1000);

            if (log.isDebugEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            creditNoteObj.setLegends(getLegendList(legendsMap));

            creditNoteObj.setResolutionCodeValue(this.resolutionCode);

            /*
             * Generando el PDF de la FACTURA con la informacion recopilada.
             */
            creditNoteInBytes = PDFCreditNoteCreator.getInstance(this.documentReportPath, this.legendSubReportPath, properties).createCreditNotePDF(creditNoteObj, docUUID);
        } catch (PDFReportException e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateInvoicePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateInvoicePDF() [" + this.docUUID + "]");
        }
        return creditNoteInBytes;
    } // generateCreditNotePDF

    /**
     * Este metodo genera una representacion impresa de una NOTA DE DEBITO en
     * formato PDF, retornando un arreglo de bytes que contienen el PDF.
     *
     * @param debitNoteType Objeto UBL que representa una NOTA DE DEBITO.
     * @return Retorna un arreglo de bytes que contiene el PDF.
     * @throws PDFReportException
     */
    public byte[] generateDebitNotePDF(UBLDocumentWRP debitNoteType, List<TransaccionTotales> transactionTotalList) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+generateDebitNotePDF() [" + this.docUUID + "]");
        }
        byte[] debitNoteInBytes = null;

        try {
            DebitNoteObject debitNoteObj = new DebitNoteObject();

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion GENERAL del documento.");
            }
            debitNoteObj.setDocumentIdentifier(debitNoteType.getDebitNoteType().getID().getValue());
            debitNoteObj.setIssueDate(formatIssueDate(debitNoteType.getDebitNoteType().getIssueDate().getValue()));

            if (StringUtils.isNotBlank(debitNoteType.getDebitNoteType().getDocumentCurrencyCode().getName())) {
                debitNoteObj.setCurrencyValue(debitNoteType.getDebitNoteType().getDocumentCurrencyCode().getName().toUpperCase());
            } else {
                debitNoteObj.setCurrencyValue(debitNoteType.getTransaccion().getDOCMONNombre().toUpperCase());
            }

            /* Informacion de SUNATTransaction */
            String sunatTransInfo = getSunatTransactionInfo(debitNoteType.getDebitNoteType().getUBLExtensions().getUBLExtension());
            if (StringUtils.isNotBlank(sunatTransInfo)) {
                debitNoteObj.setSunatTransaction(sunatTransInfo);
            }

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo guias de remision.");
            }
            debitNoteObj.setRemissionGuides(getRemissionGuides(debitNoteType.getDebitNoteType().getDespatchDocumentReference()));

            if (log.isInfoEnabled()) {
                log.info("generateDebitNotePDF() [" + this.docUUID + "] Guias de remision: " + debitNoteObj.getRemissionGuides());
            }
            if (log.isInfoEnabled()) {
                log.info("generateDebitNotePDF() [" + this.docUUID + "]============= remision");
            }

            debitNoteObj.setPaymentCondition(debitNoteType.getTransaccion().getDOCCondPago());

            debitNoteObj.setSellOrder(getContractDocumentReference(debitNoteType.getDebitNoteType().getContractDocumentReference(), IUBLConfig.CONTRACT_DOC_REF_SELL_ORDER_CODE));
//            debitNoteObj.setSellerName(getContractDocumentReference(
//                    debitNoteType.getDebitNoteType()
//                    .getContractDocumentReference(),
//                    IUBLConfig.CONTRACT_DOC_REF_SELLER_CODE));
            if (log.isInfoEnabled()) {
                log.info("generateDebitNotePDF() [" + this.docUUID + "] Condicion_pago: " + debitNoteObj.getPaymentCondition());
                log.info("generateDebitNotePDF() [" + this.docUUID + "] Orden de venta: " + debitNoteObj.getSellOrder());
                log.info("generateDebitNotePDF() [" + this.docUUID + "] Nombre_vendedor: " + debitNoteObj.getSellerName());
            }
            if (log.isInfoEnabled()) {
                log.info("generateDebitNotePDF() [" + this.docUUID + "]============= condicion pago------");
            }

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion de la NOTA DE DEBITO");
            }
            debitNoteObj.setTypeOfDebitNote(debitNoteType.getTransaccion().getREFDOCMotivCode());
            debitNoteObj.setDescOfDebitNote(debitNoteType.getDebitNoteType().getDiscrepancyResponse().get(0).getDescription().get(0).getValue().toUpperCase());
            debitNoteObj.setDocumentReferenceToCn(getDocumentReferenceValue(debitNoteType.getDebitNoteType().getBillingReference().get(0)));
            debitNoteObj.setDateDocumentReference(debitNoteType.getTransaccion().getFechaDOCRef());

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion del EMISOR del documento.");
            }
            debitNoteObj.setSenderSocialReason(debitNoteType.getTransaccion().getRazonSocial());
            debitNoteObj.setSenderRuc(debitNoteType.getTransaccion().getDocIdentidadNro());
            debitNoteObj.setSenderFiscalAddress(debitNoteType.getTransaccion().getDIRNomCalle());
            debitNoteObj.setSenderDepProvDist(debitNoteType.getTransaccion().getDIRDistrito() + " " + debitNoteType.getTransaccion().getDIRProvincia() + " " + debitNoteType.getTransaccion().getDIRDepartamento());
            debitNoteObj.setSenderContact(debitNoteType.getTransaccion().getPersonContacto());
            debitNoteObj.setSenderMail(debitNoteType.getTransaccion().getEMail());
            debitNoteObj.setSenderLogo(this.senderLogo);
            debitNoteObj.setWeb(debitNoteType.getTransaccion().getWeb());
            debitNoteObj.setPorcentajeIGV(debitNoteType.getTransaccion().getDOCPorcImpuesto());
            debitNoteObj.setComentarios(debitNoteType.getTransaccion().getFEComentario());

            debitNoteObj.setTelefono(debitNoteType.getTransaccion().getTelefono());
            debitNoteObj.setTelefono_1(debitNoteType.getTransaccion().getTelefono1());

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo Campos de usuarios personalizados." + debitNoteType.getTransaccion().getTransaccionContractdocrefs().size());
            }
            Set<TransaccionContractdocref> transaccionContractdocrefs = debitNoteType.getTransaccion().getTransaccionContractdocrefs();
            if (null != transaccionContractdocrefs && 0 < transaccionContractdocrefs.size()) {
                for (TransaccionContractdocref transaccionContractdocref : transaccionContractdocrefs) {
                    Map<String, String> hashedMap = new HashMap<>();
                    hashedMap.put(transaccionContractdocref.getUsuariocampos().getNombre(), transaccionContractdocref.getValor());
                    debitNoteObj.setInvoicePersonalizacion(hashedMap);
                }
            }
            List<WrapperItemObject> listaItem = new ArrayList<>();
            Set<TransaccionLineas> transaccionLineasList = debitNoteType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLineas : transaccionLineasList) {
                if (log.isDebugEnabled()) {
                    log.debug("generateDebitNotePDF() [" + this.docUUID + "] Agregando datos al HashMap" + transaccionLineas.getTransaccionLineasUsucampos().size());
                }
                WrapperItemObject itemObject = new WrapperItemObject();
                Map<String, String> itemObjectHash = new HashMap<>();
                List<String> newlist = new ArrayList<>();
                Set<TransaccionLineasUsucampos> transaccionLineasUsucampos = transaccionLineas.getTransaccionLineasUsucampos();
                for (TransaccionLineasUsucampos transaccionLineasUsucampo : transaccionLineasUsucampos) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo Campos " + transaccionLineasUsucampo.getUsuariocampos().getNombre());
                    }
                    itemObjectHash.put(transaccionLineasUsucampo.getUsuariocampos().getNombre(), transaccionLineasUsucampo.getValor());
                    newlist.add(transaccionLineasUsucampo.getValor());
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Nuevo Tamanio " + newlist.size());
                    }
                }
                itemObject.setLstItemHashMap(itemObjectHash);
                itemObject.setLstDinamicaItem(newlist);
                listaItem.add(itemObject);
            }
            debitNoteObj.setItemsListDynamic(listaItem);
            for (int i = 0; i < debitNoteObj.getItemsListDynamic().size(); i++) {
                for (int j = 0; j < debitNoteObj.getItemsListDynamic().get(i).getLstDinamicaItem().size(); j++) {
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Columna " + j);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("generateInvoicePDF() [" + this.docUUID + "] Fila " + i + " Contenido " + debitNoteObj.getItemsListDynamic().get(i).getLstDinamicaItem().get(j));
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion del RECEPTOR del documento.");
            }
            debitNoteObj.setReceiverRegistrationName(debitNoteType.getTransaccion().getSNRazonSocial());
            debitNoteObj.setReceiverIdentifier(debitNoteType.getTransaccion().getSNDocIdentidadNro());
            debitNoteObj.setReceiverIdentifierType(debitNoteType.getTransaccion().getSNDocIdentidadTipo());

            if (debitNoteType.getDebitNoteType().getID().getValue().startsWith(IUBLConfig.INVOICE_SERIE_PREFIX)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateDebitNotePDF() [" + this.docUUID + "] El receptor es de un documento afectado de tipo FACTURA.");
                }
                debitNoteObj.setReceiverFiscalAddress(debitNoteType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRDepartamento().toUpperCase());
            } else if (debitNoteType.getDebitNoteType().getID().getValue().startsWith(IUBLConfig.BOLETA_SERIE_PREFIX)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateDebitNotePDF() [" + this.docUUID + "] El receptor es de un documento afectado de tipo BOLETA.");
                }
                debitNoteObj.setReceiverFiscalAddress(debitNoteType.getTransaccion().getSNDIRNomCalle().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRDistrito().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRProvincia().toUpperCase() + " - " + debitNoteType.getTransaccion().getSNDIRDepartamento().toUpperCase());
            } else {
                log.error("generateDebitNotePDF() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_431.getMessage());
                throw new PDFReportException(IVenturaError.ERROR_431);
            }
            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion de los ITEMS.");
            }
            // debitNoteObj.setDebitNoteItems(getDebitNoteItems(debitNoteType.getDebitNoteType().getDebitNoteLine()));
            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion de los MONTOS.");
            }
            String currencyCode = debitNoteType.getTransaccion().getDOCMONCodigo();
            BigDecimal subtotalValue = getSubtotalValueFromTransaction(transactionTotalList, debitNoteObj.getDocumentIdentifier());
//
            debitNoteObj.setSubtotalValue(getCurrency(subtotalValue, currencyCode));
            BigDecimal igvValue = getTaxTotalValue2(debitNoteType.getTransaccion().getTransaccionImpuestosList(), IUBLConfig.TAX_TOTAL_IGV_ID);
            debitNoteObj.setIgvValue(getCurrency(igvValue, currencyCode));
            BigDecimal iscValue = getTaxTotalValue(debitNoteType.getDebitNoteType().getTaxTotal(), IUBLConfig.TAX_TOTAL_ISC_ID);
            debitNoteObj.setIscValue(getCurrency(iscValue, currencyCode));
//
//            BigDecimal lineExtensionAmount = debitNoteType.getDebitNoteType()
//                    .getRequestedMonetaryTotal().getLineExtensionAmount()
//                    .getValue();
//            debitNoteObj.setAmountValue(getCurrency(lineExtensionAmount,
//                    currencyCode));

            if (log.isDebugEnabled()) {
                log.debug("generateCreditNotePDF() [" + this.docUUID + "] Extrayendo informacion de la percepción.");
            }

            BigDecimal percepctionAmount = null;
            BigDecimal perceptionPercentage = null;
            Set<TransaccionTotales> transaccionTotalesList = debitNoteType.getTransaccion().getTransaccionTotalesList();
            for (TransaccionTotales transaccionTotales : transaccionTotalesList) {
                if (transaccionTotales.getTransaccionTotalesPK().getId().equalsIgnoreCase("2001")) {
                    percepctionAmount = transaccionTotales.getMonto();
                    perceptionPercentage = transaccionTotales.getPrcnt();
                    debitNoteObj.setPerceptionAmount(debitNoteType.getDebitNoteType().getDocumentCurrencyCode().getValue() + " " + transaccionTotales.getMonto().toString());
                    debitNoteObj.setPerceptionPercentage(transaccionTotales.getPrcnt().toString() + "%");
                }

            }
            if (log.isDebugEnabled()) {
                log.debug("generateInvoicePDF() [" + this.docUUID + "] Extrayendo monto de ISC.");
            }
            BigDecimal retentionpercentage = null;
            Set<TransaccionLineas> transaccionLineas = debitNoteType.getTransaccion().getTransaccionLineas();
            for (TransaccionLineas transaccionLinea : transaccionLineas) {
                Set<TransaccionLineaImpuestos> transaccionLineaImpuestosList = transaccionLinea.getTransaccionLineaImpuestosList();
                for (TransaccionLineaImpuestos transaccionLineaImpuesto : transaccionLineaImpuestosList) {
                    if (transaccionLineaImpuesto.getTipoTributo().equalsIgnoreCase("2000")) {
                        debitNoteObj.setISCPercetange(transaccionLineaImpuesto.getPorcentaje().setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                        retentionpercentage = transaccionLineaImpuesto.getPorcentaje();
                        break;
                    }
                }
            }
            if (retentionpercentage == null) {
                debitNoteObj.setISCPercetange(BigDecimal.ZERO.toString());
            }
            if (percepctionAmount == null) {
                debitNoteObj.setPerceptionAmount(debitNoteType.getDebitNoteType().getDocumentCurrencyCode().getValue() + " 0.00");
            }
            if (perceptionPercentage == null) {
                debitNoteObj.setPerceptionPercentage(BigDecimal.ZERO.toString());
            }
            if (null != debitNoteType.getDebitNoteType().getRequestedMonetaryTotal().getAllowanceTotalAmount() && null != debitNoteType.getDebitNoteType().getRequestedMonetaryTotal().getAllowanceTotalAmount().getValue()) {
                debitNoteObj.setDiscountValue(getCurrency(debitNoteType.getDebitNoteType().getRequestedMonetaryTotal().getAllowanceTotalAmount().getValue(), currencyCode));
            } else {
                debitNoteObj.setDiscountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }
            BigDecimal payableAmount = debitNoteType.getDebitNoteType().getRequestedMonetaryTotal().getPayableAmount().getValue();
            debitNoteObj.setTotalAmountValue(getCurrency(payableAmount, currencyCode));
            BigDecimal gravadaAmount = getTransaccionTotales(debitNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1001);
            debitNoteObj.setGravadaAmountValue(getCurrency(gravadaAmount, currencyCode));
            BigDecimal inafectaAmount = getTransaccionTotales(debitNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1002);
            debitNoteObj.setInafectaAmountValue(getCurrency(inafectaAmount, currencyCode));
            BigDecimal exoneradaAmount = getTransaccionTotales(debitNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1003);
            debitNoteObj.setExoneradaAmountValue(getCurrency(exoneradaAmount, currencyCode));
            BigDecimal gratuitaAmount = getTransaccionTotales(debitNoteType.getTransaccion().getTransaccionTotalesList(), IUBLConfig.ADDITIONAL_MONETARY_1004);
            if (!gratuitaAmount.equals(BigDecimal.ZERO)) {
                if (log.isDebugEnabled()) {
                    log.debug("generateDebitNotePDF() [" + this.docUUID + "] Existe Op. Gratuitas.");
                }
                debitNoteObj.setGratuitaAmountValue(getCurrency(gratuitaAmount, currencyCode));
            } else {
                debitNoteObj.setGratuitaAmountValue(getCurrency(BigDecimal.ZERO, currencyCode));
            }

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo informacion del CODIGO DE BARRAS.");
            }

            String barcodeValue = generateBarCodeInfoString(debitNoteType.getTransaccion().getDocIdentidadNro(), debitNoteType.getTransaccion().getDOCCodigo(), debitNoteType.getTransaccion().getDOCSerie(), debitNoteType.getTransaccion().getDOCNumero(), debitNoteType.getDebitNoteType().getTaxTotal(), debitNoteObj.getIssueDate(), debitNoteType.getTransaccion().getDOCMontoTotal().toString(), debitNoteType.getTransaccion().getSNDocIdentidadTipo(), debitNoteType.getTransaccion().getSNDocIdentidadNro(), debitNoteType.getDebitNoteType().getUBLExtensions());

//            String barcodeValue = generateBarcodeInfo(debitNoteType
//                    .getDebitNoteType().getID().getValue(),
//                    IUBLConfig.DOC_DEBIT_NOTE_CODE,
//                    debitNoteObj.getIssueDate(), debitNoteType
//                    .getDebitNoteType().getRequestedMonetaryTotal()
//                    .getPayableAmount().getValue(), debitNoteType
//                    .getDebitNoteType().getTaxTotal(), debitNoteType
//                    .getDebitNoteType().getAccountingSupplierParty(),
//                    debitNoteType.getDebitNoteType()
//                    .getAccountingCustomerParty(), debitNoteType
//                    .getDebitNoteType().getUBLExtensions());
            if (log.isInfoEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //debitNoteObj.setBarcodeValue(barcodeValue);

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "08" + File.separator + debitNoteType.getDebitNoteType().getID().getValue() + ".png";
            File f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoQR" + File.separator + "08");
            if (!f.exists()) {
                f.mkdirs();
            }

            if (log.isInfoEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] rutaPath: \n" + rutaPath);
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);

            debitNoteObj.setCodeQR(inputStream);

            f = new File(properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + "08");
            rutaPath = properties.getDirectorio().getAdjuntos() + File.separator + "CodigoPDF417" + File.separator + debitNoteType.getDebitNoteType().getID().getValue() + ".png";
            if (!f.exists()) {
                f.mkdirs();
            }
            inputStreamPDF = generatePDF417Code(barcodeValue, rutaPath, 200, 200, 1);

            debitNoteObj.setBarcodeValue(inputStreamPDF);

            String digestValue = generateDigestValue(debitNoteType.getDebitNoteType().getUBLExtensions());

            if (log.isInfoEnabled()) {
                log.debug("generateBoletaPDF() [" + this.docUUID + "] VALOR RESUMEN: \n" + digestValue);
            }

            debitNoteObj.setDigestValue(digestValue);
            String usoPdfSinRespuesta = properties.getUsoSunat().getPdf();
            boolean isPdfSinRespuesta = Boolean.parseBoolean(usoPdfSinRespuesta);
            if (isPdfSinRespuesta) {
                debitNoteObj.setValidezPDF("Este documento no tiene validez fiscal.");
            } else {
                debitNoteObj.setValidezPDF("");
            }

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Extrayendo la informacion de PROPIEDADES (AdditionalProperty).");
            }
            /*
            Map<String, LegendObject> legendsMap = getaddLeyends(debitNoteType.getDebitNoteType().getNote());
             */
            Map<String, LegendObject> legendsMap = null;
            if (TipoVersionUBL.notadebito.equals("21")) {
                legendsMap = getaddLeyends(debitNoteType.getDebitNoteType().getNote());
            } else if (TipoVersionUBL.notadebito.equals("20")) {
                legendsMap = getAdditionalProperties(debitNoteType.getDebitNoteType().getUBLExtensions().getUBLExtension());
            }
            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Colocando el importe en LETRAS.");
            }
            LegendObject legendLetters = legendsMap.get(IUBLConfig.ADDITIONAL_PROPERTY_1000);
            debitNoteObj.setLetterAmountValue(legendLetters.getLegendValue());
            legendsMap.remove(IUBLConfig.ADDITIONAL_PROPERTY_1000);

            if (log.isDebugEnabled()) {
                log.debug("generateDebitNotePDF() [" + this.docUUID + "] Colocando la lista de LEYENDAS.");
            }
            debitNoteObj.setLegends(getLegendList(legendsMap));
            debitNoteObj.setResolutionCodeValue(this.resolutionCode);
            /*
             * Generando el PDF de la NOTA DE CREDITO con la informacion
             * recopilada.
             */
            debitNoteInBytes = PDFDebitNoteCreator.getInstance(this.documentReportPath, this.legendSubReportPath, properties).createDebitNotePDF(debitNoteObj, docUUID);
        } catch (PDFReportException e) {
            log.error("generateDebitNotePDF() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("generateDebitNotePDF() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            ErrorObj error = new ErrorObj(IVenturaError.ERROR_2.getId(), e.getMessage());
            throw new PDFReportException(error);
        }
        if (log.isDebugEnabled()) {
            log.debug("-generateDebitNotePDF() [" + this.docUUID + "]");
        }
        return debitNoteInBytes;
    } // generateDebitNotePDF

    private BigDecimal getSubtotalValueFromTransaction(List<TransaccionTotales> transactionTotalList, String identifier) throws UBLDocumentException {
        if (null != transactionTotalList && !transactionTotalList.isEmpty()) {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (TransaccionTotales transaccionTotal : transactionTotalList) {
                if (Objects.equals("1005", transaccionTotal.getTransaccionTotalesPK().getId())) {
                    subtotal = transaccionTotal.getMonto();
                    break;
                }
            }
            return subtotal;
        } else {
            log.error("getSubtotalValueFromTransaction() [" + identifier + "] ERROR: " + IVenturaError.ERROR_330.getMessage());
            throw new UBLDocumentException(IVenturaError.ERROR_330);
        }
    }
} // PDFGenerateHandler
