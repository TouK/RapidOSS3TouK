package solutionTests.loginTokenAuthentication

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import application.Cache
import application.CacheOperations
import application.RsApplication
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import com.ifountain.rcmdb.test.util.CompassForTests

import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
import org.jsecurity.authc.AccountException
import auth.RsUser;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 3, 2009
* Time: 9:57:31 AM
* To change this template use File | Settings | File Templates.
*/


class RsUserTokenAuthenticatorTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();

        initialize([Cache,RsApplication,RsUser], []);

        CompassForTests.addOperationSupport (Cache, CacheOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations (RsApplication);

        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/loginTokenAuthentication"

        RsApplicationTestUtils.clearUtilityPaths();
        RsApplicationTestUtils.utilityPaths = ["auth.RsUserTokenAuthenticator": new File("${base_directory}/operations/auth/RsUserTokenAuthenticator.groovy")];

        RsApplication.getUtility("auth.RsUserTokenAuthenticator").clearCacheEntry();
    }

    public void tearDown() {
        RsApplication.getUtility("auth.RsUserTokenAuthenticator").clearCacheEntry();
        super.tearDown();
    }
    public void testAuthenticateUser()
    {
        def tokenAuthenticator=RsApplication.getUtility("auth.RsUserTokenAuthenticator");
        def entry=tokenAuthenticator.getCacheEntry();
        assertEquals(2,entry.size());
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //no login token given
        try{
            tokenAuthenticator.authenticateUser([:]);
            fail("Should throw exception");
        }
        catch(AccountException e)
        {
            assertTrue(e.getMessage().indexOf("LoginToken should be specified")>=0);
        }
        
        //no token entry case
        try{
            tokenAuthenticator.authenticateUser([loginToken:"abc"]);
            fail("Should throw exception");
        }
        catch(IncorrectCredentialsException e)
        {
            assertTrue(e.getMessage().indexOf("No userEntry found for loginToken")>=0);
        }



        //token expired case
        def user=RsUser.add(username:"testuser",passwordHash:"345");
        assertFalse(user.errors.toString(),user.hasErrors());

        def createdToken=tokenAuthenticator.generateTokenForUser("testuser",2);
        Thread.sleep(3000);
        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());
        
        try{
            tokenAuthenticator.authenticateUser([loginToken:createdToken]);
            fail("Should throw exception");
        }
        catch(IncorrectCredentialsException e)
        {

            assertTrue("Wrong message ${e.getMessage()}",e.getMessage().indexOf("LoginToken expired for user testuser")>=0);
        }
        //entries should be deleted after expire
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //successfull case
        user=RsUser.add(username:"testuser",passwordHash:"345");
        assertFalse(user.errors.toString(),user.hasErrors());

        createdToken=tokenAuthenticator.generateTokenForUser("testuser",3);
        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());
        def userFromAuth=tokenAuthenticator.authenticateUser([loginToken:createdToken]);
        assertEquals(user.id,userFromAuth.id);


         //token successfull but user does not exist case
        user=RsUser.add(username:"testuser",passwordHash:"345");
        assertFalse(user.errors.toString(),user.hasErrors());

        createdToken=tokenAuthenticator.generateTokenForUser("testuser",3);
        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());

        user.remove();
        try{
            tokenAuthenticator.authenticateUser([loginToken:createdToken]);
            fail("Should throw exception");
        }
        catch(UnknownAccountException e)
        {

            assertTrue("Wrong message ${e.getMessage()}",e.getMessage().indexOf("User testuser does not exist")>=0);
        }


    }
    public void testGenerateTokenForUser()
    {
        def user=RsUser.add(username:"testuser",passwordHash:"345");
        assertFalse(user.errors.toString(),user.hasErrors());
        def user2=RsUser.add(username:"testuser2",passwordHash:"345");
        assertFalse(user2.errors.toString(),user2.hasErrors());

        def tokenAuthenticator=RsApplication.getUtility("auth.RsUserTokenAuthenticator");
        def entry=tokenAuthenticator.getCacheEntry();
        assertEquals(2,entry.size());
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());
        
        def createdToken=tokenAuthenticator.generateTokenForUser("testuser",5);

        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());

        def userEntry=entry.users["testuser"];
        assertEquals("testuser",userEntry.username);
        assertSame(entry.users[userEntry.username],entry.tokens[userEntry.loginToken])
        assertEquals(userEntry.loginToken,createdToken);
        //check that expireAt is between 4-5 seconds in future
        def expireTimeDiff=userEntry.expireAt-System.currentTimeMillis();
        assertTrue(expireTimeDiff<=(5*1000));
        assertTrue(expireTimeDiff>=(4*1000));

        createdToken=tokenAuthenticator.generateTokenForUser("testuser2",10);

        assertEquals(2,entry.users.size());
        assertEquals(2,entry.tokens.size());

        def userEntry2=entry.users["testuser2"];
        assertEquals("testuser2",userEntry2.username);
        assertSame(entry.users[userEntry2.username],entry.tokens[userEntry2.loginToken])
        assertEquals(userEntry2.loginToken,createdToken);
        //check that expireAt is between 9-10 seconds in future
        expireTimeDiff=userEntry2.expireAt-System.currentTimeMillis();
        assertTrue(expireTimeDiff<=(10*1000));
        assertTrue(expireTimeDiff>=(9*1000));

        assertNotSame(userEntry,userEntry2);
        assertFalse(userEntry.loginToken==userEntry2.loginToken)


        3.times{
           createdToken=tokenAuthenticator.generateTokenForUser("testuser",20);

           assertEquals(2,entry.users.size());
           assertEquals(2,entry.tokens.size());

           userEntry=entry.users["testuser"];
           assertEquals("testuser",userEntry.username);
           assertSame(entry.users[userEntry.username],entry.tokens[userEntry.loginToken])
           assertEquals(userEntry.loginToken,createdToken);
        }

    }
    public void testCreateToken()
    {
        //does not create same token for the same user
        def tokenAuthenticator=RsApplication.getUtility("auth.RsUserTokenAuthenticator");
        10.times{
            def token1=tokenAuthenticator.createToken("testuser");
            def token2=tokenAuthenticator.createToken("testuser");
            println "tok1: ${token1} tok2: ${token2}"
            assertFalse(token1==token2);
            assertNotSame(token1,token2);
            assertTrue(token1.indexOf("testuser")<0);
            assertTrue(token2.indexOf("testuser")<0);
        }

        //does not create same token for different users
        10.times{
            def token1=tokenAuthenticator.createToken("testuser1");
            def token2=tokenAuthenticator.createToken("testuser2");
            println "tok1: ${token1} tok2: ${token2}"
            assertFalse(token1==token2);
            assertNotSame(token1,token2);
            assertTrue(token1.indexOf("testuser1")<0);
            assertTrue(token2.indexOf("testuser2")<0);
        }
    }
}