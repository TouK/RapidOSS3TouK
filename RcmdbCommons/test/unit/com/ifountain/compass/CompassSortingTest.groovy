package com.ifountain.compass

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 13, 2009
* Time: 1:36:57 PM
* To change this template use File | Settings | File Templates.
*/
class CompassSortingTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testSortingWithUnTokenizedFields()
    {
        initialize ([CompassTestObject], [], false);
        def insts =[CompassTestObject.add(prop1: "propertytoken1 aprop bprop cprop"),
        CompassTestObject.add(prop1: "propertytoken1 bprop cprop"),
        CompassTestObject.add(prop1: "propertytoken1 cprop"),
        CompassTestObject.add(prop1: "Propertytoken1")]
        assertEquals (insts.size(), CompassTestObject.count());
        def returnedObjects = CompassTestObject.search("alias:*", [sort:"prop1"]).results
        insts = insts.sort {obj1, obj2->
            return obj1.prop1.toLowerCase().compareTo(obj2.prop1.toLowerCase());
        }
        for(int i=0; i < insts.size(); i++)
        {
            def expectedInstance = insts[i]
            assertEquals (expectedInstance.id, returnedObjects[i].id)
        }
    }

    public void testSortingWithObjectsExtendingFromSameParentButOneOfThemDoesNotHaveSortProperty()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], false);
        def insts =[
                SortTestoLevel2Child1.add(prop3: "prop1 property value1"),
                SortTestoLevel2Child1.add(prop3: "prop1 property value2"),
                SortTestoLevel2Child1.add(prop3: "prop1 property value3"),
                SortTestoLevel2Child2.add(prop4: "prop1 property value")
        ]
        assertEquals (insts.size(), SortTestParentObject.count());
        def returnedObjects = SortTestParentObject.search("alias:*", [sort:"prop3", order:"asc"]).results
        assertEquals (insts[3].id, returnedObjects[0].id);
        assertEquals (insts[0].id, returnedObjects[1].id);
        assertEquals (insts[1].id, returnedObjects[2].id);
        assertEquals (insts[2].id, returnedObjects[3].id);

        returnedObjects = SortTestParentObject.search("alias:*", [sort:"prop3", order:"desc"]).results
        assertEquals (insts[2].id, returnedObjects[0].id);
        assertEquals (insts[1].id, returnedObjects[1].id);
        assertEquals (insts[0].id, returnedObjects[2].id);
        assertEquals (insts[3].id, returnedObjects[3].id);
    }

    public void testSortingWithObjectsExtendingFromSameParentButOneOfThemDoesNotHaveLongSortProperty()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], false);
        def insts =[
                SortTestoLevel2Child1.add(propInt: 1),
                SortTestoLevel2Child1.add(propInt: 2),
                SortTestoLevel2Child1.add(propInt: 3)
        ]
        assertEquals (insts.size(), SortTestParentObject.count());
        def returnedObjects = SortTestParentObject.search("alias:*", [sort:"propInt", order:"asc"]).results
        assertEquals (insts[0].id, returnedObjects[0].id);
        assertEquals (insts[1].id, returnedObjects[1].id);
        assertEquals (insts[2].id, returnedObjects[2].id);

        returnedObjects = SortTestParentObject.search("alias:*", [sort:"propInt", order:"desc"]).results
        assertEquals (insts[2].id, returnedObjects[0].id);
        assertEquals (insts[1].id, returnedObjects[1].id);
        assertEquals (insts[0].id, returnedObjects[2].id);
    }

    public void testSortingWithNonExistingProperty()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], false);
        def returnedObjects = SortTestoLevel2Child1.search("alias:*", [sort:"undefinedProp", order:"asc"]).results
        assertEquals (0, returnedObjects.size())
    }

    public void testSortingWithNoInstance()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], false);
        def returnedObjects = SortTestoLevel2Child1.search("alias:*", [sort:"prop3", order:"asc"]).results
        assertEquals (0, returnedObjects.size())
        returnedObjects = SortTestParentObject.search("alias:*", [sort:"prop3", order:"asc"]).results
        assertEquals (0, returnedObjects.size())
        returnedObjects = SortTestParentObject.search("alias:*", [sort:"propInt", order:"asc"]).results
        assertEquals (0, returnedObjects.size())
    }
}

class SortTestParentObject {
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
}

class SortTestoLevel1Child1 extends SortTestParentObject {
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}
class SortTestoLevel1Child2 extends SortTestParentObject {
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop2 = "";
}
class SortTestoLevel2Child1 extends SortTestoLevel1Child1 {
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop3 = "";
    Long propInt = 0;
}

class SortTestoLevel2Child2 extends SortTestoLevel1Child2 {
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop4 = "";
}