package org.ventura.soluciones.sunatws.cpr.client.production;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.ArrayList;
import java.util.List;

public class HeaderHandlerResolver implements HandlerResolver {

    private final List<Handler> handlers = new ArrayList<>();

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        return handlers;
    }

    public void addHandlers(Handler handler) {
        this.handlers.add(handler);
    }
}
