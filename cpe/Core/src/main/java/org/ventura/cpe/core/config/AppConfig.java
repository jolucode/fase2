package org.ventura.cpe.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import org.ventura.cpe.core.util.AppFaultMessageResolver;
import org.ventura.cpe.core.util.SoapInterceptor;
import org.ventura.soluciones.sunatws.consumer.Consumer;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AppProperties properties;

    private final AppFaultMessageResolver faultMessageResolver;



    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath("pe.gob.sunat.service.ose_tci");
        return jaxb2Marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        String clienteSunat = properties.getSunat().getClienteSunat();
        String integracionWebService = properties.getSunat().getIntegracionWS();
        String urlWebService;
        if (integracionWebService.equalsIgnoreCase("OSE")) {
            urlWebService = clienteSunat.equalsIgnoreCase("test") ? properties.getSunat().getRutaOseTest() : properties.getSunat().getRutaOseProd();
        } else {
            urlWebService = clienteSunat.equalsIgnoreCase("test") ? properties.getSunat().getRutaSunatTest() : properties.getSunat().getRutaSunatProd();
        }
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        webServiceTemplate.setDefaultUri(urlWebService);
        webServiceTemplate.setInterceptors(new ClientInterceptor[]{soapInterceptor()});
        webServiceTemplate.setCheckConnectionForFault(false);
        webServiceTemplate.setCheckConnectionForError(false);
        webServiceTemplate.setMessageSender(new HttpUrlConnectionMessageSender());
        webServiceTemplate.setFaultMessageResolver(faultMessageResolver);
        return webServiceTemplate;
    }

    @Bean
    public SoapInterceptor soapInterceptor() {
        Consumer consumer = new Consumer("20510910517", "20510910517", "");
        return new SoapInterceptor(consumer);
    }

    @Bean
    public HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setHttpClient(httpClient());
        return httpComponentsMessageSender;
    }

    public HttpClient httpClient() {
        return HttpClientBuilder.create().addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor()).build();
    }


}
