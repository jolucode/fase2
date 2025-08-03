package org.tempuri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import handlers.SecurityResponseHeaderHandler;
import org.apache.log4j.Logger;
import pe.gob.sunat.service.conose.StatusCdrResponse;
import pe.gob.sunat.service.conose.StatusResponse;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.Handler;
import java.util.List;

import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import javax.activation.DataHandler;


public class CONOSEClient extends CONOSEBasicClient {

    private static final String SCHEMA = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String SCHEMA_PREFIX = "wsse";

    private IbillService conoseClient;

    private final Logger logger = Logger.getLogger(CONOSEClient.class);

    private Consumer consumer;

    public CONOSEClient(String clientType) { super(clientType);  } //OSEClient

    @Override
    public String sendSummary(String fileName, byte[] contentFile, String partyType) throws IbillServiceSendSummaryMessageFaultMessage {
        long initTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() fileName[" + fileName + "], contentFile[" + contentFile + "]");
        }

        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        String response= null;

        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPHeader header = soapEnvelope.getHeader();

            // Add the security SOAP header element
            SOAPHeaderElement security = header.addHeaderElement(new QName(SCHEMA, "Security", SCHEMA_PREFIX));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", SCHEMA_PREFIX);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", SCHEMA_PREFIX);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", SCHEMA_PREFIX);

            String usuarioServicio = super.getConsumer().getUsername();
            String contrasenaServicio = super.getConsumer().getPassword();

            usernameElement.setTextContent(usuarioServicio);// "20510910517MODDATOS"
            passwordElement.setTextContent(contrasenaServicio); // "moddatos"

            WSBindingProvider wbp = (WSBindingProvider) port;

            wbp.setOutboundHeaders(Headers.create(security));

            List<Handler> handlerChain = wbp.getBinding().getHandlerChain();
            handlerChain.add(new SecurityResponseHeaderHandler());
            wbp.getBinding().setHandlerChain(handlerChain);

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        // AQUI ****
        response = port.sendSummary(fileName, contentFile,"");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        /*try {
            response = port.sendSummary(fileName, contentFile,"");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println(gson.toJson(response));
        } catch (IbillServiceSendSummaryMessageFaultMessage e) {
            e.printStackTrace();
        }*/

        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() TIME[" + (System.currentTimeMillis() - initTime) + "]");
        }
        return response;
    }

    @Override
    public StatusResponse getStatus(String ticket) throws IbillServiceGetStatusMessageFaultMessage {
        StatusResponse response = null;
        long initTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("+getStatus() ticket[" + ticket + "]");
        }
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPHeader header = soapEnvelope.getHeader();

            // Add the security SOAP header element
            SOAPHeaderElement security = header.addHeaderElement(new QName(SCHEMA, "Security", SCHEMA_PREFIX));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", SCHEMA_PREFIX);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", SCHEMA_PREFIX);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", SCHEMA_PREFIX);

            String usuarioServicio = super.getConsumer().getUsername();
            String contrasenaServicio = super.getConsumer().getPassword();

            usernameElement.setTextContent(usuarioServicio);// "20510910517MODDATOS"
            passwordElement.setTextContent(contrasenaServicio); // "moddatos"

            WSBindingProvider wbp = (WSBindingProvider) port;

            wbp.setOutboundHeaders(Headers.create(security));

            List<Handler> handlerChain = wbp.getBinding().getHandlerChain();
            handlerChain.add(new SecurityResponseHeaderHandler());
            wbp.getBinding().setHandlerChain(handlerChain);

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        response = port.getStatus(ticket);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        /*try {
            response = port.getStatus(ticket);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println(gson.toJson(response));
        } catch (IbillServiceGetStatusMessageFaultMessage e) {
            e.printStackTrace();
        }*/

        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() TIME[" + (System.currentTimeMillis() - initTime) + "]");
        }

        return  response;
    }


    @Override
    public String sendPack(String fileName, byte[] contentFile) throws IbillServiceSendPackMessageFaultMessage {
        long initTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() fileName[" + fileName + "], contentFile[" + contentFile + "]");
        }

        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        String response= null;

        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPHeader header = soapEnvelope.getHeader();

            // Add the security SOAP header element
            SOAPHeaderElement security = header.addHeaderElement(new QName(SCHEMA, "Security", SCHEMA_PREFIX));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", SCHEMA_PREFIX);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", SCHEMA_PREFIX);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", SCHEMA_PREFIX);

            String usuarioServicio = super.getConsumer().getUsername();
            String contrasenaServicio = super.getConsumer().getPassword();

            usernameElement.setTextContent(usuarioServicio);// "20510910517MODDATOS"
            passwordElement.setTextContent(contrasenaServicio); // "moddatos"

            WSBindingProvider wbp = (WSBindingProvider) port;

            wbp.setOutboundHeaders(Headers.create(security));

            List<Handler> handlerChain = wbp.getBinding().getHandlerChain();
            handlerChain.add(new SecurityResponseHeaderHandler());
            wbp.getBinding().setHandlerChain(handlerChain);

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        response = port.sendPack(fileName, contentFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        if (logger.isDebugEnabled()) {
            logger.debug("-sendPack() TIME[" + (System.currentTimeMillis() - initTime) + "]");
        }
        return response;
    }

    @Override
    public StatusCdrResponse getStatusCdr(String rucComprobante, String tipoComprobante, String serieComprobante, int numeroComprobante) throws IbillServiceGetStatusCdrMessageFaultMessage {
        StatusCdrResponse response = null;
        long initTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("+getStatusCDR() rucComprobante[" + rucComprobante + "], tipoComprobante[" + tipoComprobante + "], serieComprobante[" + serieComprobante + "], numeroComprobante[" + numeroComprobante + "]");
        }
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPHeader header = soapEnvelope.getHeader();

            // Add the security SOAP header element
            SOAPHeaderElement security = header.addHeaderElement(new QName(SCHEMA, "Security", SCHEMA_PREFIX));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", SCHEMA_PREFIX);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", SCHEMA_PREFIX);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", SCHEMA_PREFIX);

            String usuarioServicio = super.getConsumer().getUsername();
            String contrasenaServicio = super.getConsumer().getPassword();

            usernameElement.setTextContent(usuarioServicio);// "20510910517MODDATOS"
            passwordElement.setTextContent(contrasenaServicio); // "moddatos"

            WSBindingProvider wbp = (WSBindingProvider) port;

            wbp.setOutboundHeaders(Headers.create(security));

            List<Handler> handlerChain = wbp.getBinding().getHandlerChain();
            handlerChain.add(new SecurityResponseHeaderHandler());
            wbp.getBinding().setHandlerChain(handlerChain);

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        response = port.getStatusCdr(rucComprobante, tipoComprobante, serieComprobante, numeroComprobante);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //System.out.println(gson.toJson(response));


        if (logger.isDebugEnabled()) {
            logger.debug("-getStatusCdr() TIME[" + (System.currentTimeMillis() - initTime) + "]");
        }

        return  response;
    }

    @Override
    public byte[] sendBill(String fileName, byte[] contentFile) throws IbillServiceSendBillMessageFaultMessage {
        long initTime = System.currentTimeMillis();

        byte[] response= null;

        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() fileName[" + fileName + "], contentFile[" + contentFile + "]");
        }

        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        BillService billService = new BillService();
        IbillService port = billService.getBillServicePort();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

            SOAPHeader header = soapEnvelope.getHeader();

            // Add the security SOAP header element
            SOAPHeaderElement security = header.addHeaderElement(new QName(SCHEMA, "Security", SCHEMA_PREFIX));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", SCHEMA_PREFIX);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", SCHEMA_PREFIX);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", SCHEMA_PREFIX);

            String usuarioServicio = super.getConsumer().getUsername();
            String contrasenaServicio = super.getConsumer().getPassword();

            usernameElement.setTextContent(usuarioServicio);// "20510910517MODDATOS"
            passwordElement.setTextContent(contrasenaServicio); // "moddatos"

            WSBindingProvider wbp = (WSBindingProvider) port;

            wbp.setOutboundHeaders(Headers.create(security));

            List<Handler> handlerChain = wbp.getBinding().getHandlerChain();
            handlerChain.add(new SecurityResponseHeaderHandler());
            wbp.getBinding().setHandlerChain(handlerChain);

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        response = port.sendBill(fileName, contentFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //System.out.println(gson.toJson(response));


        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() TIME[" + (System.currentTimeMillis() - initTime) + "]");
        }

        return response;
    } //sendBill
} //CONOSEClient



