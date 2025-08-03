package org.ventura.soluciones.sunatws.cpr.client.production;

import org.apache.log4j.Logger;
import pe.gob.sunat.service.cpr.StatusResponse;
import pe.gob.sunat.serviceFE.BillService;
import pe.gob.sunat.serviceFE.BillServiceImpl;

import javax.activation.DataHandler;
import java.net.MalformedURLException;
import java.net.URL;

public class ProductionCPEClient extends ProductionWSCPEClient {

    private static final Logger logger = Logger.getLogger(ProductionCPEClient.class);

    @Override
    public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }
        org.ventura.soluciones.sunatws.cpr.client.production.SunatSoapInterceptor soapInterceptor = new SunatSoapInterceptor(consumer);
        BillServiceImpl billService = new BillServiceImpl(getLocationForService());
        org.ventura.soluciones.sunatws.cpr.client.production.HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
        handlerResolver.addHandlers(soapInterceptor);
        billService.setHandlerResolver(handlerResolver);
        BillService billServicePort = billService.getBillServicePort();
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [PRODUCTION]");
        }
        return billServicePort.sendBill(fileName, contentFile);
    }

    @Override
    public String sendSummary(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }
        org.ventura.soluciones.sunatws.cpr.client.production.SunatSoapInterceptor soapInterceptor = new SunatSoapInterceptor(consumer);
        BillServiceImpl billService = new BillServiceImpl(getLocationForService());
        org.ventura.soluciones.sunatws.cpr.client.production.HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
        handlerResolver.addHandlers(soapInterceptor);
        billService.setHandlerResolver(handlerResolver);
        BillService billServicePort = billService.getBillServicePort();
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [PRODUCTION]");
        }
        return billServicePort.sendSummary(fileName, contentFile);
    }

    @Override
    public String sendPack(String fileName, DataHandler contentFile) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendPack() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);
        }
        org.ventura.soluciones.sunatws.cpr.client.production.SunatSoapInterceptor soapInterceptor = new SunatSoapInterceptor(consumer);
        BillServiceImpl billService = new BillServiceImpl(getLocationForService());
        org.ventura.soluciones.sunatws.cpr.client.production.HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver();
        handlerResolver.addHandlers(soapInterceptor);
        billService.setHandlerResolver(handlerResolver);
        BillService billServicePort = billService.getBillServicePort();
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [PRODUCTION]");
        }
        return billServicePort.sendPack(fileName, contentFile);
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

    private URL getLocationForService() throws MalformedURLException {
        return new URL("https://e-factura.sunat.gob.pe/ol-ti-itemision-otroscpe-gem/billService?wsdl");
    }
}
