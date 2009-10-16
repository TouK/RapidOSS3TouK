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
import org.codehaus.groovy.grails.web.binding.DataBindingUtils
import javax.imageio.ImageIO
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.util.RapidCMDBConstants
import java.lang.reflect.Field

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2008
 * Time: 5:49:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ControllerUtils {

    def static drawImageToWeb(java.awt.Image image,String contentTypeForWeb,String imageFormat,response)
    {
        response.contentType = contentTypeForWeb;
        ImageIO.write(image, imageFormat, response.outputStream);
        response.outputStream.flush();
    }
    def static getWebResponse()
    {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        return context?.get(RapidCMDBConstants.WEB_RESPONSE);
    }

    def static convertSuccessToXml(String successMessage)
    {
        StringWriter writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Successful(successMessage.toString());

        return writer.toString();

    }
    def static convertErrorToXml(String errorMessage)
    {
        StringWriter writer = new StringWriter();
        def builder = new MarkupBuilder(writer);

        builder.Errors(){
            builder.Error(error:errorMessage)           
        }

        return writer.toString();
    }

    def static List getMultipleRelations(String relString, Class relType)
    {
        def relatedInstances = [];
        if (relString != null)
        {
            relString.split(",").each {
                def relatedInstance = relType.'searchEvery'("id:\"${it}\"")[0];
                if (relatedInstance != null)
                {
                    relatedInstances.add(relatedInstance);
                }
            }
        }
        return relatedInstances;
    }

    def static backupOldData(domainInstance, Map params)
    {
        def oldProperties = [:];
        params.each{String propName, Object propValue->
            try
            {
                oldProperties[propName] = domainInstance.getProperty (propName);
            }
            catch(groovy.lang.MissingPropertyException e)
            {
            }
        }
        return oldProperties;
    }

    def static getClassProperties(Map params, Class domainClass)
    {
        def relations = [:];
        def domainProperties = [:];
        domainClass.getPropertiesList().each {
            if (it.isRelation)
            {
                relations[it.name] = it;
            }
            else if(!it.isOperationProperty)
            {
                domainProperties[it.name] = it;    
            }
        }
        def returnedParams = [:]
        def instance = domainClass.newInstance();
        def domainObjectpropertyNames = [];
        def clonedMap = new HashMap();
        params.each {String paramName, paramValue ->
            boolean willBeAdded = true;
            if (paramName != "id" && paramName != "_id")
            {
                if (paramName.startsWith("_"))
                {
                    domainObjectpropertyNames.add(paramName.substring(1));
                }
                else if (paramName.indexOf(".") >= 0)
                {
                    def relName = StringUtils.substringBefore(paramName, ".id");
                    def relMetaData = relations[relName];
                    if (relMetaData != null)
                    {
                        if (relMetaData.isManyToOne() || relMetaData.isOneToOne())
                        {
                            if (paramValue == "null")
                            {
                                paramValue = -1l;
                            }
                            else if (!(paramValue instanceof Long))
                            {
                                paramValue = Long.parseLong(paramValue);
                            }
                            domainObjectpropertyNames.add(StringUtils.substringBefore(paramName, "."));
                        }
                        else {
                            returnedParams[relName] = getMultipleRelations(paramValue, relations[relName].relatedModel);
                            willBeAdded = false;
                        }

                    }
                    else
                    {
                        willBeAdded = false;
                    }
                }
                else
                {
                    domainObjectpropertyNames.add(paramName);
                }
                if(willBeAdded && !returnedParams.containsKey(paramName))
                {
                    clonedMap.put(paramName, paramValue);
                }
            }
        }
        domainObjectpropertyNames = domainObjectpropertyNames.unique().findAll {(domainProperties.containsKey (it) || relations.containsKey(it)) && !returnedParams.containsKey(it)}
        DataBindingUtils.bindObjectToInstance(instance, clonedMap);
        domainObjectpropertyNames.each {
            def propValue = getPropertyValue(instance, it);
            if (relations.containsKey(it) && !(propValue instanceof Collection) && propValue.id == -1l)
            {
                returnedParams.put(it, null);
            }
            else
            {
                if(domainProperties[it] != null && domainProperties[it].type.name == String.name && propValue == null)
                {
                    propValue = "";    
                }
                returnedParams[it] = propValue
            }
        }
        return returnedParams;
    }

    private static Object getPropertyValue(Object instance, String propName)
    {
        Field f = instance.getClass().getDeclaredField(propName);
        f.setAccessible (true);
        return f.get(instance)
    }
}