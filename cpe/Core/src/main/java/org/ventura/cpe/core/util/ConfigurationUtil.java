package org.ventura.cpe.core.util;

import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.config.XMLConfiguracion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationUtil {

    public boolean saveConfiguration(AppProperties appProperties) {
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            Path rutaPath = Paths.get(sRutaConfigReal, "Config.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLConfiguracion.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            XMLConfiguracion xmlConfiguracion = (XMLConfiguracion) unmarshaller.unmarshal(rutaPath.toFile());
            this.loadConfigurationToProperties(appProperties, xmlConfiguracion);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(xmlConfiguracion, rutaPath.toFile());
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadConfigurationToProperties(AppProperties appProperties, XMLConfiguracion xmlConfiguracion) {
        xmlConfiguracion.getErp().getTipoServidor().setValue(appProperties.getErp().getTipoServidor());
        xmlConfiguracion.getErp().getServidorBD().setValue(appProperties.getErp().getServidorBD());
        xmlConfiguracion.getErp().getServidorLicencias().setValue(appProperties.getErp().getServidorLicencias());
        xmlConfiguracion.getErp().getUser().setValue(appProperties.getErp().getUser());
        xmlConfiguracion.getErp().getErpPass().setValue(appProperties.getErp().getErpPass());
        xmlConfiguracion.getErp().getTipoConector().setValue(appProperties.getErp().getTipoConector());
        xmlConfiguracion.getErp().getBaseDeDatos().setValue(appProperties.getErp().getBaseDeDatos());
        xmlConfiguracion.getErp().getPassword().setValue(appProperties.getErp().getPassword());
        xmlConfiguracion.getErp().getErpRutaArchivos().setValue(appProperties.getErp().getErpRutaArchivos());
        xmlConfiguracion.getRepositorio().getTipoServidor().setValue(appProperties.getRepositorio().getTipoServidor());
        xmlConfiguracion.getRepositorio().getBaseDatos().setValue(appProperties.getRepositorio().getBaseDatos());
        xmlConfiguracion.getRepositorio().getPuerto().setValue(appProperties.getRepositorio().getPuerto());
        xmlConfiguracion.getRepositorio().getServidorBD().setValue(appProperties.getRepositorio().getServidorBD());
        xmlConfiguracion.getRepositorio().getUser().setValue(appProperties.getRepositorio().getUser());
        xmlConfiguracion.getRepositorio().getPassword().setValue(appProperties.getRepositorio().getPassword());
        xmlConfiguracion.getDirectorio().getAdjuntos().setValue(appProperties.getErp().getErpRutaArchivos());
        xmlConfiguracion.getTiempos().getRqInterval().setValue(appProperties.getTiempos().getRqInterval());
        xmlConfiguracion.getTiempos().getRsInterval().setValue(appProperties.getTiempos().getRsInterval());
        xmlConfiguracion.getWebService().getWsLocation().setValue(appProperties.getWebService().getWsLocation());
        xmlConfiguracion.getWebService().getWsUsuario().setValue(appProperties.getWebService().getWsUsuario());
        xmlConfiguracion.getWebService().getWsClave().setValue(appProperties.getWebService().getWsClave());
        xmlConfiguracion.getWebService().getWsTiempoEsperaPublic().setValue(String.valueOf(appProperties.getWebService().getWsTiempoEsperaPublic()));
    }

}
