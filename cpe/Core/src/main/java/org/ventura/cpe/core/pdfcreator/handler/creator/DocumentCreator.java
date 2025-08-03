package org.ventura.cpe.core.pdfcreator.handler.creator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.PDFReportException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class DocumentCreator {

    protected final String USER_TEMPORARY_PATH = System.getProperty("java.io.tmpdir");


    /**
     * Este metodo recibe como entrada la ruta de un archivo, luego obtiene
     * el archivo de la ruta y lo convierte a bytes.
     *
     * @param filePath La ruta del archivo.
     * @return Retorna un archivo en bytes.
     * @throws PDFReportException
     */
    @SuppressWarnings("resource")
    protected synchronized byte[] convertFileInBytes(String filePath) throws PDFReportException {
        if (log.isDebugEnabled()) {
            log.debug("+convertFileInBytes() filePath: " + filePath);
        }
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("convertFileInBytes() Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            log.error("convertFileInBytes() Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new PDFReportException(IVenturaError.ERROR_15);
        }
    } //convertFileInBytes

} //DocumentCreator
