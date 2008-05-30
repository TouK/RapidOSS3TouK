package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.Converter
import org.compass.core.config.CompassConfigurable
import org.compass.core.Resource
import org.compass.core.mapping.Mapping
import org.compass.core.marshall.MarshallingContext
import org.compass.core.config.CompassSettings

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:21:35 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractCompassConverterWrapper  implements Converter, CompassConfigurable{
    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) {
        return getConverter().marshall(resource, o, mapping, marshallingContext);
    }
    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) {
        try
        {
            return getConverter().unmarshall(resource, mapping, marshallingContext);
        }
        catch(Exception e)
        {
            return getDefaultValue();
        }
    }
    public void configure(CompassSettings compassSettings)
    {
        ((CompassConfigurable)getConverter()).configure (compassSettings);    
    }

    protected abstract Converter getConverter();
    protected abstract Object getDefaultValue();
}