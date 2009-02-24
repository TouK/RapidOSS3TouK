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

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.springframework.validation.BeanPropertyBindingResult
import relation.Relation
import com.ifountain.rcmdb.domain.util.RelationMetaData

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:27:48 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveMethodTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        RemoveMethodDomainObject.unIndexList = [];
        RemoveMethodDomainObject.eventCallList = [];
        RemoveMethodDomainObject.existingInstanceCount = 0;
        RemoveMethodDomainObject.countQuery = null;
        RemoveMethodDomainObject.relatedInstancesShouldBeReturnedFromRemoveRelationMethod = [:]
        RemoveMethodDomainObject.metaClass.'static'.keySet = {
            return []
        }
        RemoveMethodDomainObjectWithEvents.metaClass.'static'.keySet = {
            return []
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveObject()
    {
        RemoveMethodDomainObject.existingInstanceCount = 1;
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(id:1, prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        assertTrue (removeMethod.isWriteOperation());
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
        assertFalse (objectToBeRemoved.hasErrors());
        assertEquals (RemoveMethodDomainObject.countQuery, "id:\"${objectToBeRemoved.id}\"");
    }

    public void testRemoveObjectWithEvents()
    {
        initialize([relation.Relation], []);
        

        RemoveMethodDomainObject.existingInstanceCount = 1;
        def rel1Object=new Object();
        RemoveMethodDomainObjectWithEvents objectToBeRemoved = new RemoveMethodDomainObjectWithEvents(id:1, prop1:"prop1Value1",rel1:rel1Object);
        def relations=[rel1:new RelationMetaData("rel1","otherrel1",RemoveMethodDomainObject,RemoveMethodDomainObject,RelationMetaData.ONE_TO_ONE)];


        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, relations);
        removeMethod.invoke (objectToBeRemoved, null);

        
        assertSame(objectToBeRemoved, RemoveMethodDomainObjectWithEvents.unIndexList[0][0]);
        assertEquals (rel1Object,objectToBeRemoved.relationsToBeRemoved.rel1[0]);
        assertTrue (objectToBeRemoved.isBeforeDeleteCalled);
        assertTrue (objectToBeRemoved.isAfterDeleteCalled);
        assertFalse (objectToBeRemoved.isBeforeUpdateCalled);
        assertFalse (objectToBeRemoved.isAfterUpdateCalled);
        assertFalse (objectToBeRemoved.isBeforeInsertCalled);
        assertFalse (objectToBeRemoved.isAfterInsertCalled);
        assertFalse (objectToBeRemoved.isOnLoadCalled);

        def eventCallList=["beforeDelete","removeRelation","unindex","afterDelete"];
        assertEquals(eventCallList,RemoveMethodDomainObject.eventCallList);

    }

    public void testRemoveObjectReturnsErrorIfObjectDoesnotExist()
    {
        RemoveMethodDomainObject.existingInstanceCount = 0;
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(id:1, prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertTrue (objectToBeRemoved.hasErrors());
    }

    public void testRemoveObjectWithRelations()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 objectToBeRemoved = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedObj1 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedObj2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedObj3 = RelationMethodDomainObject2.add([:]);

        objectToBeRemoved.addRelation(rel1:relatedObj1, rel2:relatedObj2, rel3:relatedObj3);
        assertEquals (objectToBeRemoved, relatedObj1.revRel1);
        assertEquals (objectToBeRemoved, relatedObj2.revRel2);
        assertTrue (relatedObj3.revRel3.contains(objectToBeRemoved));
        objectToBeRemoved.remove();
        
        assertNull (RelationMethodDomainObject1.get(id:objectToBeRemoved.id))
        assertNull (RelationMethodDomainObject2.get(id:relatedObj2.id))
        assertEquals (relatedObj1, RelationMethodDomainObject2.get(id:relatedObj1.id))
        assertNull (relatedObj1.revRel1);
        assertNull (relatedObj2.revRel2);
        assertFalse (relatedObj3.revRel3.contains(objectToBeRemoved));
        assertNull (objectToBeRemoved.rel1);
        assertTrue (objectToBeRemoved.rel2.isEmpty());
        assertEquals(0, Relation.search("objectId:${objectToBeRemoved.id} OR reverseObjectId:${objectToBeRemoved.id}").total);
    }
    
}

class RemoveMethodDomainObject
{
    def static relatedInstancesShouldBeReturnedFromRemoveRelationMethod = [:];
    Long id;
    String prop1;
    def rel1;
    def rel3;
    List rel2 = [];
    def static unIndexList = [];
    def static countQuery;
    def static existingInstanceCount;
    def static eventCallList=[];
    
    def errors =  new BeanPropertyBindingResult(this, this.class.getName());
    def relationsToBeRemoved;
    

    def static unindex(objectList)
    {
        unIndexList.add(objectList);
        eventCallList.add("unindex");
    }
    def static countHits(String query)
    {
        countQuery = query;
        return existingInstanceCount;
    }

    public boolean hasErrors()
    {
        return errors.hasErrors();        
    }


    def removeRelation(Map relations)
    {
        eventCallList.add("removeRelation");
        relationsToBeRemoved = relations;
        return relatedInstancesShouldBeReturnedFromRemoveRelationMethod;
    }
}

class RemoveMethodDomainObjectWithEvents extends RemoveMethodDomainObject
{
    boolean isOnLoadCalled = false;
    boolean isBeforeInsertCalled = false;
    boolean isAfterInsertCalled = false;
    boolean isBeforeUpdateCalled = false;
    boolean isAfterUpdateCalled = false;
    boolean isBeforeDeleteCalled = false;
    boolean isAfterDeleteCalled = false;
    def onLoad = {
        isOnLoadCalled = true;
        eventCallList.add("onLoad");
    }

    def beforeInsert = {
        isBeforeInsertCalled = true;
        eventCallList.add("beforeInsert");
    }
    def beforeUpdate = {
        isBeforeUpdateCalled = true;
        eventCallList.add("beforeUpdate");
    }
    def beforeDelete = {
        isBeforeDeleteCalled = true;
        eventCallList.add("beforeDelete");
    }
    def afterInsert = {
        isAfterInsertCalled = true;
        eventCallList.add("afterInsert");
    }
    def afterUpdate = {
        isAfterUpdateCalled = true;
        eventCallList.add("afterUpdate");
    }
    def afterDelete = {
        isAfterDeleteCalled = true;
        eventCallList.add("afterDelete");
    }
}

