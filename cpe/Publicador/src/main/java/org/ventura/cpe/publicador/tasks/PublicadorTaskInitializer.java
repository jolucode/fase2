package org.ventura.cpe.publicador.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.repository.BdsMaestrasRepository;
import org.ventura.cpe.core.repository.PublicardocRepository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PublicadorTaskInitializer {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final PublicardocRepository publicardocRepository;

    private final BdsMaestrasRepository bdsMaestrasRepository;

    private final ServicePublicador publicadorService;

    private final AppProperties properties;

    @PostConstruct
    public void schedulePeriodicTask() {
        Integer tiempoPublicacion = properties.getWebService().getWsIntervaloRepeticionPublic();
        PublicadorTask publicadorTask = new PublicadorTask(publicardocRepository, bdsMaestrasRepository, publicadorService);
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(tiempoPublicacion, TimeUnit.SECONDS);
        periodicTrigger.setFixedRate(true);
        taskScheduler.schedule(publicadorTask, periodicTrigger);
    }
}
