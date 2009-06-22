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
package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 20, 2008
 * Time: 4:54:44 PM
 * To change this template use File | Settings | File Templates.
 */
class KeySetMethodTest extends RapidCmdbTestCase{
    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        ExpandoMetaClass.enableGlobally()
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testKeySet()
    {
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        GrailsDomainClass cls = new DefaultGrailsDomainClass(GetPropertiesMethodDomainObject);
        KeySetMethod method = new KeySetMethod(cls, new FederatedPropertyManagerImpl());
        List keys = method.getKeys();
        assertEquals(3, keys.size());

        RapidDomainClassProperty prop = keys[0]//propsMap["declaredProp1"];
        prop = keys[0]
        assertEquals("prop1", prop.name);
        assertTrue (prop.isKey);
        assertFalse (prop.isRelation);
        assertFalse (prop.isOperationProperty);

        prop = keys[1]
        assertEquals("prop2", prop.name);
        assertTrue (prop.isKey);
        assertFalse (prop.isRelation);
        assertFalse (prop.isOperationProperty);

        prop = keys[2]
        assertEquals("rel1", prop.name);
        assertTrue (prop.isKey);
        assertTrue (prop.isRelation);
        assertFalse (prop.isOperationProperty);
        
    }
}