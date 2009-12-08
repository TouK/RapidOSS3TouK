package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import relation.Relation
import com.ifountain.rcmdb.domain.util.RelationMetaData
import org.apache.lucene.search.BooleanQuery

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 13, 2009
* Time: 1:28:16 PM
* To change this template use File | Settings | File Templates.
*/
class RelationUtilsTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    private List initialize()
    {
        String model1Name = "Model1"
        String model2Name = "Model2"
        def model1MetaProps = [name:model1Name]
        def model2MetaProps = [name:model2Name]
        def modelProps = [];
        def modelKeyProps = [];
        def relations = []
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:model2Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def rel2 = [name:"rel2",  reverseName:"revrel2", toModel:model2Name, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def rel3 = [name:"rel3",  reverseName:"revrel3", toModel:model2Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def rel4 = [name:"rel4",  reverseName:"revrel4", toModel:model2Name, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def revrel2 = [name:"revrel2",  reverseName:"rel2", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        def revrel3 = [name:"revrel3",  reverseName:"rel3", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def revrel4 = [name:"revrel4",  reverseName:"rel4", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        String modelString = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, modelKeyProps, [rel1, rel2, rel3, rel4])
        String relatedModelString = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, modelKeyProps, [revrel1, revrel2, revrel3, revrel4])
        gcl.parseClass (modelString+relatedModelString);
        def modelClasses = Arrays.asList(gcl.loadedClasses)
        initialize(modelClasses, []);
        return modelClasses;
    }
    public void testAddRelatedObjects()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], null);
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj3], "source1");
        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (3, rels.size());
        Relation rel1 = rels[0];
        Relation rel2 = rels[1];
        Relation rel3 = rels[2];
        assertEquals (fromObj1.id, rel1.objectId);
        assertEquals (fromObj1.id, rel2.objectId);
        assertEquals (fromObj1.id, rel3.objectId);
        assertEquals (toObj1.id, rel1.reverseObjectId);
        assertEquals (toObj2.id, rel2.reverseObjectId);
        assertEquals (toObj3.id, rel3.reverseObjectId);
        assertEquals ("rel1", rel1.name);
        assertEquals ("rel1", rel2.name);
        assertEquals ("rel1", rel3.name);
        assertEquals ("revrel1", rel1.reverseName);
        assertEquals ("revrel1", rel2.reverseName);
        assertEquals ("revrel1", rel3.reverseName);
        assertEquals (RelationUtils.getSourceString(null), rel1.source);
        assertEquals (RelationUtils.getSourceString(null), rel2.source);
        assertEquals (RelationUtils.getSourceString("source1"), rel3.source);
    }

    public void testAddRelatedObjectsWhoseReverseDoesNotExist()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        RelationMetaData rel1MetaData = relations["rel1"]
        rel1MetaData.setOtherSideName (null)
        rel1MetaData.setName(null)
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], null);
        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (2, rels.size());
        Relation rel1 = rels[0];
        Relation rel2 = rels[1];
        assertEquals (fromObj1.id, rel1.objectId);
        assertEquals (fromObj1.id, rel2.objectId);
        assertEquals (toObj1.id, rel1.reverseObjectId);
        assertEquals (toObj2.id, rel2.reverseObjectId);
        assertEquals (RelationUtils.NULL_RELATION_NAME, rel1.name);
        assertEquals (RelationUtils.NULL_RELATION_NAME, rel2.name);
        assertEquals (RelationUtils.NULL_RELATION_NAME, rel1.reverseName);
        assertEquals (RelationUtils.NULL_RELATION_NAME, rel2.reverseName);
        assertEquals (RelationUtils.getSourceString(null), rel1.source);
        assertEquals (RelationUtils.getSourceString(null), rel2.source);
    }
    

    public void testRemoveRelations()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], null);
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj3], "source1");
        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (3, rels.size());

        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj1], null);
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (2, rels.size());
        assertNull (rels.find {it.reverseObjectId == toObj1.id});

        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj2, toObj3], null);
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals ("All specified relations including releation whose source is specified should be deleted since source in removeRelations is specified as null", 0, rels.size());

    }

    public void testRemoveRelationsWithSource()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def toObj5 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel1MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel1MetaData = relations["revrel1"]
        RelationUtils.addRelatedObjects(toObj5, revRel1MetaData, [fromObj1], "source1");
        
        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        //test with nonexisting sources
        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj3], "source1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj1], "source2");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        //test with not related object
        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj4], "source1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj1], "source1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (4, rels.size());
        assertEquals("Since relation object1 is not removed from fromObj2 this will not be deleted", 1, rels.findAll {it.reverseObjectId == toObj1.id}.size());
        assertEquals("Since relation object1 is not removed from fromObj2 this will not be deleted", fromObj2.id, rels.find {it.reverseObjectId == toObj1.id}.objectId);

        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj3], "source2");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (3, rels.size());
        assertNull (rels.find {it.reverseObjectId == toObj3.id});

        //test remove relation added from reverse side
        RelationUtils.removeRelations(fromObj1, rel1MetaData, [toObj5], "source1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (2, rels.size());
        assertNull (rels.find {it.objectId == toObj5.id});
    }

    public void testRemoveExistingRelations()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        def rel2MetaData = relations["rel2"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel1MetaData, [toObj1], "source1");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (4, rels.size());

        RelationUtils.removeExistingRelations (fromObj1, "rel1", "revrel1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (2, rels.size());
        assertNotNull(rels.find {it.reverseObjectId == toObj1.id && it.objectId == fromObj2.id});
        assertNotNull(rels.find {it.reverseObjectId == toObj3.id && it.objectId == fromObj1.id});

        RelationUtils.removeExistingRelations (fromObj2, "rel1", "revrel1");
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (1, rels.size());
        assertNotNull(rels.find {it.reverseObjectId == toObj3.id && it.objectId == fromObj1.id});
    }


    public void testRemoveExistingRelationsWithObjectId()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        def rel2MetaData = relations["rel2"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel1MetaData, [toObj1], "source1");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (4, rels.size());

        RelationUtils.removeExistingRelationsById(fromObj1.id);
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (1, rels.size());
        assertNotNull(rels.find {it.reverseObjectId == toObj1.id && it.objectId == fromObj2.id});

        RelationUtils.removeExistingRelationsById (fromObj2.id);
        rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (0, rels.size());
    }
    public void testGetRelatedObjectsIdsByObjectId()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        def rel2MetaData = relations["rel2"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel1MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel1MetaData = relations["revrel1"]
        RelationUtils.addRelatedObjects(toObj4, revRel1MetaData, [fromObj1], "source2");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        def relatedObjectIds = RelationUtils.getRelatedObjectsIdsByObjectId(fromObj1.id, "rel1", "revrel1");
        assertEquals (3,  relatedObjectIds.size());
        def returnedObjectConfig = relatedObjectIds[toObj1.id];
        assertTrue (returnedObjectConfig.reverseObjectId == toObj1.id && returnedObjectConfig.source == RelationUtils.getSourceString("source1"));
        returnedObjectConfig = relatedObjectIds[toObj2.id];
        assertTrue (returnedObjectConfig.reverseObjectId == toObj2.id && returnedObjectConfig.source == RelationUtils.getSourceString("source1"));
        returnedObjectConfig = relatedObjectIds[toObj4.id];
        assertTrue (returnedObjectConfig.objectId == toObj4.id && returnedObjectConfig.source == RelationUtils.getSourceString("source2"));

        relatedObjectIds = RelationUtils.getRelatedObjectsIdsByObjectId(fromObj1.id, "rel1", "revrel1", "source2");
        assertEquals (1,  relatedObjectIds.size());
        returnedObjectConfig = relatedObjectIds[toObj4.id];
        assertTrue (returnedObjectConfig.objectId == toObj4.id && returnedObjectConfig.source == RelationUtils.getSourceString("source2"));
    }

    public void testGetRelatedObjectsWithManyToManyRelation()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        def rel2MetaData = relations["rel2"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel1MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel1MetaData = relations["revrel1"]
        RelationUtils.addRelatedObjects(toObj4, revRel1MetaData, [fromObj1], "source2");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        def relatedObjects = RelationUtils.getRelatedObjects(fromObj1, rel1MetaData);
        assertEquals (3,  relatedObjects.size());
        assertNotNull (relatedObjects.find {it.id == toObj1.id});
        assertNotNull (relatedObjects.find {it.id == toObj2.id});
        assertNotNull (relatedObjects.find {it.id == toObj4.id});
        
        //test by id
        def relatedObjectsByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel1MetaData);
        assertEquals (3,  relatedObjectsByObjectId.size());
        relatedObjects.size().times{
            assertEquals(relatedObjects[it].id , relatedObjectsByObjectId[it].id)
        }

        //with source
        relatedObjects = RelationUtils.getRelatedObjects(fromObj1, rel1MetaData, "source2");
        assertEquals (1,  relatedObjects.size());
        assertNotNull (relatedObjects.find {it.id == toObj4.id});

        //test by id
        relatedObjectsByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel1MetaData,"source2");
        assertEquals (1,  relatedObjectsByObjectId.size());
        assertNotNull (relatedObjectsByObjectId.find {it.id == toObj4.id});
    }

    public void testGetRelatedObjectsWithOneToManyRelation()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel4MetaData = relations["rel4"]
        def rel2MetaData = relations["rel2"]
        RelationUtils.addRelatedObjects(fromObj1, rel4MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel4MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel4MetaData = relations["revrel4"]
        RelationUtils.addRelatedObjects(toObj4, revRel4MetaData, [fromObj1], "source2");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        def relatedObjects = RelationUtils.getRelatedObjects(fromObj1, rel4MetaData);
        assertEquals (3,  relatedObjects.size());
        assertNotNull (relatedObjects.find {it.id == toObj1.id});
        assertNotNull (relatedObjects.find {it.id == toObj2.id});
        assertNotNull (relatedObjects.find {it.id == toObj4.id});

        //test by id
        def relatedObjectsByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel4MetaData);
        assertEquals (3,  relatedObjectsByObjectId.size());
        relatedObjects.size().times{
            assertEquals(relatedObjects[it].id , relatedObjectsByObjectId[it].id)
        }

        //with source
        relatedObjects = RelationUtils.getRelatedObjects(fromObj1, rel4MetaData, "source2");
        assertEquals (1,  relatedObjects.size());
        assertNotNull (relatedObjects.find {it.id == toObj4.id});

        //test by id
        relatedObjectsByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel4MetaData,"source2");
        assertEquals (1,  relatedObjectsByObjectId.size());
        assertNotNull (relatedObjectsByObjectId.find {it.id == toObj4.id});
    }

    public void testGetRelatedObjectsWithOneToOneRelation()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel2MetaData = relations["rel2"]
        def rel4MetaData = relations["rel4"]
        RelationUtils.addRelatedObjects(fromObj1, rel2MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel4MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel2MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel2MetaData = relations["revrel2"]
        RelationUtils.addRelatedObjects(toObj4, revRel2MetaData, [fromObj1], "source1");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        def relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel2MetaData);
        assertEquals(toObj1.id, relatedObject.id);

        //test by id
        def relatedObjectByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel2MetaData);
        assertEquals(toObj1.id, relatedObjectByObjectId.id);


        //with source
        relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel2MetaData, "source1");
        assertEquals(toObj1.id, relatedObject.id);
        relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel2MetaData, "source2");
        assertNull(relatedObject)

        //test by id
        relatedObjectByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel2MetaData, "source1");
        assertEquals(toObj1.id, relatedObjectByObjectId.id);
        relatedObjectByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel2MetaData, "source2");
        assertNull(relatedObjectByObjectId)

    }

    public void testGetRelatedObjectsWithManyToOneRelation()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def fromObj1 = from.add([:]);
        def fromObj2 = from.add([:]);
        def toObj1 = to.add([:]);
        def toObj2 = to.add([:]);
        def toObj3 = to.add([:]);
        def toObj4 = to.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel3MetaData = relations["rel3"]
        def rel4MetaData = relations["rel4"]
        RelationUtils.addRelatedObjects(fromObj1, rel3MetaData, [toObj1, toObj2], "source1");
        RelationUtils.addRelatedObjects(fromObj1, rel4MetaData, [toObj3], "source2");
        RelationUtils.addRelatedObjects(fromObj2, rel3MetaData, [toObj1], "source1");

        //this relation is added from reverse reverse relations can also be deleted from other side
        relations =DomainClassUtils.getRelations(to.name);
        def revRel3MetaData = relations["revrel3"]
        RelationUtils.addRelatedObjects(toObj4, revRel3MetaData, [fromObj1], "source1");

        def rels = Relation.list([sort:"reverseObjectId"])
        assertEquals (5, rels.size());

        def relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel3MetaData);
        assertEquals(toObj1.id, relatedObject.id);

        //with source
        relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel3MetaData, "source1");
        assertEquals(toObj1.id, relatedObject.id);
        relatedObject = RelationUtils.getRelatedObjects(fromObj1, rel3MetaData, "source2");
        assertNull(relatedObject);
    }

    public void testGetRelatedObjectsWithHugeNumberOfRelatedObjects()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def objectToBeIndexed = [];
        def startId = 100000;
        def objectCount = BooleanQuery.getMaxClauseCount()+1;
        for(int i=0; i < objectCount; i++)
        {
            def obj = to.newInstance();
            obj.setProperty("id", startId++, false)
            objectToBeIndexed.add(obj);
        }
        to.index(objectToBeIndexed);
        assertEquals(objectCount, to.count());
        def fromObj1 = from.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, objectToBeIndexed, "source1");
        def relatedObjects = RelationUtils.getRelatedObjects(fromObj1, rel1MetaData);
        assertEquals (objectCount, relatedObjects.size());

        //test by id
        def relatedObjectsByObjectId = RelationUtils.getRelatedObjectsByObjectId(fromObj1.id, rel1MetaData);
        assertEquals (objectCount, relatedObjectsByObjectId.size());
    }

    public void testRemoveRelatedObjectsWithHugeNumberOfRelatedObjects()
    {
        List loadedClasses = initialize()
        Class from = loadedClasses[0]
        Class to = loadedClasses[1]
        def objectToBeIndexed = [];
        def startId = 1000000;
        def objectCount = BooleanQuery.getMaxClauseCount()+1;
        for(int i=0; i < objectCount; i++)
        {
            def obj = to.newInstance();
            obj.setProperty("id", startId++, false)
            objectToBeIndexed.add(obj);
        }
        to.index(objectToBeIndexed);
        assertEquals(objectCount, to.count());
        def fromObj1 = from.add([:]);
        def relations =DomainClassUtils.getRelations(from.name);
        def rel1MetaData = relations["rel1"]
        RelationUtils.addRelatedObjects(fromObj1, rel1MetaData, objectToBeIndexed, "source1");
        RelationUtils.removeRelations(fromObj1, rel1MetaData, objectToBeIndexed, "source1");
        assertEquals (0, Relation.count());
    }
}