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
        assertEquals(prop1Value, interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
        interceptor.setDomainClassProperty(instance.metaClass, instance.class, instance, "prop1", "updatedProp1Value")
        assertEquals("updatedProp1Value", interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1"));
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
            interceptor.getDomainClassProperty(instance.metaClass, instance.class, instance, "prop1")
            fail("Should throw exception");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals (new MissingPropertyException("prop1", domainClass).getMessage(), ex.getMessage());
        }

        try
        {
            interceptor.setDomainClassProperty(instance.metaClass, instance.class, instance, "prop1", "prop1Value")
            fail("Should throw exception");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals (new MissingPropertyException("prop1", domainClass).getMessage(), ex.getMessage());
        }
    }
}