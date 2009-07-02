package auth


import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.exception.MessageSourceException
import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 3:35:57 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserTest extends RapidCmdbWithCompassTestCase{
     public void setUp() {
        super.setUp();
        initialize([RsUser,Group, ChannelUserInformation], []);
        SegmentQueryHelper.getInstance().initialize([]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        CompassForTests.addOperationSupport (Group, GroupOperations);
    }

    public void tearDown() {

        super.tearDown();
    }


    public void testAddUser()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");
        
        def userProps = [username:"user1", password:"password",groups:[group1,group2]];
        RsUser user = RsUser.addUser(userProps);

        assertFalse(user.hasErrors());
        assertEquals(userProps.username,user.username);
        assertEquals(RsUser.hashPassword(userProps.password),user.passwordHash)


        assertEquals (2, user.groups.size())
        assertEquals(group1.id, user.groups[0].id)
        assertEquals(group2.id, user.groups[1].id)
    }
    public void testAddUserHasErrorIfUserAlreadyExists()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");


        def userProps = [username:"user1", password:"password",groups:[group1,group2]];
        RsUser user = RsUser.addUser(userProps);
        assertFalse(user.hasErrors());
        assertEquals (2, user.groups.size())
        assertEquals(1,RsUser.count());

        RsUser user2 = RsUser.addUser(userProps);
        assertTrue(user2.hasErrors());
        assertEquals(1,RsUser.count());

    }
    public void testAddUserWithGroupList()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");


        def userProps = [username:"user1", password:"password"];
        def groupsToBeAdded = ["group1", group2]
        RsUser user = RsUser.addUser(userProps, groupsToBeAdded);
        assertFalse(user.hasErrors());

        assertEquals(userProps.username,user.username);
        assertEquals(RsUser.hashPassword(userProps.password),user.passwordHash)


        assertEquals (2, user.groups.size())
        assertEquals(group1.id, user.groups[0].id)
        assertEquals(group2.id, user.groups[1].id)

    }
    public void testAddUserWithGroupListThrowsExceptionIfGroupDoesNotExist()
    {

        def userProps = [username:"user1", password:"password"];
        def groupsToBeCreated = ["group1"]
        try
        {
            RsUser.addUser(userProps, groupsToBeCreated);
            fail("Should throw exception");
        }
        catch(Exception e)
        {

        }
        assertEquals(0,RsUser.count())
    }

    public void testAddUserThrowsExceptionIfGroupsEmptyOrNull()
    {
        try
        {
            def userProps = [username:"user1", password:"password"];
            RsUser.addUser(userProps);
            fail("Should throw exception");
        }
        catch(MessageSourceException e)
        {
            assertEquals ("no.group.specified", e.getCode());
        }
        assertEquals(0,RsUser.count())

        try
        {
            def userProps = [username:"user1", password:"password",groups:[]];
            RsUser.addUser(userProps);
            fail("Should throw exception");
        }
        catch(MessageSourceException e)
        {
            assertEquals ("no.group.specified", e.getCode());
        }
        assertEquals(0,RsUser.count())

        try
        {
            def userProps = [username:"user1", password:"password"];
            RsUser.addUser(userProps,[]);
            fail("Should throw exception");
        }
        catch(MessageSourceException e)
        {
            assertEquals ("no.group.specified", e.getCode());
        }
        assertEquals(0,RsUser.count())
    }

      public void testUpdateUser()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1", password:"password",groups:[group1]];
        RsUser user = RsUser.addUser(userProps);

        assertFalse(user.hasErrors());

        assertEquals (1, user.groups.size());

        //update all props
        user=RsUser.get(id:user.id);
        def updateProps=[username:"user2",password:"password2",groups:[group1,group2]];
        RsUser updatedUser=RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());
        assertEquals(1,RsUser.count());

        assertEquals(updateProps.username,updatedUser.username);
        assertEquals(RsUser.hashPassword(updateProps.password),updatedUser.passwordHash);

        assertEquals (2, updatedUser.groups.size())
        assertEquals(group1.id, updatedUser.groups[0].id);
        assertEquals(group2.id, updatedUser.groups[1].id);

        //update only password
        user=RsUser.get(id:user.id);
        updateProps=[password:"password55"];
        updatedUser=RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());
        assertEquals(user.username,updatedUser.username);
        assertEquals(RsUser.hashPassword(updateProps.password),updatedUser.passwordHash);
        assertEquals (2, updatedUser.groups.size())

        //update only username
        user=RsUser.get(id:user.id);
        updateProps=[username:"testuser"];
        updatedUser=RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());
        assertEquals(updateProps.username,updatedUser.username);
        assertEquals(user.passwordHash,updatedUser.passwordHash);
        assertEquals (2, updatedUser.groups.size())

        //update only groups
        user=RsUser.get(id:user.id);
        updateProps=[groups:[group2]];
        updatedUser=RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());
        assertEquals(user.username,updatedUser.username);
        assertEquals(user.passwordHash,updatedUser.passwordHash);
        assertEquals (1, updatedUser.groups.size())
        assertEquals (group2.id, updatedUser.groups[0].id)


    }
    public void testUpdateUserThrowsExceptionIfGroupsEmpty()
    {
        def group1=Group.add(name:"gr1");
        def userProps = [username:"user1", password:"password",groups:[group1]];
        def user=RsUser.addUser(userProps);
        assertEquals(1,RsUser.count())

        try
        {
            def updateProps=[groups:[]]
            def updatedUser=RsUser.updateUser(user,updateProps);

            fail("Should throw exception");
        }
        catch(MessageSourceException e)
        {
            assertEquals ("no.group.specified", e.getCode());
        }
        assertEquals(1,RsUser.count())

    }

    public void testAddToGroupsAndRemoveFromGroups()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());

        assertEquals(0,user.groups.size());

        def groupsToBeAdded = [group1.name, group2]
        user.addToGroups(groupsToBeAdded);

        assertEquals (2, user.groups.size())
        assertEquals(group1.id, user.groups[0].id)
        assertEquals(group2.id, user.groups[1].id)

        //remove both groups
        user.removeFromGroups([group1.name, group2]);
        assertEquals(0,user.groups.size());

        //add again
        user.addToGroups(groupsToBeAdded);
        assertEquals (2, user.groups.size())
        assertEquals(group1.id, user.groups[0].id)
        assertEquals(group2.id, user.groups[1].id)

        //remove one of group
        user.removeFromGroups([group2.name]);
        assertEquals(1,user.groups.size())
        assertEquals(group1.id, user.groups[0].id)

        //remove the other
        user.removeFromGroups([group1]);
        assertEquals(0,user.groups.size())
    }

    public void testPasswordMethods()
    {
        def passwordList=["xxx","ab11122","xddvfvfv",""];
        passwordList.each{ password ->
            assertEquals(new Sha1Hash(password).toHex(),RsUser.hashPassword(password));
        }

        def group1=Group.add(name:"testgr");

        def user=RsUser.addUser(username:"testuser",password:"123",groups:[group1]);
        assertFalse(user.hasErrors());

        assertTrue(user.isPasswordSame("123"));
        assertFalse(user.isPasswordSame("12"));
        assertFalse(user.isPasswordSame(""));

        def user2=RsUser.addUser(username:"testuser2",password:"",groups:[group1]);
        assertFalse(user2.hasErrors());

        assertTrue(user2.isPasswordSame(""));
        assertFalse(user2.isPasswordSame("abc"));
        assertFalse(user2.isPasswordSame("12"));
    }

    public void testGetCurrentUserName()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());

        assertEquals("If there is no execution context should return system", "system", RsUser.getCurrentUserName());
        assertEquals("We should be able to access currentUserName as property", "system", user.currentUserName);
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def currentUserName = RsUser.getCurrentUserName();
            assertEquals ("If there is no user in execution context should return system", "system", currentUserName);
            currentUserName = user.currentUserName
            assertEquals ("If there is no user in execution context should return system", "system", currentUserName);
        }


        String currentUserNameToBeAddedtoContext = "testuser";
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addUsernameToCurrentContext (currentUserNameToBeAddedtoContext);
            assertEquals(currentUserNameToBeAddedtoContext, RsUser.getCurrentUserName());
            assertEquals(currentUserNameToBeAddedtoContext, user.currentUserName);
        }
    }

}