package com.ifountain.rcmdb.domain.converter.datasource
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 12:01:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotConvertingConverter implements Converter{

    public Object convert(Object value) {
        return value;
    }

}