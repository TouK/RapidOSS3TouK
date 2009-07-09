package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.Converter
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Dec 25, 2008
* Time: 2:03:13 PM
* To change this template use File | Settings | File Templates.
*/
class StringConverter implements Converter{
    org.apache.commons.beanutils.Converter converter = RapidConvertUtils.getInstance().lookup(String.class);
    public Object convert(Object value) {
        if(value == null)
        {
            return null;
        }
        else
        {
            value = converter.convert(String, value);
        }
        return value.trim();
    }

}