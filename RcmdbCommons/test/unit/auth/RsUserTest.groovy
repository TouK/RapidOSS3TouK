package auth

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 3:35:57 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserTest extends RapidCmdbTestCase{
    public void testCreateUser()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);

        def userShouldBeReturned = new RsUser();
        CompassForTests.addOperationData.setObjectsWillBeReturned([userShouldBeReturned]);
        def userProps = [username:"user1", passwordHash:"password"];
        RsUser user = RsUser.createUser(userProps);

        assertSame (userShouldBeReturned, user);
        assertEquals(1, CompassForTests.addOperationData.getParams(RsUser).size());
        assertEquals (userProps, CompassForTests.addOperationData.getParams(RsUser)[0]);
    }

    public void testCreateUserWithGroups()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);

        def userShouldBeReturned = new RsUser();
        def groupShouldBeReturned1 = new Group(name:"group1");
        def groupShouldBeReturned2 = new Group(name:"group2");

        CompassForTests.addOperationData.setObjectsWillBeReturned([userShouldBeReturned]);
        CompassForTests.getOperationData.setObjectsWillBeReturned([groupShouldBeReturned1, groupShouldBeReturned2]);
        def userProps = [username:"user1", passwordHash:"password"];
        def groupsToBeAdded = ["group1", groupShouldBeReturned2]
        RsUser user = RsUser.createUser(userProps, groupsToBeAdded);

        assertSame (userShouldBeReturned, user);
        def addMethodParams = CompassForTests.addOperationData.getParams(RsUser);
        assertEquals(1, addMethodParams.size());
        userProps.each{String propName, Object propValue->
            assertEquals (addMethodParams[0][propName], propValue);
        }

        assertEquals (2, addMethodParams[0].groups.size())
        assertSame(groupShouldBeReturned1, addMethodParams[0].groups[0])
        assertSame(groupShouldBeReturned2, addMethodParams[0].groups[1])

        def getMethodParams = CompassForTests.getOperationData.getParams(Group);
        assertEquals(2, getMethodParams.size());
        assertEquals (groupsToBeAdded[0], getMethodParams[0].name)
        assertEquals (groupsToBeAdded[1].name, getMethodParams[1].name)
    }


    
    public void testCreateUserThrowsExceptionIfGroupDoesNotExist()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);

        def userShouldBeReturned = new RsUser();

        CompassForTests.addOperationData.setObjectsWillBeReturned([userShouldBeReturned]);
        def userProps = [username:"user1", passwordHash:"password"];
        def groupsToBeCreated = ["group1"]
        try
        {
            RsUser.createUser(userProps, groupsToBeCreated);
            fail("Should throw exception");
        }
        catch(Exception e)
        {

        }
    }

    public void testCreateUserThrowsExceptionIfParamsIsNull()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        try
        {
            RsUser.createUser(null);
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertEquals ("No user props specified", e.getMessage());
        }

        try
        {
            RsUser.createUser(null, []);
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertEquals ("No user props specified", e.getMessage());
        }
    }

    public void testAddToGroups()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);

        def userShouldBeReturned = new RsUser();
        def groupShouldBeReturned1 = new Group(name:"group1");
        def groupShouldBeReturned2 = new Group(name:"group2");

        CompassForTests.getOperationData.setObjectsWillBeReturned([groupShouldBeReturned1, groupShouldBeReturned2]);
        def userProps = [username:"user1", passwordHash:"password"];
        def groupsToBeAdded = [groupShouldBeReturned1.name, groupShouldBeReturned2]
        userShouldBeReturned.addToGroups(groupsToBeAdded);

        def addRelMethodParams = CompassForTests.addRelationOperationData.getParams(RsUser);
        assertEquals(1, addRelMethodParams.size());
        assertEquals(1, addRelMethodParams[0].size());
        assertEquals(2, addRelMethodParams[0].groups.size());
        assertEquals (groupShouldBeReturned1.name, addRelMethodParams[0].groups[0].name)
        assertEquals (groupShouldBeReturned2.name, addRelMethodParams[0].groups[1].name)

        def getMethodParams = CompassForTests.getOperationData.getParams(Group);
        assertEquals(2, getMethodParams.size());
        assertEquals (groupsToBeAdded[0], getMethodParams[0].name)
        assertEquals (groupsToBeAdded[1].name, getMethodParams[1].name)
    }

    public void testRemoveFromGroups()
    {
        CompassForTests.initialize ([RsUser, Group]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);

        def userShouldBeReturned = new RsUser();
        def groupShouldBeReturned1 = new Group(name:"group1");
        def groupShouldBeReturned2 = new Group(name:"group2");

        CompassForTests.getOperationData.setObjectsWillBeReturned([groupShouldBeReturned1, groupShouldBeReturned2]);
        def userProps = [username:"user1", passwordHash:"password"];
        def groupsToBeRemoved = [groupShouldBeReturned1.name, groupShouldBeReturned2]
        userShouldBeReturned.removeFromGroups(groupsToBeRemoved);

        def removeRelMethodParams = CompassForTests.removeRelationOperationData.getParams(RsUser);
        assertEquals(1, removeRelMethodParams.size());
        assertEquals(1, removeRelMethodParams[0].size());
        assertEquals(2, removeRelMethodParams[0].groups.size());
        assertEquals (groupShouldBeReturned1.name, removeRelMethodParams[0].groups[0].name)
        assertEquals (groupShouldBeReturned2.name, removeRelMethodParams[0].groups[1].name)

        def getMethodParams = CompassForTests.getOperationData.getParams(Group);
        assertEquals(2, getMethodParams.size());
        assertEquals (groupsToBeRemoved[0], getMethodParams[0].name)
        assertEquals (groupsToBeRemoved[1].name, getMethodParams[1].name)
    }


}