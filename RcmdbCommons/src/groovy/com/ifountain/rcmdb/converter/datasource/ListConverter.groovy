package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.Converter
import com.ifountain.comp.converter.ConverterRegistry

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 4:45:41 PM
 * To change this template use File | Settings | File Templates.
 */
class ListConverter implements Converter{

    public Object convert(Object value) {
        def newList = [];
        value.each{Object listValue->
            newList.add(ConverterRegistry.getInstance().convert(listValue));            
        }
        return newList;
    }
}