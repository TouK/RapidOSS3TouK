package auth

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 10:48:47 AM
* To change this template use File | Settings | File Templates.
*/
class GroupTest extends RapidCmdbWithCompassTestCase {
    def userRole;
    def adminRole;

    public void setUp() {
        super.setUp();
        initialize([RsUser, Group, SegmentFilter, Role], []);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        SegmentQueryHelper.getInstance().initialize([]);

        userRole=Role.add(name:Role.USER)
        assertFalse(userRole.hasErrors())
        adminRole=Role.add(name:Role.ADMINISTRATOR)
        assertFalse(adminRole.hasErrors())
    }
    public void tearDown() {
        super.tearDown();
    }

    public void testAddGroup()
    {
        def user1 = RsUser.add(username:"user1",passwordHash:"abc");
        def user2 = RsUser.add(username:"user2",passwordHash:"abc");

        def groupProps = [name:"group1",segmentFilter: "filter1",users:[user1,user2],role:userRole];
        Group group = Group.addGroup(groupProps);

        assertFalse(group.hasErrors());
        assertEquals(groupProps.name,group.name);
        assertEquals(groupProps.segmentFilter,group.segmentFilter);
        assertEquals(Group.GLOBAL_FILTER,group.segmentFilterType)

        def groupUsers=group.users;
        
        assertEquals (2, groupUsers.size());
        assertNotNull(groupUsers.find{it.id == user1.id})
        assertNotNull(groupUsers.find{it.id == user2.id})
        
        //adding another group with no users
        def groupProps2 = [name:"group2",segmentFilter: "filter1",segmentFilterType:Group.CLASS_BASED_FILTER,role:userRole];
        Group group2 = Group.addGroup(groupProps2);
        assertFalse(group2.hasErrors());
        assertEquals(groupProps2.name,group2.name);
        assertEquals(Group.CLASS_BASED_FILTER,group2.segmentFilterType)

        assertEquals (0, group2.users.size());
    }
    public void testGroupWithUserList()
    {

        def user1 = RsUser.add(username:"user1",passwordHash:"abc");
        def user2 = RsUser.add(username:"user2",passwordHash:"abc");

        def usersToBeAdded = ["user1", user2]
        def groupProps = [name:"group1",segmentFilter: "filter1",users:usersToBeAdded,role:userRole];
        Group group = Group.addGroup(groupProps);

        assertFalse(group.hasErrors());
        assertEquals(groupProps.name,group.name);
        assertEquals(groupProps.segmentFilter,group.segmentFilter);

        def groupUsers=group.users;

        assertEquals (2, groupUsers.size());
        assertNotNull(groupUsers.find{it.id == user1.id})
        assertNotNull(groupUsers.find{it.id == user2.id})
    }
    public void testAddUniqueGroupHasErrorIfGroupAlreadyExists()
    {
        def user1 = RsUser.add(username:"user1",passwordHash:"abc");

        def groupProps = [name:"group1",users:[user1],role:userRole];
        Group group = Group.addUniqueGroup(groupProps);

        assertFalse(group.hasErrors());
        assertEquals(groupProps.name,group.name);

        def groupUsers=group.users;

        assertEquals (1, groupUsers.size());

        Group group2 = Group.addUniqueGroup(groupProps);
        assertTrue(group2.hasErrors());
        assertEquals(1,Group.count());
    }
    public void testAddGroupThrowsExceptionIfUserDoesnotExist()
    {
        def groupUsers=["user1"]
        def groupProps = [name: "gr1", segmentFilter: "filter1",users:groupUsers,role:userRole];
        try
        {
            Group.addGroup(groupProps);
            fail("Should throw exception");
        } catch (Exception e)
        {
            assertEquals("Could not created group since user user1 does not exist.", e.getMessage())
        }

        assertEquals(0,Group.count());
    }
    public void testAddGroupThrowsExceptionIfNoRoleIsSpecified()
    {
        def groupUsers=["user1"]
        def groupProps = [name: "gr1", segmentFilter: "filter1",users:groupUsers];
        try
        {
            Group.addGroup(groupProps);
            fail("Should throw exception");
        } 
        catch (Exception e)
        {
            assertEquals("no.role.specified", e.getCode())
        }

        assertEquals(0,Group.count());
    }
    public void testUpdateGroup()
    {
        def user1 = RsUser.add(username:"user1",passwordHash:"abc");
        def user2 = RsUser.add(username:"user2",passwordHash:"abc");

        def groupProps = [name:"group1",role:userRole];
        Group group = Group.add(groupProps);

        assertFalse(group.hasErrors());
        assertEquals(groupProps.name,group.name);
        assertEquals (0,group.users.size());

        def updateProps=[name:"group2",role:adminRole,users:[user1,"user2"]]

        def updatedGroup=Group.get(id:group.id);
        Group.updateGroup(updatedGroup,updateProps)
        assertFalse(updatedGroup.hasErrors());
        assertEquals(updateProps.name,updatedGroup.name)
        assertEquals(adminRole.id,updatedGroup.role.id)
            
        def groupUsers=updatedGroup.users;
        assertEquals (2,groupUsers.size());
        assertNotNull(groupUsers.find{it.id == user1.id})
        assertNotNull(groupUsers.find{it.id == user2.id})

    }
    public void testUpdateGroupThrowsExceptionIfRoleIsNull()
    {

        def groupProps = [name:"group1",role:userRole];
        Group group = Group.add(groupProps);

        assertFalse(group.hasErrors());
        assertEquals(groupProps.name,group.name);
        assertEquals (0,group.users.size());


        try{
            def updateProps=[name:"group2",role:null]
            Group.updateGroup(group,updateProps);
            fail("show throw exception")
        }
        catch(e)
        {
            assertEquals("no.role.specified", e.getCode())
        }

    }


