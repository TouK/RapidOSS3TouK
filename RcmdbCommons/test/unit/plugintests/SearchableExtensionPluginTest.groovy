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

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 23, 2009
* Time: 4:34:15 PM
* To change this template use File | Settings | File Templates.
*/
class SearchableExtensionPluginTest extends RapidCmdbWithCompassTestCase{

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
        def addedObjectProps = [keyProp:"object1", prop1:"prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals (addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (addedObjectProps.prop1, objectInRepo.prop1);

        //test adding same object multiple time will update object
        addedObjectProps = [keyProp:"object1", prop1:"prop1ValueUpdated"]
        addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals (addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (addedObjectProps.prop1, objectInRepo.prop1);

        //test adding same object multiple time will return error with addUnique
        def addUniqueObjectProps = [keyProp:"object1", prop1:"prop1ValueUpdatedAddUnique"]
        def addedObjectWithUnique = classes.child.addUnique(addedObjectProps);

        assertTrue(addedObjectWithUnique.hasErrors());
        assertNull(addedObjectWithUnique.id);
        assertEquals("rapidcmdb.instance.already.exist", addedObjectWithUnique.errors.allErrors[0].code);
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals (addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (addedObjectProps.prop1, objectInRepo.prop1);
    }


    public void testBulkAddMethod()
    {
        Map classes = initializePluginAndClasses();
        def objectsToBeAdded = [];
        objectsToBeAdded[0] = [keyProp:"object1", prop1:"prop1Value1"]
        objectsToBeAdded[1] = [keyProp:"object2", prop1:"prop1Value2"]
        objectsToBeAdded[2] = [keyProp:"object3", prop1:"prop1Value2"]
        def addedObjects = classes.child.bulkAdd(objectsToBeAdded);
        assertEquals(objectsToBeAdded.size(), addedObjects.size());
        for(int i=0; i < addedObjects.size(); i++){
            def addedObject = addedObjects[i];
            def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
            assertEquals (objectsToBeAdded[i].keyProp, objectInRepo.keyProp);
            assertEquals (objectsToBeAdded[i].prop1, objectInRepo.prop1);
        }
    }


    public void testAddMethodsWithTriggeringEvents()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass ("""
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
        CompassForTests.addOperationSupport (classes.child, operationClass);
        def addedObjectProps = [keyProp:"object1", prop1:"prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

        assertTrue (DataStore.get("beforeInsert"));
        assertTrue (DataStore.get("afterInsert"));

    }

    //this test is written to check whether can we access relation inside other validators
    public void testAddMethodWithConstraintsAndAccessingRelationFromPropertyConstraint()
    {
        def errorCode = "invalid.relation.object"
        def replacementParts = [
                child:[
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

        def addedObjectProps = [keyProp:"object1", prop1:"prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertTrue(addedObject.hasErrors());
        assertEquals (errorCode, addedObject.errors.allErrors[0].code);

        def relatedObjectProps = [keyProp:"relatedObj1", prop1:"prop1Value"]
        def relatedObject = classes.related.add(relatedObjectProps);
        assertFalse(relatedObject.hasErrors());

        addedObjectProps = [keyProp:"object2", prop1:"prop1Value", rel1:relatedObject]
        addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

    }

    //this test is written to check whether can we access relation inside other validators
    public void testUpdateethodWithConstraintsAndAccessingRelationFromPropertyConstraint()
    {
        def errorCode = "invalid.relation.object"
        def replacementParts = [
                child:[
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

        def relatedObjectProps = [keyProp:"relatedObj1", prop1:"prop1Value"]
        def relatedObject = classes.related.add(relatedObjectProps);
        assertFalse(relatedObject.hasErrors());

        def addedObjectProps = [keyProp:"object2", prop1:"prop1Value", rel1:relatedObject]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

        def updatedObjectProps = [keyProp:"object2", prop1:"prop1UpdatedValue"]
        def updatedObject = addedObject.update(updatedObjectProps);
        assertFalse(updatedObject.hasErrors());
        assertFalse(addedObject.hasErrors());

        updatedObjectProps = [keyProp:"object2", prop1:"prop1UpdatedValue", rel1:[]];
        updatedObject = addedObject.update(updatedObjectProps);
        assertTrue(updatedObject.hasErrors());
        assertTrue(addedObject.hasErrors());

    }

    public void testRemoveMethod()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass ("""
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
        CompassForTests.addOperationSupport (classes.child, operationClass);
        def addedObjectProps = [keyProp:"object1", prop1:"prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals (addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (addedObjectProps.prop1, objectInRepo.prop1);

        //test remove
        addedObject.remove();
        objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertNull(objectInRepo);
        assertTrue (DataStore.get("beforeDelete"));
        assertTrue (DataStore.get("afterDelete"));
    }

    public void testUpdateMethod()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass ("""
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
        CompassForTests.addOperationSupport (classes.child, operationClass);
        def addedObjectProps = [keyProp:"object1", prop1:"prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.child.search("id:${addedObject.id}").results[0];
        assertEquals (addedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (addedObjectProps.prop1, objectInRepo.prop1);

        //test update
        def updatedObjectProps = [keyProp:"object2", prop1:"prop1ValueUpdated"]
        addedObject.update(updatedObjectProps);
        assertFalse(addedObject.hasErrors());
        objectInRepo = classes.child.search("keyProp:${addedObjectProps.keyProp}").results[0];
        assertNull (objectInRepo)
        objectInRepo = classes.child.search("keyProp:${updatedObjectProps.keyProp}").results[0];
        assertEquals (updatedObjectProps.keyProp, objectInRepo.keyProp);
        assertEquals (updatedObjectProps.prop1, objectInRepo.prop1);

        //test events are called
        assertEquals (addedObjectProps.keyProp, DataStore.get("beforeUpdate")[UpdateMethod.UPDATED_PROPERTIES].keyProp);
        assertEquals (addedObjectProps.prop1, DataStore.get("beforeUpdate")[UpdateMethod.UPDATED_PROPERTIES].prop1);
        assertEquals (addedObjectProps.keyProp, DataStore.get("afterUpdate")[UpdateMethod.UPDATED_PROPERTIES].keyProp);
        assertEquals (addedObjectProps.prop1, DataStore.get("afterUpdate")[UpdateMethod.UPDATED_PROPERTIES].prop1);
    }

    public void testAddRemoveRelations()
    {
        Map classes = initializePluginAndClasses();
        def addedObjectProps1 = [keyProp:"object1", prop1:"prop1Value1"]
        def relatedObject1Props = [keyProp:"object2", prop1:"prop1Value2"]
        def addedObject1 = classes.child.add(addedObjectProps1);
        def relatedObject1 = classes.related.add(relatedObject1Props);
        assertFalse(addedObject1.hasErrors());
        assertFalse(relatedObject1.hasErrors());
        assertEquals (1, classes.child.countHits("id:${addedObject1.id}"));
        assertEquals (1, classes.related.countHits("id:${relatedObject1.id}"));
        def object1InRepo = classes.child.search("id:${addedObject1.id}").results[0];

        assertEquals (0, addedObject1.rel1.size());
        assertEquals (0, object1InRepo.rel1.size());

        //test add relation
        addedObject1.addRelation("rel1":relatedObject1);

        assertEquals (1, addedObject1.rel1.size())
        assertEquals (relatedObject1.id, addedObject1.rel1[0].id)
        assertEquals (addedObject1.id, relatedObject1.revrel1[0].id)

        //test remove relation
        addedObject1.removeRelation("rel1":relatedObject1);
        assertEquals (0, addedObject1.rel1.size())
        assertEquals (0, relatedObject1.revrel1.size())

    }

    private Map initializePluginAndClasses()
    {
        initializePluginAndClasses([:]);    
    }
    private Map initializePluginAndClasses(Map additionalParts)
    {
        def parentModelName = "ParentModel";
        def childModelName = "ChildModel";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def parentModelMetaProps = [name:parentModelName]
        def childModelMetaProps = [name:childModelName, parentModel:parentModelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def modelProps = [keyProp, prop1];
        def keyPropList = [keyProp];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, modelProps, keyPropList, [], additionalParts["parent"])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [rel1], additionalParts["child"])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1], additionalParts["related"])
        this.gcl.parseClass(parentModelString+childModelString+relatedModelString);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([parentModelClass,childModelClass, relatedModelClass], [])
        return [parent:parentModelClass, child:childModelClass, related:relatedModelClass];
    }

}