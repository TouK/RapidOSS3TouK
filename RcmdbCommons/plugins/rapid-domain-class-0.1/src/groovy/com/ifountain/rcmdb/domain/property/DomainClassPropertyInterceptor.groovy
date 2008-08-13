package com.ifountain.rcmdb.domain.property
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 9:52:13 AM
 * To change this template use File | Settings | File Templates.
 */
interface DomainClassPropertyInterceptor
{
    public void setDomainClassProperty(Object domainObject, String propertyName, Object value);
    public Object getDomainClassProperty(Object domainObject, String propertyName);
}