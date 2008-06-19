package com.ifountain.rcmdb.util

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.runtime.InvokerHelper

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 6, 2008
* Time: 2:44:33 PM
* To change this template use File | Settings | File Templates.
*/
class RapidStringUtilities {
     private RapidStringUtilities(){}
     public static void registerStringUtils()
     {
        String.metaClass.methodMissing = {java.lang.String methodName, params->
                def newParams = new ArrayList(InvokerHelper.asList(params));
                newParams.add (0, delegate);
                try
                {
                    return StringUtils.metaClass.invokeStaticMethod(StringUtils, methodName, newParams as Object[]);
                }
                catch(MissingMethodException e)
                {
                    throw new MissingMethodException (methodName, delegate.class, params);
                }
        }
     }

    
}

