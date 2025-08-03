package org.ventura.cpe.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.ventura.soluciones.sunatws.config.ISunatConfig;
import org.ventura.soluciones.sunatws.consumer.Consumer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class SoapInterceptor implements ClientInterceptor {

    private static final String DEFAULT_NS = "xmlns:SOAP-ENV";

    private static final String SOAP_ENV_NAMESPACE = "http://service.sunat.gob.pe";

    private static final String PREFERRED_PREFIX = "S";

    private static final String OTHER_PREFIX = "ns2";

    private static final String HEADER_LOCAL_NAME = "Header";

    private static final String BODY_LOCAL_NAME = "Body";

    private static final String FAULT_LOCAL_NAME = "Fault";

    private final Consumer consumer;

    public SoapInterceptor(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        WebServiceMessage request = messageContext.getRequest();
        alterSoapEnvelope((SaajSoapMessage) request);
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        WebServiceMessage message = messageContext.getResponse();
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) message;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        try {
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapEnvelope.getBody();
            SOAPFault soapFault = soapBody.getFault();
            Detail detail = soapFault.getDetail();
            String errorCode = soapFault.getFaultCode();
            String errorMessage = soapFault.getFaultString();
            String detalleError = Optional.ofNullable(detail).map(Node::getTextContent).orElse(errorMessage);
            log.error(String.format("Error occurred while invoking web service - %s ", detalleError));
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

    }

    private void alterSoapEnvelope(SaajSoapMessage soapResponse) {
        try {
            SOAPMessage soapMessage = soapResponse.getSaajMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            if (envelope.getHeader() != null) {
                envelope.getHeader().detachNode();
            }
            SOAPHeader header = envelope.addHeader();
            SOAPElement security = header.addChildElement(ISunatConfig.WS_SECURITY_HEADER_PARENT_NAME, ISunatConfig.WS_SECURITY_HEADER_PARENT_BASE_PFX, ISunatConfig.WS_SECURITY_HEADER_PARENT_VALUE);
            SOAPElement usernameToken = security.addChildElement(ISunatConfig.WS_SECURITY_SUB_UNTOKEN_NAME, ISunatConfig.WS_SECURITY_HEADER_PARENT_BASE_PFX);
            usernameToken.addAttribute(new QName(ISunatConfig.WS_SECURITY_SUB_UNTOKEN_PFX), ISunatConfig.WS_SECURITY_SUB_UNTOKEN_VALUE);
            SOAPElement username = usernameToken.addChildElement(ISunatConfig.WS_SECURITY_PARAM_USERNAME, ISunatConfig.WS_SECURITY_HEADER_PARENT_BASE_PFX);
            username.addTextNode(this.consumer.getRuc() + this.consumer.getUsername());
            SOAPElement password = usernameToken.addChildElement(ISunatConfig.WS_SECURITY_PARAM_PASSWORD, ISunatConfig.WS_SECURITY_HEADER_PARENT_BASE_PFX);
            password.setAttribute(ISunatConfig.WS_SECURITY_COMMON_ATTRIBUTE_TYPE, ISunatConfig.WS_SECURITY_COMMON_ATRIBUTE_PWD_VALUE);
            password.addTextNode(this.consumer.getPassword());
            envelope.removeNamespaceDeclaration(envelope.getPrefix());
            envelope.addNamespaceDeclaration(PREFERRED_PREFIX, SOAP_ENV_NAMESPACE);
            SOAPBody body = soapMessage.getSOAPBody();
            SOAPFault fault = body.getFault();
            envelope.setPrefix(PREFERRED_PREFIX);
            header.setPrefix(PREFERRED_PREFIX);
            body.setPrefix(PREFERRED_PREFIX);
            NodeList childNodes = body.getChildNodes();
            if (childNodes.getLength() > 0) {
                org.w3c.dom.Node node = childNodes.item(0);
                if (node.getLocalName().equalsIgnoreCase("sendBill")) {
                    int cantidad = node.getAttributes().getLength();
                    for (int i = cantidad - 1; i >= 0; i--) {
                        Node item = node.getAttributes().item(i);
                        node.getAttributes().removeNamedItem(item.getNodeName());
                    }
                    node.setPrefix(OTHER_PREFIX);
                }
            }
            if (fault != null) {
                fault.setPrefix(PREFERRED_PREFIX);
            }
            soapMessage.saveChanges();
            soapMessage.writeTo(System.out);
            System.out.println();
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
    }

    private String obtenerErrorCodeSUNAT(String faultCode) {
        String[] sErrorCodeSplit = faultCode.split("[\\D|\\W]");
        String sErrorCodeSUNAT = "";
        for (String aux : sErrorCodeSplit) {
            if (!aux.isEmpty()) {
                sErrorCodeSUNAT = aux;
            }
        }
        return sErrorCodeSUNAT;
    } //obtenerErrorCodeSUNAT
}
