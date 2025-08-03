package org.ventura.soluciones.sunatws_ose.tci.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ventura.soluciones.sunatws_ose.tci.client.service.BillService;
import org.ventura.soluciones.sunatws_ose.tci.client.service.BillService_Service;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.soluciones.sunatws_ose.tci.security.HeaderHandlerResolver;
import org.ventura.utilidades.entidades.VariablesGlobales;

import java.net.URL;

public abstract class OSEBasicClient implements IOSEClient {

    private final Logger logger = Logger.getLogger(OSEBasicClient.class);

    private Consumer consumer;

    private boolean printOption;

    private String wsdlLocation;


    protected OSEBasicClient(String clientType) {
        if (clientType.equalsIgnoreCase(TEST_CLIENT)) {
            if (logger.isDebugEnabled()) {
                logger.debug("OSEBasicClient() Conectando OSE, modo TEST.");
            }
            this.wsdlLocation = VariablesGlobales.rutaOseTestWebservice;
        } else if (clientType.equals(PRODUCTION_CLIENT)) {
            if (logger.isDebugEnabled()) {
                logger.debug("OSEBasicClient() Conectando OSE, modo PRODUCTION.");
            }
            this.wsdlLocation = VariablesGlobales.rutaOseWebservice;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("OSEBasicClient() WSDL: " + this.wsdlLocation);
        }
    }


    @Override
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void printSOAP(boolean printOption) {
        this.printOption = printOption;
    }

    protected BillService getSecurityPort() throws Exception {
        BillService_Service service = StringUtils.isBlank(this.wsdlLocation) ?
                new BillService_Service() :
                new BillService_Service(new URL(new URL(VariablesGlobales.rutaOseWebservice), this.wsdlLocation));
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(consumer);
        handlerResolver.setPrintSOAP(printOption);
        service.setHandlerResolver(handlerResolver);
        return service.getBillServicePort();
    }
}
