package org.ventura.cpe.utils;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final XMLGregorianCalendar stringDateToDateGregory(Date date){
        XMLGregorianCalendar xmlDate = null;
        try{
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(date));

        }catch (Exception e){
            System.out.println("Este es error en XMLGregorian -> " + e.getMessage());
        }
        return xmlDate;
    }
}
