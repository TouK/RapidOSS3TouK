package plugintests

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.domain.method.UpdateMethod
import com.ifountain.rcmdb.domain.method.AddMethod
import com.ifountain.rcmdb.domain.DomainLockManager
import org.apache.log4j.Logger
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 23, 2009
* Time: 4:34:15 PM
* To change this template use File | Settings | File Templates.
*/
class SearchableExtensionPluginTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }


    public void testAddMethods()
    {
        Map classes = initializePluginAndClasses();
        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals(addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(addedObjectProps.prop1, objectInRepo.prop1);

        //test adding same object multiple time will update object
        addedObjectProps = [keyProp: "object1", prop1: "prop1ValueUpdated"]
        addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals(addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(addedObjectProps.prop1, objectInRepo.prop1);

        //test adding same object multiple time will return error with addUnique
        def addUniqueObjectProps = [keyProp: "object1", prop1: "prop1ValueUpdatedAddUnique"]
        def addedObjectWithUnique = classes.child.addUnique(addedObjectProps);

        assertTrue(addedObjectWithUnique.hasErrors());
        assertNull(addedObjectWithUnique.id);
        assertEquals("rapidcmdb.instance.already.exist", addedObjectWithUnique.errors.allErrors[0].code);
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals(addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(addedObjectProps.prop1, objectInRepo.prop1);
    }


    public void testBulkAddMethod()
    {
        Map classes = initializePluginAndClasses();
        def objectsToBeAdded = [];
        objectsToBeAdded[0] = [keyProp: "object1", prop1: "prop1Value1"]
        objectsToBeAdded[1] = [keyProp: "object2", prop1: "prop1Value2"]
        objectsToBeAdded[2] = [keyProp: "object3", prop1: "prop1Value2"]
        def addedObjects = classes.child.bulkAdd(objectsToBeAdded);
        assertEquals(objectsToBeAdded.size(), addedObjects.size());
        for (int i = 0; i < addedObjects.size(); i++) {
            def addedObject = addedObjects[i];
            def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
            assertEquals(objectsToBeAdded[i].keyProp, objectInRepo.keyProp);
            assertEquals(objectsToBeAdded[i].prop1, objectInRepo.prop1);
        }
    }

    public void testBulkAddMethodSynchronization()
    {
        System.setProperty("compass.transaction.lockTimeout", "1")
        try
        {
            DomainLockManager.initialize(10000000, Logger.getRootLogger());
            Map classes = initializePluginAndClasses([:], true);
            def objectCount  =200;
            def objectsToBeAddedgr1 = [];
            for (int i = 0; i < objectCount; i++)
            {
                objectsToBeAddedgr1[i] = [keyProp: "object" + i, prop1: "prop1Value" + i]
            }
            def objectsToBeAddedgr2 = [];
            for (int i = 0; i < objectCount; i++)
            {
                objectsToBeAddedgr2[i] = [keyProp: "gr2object" + i, prop1: "prop1Value" + i]
            }

            def objectsToBeAddedgr3 = [];
            for (int i = 0; i < objectCount; i++)
            {
                objectsToBeAddedgr3[i] = [keyProp: "gr3object" + i, prop1: "prop1Value" + i]
            }
            Object lock1 = new Object();
            def threads = [];
            def threadCount = 50;
            def objectBatchSize = 50;
            def threadStates = new ArrayList(threadCount);
            boolean willWait = true;
            for (int i = 0; i < threadCount; i++)
            {
                def locali = i;
                threadStates[locali] = -1
                Thread t = Thread.start {
                    try {
                        threadStates[locali] = 0;
                        synchronized (lock1)
                        {
                            if (willWait)
                            {
                                lock1.wait();
                            }
                        }
                        threadStates[locali] = 1;
                        def objectsToBeAdded = objectsToBeAddedgr1;
                        if (locali % 3 == 1)
                        {
                            objectsToBeAdded = objectsToBeAddedgr2;
                        }
                        else if (locali % 3 == 2)
                        {
                            objectsToBeAdded = objectsToBeAddedgr3;
                        }
                        def k = 0;
                        while (k < objectsToBeAdded.size())
                        {
                            def objs = [];
                            for (int m = 0; m < objectBatchSize && k < objectsToBeAdded.size(); m++)
                            {
                                objs.add(objectsToBeAdded[k])
                                k++;
                            }
                            if (locali % 3 == 0)
                            {
                                classes.child.bulkAdd(objs);
                            }
                            else if (locali % 3 == 1)
                            {
                                classes.child2.bulkAdd(objs);
                            }
                            else if (locali % 3 == 2)
                            {
                                classes.parent.bulkAdd(objs);
                            }
                        }

                        threadStates[locali] = 2;
                    } catch (Exception e)
                    {
                        e.printStackTrace()
                        threadStates[locali] = 3;
                    }
                }
                threads.add(t);
            }
            CommonTestUtils.waitFor (new ClosureWaitAction({
                threadStates.each {threadState->

                    assertEquals(new Integer(0), threadState);
                }
            }), 400);
            synchronized (lock1)
            {
                lock1.notifyAll();
            }
            threads.each {
                it.join();
            }
            threadStates.each {
                assertEquals(new Integer(2), it);
            }
        }
        finally {
            System.clearProperty("compass.transaction.lockTimeout")
        }

    }


    public void testAddMethodsWithTriggeringEvents()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass("""
        class ${classes.child.name}Operations extends ${AbstractDomainOperation.class.name}{
            def beforeInsert()
            {
                ${DataStore.class.name}.put("beforeInsert", true);
            }
            def afterInsert()
            {
                ${DataStore.class.name}.put("afterInsert", true);
            }
        }
        """)
        CompassForTests.addOperationSupport(classes.child, operationClass);
        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

        assertTrue(DataStore.get("beforeInsert"));
        assertTrue(DataStore.get("afterInsert"));

    }

    //this test is written to check whether can we access relation inside other validators
    public void testAddMethodWithConstraintsAndAccessingRelationFromPropertyConstraint()
    {
        def errorCode = "invalid.relation.object"
        def replacementParts = [
                child: [
                        ["static\\s*constraints\\s*=\\s*\\{", """static constraints={
                                prop1(nullable:true, blank:true, validator:{val, obj ->
                                    println obj.rel1
                                    if(obj.rel1.size() == 0)
                                    {
                                        return ["${errorCode}"];
                                    }
                                }
                            );"""
                        ]
                ],
        ]
        Map classes = initializePluginAndClasses(replacementParts);

        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertTrue(addedObject.hasErrors());
        assertEquals(errorCode, addedObject.errors.allErrors[0].code);

        def relatedObjectProps = [keyProp: "relatedObj1", prop1: "prop1Value"]
        def relatedObject = classes.related.add(relatedObjectProps);
        assertFalse(relatedObject.hasErrors());

        addedObjectProps = [keyProp: "object2", prop1: "prop1Value", rel1: relatedObject]
        addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

    }

    //this test is written to check whether can we access relation inside other validators
    public void testUpdateethodWithConstraintsAndAccessingRelationFromPropertyConstraint()
    {
        def errorCode = "invalid.relation.object"
        def replacementParts = [
                child: [
                        ["static\\s*constraints\\s*=\\s*\\{", """static constraints={
                                prop1(nullable:true, blank:true, validator:{val, obj ->
                                    println obj.class.name
                                    if(obj.rel1.size() == 0)
                                    {
                                        return ["${errorCode}"];
                                    }
                                }
                            );"""
                        ]
                ],
        ]
        Map classes = initializePluginAndClasses(replacementParts);

        def relatedObjectProps = [keyProp: "relatedObj1", prop1: "prop1Value"]
        def relatedObject = classes.related.add(relatedObjectProps);
        assertFalse(relatedObject.hasErrors());

        def addedObjectProps = [keyProp: "object2", prop1: "prop1Value", rel1: relatedObject]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

        def updatedObjectProps = [keyProp: "object2", prop1: "prop1UpdatedValue"]
        def updatedObject = addedObject.update(updatedObjectProps);
        assertFalse(updatedObject.hasErrors());
        assertFalse(addedObject.hasErrors());

        updatedObjectProps = [keyProp: "object2", prop1: "prop1UpdatedValue", rel1: []];
        updatedObject = addedObject.update(updatedObjectProps);
        assertTrue(updatedObject.hasErrors());
        assertTrue(addedObject.hasErrors());

    }

    public void testRemoveMethod()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass("""
        class ${classes.child.name}Operations extends ${AbstractDomainOperation.class.name}{
            def beforeDelete()
            {
                ${DataStore.class.name}.put("beforeDelete", true);
            }
            def afterDelete()
            {
                ${DataStore.class.name}.put("afterDelete", true);
            }
        }
        """)
        CompassForTests.addOperationSupport(classes.child, operationClass);
        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals(addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(addedObjectProps.prop1, objectInRepo.prop1);

        //test remove
        addedObject.remove();
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertNull(objectInRepo);
        assertTrue(DataStore.get("beforeDelete"));
        assertTrue(DataStore.get("afterDelete"));
    }

    public void testUpdateMethod()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass("""
        class ${classes.child.name}Operations extends ${AbstractDomainOperation.class.name}{
            def beforeUpdate(params)
            {
                ${DataStore.class.name}.put("beforeUpdate", params);
            }
            def afterUpdate(params)
            {
                ${DataStore.class.name}.put("afterUpdate", params);
            }
        }
        """)
        CompassForTests.addOperationSupport(classes.child, operationClass);
        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals(addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(addedObjectProps.prop1, objectInRepo.prop1);

        //test update
        def updatedObjectProps = [keyProp: "object2", prop1: "prop1ValueUpdated"]
        addedObject.update(updatedObjectProps);
        assertFalse(addedObject.hasErrors());
        objectInRepo = classes.child.search("keyProp:${addedObjectProps.keyProp}").results[0];
        assertNull(objectInRepo)
        objectInRepo = classes.child.search("keyProp:${updatedObjectProps.keyProp}").results[0];
        assertEquals(updatedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals(updatedObjectProps.prop1, objectInRepo.prop1);

        //test events are called
        assertEquals(addedObjectProps.keyProp, DataStore.get("beforeUpdate")[UpdateMethod.UPDATED_PROPERTIES].keyProp);
        assertEquals(addedObjectProps.prop1, DataStore.get("beforeUpdate")[UpdateMethod.UPDATED_PROPERTIES].prop1);
        assertEquals(addedObjectProps.keyProp, DataStore.get("afterUpdate")[UpdateMethod.UPDATED_PROPERTIES].keyProp);
        assertEquals(addedObjectProps.prop1, DataStore.get("afterUpdate")[UpdateMethod.UPDATED_PROPERTIES].prop1);
    }

    public void testAddRemoveRelations()
    {
        Map classes = initializePluginAndClasses();
        def addedObjectProps1 = [keyProp: "object1", prop1: "prop1Value1"]
        def relatedObject1Props = [keyProp: "object2", prop1: "prop1Value2"]
        def addedObject1 = classes.child.add(addedObjectProps1);
        def relatedObject1 = classes.related.add(relatedObject1Props);
        assertFalse(addedObject1.hasErrors());
        assertFalse(relatedObject1.hasErrors());
        assertEquals(1, classes.child.countHits("id:${addedObject1.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject1.id}"));
        def object1InRepo = classes.child.search("id:${addedObject1.id}").results[0];

        assertEquals(0, addedObject1.rel1.size());
        assertEquals(0, object1InRepo.rel1.size());

        //test add relation
        addedObject1.addRelation("rel1": relatedObject1);

        assertEquals(1, addedObject1.rel1.size())
        assertEquals(relatedObject1.id, addedObject1.rel1[0].id)
        assertEquals(addedObject1.id, relatedObject1.revrel1[0].id)

        //test remove relation
        addedObject1.removeRelation("rel1": relatedObject1);
        assertEquals(0, addedObject1.rel1.size())
        assertEquals(0, relatedObject1.revrel1.size())

    }


    public void testBulkAddRelations()
    {
        Map classes = initializePluginAndClasses();
        def addedObjectProps1 = [keyProp: "object1", prop1: "prop1Value1"]
        def addedObjectProps2 = [keyProp: "object2", prop1: "prop1Value2"]
        def relatedObject1Props = [keyProp: "object2", prop1: "prop1Value2"]
        def relatedObject2Props = [keyProp: "object4", prop1: "prop1Value4"]
        def relatedObject3Props = [keyProp: "object5", prop1: "prop1Value5"]
        def addedObject1 = classes.child.add(addedObjectProps1);
        def addedObject2 = classes.child.add(addedObjectProps2);
        def relatedObject1 = classes.related.add(relatedObject1Props);
        def relatedObject2 = classes.related.add(relatedObject2Props);
        def relatedObject3 = classes.related.add(relatedObject3Props);
        assertFalse(addedObject1.hasErrors());
        assertFalse(addedObject2.hasErrors());
        assertFalse(relatedObject1.hasErrors());
        assertFalse(relatedObject2.hasErrors());
        assertFalse(relatedObject3.hasErrors());
        assertEquals(1, classes.child.countHits("id:${addedObject1.id}"));
        assertEquals(1, classes.child.countHits("id:${addedObject2.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject1.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject2.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject3.id}"));
        def object1InRepo = classes.child.search("id:${addedObject1.id}").results[0];

        assertEquals(0, addedObject1.rel1.size());
        assertEquals(0, object1InRepo.rel1.size());

        //test bulk add relation
        def returnedObjects = classes.child.bulkAddRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1, relatedObject3]]],
                        [object: addedObject2, relations: ["rel1": relatedObject2]]
                ]
        );
        assertEquals(2, returnedObjects.size());
        assertEquals(2, returnedObjects[0].rel1.size());
        assertEquals(1, returnedObjects[1].rel1.size());
        assertNotNull(returnedObjects[0].rel1.find {it.id == relatedObject1.id});
        assertNotNull(returnedObjects[0].rel1.find {it.id == relatedObject3.id});
        assertNotNull(returnedObjects[1].rel1.find {it.id == relatedObject2.id});

        //test bulk remove relation
        def returnedObjectsAfterBulkRemove = classes.child.bulkRemoveRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1]]],
                        [object: addedObject2, relations: ["rel1": relatedObject2]]
                ]
        );
        assertEquals(2, returnedObjects.size());
        assertEquals(1, returnedObjects[0].rel1.size());
        assertEquals(0, returnedObjects[1].rel1.size());
        assertNotNull(returnedObjects[0].rel1.find {it.id == relatedObject3.id});
    }

    public void testBulkAddRelationsWithSource()
    {
        Map classes = initializePluginAndClasses();
        def addedObjectProps1 = [keyProp: "object1", prop1: "prop1Value1"]
        def relatedObject1Props = [keyProp: "object2", prop1: "prop1Value2"]
        def relatedObject2Props = [keyProp: "object4", prop1: "prop1Value4"]
        def addedObject1 = classes.child.add(addedObjectProps1);
        def relatedObject1 = classes.related.add(relatedObject1Props);
        def relatedObject2 = classes.related.add(relatedObject2Props);
        assertFalse(addedObject1.hasErrors());
        assertFalse(relatedObject1.hasErrors());
        assertFalse(relatedObject2.hasErrors());
        assertEquals(1, classes.child.countHits("id:${addedObject1.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject1.id}"));
        assertEquals(1, classes.related.countHits("id:${relatedObject2.id}"));
        def object1InRepo = classes.child.search("id:${addedObject1.id}").results[0];

        assertEquals(0, addedObject1.rel1.size());
        assertEquals(0, object1InRepo.rel1.size());

        //test bulk add relation
        def returnedObjects1 = classes.child.bulkAddRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1, relatedObject2]], source: "src1"],
                ]
        );
        def returnedObjects2 = classes.child.bulkAddRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1, relatedObject2]], source: "src2"],
                ]
        );
        assertEquals(1, returnedObjects1.size());
        assertEquals(2, returnedObjects1[0].rel1.size());
        assertNotNull(returnedObjects1[0].rel1.find {it.id == relatedObject1.id});
        assertNotNull(returnedObjects1[0].rel1.find {it.id == relatedObject2.id});

        //remove relation with src1 and see relation is still accessible
        def returnedObjectsAfterBulkRemove = classes.child.bulkRemoveRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1, relatedObject2]], source: "src1"],
                ]
        );
        assertEquals(2, returnedObjectsAfterBulkRemove[0].rel1.size());
        assertNotNull(returnedObjectsAfterBulkRemove[0].rel1.find {it.id == relatedObject1.id});
        assertNotNull(returnedObjectsAfterBulkRemove[0].rel1.find {it.id == relatedObject2.id})

        //after removing relation with src2 
        returnedObjectsAfterBulkRemove = classes.child.bulkRemoveRelation(
                [
                        [object: addedObject1, relations: ["rel1": [relatedObject1, relatedObject2]], source: "src2"],
                ]
        );
        assertEquals("relations should be removed from repository completely since all sources removed", 0, returnedObjectsAfterBulkRemove[0].rel1.size());
    }

    public void testOverwritingStaticPersistanceMethods()
    {
        
    }

    private Map initializePluginAndClasses()
    {
        initializePluginAndClasses([:]);
    }
    private Map initializePluginAndClasses(Map additionalParts, boolean isPersisted = false)
    {
        def parentModelName = "ParentModel";
        def childModelName = "ChildModel";
        def childModel2Name = "ChildModel2";
        def relatedModelName = "RelatedModel";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def rel1 = [name: "rel1", reverseName: "revrel1", toModel: relatedModelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def revrel1 = [name: "revrel1", reverseName: "rel1", toModel: childModelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: false];

        def parentModelMetaProps = [name: parentModelName]
        def childModelMetaProps = [name: childModelName, parentModel: parentModelName]
        def childModel2MetaProps = [name: childModel2Name, parentModel: parentModelName]
        def relatedModelMetaProps = [name: relatedModelName]
        def modelProps = [keyProp, prop1];
        def keyPropList = [keyProp];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, modelProps, keyPropList, [], additionalParts["parent"])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [rel1], additionalParts["child"])
        String childModel2String = ModelGenerationTestUtils.getModelText(childModel2MetaProps, [], [], [rel1], additionalParts["child2"])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1], additionalParts["related"])
        this.gcl.parseClass(parentModelString + childModelString + relatedModelString + childModel2String);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        Class childModel2Class = this.gcl.loadClass(childModel2Name);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([parentModelClass, childModelClass, relatedModelClass, childModel2Class], [], isPersisted)
        return [parent: parentModelClass, child: childModelClass, related: relatedModelClass, child2: childModel2Class];
    }

}