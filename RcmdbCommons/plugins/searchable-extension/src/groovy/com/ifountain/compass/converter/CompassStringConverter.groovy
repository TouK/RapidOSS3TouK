package com.ifountain.compass.converter

import org.compass.core.Resource
import org.compass.core.converter.Converter
import org.compass.core.converter.mapping.ResourcePropertyConverter
import org.compass.core.mapping.Mapping
import org.compass.core.mapping.ResourcePropertyMapping
import org.compass.core.marshall.MarshallingContext
import com.ifountain.compass.CompassConstants
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import org.apache.commons.lang.StringUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 13, 2009
* Time: 11:40:42 AM
* To change this template use File | Settings | File Templates.
*/
class CompassStringConverter extends AbstractCompassConverterWrapper{
    public static final String EMPTY_VALUE = "_e";
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
        String propName = mapping.getName();
        if(o != null)
        {
            def objectToBePassedStr = String.valueOf(objectToBePassed);
            if(StringUtils.isBlank(objectToBePassedStr))
            {
                objectToBePassed = EMPTY_VALUE;
            }
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

    protected Object convertNullProperty(String alias, String propName) {
        def value = super.convertNullProperty(alias, propName); //To change body of overridden methods use File | Settings | File Templates.
        if(StringUtils.isBlank(value))
        {
            return EMPTY_VALUE;
        }
        return value;
    }

    protected Object getMarshallingDefaultValue() {
        return EMPTY_VALUE;
    }


}