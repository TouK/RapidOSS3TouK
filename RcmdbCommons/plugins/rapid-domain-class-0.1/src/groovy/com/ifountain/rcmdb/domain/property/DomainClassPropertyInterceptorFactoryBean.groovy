package com.ifountain.rcmdb.domain.property

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 9:51:57 AM
 * To change this template use File | Settings | File Templates.
 */
class DomainClassPropertyInterceptorFactoryBean implements FactoryBean, InitializingBean{
    String propertyInterceptorClassName;
    ClassLoader classLoader;
    DomainClassPropertyInterceptor propertyInterceptor;
    public void afterPropertiesSet() {

        def propertyInterceptorClass = DefaultDomainClassPropertyInterceptor;
        try
        {
            def tmpPropertyInterceptorClass = classLoader.loadClass(propertyInterceptorClassName);
            if(DomainClassPropertyInterceptor.isAssignableFrom(tmpPropertyInterceptorClass))
            {
                propertyInterceptorClass = tmpPropertyInterceptorClass;
            }
        }
        catch(Throwable t)
        {
        }
        propertyInterceptor = (DomainClassPropertyInterceptor)propertyInterceptorClass.newInstance();
    }

    public Object getObject() {
        return propertyInterceptor;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class getObjectType() {
        return DomainClassPropertyInterceptor;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isSingleton() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

}