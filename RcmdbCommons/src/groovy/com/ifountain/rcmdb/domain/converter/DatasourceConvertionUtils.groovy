package com.ifountain.rcmdb.domain.converter

import com.ifountain.rcmdb.domain.converter.datasource.Converter
import com.ifountain.rcmdb.domain.converter.datasource.DefaultConverter
import com.ifountain.rcmdb.domain.converter.datasource.NotConvertingConverter
import com.ifountain.rcmdb.domain.converter.datasource.ClosureConverter
import com.ifountain.rcmdb.domain.converter.datasource.StringConverter;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 24, 2008
 * Time: 5:29:33 PM
 * To change this template use File | Settings | File Templates.
 */
class DatasourceConvertionUtils
{
    private Converter defaultConverter;
    private Map converters = new HashMap();
    private static DatasourceConvertionUtils conversionUtils;
    public static DatasourceConvertionUtils getInstance()
    {
        if(conversionUtils == null)
        {
            conversionUtils = new DatasourceConvertionUtils();
        }
        return conversionUtils;
    }

    private DatasourceConvertionUtils()
    {
        registerDefaultSettings();
    }
    private void registerDefaultSettings()
    {
        registerDefaultConverters([Integer, int, short, Short, Long, long, byte, Byte], Long);
        registerDefaultConverters([float, Float, double, Double], Double);
        registerDefaultConverters([Boolean, boolean], Boolean);
        register (Date, new NotConvertingConverter());
        defaultConverter = new StringConverter()
    }

    private registerDefaultConverters(List fromClasses, Class toClass)
    {
        fromClasses.each{Class fromClass->
            register(fromClass, new DefaultConverter(fromClass, toClass));
        }
    }

    public void setDefaultConverter(Converter converter)
    {
        defaultConverter = converter;
    }
    public void register(Class cls, Converter converter)
    {
        converters[cls] =  converter;
    }
    public void register(Class cls, Closure converter)
    {
        converters[cls] =  new ClosureConverter(converter);
    }

    public Converter lookup(Class classToBeConverted)
    {
        def converter = converters[classToBeConverted];
        while(converter == null  && classToBeConverted != null)
        {
            converter = converters[classToBeConverted];
            classToBeConverted = classToBeConverted.superclass;
        }
        if(converter == null)
        {
            return defaultConverter;
        }
        else
        {
            return converter;
        }
    }
}