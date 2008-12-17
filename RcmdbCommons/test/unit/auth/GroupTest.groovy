package auth

import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 5:17:10 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupTest extends RapidCmdbTestCase{
    public void testCreateGroup()
    {
        CompassForTests.initialize ([Group]);
        CompassForTests.addOperationSupport (Group, GroupOperations);

        def groupShouldBeReturned = new Group();
        CompassForTests.addOperationData.setObjectsWillBeReturned([groupShouldBeReturned]);
        def groupProps = [name:"gr1", segmentFilter:"filter1"];
        Group group = Group.createGroup(groupProps);

        assertSame (groupShouldBeReturned, group);
        assertEquals(1, CompassForTests.addOperationData.getParams(Group).size());
        assertEquals (groupProps, CompassForTests.addOperationData.getParams(Group)[0]);
    }

    public void testCreateGroupWithUsers()
    {
        CompassForTests.initialize ([Group, RsUser]);
        CompassForTests.addOperationSupport (Group, GroupOperations);

        def groupShouldBeReturned = new Group();
        def userShouldBeReturned1 = new RsUser(username:"user1");
        def userShouldBeReturned2 = new RsUser(username:"user2");
        CompassForTests.addOperationData.setObjectsWillBeReturned([groupShouldBeReturned]);
        CompassForTests.getOperationData.setObjectsWillBeReturned([userShouldBeReturned1, userShouldBeReturned2]);
        def groupProps = [name:"gr1", segmentFilter:"filter1"];
        def usersToBeAdded = ["user1", userShouldBeReturned2]
        Group group = Group.createGroup(groupProps, usersToBeAdded);

        assertSame (groupShouldBeReturned, group);
        def addMethodParams = CompassForTests.addOperationData.getParams(Group);
        assertEquals(1, addMethodParams.size());

        groupProps.each{String propName, Object propValue->
            assertEquals (addMethodParams[0][propName], propValue);
        }
        assertSame(userShouldBeReturned1, addMethodParams[0].users[0]);
        assertSame(userShouldBeReturned2, addMethodParams[0].users[1]);

        def getMethodParams = CompassForTests.getOperationData.getParams(RsUser);
        assertEquals(2, getMethodParams.size());
        assertEquals (usersToBeAdded[0], getMethodParams[0].username)
        assertEquals (usersToBeAdded[1].username, getMethodParams[1].username)
    }
    public void testCreateGroupThrowsExceptionIfUserDoesnotExist()
    {
        CompassForTests.initialize ([Group, RsUser]);
        CompassForTests.addOperationSupport (Group, GroupOperations);

        def groupShouldBeReturned = new Group();
        CompassForTests.addOperationData.setObjectsWillBeReturned([groupShouldBeReturned]);
        def groupProps = [name:"gr1", segmentFilter:"filter1"];
        try
        {
            Group.createGroup(groupProps, ["user1"]);
            fail("Should throw exception");
        }catch(Exception e)
        {

        }
    }
    public void testCreateGroupThrowsExceptionIfGroupPropsIsNull()
    {
        CompassForTests.initialize ([Group, RsUser]);
        CompassForTests.addOperationSupport (Group, GroupOperations);
        try
        {
            Group.createGroup(null);
            fail("Should throw exception");
        }catch(Exception e)
        {
            assertEquals ("No group props specified", e.getMessage());
        }
    }

}