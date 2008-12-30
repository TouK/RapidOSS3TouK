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

import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils
import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2008
 * Time: 5:49:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ControllerUtils {
	def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]

    def static convertSuccessToXml(String successMessage)
    {
        StringWriter writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Successful(successMessage.toString());

        return writer.toString();

    }

    def static getClassProperties(params, domainClass)
    {
        def returnedParams = [:]
        def domainObjectProps = [:];
        domainClass.getPropertiesList().each{
            if(!it.isOperationProperty)
            {
                domainObjectProps[it.name] = it;
            }
        }
        params.each{propName, propValue->
            def metaProp = domainObjectProps[propName];
            if(metaProp != null)
            {
                Class propType = domainClass.metaClass.getMetaProperty(metaProp.name).type
                def indexOfDot = propName.indexOf(".");
                if(indexOfDot < 0)
                {
                     if(metaProp.isRelation)
                    {
                        if(propValue["id"] != "null")
                        {
                            def id = propValue["id"] instanceof Long?propValue["id"]:Long.parseLong(propValue["id"]);
                            returnedParams[propName] = propType.metaClass.invokeStaticMethod(propType, "get", [id] as Object[])
                        }
                        else
                        {
                            returnedParams[propName] = null;
                        }
                    }
                    else
                    {
                        if(propValue.length() != 0)
                        {
                            if(propType == Date.class)
                            {
                                def year = params[propName+"_year"];
                                def day = params[propName+"_day"];
                                def month = params[propName+"_month"];
                                def hour = params[propName+"_hour"];
                                def min = params[propName+"_minute"];
                                def date = dateFormat.parse("$year-$month-$day $hour:$min");
                                DateConverter converter = RapidConvertUtils.getInstance().lookup (Date.class);
                                propValue = converter.formater.format (date);
                            }
                            returnedParams[propName] = propValue;
                        }
                        else{
	                        if(propName.indexOf("_") == 0){
                                propName = propName.substring(1)
                                if(propType == boolean || propType == Boolean.class){
                                    if(!returnedParams[propName]){
                                        returnedParams[propName] = false
                                    }

                                }
		                    }
		                    else{
			                	returnedParams[propName] = null;
			                }
                            
                        }
                    }
                }
            }
        }
        return returnedParams;
    }
}