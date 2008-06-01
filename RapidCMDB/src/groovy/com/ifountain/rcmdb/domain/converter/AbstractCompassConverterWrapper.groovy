package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.Converter
import org.compass.core.config.CompassConfigurable
import org.compass.core.Resource
import org.compass.core.mapping.Mapping
import org.compass.core.marshall.MarshallingContext
import org.compass.core.config.CompassSettings
import org.compass.core.converter.mapping.ResourcePropertyConverter
import org.compass.core.mapping.ResourcePropertyMapping
import org.compass.core.Property.Index
import org.compass.core.Property.TermVector
import org.compass.core.Property.Store

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:21:35 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractCompassConverterWrapper  implements Converter, CompassConfigurable, ResourcePropertyConverter{
    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) {
        return getConverter().marshall(resource, o, mapping, marshallingContext);
    }
    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) {
        try
        {
            return getConverter().unmarshall(resource, mapping, marshallingContext);
        }
        catch(Throwable e)
        {
            return getDefaultValue();
        }
    }
    public void configure(CompassSettings compassSettings)
    {
        ((CompassConfigurable)getConverter()).configure (compassSettings);    
    }

    public Object fromString(String s, ResourcePropertyMapping resourcePropertyMapping) {
        return ((ResourcePropertyConverter)getConverter()).fromString (s, resourcePropertyMapping);
    }

    public String toString(Object o, ResourcePropertyMapping resourcePropertyMapping) {
        return ((ResourcePropertyConverter)getConverter()).toString (o, resourcePropertyMapping);
    }

    public boolean canNormalize() {
        return ((ResourcePropertyConverter)getConverter()).canNormalize ();
    }

    public Index suggestIndex() {
        return ((ResourcePropertyConverter)getConverter()).suggestIndex ();
    }

    public TermVector suggestTermVector() {
        return ((ResourcePropertyConverter)getConverter()).suggestTermVector ();
    }

    public Store suggestStore() {
        return ((ResourcePropertyConverter)getConverter()).suggestStore ();
    }

    public Boolean suggestOmitNorms() {
        return ((ResourcePropertyConverter)getConverter()).suggestOmitNorms ();
    }

    protected abstract Converter getConverter();
    protected abstract Object getDefaultValue();
}