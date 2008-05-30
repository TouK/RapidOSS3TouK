package com.ifountain.rcmdb.domain.converter;

import org.compass.core.converter.Converter;
import org.compass.core.converter.ConversionException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.Resource;
import org.compass.core.CompassException;
import org.compass.core.Compass;
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
public class CompassDateConverter implements Converter, CompassConfigurable
{
    org.compass.core.converter.basic.DateConverter dateConverter = new org.compass.core.converter.basic.DateConverter();
    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) throws ConversionException {
        return dateConverter.marshall(resource, o, mapping, marshallingContext);
    }

    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) throws ConversionException {
        try
        {
            return  dateConverter.unmarshall(resource, mapping, marshallingContext);
        }
        catch(Exception e)
        {
            return new Date(0);
        }
    }

    public void configure(CompassSettings compassSettings) throws CompassException {
        dateConverter.configure(compassSettings);
    }

}
