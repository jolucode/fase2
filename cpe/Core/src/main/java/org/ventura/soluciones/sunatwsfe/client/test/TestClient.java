package org.ventura.soluciones.sunatwsfe.client.test;

import org.apache.log4j.Logger;
import pe.gob.sunat.serviceFE.StatusResponse;

import javax.activation.DataHandler;

public class TestClient extends TestWSClient {

    private static final Logger logger = Logger.getLogger(TestClient.class);

    @Override
    public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() [TEST] fileName: " + fileName + " contentFile: " + contentFile);
        }

        byte[] cdrResponse = getSecurityPort().sendBill(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [TEST]");
        }
        return cdrResponse;
    }

    @Override
    public String sendSummary(String fileName, DataHandler contentFile)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [TEST] fileName: " + fileName + " contentFile: " + contentFile);
        }

        String response = getSecurityPort().sendSummary(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() [TEST]");
        }
        return response;
    }

    @Override
    public String sendPack(String fileName, DataHandler contentFile)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendPack() [TEST] fileName: " + fileName + " contentFile: " + contentFile);
        }

        String response = getSecurityPort().sendPack(fileName, contentFile);

        if (logger.isDebugEnabled()) {
            logger.debug("-sendPack() [TEST]");
        }
        return response;
    }

    @Override
    public StatusResponse getStatus(String ticket) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatus() [TEST] ticket: " + ticket);
        }

        StatusResponse response = getSecurityPort().getStatus(ticket);

        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() [TEST]");
        }
        return response;
    }

}
