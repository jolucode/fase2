package org.ventura.cpe.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import javax.xml.soap.*;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AppFaultMessageResolver implements FaultMessageResolver {

    private String lastError = "";

    @Override
    public void resolveFault(WebServiceMessage message) throws IOException {
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) message;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        try {
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapEnvelope.getBody();
            SOAPFault soapFault = soapBody.getFault();
            lastError = soapFault.getFaultString();
            Detail detail = soapFault.getDetail();
            String errorCode = soapFault.getFaultCode();
            String errorMessage = soapFault.getFaultString();
            String detalleError = Optional.ofNullable(detail).map(Node::getTextContent).orElse(errorMessage);
            log.error(String.format("Error occurred while invoking web service - %s ", detalleError));
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    public String getLastError() {
        return lastError;
    }
}
