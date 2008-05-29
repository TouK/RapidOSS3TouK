package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation

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
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveObject()
    {
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject();
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, [:]);
        removeMethod.invoke (objectToBeRemoved, null);
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
        assertNull (objectToBeRemoved.relationsToBeRemoved);
    }

    public void testRemoveObjectWithRelations()
    {

        RelationMethodDomainObject2 relatedObj1 = new RelationMethodDomainObject2();
        RelationMethodDomainObject2 relatedObj2 = new RelationMethodDomainObject2();
        RemoveMethodDomainObject objectToBeRemoved = new RemoveMethodDomainObject();
        objectToBeRemoved.rel1 = relatedObj1;
        objectToBeRemoved.rel2 += relatedObj2;        
        def relationsForObjectToBeRemoved = ["rel1":new Relation("rel1", "revRel1", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE),
        "rel2":new Relation("rel2", "revRel2", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY),
        "rel3":new Relation("rel3", "revRel3", RemoveMethodDomainObject.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE)]
        RemoveMethod removeMethod = new RemoveMethod(objectToBeRemoved.metaClass, relationsForObjectToBeRemoved);
        removeMethod.invoke (objectToBeRemoved, null);
        assertEquals (2, objectToBeRemoved.relationsToBeRemoved.size());
        assertTrue (objectToBeRemoved.relationsToBeRemoved["rel1"].contains(relatedObj1));
        assertTrue (objectToBeRemoved.relationsToBeRemoved["rel2"].contains(relatedObj2));
        assertSame(objectToBeRemoved, RemoveMethodDomainObject.unIndexList[0][0]);
    }
}

class RemoveMethodDomainObject
{
    def rel1;
    def rel3;
    List rel2 = [];
    def static unIndexList = [];
    def relationsToBeRemoved;
    def static unindex(objectList)
    {
        unIndexList.add(objectList);
    }

    def removeRelation(Map relations)
    {
        relationsToBeRemoved = relations;
    }
}

