package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 2:39:24 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveRelationMethodTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveRelation()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject4 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject5 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject6 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject7 = RelationMethodDomainObject2.add([:]);

        RelationMethodDomainObject2 notRelatedObject = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject1 anotherDomainObjectForManyToOneRelation = RelationMethodDomainObject1.add([:]);
        expectedDomainObject5.addRelation(revRel3:anotherDomainObjectForManyToOneRelation);

        def props = [rel1:expectedDomainObject2, rel2:[expectedDomainObject3,expectedDomainObject4], rel3:[expectedDomainObject5], rel4:[expectedDomainObject6, expectedDomainObject7]];
        expectedDomainObject1.addRelation(props);

        props = [rel1:[expectedDomainObject2], rel2:[expectedDomainObject3], rel3:[expectedDomainObject5], rel4:expectedDomainObject7];
        expectedDomainObject1.removeRelation(props);

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



        props = [rel1:[notRelatedObject], rel2:[notRelatedObject], rel3:[notRelatedObject], rel4:[notRelatedObject]];
        expectedDomainObject1.removeRelation(props);

        props = [rel1:null, rel2:null, rel3:null, rel4:null];
        expectedDomainObject1.removeRelation(props);
        
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

        expectedDomainObject6.removeRelation(revRel4:[expectedDomainObject1]);
        assertFalse (expectedDomainObject1.rel4.contains(expectedDomainObject6));
        assertFalse (expectedDomainObject6.revRel4.contains(expectedDomainObject1));



    }

    public void testRemoveRelationDiscardsObjectsWithUnknownObjectType()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2, RelationMethodDomainObject3, RelationMethodDomainObject4], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject3 expectedDomainObject3 = RelationMethodDomainObject3.add([:]);
        RelationMethodDomainObject3 expectedDomainObject4 = new RelationMethodDomainObject3();
        RelationMethodDomainObject4 expectedDomainObject5 = RelationMethodDomainObject4.add([:]);

        expectedDomainObject1.addRelation(rel2:[expectedDomainObject2, expectedDomainObject3]);
        expectedDomainObject1.removeRelation(rel2:[expectedDomainObject3, expectedDomainObject4, expectedDomainObject5]);
        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel2);
        assertNull(expectedDomainObject3.revRel2);
        assertTrue(expectedDomainObject1.rel2.contains(expectedDomainObject2));
        assertFalse(expectedDomainObject1.rel2.contains(expectedDomainObject3));
        assertTrue (expectedDomainObject1.hasErrors());

        assertEquals (1, relation.Relation.get(objectId:expectedDomainObject1.id, name:"rel2").relatedObjectIds.size());
        assertTrue (relation.Relation.get(objectId:expectedDomainObject1.id, name:"rel2").relatedObjectIds.containsKey(relation.Relation.getRelKey(expectedDomainObject2.id)));
        assertFalse (relation.Relation.get(objectId:expectedDomainObject1.id, name:"rel2").relatedObjectIds.containsKey(relation.Relation.getRelKey(expectedDomainObject3.id)));
    }


    public void testRemoveRelationWithNoOtherside()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject4 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject5 = RelationMethodDomainObject2.add([:]);
        expectedDomainObject1.addRelation(noOtherSideRel1:expectedDomainObject2, noOtherSideRel2:expectedDomainObject2, noOtherSideRel3:expectedDomainObject3, noOtherSideRel4:expectedDomainObject4);
        expectedDomainObject2.revRel1 = expectedDomainObject1;
        expectedDomainObject3.revRel2 = expectedDomainObject1;
        expectedDomainObject4.revRel3 += expectedDomainObject1;
        expectedDomainObject5.revRel4 += expectedDomainObject1;

        def props = [noOtherSideRel1:expectedDomainObject2, noOtherSideRel2:expectedDomainObject3, noOtherSideRel3:expectedDomainObject4, noOtherSideRel4:expectedDomainObject5];
        expectedDomainObject1.removeRelation(props);

        assertNull (expectedDomainObject1.rel1);
        assertTrue (expectedDomainObject1.rel2.isEmpty());
        assertNull (expectedDomainObject1.rel3);
        assertTrue (expectedDomainObject1.rel4.isEmpty());
    }
}