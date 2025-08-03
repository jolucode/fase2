package org.ventura.cpe.main.config;

import ventura.soluciones.commons.config.IUBLConfig;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws DatatypeConfigurationException {
        LocalDate issueDateValue = LocalDate.parse("2020-02-173", DateTimeFormatter.ofPattern(IUBLConfig.ISSUEDATE_FORMAT));
        DatatypeFactory datatypeFact = DatatypeFactory.newInstance();
        System.out.println(datatypeFact.newXMLGregorianCalendar(issueDateValue.toString()));
        System.out.println(issueDateValue);
    }
}
