package plugintests

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.domain.method.UpdateMethod
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.util.DataStore

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


    public void testAddMethodWithDateKeyProperty()
    {
        Map classes = initializePluginAndClasses([:], true);
        def datePropValue = new Date();
        def addedObjectProps = [dateProp: datePropValue]
        def addedObject = classes.modelWithDateKeyProp.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.modelWithDateKeyProp.search("id:${addedObject.id}").results[0];
        assertNotNull(objectInRepo);

        addedObject = classes.modelWithDateKeyProp.add(addedObjectProps);
        println addedObject.errors
        assertFalse(addedObject.hasErrors());
    }

    public void testAddMethodWithInvalidPropertyValue()
    {
        Map classes = initializePluginAndClasses([:], true);
        def addedObjectProps = [dateProp: ""]
        def addedObject = classes.modelWithDateKeyProp.add(addedObjectProps);
        assertTrue(addedObject.hasErrors());
        assertEquals(1, addedObject.errors.allErrors.size());
        assertEquals("rapidcmdb.invalid.property.type", addedObject.errors.allErrors[0].code);
    }

    public void testUpdateMethodWithInvalidPropertyValue()
    {
        Map classes = initializePluginAndClasses([:], true);
        def datePropValue = new Date();
        def addedObjectProps = [dateProp: datePropValue]
        def addedObject = classes.modelWithDateKeyProp.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());


        def updateProps = [dateProp: ""]
        def updatedObject = addedObject.update(updateProps);
        assertTrue(updatedObject.hasErrors());
        assertEquals(1, updatedObject.errors.allErrors.size());
        assertEquals("rapidcmdb.invalid.property.type", updatedObject.errors.allErrors[0].code);

        assertEquals(1, classes.modelWithDateKeyProp.count());
        assertNotNull(classes.modelWithDateKeyProp.get(dateProp: datePropValue));
    }


    public void testAddMethodWithFederatedProperty()
    {
        Map classes = initializePluginAndClassesForFederationTest();
        def addedObjectProps = [keyProp: "key", prop1: "prop1Value", prop2: "invalid date prop value"]
        def addedObject = classes.federatedModel.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());
        def objectInRepo = classes.federatedModel.search("id:${addedObject.id}").results[0];
        assertNotNull(objectInRepo);
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


    public void testGetMethods()
    {
        Map classes = initializePluginAndClasses();
        Class operationClass = gcl.parseClass("""
        class ${classes.child.name}Operations extends ${AbstractDomainOperation.class.name}{
            def onLoad()
            {
                ${DataStore.class.name}.put("onLoad", true);
            }
        }
        """)
        CompassForTests.addOperationSupport(classes.child, operationClass);
        def addedObjectProps = [keyProp: "object1", prop1: "prop1Value"]
        def addedObject = classes.child.add(addedObjectProps);
        assertFalse(addedObject.hasErrors());

        DataStore.clear();
        assertNotNull(classes.child.get(keyProp: "object1"));
        assertTrue(DataStore.get("onLoad"));

        DataStore.clear();
        assertNotNull(classes.child.getFromHierarchy(keyProp: "object1"));
        assertTrue(DataStore.get("onLoad"));

        DataStore.clear();
        assertNotNull(classes.child.get([keyProp: "object1"], false));
        assertNull(DataStore.get("onLoad"));

        DataStore.clear();
        assertNotNull(classes.child.getFromHierarchy([keyProp: "object1"], false));
        assertNull(DataStore.get("onLoad"));

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

    public void testListMethod() {
        Map classes = initializePluginAndClasses();
        def addedObject1 = classes.parent.add(keyProp: "object1")
        def addedObject2 = classes.parent.add(keyProp: "object2")
        def addedObject3 = classes.parent.add(keyProp: "object3")
        assertFalse(addedObject1.hasErrors());
        assertFalse(addedObject2.hasErrors());
        assertFalse(addedObject3.hasErrors());

        def objects = classes.parent.list();
        assertEquals(3, objects.size())
        assertTrue(objects.contains(addedObject1))
        assertTrue(objects.contains(addedObject2))
        assertTrue(objects.contains(addedObject3))

        //list with options (ignores max)
        objects = classes.parent.list([sort:"keyProp", order:"desc", max:1]);
        assertEquals(3, objects.size())
        assertEquals("object3", objects[0].keyProp)
        assertEquals("object2", objects[1].keyProp)
        assertEquals("object1", objects[2].keyProp)

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
        def modelWithDateKeyPropName = "ModelWithDateKey"
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def dateProp = [name: "dateProp", type: ModelGenerator.DATE_TYPE];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def rel1 = [name: "rel1", reverseName: "revrel1", toModel: relatedModelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def revrel1 = [name: "revrel1", reverseName: "rel1", toModel: childModelName, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: false];

        def modelWithDateKeyPropMetaData = [name: modelWithDateKeyPropName]
        def parentModelMetaProps = [name: parentModelName]
        def childModelMetaProps = [name: childModelName, parentModel: parentModelName]
        def childModel2MetaProps = [name: childModel2Name, parentModel: parentModelName]
        def relatedModelMetaProps = [name: relatedModelName]
        def modelProps = [keyProp, prop1];
        def keyPropList = [keyProp];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, [], modelProps, keyPropList, [], additionalParts["parent"])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [], [rel1], additionalParts["child"])
        String childModel2String = ModelGenerationTestUtils.getModelText(childModel2MetaProps, [], [], [], [rel1], additionalParts["child2"])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, [], modelProps, keyPropList, [revrel1], additionalParts["related"])
        String modelWithDateKeyPropString = ModelGenerationTestUtils.getModelText(modelWithDateKeyPropMetaData, [dateProp], [dateProp], [])
        this.gcl.parseClass(parentModelString + childModelString + relatedModelString + childModel2String + modelWithDateKeyPropString);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        Class childModel2Class = this.gcl.loadClass(childModel2Name);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        Class modelWithDateKeyPropClass = this.gcl.loadClass(modelWithDateKeyPropName);
        initialize([parentModelClass, childModelClass, relatedModelClass, childModel2Class, modelWithDateKeyPropClass], [], isPersisted)
        return [parent: parentModelClass, child: childModelClass, related: relatedModelClass, child2: childModel2Class, modelWithDateKeyProp: modelWithDateKeyPropClass];
    }

    private Map initializePluginAndClassesForFederationTest()
    {
        def modelName = "FederatedModel";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop2 = [name: "prop2", type: ModelGenerator.DATE_TYPE, blank: false, datasource: "ds1"];

        def datasource = [name: "ds1", keys: [[propertyName: "prop1"]]]

        def modelMetaProps = [name: modelName]
        def modelProps = [keyProp, prop1, prop2];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, [datasource], modelProps, keyPropList, [])
        this.gcl.parseClass(modelString);
        Class modelClass = this.gcl.loadClass(modelName);
        initialize([modelClass], [], false)
        return [federatedModel: modelClass];
    }

}