package org.ventura.cpe.extractor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"org.ventura.cpe"},
        scanBasePackageClasses = {RSocketServerAutoConfiguration.class, RSocketMessagingAutoConfiguration.class},
        exclude = {RedisRepositoriesAutoConfiguration.class, RedisAutoConfiguration.class})
public class ExtractorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtractorApplication.class, args);
    }

}
