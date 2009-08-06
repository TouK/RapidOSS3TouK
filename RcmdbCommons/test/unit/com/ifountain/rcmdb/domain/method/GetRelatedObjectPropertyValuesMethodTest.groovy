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

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.lucene.search.BooleanQuery

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 2, 2008
 * Time: 6:22:21 PM
 * To change this template use File | Settings | File Templates.
 */
class GetRelatedObjectPropertyValuesMethodTest extends RapidCmdbWithCompassTestCase
{
    Class modelClass;
    Class relatedModelClass;
    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testGetRelatedObjectProperties()
    {
        createModelClasses();
        initialize([modelClass, relatedModelClass], [])

        def relatedModelInstance1 = relatedModelClass.'add'(keyProp: "relatedModel1", prop1: "instance1Prop1Value", prop2: 5, prop3: "instance1Prop3Value");
        def relatedModelInstance2 = relatedModelClass.'add'(keyProp: "relatedModel2", prop1: "instance2Prop1Value", prop2: 7, prop3: "instance2Prop3Value");
        def relatedModelInstance3 = relatedModelClass.'add'(keyProp: "relatedModel3", prop1: "instance3Prop1Value", prop2: 8, prop3: "instance3Prop3Value");
        def modelInstance1 = modelClass.'add'(keyProp: "model1", rel1: [relatedModelInstance1, relatedModelInstance2]);
        def modelInstance2 = modelClass.'add'(keyProp: "model2", rel1: [relatedModelInstance3]);
        def modelInstance3 = modelClass.'add'(keyProp: "model2");
        println "REL MODEL ERRORS:" + relatedModelInstance1.errors
        assertFalse(relatedModelInstance1.hasErrors());
        assertFalse(relatedModelInstance2.hasErrors());
        assertFalse(relatedModelInstance3.hasErrors());
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());

        println "RELATED MODELS:" + modelInstance1.rel1;

        List results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"]);
        assertEquals(2, results.size());
        def result = results.find {it.prop1 == "instance1Prop1Value"}
        assertEquals(4, result.size())
        assertEquals("instance1Prop3Value", result.prop3);
        assertEquals(relatedModelClass.name, result.alias);
        assertEquals(relatedModelInstance1.id, result.id);

        result = results.find {it.prop1 == "instance2Prop1Value"}
        assertEquals(4, result.size())
        assertEquals("instance2Prop3Value", result.prop3);
        assertEquals(relatedModelClass.name, result.alias);
        assertEquals(relatedModelInstance2.id, result.id);

        results = modelInstance2.getRelatedModelPropertyValues("rel1", ["undefinedProp", "prop1"]);
        assertEquals(1, results.size());
        assertEquals(3, results[0].size())
        assertEquals("instance3Prop1Value", results[0].prop1);
        assertEquals(relatedModelClass.name, results[0].alias);
        assertEquals(relatedModelInstance3.id, results[0].id);

        results = modelInstance3.getRelatedModelPropertyValues("rel1", ["prop1"]);
        assertEquals(0, results.size());

        results = modelInstance1.getRelatedModelPropertyValues("undefinedrel", ["prop1"]);
        assertEquals(0, results.size());


