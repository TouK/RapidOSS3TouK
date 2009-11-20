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
package com.ifountain.compass.search

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.session.SessionManager
import com.ifountain.session.Session

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 26, 2008
 * Time: 4:22:06 PM
 * To change this template use File | Settings | File Templates.
 */
class FilterManagerTest extends RapidCmdbWithCompassTestCase
{

    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        SessionManager.destroyInstance();
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        SessionManager.destroyInstance();
    }

    public void testFilterManagerWithCompass()
    {
        Session session = SessionManager.getInstance().startSession ("user1");
        assertEquals(true, FilterManager.isFiltersEnabled(SessionManager.getInstance().getSession()));

        initialize([FilterManagerTestDomainObject1, FilterManagerTestDomainObject2], []);

        def obj1Inst1 = FilterManagerTestDomainObject1.add(prop1:"obj1Inst1", owner:"owner1", rsOwner:"p");
        def obj1Inst2 = FilterManagerTestDomainObject1.add(prop1:"obj1Inst2", owner:"owner2", rsOwner:"p");
        def obj1Inst3 = FilterManagerTestDomainObject1.add(prop1:"obj1Inst2", owner:"owner2", rsOwner:"someRsOwner");

        def obj2Inst1 = FilterManagerTestDomainObject2.add(prop1:"obj2Inst1", owner:"owner1", rsOwner:"someRsOwner");
        def obj2Inst2 = FilterManagerTestDomainObject2.add(prop1:"obj2Inst2", owner:"owner1", rsOwner:"someRsOwner");
        def obj2Inst3 = FilterManagerTestDomainObject2.add(prop1:"obj2Inst3", owner:"owner2", rsOwner:"someRsOwner");
        def obj2Inst4 = FilterManagerTestDomainObject2.add(prop1:"obj2Inst4", owner:"owner2", rsOwner:"someRsOwner");
        obj1Inst1.addRelation(rel1:[obj2Inst1,obj2Inst3], rel2:[obj2Inst2], rel3:[obj2Inst4]);

        def res = FilterManagerTestDomainObject1.search("alias:*").results;
        assertEquals (3, res.size());
        assertTrue (res.contains(obj1Inst1));
        assertTrue (res.contains(obj1Inst2));
        assertTrue (res.contains(obj1Inst3));
        assertEquals (2, res[0].rel1.size())
        assertEquals (obj2Inst2, res[0].rel2)
        assertEquals (obj2Inst4, res[0].rel3)

        def searchFilters = [:]
        searchFilters[FilterManager.GROUP_FILTERS] = ["owner:owner1"]
        searchFilters[FilterManager.CLASS_FILTERS] = [:]
        searchFilters[FilterManager.CLASS_FILTERS][FilterManagerTestDomainObject2.class.name] = ["prop1:obj2Inst2"]
        session.put (FilterManager.SESSION_FILTER_KEY, searchFilters);

        res = FilterManagerTestDomainObject1.search("alias:*").results;
        assertEquals (2, res.size());
        assertTrue (res.contains(obj1Inst1));
        assertTrue (res.contains(obj1Inst2));
        assertEquals (1, res[0].rel1.size())
        assertEquals(obj2Inst1, res[0].rel1[0])
        assertEquals (obj2Inst2, res[0].rel2)
        assertNull (res[0].rel3)

        res = FilterManagerTestDomainObject1.searchEvery("alias:*");
        assertEquals (2, res.size());
        assertTrue (res.contains(obj1Inst1));
        assertTrue (res.contains(obj1Inst2));
        assertEquals (1, res[0].rel1.size())
         assertEquals(obj2Inst1, res[0].rel1[0])
        assertEquals (obj2Inst2, res[0].rel2)
        assertNull (res[0].rel3)

        //test with disabled filters
        
        assertEquals(true, FilterManager.isFiltersEnabled(SessionManager.getInstance().getSession()));
        FilterManager.setFiltersEnabled (false);
        assertEquals(false, FilterManager.isFiltersEnabled(SessionManager.getInstance().getSession()));
        res = FilterManagerTestDomainObject1.search("alias:*").results;
        assertEquals (3, res.size());
        assertTrue (res.contains(obj1Inst1));
        assertTrue (res.contains(obj1Inst2));
        assertTrue (res.contains(obj1Inst3));
        assertEquals (2, res[0].rel1.size())
        assertEquals (obj2Inst2, res[0].rel2)
        assertEquals (obj2Inst4, res[0].rel3)

    }
    
}
class FilterManagerTestDomainObject2 {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["revRel1", "revRel2", "revRel3", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    String prop1;
    String owner;
    String rsOwner = "p"
    List revRel1 = [];
    FilterManagerTestDomainObject1 revRel2;
    FilterManagerTestDomainObject1 revRel3;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static constraints={
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
     revRel1(nullable:true)
     revRel2(nullable:true)
     revRel3(nullable:true)
    }
    static relations = [
            revRel1:[type:FilterManagerTestDomainObject1, reverseName:"rel1", isMany:true],
            revRel2:[isMany:false, reverseName:"rel2", type:FilterManagerTestDomainObject1],
            revRel3:[isMany:false, reverseName:"rel3", type:FilterManagerTestDomainObject1],
    ]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    //AUTO_GENERATED_CODE
    public boolean equals(Object obj) {
        if(obj instanceof FilterManagerTestDomainObject2)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}


class FilterManagerTestDomainObject1 {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["rel1", "rel2","rel3", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    String prop1;
    String owner;
    String rsOwner = "p"
    List rel1 = [];
    FilterManagerTestDomainObject2 rel2;
    FilterManagerTestDomainObject2 rel3;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static constraints={
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
     rel1(nullable:true)
     rel2(nullable:true)
     rel3(nullable:true)
    }
    static relations = [
            rel1:[type:FilterManagerTestDomainObject2, reverseName:"revRel1", isMany:true],
            rel2:[isMany:false, reverseName:"revRel2", type:FilterManagerTestDomainObject2],
            rel3:[isMany:false, reverseName:"revRel3", type:FilterManagerTestDomainObject2],
    ]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    //AUTO_GENERATED_CODE
    public boolean equals(Object obj) {
        if(obj instanceof FilterManagerTestDomainObject1)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}

