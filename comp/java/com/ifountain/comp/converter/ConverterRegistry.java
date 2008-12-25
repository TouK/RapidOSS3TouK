package com.ifountain.comp.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 24, 2008
 * Time: 5:29:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConverterRegistry
{
    private Converter defaultConverter;
    private Map<Class, Converter> converters = new HashMap<Class, Converter>();
    private static ConverterRegistry conversionUtils;
    public static ConverterRegistry getInstance()
    {
        if(conversionUtils == null)
        {
            conversionUtils = new ConverterRegistry();
        }
        return conversionUtils;
    }

    private ConverterRegistry()
    {
    }

    public void setDefaultConverter(Converter converter)
    {
        defaultConverter = converter;
    }
    public void register(Class cls, Converter converter)
    {
        converters.put(cls, converter);
    }

    public Converter lookup(Class classToBeConverted)
    {
        Converter converter = converters.get(classToBeConverted);
        while(converter == null  && classToBeConverted != null)
        {
            converter = converters.get(classToBeConverted);
            classToBeConverted = classToBeConverted.getSuperclass();
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