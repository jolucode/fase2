package org.ventura.soluciones.sunatwsconsult.client.test;

import org.apache.log4j.Logger;
import org.ventura.soluciones.sunatwsconsult.client.production.ProductionWSClient;
import pe.gob.sunat.service.consult.StatusResponse;

/**
 * Esta clase implementa los metodos faltantes de la interfaz ISunatClient, relacionados
 * a los metodos extraidos del servicio web de Produccion.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class TestClient extends ProductionWSClient {

    private static final Logger logger = Logger.getLogger(TestClient.class);


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

        StatusResponse response = getSecurityPort().getStatusCdr(rucComprobante, tipoComprobante, serieComprobante, numeroComprobante);

        if (logger.isDebugEnabled()) {
            logger.debug("-getStatusCdr() [PRODUCTION]");
        }
        return response;
    } //getStatusCdr

} //ProductionClient
