package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 6, 2008
 * Time: 5:13:43 PM
 */
class DomainUtilsTest extends RapidCmdbTestCase
{
    public void testGetKeys()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        DefaultGrailsDomainClass domainClass1 = new DefaultGrailsDomainClass(DomainUtilsTestDomainClassSample3);
        def keys = DomainClassUtils.getKeys (domainClass1);
        assertEquals (2, keys.size());
        assertTrue(keys.contains("name"));
        assertTrue(keys.contains("surname"));
    }
}

class DomainUtilsTestDomainClassSample1
{
    long id;
    long version;
    String name;
    String surname

    static constraints =
    {
        name(key:["surname"], nullable:false)
    }
}

class DomainUtilsTestDomainClassSample2    extends DomainUtilsTestDomainClassSample1
{
    String ssn;

    static constraints =
    {
        name(key:["surname"], nullable:false)
    }
}

class DomainUtilsTestDomainClassSample3 extends DomainUtilsTestDomainClassSample2
{
    String age;
}