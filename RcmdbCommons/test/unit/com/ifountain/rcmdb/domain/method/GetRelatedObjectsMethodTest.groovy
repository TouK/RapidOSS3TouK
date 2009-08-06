package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 6, 2009
* Time: 2:12:58 PM
* To change this template use File | Settings | File Templates.
*/
class GetRelatedObjectsMethodTest extends RapidCmdbWithCompassTestCase {
    Class from;
    Class to;
    public void setUp() {
        super.setUp();
        List modeledClasses = initialize();
        from = modeledClasses[0]
        to = modeledClasses[1]
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testGetRelatedObjectsWithOneToOneRelation() {
        def fromObj1 = from.add([:])
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);

        fromObj1.addRelation("rel2": toObj1);

        def relatedObject = fromObj1.getRelatedObjects("rel2");
        assertEquals(toObj1.id, relatedObject.id)

        fromObj1.addRelation(["rel2": toObj1], "source1")
        relatedObject = fromObj1.getRelatedObjects("rel2");
        assertEquals(toObj1.id, relatedObject.id)

        relatedObject = fromObj1.getRelatedObjects("rel2", "source1");
        assertEquals(toObj1.id, relatedObject.id)

        relatedObject = fromObj1.getRelatedObjects("rel2", "source2");
        assertNull(relatedObject)

        fromObj1.addRelation(["rel2": toObj2], "source1")
        relatedObject = fromObj1.getRelatedObjects("rel2");
        assertEquals(toObj2.id, relatedObject.id)

        relatedObject = fromObj1.getRelatedObjects("rel2", "source1");
        assertEquals(toObj2.id, relatedObject.id)
    }

    public void testGetRelatedObjectsWithOneToManyRelation() {
        def fromObj1 = from.add([:])
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);

        fromObj1.addRelation([rel4: [toObj1, toObj2]], "source1")
        fromObj1.addRelation([rel4: toObj1], "source2")
        fromObj1.addRelation([rel4: toObj3], "source2")

        def relatedObjects = fromObj1.getRelatedObjects("rel4");
        assertTrue(relatedObjects instanceof List);
        assertEquals(3, relatedObjects.size());

        def result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj2.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj3.id}
        assertNotNull(result);

        relatedObjects = fromObj1.getRelatedObjects("rel4", "source1");
        assertEquals(2, relatedObjects.size());
        result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj2.id}
        assertNotNull(result);

        relatedObjects = fromObj1.getRelatedObjects("rel4", "source2");
        assertEquals(2, relatedObjects.size());
        result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj3.id}
        assertNotNull(result);

        //opposite side
        def relatedObject = toObj1.getRelatedObjects("revrel4")
        assertNotNull(relatedObject)
        assertEquals(fromObj1.id, relatedObject.id)

        relatedObject = toObj1.getRelatedObjects("revrel4", "source1")
        assertNotNull(relatedObject)
        assertEquals(fromObj1.id, relatedObject.id)

        relatedObject = toObj1.getRelatedObjects("revrel4", "source2")
        assertNotNull(relatedObject)
        assertEquals(fromObj1.id, relatedObject.id)

        relatedObject = toObj1.getRelatedObjects("revrel4", "unknownsource")
        assertNull(relatedObject)
    }

    public void testGetRelatedObjectsWithManyToManyRelation() {
        def fromObj1 = from.add([:])
        def fromObj2 = from.add([:])
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);

        fromObj1.addRelation([rel1: [toObj1, toObj2]], "source1")
        fromObj1.addRelation([rel1: toObj1], "source2")
        fromObj1.addRelation([rel1: toObj3], "source2")

        toObj1.addRelation([revrel1: fromObj2], "source2")

        def relatedObjects = fromObj1.getRelatedObjects("rel1");
        assertTrue(relatedObjects instanceof List);
        assertEquals(3, relatedObjects.size());

        def result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj2.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj3.id}
        assertNotNull(result);

        relatedObjects = fromObj1.getRelatedObjects("rel1", "source1");
        assertEquals(2, relatedObjects.size());
        result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj2.id}
        assertNotNull(result);

        relatedObjects = fromObj1.getRelatedObjects("rel1", "source2");
        assertEquals(2, relatedObjects.size());
        result = relatedObjects.find {it.id == toObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == toObj3.id}
        assertNotNull(result);


        // opposite side
        relatedObjects = toObj1.getRelatedObjects("revrel1");
        assertTrue(relatedObjects instanceof List);
        assertEquals(2, relatedObjects.size());
        result = relatedObjects.find {it.id == fromObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == fromObj2.id}
        assertNotNull(result);

        relatedObjects = toObj1.getRelatedObjects("revrel1", "source1");
        assertEquals(1, relatedObjects.size())
        assertEquals(fromObj1.id, relatedObjects[0].id)

        relatedObjects = toObj1.getRelatedObjects("revrel1", "source2");
        assertEquals(2, relatedObjects.size())
        result = relatedObjects.find {it.id == fromObj1.id}
        assertNotNull(result);
        result = relatedObjects.find {it.id == fromObj2.id}
        assertNotNull(result);

        relatedObjects = toObj1.getRelatedObjects("revrel1", "unknownsource");
        assertEquals(0, relatedObjects.size())
    }

    public void testGetRelatedObjectsThrowExceptionIfRelationNameIsInvalid(){
        def fromObj1 = from.add([:])
        try{
            fromObj1.getRelatedObjects("invalidRel")
            fail("should throw exception")
        }
        catch(MissingPropertyException e){
            assertEquals("No such relation: invalidRel for class: ${fromObj1.class.name}", e.getMessage())
        }
    }

    private List initialize()
    {
        String model1Name = "Model1"
        String model2Name = "Model2"
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name]
        def modelProps = [];
        def modelKeyProps = [];
        def relations = []
        def rel1 = [name: "rel1", reverseName: "revrel1", toModel: model2Name, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def rel2 = [name: "rel2", reverseName: "revrel2", toModel: model2Name, cardinality: ModelGenerator.RELATION_TYPE_ONE, reverseCardinality: ModelGenerator.RELATION_TYPE_ONE, isOwner: true];
        def rel3 = [name: "rel3", reverseName: "revrel3", toModel: model2Name, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_ONE, isOwner: true];
        def rel4 = [name: "rel4", reverseName: "revrel4", toModel: model2Name, cardinality: ModelGenerator.RELATION_TYPE_ONE, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def revrel1 = [name: "revrel1", reverseName: "rel1", toModel: model1Name, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: false];
        def revrel2 = [name: "revrel2", reverseName: "rel2", toModel: model1Name, cardinality: ModelGenerator.RELATION_TYPE_ONE, reverseCardinality: ModelGenerator.RELATION_TYPE_ONE, isOwner: false];
        def revrel3 = [name: "revrel3", reverseName: "rel3", toModel: model1Name, cardinality: ModelGenerator.RELATION_TYPE_ONE, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: false];
        def revrel4 = [name: "revrel4", reverseName: "rel4", toModel: model1Name, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_ONE, isOwner: false];
        String modelString = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, modelKeyProps, [rel1, rel2, rel3, rel4])
        String relatedModelString = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, modelKeyProps, [revrel1, revrel2, revrel3, revrel4])
        gcl.parseClass(modelString + relatedModelString);
        def modelClasses = Arrays.asList(gcl.loadedClasses)
        initialize(modelClasses, []);
        return modelClasses;
    }
}