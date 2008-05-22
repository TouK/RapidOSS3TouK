package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 10:57:51 AM
* To change this template use File | Settings | File Templates.
*/
class LongConverter implements Converter{
    org.apache.commons.beanutils.converters.LongConverter converter;
    public LongConverter()
    {
         converter = new org.apache.commons.beanutils.converters.LongConverter();        
    }
    public Object convert(Class aClass, Object o) {
        if(String.valueOf(o) == "") return null;
        return converter.convert(aClass, o);
    }
    
}