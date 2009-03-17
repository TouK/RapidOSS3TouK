package com.ifountain.rcmdb.domain.converter

import org.compass.core.Resource
import org.compass.core.converter.Converter
import org.compass.core.converter.mapping.ResourcePropertyConverter
import org.compass.core.mapping.Mapping
import org.compass.core.mapping.ResourcePropertyMapping
import org.compass.core.marshall.MarshallingContext
import com.ifountain.compass.CompassConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 13, 2009
* Time: 11:40:42 AM
* To change this template use File | Settings | File Templates.
*/
class CompassStringConverter extends AbstractCompassConverterWrapper{
    public static final String EMPTY_VALUE = "__rsempty__";
    org.compass.core.converter.basic.StringConverter stringConverter;
    public CompassStringConverter()
    {
        stringConverter = new org.compass.core.converter.basic.StringConverter();
    }

    protected Converter getConverter() {
        return stringConverter; //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) {
        Object objectToBePassed = o;
        if(objectToBePassed == null || String.valueOf(objectToBePassed).trim().length() == 0)
        {
            objectToBePassed = EMPTY_VALUE;
        }
        if(mapping.getName().startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
        {
            objectToBePassed = String.valueOf(objectToBePassed).toLowerCase();
        }
        return super.marshall(resource, objectToBePassed, mapping, marshallingContext);
    }

    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) {
        Object value = super.unmarshall(resource, mapping, marshallingContext); //To change body of overridden methods use File | Settings | File Templates.
        if(value == EMPTY_VALUE)
        {
            value = "";
        }
        return value;
    }


    protected Object getDefaultValue() {
        return "";
    }


}