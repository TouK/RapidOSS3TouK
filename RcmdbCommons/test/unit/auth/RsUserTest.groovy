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

        initialize([RsUser,Group,Role, ChannelUserInformation,LdapConnection,LdapUserInformation,RsUserInformation], []);
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

        def userGroups=user.groups;
        assertEquals (2, userGroups.size())
        assertNotNull(userGroups.find{it.id == group1.id})
        assertNotNull(userGroups.find{it.id == group2.id})
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

        def userGroups=user.groups;
        assertEquals (2, userGroups.size())
        assertNotNull(userGroups.find{it.id == group1.id})
        assertNotNull(userGroups.find{it.id == group2.id})
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
    public void testAddUserWithGroupListThrowsExceptionIfGroupDoesNotExist()
    {
        def groupsToBeCreated = ["group1"]
        def userProps = [username:"user1", password:"password",groups:groupsToBeCreated];

        try
        {
            RsUser.addUser(userProps);
            fail("Should throw exception");
        }
        catch(Exception e)
        {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("Could not create user since")>=0)
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

    public void testUpdateUserWithGroupList()
    {
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");


        def userProps = [username:"user1", passwordHash:"password"];
        RsUser user = RsUser.add(userProps);

        assertFalse(user.hasErrors());

        assertEquals (0, user.groups.size());

        def groupsToBeAdded = ["group1", group2]
        def updateProps = [groups:groupsToBeAdded];

        RsUser updatedUser = RsUser.updateUser(user,updateProps);
        assertFalse(updatedUser.hasErrors());

        assertEquals(userProps.username,updatedUser.username);
        
        def userGroups=updatedUser.groups;
        assertEquals (2, userGroups.size())
        assertNotNull(userGroups.find{it.id == group1.id})
        assertNotNull(userGroups.find{it.id == group2.id})
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

    public void testAddChannelInformationAndRetrieveChannelInformation()
    {
        def channelType="email";
        def userDestination="testemail";

        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());


        assertEquals(0,ChannelUserInformation.count());
        //assertEquals("",user.retrieveEmail());
        assertNull(user.retrieveChannelInformation(channelType));

        def emailInformation=user.addChannelInformation(type:channelType,destination:userDestination);
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

        //assertEquals("testemail",user.retrieveEmail());
        
        def retrievedChannelInformation=user.retrieveChannelInformation(channelType);
        assertEquals(userInformations[0].id,retrievedChannelInformation.id);

    }
    public void testAddChannelInformations()
    {
        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());

        assertEquals(0,ChannelUserInformation.count());


        def addedChannels=user.addChannelInformations([
                                        [type:"email",destination:"useremail"],
                                        [type:"jabber",destination:"usrjabber"]
                                    ]);

        assertEquals(2,ChannelUserInformation.count());
        assertEquals(2,user.userInformations.size());
        
        assertEquals(2,addedChannels.size());
        addedChannels.each{ channelInfo ->
            assertEquals(user.id,channelInfo.userId);
            assertFalse(channelInfo.hasErrors());
        }

        assertEquals("email",addedChannels[0].type);
        assertEquals("useremail",addedChannels[0].destination);

        assertEquals("jabber",addedChannels[1].type);
        assertEquals("usrjabber",addedChannels[1].destination);

        
        
    }
    public void testAddChannelInformationsAndRollBackIfErrorOccursRollsBackWhenErrorOccurs()
    {
        def userProps = [username:"user1",passwordHash:"password"];
        def user=RsUser.add(userProps);
        assertFalse(user.hasErrors());

        assertEquals(0,ChannelUserInformation.count());

        user.addChannelInformation(type:"email",destination:"email1");

        assertEquals(1,ChannelUserInformation.count());

        def addedChannels=user.addChannelInformationsAndRollBackIfErrorOccurs([
                                        [type:"email",destination:"email2"],
                                        [type:"jabber",destination:"jabber2"],
                                        [type:null,destination:"usrjabber"]
                                    ]);


        assertEquals(1,ChannelUserInformation.count());
        assertEquals(1,user.userInformations.size());

        def userEmailInformation=user.retrieveChannelInformation("email");
        assertEquals("email1",userEmailInformation.destination);

        assertEquals(3,addedChannels.size());
        assertFalse(addedChannels[0].hasErrors());
        assertFalse(addedChannels[1].hasErrors());
        assertTrue(addedChannels[2].hasErrors());

    }

  
    public void testRemovingUserRemovesUserInformations()
    {
        def user=RsUser.add(username:"testuser",passwordHash:"aaa");
        assertFalse(user.errors.toString(),user.hasErrors());

        user.addChannelInformation(type:"email",destination:"useremail");

        assertEquals(1,RsUser.count());
        assertEquals(1,ChannelUserInformation.count());

        user.remove();

        assertEquals(0,RsUser.count());
        assertEquals(0,ChannelUserInformation.count());

    }
    public void testUserCannotDeleteOwnAccountAndRsAdminUser()
    {
        def user=RsUser.add(username:RsUser.RSADMIN,passwordHash:"aaa");
        assertFalse(user.errors.toString(),user.hasErrors());
        try{
            user.remove();
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}","Can not delete user ${RsUser.RSADMIN}",e.getMessage());
        }

        assertEquals(1,RsUser.count());

        user=RsUser.add(username:"system",passwordHash:"aaa");
        assertFalse(user.errors.toString(),user.hasErrors());
        try{
            user.remove();
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}","Can not delete your own account",e.getMessage());
        }

        assertEquals(2,RsUser.count());
        
        //test a successfull remove
        user=RsUser.add(username:"testuser",passwordHash:"aaa");
        assertFalse(user.errors.toString(),user.hasErrors());

        assertEquals(3,RsUser.count());

        user.remove();

        assertEquals(2,RsUser.count());
    }

    public void testHasRole()
    {
        def role=Role.add(name:Role.ADMINISTRATOR);
        assertFalse(role.hasErrors());
        def group = Group.add(name:"group1",role:role);
        assertFalse(group.hasErrors());

        def userProps = [username:"user1", passwordHash:"password",groups:[group]];
        def user= RsUser.add(userProps);
        assertFalse(user.hasErrors());


        assertTrue(user.hasRole(Role.ADMINISTRATOR));
        assertFalse(user.hasRole(Role.USER));
        assertFalse(user.hasRole("abc"));
        
        user.removeRelation(groups:group);
        assertFalse(user.hasErrors());

        assertEquals(0,user.groups.size());
        assertFalse(user.hasRole(Role.ADMINISTRATOR));
        assertFalse(user.hasRole(Role.USER));
        assertFalse(user.hasRole("abc"));
    }

    public void testHasAllRoles()
    {
        def adminRole=Role.add(name:Role.ADMINISTRATOR);
        assertFalse(adminRole.hasErrors());
        def adminGroup = Group.add(name:"group1",role:adminRole);
        assertFalse(adminGroup.hasErrors());

        def userRole=Role.add(name:Role.USER);
        assertFalse(userRole.hasErrors());
        def userGroup = Group.add(name:"group2",role:userRole);
        assertFalse(userGroup.hasErrors());


        def userProps = [username:"user1", passwordHash:"password",groups:[adminGroup]];
        def user= RsUser.add(userProps);
        assertFalse(user.hasErrors());


        assertTrue(user.hasAllRoles([]));
        assertFalse(user.hasAllRoles([Role.USER]));
        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER]));
        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER,"abc"]));

        user.addRelation(groups:userGroup);
        assertFalse(user.hasErrors());
        assertEquals(2,user.groups.size());

        assertTrue(user.hasAllRoles([Role.ADMINISTRATOR]));
        assertTrue(user.hasAllRoles([Role.USER]));
        assertTrue(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER]));
        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER,"abc"]));

        user.update(groups:[]);
        assertFalse(user.hasErrors());
        assertEquals(0,user.groups.size());

        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR]));
        assertFalse(user.hasAllRoles([Role.USER]));
        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER]));
        assertFalse(user.hasAllRoles([Role.ADMINISTRATOR,Role.USER,"abc"]));
    }


    public void testGetChannelTypes()
    {
        assertEquals(["email"],RsUser.getChannelTypes());
        assertEquals(["email"],RsUser.getEditableChannelTypes());

    }
}