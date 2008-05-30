package com.ifountain.rcmdb.domain.converter;

import org.compass.core.converter.Converter;
import org.compass.core.converter.ConversionException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.Resource;
import org.compass.core.CompassException;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.mapping.Mapping;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 29, 2008
 * Time: 5:59:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompassLongConverter implements Converter, CompassConfigurable
{
    org.compass.core.converter.basic.LongConverter longConverter = new org.compass.core.converter.basic.LongConverter();
    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) throws ConversionException {
        return longConverter.marshall(resource, o, mapping, marshallingContext);
    }

    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) throws ConversionException {
        try
        {
            return  longConverter.unmarshall(resource, mapping, marshallingContext);
        }
        catch(Exception e)
        {
            return new Long(-1111);
        }
    }

    public void configure(CompassSettings compassSettings) throws CompassException {
        longConverter.configure(compassSettings);
    }

}
