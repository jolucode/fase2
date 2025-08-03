package org.ventura.cpe.extractor.tasks;

import com.sap.smb.sbo.api.ICompany;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ventura.cpe.extractor.sbo.SapBOResumenExtractor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ExtractorResumenTask implements Runnable {

    private final ICompany sociedad;

    private final SapBOResumenExtractor extractor;

    @NonNull
    private final Optional<String> optionalFecha;

    @NonNull
    private final String pattern;

    @NonNull
    private final int posicion;

    @Override
    public void run() {
        String fechaEmisionLocal;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaEmision = optionalFecha.orElse("");
        if (optionalFecha.isPresent()) {
            LocalDate localDate = LocalDate.parse(fechaEmision, formatter);
            LocalDate fechaHoy = LocalDate.parse(fechaEmision, formatter);
            do {
                fechaEmisionLocal = localDate.format(DateTimeFormatter.ofPattern(pattern));
                log.info("Extrayendo resumen diario {}", fechaEmision);
                LocalTime ahora = LocalTime.now();
                log.info("Hora Actual Resumen: {}", ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                extractor.extraerResumenDiario(sociedad, fechaEmisionLocal, posicion);
                localDate = localDate.plusDays(1);
            } while (localDate.isBefore(fechaHoy) || localDate.isEqual(fechaHoy));
        } else {
            LocalDate localDate = LocalDate.now();
            fechaEmision = localDate.format(formatter);
            fechaEmisionLocal = localDate.format(DateTimeFormatter.ofPattern(pattern));
            log.info("Extrayendo resumen diario {}", fechaEmision);
            LocalTime ahora = LocalTime.now();
            log.info("Hora Actual Resumen: {}", ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            extractor.extraerResumenDiario(sociedad, fechaEmisionLocal, posicion);
        }
    }
}
