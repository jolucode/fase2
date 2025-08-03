package org.ventura.cpe.extractor.sbo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class VSFactory {

    private final MessageSource messageSource;

    public String getQuery(int numero, String tipo, String... parametros) {
        switch (tipo) {
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "10":
            case "11":
            case "15":
                tipo = "MSSQL";
                break;
            case "9":
                tipo = "HANA";
                break;
            default:
                tipo = "HANA";
                break;
        }
        String queryNumber = numero < 10 ? "000" + numero : "00" + numero;
        String queryFormat = messageSource.getMessage("queryFormat", new Object[]{queryNumber, tipo}, Locale.getDefault());
        String query = messageSource.getMessage(queryFormat, parametros, Locale.getDefault());
        log.info(query);
        return query;
    }
}
