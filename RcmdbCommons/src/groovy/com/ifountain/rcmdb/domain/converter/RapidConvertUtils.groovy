package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.ConvertUtilsBean
import org.apache.commons.beanutils.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 2:35:48 PM
* To change this template use File | Settings | File Templates.
*/
class RapidConvertUtils extends ConvertUtilsBean{
    private static RapidConvertUtils convertUtils;
    private Map converters;
    public RapidConvertUtils() {
        super(); //To change body of overridden methods use File | Settings | File Templates.
        converters = new HashMap();
    }

    public static RapidConvertUtils getInstance()
    {
        if(convertUtils == null)
        {
            convertUtils = new RapidConvertUtils();
            convertUtils.deregister();
        }
        return convertUtils;
    }

    public void register(Converter converter, Class aClass) {

        if(converters != null)
        {
            converters.put (aClass, converter);
        }
    }

    

    public void deregister(Class aClass) {
        if(converters != null)
        {
            converters.remove(aClass)
        }
    }

    public Converter lookup(Class aClass) {
        if(converters != null)
        {
            return ((Converter) converters.get(aClass));
        }
        return null;
    }

}