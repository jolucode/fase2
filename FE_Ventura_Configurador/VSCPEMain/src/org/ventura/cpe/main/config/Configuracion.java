package org.ventura.cpe.main.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Configuracion")
public class Configuracion {

    @XmlElement(name = "GestorTiempo")
    private final Configuracion.GestorTiempo gestorTiempo = new GestorTiempo();

    @XmlElement(name = "ERP")
    private final Configuracion.Erp erp = new Erp();

    @XmlElement(name = "UsoSunat")
    private final Configuracion.UsoSunat usoSunat = new UsoSunat();

    @XmlElement(name = "Repositorio")
    private final Configuracion.Repositorio repositorio = new Repositorio();

    @XmlElement(name = "Proxy")
    private final Configuracion.Proxy proxy = new Proxy();

    @XmlElement(name = "Directorio")
    private final Configuracion.Directorio directorio = new Directorio();

    @XmlElement(name = "Tiempos")
    private final Configuracion.Tiempos tiempos = new Tiempos();

    @XmlElement(name = "WebService")
    private final Configuracion.WebService webService = new WebService();

    @XmlElement(name = "Impresion")
    private final Configuracion.Impresion impresion = new Impresion();

    @XmlElement(name = "CertificadoDigital")
    private final Configuracion.CertificadoDigital certificadoDigital = new CertificadoDigital();

    @XmlElement(name = "PFEConnector")
    private final Configuracion.PFEConnector pfeConnector = new PFEConnector();

    @XmlElement(name = "ResumenDiario")
    private final Configuracion.ResumenDiario resumenDiario = new ResumenDiario();

    @XmlElement(name = "General")
    private final Configuracion.General general = new General();

    @XmlElement(name = "Sunat")
    private final Configuracion.Sunat sunat = new Sunat();

    @XmlElement(name = "VersionUBL")
    private final Configuracion.VersionUBL versionUBL = new VersionUBL();

    @XmlElement(name = "Pdf")
    private final Configuracion.Pdf pdf = new Pdf();


    public static class GestorTiempo {

        private Valor tiempo = new Valor();

        public Valor getTiempo() {
            return tiempo;
        }

        @XmlElement(name = "Tiempo")
        public void setTiempo(Valor tiempo) {
            this.tiempo = tiempo;
        }
    }


    public static class Erp {

        private Valor servidorLicencias = new Valor();

        private Valor tipoServidor = new Valor();

        private Valor tipoConector = new Valor();

        private Valor servidorBD = new Valor();

        private Valor baseDeDatos = new Valor();

        private Valor user = new Valor();

        private Valor password = new Valor();

        private Valor erpUser = new Valor();

        private Valor erpPass = new Valor();

        private Valor erpRutaArchivos = new Valor();

        private Valor erpNombreSociedad = new Valor();

        public Valor getServidorLicencias() {
            return servidorLicencias;
        }

        @XmlElement(name = "ServidorLicencias")
        public void setServidorLicencias(Valor servidorLicencias) {
            this.servidorLicencias = servidorLicencias;
        }

        public Valor getTipoServidor() {
            return tipoServidor;
        }

        @XmlElement(name = "TipoServidor")
        public void setTipoServidor(Valor tipoServidor) {
            this.tipoServidor = tipoServidor;
        }

        public Valor getTipoConector() {
            return tipoConector;
        }

        @XmlElement(name = "TipoConector")
        public void setTipoConector(Valor tipoConector) {
            this.tipoConector = tipoConector;
        }

        public Valor getServidorBD() {
            return servidorBD;
        }

        @XmlElement(name = "ServidorBD")
        public void setServidorBD(Valor servidorBD) {
            this.servidorBD = servidorBD;
        }

        public Valor getBaseDeDatos() {
            return baseDeDatos;
        }

        @XmlElement(name = "BaseDeDatos")
        public void setBaseDeDatos(Valor baseDeDatos) {
            this.baseDeDatos = baseDeDatos;
        }

        public Valor getUser() {
            return user;
        }

        @XmlElement(name = "User")
        public void setUser(Valor user) {
            this.user = user;
        }

