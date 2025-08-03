package org.ventura.cpe.core.util;

import lombok.extern.slf4j.Slf4j;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.ws.response.TransaccionRespuesta;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class RespuestaSunat {

    public static int AmbienteSunat;

    public static TransaccionRespuesta.Sunat SetRespuestaSUNAT(byte[] cdr, int ambienteTrabajo, String TipoDocumento, String sunatType) throws VenturaExcepcion {
        try {
            if (cdr == null) {
                return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(cdr);
            ZipInputStream zis = new ZipInputStream(bais);
            ZipEntry entry = zis.getNextEntry();
            byte[] xml = null;
            if (entry != null) {
                while (entry != null) {
                    if (!entry.isDirectory()) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] bytesIn = new byte['?'];
                        int read = 0;
                        while ((read = zis.read(bytesIn)) != -1) {
                            baos.write(bytesIn, 0, read);
                        }
                        baos.close();
                        xml = baos.toByteArray();
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
                zis.close();
            } else {
                zis.close();
                log.error("Extraer respuesta SUNAT: El Cdr devuelvo está dañado");
                return null;
            }

            if (xml == null) {
                return null;
            }
            TransaccionRespuesta.Sunat sunat = new TransaccionRespuesta.Sunat();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(xml));
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if (prefix == null) {
                        throw new NullPointerException("Null prefix");
                    }
                    if ("cac".equals(prefix)) {
                        return "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";
                    }
                    if ("cbc".equals(prefix)) {
                        return "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
                    }
                    if ("ext".equals(prefix)) {
                        return "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2";
                    }
                    if ("xml".equals(prefix)) {
                        return "http://www.w3.org/XML/1998/namespace";
                    }
                    if ("xmlns".equals(prefix)) {
                        return "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2";
                    }
                    if ("ar".equals(prefix)) {
                        return "urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2";
                    }
                    if ("soap".equals(prefix)) {
                        return "http://schemas.xmlsoap.org/soap/envelope/";
                    }
                    if ("ds".equals(prefix)) {
                        return "http://www.w3.org/2000/09/xmldsig#";
                    }
                    return "";
                }

                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException();
                }

                public Iterator getPrefixes(String uri) {
                    throw new UnsupportedOperationException();
                }
            });
            Node nodo = null;
            if (sunatType.equalsIgnoreCase("OSE")) {
                nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:DocumentReference/cbc:ID", doc.getDocumentElement(), XPathConstants.NODE);
                sunat.setId(nodo.getTextContent());
                nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ResponseCode", doc.getDocumentElement(), XPathConstants.NODE);
                sunat.setCodigo(Integer.parseInt(nodo.getTextContent()));
                nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:Description", doc.getDocumentElement(), XPathConstants.NODE);
                sunat.setMensaje(nodo.getTextContent());
            } else {
                if (ambienteTrabajo == 1) {
                    if ((TipoDocumento.equalsIgnoreCase("40")) || (TipoDocumento.equalsIgnoreCase("20"))) {
                        nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ReferenceID", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setId(nodo.getTextContent());
                        nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ResponseCode", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setCodigo(Integer.parseInt(nodo.getTextContent()));
                        nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:Description", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setMensaje(nodo.getTextContent());
                    } else {
                        nodo = (Node) xPath.evaluate("/*[name()='ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ReferenceID", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setId(nodo.getTextContent());
                        nodo = (Node) xPath.evaluate("/*[name()='ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ResponseCode", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setCodigo(Integer.parseInt(nodo.getTextContent()));
                        nodo = (Node) xPath.evaluate("/*[name()='ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:Description", doc.getDocumentElement(), XPathConstants.NODE);
                        sunat.setMensaje(nodo.getTextContent());
                    }
                } else {
                    nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ReferenceID", doc.getDocumentElement(), XPathConstants.NODE);
                    sunat.setId(nodo.getTextContent());
                    nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:ResponseCode", doc.getDocumentElement(), XPathConstants.NODE);
                    sunat.setCodigo(Integer.parseInt(nodo.getTextContent()));
                    nodo = (Node) xPath.evaluate("/*[name()='ar:ApplicationResponse']/cac:DocumentResponse/cac:Response/cbc:Description", doc.getDocumentElement(), XPathConstants.NODE);
                    sunat.setMensaje(nodo.getTextContent());
                }
            }

            return sunat;
        } catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException | NumberFormatException | SAXException ex) {
            log.error("Extraer respuesta SUNAT. {}", ex.getMessage());
        }
        return null;
    }
}
