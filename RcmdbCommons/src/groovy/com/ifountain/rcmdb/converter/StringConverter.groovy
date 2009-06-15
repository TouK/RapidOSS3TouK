package com.ifountain.rcmdb.converter

import org.apache.commons.beanutils.Converter
import java.text.SimpleDateFormat
import java.text.DecimalFormat

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 15, 2009
* Time: 3:14:08 PM
* To change this template use File | Settings | File Templates.
*/
class StringConverter implements Converter {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
    SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    DecimalFormat decimalFormat = new DecimalFormat();
    public StringConverter(String dateFormatStr)
    {
        setDateFormat(dateFormatStr);
    }
    public Object convert(Class aClass, Object o) {
        if (o instanceof Date)
        {
            return dateFormat.format (o);
        }
        else if(o instanceof BigDecimal)
        {
            return decimalFormat.format(o)
        }
        return String.valueOf(o); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDateFormat(String dateFormatString)
    {
        dateFormat =  new SimpleDateFormat(dateFormatString);
    }

}