package com.ifountain.rcmdb.domain.converter.datasource

import com.ifountain.rcmdb.domain.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 2:03:13 PM
 * To change this template use File | Settings | File Templates.
 */
class StringConverter implements Converter{
    org.apache.commons.beanutils.converters.StringConverter converter = RapidConvertUtils.getInstance().lookup(String.class);
    public Object convert(Object value) {
        return converter.convert(String, value);
    }

}