package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.Converter
import java.text.SimpleDateFormat
import org.apache.commons.beanutils.ConversionException

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 10:04:30 AM
* To change this template use File | Settings | File Templates.
*/
class DateConverter implements Converter{
    SimpleDateFormat formater;
    String format;
    public DateConverter(String format)
    {
        this.format = format;
        formater = new SimpleDateFormat(format);
    }
    public Object convert(Class aClass, Object o) {
        if(o == null) return null;
        if(o instanceof Date) return o;
        if(String.valueOf(o) == "") return null;
        try
        {
            return formater.parse(o); //To change body of implemented methods use File | Settings | File Templates.
        }
        catch(java.text.ParseException e)
        {
            throw new ConversionException (e.getMessage()) ;          
        }
    }

}