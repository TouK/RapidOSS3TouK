package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.Converter
import com.ifountain.comp.converter.ConverterRegistry
import java.util.Map.Entry

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 5:14:19 PM
 * To change this template use File | Settings | File Templates.
 */
class MapConverter implements Converter{

    public Object convert(Object map) {
        Set entries = ((Map)map).entrySet();
        entries.each{Entry entry->
            entry.setValue(ConverterRegistry.getInstance().convert(entry.getValue()));
        }
        return map;
    }

}