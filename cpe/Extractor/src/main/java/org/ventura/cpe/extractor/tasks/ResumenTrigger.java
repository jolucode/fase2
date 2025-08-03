package org.ventura.cpe.extractor.tasks;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class ResumenTrigger implements Trigger {

    private final int segundosRetraso;

    private final LocalDateTime fechaReferencia;

    public ResumenTrigger(int segundosRetraso, LocalDateTime fechaReferencia) {
        this.segundosRetraso = segundosRetraso;
        this.fechaReferencia = fechaReferencia;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime fechaNoche = LocalDate.now().atTime(23, 59);
            LocalTime tiempoReferencia = fechaReferencia.toLocalTime();
            LocalTime timeNoche = fechaNoche.toLocalTime();
            LocalTime tiempoLocal = localDateTime.toLocalTime();
            if (tiempoLocal.isAfter(tiempoReferencia) && tiempoLocal.isBefore(timeNoche)) {
                LocalDateTime dateTime = localDateTime.plusSeconds(segundosRetraso);
                return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
            LocalDateTime tiempoProximaEjecucion = LocalDate.now().atTime(tiempoReferencia);
            return Date.from(tiempoProximaEjecucion.atZone(ZoneId.systemDefault()).toInstant());
        }
        LocalTime tiempoReferencia = fechaReferencia.toLocalTime();
        LocalTime tiempoAhora = LocalTime.now();
        if (tiempoAhora.isAfter(tiempoReferencia)) {
            LocalDateTime ahora = LocalDate.now().atTime(tiempoAhora);
            return Date.from(ahora.atZone(ZoneId.systemDefault()).toInstant());
        }
        return Date.from(fechaReferencia.atZone(ZoneId.systemDefault()).toInstant());
    }
}
