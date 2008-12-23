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
        RemoveMethodDomainObject.existingInstanceCount = 1;
        RemoveMethodDomainObjectWithEvents objectToBeRemoved = new RemoveMethodDomainObjectWithEvents(id:1, prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObjectWithEvents.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
        assertTrue (objectToBeRemoved.isBeforeDeleteCalled);
        assertTrue (objectToBeRemoved.isAfterDeleteCalled);
        assertFalse (objectToBeRemoved.isBeforeUpdateCalled);
        assertFalse (objectToBeRemoved.isAfterUpdateCalled);
        assertFalse (objectToBeRemoved.isBeforeInsertCalled);
        assertFalse (objectToBeRemoved.isAfterInsertCalled);
        assertFalse (objectToBeRemoved.isOnLoadCalled);
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
    def errors =  new BeanPropertyBindingResult(this, this.class.getName());
    def relationsToBeRemoved;
    def isRemoveRelationFlushed = true;
    def static unindex(objectList)
    {
        unIndexList.add(objectList);
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


    def removeRelation(Map relations, boolean flush)
    {
        relationsToBeRemoved = relations;
        isRemoveRelationFlushed = flush;
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
    }

    def beforeInsert = {
        isBeforeInsertCalled = true;
    }
    def beforeUpdate = {
        isBeforeUpdateCalled = true;
    }
    def beforeDelete = {
        isBeforeDeleteCalled = true;
    }
    def afterInsert = {
        isAfterInsertCalled = true;
    }
    def afterUpdate = {
        isAfterUpdateCalled = true;
    }
    def afterDelete = {
        isAfterDeleteCalled = true;
    }
}

