package org.ventura.wsclient.config.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Repositorio">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ServidorBD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Puerto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BaseDatos" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Proxy">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="UsarProxy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Puerto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ReqAuth" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TransaccionProps" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ConsultaProps" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Directorio">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Adjuntos">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Tiempos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RQTimeOut" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RQInterval" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RSTimeOut" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RSInterval" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="EmisorElectronico">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SenderLogo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CertificadoDigital">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ProveedorKeystore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TipoKeystore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RutaCertificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PasswordCertificado">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Sunat">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ClienteSunat" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MostrarSoap" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Ambiente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="IntegracionWS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Usuario">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="UsuarioSol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="ClaveSol">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Pdf">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="InvoiceReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BoletaReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="CreditNoteReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DebitNoteReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="LegendSubReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RemissionGuideReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PFEConnector">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Tipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "repositorio",
    "proxy",
    "transaccionProps",
    "consultaProps",
    "directorio",
    "tiempos",
    "emisorElectronico",
    "certificadoDigital",
    "sunat",
    "pdf",
    "pfeConnector"
})
@XmlRootElement(name = "Configuracion")
public class Configuracion {

    @XmlElement(name = "Repositorio", required = true)
    protected Configuracion.Repositorio repositorio;
    @XmlElement(name = "Proxy", required = true)
    protected Configuracion.Proxy proxy;
    @XmlElement(name = "TransaccionProps", required = true)
    protected String transaccionProps;
    @XmlElement(name = "ConsultaProps", required = true)
    protected String consultaProps;
    @XmlElement(name = "Directorio", required = true)
    protected Configuracion.Directorio directorio;
    @XmlElement(name = "Tiempos", required = true)
    protected Configuracion.Tiempos tiempos;
    @XmlElement(name = "EmisorElectronico", required = true)
    protected Configuracion.EmisorElectronico emisorElectronico;
    @XmlElement(name = "CertificadoDigital", required = true)
    protected Configuracion.CertificadoDigital certificadoDigital;
    @XmlElement(name = "Sunat", required = true)
    protected Configuracion.Sunat sunat;
    @XmlElement(name = "Pdf", required = true)
    protected Configuracion.Pdf pdf;
    @XmlElement(name = "PFEConnector", required = true)
    protected Configuracion.PFEConnector pfeConnector;

    /**
     * Gets the value of the repositorio property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Repositorio }
     *     
     */
    public Configuracion.Repositorio getRepositorio() {
        return repositorio;
    } //getRepositorio

    /**
     * Sets the value of the repositorio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Repositorio }
     *     
     */
    public void setRepositorio(Configuracion.Repositorio value) {
        this.repositorio = value;
    } //setRepositorio

    /**
     * Gets the value of the proxy property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Proxy }
     *     
     */
    public Configuracion.Proxy getProxy() {
        return proxy;
    } //getProxy

    /**
     * Sets the value of the proxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Proxy }
     *     
     */
    public void setProxy(Configuracion.Proxy value) {
        this.proxy = value;
    } //setProxy

    /**
     * Gets the value of the transaccionProps property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransaccionProps() {
        return transaccionProps;
    } //getTransaccionProps

    /**
     * Sets the value of the transaccionProps property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransaccionProps(String value) {
        this.transaccionProps = value;
    } //setTransaccionProps

    /**
     * Gets the value of the consultaProps property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsultaProps() {
        return consultaProps;
    } //getConsultaProps

    /**
     * Sets the value of the consultaProps property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsultaProps(String value) {
        this.consultaProps = value;
    } //setConsultaProps

    /**
     * Gets the value of the directorio property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Directorio }
     *     
     */
    public Configuracion.Directorio getDirectorio() {
        return directorio;
    } //getDirectorio

    /**
     * Sets the value of the directorio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Directorio }
     *     
     */
    public void setDirectorio(Configuracion.Directorio value) {
        this.directorio = value;
    } //setDirectorio

    /**
     * Gets the value of the tiempos property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Tiempos }
     *     
     */
    public Configuracion.Tiempos getTiempos() {
        return tiempos;
    } //getTiempos

