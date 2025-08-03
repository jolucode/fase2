package org.ventura.soluciones.sunatwsfe.client.production;

import org.apache.log4j.Logger;
import org.ventura.utilidades.entidades.VariablesGlobales;
import pe.gob.sunat.serviceFE.*;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Esta clase implementa los metodos faltantes de la interfaz ISunatClient, relacionados
 * a los metodos extraidos del servicio web de Produccion.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class ProductionClient extends ProductionWSClient {

    private static final Logger logger = Logger.getLogger(ProductionClient.class);

    /**
     * Este metodo envia una 'Factura', 'Boleta', 'Nota de Credito' o 'Nota de Debito' al
     * servicio web de Sunat, retornando un CDR de respuesta.
     */
    @Override
    public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }
        SunatSoapInterceptor soapInterceptor = new SunatSoapInterceptor(consumer);
        BillServiceImpl billService = new BillServiceImpl(getLocationForService());
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
        handlerResolver.addHandlers(soapInterceptor);
        billService.setHandlerResolver(handlerResolver);
        BillService billServicePort = billService.getBillServicePort();
        byte[] bytes = billServicePort.sendBill(fileName, contentFile);
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [PRODUCTION]");
        }
        return bytes;
    }

    /**
     * Este metodo envia una 'Comunicacion de Baja' o 'Resumen Diario' al servicio web de la
     * Sunat, retornando un numero de ticket.
     */
    @Override
    public String sendSummary(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }
        System.out.println(contentFile);
        String response = getSecurityPort().sendSummary(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() [PRODUCTION]");
        }
        return response;
    } //sendSummary

    /**
     * Este metodo envia un documento al servicio web de la Sunat, retornando un numero de
     * ticket.
     */
    @Override
    public String sendPack(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendPack() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }

        String response = getSecurityPort().sendPack(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendPack() [PRODUCTION]");
        }
        return response;
    } //sendPack

    /**
     * Este metodo envia el ticket al servicio web de la Sunat, retornando un objeto que contiene
     * el codigo de respuesta y el CDR de respuesta.
     */
    @Override
    public StatusResponse getStatus(String ticket) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatus() [PRODUCTION] ticket: " + ticket);
        }

        StatusResponse response = getSecurityPort().getStatus(ticket);

        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() [PRODUCTION]");
        }
        return response;
    } //getStatus

    private URL getLocationForService() throws MalformedURLException {
        return new URL(VariablesGlobales.rutaSunatWebservice);
    }

} //ProductionClient
