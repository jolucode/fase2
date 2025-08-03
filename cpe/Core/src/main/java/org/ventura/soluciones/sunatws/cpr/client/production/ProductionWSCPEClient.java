package org.ventura.soluciones.sunatws.cpr.client.production;

import org.apache.log4j.Logger;
import org.ventura.soluciones.sunatws.cpr.client.production.service.BillService;
import org.ventura.soluciones.sunatws.cpr.client.production.service.BillService_Service;
import org.ventura.soluciones.sunatws.cpr.consumer.Consumer;
import org.ventura.soluciones.sunatws.cpr.factory.ISunatClient;
import org.ventura.soluciones.sunatws.cpr.security.HeaderHandlerResolver;

import javax.xml.bind.JAXBException;

public abstract class ProductionWSCPEClient implements ISunatClient {


    private static final Logger logger = Logger.getLogger(ProductionWSCPEClient.class);

    protected Consumer consumer;

    protected boolean printOption;

    /**
     * Este metodo define el objeto Consumidor que contiene los datos del emisor
     * electronico (RUC, Usuario secundario SOL y Clave secundaria SOL).
     */
    @Override
    public void setConsumer(Consumer consumer) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-setConsumer()");
        }
        this.consumer = consumer;
    } //setConsumer

    /**
     * Este metodo habilita o desabilita la opcion de mostrar la trama
     * SOAP (Consulta y respuesta).
     */
    @Override
    public void printSOAP(boolean printOption) {
        this.printOption = true;
        if (logger.isDebugEnabled()) {
            logger.debug("PrintSoap" + this.printOption);
        }
    } //printSOAP

    /**
     * Este metodo implementa la seguridad establecida por Sunat, para
     * el envio de documentos electronicos.
     *
     * @return Retorna el objeto BillService de la opcion de 'Test Client'.
     * @throws JAXBException
     */

    protected BillService getSecurityPort() throws JAXBException {
        BillService_Service service = new BillService_Service();

        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(consumer);
        handlerResolver.setPrintSOAP(printOption);

        service.setHandlerResolver(handlerResolver);
        return service.getBillServicePort();
    } //getSecurityPort

}
