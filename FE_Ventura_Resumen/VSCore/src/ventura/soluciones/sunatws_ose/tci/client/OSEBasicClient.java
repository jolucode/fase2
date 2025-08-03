package ventura.soluciones.sunatws_ose.tci.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ventura.utilidades.entidades.VariablesGlobales;

import ventura.soluciones.sunatws_ose.tci.client.service.BillService;
import ventura.soluciones.sunatws_ose.tci.client.service.BillService_Service;
import ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import ventura.soluciones.sunatws_ose.tci.security.HeaderHandlerResolver;

public abstract class OSEBasicClient implements IOSEClient {

    private final Logger logger = Logger.getLogger(OSEBasicClient.class);

    private Consumer consumer;

    private boolean printOption;

    private String wsdlLocation;

    private String clientType;

    protected OSEBasicClient(String clientType) {
        this.clientType = clientType;
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
    } //OSEBasicClient

    @Override
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    } //setConsumer

    @Override
    public void printSOAP(boolean printOption) {
        this.printOption = printOption;
    } //printSOAP

    protected BillService getSecurityPort() throws JAXBException, MalformedURLException, Exception {
        BillService_Service service;
        if (StringUtils.isBlank(this.wsdlLocation)) {
            if (Objects.equals(clientType, TEST_CLIENT)) {
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
            URL url = new URL(ventura.soluciones.sunatws_ose.tci.client.service.BillService_Service.class.getResource("."), this.wsdlLocation);
            service = new BillService_Service(url);
        } else {
            URL url = new URL(ventura.soluciones.sunatws_ose.tci.client.service.BillService_Service.class.getResource("."), this.wsdlLocation);
            if (logger.isDebugEnabled()) {
                logger.debug("getSecurityPort() WSDL[" + this.wsdlLocation + "] para BillService_Service.");
            }
            service = new BillService_Service(url);
        }
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(consumer);
        handlerResolver.setPrintSOAP(printOption);
        service.setHandlerResolver(handlerResolver);
        return service.getBillServicePort();
    } //getSecurityPort

} //OSEBasicClient
