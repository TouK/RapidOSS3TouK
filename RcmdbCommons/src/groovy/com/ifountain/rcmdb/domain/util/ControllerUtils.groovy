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
package com.ifountain.rcmdb.domain.util

import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2008
 * Time: 5:49:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ControllerUtils {
    def static convertSuccessToXml(String successMessage)
    {
        StringWriter writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Successful(successMessage.toString());

        return writer.toString();

    }

    def static getClassProperties(Map params, Class domainClass)
    {
        def returnedParams = [:]
        def instance = domainClass.newInstance();
        def domainObjectpropertyNames = [];
        def relations = [];
        def clonedMap = new HashMap();
        params.each{String paramName, paramValue->
            if(paramName!="id" && paramName != "_id")
            {
                if(paramName.startsWith("_"))
                {
                    domainObjectpropertyNames.add(paramName.substring(1));
                }
                else if(paramName.indexOf(".") >= 0)
                {
                    relations.add(StringUtils.substringBefore(paramName,"."));
                    if(paramValue == "null")
                    {
                        paramValue = -1l;
                    }
                    else if(!(paramValue instanceof Long))
                    {
                        paramValue = Long.parseLong(paramValue);
                    }
                    domainObjectpropertyNames.add(StringUtils.substringBefore(paramName,"."));
                }
                else
                {
                    domainObjectpropertyNames.add(paramName);
                }
                clonedMap.put (paramName, paramValue);
            }
        }
        domainObjectpropertyNames = domainObjectpropertyNames.unique ().findAll {domainClass.metaClass.getMetaProperty(it) != null}
        ApplicationHolder.getApplication().getClassLoader().loadClass("org.codehaus.groovy.grails.web.binding.DataBindingUtils").bindObjectToInstance (instance, clonedMap);
        domainObjectpropertyNames.each{
            def propValue = instance.getProperty(it);
            if(relations.contains(it) && propValue.id == -1l)
            {
                returnedParams.put (it, null);
            }
            else
            {
                returnedParams[it] = propValue
            }
        }
        return returnedParams;
    }
}