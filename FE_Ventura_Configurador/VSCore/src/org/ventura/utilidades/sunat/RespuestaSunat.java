package org.ventura.utilidades.sunat;

import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.w3c.dom.DOMException;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RespuestaSunat {

    public static int AmbienteSunat;

    public static TransaccionRespuesta.Sunat SetRespuestaSUNAT(byte[] cdr, int ambienteTrabajo, String TipoDocumento)
            throws VenturaExcepcion {
        try {
            if (cdr == null) {
                return null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(cdr);
            ZipInputStream zis = new ZipInputStream(bais);
            ZipEntry entry = zis.getNextEntry();
            String name = entry.getName();
            byte[] xml = null;
            if (null != entry) {
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
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Extraer respuesta SUNAT: El Cdr devuelvo está dañado");
                return null;
            }

            if (xml == null) {
                return null;
            }
            String content = new String(xml, StandardCharsets.UTF_8);
            InputSource inputXML = new InputSource(new StringReader(content));
            TransaccionRespuesta.Sunat sunat = new TransaccionRespuesta.Sunat();
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                @Override
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
            String nodo;
            if (ambienteTrabajo == 1) {
                if ((TipoDocumento.equalsIgnoreCase("40")) || (TipoDocumento.equalsIgnoreCase("20"))) {
                    nodo = xPath.evaluate("ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ReferenceID", inputXML);
                    sunat.setId(nodo);
                    inputXML = new InputSource(new StringReader(content));
                    nodo = xPath.evaluate("/ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ResponseCode", inputXML);
                    sunat.setCodigo(Integer.parseInt(nodo));
                    inputXML = new InputSource(new StringReader(content));
                    nodo = xPath.evaluate("/ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:Description", inputXML);
                    sunat.setMensaje(nodo);
                } else {
                    nodo = xPath.evaluate("/ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ReferenceID", inputXML);
                    sunat.setId(nodo);
                    inputXML = new InputSource(new StringReader(content));
                    nodo = xPath.evaluate("/ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ResponseCode", inputXML);
                    sunat.setCodigo(Integer.parseInt(nodo));
                    inputXML = new InputSource(new StringReader(content));
                    nodo = xPath.evaluate("/ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:Description", inputXML);
                    sunat.setMensaje(nodo);
                }
            } else {
                nodo = xPath.evaluate("/ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ReferenceID", inputXML);
                sunat.setId(nodo);
                inputXML = new InputSource(new StringReader(content));
                nodo = xPath.evaluate("/ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:ResponseCode", inputXML);
                sunat.setCodigo(Integer.parseInt(nodo));
                inputXML = new InputSource(new StringReader(content));
                nodo = xPath.evaluate("/ar:ApplicationResponse/cac:DocumentResponse/cac:Response/cbc:Description", inputXML);
                sunat.setMensaje(nodo);
            }
            return sunat;
        } catch (IOException | XPathExpressionException | DOMException | NumberFormatException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Extraer respuesta SUNAT. {0}", new Object[]{ex.getMessage()});
        }
        return null;
    }
}
