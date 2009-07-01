package com.ifountain.rcmdb.converter.datasource

import com.ifountain.comp.converter.ConverterRegistry
import com.ifountain.comp.converter.Converter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 2:56:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatasourceConversionUtils
{

    public static ConverterRegistry getRegistry()
    {
        return ConverterRegistry.getInstance();
    }
    public static void registerDefaultConverters()
    {
        registerDefaultConverters([Integer, int, short, Short, Long, long, byte, Byte, BigInteger], Long);
        registerDefaultConverters([float, Float, double, Double, BigDecimal], Double);
        registerDefaultConverters([Boolean, boolean], Boolean);
        register (Date, new NotConvertingConverter());
        register (List, new ListConverter());
        register (Object[].class, new ListConverter());
        register (Map, new MapConverter());

        ConverterRegistry.getInstance().setDefaultConverter(new StringConverter())
    }
    public static void register(Class cls, Converter converter)
    {
        ConverterRegistry.getInstance().register(cls, converter);
    }
    public static void register(Class cls, Closure converter)
    {
        ConverterRegistry.getInstance().register(cls, new ClosureConverter(converter));
    }

    private static registerDefaultConverters(List fromClasses, Class toClass)
    {
        fromClasses.each{Class fromClass->
            register(fromClass, new DefaultConverter(fromClass, toClass));
        }
    }
}