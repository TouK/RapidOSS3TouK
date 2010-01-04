package com.ifountain.compass.integration
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RapidApplicationOperations

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 13, 2009
* Time: 1:36:57 PM
* To change this template use File | Settings | File Templates.
*/
class CompassRangeQueryTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRangeQueries()
    {
        initialize ([CompassTestObject], [], false);
        def insts =[CompassTestObject.add(prop49: 111111),
        CompassTestObject.add(prop49: 2)]
        assertEquals (insts.size(), CompassTestObject.count());
        def results = CompassTestObject.search("prop49:[3 TO 100000000]", [sort:"prop1"]).results
        assertEquals (1, results.size());
        assertEquals (insts[0].id, results[0].id);
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

    public void testSortingWithNumberProperty()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], true);
        def insts =[
                SortTestoLevel2Child1.add(propInt: 1111),
                SortTestoLevel2Child1.add(propInt: 112),
                SortTestoLevel2Child1.add(propInt: 12),
        ]
        assertEquals (insts.size(), SortTestParentObject.count());
        def returnedObjects = SortTestParentObject.search("alias:*", [sort:"propInt", order:"asc"]).results
        assertEquals (insts[2].id, returnedObjects[0].id);
        assertEquals (insts[1].id, returnedObjects[1].id);
        assertEquals (insts[0].id, returnedObjects[2].id);

        returnedObjects = SortTestParentObject.search("alias:*", [sort:"propInt", order:"desc"]).results
        assertEquals (insts[0].id, returnedObjects[0].id);
        assertEquals (insts[1].id, returnedObjects[1].id);
        assertEquals (insts[2].id, returnedObjects[2].id);
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

    public void testSortingCastExceptionDoesNotOccur()
    {
        initialize ([SortTestParentObject, SortTestoLevel2Child1, SortTestoLevel2Child2, SortTestoLevel1Child1,SortTestoLevel1Child2], [], false);

        SortTestoLevel2Child1.add(d:System.currentTimeMillis());
        SortTestParentObject.add([:]);

        def returnedObjects = SortTestParentObject.search("alias:*").total
        assertEquals (2, returnedObjects)
        returnedObjects = SortTestParentObject.search("alias:*", [sort:"d"]).total
        assertEquals (2, returnedObjects)
    }
}