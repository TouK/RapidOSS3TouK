package com.ifountain.rcmdb.domain.property
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:18:49 AM
 * To change this template use File | Settings | File Templates.
 */
class DefaultDomainClassPropertyInterceptor implements DomainClassPropertyInterceptor{

    public void setDomainClassProperty(Object domainObject, String propertyName, Object value)
    {
        def metaProp = domainObject.metaClass.getMetaProperty(propertyName);
        if(metaProp != null)
        {
            metaProp.setProperty(domainObject, value);
        }
        else
        {
            throw new MissingPropertyException(propertyName, domainObject.class)
        }
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        def metaProp = domainObject.metaClass.getMetaProperty(propertyName);
        if(metaProp != null)
        {
            return metaProp.getProperty(domainObject)
        }
        else
        {
            throw new MissingPropertyException(propertyName, domainObject.class)
        }
    }

}