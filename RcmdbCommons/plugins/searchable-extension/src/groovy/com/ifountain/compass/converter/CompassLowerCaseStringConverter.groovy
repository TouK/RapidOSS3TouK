package com.ifountain.compass.converter

import org.compass.core.Resource
import org.compass.core.mapping.Mapping
import org.compass.core.marshall.MarshallingContext
import com.ifountain.compass.CompassConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 9, 2009
* Time: 6:05:49 PM
* To change this template use File | Settings | File Templates.
*/
class CompassLowerCaseStringConverter extends CompassStringConverter{

    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) {
        if(o != null)
        {
            if(mapping.getName().startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
            {
                o = String.valueOf(o).toLowerCase();
            }
        }
        return super.marshall(resource, o, mapping, marshallingContext); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Object convertNullProperty(String alias, String propName) {
        Object o = super.convertNullProperty(alias, propName); //To change body of overridden methods use File | Settings | File Templates.
        if(propName.startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
        {
            o =  String.valueOf(o).toLowerCase();
        }
        return o;
    }

}