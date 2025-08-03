package org.ventura.cpe.extractor.tasks;

import com.sap.smb.sbo.api.ICompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ventura.cpe.extractor.sbo.SapBOExtractor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class ExtractorTask implements Runnable {

    private final ICompany sociedad;

    private final SapBOExtractor extractor;

    @Override
    public void run() {
        LocalTime ahora = LocalTime.now();
        log.info("Hora Actual: {}", ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        extractor.extraerTransacciones(sociedad);
        System.out.println();
    }
}
