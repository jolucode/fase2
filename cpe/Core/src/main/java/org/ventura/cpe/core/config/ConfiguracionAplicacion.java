package org.ventura.cpe.core.config;

import lombok.ToString;
import org.ventura.cpe.core.exception.VenturaExcepcion;

public class ConfiguracionAplicacion {

    private final ConfiguracionAplicacion.GestorTiempo gestorTiempo = new ConfiguracionAplicacion.GestorTiempo();

    private final ConfiguracionAplicacion.Erp erp = new ConfiguracionAplicacion.Erp();

    private final ConfiguracionAplicacion.Jdbc jdbc = new ConfiguracionAplicacion.Jdbc();

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

    public ConfiguracionAplicacion(XMLConfiguracion xmlConfiguracion) throws VenturaExcepcion {
        this.gestorTiempo.setTiempo(xmlConfiguracion.getGestorTiempo().getTiempo().normalValue());
        this.erp.setServidorLicencias(xmlConfiguracion.getErp().getServidorLicencias().normalValue());
        this.erp.setTipoServidor(xmlConfiguracion.getErp().getTipoServidor().normalValue());
        this.erp.setTipoConector(xmlConfiguracion.getErp().getTipoConector().normalValue());
        this.erp.setServidorBD(xmlConfiguracion.getErp().getServidorBD().normalValue());
        this.erp.setBaseDeDatos(xmlConfiguracion.getErp().getBaseDeDatos().normalValue());
        this.erp.setUser(xmlConfiguracion.getErp().getUser().normalValue());
        this.erp.setPassword(xmlConfiguracion.getErp().getPassword().normalValue());
        this.erp.setErpUser(xmlConfiguracion.getErp().getErpUser().normalValue());
        this.erp.setErpPass(xmlConfiguracion.getErp().getErpPass().normalValue());
        this.erp.setErpRutaArchivos(xmlConfiguracion.getErp().getErpRutaArchivos().normalValue());
        this.erp.setErpNombreSociedad(xmlConfiguracion.getErp().getErpNombreSociedad().normalValue());
        /** Harol 17-04-2024 JDBC xml*/
        this.jdbc.setEstadoJdbc(xmlConfiguracion.getJdbc().getEstadoJdbc().normalValue());
        this.jdbc.setServidorBDJdbc(xmlConfiguracion.getJdbc().getServidorBDJdbc().normalValue());
        this.jdbc.setNameUserField(xmlConfiguracion.getJdbc().getNameUserField().normalValue());
        this.jdbc.setSqlServer(xmlConfiguracion.getJdbc().getSqlServer().normalValue());
        /** */
        this.usoSunat.setWs(xmlConfiguracion.getUsoSunat().getWs().normalValue());
        this.usoSunat.setPdf(xmlConfiguracion.getUsoSunat().getPdf().normalValue());
        this.usoSunat.setValidacion(xmlConfiguracion.getUsoSunat().getValidacion().normalValue());
        this.repositorio.setTipoServidor(xmlConfiguracion.getRepositorio().getTipoServidor().normalValue());
        this.repositorio.setServidorBD(xmlConfiguracion.getRepositorio().getServidorBD().normalValue());
        this.repositorio.setPuerto(xmlConfiguracion.getRepositorio().getPuerto().normalValue());
        this.repositorio.setBaseDatos(xmlConfiguracion.getRepositorio().getBaseDatos().normalValue());
        this.repositorio.setUser(xmlConfiguracion.getRepositorio().getUser().normalValue());
        this.repositorio.setPassword(xmlConfiguracion.getRepositorio().getPassword().normalValue());
        this.proxy.setUsarProxy(xmlConfiguracion.getProxy().getUsarProxy().normalValue());
        this.proxy.setServidor(xmlConfiguracion.getProxy().getServidor().normalValue());
        this.proxy.setPuerto(xmlConfiguracion.getProxy().getPuerto().normalValue());
        this.proxy.setRequAuth(xmlConfiguracion.getProxy().getRequAuth().normalValue());
        this.proxy.setUser(xmlConfiguracion.getProxy().getUser().normalValue());
        this.proxy.setPass(xmlConfiguracion.getProxy().getPass().normalValue());
        this.directorio.setAdjuntos(xmlConfiguracion.getDirectorio().getAdjuntos().normalValue());
        this.tiempos.setRqTimeOut(xmlConfiguracion.getTiempos().getRqTimeOut().normalValue());
        this.tiempos.setRqInterval(xmlConfiguracion.getTiempos().getRqInterval().normalValue());
        this.tiempos.setRsTimeOut(xmlConfiguracion.getTiempos().getRsTimeOut().normalValue());
        this.tiempos.setRsInterval(xmlConfiguracion.getTiempos().getRsInterval().normalValue());
        this.webService.setWsUsuario(xmlConfiguracion.getWebService().getWsUsuario().normalValue());
        this.webService.setWsClave(xmlConfiguracion.getWebService().getWsClave().normalValue());
        this.webService.setWsLocation(xmlConfiguracion.getWebService().getWsLocation().normalValue());
        this.webService.setWsTiempoEsperaPublic(Integer.parseInt(xmlConfiguracion.getWebService().getWsTiempoEsperaPublic().normalValue()));
        this.webService.setWsIntervaloRepeticionPublic(Integer.parseInt(xmlConfiguracion.getWebService().getWsIntervaloRepeticionPublic().normalValue()));
        this.webService.setWsTiempoPublicacionPublic(Integer.parseInt(xmlConfiguracion.getWebService().getWsTiempoPublicacionPublic().normalValue()));
        this.emisorElectronico.setRs(xmlConfiguracion.getEmisorElectronico().getRs().normalValue());
        this.emisorElectronico.setSenderLogo(xmlConfiguracion.getEmisorElectronico().getSenderLogo().normalValue());
        this.impresion.setPdf(xmlConfiguracion.getImpresion().getPdf().normalValue());
        this.pdf.setInvoiceReportPath(xmlConfiguracion.getPdf().getInvoiceReportPath().normalValue());
        this.pdf.setBoletaReportPath(xmlConfiguracion.getPdf().getBoletaReportPath().normalValue());
        this.pdf.setCreditNoteReportPath(xmlConfiguracion.getPdf().getCreditNoteReportPath().normalValue());
        this.pdf.setPerceptionReportPath(xmlConfiguracion.getPdf().getPerceptionReportPath().normalValue());
        this.pdf.setRetentionReportPath(xmlConfiguracion.getPdf().getRetentionReportPath().normalValue());
        this.pdf.setDebitNoteReportPath(xmlConfiguracion.getPdf().getDebitNoteReportPath().normalValue());
        this.pdf.setLegendSubReportPath(xmlConfiguracion.getPdf().getLegendSubReportPath().normalValue());
        this.pdf.setRemissionGuideReportPath(xmlConfiguracion.getPdf().getRemissionGuideReportPath().normalValue());
        this.certificadoDigital.setRutaCertificado(xmlConfiguracion.getCertificadoDigital().getRutaCertificado().normalValue());
        this.certificadoDigital.setPasswordCertificado(xmlConfiguracion.getCertificadoDigital().getPasswordCertificado().normalValue());
        this.certificadoDigital.setProveedorKeystore(xmlConfiguracion.getCertificadoDigital().getProveedorKeystore().normalValue());
        this.certificadoDigital.setTipoKeystore(xmlConfiguracion.getCertificadoDigital().getTipoKeystore().normalValue());
        this.pfeConnector.setTipo(xmlConfiguracion.getPfeConnector().getTipo().normalValue());
        this.resumenDiario.setHora(xmlConfiguracion.getResumenDiario().getHora().normalValue());
        this.resumenDiario.setFecha(xmlConfiguracion.getResumenDiario().getFecha().normalValue());
        this.general.setPuertoConfigurador(xmlConfiguracion.getGeneral().getPuertoConfigurador().normalValue());
        this.general.setPuertoRequest(xmlConfiguracion.getGeneral().getPuertoRequest().normalValue());
        this.general.setPuertoResponse(xmlConfiguracion.getGeneral().getPuertoResponse().normalValue());
        this.general.setPuertoResumen(xmlConfiguracion.getGeneral().getPuertoResumen().normalValue());
        this.general.setPuertoPublicador(xmlConfiguracion.getGeneral().getPuertoPublicador().normalValue());
        this.general.setPuertoConfigurador(xmlConfiguracion.getGeneral().getPuertoConfigurador().normalValue());
        this.sunat.setMostrarSoap(xmlConfiguracion.getSunat().getMostrarSoap().normalValue());
        this.sunat.setClienteSunat(xmlConfiguracion.getSunat().getClienteSunat().normalValue());
        this.sunat.setAmbiente(xmlConfiguracion.getSunat().getAmbiente().normalValue());
        this.sunat.setIntegracionWS(xmlConfiguracion.getSunat().getIntegracionWS().normalValue());
        this.sunat.setRutaSunatTest(xmlConfiguracion.getSunat().getRutaSunatTest().normalValue());
        this.sunat.setRutaSunatProd(xmlConfiguracion.getSunat().getRutaSunatProd().normalValue());
        this.sunat.setRutaOseTest(xmlConfiguracion.getSunat().getRutaOseTest().normalValue());
        this.sunat.setRutaOseProd(xmlConfiguracion.getSunat().getRutaOseProd().normalValue());
        this.sunat.setRutaOseProd(xmlConfiguracion.getSunat().getRutaOseProd().normalValue());
        ConfiguracionAplicacion.Sunat.Usuario usuario = new Sunat.Usuario();
        usuario.setUsuarioSol(xmlConfiguracion.getSunat().getUsuario().getUsuarioSol().normalValue());
        usuario.setClaveSol(xmlConfiguracion.getSunat().getUsuario().getClaveSol().normalValue());
        this.sunat.setUsuario(usuario);
        this.versionUBL.setFactura(xmlConfiguracion.getVersionUBL().getFactura().normalValue());
        this.versionUBL.setBoleta(xmlConfiguracion.getVersionUBL().getBoleta().normalValue());
        this.versionUBL.setNotaCredito(xmlConfiguracion.getVersionUBL().getNotaCredito().normalValue());
        this.versionUBL.setNotaDebito(xmlConfiguracion.getVersionUBL().getNotaDebito().normalValue());
    }

