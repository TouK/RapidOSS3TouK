package com.ifountain.comp.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<Object, ConverterHolder> converters;
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
        converters = new HashMap<Object, ConverterHolder>();
        unregisterAll();
    }
    public void unregisterAll()
    {
        converters.clear();
        defaultConverter = null;
    }
    public void setDefaultConverter(Converter converter)
    {
        defaultConverter = converter;
    }
    public void register(Class cls, Converter converter)
    {
        ConverterHolder holder = (ConverterHolder)converters.get(cls);
        if(holder == null || !holder.ownerClass.equals(cls))
        {
            List entrySet = new ArrayList(converters.entrySet());
            for(int i=0; i < entrySet.size(); i++)
            {
                Map.Entry entry = (Map.Entry)entrySet.get(i);
                Class registeredCls = (Class)entry.getKey();
                ConverterHolder registeredHolder = (ConverterHolder)entry.getValue();
                if(!registeredHolder.ownerClass.equals(registeredCls) && cls.isAssignableFrom(registeredCls))
                {
                    converters.remove(registeredCls);
                }
            }
            converters.put(cls, new ConverterHolder(cls, converter));
        }
        else
        {
            holder.converter = converter;
        }
    }

    public Object convert(Object objectToBeConverted) throws Exception {
        Converter converter = defaultConverter;
        if(objectToBeConverted != null)
        {
            converter = lookup(objectToBeConverted.getClass());
        }
        if(converter != null)
        {
            return converter.convert(objectToBeConverted);
        }
        else
        {
            throw new Exception("No converter is defined for "+objectToBeConverted.getClass());
        }
    }

    public Converter lookup(Class classToBeConverted)
    {
        ConverterHolder converterHolder = lookupParent(classToBeConverted, true);
        if(converterHolder == null)
        {
            return defaultConverter;
        }
        else
        {
            return converterHolder.converter;
        }
    }

    private ConverterHolder lookupParent(Class classToBeConverted, boolean isFirstLevel)
    {
        ConverterHolder converterHolder= converters.get(classToBeConverted);
        if(converterHolder != null) return converterHolder;
        Class parentClass = classToBeConverted.getSuperclass();
        if(parentClass != null)
        {
            converterHolder = lookupParent(parentClass,false);
        }
        if(converterHolder == null)
        {
            Class[] interfaces = classToBeConverted.getInterfaces();
            for(int i=0; i < interfaces.length && converterHolder == null; i++)
            {
                converterHolder = lookupParent(interfaces[i], false);
            }
        }
        if(converterHolder != null && isFirstLevel)
        {
            converters.put(classToBeConverted, converterHolder);
        }
        else if(converterHolder == null && isFirstLevel && classToBeConverted.isArray())
        {
            converterHolder = converters.get(Object[].class);
            if(converterHolder != null)
            {
                converters.put(classToBeConverted, converterHolder);
            }
        }
        return converterHolder;
    }

    class ConverterHolder{
        Converter converter;
        Class ownerClass;
        public ConverterHolder(Class ownerClass, Converter converter)
        {
            this.ownerClass = ownerClass;
            this.converter = converter;
        }
    }
}