        public Valor getPassword() {
            return password;
        }

        @XmlElement(name = "Password")
        public void setPassword(Valor password) {
            this.password = password;
        }

        public Valor getErpUser() {
            return erpUser;
        }

        @XmlElement(name = "ERPUser")
        public void setErpUser(Valor erpUser) {
            this.erpUser = erpUser;
        }

        public Valor getErpPass() {
            return erpPass;
        }

        @XmlElement(name = "ERPPass")
        public void setErpPass(Valor erpPass) {
            this.erpPass = erpPass;
        }

        public Valor getErpRutaArchivos() {
            return erpRutaArchivos;
        }

        @XmlElement(name = "ERPRutaArchivos")
        public void setErpRutaArchivos(Valor erpRutaArchivos) {
            this.erpRutaArchivos = erpRutaArchivos;
        }

        public Valor getErpNombreSociedad() {
            return erpNombreSociedad;
        }

        @XmlElement(name = "ERPNombreSociedad")
        public void setErpNombreSociedad(Valor erpNombreSociedad) {
            this.erpNombreSociedad = erpNombreSociedad;
        }
    }


    public static class UsoSunat {

        private Valor ws = new Valor();

        private Valor pdf = new Valor();

        private Valor validacion = new Valor();

        public Valor getWs() {
            return ws;
        }

        @XmlElement(name = "WS")
        public void setWs(Valor ws) {
            this.ws = ws;
        }

        public Valor getPdf() {
            return pdf;
        }

        @XmlElement(name = "PDF")
        public void setPdf(Valor pdf) {
            this.pdf = pdf;
        }

        public Valor getValidacion() {
            return validacion;
        }

        @XmlElement(name = "Validacion")
        public void setValidacion(Valor validacion) {
            this.validacion = validacion;
        }
    }


    public static class Repositorio {

        private Valor tipoServidor = new Valor();

        private Valor servidorBD = new Valor();

        private Valor puerto = new Valor();

        private Valor baseDatos = new Valor();

        private Valor user = new Valor();

        private Valor password = new Valor();

        public Valor getTipoServidor() {
            return tipoServidor;
        }

        @XmlElement(name = "TipoServidor")
        public void setTipoServidor(Valor tipoServidor) {
            this.tipoServidor = tipoServidor;
        }

        public Valor getServidorBD() {
            return servidorBD;
        }

        @XmlElement(name = "ServidorBD")
        public void setServidorBD(Valor servidorBD) {
            this.servidorBD = servidorBD;
        }

        public Valor getPuerto() {
            return puerto;
        }

        @XmlElement(name = "Puerto")
        public void setPuerto(Valor puerto) {
            this.puerto = puerto;
        }

        public Valor getBaseDatos() {
            return baseDatos;
        }

        @XmlElement(name = "BaseDatos")
        public void setBaseDatos(Valor baseDatos) {
            this.baseDatos = baseDatos;
        }

        public Valor getUser() {
            return user;
        }

        @XmlElement(name = "User")
        public void setUser(Valor user) {
            this.user = user;
        }

        public Valor getPassword() {
            return password;
        }

        @XmlElement(name = "Password")
        public void setPassword(Valor password) {
            this.password = password;
        }
    }


    public static class Proxy {

        private Valor usarProxy = new Valor();

        private Valor servidor = new Valor();

        private Valor puerto = new Valor();

        private Valor requAuth = new Valor();

        private Valor user = new Valor();

        private Valor pass = new Valor();

        public Valor getUsarProxy() {
            return usarProxy;
        }

        @XmlElement(name = "UsarProxy")
        public void setUsarProxy(Valor usarProxy) {
            this.usarProxy = usarProxy;
        }

        public Valor getServidor() {
            return servidor;
        }

        @XmlElement(name = "Servidor")
        public void setServidor(Valor servidor) {
            this.servidor = servidor;
        }

        public Valor getPuerto() {
            return puerto;
        }

        @XmlElement(name = "Puerto")
        public void setPuerto(Valor puerto) {
            this.puerto = puerto;
        }

