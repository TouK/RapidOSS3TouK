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
        MetaClass tempCls = String.metaClass;
        StringUtils.metaClass.getMethods().each{MetaMethod method->
            if(tempCls.getMetaMethod(method.name, method.getNativeParameterTypes()) == null)
            {
                tempCls."${method.name}" = {args->
                    List newArgs = new ArrayList(InvokerHelper.asList (args));
                    newArgs.add (0, delegate);
                    return method.invoke(StringUtils, newArgs as Object[]);
                }
            }
        }
     }
}