package org.ventura.cpe.core.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class DateAttributeConverter implements AttributeConverter<java.util.Date, Date> {

    public static final ZoneId ZONE_AMERICA_LIMA = ZoneId.of("America/Lima");

    @Override
    public Date convertToDatabaseColumn(java.util.Date fecha) {
        if (fecha == null) {
            return null;
        }
        // Untested, probably something like this
        LocalDate localDate = convertToLocalDate(fecha);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_AMERICA_LIMA);
        LocalDate producerLocalDate = zonedDateTime.toLocalDate();
        return Date.valueOf(producerLocalDate);
    }

    @Override
    public java.util.Date convertToEntityAttribute(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(sqlDate.getTime());
        return java.util.Date.from(instant);
    }

    private LocalDate convertToLocalDate(java.util.Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
