package auth

import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.auth.SegmentQueryHelper

import application.RsApplication
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 5:16:52 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserLocalAuthenticatorTest extends RapidCmdbWithCompassTestCase{
      public void setUp() {
        super.setUp();
        initialize([RsUser,Group,Role, RsApplication], []);
        SegmentQueryHelper.getInstance().initialize([]);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        CompassForTests.addOperationSupport (Group, GroupOperations);

        RsApplicationTestUtils.initializeRsApplicationOperations (RsApplication);

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testAuthenticateUser()
    {
        def localAuthenticator=RsApplication.getUtility("auth.RsUserLocalAuthenticator");

        //no username
        try{
            localAuthenticator.authenticateUser([:]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.AccountException e)
        {

            assertTrue("Wrong message : ${e.getMessage()}",e.getMessage().indexOf("Null usernames are not allowed")>=0);
        }

        //no user case
        try{
            localAuthenticator.authenticateUser([login:"nouser",password:"123"]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.UnknownAccountException e)
        {
            assertTrue(e.getMessage().indexOf("No account found for user nouser")>=0);
        }

        //wrong password case
        def group1 = Group.add(name:"group1");
        def group2 = Group.add(name:"group2");

        def userProps = [username:"user1", password:"123",groups:[group1,group2]];
        RsUser user = RsUser.addUser(userProps);


        try{
            localAuthenticator.authenticateUser([login:"user1",password:"12345"]);
            fail("should throw Exception");
        }
        catch(org.jsecurity.authc.IncorrectCredentialsException e)
        {
            assertTrue(e.getMessage().indexOf("Invalid password for user 'user1'")>=0);
        }

        //successfull login case
        def userFromAuth=localAuthenticator.authenticateUser([login:"user1",password:"123"]);
        assertEquals(user.id,userFromAuth.id);
    }
}