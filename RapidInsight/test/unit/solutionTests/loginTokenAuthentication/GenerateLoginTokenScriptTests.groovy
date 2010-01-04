package solutionTests.loginTokenAuthentication

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.RsUser
import auth.RsUserOperations
import auth.Group;
import auth.Role;
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import application.RapidApplication
import application.Cache
import application.CacheOperations
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.auth.UserConfigurationSpace;
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 3, 2009
* Time: 11:51:16 AM
* To change this template use File | Settings | File Templates.
*/


class GenerateLoginTokenScriptTests  extends RapidCmdbWithCompassTestCase {


    public void setUp() {
        super.setUp();

        initialize([RsUser,Group,Role,RapidApplication,Cache], []);
        SegmentQueryHelper.getInstance().initialize([]);

        CompassForTests.addOperationSupport (Cache, CacheOperations);
        CompassForTests.addOperationSupport (RsUser, RsUserOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);

        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/loginTokenAuthentication"

        RapidApplicationTestUtils.clearUtilityPaths();
        RapidApplicationTestUtils.utilityPaths = ["auth.RsUserTokenAuthenticator": new File("${base_directory}/operations/auth/RsUserTokenAuthenticator.groovy")];

        RapidApplication.getUtility("auth.RsUserTokenAuthenticator").clearCacheEntry();

        UserConfigurationSpace.getInstance().initialize();
        initializeScriptManager();
    }

    public void tearDown() {
        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/loginTokenAuthentication/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('generateLoginToken');
    }
    public void testGenerateLoginTokenForCurrentUser()
    {
        def tokenAuthenticator=RapidApplication.getUtility("auth.RsUserTokenAuthenticator");
        def entry=tokenAuthenticator.getCacheEntry();
        assertEquals(2,entry.size());
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //no such user
        def httpParams=[:]
        def result=runScriptWithUser(httpParams,"testuser");

        
        assertTrue(result.indexOf("Error")>=0);
        assertTrue(result.indexOf("User testuser does not exist")>=0);
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //token generated successfully
        def user=RsUser.add(username:"testuser",passwordHash:"1234");

        result=runScriptWithUser(httpParams,"testuser");
        assertFalse(result.indexOf("Error")>=0);

        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());
        assertNotNull (entry.users["testuser"]);
        assertNotNull (entry.tokens[result]);

        def userEntry=entry.users["testuser"]
        def expireTimeDiff=userEntry.expireAt-System.currentTimeMillis();
        assertTrue(expireTimeDiff<=(60*1000));
        assertTrue(expireTimeDiff>=(55*1000));

        //token with expire
        httpParams.expireAfter="300";
        result=runScriptWithUser(httpParams,"testuser");
        assertFalse(result.indexOf("Error")>=0);

        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());
        assertNotNull (entry.users["testuser"]);
        assertNotNull (entry.tokens[result]);

        userEntry=entry.users["testuser"]
        expireTimeDiff=userEntry.expireAt-System.currentTimeMillis();
        assertTrue(expireTimeDiff<=(300*1000));
        assertTrue(expireTimeDiff>=(295*1000));
    }
    public void testGenerateLoginTokenForOtherUser()
    {
        def tokenAuthenticator=RapidApplication.getUtility("auth.RsUserTokenAuthenticator");
        def entry=tokenAuthenticator.getCacheEntry();
        assertEquals(2,entry.size());
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //no such user
        def httpParams=[tokenUser:"testuser"]
        def result=runScriptWithUser(httpParams,"rsadmin");
        
        assertTrue(result.indexOf("Error")>=0);
        assertTrue(result.indexOf("User rsadmin does not exist")>=0);
        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //not admin

        def userRole=Role.add(name:Role.USER);
        def userGroup=Group.add(name:RsUser.RSUSER,role:userRole);

        def user=RsUser.addUser(username:"rsadmin",password:"1234",groups:[userGroup]);
        assertFalse(user.hasErrors());

        result=runScriptWithUser(httpParams,"rsadmin");
        assertTrue(result.indexOf("Error")>=0);
        assertTrue(result.indexOf("Administrator Role is needed")>=0);

        assertEquals(0,entry.users.size());
        assertEquals(0,entry.tokens.size());

        //admin does successfuly

        def adminRole=Role.add(name:Role.ADMINISTRATOR);
        def adminGroup=Group.add(name:RsUser.RSADMIN,role:adminRole);
        RsUser.updateUser(user,[groups:[adminGroup]])
        assertFalse(user.hasErrors());

        def testUser=RsUser.add(username:"testuser",passwordHash:"1234");
        assertFalse(testUser.hasErrors());

        result=runScriptWithUser(httpParams,"rsadmin");
        assertFalse(result.indexOf("Error")>=0);

        assertEquals(1,entry.users.size());
        assertEquals(1,entry.tokens.size());
        assertNotNull (entry.users["testuser"]);
        assertNotNull (entry.tokens[result]);


    }

     def runScriptWithUser(httpParams,username){

        def scriptParams=[:];
        scriptParams.web=[:];
        scriptParams.web.session=[username:username];
        scriptParams.params=httpParams;

        def result=ScriptManagerForTest.runScript("generateLoginToken",scriptParams);
        println "will run script with params ${scriptParams}"
        println "result from script : ${result}"
        return result;
    }


}