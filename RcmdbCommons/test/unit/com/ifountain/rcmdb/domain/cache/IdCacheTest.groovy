package com.ifountain.rcmdb.domain.cache

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 29, 2009
* Time: 10:09:31 AM
* To change this template use File | Settings | File Templates.
*/
public class IdCacheTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }


    public void testGetWithNonExistingInstances()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class childModel = classes["child1"]
        Class modelWithoutInheritance = classes["modelWithoutInheritance"]
        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        IdCacheEntry entry = IdCache.get(modelWithoutInheritance, params1)
        assertFalse(entry.exist);
        assertNull(entry.alias);
        assertEquals(-1, entry.id);

        IdCacheEntry secondRequestEntry = IdCache.get(modelWithoutInheritance, params1)
        assertSame(secondRequestEntry, entry);

        def params2 = [prop1: "prop1value2", prop2: "prop2value2"]
        IdCacheEntry thirdRequestEntry = IdCache.get(modelWithoutInheritance, params2)
        assertNotSame("For different instances of same class different entries should be returned", secondRequestEntry, thirdRequestEntry);

        def params3 = [prop1: "prop1value2", prop2: "prop2value2"]
        IdCacheEntry fourthRequestEntry = IdCache.get(childModel, params2)
        assertNotSame("For same instances of different class different entries should be returned", thirdRequestEntry, fourthRequestEntry);
    }

    public void testGetWithNonExistingInstancesWithIdMethod()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class childModel = classes["child1"]
        Class modelWithoutInheritance = classes["modelWithoutInheritance"]
        IdCacheEntry entry = IdCache.get(1)
        assertFalse(entry.exist());
        IdCacheEntry entryAfterSecodRequest = IdCache.get(1)
        assertSame(entry, entryAfterSecodRequest);

        IdCacheEntry entryForAnotherId = IdCache.get(2)
        assertSame(entry, entryForAnotherId);
    }



    public void testGetWithExistingInstancesWithIdMethod()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class modelWithoutInheritance = classes["modelWithoutInheritance"]

        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        def addedInstance = modelWithoutInheritance.add(params1)

        IdCache.clearCache();

        IdCacheEntry entry = IdCache.get(modelWithoutInheritance, params1)
        IdCacheEntry entryFromIdMethod = IdCache.get(addedInstance.id);
        assertSame(entry, entryFromIdMethod);

        entryFromIdMethod.clear();
        IdCacheEntry entryFromIdMethodAfterClear = IdCache.get(addedInstance.id);
        assertNotSame(entry, entryFromIdMethodAfterClear);

    }

    public void testGetWithExistingInstances()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class modelWithoutInheritance = classes["modelWithoutInheritance"]
        def operationClassString = """
            class ${modelWithoutInheritance.name}Operations extends ${AbstractDomainOperation.class.name}
            {
                def onLoad()
                {
                    ${DataStore.class.name}.put("onLoad", "onLoad")
                }
            }
        """
        def operationClass = gcl.parseClass(operationClassString);
        CompassForTests.addOperationSupport (modelWithoutInheritance, operationClass)

        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        def addedInstance = modelWithoutInheritance.add(params1)

        IdCache.clearCache();
        DataStore.clear();
        IdCacheEntry entry = IdCache.get(modelWithoutInheritance, params1)
        assertNull ("onLoad should not be called", DataStore.get("onLoad"));
        assertTrue(entry.exist);
        assertEquals(modelWithoutInheritance, entry.alias);
        assertEquals(addedInstance.id, entry.id);



        //cache will respond without going to compass for subsequent requests
        modelWithoutInheritance.unindex(addedInstance);
        assertTrue("no instance should exist", modelWithoutInheritance.list().isEmpty());
        IdCacheEntry entryAfterUnindexing = IdCache.get(modelWithoutInheritance, params1)
        assertSame(entry, entryAfterUnindexing);
        assertTrue(entry.exist);
        assertEquals(modelWithoutInheritance, entry.alias);
        assertEquals(addedInstance.id, entry.id);

        //test with added instance  as parameter
        IdCacheEntry entryWithRealObject = IdCache.get(modelWithoutInheritance, addedInstance)
        assertSame(entry, entryWithRealObject);
        assertTrue(entryWithRealObject.exist);
        assertEquals(modelWithoutInheritance, entryWithRealObject.alias);
        assertEquals(addedInstance.id, entryWithRealObject.id);
    }


    public void testGetWithExistingDateKeyInstances()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class modelWithDateKey = classes["modelWithDateKey"]

        def date = new Date(500);
        def params1 = [dateProp: date]
        def modelWithDateKeyInstance = modelWithDateKey.add(params1)

        IdCache.clearCache();

        IdCacheEntry entry = IdCache.get(modelWithDateKey, params1)
        assertTrue(entry.exist);
        assertEquals(modelWithDateKey, entry.alias);
        assertEquals(modelWithDateKeyInstance.id, entry.id);

        date = new Date(date.getTime() + 50);
        params1 = [dateProp: date]

        IdCacheEntry entryWithUpdatedDateProp = IdCache.get(modelWithDateKey, params1)
        assertNotSame(entry, entryWithUpdatedDateProp);
        assertFalse(entryWithUpdatedDateProp.exist());

        //test if no converter exist throws exception
        RapidConvertUtils.getInstance().deregister();

        try {
            IdCache.get(modelWithDateKey, params1)
            fail("Should throw exception");
        }
        catch (java.lang.RuntimeException e)
        {
            assertEquals ("No converter defined for date", e.getMessage());
        }
    }


    public void testUpdate()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class modelWithoutInheritance = classes["modelWithoutInheritance"]


        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        def addedInstance = modelWithoutInheritance.add(params1)

        IdCacheEntry entry = IdCache.update(addedInstance, false);
        assertFalse(entry.exist);
        assertEquals(-1, entry.id);
        assertNull(entry.alias);


        entry = IdCache.update(addedInstance, true);
        assertTrue(entry.exist);
        assertEquals(addedInstance.id, entry.id);
        assertEquals(addedInstance.class, entry.alias);

        IdCache.clearCache();

        entry = IdCache.update(addedInstance, true);
        assertTrue(entry.exist);
        assertEquals(addedInstance.id, entry.id);
        assertEquals(addedInstance.class, entry.alias);

        IdCache.clearCache();

        entry = IdCache.update(addedInstance, false);
        assertFalse(entry.exist);
        assertEquals(-1, entry.id);
        assertNull(entry.alias);

    }

    public void testGetWithExistingHierarchyInstances()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class childModel1 = classes["child1"]
        Class childModel2 = classes["child2"]
        Class parentModel = classes["parent"]

        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        def addedInstance = childModel1.add(params1)

        IdCache.clearCache();

        IdCacheEntry entryForChild1 = IdCache.get(childModel1, params1)
        assertTrue(entryForChild1.exist);
        assertEquals(childModel1, entryForChild1.alias);
        assertEquals(addedInstance.id, entryForChild1.id);

        IdCacheEntry entryForChild2 = IdCache.get(childModel2, params1)
        assertSame(entryForChild1, entryForChild2);
        assertTrue(entryForChild2.exist);
        assertEquals(childModel1, entryForChild2.alias);
        assertEquals(addedInstance.id, entryForChild2.id);


        IdCacheEntry entryForParent = IdCache.get(parentModel, params1)
        assertSame(entryForChild1, entryForParent);
        assertTrue(entryForParent.exist);
        assertEquals(childModel1, entryForParent.alias);
        assertEquals(addedInstance.id, entryForParent.id);
    }

    public void testGetWithModelWhoseKeyIsId()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class modelWithIdKey = classes["modelWithIdKey"]

        def addedInstance = modelWithIdKey.add([:])
        IdCache.clearCache();

        IdCacheEntry entry = IdCache.get(modelWithIdKey, [id: 1000]);
        assertEquals(1, IdCache.size());
        assertFalse(entry.exist);
        assertNull(entry.alias);
        assertEquals(-1, entry.id);

        IdCacheEntry entry2 = IdCache.get(modelWithIdKey, [id: 1000]);
        assertSame(entry, entry2);
        assertEquals(1, IdCache.size());
        assertFalse(entry2.exist);
        assertNull(entry2.alias);
        assertEquals(-1, entry2.id);

        IdCacheEntry exitingEntry1 = IdCache.get(modelWithIdKey, addedInstance);
        assertEquals(2, IdCache.size());
        assertTrue(exitingEntry1.exist);
        assertEquals(modelWithIdKey, exitingEntry1.alias);
        assertEquals(addedInstance.id, exitingEntry1.id);

        IdCacheEntry exitingEntry2 = IdCache.get(modelWithIdKey, addedInstance);
        assertEquals(2, IdCache.size());
        assertSame(exitingEntry1, exitingEntry2);
        assertTrue(exitingEntry2.exist);
        assertEquals(modelWithIdKey, exitingEntry2.alias);
        assertEquals(addedInstance.id, exitingEntry2.id);

    }


    public void testMarkAsDeleted()
    {
        IdCache.initialize(10000);
        Map classes = initializeCompass();
        Class childModel1 = classes["child1"]

        def params1 = [prop1: "prop1value1", prop2: "prop2value1"]
        def params2 = [prop1: "prop1value2", prop2: "prop2value2"]
        def addedInstance = childModel1.add(params1)
        def addedInstance2 = childModel1.add(params2)

        IdCache.clearCache();

        IdCacheEntry entryForChild1Instance1 = IdCache.get(childModel1, params1)
        IdCacheEntry entryForChild1Instance2 = IdCache.get(childModel1, params2)
        assertTrue(entryForChild1Instance1.exist);
        assertEquals(childModel1, entryForChild1Instance1.alias);
        assertEquals(addedInstance.id, entryForChild1Instance1.id);

        assertTrue(entryForChild1Instance2.exist);
        assertEquals(childModel1, entryForChild1Instance2.alias);
        assertEquals(addedInstance2.id, entryForChild1Instance2.id);

        IdCache.markAsDeleted(childModel1, params1);

        IdCacheEntry entryForChild1Instance1AfterMarking = IdCache.get(childModel1, params1)
        IdCacheEntry entryForChild1Instance2AfterMarking = IdCache.get(childModel1, params2)
        assertSame(entryForChild1Instance1, entryForChild1Instance1AfterMarking);
        assertFalse(entryForChild1Instance1AfterMarking.exist);
        assertNull(entryForChild1Instance1AfterMarking.alias);
        assertEquals(-1, entryForChild1Instance1AfterMarking.id);


        assertSame(entryForChild1Instance2, entryForChild1Instance2AfterMarking);
        assertTrue(entryForChild1Instance2AfterMarking.exist);
        assertEquals(childModel1, entryForChild1Instance2AfterMarking.alias);
        assertEquals(addedInstance2.id, entryForChild1Instance2AfterMarking.id);


        //test with instance
        IdCache.markAsDeleted(childModel1, addedInstance);
        IdCacheEntry entryForChild1Instance1AfterMarkingWithInstance = IdCache.get(childModel1, addedInstance)
        assertSame(entryForChild1Instance1, entryForChild1Instance1AfterMarkingWithInstance);
        assertFalse(entryForChild1Instance1AfterMarkingWithInstance.exist);
        assertNull(entryForChild1Instance1AfterMarkingWithInstance.alias);
        assertEquals(-1, entryForChild1Instance1AfterMarkingWithInstance.id);

        //test with no entries
        IdCache.markAsDeleted(childModel1, [:])
        IdCacheEntry entryForChild1Instance2AfterMarkingNonexistant = IdCache.get(childModel1, params2)
        assertSame(entryForChild1Instance2, entryForChild1Instance2AfterMarkingNonexistant);
        assertTrue(entryForChild1Instance2AfterMarkingNonexistant.exist);
        assertEquals(childModel1, entryForChild1Instance2AfterMarkingNonexistant.alias);
        assertEquals(addedInstance2.id, entryForChild1Instance2AfterMarkingNonexistant.id);
    }

    public void testIdNumberOfRecordsEqualsToMaxLimitHalfOfTherecordsWillBeDleted()
    {
        IdCache.initialize(10);

        Map classes = initializeCompass();
        Class childModel1 = classes["child1"]
        for (int i = 0; i < 10; i++)
        {
            def params1 = [prop1: "prop1value" + i, prop2: "prop2value" + i]
            def addedInstance = childModel1.add(params1)
            IdCache.get(childModel1, params1)
        }
        assertEquals(10, IdCache.size());

        def params1 = [prop1: "prop1value11", prop2: "prop2value11"]
        def addedInstance = childModel1.add(params1)
        IdCache.get(childModel1, params1)
        assertEquals(6, IdCache.size());
    }

    private Map initializeCompass()
    {
        def model1Name = "ParentModel";
        def model2Name = "ChildModel1";
        def model5Name = "ChildModel2";
        def model3Name = "ModelWithIdKey";
        def model4Name = "Model4";
        def modelWithDateKeyName = "ModelWithDateKey";
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def prop2 = [name: "prop2", type: ModelGenerator.STRING_TYPE];
        def dateProp = [name: "dateProp", type: ModelGenerator.DATE_TYPE];
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name, parentModel: model1Name]
        def model3MetaProps = [name: model3Name]
        def model4MetaProps = [name: model4Name]
        def model5MetaProps = [name: model5Name, parentModel: model1Name]
        def modelWithDateKeyProps = [name: modelWithDateKeyName]

        def modelProps = [prop1, prop2];
        def keyPropList = [prop1, prop2];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, []);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, [], [], []);
        def model3Text = ModelGenerationTestUtils.getModelText(model3MetaProps, modelProps, [], []);
        def model4Text = ModelGenerationTestUtils.getModelText(model4MetaProps, modelProps, keyPropList, []);
        def model5Text = ModelGenerationTestUtils.getModelText(model5MetaProps, [], [], []);
        def modelWithDateKeyText = ModelGenerationTestUtils.getModelText(modelWithDateKeyProps, [dateProp], [dateProp], []);
        gcl.parseClass(model1Text + model2Text + model3Text + model4Text + model5Text + modelWithDateKeyText);
        def parentClass = gcl.loadClass(model1Name)
        def childClass1 = gcl.loadClass(model2Name)
        def childClass2 = gcl.loadClass(model5Name)
        def model3Class = gcl.loadClass(model3Name)
        def model4Class = gcl.loadClass(model4Name)
        def modelWithDateKeyClass = gcl.loadClass(modelWithDateKeyName)
        def compassClasses = [parentClass, childClass1, model3Class, model4Class, modelWithDateKeyClass];
        initialize(compassClasses, []);
        return [parent: parentClass, child1: childClass1, child2: childClass2, modelWithIdKey: model3Class, modelWithoutInheritance: model4Class, modelWithDateKey: modelWithDateKeyClass];
    }
}