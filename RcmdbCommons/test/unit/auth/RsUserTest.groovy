package auth


import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.exception.MessageSourceException
import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import connection.LdapConnection

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
        clearMetaClasses();

        initialize([RsUser,Group, ChannelUserInformation,LdapConnection,LdapUserInformation,RsUserInformation], []);
        SegmentQueryHelper.getInstance().initialize([]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        CompassForTests.addOperationSupport (Group, GroupOperations);
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }

    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsUser);
        ExpandoMetaClass.enableGlobally();
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
    public void testAddUniqueUserHasErrorIfUserAlreadyExists()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");


        def userProps = [username:"user1", password:"password",groups:[group1,group2]];
        RsUser user = RsUser.addUniqueUser(userProps);
        assertFalse(user.hasErrors());
        assertEquals (2, user.groups.size())
        assertEquals(1,RsUser.count());

        RsUser user2 = RsUser.addUniqueUser(userProps);
        assertTrue(user2.hasErrors());
        assertEquals(1,RsUser.count());

    }
    public void testAddUserWithGroupList()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def groupsToBeAdded = ["group1", group2]
        def userProps = [username:"user1", password:"password",groups:groupsToBeAdded];
        
        RsUser user = RsUser.addUser(userProps);
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
            def userProps = [username:"user1", password:"password",groups:null];
            RsUser.addUser(userProps);
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

    public void testAddUserAndUpdateUserWithReturnAllModels()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1", password:"password",groups:[group1],email:"myemail"];
        def createdObjects= RsUser.addUser(userProps,true);
        def user=createdObjects.rsUser;
        def emailInformation=createdObjects.emailInformation;

        assertFalse(user.hasErrors());
        assertFalse(emailInformation.hasErrors());
        assertEquals(2,createdObjects.size());

        assertEquals(userProps.username,user.username);
        assertEquals(userProps.email,emailInformation.destination);

        assertEquals (1, user.groups.size());


        def updateProps = [username:"user2",groups:[group1,group2],email:"myemail2"];

        def updatedObjects= RsUser.updateUser(user,updateProps,true);
        def updatedUser=updatedObjects.rsUser;
        def updatedEmailInformation=updatedObjects.emailInformation;

        assertFalse(updatedUser.hasErrors());
        assertFalse(updatedEmailInformation.hasErrors());
        assertEquals(2,updatedObjects.size());

        assertEquals(updateProps.username,updatedUser.username);
        assertEquals(updateProps.email,updatedEmailInformation.destination);

        assertEquals (2, updatedUser.groups.size());

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

    public void testAddLdapInformationAndRetrieveLdapInformation()
    {
        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());

        def ldapConnection=LdapConnection.add(name:"ldapcon",url:"aaa");
        assertFalse(ldapConnection.hasErrors());


        assertEquals(0,LdapUserInformation.count());
        assertNull(user.retrieveLdapInformation());

        def ldapInformation=user.addLdapInformation(userdn:"testdn",ldapConnection:ldapConnection);
        assertFalse(user.hasErrors());
        
        assertEquals(1,LdapUserInformation.count());

        def userInformations=user.userInformations;
        assertEquals(1,userInformations.size());
        assertTrue(userInformations[0] instanceof LdapUserInformation);
        assertEquals(user.id,userInformations[0].userId);
        assertEquals("ldap",userInformations[0].type);
        assertEquals("testdn",userInformations[0].userdn);
        assertEquals(ldapConnection.id,userInformations[0].ldapConnection.id)

        assertEquals(userInformations[0].id,ldapInformation.id);
        assertEquals(user.id,ldapInformation.rsUser.id);

        def retrievedLdapInformation=user.retrieveLdapInformation();
        assertEquals(userInformations[0].id,retrievedLdapInformation.id);        
        
    }

    public void testAddEmailAndRetrieveEmail()
    {
        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());


        assertEquals(0,ChannelUserInformation.count());
        assertEquals("",user.retrieveEmail());
        assertNull(user.retrieveEmailInformation());

        def emailInformation=user.addEmail("testemail");
        assertFalse(user.hasErrors());

        assertEquals(1,ChannelUserInformation.count());

        def userInformations=user.userInformations;
        assertEquals(1,userInformations.size());
        assertTrue(userInformations[0] instanceof ChannelUserInformation);
        assertEquals(user.id,userInformations[0].userId);
        assertEquals("email",userInformations[0].type);
        assertEquals("testemail",userInformations[0].destination);

        assertEquals(userInformations[0].id,emailInformation.id);
        assertEquals(user.id,emailInformation.rsUser.id);

        assertEquals("testemail",user.retrieveEmail());
        
        def retrievedEmailInformation=user.retrieveEmailInformation();
        assertEquals(userInformations[0].id,retrievedEmailInformation.id);

    }
    public void testAddUserAndUpdateUserAddsEmailChannelInformation()
    {
        def group1 = Group.add(name:"group1");

        assertEquals(0,ChannelUserInformation.count());

        def userProps = [username:"user1", password:"password",groups:[group1],email:"myemail"];
        def user= RsUser.addUser(userProps);
        
        assertFalse(user.hasErrors());
        assertEquals(userProps.email,user.retrieveEmail())
        assertEquals(1,ChannelUserInformation.count());

        def updateProps = [email:"myemail2"];

        def updatedUser= RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());
        assertEquals(updateProps.email,updatedUser.retrieveEmail())
        assertEquals(1,ChannelUserInformation.count());
    }

    public void testAddUserRollsBackIfEmailInformationHasErrors()
    {
        RsUser.metaClass.addEmail = { email->
            return ChannelUserInformation.add(userId: 4 );
        }
        
        def group1 = Group.add(name:"group1");


        def userProps = [username:"user1", password:"password",groups:[group1],email:"myemail"];
        def createdObjects= RsUser.addUser(userProps,true);

        def user=createdObjects.rsUser;
        def emailInformation=createdObjects.emailInformation;

        assertFalse(user.hasErrors());
        assertTrue(emailInformation.hasErrors())

        assertEquals(0,ChannelUserInformation.count());
        assertEquals(0,RsUser.count());
    }

    public void testAddUserRollsBackIfUserHasErrors()
    {

        def group1 = Group.add(name:"group1");

        def userProps = [username:null, password:"password",groups:[group1],email:"myemail"];
        def createdObjects= RsUser.addUser(userProps,true);

        def user=createdObjects.rsUser;
        def emailInformation=createdObjects.emailInformation;


        assertTrue(user.hasErrors());
        assertFalse(emailInformation.errors.toString(),emailInformation.hasErrors());
        assertEquals(userProps.email,emailInformation.destination);
        
        assertEquals(0,ChannelUserInformation.count());
        assertEquals(0,RsUser.count());

    }

    public void testUpdateUserRollsBackIfEmailInformationHasErrors()
    {
        RsUser.metaClass.addEmail = { email->
            return ChannelUserInformation.add(userId: 4 );
        }

        def group1 = Group.add(name:"group1");


        def userProps = [username:"user1", passwordHash:"password"];
        def user= RsUser.add(userProps);
        assertFalse(user.errors.toString(),user.hasErrors());
        assertEquals(0,user.groups.size());
        
        assertEquals(0,ChannelUserInformation.count());
        assertEquals(1,RsUser.count());

        def updateProps = [username:"user2", password:"password2",groups:[group1],email:"myemail2"];
        def updatedObjects=RsUser.updateUser(user,updateProps,true);

        assertFalse(updatedObjects.rsUser.hasErrors());
        assertTrue(updatedObjects.emailInformation.hasErrors())
        
        assertEquals(userProps.username,updatedObjects.rsUser.username);
        assertEquals(userProps.passwordHash,updatedObjects.rsUser.passwordHash);
        assertEquals(0,updatedObjects.rsUser.groups.size());


    }

    public void testUpdateUserRollsBackIfUserHasErrors()
    {

        def group1 = Group.add(name:"group1");

        def userProps = [username:"user1", password:"password",groups:[group1],email:"myemail"];
        def createdObjects= RsUser.addUser(userProps,true);

        def user=createdObjects.rsUser;
        def emailInformation=createdObjects.emailInformation;

        assertFalse(user.hasErrors());
        assertFalse(emailInformation.hasErrors());
        assertEquals(userProps.email,emailInformation.destination);

        assertEquals(1,ChannelUserInformation.count());
        assertEquals(1,RsUser.count());

        def updateProps=[username:null,email:"myemail2"];

        def updatedObjects=RsUser.updateUser(user,updateProps,true);

        assertTrue(updatedObjects.rsUser.hasErrors());
        assertFalse(updatedObjects.emailInformation.hasErrors());
        
        assertEquals(userProps.email,updatedObjects.emailInformation.destination);

        assertEquals(1,ChannelUserInformation.count());
        assertEquals(1,RsUser.count());
    }



}