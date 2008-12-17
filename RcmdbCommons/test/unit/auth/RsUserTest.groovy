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
        def groupShouldBeReturned = new Group();

        CompassForTests.addOperationData.setObjectsWillBeReturned([userShouldBeReturned, groupShouldBeReturned]);
        def userProps = [username:"user1", passwordHash:"password"];
        def groupsToBeCreated = ["group1"]
        RsUser user = RsUser.createUser(userProps, groupsToBeCreated);

        assertSame (userShouldBeReturned, user);
        def addMethodParams = CompassForTests.addOperationData.getParams(RsUser);
        assertEquals(1, addMethodParams.size());
        userProps.each{String propName, Object propValue->
            assertEquals (addMethodParams[0][propName], propValue);
        }

        assertEquals (1, addMethodParams[0].groups.size())
        assertSame(groupShouldBeReturned, addMethodParams[0].groups[0])

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
            assertEquals ("Null user props specified", e.getMessage());
        }

        try
        {
            RsUser.createUser(null, []);
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertEquals ("Null user props specified", e.getMessage());
        }
    }


}