package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import java.text.SimpleDateFormat
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import groovy.xml.MarkupBuilder
import grails.converters.XML
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.context.MessageSource
import java.text.MessageFormat

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
        builder.Successfull(successMessage.toString());

        return writer.toString();

    }

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
                            def id = propValue["id"] instanceof Long?propValue["id"]:Long.parseLong(propValue["id"]);
                            def metaProp = domainClass.metaClass.getMetaProperty(propName);
                            if(metaProp)
                            {
                                def fieldType = metaProp.type;
                                returnedParams[propName] = fieldType.metaClass.invokeStaticMethod(fieldType, "get", [id] as Object[])
                            }
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
	                        if(propName.indexOf("_") == 0){
		                        propName = propName.substring(1)
		                    	def metaProp = domainClass.metaClass.getMetaProperty(propName)
		                    	if(metaProp && (metaProp.type == boolean || metaProp.type == Boolean.class)){
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