    /**
     * Sets the value of the tiempos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Tiempos }
     *     
     */
    public void setTiempos(Configuracion.Tiempos value) {
        this.tiempos = value;
    } //setTiempos

    /**
     * Gets the value of the emisorElectronico property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.EmisorElectronico }
     *     
     */
    public Configuracion.EmisorElectronico getEmisorElectronico() {
        return emisorElectronico;
    } //getEmisorElectronico

    /**
     * Sets the value of the emisorElectronico property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.EmisorElectronico }
     *     
     */
    public void setEmisorElectronico(Configuracion.EmisorElectronico value) {
        this.emisorElectronico = value;
    } //setEmisorElectronico

    /**
     * Gets the value of the certificadoDigital property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.CertificadoDigital }
     *     
     */
    public Configuracion.CertificadoDigital getCertificadoDigital() {
        return certificadoDigital;
    } //getCertificadoDigital

    /**
     * Sets the value of the certificadoDigital property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.CertificadoDigital }
     *     
     */
    public void setCertificadoDigital(Configuracion.CertificadoDigital value) {
        this.certificadoDigital = value;
    } //setCertificadoDigital

    /**
     * Gets the value of the sunat property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Sunat }
     *     
     */
    public Configuracion.Sunat getSunat() {
        return sunat;
    } //getSunat

    /**
     * Sets the value of the sunat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Sunat }
     *     
     */
    public void setSunat(Configuracion.Sunat value) {
        this.sunat = value;
    } //setSunat

    /**
     * Gets the value of the pdf property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.Pdf }
     *     
     */
    public Configuracion.Pdf getPdf() {
        return pdf;
    } //getPdf

    /**
     * Sets the value of the pdf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.Pdf }
     *     
     */
    public void setPdf(Configuracion.Pdf value) {
        this.pdf = value;
    } //setPdf

    /**
     * Gets the value of the pfeConnector property.
     * 
     * @return
     *     possible object is
     *     {@link Configuracion.PFEConnector }
     *     
     */
    public Configuracion.PFEConnector getPFEConnector() {
        return pfeConnector;
    } //getPFEConnector

