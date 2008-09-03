package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import relation.Relation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 8:56:03 AM
* To change this template use File | Settings | File Templates.
*/
class AddRelationMethodTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }


    public void testAddMethodWithOneToOneRelations()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject3 = RelationMethodDomainObject2.add([:]);
        
        expectedDomainObject1.addRelation(rel1:expectedDomainObject2);
        assertEquals(expectedDomainObject2, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel1);

        expectedDomainObject1.addRelation(rel1:expectedDomainObject3);
        assertEquals(expectedDomainObject3, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject3.revRel1);
        assertNull(expectedDomainObject2.revRel1);
    }


    public void testAddRelationMethodWithManyToOneRelations()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject1 relatedDomainObject2 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject4 = RelationMethodDomainObject2.add([:]);
        relatedDomainObject1.addRelation(rel3:relatedDomainObject3);
        relatedDomainObject2.addRelation(rel3:relatedDomainObject3);
        assertEquals(relatedDomainObject3, relatedDomainObject1.rel3);
        assertEquals(relatedDomainObject3, relatedDomainObject2.rel3);
        assertTrue(relatedDomainObject3.revRel3.contains(relatedDomainObject1));
        assertTrue(relatedDomainObject3.revRel3.contains(relatedDomainObject2));
        relatedDomainObject2.addRelation(rel3:[relatedDomainObject4]);
        assertEquals(relatedDomainObject4, relatedDomainObject2.rel3);
        println relatedDomainObject4.revRel3;
        assertTrue(relatedDomainObject4.revRel3.contains(relatedDomainObject2));
        assertFalse(relatedDomainObject3.revRel3.contains(relatedDomainObject2));
    }

    public void testAddRelationMethodWithOneToManyRelations()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject1 relatedDomainObject4 = RelationMethodDomainObject1.add([:]);
        relatedDomainObject1.addRelation(rel2:[relatedDomainObject2, relatedDomainObject3]);

        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel2);
        assertEquals(relatedDomainObject1, relatedDomainObject3.revRel2);
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject2));
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject3));
        relatedDomainObject4.addRelation(rel2:[relatedDomainObject2]);
//
        assertEquals(relatedDomainObject4, relatedDomainObject2.revRel2);
        assertTrue(relatedDomainObject4.rel2.contains(relatedDomainObject2));
        assertFalse(relatedDomainObject1.rel2.contains(relatedDomainObject2));

        relatedDomainObject4.addRelation(rel2:null);

        assertEquals(relatedDomainObject4, relatedDomainObject2.revRel2);
        assertTrue(relatedDomainObject4.rel2.contains(relatedDomainObject2));
        assertFalse(relatedDomainObject1.rel2.contains(relatedDomainObject2));
    }




    public void testAddRelationMethodWithManyToManyRelations()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);
        relatedDomainObject1.addRelation(rel4:relatedDomainObject2);

        assertTrue(relatedDomainObject1.rel4.contains(relatedDomainObject2));
        assertTrue(relatedDomainObject2.revRel4.contains(relatedDomainObject1));

        relatedDomainObject1.addRelation(rel4:null);
        assertTrue(relatedDomainObject1.rel4.contains(relatedDomainObject2));
        assertTrue(relatedDomainObject2.revRel4.contains(relatedDomainObject1));
    }

    public void testWithMultipleRelations()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject4 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject5 = RelationMethodDomainObject2.add([:]);
        relatedDomainObject1.addRelation(rel1:relatedDomainObject2, rel2:[relatedDomainObject3,relatedDomainObject4], rel4:relatedDomainObject5);

        assertEquals(relatedDomainObject2, relatedDomainObject1.rel1);
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject3));
        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject4));
        assertTrue(relatedDomainObject1.rel4.contains(relatedDomainObject5));
        assertTrue(relatedDomainObject5.revRel4.contains(relatedDomainObject1));
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel1);
        assertEquals(relatedDomainObject1, relatedDomainObject3.revRel2);
        assertEquals(relatedDomainObject1, relatedDomainObject4.revRel2);
    }

    public void testIfOneToOneRelationAlreadyExistsDoesnotAddTwice()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);
        relatedDomainObject1.addRelation(rel1:relatedDomainObject2);
        relatedDomainObject1.addRelation(rel1:relatedDomainObject2);

        assertEquals(relatedDomainObject2, relatedDomainObject1.rel1);
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel1);
    }

    public void testIfOneToManyRelationAlreadyExistsDoesnotAddTwice()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);

        relatedDomainObject1.addRelation(rel2:relatedDomainObject2);
        relatedDomainObject1.addRelation(rel2:relatedDomainObject2);

        assertTrue(relatedDomainObject1.rel2.contains(relatedDomainObject2));
        assertEquals(relatedDomainObject1, relatedDomainObject2.revRel2);
    }

    public void testWithNonExistingOtherSide()
    {

        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 relatedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 relatedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject3 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject4 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 relatedDomainObject5 = RelationMethodDomainObject2.add([:]);

        relatedDomainObject1.addRelation(noOtherSideRel1:relatedDomainObject2, noOtherSideRel2:relatedDomainObject3, noOtherSideRel3:relatedDomainObject4, noOtherSideRel4:relatedDomainObject5);
        assertEquals(relatedDomainObject2, relatedDomainObject1.noOtherSideRel1);
        assertTrue(relatedDomainObject1.noOtherSideRel2.contains(relatedDomainObject3));
        assertEquals(relatedDomainObject4, relatedDomainObject1.noOtherSideRel3);
        assertTrue(relatedDomainObject1.noOtherSideRel4.contains(relatedDomainObject5));
    }

}