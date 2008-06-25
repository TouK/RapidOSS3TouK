package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.springframework.validation.BeanPropertyBindingResult

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:27:48 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        RemoveMethodDomainObject.unIndexList = [];
        RemoveMethodDomainObject.existingInstanceCount = 0;
        RemoveMethodDomainObject.countQuery = null;
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveObject()
    {
        RemoveMethodDomainObject.existingInstanceCount = 1;
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:], ["prop1"]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
        assertFalse (objectToBeRemoved.hasErrors());
        assertEquals (RemoveMethodDomainObject.countQuery, "prop1:\"prop1Value1\"");
    }

    public void testRemoveObjectWithEvents()
    {
        RemoveMethodDomainObject.existingInstanceCount = 1;
        RemoveMethodDomainObjectWithEvents objectToBeRemoved = new RemoveMethodDomainObjectWithEvents(prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:], ["prop1"]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObjectWithEvents.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
        assertTrue (objectToBeRemoved.isBeforeDeleteCalled);
        assertFalse (objectToBeRemoved.isBeforeUpdateCalled);
        assertFalse (objectToBeRemoved.isBeforeInsertCalled);
        assertFalse (objectToBeRemoved.isOnLoadCalled);
    }

    public void testRemoveObjectReturnsErrorIfObjectDoesnotExist()
    {
        RemoveMethodDomainObject.existingInstanceCount = 0;
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(prop1:"prop1Value1");
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:], ["prop1"]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertTrue (objectToBeRemoved.hasErrors());
    }

    public void testRemoveObjectWithRelations()
    {
        RemoveMethodDomainObject.existingInstanceCount = 1;
        RelationMethodDomainObject2 relatedObj1 = new RelationMethodDomainObject2();
        RelationMethodDomainObject2 relatedObj2 = new RelationMethodDomainObject2();
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject(prop1:"prop1Value1");
        objectToBeRemoved.rel1 = relatedObj1;
        objectToBeRemoved.rel2 += relatedObj2;
        def rel2 = new Relation("rel2", "revRel2", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY);
        rel2.isCascade = true;
        def relationsForObjectToBeRemoved = ["rel1":new Relation("rel1", "revRel1", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE),
        "rel2":rel2,
        "rel3":new Relation("rel3", "revRel3", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE)]
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, relationsForObjectToBeRemoved, ["prop1"]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertEquals (2, objectToBeRemoved.relationsToBeRemoved.size());
        assertTrue (objectToBeRemoved.relationsToBeRemoved["rel1"].contains(relatedObj1));
        assertTrue (objectToBeRemoved.relationsToBeRemoved["rel2"].contains(relatedObj2));
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
        assertTrue(relatedObj2.isRemoved);
        assertFalse(relatedObj1.isRemoved);
    }
    
}

class RemoveMethodDomainObject
{
    String prop1;
    def rel1;
    def rel3;
    List rel2 = [];
    def static unIndexList = [];
    def static countQuery;
    def static existingInstanceCount;
    def errors =  new BeanPropertyBindingResult(this, this.class.getName());
    def relationsToBeRemoved;
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


    def removeRelation(Map relations)
    {
        relationsToBeRemoved = relations;
    }
}

class RemoveMethodDomainObjectWithEvents extends RemoveMethodDomainObject
{
    boolean isOnLoadCalled = false;
    boolean isBeforeInsertCalled = false;
    boolean isBeforeUpdateCalled = false;
    boolean isBeforeDeleteCalled = false;
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
}