        public Valor getRequAuth() {
            return requAuth;
        }

        @XmlElement(name = "RequAuth")
        public void setRequAuth(Valor requAuth) {
            this.requAuth = requAuth;
        }

        public Valor getUser() {
            return user;
        }

        @XmlElement(name = "User")
        public void setUser(Valor user) {
            this.user = user;
        }

        public Valor getPass() {
            return pass;
        }

        @XmlElement(name = "Pass")
        public void setPass(Valor pass) {
            this.pass = pass;
        }
    }


    public static class Directorio {

        private Valor adjuntos = new Valor();

        public Valor getAdjuntos() {
            return adjuntos;
        }

        @XmlElement(name = "Adjuntos")
        public void setAdjuntos(Valor adjuntos) {
            this.adjuntos = adjuntos;
        }
    }


    public static class Tiempos {

        private Valor rqTimeOut = new Valor();

        private Valor rqInterval = new Valor();

        private Valor rsTimeOut = new Valor();

        private Valor rsInterval = new Valor();

        public Valor getRqTimeOut() {
            return rqTimeOut;
        }

        @XmlElement(name = "RQTimeOut")
        public void setRqTimeOut(Valor rqTimeOut) {
            this.rqTimeOut = rqTimeOut;
        }

        public Valor getRqInterval() {
            return rqInterval;
        }

        @XmlElement(name = "RQInterval")
        public void setRqInterval(Valor rqInterval) {
            this.rqInterval = rqInterval;
        }

        public Valor getRsTimeOut() {
            return rsTimeOut;
        }

        @XmlElement(name = "RSTimeOut")
        public void setRsTimeOut(Valor rsTimeOut) {
            this.rsTimeOut = rsTimeOut;
        }

        public Valor getRsInterval() {
            return rsInterval;
        }

        @XmlElement(name = "RSInterval")
        public void setRsInterval(Valor rsInterval) {
            this.rsInterval = rsInterval;
        }
    }


    public static class WebService {

        private Valor wsUsuario = new Valor();

        private Valor wsClave = new Valor();

        private Valor wsLocation = new Valor();

        private Valor wsTiempoEsperaPublic = new Valor();

        private Valor wsIntervaloRepeticionPublic = new Valor();

        private Valor wsTiempoPublicacionPublic = new Valor();

        public Valor getWsUsuario() {
            return wsUsuario;
        }

        @XmlElement(name = "WSUsuario")
        public void setWsUsuario(Valor wsUsuario) {
            this.wsUsuario = wsUsuario;
        }

        public Valor getWsClave() {
            return wsClave;
        }

        @XmlElement(name = "WSClave")
        public void setWsClave(Valor wsClave) {
            this.wsClave = wsClave;
        }

        public Valor getWsLocation() {
            return wsLocation;
        }

        @XmlElement(name = "WSLocation")
        public void setWsLocation(Valor wsLocation) {
            this.wsLocation = wsLocation;
        }

        public Valor getWsTiempoEsperaPublic() {
            return wsTiempoEsperaPublic;
        }

        @XmlElement(name = "WSTiempoEsperaPublic")
        public void setWsTiempoEsperaPublic(Valor wsTiempoEsperaPublic) {
            this.wsTiempoEsperaPublic = wsTiempoEsperaPublic;
        }

        public Valor getWsIntervaloRepeticionPublic() {
            return wsIntervaloRepeticionPublic;
        }

        @XmlElement(name = "WSIntervaloRepeticionPublic")
        public void setWsIntervaloRepeticionPublic(Valor wsIntervaloRepeticionPublic) {
            this.wsIntervaloRepeticionPublic = wsIntervaloRepeticionPublic;
        }

        public Valor getWsTiempoPublicacionPublic() {
            return wsTiempoPublicacionPublic;
        }

        @XmlElement(name = "WSTiempoPublicacionPublic")
        public void setWsTiempoPublicacionPublic(Valor wsTiempoPublicacionPublic) {
            this.wsTiempoPublicacionPublic = wsTiempoPublicacionPublic;
        }
    }


