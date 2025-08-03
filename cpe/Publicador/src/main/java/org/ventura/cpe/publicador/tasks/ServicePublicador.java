/* Decompiler 30ms, total 143ms, lines 103 */
package org.ventura.cpe.publicador.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.BdsMaestras;
import org.ventura.cpe.core.domain.PublicardocWs;
import org.ventura.cpe.core.publicacion.PublicacionResponse;
import org.ventura.cpe.core.repository.PublicardocRepository;
import org.ventura.cpe.core.util.DocumentoPublicado;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePublicador {

    private final AppProperties properties;

    private final PublicardocRepository publicardocRepository;

    private boolean hasError, isSuccess, is400Status;

    public boolean containRuc(List<BdsMaestras> listSociedades, String ruc){
        return listSociedades.stream().anyMatch(item -> item.getRucSociedad().equals(ruc));
    }
    public void publicar(PublicardocWs docPublicar, List<BdsMaestras> listSociedades) {
        try {
            String usuario = "";
            String clave = "";
            String location = "";

            if((listSociedades != null || listSociedades.size() > 0) && containRuc(listSociedades, docPublicar.getRSRuc())){
                Optional<BdsMaestras> optionalBd = listSociedades.stream().filter(x -> x.getRucSociedad().equals(docPublicar.getRSRuc())).findAny();
                if(optionalBd.isPresent()){
                    BdsMaestras BdsMaestras = optionalBd.get();
                    usuario =BdsMaestras.getUsuarioWebService();
                    clave = BdsMaestras.getPasswordWebService();
                    location = BdsMaestras.getWebServicePublicacion();
                }
            } else {
                usuario = properties.getWebService().getWsUsuario();
                clave = properties.getWebService().getWsClave();
                location = properties.getWebService().getWsLocation();

            }
            DocumentoPublicado documentoPublicado = new DocumentoPublicado(usuario, clave, docPublicar);
            documentoPublicado.encodeFiles();
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.newConnection().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
            ClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);
            ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(64 * 1024 * 1024)).build();
            WebClient webClient = WebClient.builder().clientConnector(httpConnector).exchangeStrategies(exchangeStrategies).filter(logResponse()).baseUrl(location).build();
            Mono<String> stringMono = webClient.post().uri("/api/publicar").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                    bodyValue(documentoPublicado)
                    .exchange()
                    .flatMap(clientResponse -> {
                        Mono<String> bodyToMono = Mono.from(clientResponse.bodyToMono(String.class));
                        if (clientResponse.statusCode().is5xxServerError()) {
                            clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody());
                            hasError = true;
                            return bodyToMono;
                        } else if (clientResponse.statusCode().is4xxClientError()) {
                            is400Status = true;
                            clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody());
                            return bodyToMono;
                        } else if (clientResponse.statusCode().is2xxSuccessful()) {
                            isSuccess = true;
                            return clientResponse.bodyToMono(String.class);
                        } else {
                            return clientResponse.bodyToMono(String.class);
                        }
                    })
                    .doOnError(throwable -> log.error(throwable.getLocalizedMessage()));
            String result = stringMono.block();
            assert result != null;
            Optional<PublicacionResponse> optional = this.handleResponse(result);
            if (optional.isPresent()) {
                PublicacionResponse publicacionResponse = optional.get();
                if (isSuccess) {
                    log.info(publicacionResponse.getMensaje());
                    docPublicar.setEstadoPublicacion('B');
                    publicardocRepository.saveAndFlush(docPublicar);
                } else if (is400Status) {
                    log.error(publicacionResponse.getError());
                } else {
                    log.error(publicacionResponse.getError());
                }
            } else if (hasError) {
                docPublicar.setEstadoPublicacion('B');
                publicardocRepository.saveAndFlush(docPublicar);
                log.error(result);
            }
            isSuccess = false;
        } catch (Exception e) {
            isSuccess = false;
            log.error(e.getLocalizedMessage());
        }
    }

    private Optional<PublicacionResponse> handleResponse(String result) {
        try {
            ObjectMapper objectMapper = new JsonMapper();
            int indexOf = result.indexOf("{");
            String cadenaLimpia = result.substring(indexOf);
            return Optional.ofNullable(objectMapper.readValue(cadenaLimpia, PublicacionResponse.class));
        } catch (JsonProcessingException e) {
            log.error(e.getLocalizedMessage());
        }
        log.info(result);
        return Optional.empty();
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
