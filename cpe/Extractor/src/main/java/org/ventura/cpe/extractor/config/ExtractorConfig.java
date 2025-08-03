package org.ventura.cpe.extractor.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.ventura.cpe.core.repository")
public class ExtractorConfig {

    @Bean(name = "messageSource")
    public MessageSource configureMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:query/queries");
        messageSource.setCacheSeconds(0);
        messageSource.setAlwaysUseMessageFormat(true);
        return messageSource;
    }
}
