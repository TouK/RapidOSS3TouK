package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:33:32 PM
* To change this template use File | Settings | File Templates.
*/
class DoubleConverter  implements Converter{
    org.apache.commons.beanutils.converters.DoubleConverter converter;
    public DoubleConverter()
    {
         converter = new org.apache.commons.beanutils.converters.DoubleConverter();
    }
    public Object convert(Class aClass, Object o) {
        if(String.valueOf(o) == "") return null;
        return converter.convert(aClass, o);
    }

}