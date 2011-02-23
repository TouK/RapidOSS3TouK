package message

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
import com.ifountain.rcmdb.auth.UserConfigurationSpace
import connector.NotificationConnector

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
        RsMessageRuleOperations.setConfiguredDestinationNames(null);
    }

    public void tearDown()
    {
        RsMessageRuleOperations.setConfiguredDestinationNames(null);
        clearMetaClasses();
        super.tearDown();
    }
    def initializeModels()
    {
        initialize ([RsMessage,RsMessageRule,RsUser,RsUserInformation,ChannelUserInformation,Role,Group,NotificationConnector],[]);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsMessageRule,RsMessageRuleOperations);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);

        //CompassForTests.addOperationSupport (Group,GroupOperations);
        UserConfigurationSpace.getInstance().initialize();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsMessageRuleOperations)
        ExpandoMetaClass.enableGlobally();
    }

    public void testGetDestinations()
    {
        RsMessageRuleOperations.setConfiguredDestinationNames([]);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);

        def config=RsMessageRuleOperations.getDestinations();
        assertEquals(1,config.size());

        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["name"]);
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["channelType"]);

        RsMessageRuleOperations.setConfiguredDestinationNames(["emailConnector","jabberConnector"]);

        config=RsMessageRuleOperations.getDestinations();
        assertEquals(3,config.size());

        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["name"]);
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["channelType"]);

        assertEquals("emailConnector",config[1]["name"]);
        assertEquals("emailConnector",config[1]["channelType"]);

        assertEquals("jabberConnector",config[2]["name"]);
        assertEquals("jabberConnector",config[2]["channelType"]);

        RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);

        config=RsMessageRuleOperations.getDestinations();
        assertEquals(2,config.size());

        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["name"]);
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["channelType"]);

        assertEquals("email",config[1]["name"]);
        assertEquals("email",config[1]["channelType"]);;

        RsMessageRuleOperations.setConfiguredDestinationNames([]);

        config=RsMessageRuleOperations.getDestinations();
        assertEquals(1,config.size());

        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["name"]);          
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,config[0]["channelType"]);
    }
    public void testGetAndCacheConnectorDestinationNames()
    {
        initializeModels();
        def rule=RsMessageRule.add([:]);
        println rule.errors

        def configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(0,configuredDestinationNames.size());


        NotificationConnector.add(name:"jabberConnector",showAsDestination:true);
        NotificationConnector.add(name:"jabberConnector2",showAsDestination:false);
        NotificationConnector.add(name:"emailConnector",showAsDestination:true);
        NotificationConnector.add(name:"emailConnector2",showAsDestination:true);

        assertEquals(4,NotificationConnector.count())

        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(0,configuredDestinationNames.size());


        RsMessageRuleOperations.cacheConnectorDestinationNames();

        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(3,configuredDestinationNames.size());

        //should be sorted
        assertEquals("emailConnector",configuredDestinationNames[0]);
        assertEquals("emailConnector2",configuredDestinationNames[1]);
        assertEquals("jabberConnector",configuredDestinationNames[2]);

        //set to empty and check
        RsMessageRuleOperations.setConfiguredDestinationNames([]);
        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(0,configuredDestinationNames.size());

        //set to null and check destinations are cached
        RsMessageRuleOperations.setConfiguredDestinationNames(null);
        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(3,configuredDestinationNames.size());

        NotificationConnector.removeAll("name:emailConnector*");

        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(3,configuredDestinationNames.size());

        RsMessageRuleOperations.cacheConnectorDestinationNames();

        configuredDestinationNames=RsMessageRuleOperations.getConfiguredDestinationNames();
        assertEquals(1,configuredDestinationNames.size());
        assertNotNull(configuredDestinationNames.find{it=="jabberConnector"})
    }
    public void testRsMessageRuleAndRsUserSharesConfiguredDestinationNames()
    {
         RsMessageRuleOperations.setConfiguredDestinationNames([]);
         CompassForTests.addOperationSupport (RsUser,RsUserOperations);
         
         assertEquals([],RsMessageRuleOperations.getConfiguredDestinationNames());
         assertEquals([],RsUser.getConfiguredDestinationNames());

         RsMessageRuleOperations.setConfiguredDestinationNames(["email","jabber"]);

         assertEquals(["email","jabber"],RsMessageRuleOperations.getConfiguredDestinationNames());
         assertEquals(["email","jabber"],RsUser.getConfiguredDestinationNames());
         assertSame(RsMessageRuleOperations.getConfiguredDestinationNames(),RsUser.getConfiguredDestinationNames())

         RsMessageRuleOperations.setConfiguredDestinationNames([]);
         assertEquals([],RsMessageRuleOperations.getConfiguredDestinationNames());
         assertEquals([],RsUser.getConfiguredDestinationNames());
         assertSame(RsMessageRuleOperations.getConfiguredDestinationNames(),RsUser.getConfiguredDestinationNames())
    }

    public void testGetDestinationNames()
    {
        RsMessageRuleOperations.setConfiguredDestinationNames([]);
        CompassForTests.addOperationSupport (RsUser,RsUserOperations);
        
        def defaultNames=RsMessageRuleOperations.getDestinationNames();
        assertEquals(1,defaultNames.size());
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,defaultNames[0]);

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
        }

        def names=RsMessageRuleOperations.getDestinationNames();
        assertEquals(2,names.size());
        assertEquals("email",names[0]);
        assertEquals("dest2",names[1]);

        assertTrue(RsMessageRuleOperations.isChannelType(RsMessageRuleOperations.getDestinationChannelType("email")))
        assertFalse(RsMessageRuleOperations.isChannelType(RsMessageRuleOperations.getDestinationChannelType("dest2")))

        def channelDestinationNames=RsMessageRuleOperations.getChannelDestinationNames();        
        assertEquals(1,channelDestinationNames.size());
        assertEquals("email",channelDestinationNames[0]);
        
        def nonChannelDestinationNames=RsMessageRuleOperations.getNonChannelDestinationNames();
        assertEquals(1,nonChannelDestinationNames.size());
        assertEquals("dest2",nonChannelDestinationNames[0]);
    }

    public void testGetDestinationGroups()
    {
        initializeModels();

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
        }
        
        def destinationGroups=RsMessageRule.getDestinationGroups();
        assertEquals(2,destinationGroups.size());
        assertEquals("Channel",destinationGroups[0].name);
        assertEquals(RsMessageRule.getChannelDestinationNames(),destinationGroups[0].destinationNames);

        assertEquals("Non-Channel",destinationGroups[1].name);
        assertEquals(RsMessageRule.getNonChannelDestinationNames(),destinationGroups[1].destinationNames);


        def normalUser=RsUser.add(username:"normaluser",passwordHash:"aaa");

        def groupsForUser=RsMessageRule.getDestinationGroupsForUser(normalUser.username);
        assertEquals(1,groupsForUser.size());
        assertEquals("Channel",groupsForUser[0].name);
        assertEquals(RsMessageRule.getChannelDestinationNames(),groupsForUser[0].destinationNames);
        

        def adminGroup=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);
        def adminUser=RsUser.addUser(username:"admin",password:"aaa",groups:[adminGroup]);

        def groupsForAdmin=RsMessageRule.getDestinationGroupsForUser(adminUser.username);
        assertEquals(2,groupsForAdmin.size());
        assertEquals("Channel",groupsForAdmin[0].name);
        assertEquals(RsMessageRule.getChannelDestinationNames(),groupsForAdmin[0].destinationNames);

        assertEquals("Non-Channel",destinationGroups[1].name);
        assertEquals(RsMessageRule.getNonChannelDestinationNames(),groupsForAdmin[1].destinationNames);

    }

    public void testGetUserDestinationForChannel()
    {
        initializeModels();



        def user=RsUser.add(username:"testuser",passwordHash:"aaa");
        assertFalse(user.hasErrors());
        user.addChannelInformation(type:"email",destination:"useremail");

        assertEquals("useremail",RsMessageRule.getUserDestinationForChannel(user,"email"));
        assertEquals(null,RsMessageRule.getUserDestinationForChannel(user,null));

        ChannelUserInformation.removeAll();

        assertEquals(null,RsMessageRule.getUserDestinationForChannel(user,"email"));

        //test with default
        user.addChannelInformation(type:"jabber",destination:"userjabber",isDefault:true);

        assertEquals("userjabber",RsMessageRule.getUserDestinationForChannel(user,RsMessageRule.DEFAULT_DESTINATION));
        assertEquals("userjabber",RsMessageRule.getUserDestinationForChannel(user,"jabber"));

    }
    
    public void testValidateUserDestinationForChannelThrowsExceptionIfChannelTypeAndUserDoesNotHaveDestination()
    {
         initializeModels();
        
         def username="testuser";

         def user=RsUser.add(username:username,passwordHash:"aaa");
         assertFalse(user.hasErrors());

         try{
             RsMessageRule.validateUserDestinationForChannel(user,"","email");
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for email is not defined")>=0)
         }

        def group=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);

        def adminUser=RsUser.addUser(username:"adminUser",password:"aaa",groups:[group]);
        assertFalse(adminUser.hasErrors());
        assertTrue(RsUser.hasRole(adminUser.username,Role.ADMINISTRATOR));

         try{
             RsMessageRule.validateUserDestinationForChannel(adminUser,null,"email");
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for email is not defined")>=0)
         }
    }
    public void testValidateUserDestinationForChannelThrowsExceptionIfNonChannelTypeAndUserIsNotAdmin()
    {
        initializeModels();

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
        }


        def username="testuser";

        def group=createGroupWithRole("testgroup",Role.USER);

        def user=RsUser.add(username:username,passwordHash:"aaa",groups:[group]);
        assertFalse(user.hasErrors());

        try{
             RsMessageRule.validateUserDestinationForChannel(user,"","");
             fail("should throw exception");
        }
        catch(e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().indexOf("does not have permission to create rule with Non-Channel destination")>=0)
        }
    }
    public void testValidateUserDestinationDoesNotThrowExceptionOnValidCases()
    {
        initializeModels();

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
        }


        def username="testuser";

        def group=createGroupWithRole("testgroup",Role.USER);

        def user=RsUser.add(username:username,passwordHash:"aaa",groups:[group]);
        assertFalse(user.hasErrors());


        RsMessageRule.validateUserDestinationForChannel(user,"user@com","email");


        def adminGroup=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);

        def adminUser=RsUser.addUser(username:"adminUser",password:"aaa",groups:[adminGroup]);
        assertFalse(adminUser.hasErrors());
        assertTrue(RsUser.hasRole(adminUser.username,Role.ADMINISTRATOR));

        RsMessageRule.validateUserDestinationForChannel(adminUser,"admin@com","email");
        RsMessageRule.validateUserDestinationForChannel(adminUser,"","");


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

         RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);
        
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

        def adminUser=RsUser.addUser(username:"adminUser",password:"aaa",groups:[group]);
        assertFalse(user.hasErrors());
        assertTrue(RsUser.hasRole(adminUser.username,Role.ADMINISTRATOR));

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

    public void testAddMessageRuleForUserThrowsExceptionIfDestinationIsChannelTypeAndUserDoesNotHaveDefaultDestination()
    {
         initializeModels();

         RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);

         def params=[:]
         params.destinationType="email";
         def username="testuser";

         def user=RsUser.add(username:username,passwordHash:"aaa");
         assertFalse(user.hasErrors());

         //test with default with no default channel info
         params.destinationType=RsMessageRule.DEFAULT_DESTINATION;
         try{
             RsMessageRule.addMessageRuleForUser(params,user.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for ${params.destinationType} is not defined")>=0)
         }

         //test with default with empty default channel info
         user.addChannelInformation(type:"email",destination:"",isDefault:true);
         try{
             RsMessageRule.addMessageRuleForUser(params,user.username);
             fail("should throw exception");
         }
         catch(e)
         {
             assertTrue("wrong exception ${e}",e.getMessage().indexOf("destination for ${params.destinationType} is not defined")>=0)
         }

    }
    
    public void testAddMessageAndUpdateRuleForUserThrowsExceptionIfDestinationIsNonChannelAndUserIsNotAdmin()
    {
        initializeModels();

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
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
        RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);
        
        def params=[:]
        params.destinationType="email";
        params.searchQueryId=44;
        params.delay=5;
        params.sendClearEventType=true;
        params.enabled=false;

        def username="testuser";
        def userEmail="testemail";

        def user=RsUser.add(username:username,passwordHash:"aaa");
        assertFalse(user.hasErrors());
        user.addChannelInformation(type:"email",destination:userEmail);


        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());
        

        assertEquals(user.username,messageRule.users);
        assertEquals(params.destinationType,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.sendClearEventType,messageRule.sendClearEventType);
        assertEquals(params.enabled,messageRule.enabled);

        //test update
        def updateParams=[:]
        updateParams.destinationType="email";
        updateParams.searchQueryId=49;
        updateParams.delay=7;
        updateParams.sendClearEventType=false;
        updateParams.enabled=true;

        RsMessageRule.updateMessageRuleForUser(messageRule,updateParams,username);


        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.username,messageRule.users);
        assertEquals(updateParams.destinationType,messageRule.destinationType);
        assertEquals(updateParams.searchQueryId,messageRule.searchQueryId);
        assertEquals(updateParams.delay,messageRule.delay);
        assertEquals(updateParams.sendClearEventType,messageRule.sendClearEventType);
        assertEquals(updateParams.enabled,messageRule.enabled);

    }

    public void testAddMessageRuleForUserWithDefaultDestination()
    {
        initializeModels();
        RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);

        def params=[:]
        params.destinationType=RsMessageRule.DEFAULT_DESTINATION;
        params.searchQueryId=44;
        params.delay=5;
        params.sendClearEventType=true;
        params.enabled=false;

        def username="testuser";

        def user=RsUser.add(username:username,passwordHash:"aaa");
        assertFalse(user.hasErrors());

        //test create with default
        user.addChannelInformation(type:"email",destination:"email1",isDefault:true);
        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.username,messageRule.users);
        assertEquals(RsMessageRule.DEFAULT_DESTINATION,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.sendClearEventType,messageRule.sendClearEventType);
        assertEquals(params.enabled,messageRule.enabled);

    }

    public void testAddMessageRuleForUserWithEmptyUsersAndGroups()
    {
        initializeModels();
        RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);

        def params=[:]
        params.destinationType="email";
        params.searchQueryId=44;
        params.delay=5;
        params.sendClearEventType=true;
        params.enabled=false;
        params.ruleType="public";
        params.users="";
        params.groups="";

        def username="testuser";
        def userEmail="testemail";

        def user=RsUser.add(username:username,passwordHash:"aaa");
        assertFalse(user.hasErrors());
        user.addChannelInformation(type:"email",destination:userEmail);


        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals("_",messageRule.users);
        assertEquals("_",messageRule.groups);
        assertEquals(params.destinationType,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.sendClearEventType,messageRule.sendClearEventType);
        assertEquals(params.enabled,messageRule.enabled);

    }

    public void testAddAndUpdateMessageRuleForUserWithNonChannelDestination()
    {
        initializeModels();

        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"dest2"]
                   ];
        }

        def params=[:]
        params.destinationType="dest2";
        params.searchQueryId=44;
        params.delay=5;
        params.sendClearEventType=true;
        params.enabled=false;

        def username="testuser";
        def userEmail="testemail";

        def group=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);

        def user=RsUser.addUser(username:username,password:"aaa",groups:[group]);
        assertFalse(user.hasErrors());


        def messageRule=RsMessageRule.addMessageRuleForUser(params,username);
        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.username,messageRule.users);
        assertEquals(params.destinationType,messageRule.destinationType);
        assertEquals(params.searchQueryId,messageRule.searchQueryId);
        assertEquals(params.delay,messageRule.delay);
        assertEquals(params.sendClearEventType,messageRule.sendClearEventType);
        assertEquals(params.enabled,messageRule.enabled);


        //test update
        def updateParams=[:]
        updateParams.destinationType="dest2";
        updateParams.searchQueryId=49;
        updateParams.delay=7;
        updateParams.sendClearEventType=false;
        updateParams.enabled=true;

        RsMessageRule.updateMessageRuleForUser(messageRule,updateParams,username);


        assertFalse(messageRule.hasErrors());
        assertEquals(1,RsMessageRule.count());


        assertEquals(user.username,messageRule.users);
        assertEquals(updateParams.destinationType,messageRule.destinationType);
        assertEquals(updateParams.searchQueryId,messageRule.searchQueryId);
        assertEquals(updateParams.delay,messageRule.delay);
        assertEquals(updateParams.sendClearEventType,messageRule.sendClearEventType);
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
         def rule=RsMessageRule.add(searchQueryId:1,username:"testuser",destinationType:"fortest",delay:0);
         assertFalse("Rule has errors ${rule.errors.toString()}",rule.hasErrors());
         return rule;
     }


}
