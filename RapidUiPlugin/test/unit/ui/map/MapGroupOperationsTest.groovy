package ui.map

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 6, 2009
 * Time: 11:23:31 AM
 * To change this template use File | Settings | File Templates.
 */



class MapGroupOperationsTest  extends RapidCmdbWithCompassTestCase{
    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testBeforeDeleteGeneratesExceptionIfGroupHaveQueries()
    {
        initialize([TopoMap, MapGroup], []);
        CompassForTests.addOperationSupport (MapGroup,MapGroupOperations);

        assertEquals(0,TopoMap.count());
        assertEquals(0,MapGroup.count());

        def group=MapGroup.add(groupName:"testgroup",username:"user1");
        assertFalse(group.hasErrors())

        def map1=TopoMap.add(mapName:"testmap1",mapPropertyList:"a",mapProperties:"user1",nodePropertyList:"x",nodes:"y",group:group,username:"x",layout:5);
        assertFalse(map1.errors.toString(),map1.hasErrors());
        def map2=TopoMap.add(mapName:"testmap2",mapPropertyList:"a",mapProperties:"user1",nodePropertyList:"x",nodes:"y",group:group,username:"x",layout:5);
        assertFalse(map2.errors.toString(),map2.hasErrors());

        assertEquals(2,TopoMap.count());
        assertEquals(1,MapGroup.count());



        group=MapGroup.list()[0];
        assertEquals(2,group.maps.size());

        try {
            group.remove();
            fail("Should throw exception")
        }
        catch(e)
        {
             println e;
        }

        group.removeRelation([maps:map1]);
        group=MapGroup.list()[0];
        assertEquals(1,group.maps.size());

        try {
            group.remove();
            fail("Should throw exception")
        }
        catch(e)
        {
             println e;
        }

        group.removeRelation([maps:map2]);
        group=MapGroup.list()[0];
        assertEquals(0,group.maps.size());

         try {
            group.remove();

        }
        catch(e)
        {
             println e;
             fail("Should not throw exception")
        }

        assertEquals(0,MapGroup.count());
    }
}