    public void testAddUsers()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        def user1 = RsUser.add(username: "user1", passwordHash: "asdf")
        assertFalse(user1.hasErrors())
        def user2 = RsUser.add(username: "user2", passwordHash: "asdf")
        assertFalse(user2.hasErrors());

        Group group = Group.add(groupProps);
        assertFalse(group.hasErrors())
        assertEquals(0, group.users.size())

        group.addUsers(["user1", user2])
        assertEquals(2, group.users.size())
        assertTrue(group.users.contains(user1))
        assertTrue(group.users.contains(user2))
    }

    public void testRemoveUsers()
    {

        def user1 = RsUser.add(username: "user1", passwordHash: "asdf")
        assertFalse(user1.hasErrors())
        def user2 = RsUser.add(username: "user2", passwordHash: "asdf")
        assertFalse(user2.hasErrors());

        def groupProps = [name: "gr1", segmentFilter: "filter1",users:["user1", user2],role:userRole];

        Group group = Group.addGroup(groupProps);
        assertFalse(group.hasErrors())
        assertEquals(2, group.users.size())

        group.removeUsers(["user1", user2])
        assertEquals(0, group.users.size())
    }

    public void testAssignRole() {
        def groupProps = [name: "gr1", segmentFilter: "filter1",role:userRole];
        Group group = Group.addGroup(groupProps);
        assertFalse(group.hasErrors())

        Role role = Role.add(name: "role1")
        assertFalse(role.hasErrors())

        group.assignRole(role)

        assertEquals(role, group.role)
    }

    public void testRemoveRole() {
        def groupProps = [name: "gr1", segmentFilter: "filter1",role:userRole];
        Group group = Group.addGroup(groupProps);
        assertFalse(group.hasErrors())

        Role role = Role.add(name: "role1")
        assertFalse(role.hasErrors())

        group.assignRole(role)

        assertEquals(role, group.role)

        group.removeRole(role)
        assertNull(group.role)
    }

    public void testSegmentGroupFiltersCreation() {
        String groupName = "group1";
        assertNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName))

        Group group = Group.addGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER,role:userRole);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);
        assertEquals("name:*", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
    }

    public void testSegmentGroupFiltersRemovalAfterDelete() {
        String groupName = "group1";
        Group group = Group.addGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER,role:userRole);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

        group.remove();
        groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNull(groupFilters);

    }

    public void testSegmentGroupFiltersCalculationAfterUpdate() {
        String groupName = "group1";
        Group group = Group.addGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER,role:userRole);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);
        SegmentQueryHelper.getInstance().removeGroupFilters(groupName);

        group.update(segmentFilterType: Group.CLASS_BASED_FILTER)

        groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

    }

    public void testRsAdminGroupCannotBeDeleted()
    {
        def group=Group.add(name:RsUser.RSADMIN);
        assertFalse(group.errors.toString(),group.hasErrors());
        try{
            group.remove();
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}","Can not delete group ${RsUser.RSADMIN}",e.getMessage());
        }

        assertEquals(1,Group.count());

        //test a successfull remove
        group=Group.add(name:"testgroup");
        assertFalse(group.errors.toString(),group.hasErrors());

        assertEquals(2,Group.count());

        group.remove();

        assertEquals(1,Group.count());
    }

}