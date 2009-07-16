package message

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.RsUser
import auth.RsUserInformation
import auth.ChannelUserInformation
import auth.Role
import com.ifountain.rcmdb.test.util.CompassForTests
import auth.Group
import auth.RsUserOperations
import auth.GroupOperations
import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 16, 2009
* Time: 1:18:17 PM
* To change this template use File | Settings | File Templates.
*/
class RsMessageRuleOperationsTest extends RapidCmdbWithCompassTestCase {


    public void setUp()
    {
        super.setUp();
        clearMetaClasses();
        //SegmentQueryHelper.getInstance().initialize ([]);
    }

    public void tearDown()
    {
        clearMetaClasses();
        super.tearDown();
    }
    def initializeModels()
    {                                                   
        initialize ([RsMessage,RsMessageRule,RsUser,RsUserInformation,ChannelUserInformation,Role,Group],[]);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsMessageRule,RsMessageRuleOperations);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        //CompassForTests.addOperationSupport (Group,GroupOperations);
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsMessageRuleOperations)
        ExpandoMetaClass.enableGlobally();
    }

    public void testGetDestinationConfig()
    {
        def config=RsMessageRuleOperations.getDestinationConfig();
        assertEquals(1,config.size());
        //assertEquals("email",config["email"].channelInformationType);
        assertEquals("email",config["email"]);
    }
    public void testGetDestinationNames()
    {
        def defaultNames=RsMessageRuleOperations.getDestinationNames();
        assertEquals(1,defaultNames.size());
        assertEquals("email",defaultNames[0]);

//        RsMessageRuleOperations.metaClass.'static'.getDestinationConfig = { ->
//            return ["email":[channelInformationType:"email"],
//                    "dest2":[:]];
//        }

        RsMessageRuleOperations.metaClass.'static'.getDestinationConfig = { ->
            return ["email":"email",
                    "dest2":""];
        }

        def names=RsMessageRuleOperations.getDestinationNames();
        assertEquals(2,names.size());
        assertEquals("email",names[0]);
        assertEquals("dest2",names[1]);

        assertTrue(RsMessageRuleOperations.isChannelDestination("email"))
        assertFalse(RsMessageRuleOperations.isChannelDestination("dest2"))

        def channelDestinationNames=RsMessageRuleOperations.getChannelDestinationNames();
        assertEquals(1,channelDestinationNames.size());
        assertEquals("email",channelDestinationNames[0]);
        
        def nonChannelDestinationNames=RsMessageRuleOperations.getNonChannelDestinationNames();
        assertEquals(1,nonChannelDestinationNames.size());
        assertEquals("dest2",nonChannelDestinationNames[0]);


    }

    public void testAddAndUpdateMessageRuleForUserThrowsExceptionIfUserDoesNotExist()
    {
         initializeModels();
         def params=[:]
         def username="testuser";

         try{
             RsMessageRule.addMessageRuleForUser(params,username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("No user defined with username")>=0)
         }

         //test for update
         def existingRule=createRsMessageRuleForTest();
         try{
             RsMessageRule.updateMessageRuleForUser(existingRule,params,username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("No user defined with username")>=0)
         }
    }

    public void testAddAndUpdateMessageRuleForUserThrowsExceptionIfDestinationIsChannelTypeAndUserDoesNotHaveDestination()
    {
         initializeModels();
         def params=[:]
         params.destinationType="email";
         def username="testuser";

         def user=RsUser.add(username:username,passwordHash:"aaa");
         assertFalse(user.hasErrors());

         try{
             RsMessageRule.addMessageRuleForUser(params,user.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for ${params.destinationType} is not defined")>=0)
         }

        def group=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);

        def adminUser=RsUser.add(username:"adminUser",passwordHash:"aaa",groups:[group]);
        assertFalse(user.hasErrors());
        assertTrue(adminUser.hasRole(Role.ADMINISTRATOR));

         try{
             RsMessageRule.addMessageRuleForUser(params,adminUser.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for ${params.destinationType} is not defined")>=0)
         }

         
         //test for update
         def existingRule=createRsMessageRuleForTest();
         try{
             RsMessageRule.updateMessageRuleForUser(existingRule,params,user.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for ${params.destinationType} is not defined")>=0);
         }

    }
    
    public void testAddMessageAndUpdateRuleForUserThrowsExceptionIfDestinationIsNonChannelAndUserIsNotAdmin()
    {
        initializeModels();

//        RsMessageRule.metaClass.'static'.getDestinationConfig = { ->
//           return ["email":[channelInformationType:"email"],
//                   "dest2":[:]];
//       }

        RsMessageRule.metaClass.'static'.getDestinationConfig = { ->
           return ["email":"email",
                   "dest2":""];
       }

        def params=[:]
        params.destinationType="dest2";
        def username="testuser";

        def group=createGroupWithRole("testgroup",Role.USER);

        def user=RsUser.add(username:username,passwordHash:"aaa",groups:[group]);
        assertFalse(user.hasErrors());

        try{
             RsMessageRule.addMessageRuleForUser(params,user.username);
             fail("should throw exception");
        }
        catch(e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().indexOf("does not have permission to create rule with Non-Channel destination")>=0)
        }

        //test for update
         def existingRule=createRsMessageRuleForTest();
         try{
             RsMessageRule.updateMessageRuleForUser(existingRule,params,user.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("does not have permission to create rule with Non-Channel destination")>=0)
         }

    }

    public void testAddAndUpdateMessageRuleForUserWithChannelDestination()
    {
        initializeModels();
        
        def params=[:]
        params.destinationType="email";
        params.searchQueryId=44;
        params.delay=5;
        params.clearAction=true;
        params.enabled=false;

        def username="testuser";
        def userEmail="testemail";

        def user=RsUser.add(username:username,passwordHash:"aaa");
        assertFalse(user.hasErrors());
        user.addEmail(userEmail);


        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());
        

        assertEquals(user.id,messageRule.userId);
        assertEquals(params.destinationType,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.clearAction,messageRule.clearAction);
        assertEquals(params.enabled,messageRule.enabled);

        //test update
        def updateParams=[:]
        updateParams.destinationType="email";
        updateParams.searchQueryId=49;
        updateParams.delay=7;
        updateParams.clearAction=false;
        updateParams.enabled=true;

        RsMessageRule.updateMessageRuleForUser(messageRule,updateParams,username);


        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.id,messageRule.userId);
        assertEquals(updateParams.destinationType,messageRule.destinationType);
        assertEquals(updateParams.searchQueryId,messageRule.searchQueryId);
        assertEquals(updateParams.delay,messageRule.delay);
        assertEquals(updateParams.clearAction,messageRule.clearAction);
        assertEquals(updateParams.enabled,messageRule.enabled);


    }

    public void testAddAndUpdateMessageRuleForUserWithNonChannelDestination()
    {
        initializeModels();

//        RsMessageRule.metaClass.'static'.getDestinationConfig = { ->
//           return ["email":[channelInformationType:"email"],
//                   "dest2":[:]];
//        }


        RsMessageRule.metaClass.'static'.getDestinationConfig = { ->
           return ["email":"email",
                   "dest2":""];
        }

        def params=[:]
        params.destinationType="dest2";
        params.searchQueryId=44;
        params.delay=5;
        params.clearAction=true;
        params.enabled=false;

        def username="testuser";
        def userEmail="testemail";

        def group=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);

        def user=RsUser.add(username:username,passwordHash:"aaa",groups:[group]);
        assertFalse(user.hasErrors());


        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.id,messageRule.userId);
        assertEquals(params.destinationType,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.clearAction,messageRule.clearAction);
        assertEquals(params.enabled,messageRule.enabled);


        //test update
        def updateParams=[:]
        updateParams.destinationType="dest2";
        updateParams.searchQueryId=49;
        updateParams.delay=7;
        updateParams.clearAction=false;
        updateParams.enabled=true;

        RsMessageRule.updateMessageRuleForUser(messageRule,updateParams,username);


        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.id,messageRule.userId);
        assertEquals(updateParams.destinationType,messageRule.destinationType);
        assertEquals(updateParams.searchQueryId,messageRule.searchQueryId);
        assertEquals(updateParams.delay,messageRule.delay);
        assertEquals(updateParams.clearAction,messageRule.clearAction);
        assertEquals(updateParams.enabled,messageRule.enabled);

    }

     private def createGroupWithRole(groupName,roleName)
     {
        def role=Role.add(name:roleName);
        assertFalse(role.hasErrors());
        def group=Group.add(name:groupName,role:role);
        assertFalse(group.hasErrors());

        return group;
     }

     private def createRsMessageRuleForTest()
     {
         def rule=RsMessageRule.add(searchQueryId:1,userId:1,destinationType:"fortest",delay:0);
         assertFalse(rule.hasErrors());
         return rule;
     }


}
