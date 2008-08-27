package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.Converter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 4:47:22 PM
 * To change this template use File | Settings | File Templates.
 */
class BooleanConverter implements Converter{
    org.apache.commons.beanutils.converters.BooleanConverter converter;
    public BooleanConverter()
    {
        converter = new org.apache.commons.beanutils.converters.BooleanConverter();
    }
    public Object convert(Class aClass, Object o) {
        if(String.valueOf(o) == "") return null;
        return converter.convert(aClass, o);
    }

}