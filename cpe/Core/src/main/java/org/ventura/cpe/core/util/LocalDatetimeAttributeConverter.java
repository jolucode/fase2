package org.ventura.cpe.core.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.*;

@Converter(autoApply = true)
public class LocalDatetimeAttributeConverter implements AttributeConverter<LocalDateTime, Date> {

    public static final ZoneId ZONE_AMERICA_LIMA = ZoneId.of("America/Lima");

    @Override
    public Date convertToDatabaseColumn(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // Untested, probably something like this
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_AMERICA_LIMA);
        LocalDate producerLocalDate = zonedDateTime.toLocalDate();
        return Date.valueOf(producerLocalDate);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        // Fixed implementation considering server timezone
        Instant instant = Instant.ofEpochMilli(sqlDate.getTime());
        return LocalDateTime.ofInstant(instant, ZONE_AMERICA_LIMA);
    }
}
