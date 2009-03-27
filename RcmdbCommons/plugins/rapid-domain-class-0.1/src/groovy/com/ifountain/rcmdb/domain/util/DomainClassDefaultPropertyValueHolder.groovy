package com.ifountain.rcmdb.domain.util
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
            classInstances[domainClass.name] = domainClass.newInstance();
            classSimpleNames[domainClass.simpleName] = domainClass.name;
        }
    }
    
    public static Object getDefaultProperyWithSimpleName(String simpleName, String propName)
    {
        return getDefaultPropery (classSimpleNames[simpleName], propName);
    }
    public static Object getDefaultPropery(String className, String propName)
    {
        def instance = classInstances[className];
        if(instance != null)
        {
            return instance[propName]
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