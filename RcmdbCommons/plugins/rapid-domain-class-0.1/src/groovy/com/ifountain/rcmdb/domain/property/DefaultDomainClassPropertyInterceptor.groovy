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
        domainObject.setProperty(propertyName, value);
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        return domainObject.getProperty(propertyName);
    }

}