        results = modelInstance2.getRelatedModelPropertyValues("rel1", []);
        assertEquals(1, results.size());
        assertEquals(2, results[0].size())
        assertEquals(relatedModelClass.name, results[0].alias);
        assertEquals(relatedModelInstance3.id, results[0].id);
    }

    public void testGetRelatedObjectPropertiesWithSource(){
        createModelClasses();
        initialize([modelClass, relatedModelClass], [])

        def relatedModelInstance1 = relatedModelClass.'add'(keyProp: "relatedModel1", prop1: "instance1Prop1Value", prop2: 5, prop3: "instance1Prop3Value");
        def relatedModelInstance2 = relatedModelClass.'add'(keyProp: "relatedModel2", prop1: "instance2Prop1Value", prop2: 7, prop3: "instance2Prop3Value");
        def relatedModelInstance3 = relatedModelClass.'add'(keyProp: "relatedModel3", prop1: "instance3Prop1Value", prop2: 8, prop3: "instance3Prop3Value");
        def modelInstance1 = modelClass.'add'(keyProp: "model1");
        def modelInstance2 = modelClass.'add'(keyProp: "model2");
        def modelInstance3 = modelClass.'add'(keyProp: "model2");
        assertFalse(relatedModelInstance1.hasErrors());
        assertFalse(relatedModelInstance2.hasErrors());
        assertFalse(relatedModelInstance3.hasErrors());
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());
        
        modelInstance1.addRelation([rel1:relatedModelInstance1], "source1")
        modelInstance1.addRelation([rel1:relatedModelInstance2], "source2")
        modelInstance2.addRelation([rel1:relatedModelInstance3], "source1")

        List results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"], [:], "source1");
        assertEquals(1, results.size());
        def result = results[0]
        assertEquals(4, result.size())
        assertEquals("instance1Prop3Value", result.prop3);
        assertEquals(relatedModelClass.name, result.alias);
        assertEquals(relatedModelInstance1.id, result.id);

        results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"], [:], "source2");
        assertEquals(1, results.size());
        result = results[0]
        assertEquals(4, result.size())
        assertEquals("instance2Prop3Value", result.prop3);
        assertEquals(relatedModelClass.name, result.alias);
        assertEquals(relatedModelInstance2.id, result.id);

        results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"], [:], "nonexistantsource");
        assertEquals(0, results.size());

        results = modelInstance2.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"], [:],"source1");
        assertEquals(1, results.size());
        assertEquals(4, results[0].size())
        assertEquals("instance3Prop1Value", results[0].prop1);
        assertEquals(relatedModelClass.name, results[0].alias);
        assertEquals(relatedModelInstance3.id, results[0].id);

        results = modelInstance2.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"], [:],"source2");
        assertEquals(0, results.size());

    }

    public void testGetRelatedObjectPropertiesWithHugeNumberOfObjects()
    {
        def defaultMaxCount = BooleanQuery.getMaxClauseCount();
        try
        {
            def newMaxClause = 250;
            BooleanQuery.setMaxClauseCount (newMaxClause);
            createModelClasses();
            initialize([modelClass, relatedModelClass], [])

            def relatedModels = [];
            def numberOfRelatedObjects = newMaxClause+100;
            for(int i=0; i < numberOfRelatedObjects; i++)
            {
                def addedRelatedObject = relatedModelClass.'add'(keyProp: "relatedModel"+i, prop1: "instance1Prop1Value"+i, prop2: 5, prop3: "instance1Prop3Value");
                assertFalse (addedRelatedObject.hasErrors());
                relatedModels.add(addedRelatedObject);
            }
            def modelInstance1 = modelClass.'add'(keyProp: "model1", rel1: relatedModels);
            assertFalse(modelInstance1.hasErrors());


            List results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1", "prop3"]);
            assertEquals (numberOfRelatedObjects, results.size());
        }
        finally{
            BooleanQuery.setMaxClauseCount (defaultMaxCount);   
        }
    }

    public void testGetRelatedObjectPropertiesWithQueryOptions() {
        createModelClasses();
        initialize([modelClass, relatedModelClass], [])
        def relatedModelInstance1 = relatedModelClass.'add'(keyProp: "relatedModel1", prop1: "instance1Prop1Value", prop2: 5, prop3: "instance1Prop3Value");
        def relatedModelInstance2 = relatedModelClass.'add'(keyProp: "relatedModel2", prop1: "instance2Prop1Value", prop2: 7, prop3: "instance2Prop3Value");
        def relatedModelInstance3 = relatedModelClass.'add'(keyProp: "relatedModel3", prop1: "instance3Prop1Value", prop2: 8, prop3: "instance3Prop3Value");
        def relatedModelInstance4 = relatedModelClass.'add'(keyProp: "relatedModel4", prop1: "instance4Prop1Value", prop2: 9, prop3: "instance4Prop3Value");
        def modelInstance1 = modelClass.'add'(keyProp: "model1", rel1: [relatedModelInstance1, relatedModelInstance2, relatedModelInstance3, relatedModelInstance4]);
        assertFalse(relatedModelInstance1.hasErrors());
        assertFalse(modelInstance1.hasErrors());
        assertFalse(relatedModelInstance2.hasErrors());
        assertFalse(relatedModelInstance3.hasErrors());
        assertFalse(relatedModelInstance4.hasErrors());

        List results = modelInstance1.getRelatedModelPropertyValues("rel1", ["prop1"], [sort: "prop1", order: "desc", max: 2, offset: 1]);
        assertEquals(2, results.size());

        def result = results[0];
        assertEquals("instance3Prop1Value", result.prop1)
        result = results[1];
        assertEquals("instance2Prop1Value", result.prop1)
    }

    private void createModelClasses() {
        def modelName = "ChildModel" + GetRelatedObjectPropertyValuesMethodTest.simpleName;
        def relatedModelName = "RelatedModel" + GetRelatedObjectPropertyValuesMethodTest.simpleName;
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop2 = [name: "prop2", type: ModelGenerator.NUMBER_TYPE, blank: false];
        def prop3 = [name: "prop3", type: ModelGenerator.STRING_TYPE, blank: false];
        def rel1 = [name: "rel1", reverseName: "revrel1", toModel: relatedModelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def revrel1 = [name: "revrel1", reverseName: "rel1", toModel: modelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: false];

        def modelMetaProps = [name: modelName]
        def relatedModelMetaProps = [name: relatedModelName]
        def modelProps = [keyProp, prop1, prop2, prop3];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, [], [], [rel1])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1])
        this.gcl.parseClass(modelString + relatedModelString);
        modelClass = this.gcl.loadClass(modelName);
        relatedModelClass = this.gcl.loadClass(relatedModelName);
    }

}