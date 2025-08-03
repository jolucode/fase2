package org.ventura.wsclient;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.pfeconn.TransaccionPFE;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.exception.SunatGenericException;
import org.ventura.wsclient.exception.ValidationException;
import org.ventura.wsclient.handler.FEProcessHandler;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.SignerDocumentException;
import ventura.soluciones.commons.exception.UBLDocumentException;
import ventura.soluciones.commons.exception.error.IVenturaError;

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
     * Este metodo realiza el proceso de generacion y envio del RESUMEN DIARIO
     * DE BOLETAS, NOTAS DE CREDITO Y DEBITO ELECTRONICAS, recibiendo como
     * parametro de entrada un objeto TransaccionResumen y retornando un objeto
     * TransaccionRespuesta con el ticket de la operacion.
     *
     * @param transaction
     * @return
     * @throws org.ventura.cpe.excepciones.VenturaExcepcion
     * @see org.ventura.cpe.dto.hb.TransaccionResumen
     * @see org.ventura.cpe.dto.TransaccionRespuesta
     */
    @Override
    public TransaccionRespuesta EnviarResumenDiario(TransaccionResumen transaction) throws VenturaExcepcion {
        if (logger.isDebugEnabled()) {
            logger.debug("+EnviarResumenDiario()");
        }
        TransaccionRespuesta transactionResponse = null;
        String docUUID = null;

        try {
            if (Optional.ofNullable(transaction).isPresent()) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Iniciando envio de objeto TransaccionResumen.", transaction.getIdTransaccion());
                //System.out.println("Iniciando envio de objeto TransaccionResumen."+ transaction.getIdTransaccion().substring(0, transaction.getIdTransaccion().length() - 2));
                /*
                 * UUID generado para el seguimiento del proceso
                 */
                docUUID = transaction.getIdTransaccion();
                System.out.println();
                System.out.println();
                System.out.println("Numero de transaccion");
                System.out.println(docUUID);
                System.out.println();
                System.out.println();
//                docUUID = docUUID.substring(1, docUUID.length());
                if (logger.isInfoEnabled()) {
                    logger.info("EnviarResumenDiario() El identificador de la transaccion: " + docUUID);
                }
                FEProcessHandler processHandler = new FEProcessHandler();
                processHandler.setDocUUID(docUUID);
                if (logger.isInfoEnabled()) {
                    logger.info("EnviarResumenDiario() [" + docUUID + "] ################################## Envio de RESUMEN DIARIO.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] La transaccion es de un RESUMEN DIARIO.", docUUID);
                transactionResponse = processHandler.transactionSummaryDocument(transaction);
            } else {
                logger.error("EnviarResumenDiario() [" + docUUID + "] ERROR: " + IVenturaError.ERROR_452.getMessage());
                throw new Exception(IVenturaError.ERROR_452.getMessage());
            }
        } catch (ConectionSunatException | SoapFaultException e) {
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException - ERROR: " + e.getMessage());
            logger.error("EnviarTransaccion() [" + docUUID + "] ConfigurationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ConfigurationException - ERROR: {1}", new Object[]{transaction.getIdTransaccion(), e.getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, IVenturaError.ERROR_2.getId(), e.getMessage(), "Sunat");
        } catch (ConfigurationException e) {
            logger.error("EnviarResumenDiario() [" + docUUID + "] ConfigurationException - ERROR: " + e.getMessage());
            logger.error("EnviarResumenDiario() [" + docUUID + "] ConfigurationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ConfigurationException - ERROR: {1}", new Object[]{docUUID, e.getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, IVenturaError.ERROR_2.getId(), e.getMessage(), "VS");
        } catch (UBLDocumentException e) {
            logger.error("EnviarResumenDiario() [" + docUUID + "] UBLDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarResumenDiario() [" + docUUID + "] UBLDocumentException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo UBLDocumentException - ERROR: {1}-{2}", new Object[]{docUUID, e.getError().getId(), e.getError().getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (ValidationException e) {
            logger.error("EnviarResumenDiario() [" + docUUID + "] ValidationException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarResumenDiario() [" + docUUID + "] ValidationException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo ValidationException - ERROR: {1}-{2}", new Object[]{docUUID, e.getError().getId(), e.getError().getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (SignerDocumentException e) {
            logger.error("EnviarResumenDiario() [" + docUUID + "] SignerDocumentException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarResumenDiario() [" + docUUID + "] SignerDocumentException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo SignerDocumentException - ERROR: {1}-{2}", new Object[]{docUUID, e.getError().getId(), e.getError().getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, e.getError().getId(), e.getError().getMessage(), "VS");
        } catch (SunatGenericException e) {
            logger.error("EnviarResumenDiario() [" + docUUID + "] SunatGenericException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("EnviarResumenDiario() [" + docUUID + "] SunatGenericException -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}] Se produjo una excepcion de tipo SunatGenericException - ERROR: {1}-{2}", new Object[]{docUUID, e.getError().getId(), e.getError().getMessage()});
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, e.getError().getId(), e.getError().getMessage(), "Ex");
        } catch (Exception e) {
            transactionResponse = exceptionResponse(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION, transaction, 0, e.getMessage(), "Ex");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-EnviarResumenDiario()");
        }
        return transactionResponse;
    } //EnviarResumenDiario

    /**
     * Este metodo genera el objeto TransaccionRespuesta de la excepcion
     * capturada en los metodos principales.
     *
     * @param rqtCode        Error devuelto segun el formato de VENTURA SOFTWARE S.A.C.
     * @param transactionObj Objeto Transaccion o TransaccionResumen extraido de
     *                       los metodos principales.
     * @param errorCode      Codigo de error extraido de la transaccion.
     * @param errorMessage   Mensaje de error extraido de la transaccion.
     * @return Retorna el objeto TransaccionRespuesta de la excepcion capturada
     * en los metodos principales.
     * <p>
     * NOTA: Metodos principales que utilizan metodo: - EnviarTransaccion -
     * EnviarConsulta - EnviarResumenDiario - ConsultarResumenDiario
     */
    private TransaccionRespuesta exceptionResponse(int rqtCode, Object transactionObj, int errorCode, String errorMessage, String emitioError) {
        TransaccionRespuesta respuesta = new TransaccionRespuesta();

        respuesta.setCodigo(rqtCode);
        respuesta.setCodigoWS(errorCode);
        respuesta.setMensaje(errorMessage);
        respuesta.setEmitioError(emitioError);

        if (transactionObj instanceof Transaccion) {
            respuesta.setIdentificador(((Transaccion) transactionObj).getDOCId());
            respuesta.setUuid(((Transaccion) transactionObj).getFEId());
        } else if (transactionObj instanceof TransaccionResumen) {
            String docUUID = ((TransaccionResumen) transactionObj).getIdTransaccion().substring(1, ((TransaccionResumen) transactionObj).getIdTransaccion().length());
            respuesta.setIdentificador(docUUID);
            respuesta.setUuid(docUUID);
        }
        return respuesta;
    } //exceptionResponse

    @Override
    public boolean Eco() throws VenturaExcepcion {
        // TODO Auto-generated method stub
        return false;
    }

} //TransaccionWS
