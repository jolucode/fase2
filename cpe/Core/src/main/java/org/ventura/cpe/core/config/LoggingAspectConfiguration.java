package org.ventura.cpe.core.config;

import org.springframework.core.env.Environment;
import org.ventura.cpe.core.logging.LoggingAspect;

//@Configuration
//@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    //    @Bean
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}
