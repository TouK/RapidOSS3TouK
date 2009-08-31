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

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.compass.CompositeDirectoryWrapperProvider

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


    public void testAddReturnsErrosIfObjectDoesnotExist()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        expectedDomainObject1.remove();
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);

        expectedDomainObject1.addRelation(rel1:expectedDomainObject2);
        assertTrue (expectedDomainObject1.hasErrors());
        assertEquals ("default.not.exist.message", expectedDomainObject1.errors.allErrors[0].code);
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

    public void testAddMethodWithDatasource()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject2 expectedDomainObject3 = RelationMethodDomainObject2.add([:]);

        expectedDomainObject1.addRelation(rel1:expectedDomainObject2, "ds1");
        assertEquals(expectedDomainObject2, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel1);

        expectedDomainObject1.addRelation(rel1:expectedDomainObject3, "ds1");
        assertEquals(expectedDomainObject3, expectedDomainObject1.rel1);
        assertEquals(expectedDomainObject1, expectedDomainObject3.revRel1);
        assertNull(expectedDomainObject2.revRel1);
    }

    public void testAddMethodDiscardsObjectsWithUnknownObjectType()
    {
        initialize([RelationMethodDomainObject1, RelationMethodDomainObject2, RelationMethodDomainObject3, RelationMethodDomainObject4], []);
        RelationMethodDomainObject1 expectedDomainObject1 = RelationMethodDomainObject1.add([:]);
        RelationMethodDomainObject2 expectedDomainObject2 = RelationMethodDomainObject2.add([:]);
        RelationMethodDomainObject3 expectedDomainObject3 = RelationMethodDomainObject3.add([:]);
        RelationMethodDomainObject3 expectedDomainObject4 = new RelationMethodDomainObject3();
        RelationMethodDomainObject4 expectedDomainObject5 = RelationMethodDomainObject4.add([:]);

        expectedDomainObject1.addRelation(rel2:[expectedDomainObject2, expectedDomainObject3, expectedDomainObject4, expectedDomainObject5]);
        assertEquals(expectedDomainObject1, expectedDomainObject2.revRel2);
        assertEquals(expectedDomainObject1, expectedDomainObject3.revRel2);
        assertTrue(expectedDomainObject1.rel2.contains(expectedDomainObject2));
        assertTrue(expectedDomainObject1.rel2.contains(expectedDomainObject3));
        assertTrue (expectedDomainObject1.hasErrors());

        def allRelatedObjectIds = RelationUtils.getRelatedObjectsIds(expectedDomainObject1, "rel2", "revRel2")
        assertEquals (2, allRelatedObjectIds.size());
        assertTrue (allRelatedObjectIds.containsKey(expectedDomainObject2.id));
        assertTrue (allRelatedObjectIds.containsKey(expectedDomainObject3.id));
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

    public void testAddRelationDefinedInChildObjects()
    {
        def parentModelName = "ParentModel";
        def childModelName = "ChildModel";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def parentModelMetaProps = [name:parentModelName]
        def childModelMetaProps = [name:childModelName, parentModel:parentModelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def modelProps = [keyProp];
        def keyPropList = [keyProp];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, modelProps, keyPropList, [])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [rel1])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1])
        this.gcl.parseClass(parentModelString+childModelString+relatedModelString);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([parentModelClass,childModelClass, relatedModelClass], [])
        def childObj = childModelClass.'add'(keyProp:"child1");
        def relatedObj = relatedModelClass.'add'(keyProp:"relatedObj1");
        def returnedObject = childObj.addRelation(rel1:relatedObj);
        assertFalse(returnedObject.hasErrors());
        assertEquals(returnedObject.id, childObj.id);
        assertFalse (childObj.hasErrors());
        assertEquals (relatedObj.id, childObj.rel1[0].id);
    }

}

class RelationMethodDomainObject3 extends RelationMethodDomainObject2{
     //AUTO_GENERATED_CODE
    static searchable = {
        except = [];
    };
    static datasources = [:]
    static constraints={
    }
    static propertyConfiguration= [:]
    static transients = [];

    //AUTO_GENERATED_CODE
}


class RelationMethodDomainObject4{
     static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static constraints={
     __operation_class__(nullable:true)
     __is_federated_properties_loaded__(nullable:true)
     errors(nullable:true)
    }

    static relations = [:];
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
}