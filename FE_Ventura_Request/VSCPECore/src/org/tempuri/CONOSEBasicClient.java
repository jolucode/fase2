package org.tempuri;


import org.apache.log4j.Logger;
import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.utilidades.entidades.VariablesGlobales;
import static org.ventura.soluciones.sunatws.config.ISunatConfig.PRODUCTION_CLIENT;
import static org.ventura.soluciones.sunatws.config.ISunatConfig.TEST_CLIENT;

public abstract class CONOSEBasicClient implements IbillService {

    private final Logger logger = Logger.getLogger(CONOSEBasicClient.class);

    private Consumer consumer;

    private boolean printOption;

    private String wsdlLocation;

    protected CONOSEBasicClient(String clientType) {
        if (clientType.equalsIgnoreCase(TEST_CLIENT)) {
            if (logger.isDebugEnabled()) {
                logger.debug("CONOSEBasicClient() Conectando CONOSE, modo TEST.");
            }
            this.wsdlLocation = VariablesGlobales.rutaCONOSETestWebservice;
        } else if (clientType.equals(PRODUCTION_CLIENT)) {
            if (logger.isDebugEnabled()) {
                logger.debug("CONOSEBasicClient() Conectando CONOSE, modo PRODUCTION.");
            }
            this.wsdlLocation = VariablesGlobales.rutaCONOSEWebservice;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("CONOSEBasicClient() WSDL: " + this.wsdlLocation);
        }
    }

   @Override
   public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Consumer getConsumer() {
        return consumer;
    }

    /*@Override
    public void printSOAP(boolean printOption) {
        this.printOption = printOption;
    }*/

    protected IbillService getSecurityPort() throws Exception {
        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();
        return port;
    }

}
