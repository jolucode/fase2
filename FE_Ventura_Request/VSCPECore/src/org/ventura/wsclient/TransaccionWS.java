package org.ventura.wsclient;

import com.sap.smb.sbo.api.ICompany;
import com.sap.smb.sbo.api.SBOCOMException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ventura.cpe.bl.TransaccionBL;
import org.ventura.cpe.dao.DocumentoDAO;
import org.ventura.cpe.dao.controller.BdCodErrorSunat;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.CodErrorSunat;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.pfeconn.TransaccionPFE;
import org.ventura.soluciones.commons.config.IUBLConfig;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.commons.exception.SignerDocumentException;
import org.ventura.soluciones.commons.exception.UBLDocumentException;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.wsclient.config.ISunatConnectorConfig;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.*;
import org.ventura.wsclient.handler.FEProcessHandler;

import java.io.File;
import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Esta clase principal contiene las implementaciones de los metodos necesarios
 * para realizar el proceso de Facturacion Electronica para los distintos tipos
 * de documentos existentes.
 */
public class TransaccionWS extends TransaccionPFE {

    private final Logger logger = Logger.getLogger(TransaccionWS.class);

    /**
     * Este metodo realiza el proceso de facturacion electronica (envio a Sunat
     * y creacion de la representacion impresa) recibiendo como parametro de
     * entrada un objeto Transaccion y retornando un objeto
     * TransaccionRespuesta.
     * <p>
     * NOTA: Utiliza el metodo WS de Sunat sendBill.
     *
     * @param transaction
     * @param extractor
     * @param Sociedad
     * @return
     * @throws org.ventura.cpe.excepciones.VenturaExcepcion
     * @see org.ventura.cpe.dto.hb.Transaccion
     * @see org.ventura.cpe.dto.TransaccionRespuesta
     */
    @Override
    public TransaccionRespuesta EnviarTransaccion(Transaccion transaction, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection) throws VenturaExcepcion {
        /*
         * Carga el log4j.properties, en caso de que se saque un log4j adicional
         *
         * **/
        String largePath = System.getProperty("user.dir") + File.separator + "log4j.properties";
        File f = new File(largePath);
        if (f.exists() && !f.isDirectory()) {
            PropertyConfigurator.configure(largePath);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("+EnviarTransaccion()");
        }
        TransaccionRespuesta transactionResponse = null;
        String docUUID = null;

        try { //comienza aqui
            if (null != transaction) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Iniciando envio de objeto Transaccion.", transaction.getFEId());

                /*
                 * UUID generado para el seguimiento del proceso
                 */
                docUUID = transaction.getFEId();
                if (logger.isInfoEnabled()) {
                    logger.info("EnviarTransaccion() El identificador de la transaccion: " + docUUID);
                }

                if (transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("EnviarTransaccion() [" + docUUID + "] El tipo de transaccion es: " + ISunatConnectorConfig.FE_TIPO_TRANS_EMISION);
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}" + "] El tipo de transaccion es: " + ISunatConnectorConfig.FE_TIPO_TRANS_EMISION, docUUID);
                    FEProcessHandler processHandler = FEProcessHandler.newInstance(docUUID);
                    if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de FACTURA.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una FACTURA.", docUUID);
                        transactionResponse = processHandler.transactionInvoiceDocument(transaction, IUBLConfig.DOC_INVOICE_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de BOLETA.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una BOLETA.", docUUID);
                        transactionResponse = processHandler.transactionBoletaDocument(transaction, IUBLConfig.DOC_BOLETA_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de NOTA DE CREDITO.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una NOTA DE CREDITO.", docUUID);
                        transactionResponse = processHandler.transactionCreditNoteDocument(transaction, IUBLConfig.DOC_CREDIT_NOTE_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de NOTA DE DEBITO.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una NOTA DE DEBITO.", docUUID);
                        transactionResponse = processHandler.transactionDebitNoteDocument(transaction, IUBLConfig.DOC_DEBIT_NOTE_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de PERCEPCION.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una PERCEPCION.", docUUID);
                        transactionResponse = processHandler.transactionPerceptionDocument(transaction, IUBLConfig.DOC_PERCEPTION_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de RETENCION.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una RETENCION.", docUUID);
                        transactionResponse = processHandler.transactionRetentionDocument(transaction, IUBLConfig.DOC_RETENTION_CODE, extractor, Sociedad, configuration, connection);
                    } else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de GUIA DE REMISIÓN.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una GUIA DE REMISIÓN.", docUUID);
                        //transactionResponse = processHandler.transactionRemissionGuideDocument(transaction, IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, extractor, Sociedad);
                        transactionResponse = processHandler.transactionRemissionGuideDocumentRest(transaction, IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, extractor, Sociedad, configuration, connection);
                    }else if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_SENDER_CARRIER_GUIDE_CODE)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("EnviarTransaccion() [" + docUUID + "] ################################## Envio de GUIA DE TRANSPORTISTA.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de EMISION de una GUIA DE TRANSPORTISTA.", docUUID);
                        transactionResponse = processHandler.transactionCarrierGuideDocumentRest(transaction, IUBLConfig.DOC_SENDER_CARRIER_GUIDE_CODE, extractor, Sociedad, configuration, connection);
                    } else {
                        logger.error("EnviarTransaccion() [" + docUUID + "] ERROR: " + IVenturaError.ERROR_463.getMessage());
                        throw new Exception(IVenturaError.ERROR_463.getMessage());
                    }
                 } else if (transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_BAJA)) {
                    System.out.println("Revisando si es una baja y por donde entra.");
                    if (logger.isDebugEnabled()) {
                        logger.debug("EnviarTransaccion() [" + docUUID + "] El tipo de transaccion es: " + ISunatConnectorConfig.FE_TIPO_TRANS_BAJA);
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}" + "] El tipo de transaccion es: " + ISunatConnectorConfig.FE_TIPO_TRANS_BAJA, docUUID);
                    FEProcessHandler processHandler = FEProcessHandler.newInstance(docUUID);
                    if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de BAJA de una BOLETA.", docUUID);
                        transactionResponse = processHandler.transactionBoletaDocument(transaction, IUBLConfig.DOC_BOLETA_CODE, extractor, Sociedad, configuration, connection);
                    } else if ("C".equalsIgnoreCase(transaction.getFEEstado()) ) {
                        System.out.println("Entro por la parte de Corregido ");
                        transactionResponse = processHandler.transactionInvoiceDocument(transaction, IUBLConfig.DOC_INVOICE_CODE, extractor, Sociedad, configuration,connection);
                        TransaccionBL.MarcarEnviado(transaction);
                    }else if( "G".equalsIgnoreCase(transaction.getFEEstado())){
                        System.out.println("Entro por la parte de baja Corregido estado G");
                        transactionResponse = processHandler.transactionVoidedDocument(transaction, transaction.getDOCCodigo(), configuration, connection);

                    } else {
                        if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {
                            System.out.println("Entro por la parte de retencion");
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genera la baja de un documento tipo: {1}", new Object[]{docUUID, transaction.getDOCCodigo()});
                            transactionResponse = processHandler.transactionVoidedDocumentRetention(transaction, transaction.getDOCCodigo(), configuration, connection);
                        } else {
                            if (transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {
                                System.out.println("Entro por la parte de percepcion");
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genera la baja de un documento tipo: {1}", new Object[]{docUUID, transaction.getDOCCodigo()});
                                transactionResponse = processHandler.transactionVoidedDocumentPerception(transaction, transaction.getDOCCodigo(), configuration, connection);
                            } else {
                                System.out.println("Entro por la parte de baja normal.");
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genera la baja de un documento tipo: {1}", new Object[]{docUUID, transaction.getDOCCodigo()});
                                transactionResponse = processHandler.transactionVoidedDocument(transaction, transaction.getDOCCodigo(), configuration, connection);
                            }
                        }
                    }

                } else {
                    logger.error("EnviarTransaccion() [" + docUUID + "] ERROR: " + IVenturaError.ERROR_451.getMessage() + " FE_TipoTrans: " + transaction.getFETipoTrans());
                }
            } else {
                logger.error("EnviarTransaccion() [" + docUUID + "] ERROR: " + IVenturaError.ERROR_452.getMessage());
                TransaccionBL.MarcarEnviado(transaction);
                throw new ValidationException(IVenturaError.ERROR_452.getMessage());
            }
        } catch (ConectionSunatException | SoapFaultException e) {
//            TransaccionBL.ReenviarTransaccion(transaction);
            TransaccionBL.Eliminar(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            //actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad);
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException - ERROR: " + e.getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ConfigurationException - ERROR: {1}", new Object[]{transaction.getFEId(), e.getMessage()});

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, IVenturaError.ERROR_2.getId(), mensaje, "Sunat");
        } catch (ConfigurationException e) {
            TransaccionBL.Eliminar(transaction);
            transaction.setFEEstado("I");
            TransaccionBL.Actualizar(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
            System.out.println("Error numero 2");
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException - ERROR: " + e.getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ConfigurationException - ERROR: {1}", new Object[]{transaction.getFEId(), e.getMessage()});

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, IVenturaError.ERROR_2.getId(), e.getMessage(), "VS");
        } catch (UBLDocumentException e) {
            TransaccionBL.Eliminar(transaction);
//            TransaccionBL.ReenviarTransaccion(transaction);
            e.printStackTrace();
            transaction.setFEEstado("I");
            TransaccionBL.Actualizar(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
            System.out.println("Error numero 3");
            logger.error("EnviarTransaccion() [" + docUUID + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] UBLDocumentException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo UBLDocumentException - ERROR: {1}-{2}", new Object[]{transaction.getFEId(), e.getError().getId(), e.getError().getMessage()});

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (ValidationException e) {
            TransaccionBL.Eliminar(transaction);
            e.printStackTrace();
            transaction.setFEEstado("I");
            TransaccionBL.Actualizar(transaction);
//            TransaccionBL.ReenviarTransaccion(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
            System.out.println("Error numero 4");
            logger.error("EnviarTransaccion() [" + docUUID + "] ValidationException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] ValidationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ValidationException - ERROR: {1}-{2}", new Object[]{transaction.getFEId(), e.getError().getId(), e.getError().getMessage()});

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (SignerDocumentException e) {
            TransaccionBL.Eliminar(transaction);
            e.printStackTrace();
            transaction.setFEEstado("I");
            TransaccionBL.Actualizar(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
//            TransaccionBL.ReenviarTransaccion(transaction);
            System.out.println("Error numero 5");
            logger.error("EnviarTransaccion() [" + docUUID + "] SignerDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] SignerDocumentException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo SignerDocumentException - ERROR: {1}-{2}", new Object[]{transaction.getFEId(), e.getError().getId(), e.getError().getMessage()});

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (SunatGenericException e) {
            TransaccionBL.Eliminar(transaction);
            e.printStackTrace();
            transaction.setFEEstado("I");
            TransaccionBL.Actualizar(transaction);
//            TransaccionBL.ReenviarTransaccion(transaction);
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = Optional.ofNullable(optional.orElse(e.getMessage())).orElse("No se pudo obtener la respuesta del servidor.");
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
            System.out.println("Error numero 6");
            logger.error("EnviarTransaccion() [" + docUUID + "] SunatGenericException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] SunatGenericException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo SunatGenericException - ERROR: {1}-{2}", new Object[]{transaction.getFEId(), e.getError().getId(), e.getError().getMessage()});

            logger.error("EnviarTransaccion() [" + docUUID + "] Se paso al metodo enviarTransaccion");

            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, e.getError().getId(), e.getError().getMessage(), "Ex");
        } catch (Exception e) {
            TransaccionBL.Eliminar(transaction);
            logger.error(e.getLocalizedMessage());
            int rqtValue = transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_EMISION) ? TransaccionRespuesta.RQT_EMITIDO_EXCEPTION : TransaccionRespuesta.RQT_BAJA_EXCEPCION;
            transactionResponse = exceptionResponse(rqtValue, transaction, 999, e.getMessage(), "Ex");
            if (e instanceof Sunat1033ResponseException) {
                transactionResponse.setCodigoWS(1033);
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION_1033);
            }
            Optional<String> optional = Optional.ofNullable(e.getLocalizedMessage());
            String mensaje = optional.orElse(e.getMessage());
            actualizarEstadoMensajeTransaccion(transaction, mensaje, "E", Sociedad, connection);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-EnviarTransaccion()");
        }
        return transactionResponse;
    } //EnviarTransaccion

    private void actualizarEstadoMensajeTransaccion(Transaccion transaction, String respuesta, String estado, ICompany sociedad, Connection connection) throws VenturaExcepcion {
        respuesta = Optional.ofNullable(respuesta).orElse("No se pudo obtener la respuesta");
        try {
            DocumentoDAO.ActualizarMensaje(transaction, respuesta, estado, sociedad, connection);
        } catch (SBOCOMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este metodo genera el objeto TransaccionRespuesta de la excepcion
     * capturada en los metodos principales.
     *
     * @param rqtCode        Error devuelto segun el formato de VENTURA SOFTWARE S.A.C.
     * @param transactionObj Objeto Transaccion o TransaccionResumen extraido de
     *                       los metodos principales.
     * @param errorCode      Codigo de error extraido de la transaccion.
     * @return Retorna el objeto TransaccionRespuesta de la excepcion capturada
     * en los metodos principales.
     * <p>
     * NOTA: Metodos principales que utilizan metodo: - EnviarTransaccion -
     * EnviarConsulta - EnviarResumenDiario - ConsultarResumenDiario
     */
    private TransaccionRespuesta exceptionResponse(int rqtCode, Object transactionObj, int errorCode, String codErrorSunat, String emitioError) {
        String cod = codErrorSunat.length()==3? "0".concat(codErrorSunat):codErrorSunat;
        CodErrorSunat objCodError = BdCodErrorSunat.findCodErrorSunat(cod);

        TransaccionRespuesta respuesta = new TransaccionRespuesta();
        respuesta.setCodigo(rqtCode);
        respuesta.setCodigoWS(errorCode);

        if(objCodError != null){
            respuesta.setMensaje(cod + " - " + objCodError.getCodDescription());
        }else{
            respuesta.setMensaje(cod + " - " + "No existe codigo de error en la tabla intermedia");
        }
        respuesta.setEstado("E");
        respuesta.setEmitioError(emitioError);







        if (transactionObj instanceof Transaccion) {
            respuesta.setIdentificador(((Transaccion) transactionObj).getDOCId());
            respuesta.setUuid(((Transaccion) transactionObj).getFEId());
        } else if (transactionObj instanceof TransaccionResumen) {
            respuesta.setIdentificador(((TransaccionResumen) transactionObj).getIdTransaccion());
            respuesta.setUuid(((TransaccionResumen) transactionObj).getIdTransaccion());
        }
        return respuesta;
    } //exceptionResponse

    @Override
    public boolean Eco() throws VenturaExcepcion {
        // TODO Auto-generated method stub
        return false;
    }

} //TransaccionWS
