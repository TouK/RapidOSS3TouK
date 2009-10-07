package scriptTests


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
import com.ifountain.rcmdb.auth.SegmentQueryHelper;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 7, 2009
* Time: 4:55:24 PM
* To change this template use File | Settings | File Templates.
*/
class TransferLdapUsersScriptTest extends RapidCmdbWithCompassTestCase {

     public void setUp() {
        super.setUp();


        initialize([RsUser,Group,Role,LdapConnection,RsUserInformation,LdapUserInformation], []);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        CompassForTests.addOperationSupport (Group,GroupOperations);
        CompassForTests.addOperationSupport (LdapConnection,LdapConnectionOperations);
        initializeScriptManager();

        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        SegmentQueryHelper.getInstance().initialize([]);
    }

    public void tearDown() {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidCMDB/scripts"
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

        def group=Group.add(name:RsUser.RSUSER);
        assertFalse(group.hasErrors());

        
        def scriptResult=ScriptManagerForTest.runScript("TransferLdapUsers",[:]);
        println scriptResult.replace("<br>","\n<br>")

        //admin and ldapuser should exist and there may be other users
        assertTrue(RsUser.count()>=2);

        //check ldapUser
        def ldapUser=RsUser.get(username:"ldapuser");
        assertNotNull(ldapUser);
        assertTrue(ldapUser.isPasswordSame(""));
        assertEquals(group.id,ldapUser.groups[0].id);

        def ldapUserInformation=ldapUser.retrieveLdapInformation();
        assertNotNull(ldapUserInformation);
        assertEquals("CN=ldapuser,CN=Users,DC=molkay,DC=selfip,DC=net",ldapUserInformation.userdn);
        assertEquals(ldapConnection.id,ldapUserInformation.ldapConnection.id);

        //check adminUser
        def adminUser=RsUser.get(username:"admin");
        assertNotNull(adminUser);
        assertTrue(adminUser.isPasswordSame(""));
        assertEquals(group.id,adminUser.groups[0].id);

        def adminUserInformation=adminUser.retrieveLdapInformation();
        assertNotNull(adminUserInformation);
        assertEquals("CN=admin,CN=Users,DC=molkay,DC=selfip,DC=net",adminUserInformation.userdn);
        assertEquals(ldapConnection.id,adminUserInformation.ldapConnection.id);

    }

}