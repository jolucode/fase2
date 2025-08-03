package org.ventura.cpe.extractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;
import org.ventura.cpe.extractor.sbo.VSFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExtractorApplicationTests {

    @Autowired
    VSFactory vsFactory;

    @Autowired
    private MessageSource messageSource;

    @Value("${resumenDiario.fecha}")
    private String fecha;

    @Value("${resumenDiario.hora}")
    private String hora;

    @Test
    public void contextLoads() {
//        vsFactory.getQuery(1, "HANA");
//        vsFactory.getQuery(2, "HANA", "13", "15");
//        vsFactory.getQuery(3, "HANA", "13", "18");
//        vsFactory.getQuery(4, "HANA", "13", "18", "15");
//        vsFactory.getQuery(5, "HANA", "13", "18", "354");
//        vsFactory.getQuery(6, "HANA", "13", "18");
//        vsFactory.getQuery(7, "HANA", "13", "18");
//        vsFactory.getQuery(8, "HANA", "13", "18");
//        vsFactory.getQuery(9, "HANA", "13", "18");
//        vsFactory.getQuery(10, "HANA", "13", "18");
//        vsFactory.getQuery(11, "HANA", "13", "18");
//        vsFactory.getQuery(12, "HANA", "13", "18");
//        vsFactory.getQuery(13, "HANA", "13", "18");
//        vsFactory.getQuery(14, "HANA", "13", "18");
//        vsFactory.getQuery(15, "HANA", "13", "18", "123");
//        vsFactory.getQuery(16, "HANA", "13", "18");
//        vsFactory.getQuery(17, "HANA", "13");
//        vsFactory.getQuery(18, "HANA", "13", "18");

        //        <second> <minute> <hour> <day-of-month> <month> <day-of-week> <year> <command>
//        CronTrigger cronTrigger = createCronTrigger();
//        System.out.println(cronTrigger.getExpression());
    }

//    private CronTrigger createCronTrigger() {
//        String pattern;
//        LocalDateTime localDateTime;
//        if (fecha.isEmpty()) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//            TemporalAccessor parse = formatter.parse(hora);
//            LocalTime localTime = LocalTime.from(parse);
//            LocalDate hoy = LocalDate.now();
//            localDateTime = LocalDateTime.of(hoy, localTime);
//            pattern = "0 mm HH * * *";
//        } else {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
//            TemporalAccessor parse = formatter.parse(fecha + " " + hora);
//            localDateTime = LocalDateTime.from(parse);
//            pattern = "0 mm/10 HH dd MM *";
//        }
//        DateTimeFormatter cronFormatter = DateTimeFormatter.ofPattern(pattern);
//        return new CronTrigger(cronFormatter.format(localDateTime));
//    }
}
