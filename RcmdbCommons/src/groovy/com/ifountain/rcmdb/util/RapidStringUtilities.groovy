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
    private RapidStringUtilities() {}
    public static void registerStringUtils()
    {
        String.metaClass.methodMissing = {java.lang.String methodName, params ->
            def newParams = new ArrayList(InvokerHelper.asList(params));
            newParams.add(0, delegate);
            try
            {
                return StringUtils.metaClass.invokeStaticMethod(StringUtils, methodName, newParams as Object[]);
            }
            catch (MissingMethodException e1)
            {
                try {
                    return RapidStringUtilities.metaClass.invokeStaticMethod(RapidStringUtilities, methodName, newParams as Object[]);
                }
                catch (MissingMethodException e2) {
                    throw new MissingMethodException(methodName, delegate.class, params);
                }

            }
        }
    }

    public static String toASCII(String s, char placeholder) {
        byte[] value = s.getBytes();
        StringBuffer buf = new StringBuffer(value.length);
        for (int i = 0; i < value.length; i++) {
            if ((Character.isISOControl((char) value[i])) ||
                    ((value[i] & 0xFF) >= 0x80)) {
                buf.append(placeholder);
            }
            else {
                buf.append((char) value[i]);
            }
        }
        return buf.toString();
    }

    public static String toASCII(String s) {
        byte[] value = s.getBytes();
        StringBuffer buf = new StringBuffer(value.length);
        for (int i = 0; i < value.length; i++) {
            if (!(Character.isISOControl((char) value[i])) && !((value[i] & 0xFF) >= 0x80)) {
                buf.append((char) value[i]);
            }
        }
        return buf.toString();
    }

}

