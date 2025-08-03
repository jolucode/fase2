package org.ventura.cpe.extractor.tasks;

import com.sap.smb.sbo.api.ICompany;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.extractor.sbo.SapBOExtractor;
import org.ventura.cpe.extractor.sbo.SapBOResumenExtractor;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ExtractorTaskInitializer {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final SAPBOService sapboService;

    private final SapBOExtractor extractor;

    private final SapBOResumenExtractor resumenExtractor;

    @Value("${configuracion.erp.tipoServidor}")
    private String tipoServidor;

    @Value("${configuracion.tiempos.rqInterval}")
    private Long tiempoExtraccion;

    @Value("${configuracion.resumenDiario.fecha}")
    private String fecha;

    @Value("${configuracion.resumenDiario.hora}")
    private String hora;

    private int posicion;

    @PostConstruct
    public void schedulePeriodicTask() {
        boolean isConnected = sapboService.conectarSociedad();
        if (isConnected) {
            HashMap<String, ICompany> sociedadesMap = SAPBOService.getSociedades();
            taskScheduler.setPoolSize(sociedadesMap.size());
            sociedadesMap.forEach((key, sociedad) -> {
                boolean isHana = tipoServidor.equalsIgnoreCase("9");
                String pattern = isHana ? "yyyy-MM-dd" : "yyyyMMdd";
                ExtractorTask extractorTask = new ExtractorTask(sociedad, extractor);
                PeriodicTrigger periodicTrigger = new PeriodicTrigger(tiempoExtraccion, TimeUnit.SECONDS);
                periodicTrigger.setFixedRate(true);
                taskScheduler.schedule(extractorTask, periodicTrigger);
                ResumenTrigger resumenTrigger = createResumenTrigger();
                Optional<String> optional = Optional.ofNullable(fecha).map(s -> s.isEmpty() ? null : s);
                ExtractorResumenTask extractorResumenTask = new ExtractorResumenTask(sociedad, resumenExtractor, optional, pattern, posicion);
                taskScheduler.schedule(extractorResumenTask, resumenTrigger);
                posicion++;
            });
        }
    }

    private ResumenTrigger createResumenTrigger() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        TemporalAccessor parse = formatter.parse(hora);
        LocalTime localTime = LocalTime.from(parse);
        LocalDate hoy = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(hoy, localTime);
        int segundosRetraso = Math.max(tiempoExtraccion.intValue(), 300);
        return new ResumenTrigger(segundosRetraso, localDateTime);
    }
}