    public static class EmisorElectronico {

        private Valor rs = new Valor();

        private Valor senderLogo = new Valor();

        public Valor getRs() {
            return rs;
        }

        @XmlElement(name = "RS")
        public void setRs(Valor rs) {
            this.rs = rs;
        }

        public Valor getSenderLogo() {
            return senderLogo;
        }

        @XmlElement(name = "SenderLogo")
        public void setSenderLogo(Valor senderLogo) {
            this.senderLogo = senderLogo;
        }
    }


    public static class Impresion {

        private Valor pdf = new Valor();

        public Valor getPdf() {
            return pdf;
        }

        @XmlElement(name = "PDF")
        public void setPdf(Valor pdf) {
            this.pdf = pdf;
        }
    }


    public static class Pdf {

        private Valor invoiceReportPath = new Valor();

        private Valor boletaReportPath = new Valor();

        private Valor creditNoteReportPath = new Valor();

        private Valor perceptionReportPath = new Valor();

        private Valor retentionReportPath = new Valor();

        private Valor debitNoteReportPath = new Valor();

        private Valor legendSubReportPath = new Valor();

        private Valor remissionGuideReportPath = new Valor();

        public Valor getInvoiceReportPath() {
            return invoiceReportPath;
        }

        @XmlElement(name = "InvoiceReportPath")
        public void setInvoiceReportPath(Valor invoiceReportPath) {
            this.invoiceReportPath = invoiceReportPath;
        }

        public Valor getBoletaReportPath() {
            return boletaReportPath;
        }

        @XmlElement(name = "BoletaReportPath")
        public void setBoletaReportPath(Valor boletaReportPath) {
            this.boletaReportPath = boletaReportPath;
        }

        public Valor getCreditNoteReportPath() {
            return creditNoteReportPath;
        }

        @XmlElement(name = "CreditNoteReportPath")
        public void setCreditNoteReportPath(Valor creditNoteReportPath) {
            this.creditNoteReportPath = creditNoteReportPath;
        }

        public Valor getPerceptionReportPath() {
            return perceptionReportPath;
        }

        @XmlElement(name = "PerceptionReportPath")
        public void setPerceptionReportPath(Valor perceptionReportPath) {
            this.perceptionReportPath = perceptionReportPath;
        }

        public Valor getRetentionReportPath() {
            return retentionReportPath;
        }

        @XmlElement(name = "RetentionReportPath")
        public void setRetentionReportPath(Valor retentionReportPath) {
            this.retentionReportPath = retentionReportPath;
        }

        public Valor getDebitNoteReportPath() {
            return debitNoteReportPath;
        }

        @XmlElement(name = "DebitNoteReportPath")
        public void setDebitNoteReportPath(Valor debitNoteReportPath) {
            this.debitNoteReportPath = debitNoteReportPath;
        }

        public Valor getLegendSubReportPath() {
            return legendSubReportPath;
        }

        @XmlElement(name = "LegendSubReportPath")
        public void setLegendSubReportPath(Valor legendSubReportPath) {
            this.legendSubReportPath = legendSubReportPath;
        }

        public Valor getRemissionGuideReportPath() {
            return remissionGuideReportPath;
        }

        @XmlElement(name = "RemissionGuideReportPath")
        public void setRemissionGuideReportPath(Valor remissionGuideReportPath) {
            this.remissionGuideReportPath = remissionGuideReportPath;
        }
    }


    public static class CertificadoDigital {

        private Valor rutaCertificado = new Valor();

        private Valor passwordCertificado = new Valor();

        private Valor proveedorKeystore = new Valor();

        private Valor tipoKeystore = new Valor();

        public Valor getRutaCertificado() {
            return rutaCertificado;
        }

        @XmlElement(name = "RutaCertificado")
        public void setRutaCertificado(Valor rutaCertificado) {
            this.rutaCertificado = rutaCertificado;
        }

        public Valor getPasswordCertificado() {
            return passwordCertificado;
        }

