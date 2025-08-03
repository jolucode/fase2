package org.ventura.cpe.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.ventura.cpe.core.config.ConfiguracionAplicacion;
import org.ventura.cpe.core.config.GlobalConfiguracionAplicacion;
import org.ventura.cpe.core.config.XMLConfiguracion;
import org.ventura.cpe.core.exception.VenturaExcepcion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class AppEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            StringBuilder sRutaConfigReal = new StringBuilder(System.getProperty("user.dir"));
            String[] sRutaConfigGeneral = sRutaConfigReal.toString().split("[\\\\/]", -1);
            sRutaConfigReal = new StringBuilder();
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal.append(sRutaConfigGeneral[i]).append(File.separator);
            }
            sRutaConfigReal.append("Config.xml");
            File file = new File(sRutaConfigReal.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLConfiguracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            XMLConfiguracion xmlConfiguracion = (XMLConfiguracion) jaxbUnmarshaller.unmarshal(file);
            ConfiguracionAplicacion configuracionAplicacion = new ConfiguracionAplicacion(xmlConfiguracion);
            GlobalConfiguracionAplicacion globalConfiguracion = new GlobalConfiguracionAplicacion(configuracionAplicacion);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            File config = new File("config.yml");
            if (!config.exists()) {
                config.createNewFile();
            }
            Resource path = new FileSystemResource(config);
            mapper.writeValue(path.getFile(), globalConfiguracion);
            PropertySource<?> propertySource = loadYaml(path);
            environment.getPropertySources().addLast(propertySource);
        } catch (JAXBException | IOException | VenturaExcepcion e) {
            e.printStackTrace();
        }
    }

    private PropertySource<?> loadYaml(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return this.loader.load("custom-resource", path).get(0);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load yaml configuration from " + path, ex);
        }
    }
}
