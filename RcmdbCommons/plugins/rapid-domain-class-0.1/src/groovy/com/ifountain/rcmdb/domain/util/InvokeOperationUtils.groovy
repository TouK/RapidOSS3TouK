package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 18, 2009
* Time: 10:45:43 AM
* To change this template use File | Settings | File Templates.
*/
class InvokeOperationUtils {
    public static Object invokeMethod(domainObject, String name, args, operationClass, domainOperationMethods)
    {
        if(willProcess(name, operationClass, domainOperationMethods))
        {
                def oprInstance = domainObject[RapidCMDBConstants.OPERATION_PROPERTY_NAME];
                if(oprInstance == null)
                {
                    oprInstance = operationClass.newInstance() ;
                    operationClass.metaClass.getMetaProperty("domainObject").setProperty(oprInstance, domainObject);
                    domainObject.setProperty(RapidCMDBConstants.OPERATION_PROPERTY_NAME, oprInstance);
                }
                try {
                    return oprInstance.invokeMethod(name, args)
                } catch (MissingMethodException e) {
                    processMissingMethodException(e, operationClass, name);
                }
        }
        throw new MissingMethodException (name,  domainObject.class, args);
    }

    private static boolean willProcess(String methodName, operationClass, domainOperationMethods)
    {
        return operationClass != null && domainOperationMethods.containsKey(methodName);
    }

    private static void processMissingMethodException(MissingMethodException e, operationClass, methodName)
    {
        if(e.getType().name != operationClass.name || e.getMethod() != methodName)
        {
            throw e;
        }
    }

    public static Object invokeStaticMethod(Class domainClass, String methodName, args, operationClass, domainOperationMethods)
    {
        if(willProcess(methodName, operationClass, domainOperationMethods))
        {
            try {
                return operationClass."${methodName}"(*args)
            } catch (MissingMethodException e) {
                processMissingMethodException(e, operationClass, methodName);
            }
        }
        throw new MissingMethodException (methodName,  domainClass, args);
    }
}