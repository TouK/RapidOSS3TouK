package solutionTests.ldapAuthentication


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.*
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import connection.LdapConnection;
import connection.LdapConnectionOperations
import com.ifountain.rcmdb.test.util.LdapConnectionTestUtils
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.auth.UserConfigurationSpace;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 7, 2009
* Time: 4:55:24 PM
* To change this template use File | Settings | File Templates.
*/
class TransferLdapUsersScriptTest extends RapidCmdbWithCompassTestCase {

     def ROOT_DN="DC=molkay,DC=selfip,DC=net";

     public void setUp() {
        super.setUp();


        initialize([RsUser,Group,Role,LdapConnection,RsUserInformation,LdapUserInformation], []);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        CompassForTests.addOperationSupport (Group,GroupOperations);
        CompassForTests.addOperationSupport (LdapConnection,LdapConnectionOperations);
        initializeScriptManager();

        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        SegmentQueryHelper.getInstance().initialize([]);
        UserConfigurationSpace.getInstance().initialize();
    }

    public void tearDown() {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/ldapAuthentication/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('TransferLdapUsers');

    }

    //admin and ldapuser should exist in ldap server
    public void testTransferLdapUsersSuccessfully()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        def connectionParams=[name:"ldapConnection"];
        connectionParams.putAll (params);
        def ldapConnection=LdapConnection.add(connectionParams);
        assertFalse(ldapConnection.hasErrors());

        def role=Role.add(name:Role.USER);
        assertFalse(role.hasErrors())

        def group=Group.add(name:RsUser.RSUSER,role:role);
        assertFalse(group.hasErrors());

        def scriptResult=ScriptManagerForTest.runScript("TransferLdapUsers",[:]);
        println scriptResult.replace("<br>","\n<br>")

        //no Exceptions in result
        assertTrue(scriptResult.indexOf("Exception")<0)

        //check users

        //admin and ldapuser should exist and there may be other users
        assertTrue(RsUser.count()>=2);

        //check ldapUser
        def ldapUser=RsUser.get(username:"ldapuser");
        assertNotNull(ldapUser);
        assertTrue(ldapUser.isPasswordSame(""));
        assertEquals(group.id,ldapUser.groups[0].id);

        def ldapUserInformation=ldapUser.retrieveLdapInformation();
        assertNotNull(ldapUserInformation);
        assertEquals("CN=ldapuser,CN=Users,${ROOT_DN}",ldapUserInformation.userdn);
        assertEquals(ldapConnection.id,ldapUserInformation.ldapConnection.id);

        //check adminUser
        def adminUser=RsUser.get(username:"admin");
        assertNotNull(adminUser);
        assertTrue(adminUser.isPasswordSame(""));
        assertEquals(group.id,adminUser.groups[0].id);

        def adminUserInformation=adminUser.retrieveLdapInformation();
        assertNotNull(adminUserInformation);
        assertEquals("CN=admin,CN=Users,${ROOT_DN}",adminUserInformation.userdn);
        assertEquals(ldapConnection.id,adminUserInformation.ldapConnection.id);


        //check groups , rsuser already exists at leasts users and administrators groups should be added
        assertTrue(Group.count()>=3);

        //check Users group
        def usersGroup=Group.get(name:"Users");
        assertNotNull (usersGroup);
        def usersGroup_users=usersGroup.users;
        assertTrue(usersGroup_users.size()>=1);
        assertTrue(usersGroup_users.findAll{it.id==ldapUser.id}.size()==1)
        assertTrue(usersGroup_users.findAll{it.id==adminUser.id}.size()==0)

        //check administratorsGroup
        def administratorsGroup=Group.get(name:"Administrators");
        assertNotNull(administratorsGroup);

        def administratorsGroup_users=administratorsGroup.users;
        assertTrue(administratorsGroup_users.size()>=1);
        assertTrue(administratorsGroup_users.findAll{it.id==ldapUser.id}.size()==0)
        assertTrue(administratorsGroup_users.findAll{it.id==adminUser.id}.size()==1)


    }

    public void testScriptDoesNotThrowExceptionIfLdapConnectionObjectIsMissing()
    {
        def scriptResult=ScriptManagerForTest.runScript("TransferLdapUsers",[:]);
        println scriptResult.replace("<br>","\n<br>")

        assertTrue(scriptResult.indexOf("No connection found with id")>=0)
    }

    public void testScriptThrowsExceptionIfLdapConnectionIsWrong()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        def connectionParams=[name:"ldapConnection"];
        connectionParams.putAll (params);
        connectionParams.username="nosuch_user_343434";

        def ldapConnection=LdapConnection.add(connectionParams);
        assertFalse(ldapConnection.hasErrors());

        try{
            def scriptResult=ScriptManagerForTest.runScript("TransferLdapUsers",[:]);
            println scriptResult.replace("<br>","\n<br>")

            fail("should throw AuthenticationException");
        }
        catch(Exception e)
        {
            assertTrue(e.getMessage().indexOf("Exception occured while connecting to ldap")>=0);
        }

    }

}