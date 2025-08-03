/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.loader;

import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.main.Interfaz;
import org.ventura.utilidades.encriptacion.Criptor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.logging.Level;

import static org.ventura.cpe.main.Interfaz.*;

/**
 *
 * @author VSUser
 */
public class loader {

    public static boolean DatosWS(Document doc, XPath xPath) {
        Node nodo;
        String p;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSLocation", doc.getDocumentElement(), XPathConstants.NODE);
            WS_URL = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSUsuario", doc.getDocumentElement(), XPathConstants.NODE);
            WS_USER = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSClave", doc.getDocumentElement(), XPathConstants.NODE);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            WS_PASS = p;
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSTiempoEsperaPublic", doc.getDocumentElement(), XPathConstants.NODE);
            WS_TIEMPOESPERA = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSIntervaloRepeticionPublic", doc.getDocumentElement(), XPathConstants.NODE);
            WS_INTERVALOREPETICION = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSTiempoPublicacionPublic", doc.getDocumentElement(), XPathConstants.NODE);
            WS_TIEMPOPUBLICACION = nodo.getTextContent();
        } catch (XPathExpressionException | DOMException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            return false;
        }
        return true;
    }

    public static boolean DatosPROXY(Document doc, XPath xPath) throws IOException {
        Node nodo;

        try {
            boolean usar, auten;
            String proxyHost, proxyPort;

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/UsarProxy", doc.getDocumentElement(), XPathConstants.NODE);
            usar = Interfaz.USEPROXY = Boolean.parseBoolean(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Servidor", doc.getDocumentElement(), XPathConstants.NODE);
            proxyHost = Interfaz.HOSTNAME = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            proxyPort = Interfaz.HOSTPORT = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/RequAuth", doc.getDocumentElement(), XPathConstants.NODE);
            auten = Boolean.parseBoolean(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/User", doc.getDocumentElement(), XPathConstants.NODE);
            final String authUser = Interfaz.USERPROXY = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Pass", doc.getDocumentElement(), XPathConstants.NODE);
            String authPassword = nodo.getTextContent();
            final String authPassword1 = Interfaz.PASSPROXY = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(authPassword) : authPassword;

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Repositorio(Document doc, XPath xPath) throws IOException {
        Node nodo;

        try {
            boolean usar, auten;
            String password, user, baseDatos, tipoServidor;

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Password", doc.getDocumentElement(), XPathConstants.NODE);
            String passBd = nodo.getTextContent();
            password = Interfaz.PASSWORD = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(passBd) : passBd;
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/BaseDatos", doc.getDocumentElement(), XPathConstants.NODE);
            baseDatos = Interfaz.HOSTNAME = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            tipoServidor = TIPOSERVIDOR = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/User", doc.getDocumentElement(), XPathConstants.NODE);
            user = nodo.getTextContent();

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Directorio(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Directorio
            String p = null;
            nodo = (Node) xPath.evaluate("/Configuracion/Directorio/Adjuntos", doc.getDocumentElement(), XPathConstants.NODE);
            p = nodo.getTextContent();
            //p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            ADJUNTOS = p;
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

}