    public ConfiguracionAplicacion.GestorTiempo getGestorTiempo() {
        return gestorTiempo;
    }

    public ConfiguracionAplicacion.Erp getErp() {
        return erp;
    }

    public ConfiguracionAplicacion.Jdbc getJdbc() {
        return jdbc;
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

    @ToString
    public static class GestorTiempo {

        private String tiempo = "";

        public String getTiempo() {
            return tiempo;
        }

        public void setTiempo(String tiempo) {
            this.tiempo = tiempo;
        }
    }

    @ToString
    public static class Erp {

        private String servidorLicencias = "";

        private String tipoServidor = "";

        private String tipoConector = "";

        private String servidorBD = "";

        private String baseDeDatos = "";

        private String user = "";

        private String password = "";

        private String erpUser = "";

        private String erpPass = "";

        private String erpRutaArchivos = "";

        private String erpNombreSociedad = "";

        public String getServidorLicencias() {
            return servidorLicencias;
        }

        public void setServidorLicencias(String servidorLicencias) {
            this.servidorLicencias = servidorLicencias;
        }

        public String getTipoServidor() {
            return tipoServidor;
        }

        public void setTipoServidor(String tipoServidor) {
            this.tipoServidor = tipoServidor;
        }

        public String getTipoConector() {
            return tipoConector;
        }

        public void setTipoConector(String tipoConector) {
            this.tipoConector = tipoConector;
        }

        public String getServidorBD() {
            return servidorBD;
        }

        public void setServidorBD(String servidorBD) {
            this.servidorBD = servidorBD;
        }

        public String getBaseDeDatos() {
            return baseDeDatos;
        }

        public void setBaseDeDatos(String baseDeDatos) {
            this.baseDeDatos = baseDeDatos;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getErpUser() {
            return erpUser;
        }

        public void setErpUser(String erpUser) {
            this.erpUser = erpUser;
        }

        public String getErpPass() {
            return erpPass;
        }

        public void setErpPass(String erpPass) {
            this.erpPass = erpPass;
        }

        public String getErpRutaArchivos() {
            return erpRutaArchivos;
        }

        public void setErpRutaArchivos(String erpRutaArchivos) {
            this.erpRutaArchivos = erpRutaArchivos;
        }

        public String getErpNombreSociedad() {
            return erpNombreSociedad;
        }

        public void setErpNombreSociedad(String erpNombreSociedad) {
            this.erpNombreSociedad = erpNombreSociedad;
        }
    }

    @ToString
    public static class Jdbc {

        private String estadoJdbc = "";

        private String servidorBDJdbc = "";

        private String nameUserField = "";

        private String sqlServer = "";

        public String getEstadoJdbc() {
            return estadoJdbc;
        }

        public void setEstadoJdbc(String estadoJdbc) {
            this.estadoJdbc = estadoJdbc;
        }

        public String getServidorBDJdbc() {
            return servidorBDJdbc;
        }

        public void setServidorBDJdbc(String servidorBDJdbc) {
            this.servidorBDJdbc = servidorBDJdbc;
        }

        public String getNameUserField() {
            return nameUserField;
        }

        public void setNameUserField(String nameUserField) {
            this.nameUserField = nameUserField;
        }

        public String getSqlServer() {
            return sqlServer;
        }

        public void setSqlServer(String sqlServer) {
            this.sqlServer = sqlServer;
        }
    }

    @ToString
    public static class UsoSunat {

        private String ws = "";

        private String pdf = "";

        private String validacion = "";

        public String getWs() {
            return ws;
        }

        public void setWs(String ws) {
            this.ws = ws;
        }

        public String getPdf() {
            return pdf;
        }

        public void setPdf(String pdf) {
            this.pdf = pdf;
        }

        public String getValidacion() {
            return validacion;
        }

        public void setValidacion(String validacion) {
            this.validacion = validacion;
        }
    }

    @ToString
    public static class Repositorio {

        private String tipoServidor = "";

        private String servidorBD = "";

        private String puerto = "";

        private String baseDatos = "";

        private String user = "";

        private String password = "";

        public String getTipoServidor() {
            return tipoServidor;
        }

        public void setTipoServidor(String tipoServidor) {
            this.tipoServidor = tipoServidor;
        }

        public String getServidorBD() {
            return servidorBD;
        }

        public void setServidorBD(String servidorBD) {
            this.servidorBD = servidorBD;
        }

        public String getPuerto() {
            return puerto;
        }

        public void setPuerto(String puerto) {
            this.puerto = puerto;
        }

        public String getBaseDatos() {
            return baseDatos;
        }

        public void setBaseDatos(String baseDatos) {
            this.baseDatos = baseDatos;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @ToString
    public static class Proxy {

        private String usarProxy = "";

        private String servidor = "";

        private String puerto = "";

        private String requAuth = "";

        private String user = "";

        private String pass = "";

        public String getUsarProxy() {
            return usarProxy;
        }

        public void setUsarProxy(String usarProxy) {
            this.usarProxy = usarProxy;
        }

        public String getServidor() {
            return servidor;
        }

        public void setServidor(String servidor) {
            this.servidor = servidor;
        }

        public String getPuerto() {
            return puerto;
        }

        public void setPuerto(String puerto) {
            this.puerto = puerto;
        }

        public String getRequAuth() {
            return requAuth;
        }

        public void setRequAuth(String requAuth) {
            this.requAuth = requAuth;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }
    }

    @ToString
    public static class Directorio {

        private String adjuntos = "";

        public String getAdjuntos() {
            return adjuntos;
        }

        public void setAdjuntos(String adjuntos) {
            this.adjuntos = adjuntos;
        }
    }

    @ToString
    public static class Tiempos {

        private String rqTimeOut = "";

        private String rqInterval = "";

        private String rsTimeOut = "";

        private String rsInterval = "";

        public String getRqTimeOut() {
            return rqTimeOut;
        }

        public void setRqTimeOut(String rqTimeOut) {
            this.rqTimeOut = rqTimeOut;
        }

        public String getRqInterval() {
            return rqInterval;
        }

        public void setRqInterval(String rqInterval) {
            this.rqInterval = rqInterval;
        }

        public String getRsTimeOut() {
            return rsTimeOut;
        }

        public void setRsTimeOut(String rsTimeOut) {
            this.rsTimeOut = rsTimeOut;
        }

        public String getRsInterval() {
            return rsInterval;
        }

        public void setRsInterval(String rsInterval) {
            this.rsInterval = rsInterval;
        }
    }

    @ToString
    public static class WebService {

        private String wsUsuario = "";

        private String wsClave = "";

        private String wsLocation = "";

        private Integer wsTiempoEsperaPublic = 0;

        private Integer wsIntervaloRepeticionPublic = 0;

        private Integer wsTiempoPublicacionPublic = 0;

        public String getWsUsuario() {
            return wsUsuario;
        }

        public void setWsUsuario(String wsUsuario) {
            this.wsUsuario = wsUsuario;
        }

        public String getWsClave() {
            return wsClave;
        }

        public void setWsClave(String wsClave) {
            this.wsClave = wsClave;
        }

        public String getWsLocation() {
            return wsLocation;
        }

        public void setWsLocation(String wsLocation) {
            this.wsLocation = wsLocation;
        }

        public Integer getWsTiempoEsperaPublic() {
            return wsTiempoEsperaPublic;
        }

        public void setWsTiempoEsperaPublic(Integer wsTiempoEsperaPublic) {
            this.wsTiempoEsperaPublic = wsTiempoEsperaPublic;
        }

        public Integer getWsIntervaloRepeticionPublic() {
            return wsIntervaloRepeticionPublic;
        }

        public void setWsIntervaloRepeticionPublic(Integer wsIntervaloRepeticionPublic) {
            this.wsIntervaloRepeticionPublic = wsIntervaloRepeticionPublic;
        }

        public Integer getWsTiempoPublicacionPublic() {
            return wsTiempoPublicacionPublic;
        }

        public void setWsTiempoPublicacionPublic(Integer wsTiempoPublicacionPublic) {
            this.wsTiempoPublicacionPublic = wsTiempoPublicacionPublic;
        }
    }

    @ToString
    public static class EmisorElectronico {

        private String rs = "";

        private String senderLogo = "";

        public String getRs() {
            return rs;
        }

        public void setRs(String rs) {
            this.rs = rs;
        }

        public String getSenderLogo() {
            return senderLogo;
        }

        public void setSenderLogo(String senderLogo) {
            this.senderLogo = senderLogo;
        }
    }

    @ToString
    public static class Impresion {

        private String pdf = "";

        public String getPdf() {
            return pdf;
        }

        public void setPdf(String pdf) {
            this.pdf = pdf;
        }
    }

    @ToString
    public static class Pdf {

        private String invoiceReportPath = "";

        private String boletaReportPath = "";

        private String creditNoteReportPath = "";

        private String perceptionReportPath = "";

        private String retentionReportPath = "";

        private String debitNoteReportPath = "";

        private String legendSubReportPath = "";

        private String remissionGuideReportPath = "";

        public String getInvoiceReportPath() {
            return invoiceReportPath;
        }

        public void setInvoiceReportPath(String invoiceReportPath) {
            this.invoiceReportPath = invoiceReportPath;
        }

        public String getBoletaReportPath() {
            return boletaReportPath;
        }

        public void setBoletaReportPath(String boletaReportPath) {
            this.boletaReportPath = boletaReportPath;
        }

        public String getCreditNoteReportPath() {
            return creditNoteReportPath;
        }

        public void setCreditNoteReportPath(String creditNoteReportPath) {
            this.creditNoteReportPath = creditNoteReportPath;
        }

        public String getPerceptionReportPath() {
            return perceptionReportPath;
        }

        public void setPerceptionReportPath(String perceptionReportPath) {
            this.perceptionReportPath = perceptionReportPath;
        }

        public String getRetentionReportPath() {
            return retentionReportPath;
        }

        public void setRetentionReportPath(String retentionReportPath) {
            this.retentionReportPath = retentionReportPath;
        }

        public String getDebitNoteReportPath() {
            return debitNoteReportPath;
        }

        public void setDebitNoteReportPath(String debitNoteReportPath) {
            this.debitNoteReportPath = debitNoteReportPath;
        }

        public String getLegendSubReportPath() {
            return legendSubReportPath;
        }

        public void setLegendSubReportPath(String legendSubReportPath) {
            this.legendSubReportPath = legendSubReportPath;
        }

        public String getRemissionGuideReportPath() {
            return remissionGuideReportPath;
        }

        public void setRemissionGuideReportPath(String remissionGuideReportPath) {
            this.remissionGuideReportPath = remissionGuideReportPath;
        }
    }

    @ToString
    public static class CertificadoDigital {

        private String rutaCertificado = "";

        private String passwordCertificado = "";

        private String proveedorKeystore = "";

        private String tipoKeystore = "";

        public String getRutaCertificado() {
            return rutaCertificado;
        }

        public void setRutaCertificado(String rutaCertificado) {
            this.rutaCertificado = rutaCertificado;
        }

        public String getPasswordCertificado() {
            return passwordCertificado;
        }

        public void setPasswordCertificado(String passwordCertificado) {
            this.passwordCertificado = passwordCertificado;
        }

        public String getProveedorKeystore() {
            return proveedorKeystore;
        }

        public void setProveedorKeystore(String proveedorKeystore) {
            this.proveedorKeystore = proveedorKeystore;
        }

        public String getTipoKeystore() {
            return tipoKeystore;
        }

        public void setTipoKeystore(String tipoKeystore) {
            this.tipoKeystore = tipoKeystore;
        }
    }

    @ToString
    public static class PFEConnector {

        private String tipo = "";

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
    }

    @ToString
    public static class ResumenDiario {

        private String hora = "";

        private String fecha = "";

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }

    @ToString
    public static class General {

        private String puertoConfigurador = "";

        private String puertoRequest = "";

        private String puertoResponse = "";

        private String puertoResumen = "";

        private String puertoPublicador = "";

        private String puertoExtractor = "";

        public String getPuertoConfigurador() {
            return puertoConfigurador;
        }

        public void setPuertoConfigurador(String puertoConfigurador) {
            this.puertoConfigurador = puertoConfigurador;
        }

        public String getPuertoRequest() {
            return puertoRequest;
        }

        public void setPuertoRequest(String puertoRequest) {
            this.puertoRequest = puertoRequest;
        }

        public String getPuertoResponse() {
            return puertoResponse;
        }

        public void setPuertoResponse(String puertoResponse) {
            this.puertoResponse = puertoResponse;
        }

        public String getPuertoResumen() {
            return puertoResumen;
        }

        public void setPuertoResumen(String puertoResumen) {
            this.puertoResumen = puertoResumen;
        }

        public String getPuertoPublicador() {
            return puertoPublicador;
        }

        public void setPuertoPublicador(String puertoPublicador) {
            this.puertoPublicador = puertoPublicador;
        }

        public String getPuertoExtractor() {
            return puertoExtractor;
        }

        public void setPuertoExtractor(String puertoExtractor) {
            this.puertoExtractor = puertoExtractor;
        }
    }

    @ToString
    public static class Sunat {

        private String mostrarSoap = "";

        private String clienteSunat = "";

        private String ambiente = "";

        private ConfiguracionAplicacion.Sunat.Usuario usuario;

        private String integracionWS = "";

        private String rutaSunatTest = "";

        private String rutaSunatProd = "";

        private String rutaOseTest = "";

        private String rutaOseProd = "";

        public String getMostrarSoap() {
            return mostrarSoap;
        }

        public void setMostrarSoap(String mostrarSoap) {
            this.mostrarSoap = mostrarSoap;
        }

        public String getClienteSunat() {
            return clienteSunat;
        }

        public void setClienteSunat(String clienteSunat) {
            this.clienteSunat = clienteSunat;
        }

        public String getAmbiente() {
            return ambiente;
        }

        public void setAmbiente(String ambiente) {
            this.ambiente = ambiente;
        }

        public ConfiguracionAplicacion.Sunat.Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(ConfiguracionAplicacion.Sunat.Usuario usuario) {
            this.usuario = usuario;
        }

        public String getIntegracionWS() {
            return integracionWS;
        }

        public void setIntegracionWS(String integracionWS) {
            this.integracionWS = integracionWS;
        }

        public String getRutaSunatTest() {
            return rutaSunatTest;
        }

        public void setRutaSunatTest(String rutaSunatTest) {
            this.rutaSunatTest = rutaSunatTest;
        }

        public String getRutaSunatProd() {
            return rutaSunatProd;
        }

        public void setRutaSunatProd(String rutaSunatProd) {
            this.rutaSunatProd = rutaSunatProd;
        }

        public String getRutaOseTest() {
            return rutaOseTest;
        }

        public void setRutaOseTest(String rutaOseTest) {
            this.rutaOseTest = rutaOseTest;
        }

        public String getRutaOseProd() {
            return rutaOseProd;
        }

        public void setRutaOseProd(String rutaOseProd) {
            this.rutaOseProd = rutaOseProd;
        }

        @ToString
        public static class Usuario {

            private String usuarioSol = "";

            private String claveSol = "";

            public String getUsuarioSol() {
                return usuarioSol;
            }

            public void setUsuarioSol(String usuarioSol) {
                this.usuarioSol = usuarioSol;
            }

            public String getClaveSol() {
                return claveSol;
            }

            public void setClaveSol(String claveSol) {
                this.claveSol = claveSol;
            }
        }
    }

    @ToString
    public static class VersionUBL {

        private String factura = "";

        private String boleta = "";

        private String notaCredito = "";

        private String notaDebito = "";

        public String getFactura() {
            return factura;
        }

        public void setFactura(String factura) {
            this.factura = factura;
        }

        public String getBoleta() {
            return boleta;
        }

        public void setBoleta(String boleta) {
            this.boleta = boleta;
        }

        public String getNotaCredito() {
            return notaCredito;
        }

        public void setNotaCredito(String notaCredito) {
            this.notaCredito = notaCredito;
        }

        public String getNotaDebito() {
            return notaDebito;
        }

        public void setNotaDebito(String notaDebito) {
            this.notaDebito = notaDebito;
        }
    }
}
