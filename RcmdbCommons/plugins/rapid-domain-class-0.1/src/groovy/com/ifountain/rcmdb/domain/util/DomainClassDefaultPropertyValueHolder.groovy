package com.ifountain.rcmdb.domain.util

import java.lang.reflect.Field
import com.ifountain.compass.CompassConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 27, 2009
* Time: 11:57:41 AM
* To change this template use File | Settings | File Templates.
*/
class DomainClassDefaultPropertyValueHolder {
    public static final DOMAIN_NOT_FOUND_EXCEPTION_MESSAGE = "Domain class is not defined";
    private static Map classInstances = Collections.synchronizedMap([:]);
    private static Map classSimpleNames = Collections.synchronizedMap([:]);
    public static void initialize(List domainClasses)
    {
        destroy();
        domainClasses.each{Class domainClass->
            Map fields = [:]
            getDeclaredFields(domainClass, fields)

            def instance = domainClass.newInstance();
            def propDefaultValues = [:]
            def fieldNames = [:]
            fields.each{fieldName, field->
                propDefaultValues[fieldName] = instance[fieldName];                
                fieldNames[fieldName] = fieldName;
                def untokenizedFieldName = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+fieldName;
                fieldNames[untokenizedFieldName] = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+fieldName;
                propDefaultValues[untokenizedFieldName] = instance[fieldName];
            }
            classInstances[domainClass.name] = [fields:fieldNames,instance:propDefaultValues, instanceClass:domainClass];
            classSimpleNames[domainClass.simpleName] = domainClass.name;
        }
    }

    private static getDeclaredFields(Class cls, Map fieldMap)
    {
        if(cls.superclass != null)
        {
            getDeclaredFields (cls.superclass, fieldMap);
        }
        cls.getDeclaredFields().each{
            it.setAccessible (true);
            fieldMap[it.name] = it;
        }
    }
    
    public static Object getDefaultProperyWithSimpleName(String simpleName, String propName)
    {
        return getDefaultPropery (classSimpleNames[simpleName], propName);
    }
    public static Object getDefaultPropery(String className, String propName)
    {
        def classConfig = classInstances[className];

        if(classConfig != null)
        {
            def instance =  classConfig.instance;
            if(classConfig.fields[propName] == null)
            {
                throw new MissingPropertyException(propName, classConfig.instanceClass)
            }
            return instance[propName];
        }
        else {
            throw new Exception(DOMAIN_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }
    
    public static void destroy()
    {
        classInstances.clear();
    }
}