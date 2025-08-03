package org.ventura.cpe.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "configuracion")
public class AppProperties {

    private final ConfiguracionAplicacion.GestorTiempo gestorTiempo = new ConfiguracionAplicacion.GestorTiempo();

    private final ConfiguracionAplicacion.Erp erp = new ConfiguracionAplicacion.Erp();

    private final ConfiguracionAplicacion.UsoSunat usoSunat = new ConfiguracionAplicacion.UsoSunat();

    private final ConfiguracionAplicacion.Repositorio repositorio = new ConfiguracionAplicacion.Repositorio();

    private final ConfiguracionAplicacion.Proxy proxy = new ConfiguracionAplicacion.Proxy();

    private final ConfiguracionAplicacion.Directorio directorio = new ConfiguracionAplicacion.Directorio();

    private final ConfiguracionAplicacion.Tiempos tiempos = new ConfiguracionAplicacion.Tiempos();

    private final ConfiguracionAplicacion.WebService webService = new ConfiguracionAplicacion.WebService();

    private final ConfiguracionAplicacion.EmisorElectronico emisorElectronico = new ConfiguracionAplicacion.EmisorElectronico();

    private final ConfiguracionAplicacion.Impresion impresion = new ConfiguracionAplicacion.Impresion();

    private final ConfiguracionAplicacion.CertificadoDigital certificadoDigital = new ConfiguracionAplicacion.CertificadoDigital();

    private final ConfiguracionAplicacion.PFEConnector pfeConnector = new ConfiguracionAplicacion.PFEConnector();

    private final ConfiguracionAplicacion.ResumenDiario resumenDiario = new ConfiguracionAplicacion.ResumenDiario();

    private final ConfiguracionAplicacion.General general = new ConfiguracionAplicacion.General();

    private final ConfiguracionAplicacion.Sunat sunat = new ConfiguracionAplicacion.Sunat();

    private final ConfiguracionAplicacion.VersionUBL versionUBL = new ConfiguracionAplicacion.VersionUBL();

    private final ConfiguracionAplicacion.Pdf pdf = new ConfiguracionAplicacion.Pdf();

    public ConfiguracionAplicacion.GestorTiempo getGestorTiempo() {
        return gestorTiempo;
    }

    public ConfiguracionAplicacion.Erp getErp() {
        return erp;
    }

    public ConfiguracionAplicacion.UsoSunat getUsoSunat() {
        return usoSunat;
    }

    public ConfiguracionAplicacion.Repositorio getRepositorio() {
        return repositorio;
    }

    public ConfiguracionAplicacion.Proxy getProxy() {
        return proxy;
    }

    public ConfiguracionAplicacion.Directorio getDirectorio() {
        return directorio;
    }

    public ConfiguracionAplicacion.Tiempos getTiempos() {
        return tiempos;
    }

    public ConfiguracionAplicacion.WebService getWebService() {
        return webService;
    }

    public ConfiguracionAplicacion.EmisorElectronico getEmisorElectronico() {
        return emisorElectronico;
    }

    public ConfiguracionAplicacion.Impresion getImpresion() {
        return impresion;
    }

    public ConfiguracionAplicacion.CertificadoDigital getCertificadoDigital() {
        return certificadoDigital;
    }

    public ConfiguracionAplicacion.PFEConnector getPfeConnector() {
        return pfeConnector;
    }

    public ConfiguracionAplicacion.ResumenDiario getResumenDiario() {
        return resumenDiario;
    }

    public ConfiguracionAplicacion.General getGeneral() {
        return general;
    }

    public ConfiguracionAplicacion.Sunat getSunat() {
        return sunat;
    }

    public ConfiguracionAplicacion.VersionUBL getVersionUBL() {
        return versionUBL;
    }

    public ConfiguracionAplicacion.Pdf getPdf() {
        return pdf;
    }
}
