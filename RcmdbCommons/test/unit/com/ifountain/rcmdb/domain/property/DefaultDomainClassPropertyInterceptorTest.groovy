package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:59:01 AM
 * To change this template use File | Settings | File Templates.
 */
class DefaultDomainClassPropertyInterceptorTest extends RapidCmdbTestCase{
    public void testGetProperty()
    {
        GroovyClassLoader gcl = new GroovyClassLoader();
        String prop1Value = "prop1Value"
        Class domainClass = gcl.parseClass("""
            class DomainClass1
            {
                String prop1 = "${prop1Value}"
            }
        """)

        def instance = domainClass.newInstance();
        DefaultDomainClassPropertyInterceptor interceptor = new DefaultDomainClassPropertyInterceptor();
        assertEquals(prop1Value, interceptor.getDomainClassProperty(instance, "prop1"));
        interceptor.setDomainClassProperty(instance, "prop1", "updatedProp1Value")
        assertEquals("updatedProp1Value", interceptor.getDomainClassProperty(instance, "prop1"));
    }

    public void testThrowsPropertyNotDefinedIfPropertyDoesnotExists()
    {
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class domainClass = gcl.parseClass("""
            class DomainClass1
            {
            }
        """)

        def instance = domainClass.newInstance();
        DefaultDomainClassPropertyInterceptor interceptor = new DefaultDomainClassPropertyInterceptor();
        try
        {
            interceptor.getDomainClassProperty(instance, "prop1")
            fail("Should throw exception");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals (new MissingPropertyException("prop1", domainClass).getMessage(), ex.getMessage());
        }

        try
        {
            interceptor.setDomainClassProperty(instance, "prop1", "prop1Value")
            fail("Should throw exception");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals (new MissingPropertyException("prop1", domainClass).getMessage(), ex.getMessage());
        }
    }
}