        @XmlElement(name = "PasswordCertificado")
        public void setPasswordCertificado(Valor passwordCertificado) {
            this.passwordCertificado = passwordCertificado;
        }

        public Valor getProveedorKeystore() {
            return proveedorKeystore;
        }

        @XmlElement(name = "ProveedorKeystore")
        public void setProveedorKeystore(Valor proveedorKeystore) {
            this.proveedorKeystore = proveedorKeystore;
        }

        public Valor getTipoKeystore() {
            return tipoKeystore;
        }

        @XmlElement(name = "TipoKeystore")
        public void setTipoKeystore(Valor tipoKeystore) {
            this.tipoKeystore = tipoKeystore;
        }
    }


    public static class PFEConnector {

        private Valor tipo = new Valor();

        public Valor getTipo() {
            return tipo;
        }

        @XmlElement(name = "Tipo")
        public void setTipo(Valor tipo) {
            this.tipo = tipo;
        }
    }


    public static class ResumenDiario {

        private Valor hora = new Valor();

        private Valor fecha = new Valor();

        public Valor getHora() {
            return hora;
        }

        @XmlElement(name = "Hora")
        public void setHora(Valor hora) {
            this.hora = hora;
        }

        public Valor getFecha() {
            return fecha;
        }

        @XmlElement(name = "Fecha")
        public void setFecha(Valor fecha) {
            this.fecha = fecha;
        }
    }


    public static class General {

        private Valor puertoConfigurador = new Valor();

        private Valor puertoRequest = new Valor();

        private Valor puertoResponse = new Valor();

        private Valor puertoResumen = new Valor();

        private Valor puertoPublicador = new Valor();

        private Valor puertoExtractor = new Valor();

        public Valor getPuertoConfigurador() {
            return puertoConfigurador;
        }

        @XmlElement(name = "PuertoVS_Configurador")
        public void setPuertoConfigurador(Valor puertoConfigurador) {
            this.puertoConfigurador = puertoConfigurador;
        }

        public Valor getPuertoRequest() {
            return puertoRequest;
        }

        @XmlElement(name = "PuertoVS_Request")
        public void setPuertoRequest(Valor puertoRequest) {
            this.puertoRequest = puertoRequest;
        }

        public Valor getPuertoResponse() {
            return puertoResponse;
        }

        @XmlElement(name = "PuertoVS_Response")
        public void setPuertoResponse(Valor puertoResponse) {
            this.puertoResponse = puertoResponse;
        }

        public Valor getPuertoResumen() {
            return puertoResumen;
        }

        @XmlElement(name = "PuertoVS_Resumen")
        public void setPuertoResumen(Valor puertoResumen) {
            this.puertoResumen = puertoResumen;
        }

        public Valor getPuertoPublicador() {
            return puertoPublicador;
        }

        @XmlElement(name = "PuertoVS_PublicWS")
        public void setPuertoPublicador(Valor puertoPublicador) {
            this.puertoPublicador = puertoPublicador;
        }

        public Valor getPuertoExtractor() {
            return puertoExtractor;
        }

        @XmlElement(name = "PuertoVS_Extractor")
        public void setPuertoExtractor(Valor puertoExtractor) {
            this.puertoExtractor = puertoExtractor;
        }
    }


    public static class Sunat {

        private Valor mostrarSoap = new Valor();

        private Valor clienteSunat = new Valor();

        private Valor ambiente = new Valor();

        private Usuario usuario;

        private Valor integracionWS = new Valor();

        private Valor rutaSunatTest = new Valor();

        private Valor rutaSunatProd = new Valor();

        private Valor rutaOseTest = new Valor();

        private Valor rutaOseProd = new Valor();

        public Valor getMostrarSoap() {
            return mostrarSoap;
        }

        @XmlElement(name = "MostrarSoap")
        public void setMostrarSoap(Valor mostrarSoap) {
            this.mostrarSoap = mostrarSoap;
        }

        public Valor getClienteSunat() {
            return clienteSunat;
        }

        @XmlElement(name = "ClienteSunat")
        public void setClienteSunat(Valor clienteSunat) {
            this.clienteSunat = clienteSunat;
        }

