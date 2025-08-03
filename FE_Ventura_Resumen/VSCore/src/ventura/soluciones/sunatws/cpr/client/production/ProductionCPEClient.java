package ventura.soluciones.sunatws.cpr.client.production;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;

import pe.gob.sunat.service.cpr.StatusResponse;

public class ProductionCPEClient extends ProductionWSCPEClient {

    private static final Logger logger = Logger.getLogger(ProductionCPEClient.class);

    @Override
    public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() [PRODUCTION] fileName: " + fileName
                    + " contentFile: " + contentFile);
        }
        byte[] cdrResponse = getSecurityPort().sendBill(fileName, contentFile);
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [PRODUCTION]");
        }
        return cdrResponse;
    }

    @Override
    public String sendSummary(String fileName, DataHandler contentFile)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [PRODUCTION] fileName: " + fileName
                    + " contentFile: " + contentFile);
        }

        String response = getSecurityPort().sendSummary(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() [PRODUCTION]");
        }
        return response;
    }

    @Override
    public String sendPack(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendPack() [PRODUCTION] fileName: " + fileName
                    + " contentFile: " + contentFile);
        }

        String response = getSecurityPort().sendPack(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendPack() [PRODUCTION]");
        }
        return response;
    }

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
    }

}
