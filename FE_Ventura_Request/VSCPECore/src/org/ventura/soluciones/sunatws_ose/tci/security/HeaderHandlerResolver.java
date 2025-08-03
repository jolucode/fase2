package org.ventura.soluciones.sunatws_ose.tci.security;

import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.soluciones.sunatws_ose.tci.security.soap.SecuritySOAPHandler;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.ArrayList;
import java.util.List;

public class HeaderHandlerResolver implements HandlerResolver {

    private Consumer consumer;

    private boolean printOption;


    public HeaderHandlerResolver(Consumer consumer) {
        this.consumer = consumer;
    } //HeaderHandlerResolver


    public void setPrintSOAP(boolean printOption) {
        this.printOption = printOption;
    } //setPrintSOAP

    @SuppressWarnings("rawtypes")
    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<Handler>();

        SecuritySOAPHandler securitySOAP = new SecuritySOAPHandler(consumer, printOption);
        handlerChain.add(securitySOAP);

        return handlerChain;
    } //getHandlerChain

} //HeaderHandlerResolver
