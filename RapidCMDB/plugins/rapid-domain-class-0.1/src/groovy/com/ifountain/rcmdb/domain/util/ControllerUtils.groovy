package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 13, 2008
 * Time: 5:49:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ControllerUtils {
    def static getClassProperties(params, domainClass)
    {
        def returnedParams = [:]
        params.each{propName, propValue->
            if(!PROPS_TO_BE_EXCLUDED.containsKey(propName))
            {
                def indexOfDot = propName.indexOf(".");
                if(indexOfDot < 0)
                {
                     if(propValue instanceof Map)
                    {
                        if(propValue["id"] != "null")
                        {
                            def id = Long.parseLong(propValue["id"]);
                            def fieldType = domainClass.metaClass.getMetaProperty(propName).type;
                            returnedParams[propName] = fieldType.metaClass.invokeStaticMethod(fieldType, "get", [id] as Object[])
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
                            def metaProp = domainClass.metaClass.getMetaProperty(propName);
                            if(metaProp)
                            {
                                if(metaProp.type == Date.class)
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
                        }
                        else{
                            returnedParams[propName] = null;
                        }
                    }
                }
            }
        }
        return returnedParams;
    }
}