package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.Converter
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.apache.commons.beanutils.ConversionException
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 10:33:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultConverter implements Converter
{
    private Class from;
    private Class to;
    private org.apache.commons.beanutils.Converter apacheConverter;
    public DefaultConverter(Class from, Class to)
    {
        this.from = from;
        this.to = to;
        apacheConverter = RapidConvertUtils.getInstance().lookup(to);
    }

    public Object convert(Object value) {
        if(apacheConverter != null)
        {
            return apacheConverter.convert(from, value);
        }
        else
        {
            throw new ConversionException("No converter exists to convert ${from.name} to ${to.name}".toString());
        }
    }

}