    /**
     * Sets the value of the pfeConnector property.
     * 
     * @param value
     *     allowed object is
     *     {@link Configuracion.PFEConnector }
     *     
     */
    public void setPFEConnector(Configuracion.PFEConnector value) {
        this.pfeConnector = value;
    } //setPFEConnector

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ServidorBD" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Puerto" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="BaseDatos" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "servidorBD",
        "puerto",
        "baseDatos",
        "user",
        "password"
    })
    public static class Repositorio {

        @XmlElement(name = "ServidorBD", required = true)
        protected String servidorBD;
        @XmlElement(name = "Puerto", required = true)
        protected String puerto;
        @XmlElement(name = "BaseDatos", required = true)
        protected String baseDatos;
        @XmlElement(name = "User", required = true)
        protected String user;
        @XmlElement(name = "Password", required = true)
        protected String password;

        /**
         * Gets the value of the servidorBD property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServidorBD() {
            return servidorBD;
        } //getServidorBD

        /**
         * Sets the value of the servidorBD property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServidorBD(String value) {
            this.servidorBD = value;
        } //setServidorBD

        /**
         * Gets the value of the puerto property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPuerto() {
            return puerto;
        } //getPuerto

        /**
         * Sets the value of the puerto property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPuerto(String value) {
            this.puerto = value;
        } //setPuerto

        /**
         * Gets the value of the baseDatos property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBaseDatos() {
            return baseDatos;
        } //getBaseDatos

        /**
         * Sets the value of the baseDatos property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBaseDatos(String value) {
            this.baseDatos = value;
        } //setBaseDatos

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        } //getUser

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        } //setUser

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        } //getPassword

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        } //setPassword

    } //Repositorio

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="UsarProxy" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Puerto" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ReqAuth" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "usarProxy",
        "servidor",
        "puerto",
        "reqAuth",
        "user",
        "password"
    })
    public static class Proxy {

        @XmlElement(name = "UsarProxy", required = true)
        protected String usarProxy;
        @XmlElement(name = "Servidor", required = true)
        protected String servidor;
        @XmlElement(name = "Puerto", required = true)
        protected String puerto;
        @XmlElement(name = "ReqAuth", required = true)
        protected String reqAuth;
        @XmlElement(name = "User", required = true)
        protected String user;
        @XmlElement(name = "Password", required = true)
        protected String password;

        /**
         * Gets the value of the usarProxy property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUsarProxy() {
            return usarProxy;
        } //getUsarProxy

        /**
         * Sets the value of the usarProxy property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUsarProxy(String value) {
            this.usarProxy = value;
        } //setUsarProxy

        /**
         * Gets the value of the servidor property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServidor() {
            return servidor;
        } //getServidor

        /**
         * Sets the value of the servidor property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServidor(String value) {
            this.servidor = value;
        } //setServidor

        /**
         * Gets the value of the puerto property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPuerto() {
            return puerto;
        } //getPuerto

        /**
         * Sets the value of the puerto property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPuerto(String value) {
            this.puerto = value;
        } //setPuerto

        /**
         * Gets the value of the reqAuth property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReqAuth() {
            return reqAuth;
        } //getReqAuth

        /**
         * Sets the value of the reqAuth property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReqAuth(String value) {
            this.reqAuth = value;
        } //setReqAuth

        /**
         * Gets the value of the user property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUser() {
            return user;
        } //getUser

        /**
         * Sets the value of the user property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUser(String value) {
            this.user = value;
        } //setUser

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        } //getPassword

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        } //setPassword

    } //Proxy

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Adjuntos">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "adjuntos"
    })
    public static class Directorio {

    	@XmlElement(name = "Adjuntos", required = true)
        protected Configuracion.Directorio.Adjuntos adjuntos;

        /**
         * Gets the value of the adjuntos property.
         * 
         * @return
         *     possible object is
         *     {@link Configuracion.Directorio.Adjuntos }
         *     
         */
        public Configuracion.Directorio.Adjuntos getAdjuntos() {
            return adjuntos;
        } //getAdjuntos

        /**
         * Sets the value of the adjuntos property.
         * 
         * @param value
         *     allowed object is
         *     {@link Configuracion.Directorio.Adjuntos }
         *     
         */
        public void setAdjuntos(Configuracion.Directorio.Adjuntos value) {
            this.adjuntos = value;
        } //setAdjuntos

        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Adjuntos {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "encriptado")
            protected String encriptado;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            } //getValue

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            } //setValue

            /**
             * Gets the value of the encriptado property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEncriptado() {
                return encriptado;
            } //getEncriptado

            /**
             * Sets the value of the encriptado property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEncriptado(String value) {
                this.encriptado = value;
            } //setEncriptado

        } //Adjuntos

    } //Directorio

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="RQTimeOut" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RQInterval" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RSTimeOut" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RSInterval" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rqTimeOut",
        "rqInterval",
        "rsTimeOut",
        "rsInterval"
    })
    public static class Tiempos {

        @XmlElement(name = "RQTimeOut", required = true)
        protected String rqTimeOut;
        @XmlElement(name = "RQInterval", required = true)
        protected String rqInterval;
        @XmlElement(name = "RSTimeOut", required = true)
        protected String rsTimeOut;
        @XmlElement(name = "RSInterval", required = true)
        protected String rsInterval;

        /**
         * Gets the value of the rqTimeOut property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRQTimeOut() {
            return rqTimeOut;
        } //getRQTimeOut

        /**
         * Sets the value of the rqTimeOut property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRQTimeOut(String value) {
            this.rqTimeOut = value;
        } //setRQTimeOut

        /**
         * Gets the value of the rqInterval property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRQInterval() {
            return rqInterval;
        } //getRQInterval

        /**
         * Sets the value of the rqInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRQInterval(String value) {
            this.rqInterval = value;
        } //setRQInterval

        /**
         * Gets the value of the rsTimeOut property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRSTimeOut() {
            return rsTimeOut;
        } //getRSTimeOut

        /**
         * Sets the value of the rsTimeOut property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRSTimeOut(String value) {
            this.rsTimeOut = value;
        } //setRSTimeOut

        /**
         * Gets the value of the rsInterval property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRSInterval() {
            return rsInterval;
        } //getRSInterval

        /**
         * Sets the value of the rsInterval property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRSInterval(String value) {
            this.rsInterval = value;
        } //setRSInterval

    } //Tiempos

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="RS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SenderLogo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "rs",
        "senderLogo"
    })
    public static class EmisorElectronico {

        @XmlElement(name = "RS", required = true)
        protected String rs;
        @XmlElement(name = "SenderLogo", required = true)
        protected String senderLogo;

        /**
         * Gets the value of the rs property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRS() {
            return rs;
        } //getRS

        /**
         * Sets the value of the rs property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRS(String value) {
            this.rs = value;
        } //setRS

        /**
         * Gets the value of the senderLogo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSenderLogo() {
            return senderLogo;
        } //getSenderLogo

        /**
         * Sets the value of the senderLogo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSenderLogo(String value) {
            this.senderLogo = value;
        } //setSenderLogo

    } //EmisorElectronico

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ProveedorKeystore" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TipoKeystore" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RutaCertificado" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PasswordCertificado">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "proveedorKeystore",
        "tipoKeystore",
        "rutaCertificado",
        "passwordCertificado"
    })
    public static class CertificadoDigital {

        @XmlElement(name = "ProveedorKeystore", required = true)
    	protected String proveedorKeystore;
    	@XmlElement(name = "TipoKeystore", required = true)
    	protected String tipoKeystore;
    	@XmlElement(name = "RutaCertificado", required = true)
    	protected String rutaCertificado;
    	@XmlElement(name = "PasswordCertificado", required = true)
    	protected Configuracion.CertificadoDigital.PasswordCertificado passwordCertificado;

        /**
         * Gets the value of the proveedorKeystore property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProveedorKeystore() {
            return proveedorKeystore;
        } //getProveedorKeystore

        /**
         * Sets the value of the proveedorKeystore property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProveedorKeystore(String value) {
            this.proveedorKeystore = value;
        } //setProveedorKeystore

        /**
         * Gets the value of the tipoKeystore property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipoKeystore() {
            return tipoKeystore;
        } //getTipoKeystore

        /**
         * Sets the value of the tipoKeystore property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipoKeystore(String value) {
            this.tipoKeystore = value;
        } //setTipoKeystore

        /**
         * Gets the value of the rutaCertificado property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRutaCertificado() {
            return rutaCertificado;
        } //getRutaCertificado

        /**
         * Sets the value of the rutaCertificado property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRutaCertificado(String value) {
            this.rutaCertificado = value;
        } //setRutaCertificado

        /**
         * Gets the value of the passwordCertificado property.
         * 
         * @return
         *     possible object is
         *     {@link Configuracion.CertificadoDigital.PasswordCertificado }
         *     
         */
        public Configuracion.CertificadoDigital.PasswordCertificado getPasswordCertificado() {
            return passwordCertificado;
        } //getPasswordCertificado

        /**
         * Sets the value of the passwordCertificado property.
         * 
         * @param value
         *     allowed object is
         *     {@link Configuracion.CertificadoDigital.PasswordCertificado }
         *     
         */
        public void setPasswordCertificado(Configuracion.CertificadoDigital.PasswordCertificado value) {
            this.passwordCertificado = value;
        } //setPasswordCertificado

        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class PasswordCertificado {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "encriptado")
            protected String encriptado;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            } //getValue

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            } //setValue

            /**
             * Gets the value of the encriptado property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEncriptado() {
                return encriptado;
            } //getEncriptado

            /**
             * Sets the value of the encriptado property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEncriptado(String value) {
                this.encriptado = value;
            } //setEncriptado

        } //PasswordCertificado

    } //CertificadoDigital

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ClienteSunat" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MostrarSoap" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Ambiente" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="IntegracionWS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Usuario">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="UsuarioSol" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="ClaveSol">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                           &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "clienteSunat",
        "mostrarSoap",
        "ambiente",
        "integracionWS",
        "usuario",
        "rutaConoseProd",
        "rutaConoseTest"
    })
	public static class Sunat {

        @XmlElement(name = "ClienteSunat", required = true)
        protected String clienteSunat;
        @XmlElement(name = "MostrarSoap", required = true)
        protected String mostrarSoap;
        @XmlElement(name = "Ambiente", required = true)
        protected String ambiente;
        @XmlElement(name = "IntegracionWS", required = true)
        protected String integracionWS;
        @XmlElement(name = "Usuario", required = true)
        protected Configuracion.Sunat.Usuario usuario;

        public String getRutaConoseProd() { return rutaConoseProd; }

        public String getRutaConoseTest() { return rutaConoseTest; }

        @XmlElement(name = "RutaConoseProd", required = true)
        protected String rutaConoseProd;

        @XmlElement(name = "RutaConoseTest", required = true)
        protected String rutaConoseTest;
        /**
         * Gets the value of the clienteSunat property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getClienteSunat() {
            return clienteSunat;
        } //getClienteSunat

        /**
         * Sets the value of the clienteSunat property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setClienteSunat(String value) {
            this.clienteSunat = value;
        } //setClienteSunat

        /**
         * Gets the value of the mostrarSoap property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMostrarSoap() {
            return mostrarSoap;
        } //getMostrarSoap

        /**
         * Sets the value of the mostrarSoap property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMostrarSoap(String value) {
            this.mostrarSoap = value;
        } //setMostrarSoap

        /**
         * Gets the value of the ambiente property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAmbiente() {
            return ambiente;
        } //getAmbiente

        /**
         * Sets the value of the ambiente property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAmbiente(String value) {
            this.ambiente = value;
        } //setAmbiente

        /**
         * Gets the value of the integracionWS property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getIntegracionWS() {
            return integracionWS;
        } //getIntegracionWS

        /**
         * Sets the value of the integracionWS property.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setIntegracionWS(String value) {
            this.integracionWS = value;
        } //setIntegracionWS

        /**
         * Gets the value of the usuario property.
         * 
         * @return
         *     possible object is
         *     {@link Configuracion.Sunat.Usuario }
         *     
         */
        public Configuracion.Sunat.Usuario getUsuario() {
            return usuario;
        } //getUsuario

        /**
         * Sets the value of the usuario property.
         * 
         * @param value
         *     allowed object is
         *     {@link Configuracion.Sunat.Usuario }
         *     
         */
        public void setUsuario(Configuracion.Sunat.Usuario value) {
            this.usuario = value;
        } //setUsuario

        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="UsuarioSol" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="ClaveSol">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                 &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/extension>
         *             &lt;/simpleContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "usuarioSol",
            "claveSol"
        })
        public static class Usuario {

        	@XmlElement(name = "UsuarioSol", required = true)
            protected String usuarioSol;
            @XmlElement(name = "ClaveSol", required = true)
            protected Configuracion.Sunat.Usuario.ClaveSol claveSol;

            /**
             * Gets the value of the usuarioSol property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUsuarioSol() {
                return usuarioSol;
            } //getUsuarioSol

            /**
             * Sets the value of the usuarioSol property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUsuarioSol(String value) {
                this.usuarioSol = value;
            } //setUsuarioSol

            /**
             * Gets the value of the claveSol property.
             * 
             * @return
             *     possible object is
             *     {@link Configuracion.Sunat.Usuario.ClaveSol }
             *     
             */
            public Configuracion.Sunat.Usuario.ClaveSol getClaveSol() {
                return claveSol;
            } //getClaveSol

            /**
             * Sets the value of the claveSol property.
             * 
             * @param value
             *     allowed object is
             *     {@link Configuracion.Sunat.Usuario.ClaveSol }
             *     
             */
            public void setClaveSol(Configuracion.Sunat.Usuario.ClaveSol value) {
                this.claveSol = value;
            } //setClaveSol

            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *       &lt;attribute name="encriptado" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/extension>
             *   &lt;/simpleContent>
             * &lt;/complexType>
             * </pre>
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class ClaveSol {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "encriptado")
                protected String encriptado;

                /**
                 * Gets the value of the value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                } //getValue

                /**
                 * Sets the value of the value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                } //setValue

                /**
                 * Gets the value of the encriptado property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getEncriptado() {
                    return encriptado;
                } //getEncriptado

                /**
                 * Sets the value of the encriptado property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setEncriptado(String value) {
                    this.encriptado = value;
                } //setEncriptado

            } //ClaveSol

        } //Usuario

    } //Sunat

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="InvoiceReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="BoletaReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="CreditNoteReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DebitNoteReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="LegendSubReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RetentionReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="PerceptionReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="RemissionGuideReportPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "invoiceReportPath",
        "boletaReportPath",
        "creditNoteReportPath",
        "debitNoteReportPath",
        "legendSubReportPath",
        "retentionReportPath",
        "perceptionReportPath",
        "remissionGuideReportPath"
    })
    public static class Pdf {

        @XmlElement(name = "InvoiceReportPath", required = true)
        protected String invoiceReportPath;
        @XmlElement(name = "BoletaReportPath", required = true)
        protected String boletaReportPath;
        @XmlElement(name = "CreditNoteReportPath", required = true)
        protected String creditNoteReportPath;
        @XmlElement(name = "DebitNoteReportPath", required = true)
        protected String debitNoteReportPath;
        @XmlElement(name = "LegendSubReportPath", required = true)
        protected String legendSubReportPath;
        @XmlElement(name = "RetentionReportPath", required = true)
        protected String retentionReportPath;
        @XmlElement(name = "PerceptionReportPath", required = true)
        protected String perceptionReportPath;
        @XmlElement(name = "RemissionGuideReportPath", required = true)
        protected String remissionGuideReportPath;

        /**
         * Gets the value of the invoiceReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInvoiceReportPath() {
            return invoiceReportPath;
        } //getInvoiceReportPath

        /**
         * Sets the value of the invoiceReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInvoiceReportPath(String value) {
            this.invoiceReportPath = value;
        } //setInvoiceReportPath

        /**
         * Gets the value of the boletaReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBoletaReportPath() {
            return boletaReportPath;
        } //getBoletaReportPath

        /**
         * Sets the value of the boletaReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBoletaReportPath(String value) {
            this.boletaReportPath = value;
        } //setBoletaReportPath

        /**
         * Gets the value of the creditNoteReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCreditNoteReportPath() {
            return creditNoteReportPath;
        } //getCreditNoteReportPath

        /**
         * Sets the value of the creditNoteReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCreditNoteReportPath(String value) {
            this.creditNoteReportPath = value;
        } //setCreditNoteReportPath

        /**
         * Gets the value of the debitNoteReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDebitNoteReportPath() {
            return debitNoteReportPath;
        } //getDebitNoteReportPath

        /**
         * Sets the value of the debitNoteReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDebitNoteReportPath(String value) {
            this.debitNoteReportPath = value;
        } //setDebitNoteReportPath

        /**
         * Gets the value of the legendSubReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLegendSubReportPath() {
            return legendSubReportPath;
        } //getLegendSubReportPath

        /**
         * Sets the value of the legendSubReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLegendSubReportPath(String value) {
            this.legendSubReportPath = value;
        } //setLegendSubReportPath

        /**
         * Gets the value of the retentionReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
		public String getRetentionReportPath() {
			return retentionReportPath;
		} //getRetentionReportPath

        /**
         * Sets the value of the retentionReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
		public void setRetentionReportPath(String value) {
			this.retentionReportPath = value;
		} //setRetentionReportPath

        /**
         * Gets the value of the perceptionReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
		public String getPerceptionReportPath() {
			return perceptionReportPath;
		} //getPerceptionReportPath

        /**
         * Sets the value of the perceptionReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
		public void setPerceptionReportPath(String value) {
			this.perceptionReportPath = value;
		} //setPerceptionReportPath

        /**
         * Gets the value of the perceptionReportPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
		public String getRemissionGuideReportPath() {
            return remissionGuideReportPath;
        } //getRemissionGuideReportPath

        /**
         * Sets the value of the perceptionReportPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemissionGuideReportPath(String value) {
            this.remissionGuideReportPath = value;
        } //setRemissionGuideReportPath

    } //Pdf

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Tipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "tipo"
    })
    public static class PFEConnector {

        @XmlElement(name = "Tipo", required = true)
        protected String tipo;

        /**
         * Gets the value of the tipo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipo() {
            return tipo;
        } //getTipo

        /**
         * Sets the value of the tipo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipo(String value) {
            this.tipo = value;
        } //setTipo

    } //PFEConnector

} //Configuracion
