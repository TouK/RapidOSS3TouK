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
class GroupOperationsWithCompassTests extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([RsUser, Group, SegmentFilter, Role], []);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        SegmentQueryHelper.getInstance().initialize([]);
    }

    public void testCreateGroup()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        Group group = Group.createGroup(groupProps);
        assertFalse(group.hasErrors())
        assertEquals(1, Group.count())
        assertEquals(groupProps.name, group.name)
        assertEquals(groupProps.segmentFilter, group.segmentFilter)
    }

    public void testCreateGroupWithUsers()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        def user1 = RsUser.add(username: "user1", passwordHash: "asdf")
        assertFalse(user1.hasErrors())
        def user2 = RsUser.add(username: "user2", passwordHash: "asdf")
        assertFalse(user2.hasErrors());

        Group group = Group.createGroup(groupProps, ["user1", user2]);

        assertFalse(group.hasErrors())

        assertEquals(2, group.users.size())

        assertTrue(group.users.contains(user1))
        assertTrue(group.users.contains(user2))
    }

    public void testCreateGroupThrowsExceptionIfUserDoesnotExist()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        try
        {
            Group.createGroup(groupProps, ["user1"]);
            fail("Should throw exception");
        } catch (Exception e)
        {
            assertEquals("Could not created group since user user1 does not exist.", e.getMessage())
        }
    }

    public void testCreateGroupThrowsExceptionIfGroupPropsIsNull()
    {
        try
        {
            Group.createGroup(null);
            fail("Should throw exception");
        } catch (Exception e)
        {
            assertEquals("No group props specified", e.getMessage());
        }
    }

    public void testAddUsers()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        def user1 = RsUser.add(username: "user1", passwordHash: "asdf")
        assertFalse(user1.hasErrors())
        def user2 = RsUser.add(username: "user2", passwordHash: "asdf")
        assertFalse(user2.hasErrors());

        Group group = Group.createGroup(groupProps);
        assertFalse(group.hasErrors())
        assertEquals(0, group.users.size())

        group.addUsers(["user1", user2])
        assertEquals(2, group.users.size())
        assertTrue(group.users.contains(user1))
        assertTrue(group.users.contains(user2))
    }

    public void testRemoveUsers()
    {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        def user1 = RsUser.add(username: "user1", passwordHash: "asdf")
        assertFalse(user1.hasErrors())
        def user2 = RsUser.add(username: "user2", passwordHash: "asdf")
        assertFalse(user2.hasErrors());

        Group group = Group.createGroup(groupProps, ["user1", user2]);
        assertFalse(group.hasErrors())
        assertEquals(2, group.users.size())

        group.removeUsers(["user1", user2])
        assertEquals(0, group.users.size())
    }

    public void testAssignRole() {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        Group group = Group.createGroup(groupProps);
        assertFalse(group.hasErrors())

        Role role = Role.add(name: "role1")
        assertFalse(role.hasErrors())

        group.assignRole(role)

        assertEquals(role, group.role)
    }

    public void testRemoveRole() {
        def groupProps = [name: "gr1", segmentFilter: "filter1"];
        Group group = Group.createGroup(groupProps);
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

        Group group = Group.createGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);
        assertEquals("name:*", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
    }

    public void testSegmentGroupFiltersRemovalAfterDelete() {
        String groupName = "group1";
        Group group = Group.createGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

        group.remove();
        groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNull(groupFilters);

    }

    public void testSegmentGroupFiltersCalculationAfterUpdate() {
        String groupName = "group1";
        Group group = Group.createGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);
        SegmentQueryHelper.getInstance().removeGroupFilters(groupName);

        group.update(segmentFilterType: Group.CLASS_BASED_FILTER)

        groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

    }

}