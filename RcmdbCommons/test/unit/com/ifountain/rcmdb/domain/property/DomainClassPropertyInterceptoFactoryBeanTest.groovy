package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 9:54:01 AM
 * To change this template use File | Settings | File Templates.
 */
class DomainClassPropertyInterceptoFactoryBeanTest extends RapidCmdbTestCase
{
    public void testFactory()
    {
        DomainClassPropertyInterceptorFactoryBean factoryBean = new DomainClassPropertyInterceptorFactoryBean();
        factoryBean.setPropertyInterceptorClassName (DomainClassPropertyInterceptorMockImpl.name);
        factoryBean.setClassLoader(this.getClass().getClassLoader());
        factoryBean.afterPropertiesSet();

        assertEquals(DomainClassPropertyInterceptor, factoryBean.getObjectType());
        DomainClassPropertyInterceptor interceptor = factoryBean.getObject();
        assertTrue(interceptor instanceof DomainClassPropertyInterceptorMockImpl);
        assertTrue(factoryBean.isSingleton());
        assertSame (interceptor, factoryBean.getObject());

    }

    public void testFactoryWithNonExistingClass()
    {
        DomainClassPropertyInterceptorFactoryBean factoryBean = new DomainClassPropertyInterceptorFactoryBean();
        factoryBean.setPropertyInterceptorClassName ("NonExistingClass");
        factoryBean.setClassLoader(this.getClass().getClassLoader());
        factoryBean.afterPropertiesSet();

        assertEquals(DomainClassPropertyInterceptor, factoryBean.getObjectType());
        DomainClassPropertyInterceptor interceptor = factoryBean.getObject();
        assertTrue(interceptor instanceof DefaultDomainClassPropertyInterceptor);

    }

    public void testFactoryWithInvalidClass()
    {
        DomainClassPropertyInterceptorFactoryBean factoryBean = new DomainClassPropertyInterceptorFactoryBean();
        factoryBean.setPropertyInterceptorClassName (DomainClassPropertyInterceptoFactoryBeanTest.name);
        factoryBean.setClassLoader(this.getClass().getClassLoader());
        factoryBean.afterPropertiesSet();

        assertEquals(DomainClassPropertyInterceptor, factoryBean.getObjectType());
        DomainClassPropertyInterceptor interceptor = factoryBean.getObject();
        assertTrue(interceptor instanceof DefaultDomainClassPropertyInterceptor);
    }
}

class DomainClassPropertyInterceptorMockImpl implements DomainClassPropertyInterceptor
{

    public void setDomainClassProperty(Object domainObject, String propertyName, Object value) {
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
    }

}