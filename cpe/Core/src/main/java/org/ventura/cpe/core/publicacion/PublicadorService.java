package org.ventura.cpe.core.publicacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.PublicardocWs;
import org.ventura.cpe.core.repository.PublicardocRepository;
import org.ventura.cpe.core.util.DocumentoPublicado;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicadorService {

    private final AppProperties properties;

    private final PublicardocRepository publicardocRepository;

    private boolean hasError, isSuccess, is400Status;

    public void publicar(PublicardocWs docPublicar) {
        System.out.println();
        try {
            String usuario = properties.getWebService().getWsUsuario();
            String clave = properties.getWebService().getWsClave();
            String location = properties.getWebService().getWsLocation();
            DocumentoPublicado documentoPublicado = new DocumentoPublicado(usuario, clave, docPublicar);
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.newConnection().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
            ClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);
            WebClient webClient = WebClient.builder().clientConnector(httpConnector).filter(logResponse()).baseUrl(location).build();
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
                    });
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
