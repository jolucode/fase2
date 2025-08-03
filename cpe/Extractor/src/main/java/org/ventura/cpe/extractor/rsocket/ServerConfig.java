package org.ventura.cpe.extractor.rsocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

import java.util.Arrays;

@Slf4j
@Configuration
public class ServerConfig {

    @Bean
    public RSocketMessageHandler rsocketMessageHandler(RSocketStrategies rsocketStrategies) {
        log.info("Inicializando RSocketMessageHandler...");
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies);
        return handler;
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.addAll(Arrays.asList(new Jackson2CborEncoder(), new Jackson2JsonEncoder())))
                .decoders(decoders -> decoders.addAll(Arrays.asList(new Jackson2CborDecoder(), new Jackson2JsonDecoder())))
                .routeMatcher(new PathPatternRouteMatcher())
                .build();
    }
}