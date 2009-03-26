package com.ifountain.rcmdb.domain.validator

import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 26, 2009
* Time: 12:00:09 AM
* To change this template use File | Settings | File Templates.
*/
public class DomainClassValidationWrapper {
    def domainObject;
    Map updatedProps;
    public DomainClassValidationWrapper(domainObject, Map updatedProps)
    {
        this.domainObject = domainObject;
        this.updatedProps = updatedProps;
    }

    public Object methodMissing(String methodName, Object args)
    {
        return domainObject.invokeMethod(methodName, args);        
    }

    public Object getProperty(String propName)
    {
        if(updatedProps.containsKey(propName))
        {
            return updatedProps.get(propName);
        }
        return domainObject.getProperty (propName);
    }

    public void setProperty(String propName, Object propValue)
    {
        if(propName == RapidCMDBConstants.ERRORS_PROPERTY_NAME)
        {
            domainObject.setProperty (propName, propValue, false)            
        }
        else
        {
            throw RapidValidationException.propertySetException();
        }

    }
}