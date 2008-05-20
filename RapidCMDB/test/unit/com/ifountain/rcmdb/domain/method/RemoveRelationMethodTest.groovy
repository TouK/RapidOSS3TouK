package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy

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

        RelationMethodDomainObject1.indexList = [];
        RelationMethodDomainObject2.indexList = [];

        RemoveRelationMethod remove = new RemoveRelationMethod(RelationMethodDomainObject1.metaClass, relationsForObject1);
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

}