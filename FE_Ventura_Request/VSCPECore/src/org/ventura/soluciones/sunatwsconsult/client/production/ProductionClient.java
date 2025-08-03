package org.ventura.soluciones.sunatwsconsult.client.production;

import org.apache.log4j.Logger;
import pe.gob.sunat.service.consult.StatusResponse;
import pe.gob.sunat.service.consult.SunatSoapConsultInterceptor;

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


    @Override
    public StatusResponse getStatus(String rucComprobante, String tipoComprobante, String serieComprobante, Integer numeroComprobante) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatus() [PRODUCTION] rucComprobante: " + rucComprobante + " tipoComprobante: " + tipoComprobante + " serieComprobante: " + serieComprobante + " numeroComprobante: " + numeroComprobante);
        }

        StatusResponse response = getSecurityPort().getStatus(rucComprobante, tipoComprobante, serieComprobante, numeroComprobante);

        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() [PRODUCTION]");
        }
        return response;
    } //getStatus

    @Override
    public StatusResponse getStatusCDR(String rucComprobante, String tipoComprobante, String serieComprobante, Integer numeroComprobante) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatusCdr() [PRODUCTION] rucComprobante: " + rucComprobante + " tipoComprobante: " + tipoComprobante + " serieComprobante: " + serieComprobante + " numeroComprobante: " + numeroComprobante);
        }
        SunatSoapConsultInterceptor soapConsultInterceptor = new SunatSoapConsultInterceptor(consumer);
        pe.gob.sunat.service.consult.BillConsultService billService = new pe.gob.sunat.service.consult.BillConsultService(getLocationForConsultService());
        pe.gob.sunat.service.consult.HeaderHandlerResolver handlerResolver = new pe.gob.sunat.service.consult.HeaderHandlerResolver();
        handlerResolver.addHandlers(soapConsultInterceptor);
        billService.setHandlerResolver(handlerResolver);
        pe.gob.sunat.service.consult.BillService consultServicePort = billService.getBillConsultServicePort();
        pe.gob.sunat.service.consult.StatusResponse statusResponse = consultServicePort.getStatusCdr(rucComprobante, tipoComprobante, serieComprobante, numeroComprobante);
        return new StatusResponse(statusResponse.getContent(), statusResponse.getStatusCode(), statusResponse.getStatusMessage());
    } //getStatusCdr

    private URL getLocationForConsultService() throws MalformedURLException {
        String urlWebService = "https://e-factura.sunat.gob.pe/ol-it-wsconscpegem/billConsultService?wsdl";
        return new URL(urlWebService);
    }
} //ProductionClient
