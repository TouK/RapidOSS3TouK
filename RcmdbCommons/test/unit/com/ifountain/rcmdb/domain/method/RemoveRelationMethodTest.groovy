package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:39:24 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveRelationMethodTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        IdGenerator.initialize (new MockIdGeneratorStrategy());
        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveRelation()
    {
        RelationMethodDomainObject1 expectedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 expectedDomainObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 expectedDomainObject3 = new RelationMethodDomainObject2(id:3);
        RelationMethodDomainObject2 expectedDomainObject4 = new RelationMethodDomainObject2(id:4);
        RelationMethodDomainObject2 expectedDomainObject5 = new RelationMethodDomainObject2(id:5);
        RelationMethodDomainObject2 expectedDomainObject6 = new RelationMethodDomainObject2(id:6);
        RelationMethodDomainObject2 expectedDomainObject7 = new RelationMethodDomainObject2(id:7);
        RelationMethodDomainObject2 notRelatedObject = new RelationMethodDomainObject2(id:19);
        RelationMethodDomainObject1 anotherDomainObjectForManyToOneRelation = new RelationMethodDomainObject1(id:8);
        expectedDomainObject5.revRel3 += anotherDomainObjectForManyToOneRelation;

        def relationsForObject1 = ["rel1":new Relation("rel1", "revRel1", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE),
        rel2:new Relation("rel2", "revRel2", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY),
        rel3:new Relation("rel3", "revRel3", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.MANY_TO_ONE),
        rel4:new Relation("rel4", "revRel4", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.MANY_TO_MANY)]

        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        def props = [rel1:expectedDomainObject2, rel2:[expectedDomainObject3,expectedDomainObject4], rel3:[expectedDomainObject5], rel4:[expectedDomainObject6, expectedDomainObject7]];
        add.invoke (expectedDomainObject1, [props] as Object[]);
        expectedDomainObject1.numberOfFlushCalls = 0
        expectedDomainObject2.numberOfFlushCalls = 0
        expectedDomainObject3.numberOfFlushCalls = 0
        expectedDomainObject3.numberOfFlushCalls = 0
        expectedDomainObject4.numberOfFlushCalls = 0
        expectedDomainObject5.numberOfFlushCalls = 0
        expectedDomainObject6.numberOfFlushCalls = 0
        expectedDomainObject7.numberOfFlushCalls = 0
        anotherDomainObjectForManyToOneRelation.numberOfFlushCalls = 0
        notRelatedObject.numberOfFlushCalls = 0
        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];

        RemoveRelationMethod remove = new RemoveRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        assertTrue (remove.isWriteOperation());
        props = [rel1:[expectedDomainObject2], rel2:[expectedDomainObject3], rel3:[expectedDomainObject5], rel4:expectedDomainObject7];
        remove.invoke (expectedDomainObject1, [props] as Object[]);

        assertNull (expectedDomainObject1.rel1);
        assertNull (expectedDomainObject2.revRel1);
        
        assertEquals (1, expectedDomainObject1.rel2.size());
        assertTrue (expectedDomainObject1.rel2.contains(expectedDomainObject4));
        assertNull (expectedDomainObject3.revRel2);

        assertNull (expectedDomainObject1.rel3);
        assertEquals (1, expectedDomainObject5.revRel3.size());
        assertFalse (expectedDomainObject5.revRel3.contains(expectedDomainObject1));
        assertTrue (expectedDomainObject5.revRel3.contains(anotherDomainObjectForManyToOneRelation));

        assertEquals (1, expectedDomainObject1.rel4.size());
        assertTrue (expectedDomainObject1.rel4.contains(expectedDomainObject6));
        assertFalse (expectedDomainObject1.rel4.contains(expectedDomainObject7));
        assertEquals (0, expectedDomainObject7.revRel4.size());

        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(expectedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject2));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject3));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject5));
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject7));
        assertFalse(RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject1));
        assertFalse (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject4));
        assertFalse (RelationMethodDomainObject2.indexList[0].contains(expectedDomainObject6));
        assertEquals (4, expectedDomainObject1.numberOfFlushCalls)
        expectedDomainObject1.isFlushedByProperty.each{
            assertFalse(it);
        }
        assertEquals (1, expectedDomainObject2.numberOfFlushCalls)
        assertEquals (1, expectedDomainObject3.numberOfFlushCalls)
        assertFalse(expectedDomainObject3.isFlushedByProperty[0]);
        assertEquals (0, expectedDomainObject4.numberOfFlushCalls)
        assertEquals (0, expectedDomainObject5.numberOfFlushCalls)
        assertEquals (0, expectedDomainObject6.numberOfFlushCalls)
        assertEquals (0, expectedDomainObject7.numberOfFlushCalls)


        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];
        
        props = [rel1:[notRelatedObject], rel2:[notRelatedObject], rel3:[notRelatedObject], rel4:[notRelatedObject]];
        remove.invoke (expectedDomainObject1, [props] as Object[]);


        assertNull (expectedDomainObject1.rel1);
        assertNull (expectedDomainObject2.revRel1);

        assertEquals (1, expectedDomainObject1.rel2.size());
        assertTrue (expectedDomainObject1.rel2.contains(expectedDomainObject4));
        assertNull (expectedDomainObject3.revRel2);

        assertNull (expectedDomainObject1.rel3);
        assertEquals (1, expectedDomainObject5.revRel3.size());
        assertFalse (expectedDomainObject5.revRel3.contains(expectedDomainObject1));
        assertTrue (expectedDomainObject5.revRel3.contains(anotherDomainObjectForManyToOneRelation));

        assertEquals (1, expectedDomainObject1.rel4.size());
        assertTrue (expectedDomainObject1.rel4.contains(expectedDomainObject6));
        assertFalse (expectedDomainObject1.rel4.contains(expectedDomainObject7));
        assertEquals (0, expectedDomainObject7.revRel4.size());

        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(expectedDomainObject1));
        assertEquals (1, RelationMethodDomainObject2.indexList.size());
        assertTrue (RelationMethodDomainObject2.indexList[0].contains(notRelatedObject));



    }


    public void testRemoveRelationWithNoOtherside()
    {
        RelationMethodDomainObject1 expectedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 expectedDomainObject2 = new RelationMethodDomainObject2(id:2);
        RelationMethodDomainObject2 expectedDomainObject3 = new RelationMethodDomainObject2(id:3);
        RelationMethodDomainObject2 expectedDomainObject4 = new RelationMethodDomainObject2(id:4);
        RelationMethodDomainObject2 expectedDomainObject5 = new RelationMethodDomainObject2(id:5);
        expectedDomainObject2.revRel1 = expectedDomainObject1;
        expectedDomainObject3.revRel2 = expectedDomainObject1;
        expectedDomainObject4.revRel3 += expectedDomainObject1;
        expectedDomainObject5.revRel4 += expectedDomainObject1;


        def relationsForObject1 = ["rel1":new Relation("rel1", null, RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE),
        rel2:new Relation("rel2", null, RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_MANY),
        rel3:new Relation("rel3", null, RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.MANY_TO_ONE),
        rel4:new Relation("rel4", null, RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.MANY_TO_MANY)]

        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        def props = [rel1:expectedDomainObject2, rel2:expectedDomainObject3, rel3:expectedDomainObject4, rel4:expectedDomainObject5];
        add.invoke (expectedDomainObject1, [props] as Object[]);
        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];

        RemoveRelationMethod remove = new RemoveRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        props = [rel1:expectedDomainObject2, rel2:expectedDomainObject3, rel3:expectedDomainObject4, rel4:expectedDomainObject5];
        remove.invoke (expectedDomainObject1, [props] as Object[]);

        assertNull (expectedDomainObject1.rel1);
        assertTrue (expectedDomainObject1.rel2.isEmpty());
        assertNull (expectedDomainObject1.rel3);
        assertTrue (expectedDomainObject1.rel4.isEmpty());

        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel1);
        assertEquals(expectedDomainObject1, expectedDomainObject3.revRel2);
        assertTrue(expectedDomainObject4.revRel3.contains(expectedDomainObject1));
        assertTrue(expectedDomainObject5.revRel4.contains(expectedDomainObject1));
        assertEquals (1, RelationMethodDomainObject1.indexList.size());
        assertTrue (RelationMethodDomainObject1.indexList[0].contains(expectedDomainObject1));
        assertEquals (0, RelationMethodDomainObject2.indexList.size());
    }


    public void testRemoveMethodWithoutIndexing()
    {
        RelationMethodDomainObject1 expectedDomainObject1 = new RelationMethodDomainObject1(id:1);
        RelationMethodDomainObject2 expectedDomainObject2 = new RelationMethodDomainObject2(id:2);

        def relationsForObject1 = ["rel1":new Relation("rel1", "revRel1", RelationMethodDomainObject1.class, RelationMethodDomainObject2.class, Relation.ONE_TO_ONE)]

        AddRelationMethod add = new AddRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        def props = [rel1:expectedDomainObject2];
        add.invoke (expectedDomainObject1, [props] as Object[]);
        expectedDomainObject1.numberOfFlushCalls = 0
        expectedDomainObject2.numberOfFlushCalls = 0
        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];

        RemoveRelationMethod remove = new RemoveRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
        props = [rel1:[expectedDomainObject2]];
        def relatedInstances = remove.invoke (expectedDomainObject1, [props, false] as Object[]);

        assertNull (expectedDomainObject1.rel1);
        assertNull (expectedDomainObject2.revRel1);
        assertEquals (0, RelationMethodDomainObject1.indexList.size());
        assertEquals (0, RelationMethodDomainObject2.indexList.size());
        assertEquals (1, relatedInstances.size())
        assertEquals (1, relatedInstances[RelationMethodDomainObject2].size())
        assertTrue (relatedInstances[RelationMethodDomainObject2].contains(expectedDomainObject2))
    }

}