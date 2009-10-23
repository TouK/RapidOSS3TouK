package com.ifountain.rcmdb.domain.method

import org.apache.log4j.Logger
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 23, 2009
* Time: 11:39:16 AM
* To change this template use File | Settings | File Templates.
*/
class AsStringMapMethod extends AbstractRapidDomainMethod{
    AsMapMethod asMapMethod;
    public AsStringMapMethod(MetaClass mc, Class domainClass, Logger logger) {
        super(mc)
        asMapMethod = new AsMapMethod(mc,domainClass, logger);
    }

    public Object invoke(Object domainObject, Object[] arguments) {
        def stringConverter = RapidConvertUtils.getInstance().lookup(String);
        def tmpPropertyMap = asMapMethod.invoke(domainObject, arguments)
        def propertyMap = [:]
        tmpPropertyMap.each{propName, propvalue->
            propertyMap[propName] = stringConverter.convert(String, propvalue)
        }
        return propertyMap;
    }
}