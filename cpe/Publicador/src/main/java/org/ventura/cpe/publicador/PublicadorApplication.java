package org.ventura.cpe.publicador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.ventura.cpe"},
        exclude = {RedisRepositoriesAutoConfiguration.class, RedisAutoConfiguration.class})
public class PublicadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicadorApplication.class, args);
    }

}