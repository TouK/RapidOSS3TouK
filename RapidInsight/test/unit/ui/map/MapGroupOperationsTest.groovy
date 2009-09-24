package ui.map

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import auth.RsUser

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

    public void testGetVisibleGroupsForUserAndGetSaveGroupsForUser()
    {
        initialize([TopoMap, MapGroup], []);
        CompassForTests.addOperationSupport (MapGroup,MapGroupOperations);

        def adminPublicGroup=MapGroup.add(groupName:"adminPublicGroup",username:RsUser.RSADMIN,isPublic:true);
        assertFalse(adminPublicGroup.hasErrors())

        //will not be in user visibleGroups list
        def adminNonPublicGroup=MapGroup.add(groupName:"adminNonPublicGroup",username:RsUser.RSADMIN,isPublic:false);
        assertFalse(adminNonPublicGroup.hasErrors())

        def userGroup=MapGroup.add(groupName:"userGroup",username:"testuser");
        assertFalse(userGroup.hasErrors());

        //will not be in user visibleGroups list
        def userPublicGroup=MapGroup.add(groupName:"userPublicGroup",username:"testuser2",isPublic:true);
        assertFalse(userPublicGroup.hasErrors());

        //test for testuser
        def visibleGroups=MapGroup.getVisibleGroupsForUser("testuser");
        assertEquals(2,visibleGroups.size());
        assertEquals(1,visibleGroups.findAll{it.id==adminPublicGroup.id}.size());
        assertEquals(0,visibleGroups.findAll{it.id==adminNonPublicGroup.id}.size());
        assertEquals(1,visibleGroups.findAll{it.id==userGroup.id}.size());
        assertEquals(0,visibleGroups.findAll{it.id==userPublicGroup.id}.size());


        def saveGroups=MapGroup.getSaveGroupsForUser("testuser");
        assertEquals(1,saveGroups.size());
        assertEquals(0,saveGroups.findAll{it.id==adminPublicGroup.id}.size());
        assertEquals(0,saveGroups.findAll{it.id==adminNonPublicGroup.id}.size());
        assertEquals(1,saveGroups.findAll{it.id==userGroup.id}.size());
        assertEquals(0,saveGroups.findAll{it.id==userPublicGroup.id}.size());
        
        //test for admin
        visibleGroups=MapGroup.getVisibleGroupsForUser(RsUser.RSADMIN);
        assertEquals(2,visibleGroups.size());
        assertEquals(1,visibleGroups.findAll{it.id==adminPublicGroup.id}.size());
        assertEquals(1,visibleGroups.findAll{it.id==adminNonPublicGroup.id}.size());
        assertEquals(0,visibleGroups.findAll{it.id==userGroup.id}.size());
        assertEquals(0,visibleGroups.findAll{it.id==userPublicGroup.id}.size());

        saveGroups=MapGroup.getSaveGroupsForUser(RsUser.RSADMIN);
        assertEquals(1,saveGroups.size());
        assertEquals(0,saveGroups.findAll{it.id==adminPublicGroup.id}.size());
        assertEquals(1,saveGroups.findAll{it.id==adminNonPublicGroup.id}.size());
        assertEquals(0,saveGroups.findAll{it.id==userGroup.id}.size());
        assertEquals(0,saveGroups.findAll{it.id==userPublicGroup.id}.size());
        
    }
}