        public Valor getAmbiente() {
            return ambiente;
        }

        @XmlElement(name = "Ambiente")
        public void setAmbiente(Valor ambiente) {
            this.ambiente = ambiente;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        @XmlElement(name = "Usuario")
        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public Valor getIntegracionWS() {
            return integracionWS;
        }

        @XmlElement(name = "IntegracionWS")
        public void setIntegracionWS(Valor integracionWS) {
            this.integracionWS = integracionWS;
        }

        public Valor getRutaSunatTest() {
            return rutaSunatTest;
        }

        @XmlElement(name = "RutaSunatTest")
        public void setRutaSunatTest(Valor rutaSunatTest) {
            this.rutaSunatTest = rutaSunatTest;
        }

        public Valor getRutaSunatProd() {
            return rutaSunatProd;
        }

        @XmlElement(name = "RutaSunatProd")
        public void setRutaSunatProd(Valor rutaSunatProd) {
            this.rutaSunatProd = rutaSunatProd;
        }

        public Valor getRutaOseTest() {
            return rutaOseTest;
        }

        @XmlElement(name = "RutaOseTest")
        public void setRutaOseTest(Valor rutaOseTest) {
            this.rutaOseTest = rutaOseTest;
        }

        public Valor getRutaOseProd() {
            return rutaOseProd;
        }

        @XmlElement(name = "RutaOseProd")
        public void setRutaOseProd(Valor rutaOseProd) {
            this.rutaOseProd = rutaOseProd;
        }


        public static class Usuario {

            private Valor usuarioSol = new Valor();

            private Valor claveSol = new Valor();

            public Valor getUsuarioSol() {
                return usuarioSol;
            }

            @XmlElement(name = "UsuarioSol")
            public void setUsuarioSol(Valor usuarioSol) {
                this.usuarioSol = usuarioSol;
            }

            public Valor getClaveSol() {
                return claveSol;
            }

            @XmlElement(name = "ClaveSol")
            public void setClaveSol(Valor claveSol) {
                this.claveSol = claveSol;
            }
        }
    }


    public static class VersionUBL {

        private Valor factura = new Valor();

        private Valor boleta = new Valor();

        private Valor notaCredito = new Valor();

        private Valor notaDebito = new Valor();

        public Valor getFactura() {
            return factura;
        }

        @XmlElement(name = "Factura")
        public void setFactura(Valor factura) {
            this.factura = factura;
        }

        public Valor getBoleta() {
            return boleta;
        }

        @XmlElement(name = "Boleta")
        public void setBoleta(Valor boleta) {
            this.boleta = boleta;
        }

        public Valor getNotaCredito() {
            return notaCredito;
        }

        @XmlElement(name = "NotaCredito")
        public void setNotaCredito(Valor notaCredito) {
            this.notaCredito = notaCredito;
        }

        public Valor getNotaDebito() {
            return notaDebito;
        }

        @XmlElement(name = "NotaDebito")
        public void setNotaDebito(Valor notaDebito) {
            this.notaDebito = notaDebito;
        }
    }

    public GestorTiempo getGestorTiempo() {
        return gestorTiempo;
    }

    public Erp getErp() {
        return erp;
    }

    public UsoSunat getUsoSunat() {
        return usoSunat;
    }

    public Repositorio getRepositorio() {
        return repositorio;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Directorio getDirectorio() {
        return directorio;
    }

    public Tiempos getTiempos() {
        return tiempos;
    }

    public WebService getWebService() {
        return webService;
    }

    public Impresion getImpresion() {
        return impresion;
    }

    public CertificadoDigital getCertificadoDigital() {
        return certificadoDigital;
    }

    public PFEConnector getPfeConnector() {
        return pfeConnector;
    }

    public ResumenDiario getResumenDiario() {
        return resumenDiario;
    }

    public General getGeneral() {
        return general;
    }

    public Sunat getSunat() {
        return sunat;
    }

    public VersionUBL getVersionUBL() {
        return versionUBL;
    }

    public Pdf getPdf() {
        return pdf;
    }
}
