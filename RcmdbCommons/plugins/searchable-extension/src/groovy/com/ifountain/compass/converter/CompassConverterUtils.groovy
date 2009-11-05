package com.ifountain.compass.converter

import com.ifountain.compass.CompassConstants
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 27, 2009
* Time: 3:10:33 PM
* To change this template use File | Settings | File Templates.
*/
class CompassConverterUtils {
    public static Object getNullPropertyValue(String alias, String propName, Object converterDefaultValue)
    {
        def value = null;
        try{
            value = DomainClassDefaultPropertyValueHolder.getDefaultProperyWithSimpleName(alias, propName);
        }catch(Exception e){/*this exception should be ignored*/}
        if(value == null){
            value = converterDefaultValue;
        }
        return value;
    }
}