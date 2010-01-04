package solutionTests.ldapAuthentication

import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import connection.LdapConnection
import com.ifountain.comp.test.util.logging.TestLogUtils
import connection.LdapConnectionOperations
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.test.util.DatasourceTestUtils
import application.RapidApplication
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils;
import auth.RsUser;
import auth.Group;
import auth.Role;
import auth.ChannelUserInformation;
import auth.LdapUserInformation;
import auth.RsUserInformation;
import auth.RsUserOperations;
import auth.GroupOperations
import com.ifountain.rcmdb.auth.UserConfigurationSpace;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 5:00:28 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserLdapAuthenticatorTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        clearMetaClasses();

        initialize([RsUser,Group,Role, ChannelUserInformation,LdapConnection,LdapUserInformation,RsUserInformation,RapidApplication], []);
        SegmentQueryHelper.getInstance().initialize([]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        CompassForTests.addOperationSupport (Group, GroupOperations);
        CompassForTests.addOperationSupport (LdapConnection, LdapConnectionOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);

        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);

        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/ldapAuthentication"

        RapidApplicationTestUtils.clearUtilityPaths();
        RapidApplicationTestUtils.utilityPaths = ["auth.RsUserLdapAuthenticator": new File("${base_directory}/operations/auth/RsUserLdapAuthenticator.groovy")];
        UserConfigurationSpace.getInstance().initialize();
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }

    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(LdapConnection);
        ExpandoMetaClass.enableGlobally();
    }
    public void testAuthenticateUser()
    {
        def ldapAuthenticator=RapidApplication.getUtility("auth.RsUserLdapAuthenticator");

        //no ldap information for user case
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1", password:"123",groups:[group1,group2]];
        RsUser user = RsUser.addUser(userProps);


        try{
            ldapAuthenticator.authenticateUser([login:"user1",password:"12345"]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.UnknownAccountException e)
        {
            assertTrue(e.getMessage().indexOf("Ldap Information could not be found for 'user1'")>=0);
        }

        //no ldapConnection in user ldapInformation
        def ldapInformation=user.addLdapInformation(userdn: "ldapDn");
        try{
            ldapAuthenticator.authenticateUser([login:"user1",password:"12345"]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.UnknownAccountException e)
        {
            assertTrue("Wrong message ${e.getMessage()}",e.getMessage().indexOf("LdapInformation is not bound with an LdapConnection for user 'user1'")>=0);
        }

        //ldap checkAuthentication fails
        def ldapConnection=LdapConnection.add(name:"ldapcon",url:"aaa");
        assertFalse(ldapConnection.hasErrors());


        LdapConnection.metaClass.checkAuthentication={ String authUsername,String authPassword ->
            return false;
        }

        ldapInformation=user.addLdapInformation(userdn: "ldapDn",ldapConnection:ldapConnection);
        try{
            ldapAuthenticator.authenticateUser([login:"user1",password:"12345"]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.IncorrectCredentialsException e)
        {
            assertTrue("Wrong message ${e.getMessage()}",e.getMessage().indexOf("Invalid Ldap password for user 'user1'")>=0);
        }

        //successfull login case
        LdapConnection.metaClass.checkAuthentication={ String authUsername,String authPassword ->
            return true;
        }

        def userFromAuth=ldapAuthenticator.authenticateUser([login:"user1",password:"123"]);
        assertEquals(user.id,userFromAuth.id);
    }
}