/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.util

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.NullObject
import com.ifountain.compass.utils.QueryParserUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 6, 2008
* Time: 2:44:33 PM
* To change this template use File | Settings | File Templates.
*/
class RapidStringUtilities {
    private RapidStringUtilities() {}
    //this method wil register all apache stringutils methods and all static methods
    //on this classes to string metaclass
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

        NullObject.metaClass.toQuery = {->
            return String.valueOf(null);
        }
        NullObject.metaClass.exactQuery = {->
            return String.valueOf(null).exactQuery();
        }
    }

    public static String exactQuery(String s)
    {
        return QueryParserUtils.toExactQuery (s);
    }
    public static String toQuery(String s)
    {
        return org.apache.lucene.queryParser.QueryParser.escape(s);
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

