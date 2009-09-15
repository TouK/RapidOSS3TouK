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

    public void setDomainClassProperty(MetaClass metaClass, Class domainClass, Object domainObject, String propertyName, Object value) {
    }

    public Object getDomainClassProperty(MetaClass metaClass, Class domainClass, Object domainObject, String propertyName) {
    }

}