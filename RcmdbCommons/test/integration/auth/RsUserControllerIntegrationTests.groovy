package auth

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 20, 2009
* Time: 6:07:09 PM
* To change this template use File | Settings | File Templates.
*/
class RsUserControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;

    def adminGroupId;
    def userGroupId;
    def testUsername = "testuser";
    def RsMessageRule;

    public void setUp() {
        super.setUp();
        adminGroupId = Group.get(name: RsUser.RSADMIN).id
        userGroupId = Group.get(name: RsUser.RSUSER).id
        RsUser.get(username: testUsername)?.remove();
        RsUser.get(username: "${testUsername}2")?.remove();
        ChannelUserInformation.removeAll();
        RsMessageRule = ApplicationHolder.application.classLoader.loadClass("message.RsMessageRule");
        RsMessageRule.setConfiguredDestinationNames(["email"]);
    }

    public void tearDown() {
        super.tearDown();
        RsUser.get(username: testUsername)?.remove();
        RsUser.get(username: "${testUsername}2")?.remove();
        RsMessageRule.setConfiguredDestinationNames([]);
    }

    public void testAddUserSuccessfully()
    {
        def controller = new RsUserController();
        controller.params["username"] = testUsername;
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = adminGroupId.toString();

        controller.save();

        def rsUser = RsUser.get(username: testUsername);
        assertEquals("User ${rsUser.id} created", controller.flash.message);

        assertEquals("/rsUser/show/${rsUser.id}", controller.response.redirectedUrl);

        def userGroups = rsUser.groups;
        assertEquals(1, userGroups.size());
        assertEquals(adminGroupId, userGroups[0].id);

        def userInformations = rsUser.userInformations;
        assertEquals(1, userInformations.size());
        assertEquals("email", userInformations[0].type);
        assertEquals("useremail", userInformations[0].destination);
        assertTrue(userInformations[0].isDefault);

        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(1, ChannelUserInformation.countHits("type:email"));
    }

    public void testAddUserDoesNotAddUserWhenPasswordsAreNotSame()
    {
        def controller = new RsUserController();
        controller.params["username"] = testUsername;
        controller.params["password1"] = "123";
        controller.params["password2"] = "124";
        controller.params["email"] = "useremail";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = adminGroupId.toString();


        controller.save();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("default.passwords.dont.match", controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals(null, controller.modelAndView.model.rsUser.id);
        assertEquals(testUsername, controller.modelAndView.model.rsUser.username);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.availableGroups[0].id)

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(null, controller.modelAndView.model.userChannels[0].id);

        assertEquals("email", controller.modelAndView.model.defaultDestination);       
        assertEquals(0, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, ChannelUserInformation.count());

    }
    public void testAddUserDoesNotAddUserWhenDefaultDestinationIsNotGiven()
    {
        def controller = new RsUserController();
        controller.params["username"] = testUsername;
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = adminGroupId.toString();


        controller.save();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("default.custom.error", controller.flash.errors.getAllErrors()[0].code);
        assertEquals("Default destination 'email' is not provided", controller.flash.errors.getAllErrors()[0].arguments[0]);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals(null, controller.modelAndView.model.rsUser.id);
        assertEquals(testUsername, controller.modelAndView.model.rsUser.username);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.availableGroups[0].id)

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(null, controller.modelAndView.model.userChannels[0].id);

        assertEquals("email", controller.modelAndView.model.defaultDestination);        
        assertEquals(0, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, ChannelUserInformation.count());

    }

    public void testAddUserDoesNotAddUserWhenExceptionOccurs()
    {
        def controller = new RsUserController();
        controller.params["username"] = testUsername;
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail";
        controller.params["defaultDestination"] = "email";


        controller.save();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("no.group.specified", controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals(null, controller.modelAndView.model.rsUser.id);
        assertEquals(testUsername, controller.modelAndView.model.rsUser.username);

        assertEquals(null, controller.modelAndView.model.userGroups)


        assertEquals(2, controller.modelAndView.model.availableGroups.size())
        assertNotNull(controller.modelAndView.model.availableGroups.find {it.id == adminGroupId})
        assertNotNull(controller.modelAndView.model.availableGroups.find {it.id == userGroupId})



        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(null, controller.modelAndView.model.userChannels[0].id);

        assertEquals("email", controller.modelAndView.model.defaultDestination);
        assertEquals(0, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, ChannelUserInformation.count());
    }

    public void testAddUserDoesNotAddUserWhenRsUserHaveErrors()
    {
        def existingUser = RsUser.add(username: testUsername, passwordHash: "asd");
        assertFalse(existingUser.hasErrors());


        def controller = new RsUserController();
        controller.params["username"] = testUsername;
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = adminGroupId.toString();


        controller.save();

        assertNull(controller.flash.errors);

        assertTrue(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals(testUsername, controller.modelAndView.model.rsUser.username);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.availableGroups[0].id)


        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(null, controller.modelAndView.model.userChannels[0].id);

        def existingUserFromRepo = RsUser.get(username: testUsername);
        assertEquals(0, existingUserFromRepo.groups.size());

        assertEquals("email", controller.modelAndView.model.defaultDestination);
        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, ChannelUserInformation.count());
    }

    public void testAddUserRollsBackIfUserChannelInformationsHaveError()
    {
        //          RsUser.metaClass.'static'.getChannelTypes= { ->
        //            return ["email"];
        //          }

        def defaultChannelTypeList = RsUser.getChannelTypes().clone();
        assertEquals("defaultChannelTypeList can not contain null type, possibly another test modified metaclass and didnt roll back", 0, defaultChannelTypeList.findAll {it == null}.size())

        RsUser.metaClass.'static'.getChannelTypes = {->
            return ["email", null];
        }

        assertEquals(["email", null], RsUser.getChannelTypes());

        try {
            def controller = new RsUserController();
            controller.params["username"] = testUsername;
            controller.params["password1"] = "123";
            controller.params["password2"] = "123";
            controller.params["email"] = "useremail";
            controller.params["defaultDestination"] = "email";
            controller.params["groups.id"] = adminGroupId.toString();


            controller.save();

            assertNull(controller.flash.errors)


            assertFalse(controller.modelAndView.model.rsUser.hasErrors());
            assertEquals(testUsername, controller.modelAndView.model.rsUser.username);

            assertEquals(1, controller.modelAndView.model.userGroups.size())
            assertEquals(adminGroupId, controller.modelAndView.model.userGroups[0].id)

            assertEquals(1, controller.modelAndView.model.availableGroups.size())
            assertEquals(userGroupId, controller.modelAndView.model.availableGroups[0].id)


            assertEquals(2, controller.modelAndView.model.userChannels.size());
            println controller.modelAndView.model.userChannels
            assertEquals("email", controller.modelAndView.model.userChannels[0].type);
            assertEquals("useremail", controller.modelAndView.model.userChannels[0].destination);
            assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
            assertNotNull(controller.modelAndView.model.userChannels[0].id);

            //type null can not be added
            assertTrue(controller.modelAndView.model.userChannels[1].hasErrors());
            assertNull(controller.modelAndView.model.userChannels[1].id);

            assertEquals("email", controller.modelAndView.model.defaultDestination);
            assertEquals(0, RsUser.countHits("username:${testUsername}"));
            assertEquals(0, ChannelUserInformation.count());
        }
        finally {
            RsUser.metaClass.'static'.getChannelTypes = {->
                return defaultChannelTypeList;
            }
            assertEquals(defaultChannelTypeList, RsUser.getChannelTypes());
        }
    }


    private def addTestUser()
    {
        def params = [:];
        params.username = testUsername;
        params.groups = [Group.get(id: adminGroupId)];
        params.password = "abc";
        def user = RsUser.addUser(params);

        assertFalse(user.hasErrors());
        assertEquals(1, user.groups.size());

        user.addChannelInformationsAndRollBackIfErrorOccurs([[type: "email", destination: "testemail"]])

        assertEquals(1, user.userInformations.size());

        return user;
    }
    public void testUpdateUserGeneratesErrorMessageWhenUserNotFound()
    {
        def controller = new RsUserController();
        //we assign a string to id so controller can not find user
        def userId = "nouser"
        controller.params["id"] = userId;

        controller.update();

        assertEquals("User not found with id ${userId}", controller.flash.message);
        assertEquals("/rsUser/edit/${userId}", controller.response.redirectedUrl);

        assertEquals(0, ChannelUserInformation.count());
    }

    public void testUpdateUserDoesNotUpdateUserWhenPasswordsAreNotSame()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans 
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = "${testUsername}2";
        controller.params["password1"] = "123";
        controller.params["password2"] = "124";
        controller.params["email"] = "useremail2";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = userGroupId.toString();


        controller.update();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("default.passwords.dont.match", controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals("${testUsername}2", controller.modelAndView.model.rsUser.username);
        assertEquals(rsUser.id, controller.modelAndView.model.rsUser.id);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.availableGroups[0].id)

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail2", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(emailInformation.id, controller.modelAndView.model.userChannels[0].id);
        assertEquals("email", controller.modelAndView.model.defaultDestination);

        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, RsUser.countHits("username:${testUsername}2"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));
    }
     public void testUpdateUserDoesNotUpdateUserWhenDefaultDestinationIsNotGiven()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = "${testUsername}2";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = userGroupId.toString();


        controller.update();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("default.custom.error", controller.flash.errors.getAllErrors()[0].code);
        assertEquals("Default destination 'email' is not provided", controller.flash.errors.getAllErrors()[0].arguments[0]);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals("${testUsername}2", controller.modelAndView.model.rsUser.username);
        assertEquals(rsUser.id, controller.modelAndView.model.rsUser.id);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.availableGroups[0].id)

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(emailInformation.id, controller.modelAndView.model.userChannels[0].id);
        assertEquals("email", controller.modelAndView.model.defaultDestination);

        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, RsUser.countHits("username:${testUsername}2"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

    }

    public void testUpdateUserDoesNotUpdateUserWhenExceptionOccurs()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = "${testUsername}2";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail2";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = "";

        controller.update();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("no.group.specified", controller.flash.errors.getAllErrors()[0].code);

        assertFalse(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals("${testUsername}2", controller.modelAndView.model.rsUser.username);
        assertEquals(rsUser.id, controller.modelAndView.model.rsUser.id);

        assertEquals(0, controller.modelAndView.model.userGroups.size())

        assertEquals(2, controller.modelAndView.model.availableGroups.size())
        assertNotNull(controller.modelAndView.model.availableGroups.find {it.id == adminGroupId})
        assertNotNull(controller.modelAndView.model.availableGroups.find {it.id == userGroupId})

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail2", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(emailInformation.id, controller.modelAndView.model.userChannels[0].id);
        assertEquals("email", controller.modelAndView.model.defaultDestination);
        
        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, RsUser.countHits("username:${testUsername}2"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));
    }

    public void testUpdateUserDoesNotUpdateUserWhenRsUserHaveErrors()
    {

        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = null;
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail2";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = userGroupId.toString();

        controller.update();

        assertNull(controller.flash.errors);


        assertTrue(controller.modelAndView.model.rsUser.hasErrors());
        assertEquals("", controller.modelAndView.model.rsUser.username);
        assertEquals(rsUser.id, controller.modelAndView.model.rsUser.id);

        assertEquals(1, controller.modelAndView.model.userGroups.size())
        assertEquals(userGroupId, controller.modelAndView.model.userGroups[0].id)

        assertEquals(1, controller.modelAndView.model.availableGroups.size())
        assertEquals(adminGroupId, controller.modelAndView.model.availableGroups[0].id)

        assertEquals(1, controller.modelAndView.model.userChannels.size());
        assertEquals("email", controller.modelAndView.model.userChannels[0].type);
        assertEquals("useremail2", controller.modelAndView.model.userChannels[0].destination);
        assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
        assertEquals(emailInformation.id, controller.modelAndView.model.userChannels[0].id);
        assertEquals("email", controller.modelAndView.model.defaultDestination);

        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(0, RsUser.countHits("username:${testUsername}2"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

    }

    public void testUpdateUserRollsBackIfUserChannelInformationsHaveError()
    {
        //          RsUser.metaClass.'static'.getChannelTypes= { ->
        //            return ["email"];
        //          }

        def defaultChannelTypeList = RsUser.getChannelTypes().clone();
        assertEquals("defaultChannelTypeList can not contain null type, possibly another test modified metaclass and didnt roll back", 0, defaultChannelTypeList.findAll {it == null}.size())

        RsUser.metaClass.'static'.getChannelTypes = {->
            return ["email", null];
        }

        assertEquals(["email", null], RsUser.getChannelTypes());

        try {
            def rsUser = addTestUser();
            def emailInformation = rsUser.userInformations[0];

            //update params are different than add params to check beans
            def controller = new RsUserController();
            controller.params["id"] = rsUser.id.toString();
            controller.params["username"] = "${testUsername}2";
            controller.params["password1"] = "123";
            controller.params["password2"] = "123";
            controller.params["email"] = "useremail2";
            controller.params["defaultDestination"] = "email";
            controller.params["groups.id"] = userGroupId.toString();

            controller.update();

            assertNull(controller.flash.errors);

            assertFalse(controller.modelAndView.model.rsUser.hasErrors());
            assertEquals("${testUsername}", controller.modelAndView.model.rsUser.username);
            assertEquals(rsUser.id, controller.modelAndView.model.rsUser.id);

            assertEquals(1, controller.modelAndView.model.userGroups.size())
            assertEquals(userGroupId, controller.modelAndView.model.userGroups[0].id)

            assertEquals(1, controller.modelAndView.model.availableGroups.size())
            assertEquals(adminGroupId, controller.modelAndView.model.availableGroups[0].id)

            assertEquals(2, controller.modelAndView.model.userChannels.size());
            assertEquals("email", controller.modelAndView.model.userChannels[0].type);
            assertEquals("testemail", controller.modelAndView.model.userChannels[0].destination);
            assertFalse(controller.modelAndView.model.userChannels[0].hasErrors());
            assertEquals(emailInformation.id, controller.modelAndView.model.userChannels[0].id);
            assertEquals("email", controller.modelAndView.model.defaultDestination);

            //type null can not be added
            assertTrue(controller.modelAndView.model.userChannels[1].hasErrors());
            assertNull(controller.modelAndView.model.userChannels[1].id);

            assertEquals(1, RsUser.countHits("username:${testUsername}"));
            assertEquals(0, RsUser.countHits("username:${testUsername}2"));
            assertEquals(1, ChannelUserInformation.count());
            assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

        }
        finally {
            RsUser.metaClass.'static'.getChannelTypes = {->
                return defaultChannelTypeList;
            }
            assertEquals(defaultChannelTypeList, RsUser.getChannelTypes());
        }
    }

    public void testUpdateUserSuccessfully()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = "${testUsername}2";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["email"] = "useremail2";
        controller.params["defaultDestination"] = "email";
        controller.params["groups.id"] = userGroupId.toString();

        controller.update();


        def rsUserUpdated = RsUser.get(id: rsUser.id);
        assertEquals("User ${rsUserUpdated.id} updated", controller.flash.message);
        assertEquals("/rsUser/show/${rsUserUpdated.id}", controller.response.redirectedUrl);

        assertTrue(rsUserUpdated.isPasswordSame("123"))

        assertEquals(1, RsUser.countHits("username:${testUsername}2"));

        def userGroups = rsUserUpdated.groups;
        assertEquals(1, userGroups.size());
        assertEquals(userGroupId, userGroups[0].id);

        def userInformations = rsUser.userInformations;
        assertEquals(1, userInformations.size());
        assertEquals("email", userInformations[0].type);
        assertEquals("useremail2", userInformations[0].destination);
        assertTrue(userInformations[0].isDefault);

        assertEquals(1, ChannelUserInformation.countHits("type:email"));
    }
    public void testUpdateUserDoesNotUpdatePasswordWhenPasswordIsEmpty()
    {
        def rsUser = addTestUser();

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.params["username"] = "${testUsername}2";
        controller.params["password1"] = "";
        controller.params["password2"] = "";        
        controller.params["groups.id"] = userGroupId.toString();
        controller.update();

        def rsUserUpdated = RsUser.get(id: rsUser.id);
        assertEquals("User ${rsUserUpdated.id} updated", controller.flash.message);

        assertEquals("/rsUser/show/${rsUserUpdated.id}", controller.response.redirectedUrl);

        assertTrue(rsUserUpdated.isPasswordSame("abc"))

        assertEquals(1, RsUser.countHits("username:${testUsername}2"));

    }

    public void testChangeProfileGeneratesErrorMessageWhenUserNotFound()
    {
        def controller = new RsUserController();
        //we assign a string to id so controller can not find user
        def username = "nouser"
        controller.params["username"] = username;

        controller.changeProfile();

        def response = controller.response.getContentAsString();
        def responseXml = new XmlSlurper().parseText(response);

        println response

        assertEquals(1, responseXml.Error.size());
        assertTrue(responseXml.Error[0].@"error".toString().indexOf("[auth.RsUser] not found with id [${username}]") >= 0);

        assertEquals(0, ChannelUserInformation.count());
    }
    public void testChangeProfileDoesNotUpdateUserWhenPasswordsAreNotSame()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["username"] = "${testUsername}";
        controller.params["password1"] = "123";
        controller.params["password2"] = "124";
        controller.params["email"] = "useremail2";



        controller.changeProfile();

        def response = controller.response.getContentAsString();
        def responseXml = new XmlSlurper().parseText(response);

        println response

        assertEquals(1, responseXml.Error.size());
        assertTrue(responseXml.Error[0].@"error".toString().indexOf("Passwords don't match") >= 0);


        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

    }

    public void testChangeProfileDoesNotUpdateUserWhenOldPasswordIsWrong()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["username"] = "${testUsername}";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["oldPassword"] = "nosuchpass";
        controller.params["email"] = "useremail2";



        controller.changeProfile();

        def response = controller.response.getContentAsString();
        def responseXml = new XmlSlurper().parseText(response);

        println response

        assertEquals(1, responseXml.Error.size());
        assertTrue(responseXml.Error[0].@"error".toString().indexOf("Old Password doesn't match") >= 0);


        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

    }

    public void testChangeProfileDoesNotUpdateUserWhenDefaultDestinationIsNotGiven()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["username"] = "${testUsername}";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["oldPassword"] = "abc";
        controller.params["email"] = "";
        controller.params["defaultDestination"] = "email";

        controller.changeProfile();

        def response = controller.response.getContentAsString();
        def responseXml = new XmlSlurper().parseText(response);

        assertEquals(1, responseXml.Error.size());
        assertTrue(responseXml.Error[0].@"error".toString().indexOf("Default destination 'email' is not provided") >= 0);
        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(1, ChannelUserInformation.count());
        assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));
    }


    public void testChangeProfileRollsBackIfUserChannelInformationsHaveError()
    {
        //          RsUser.metaClass.'static'.getEditableChannelTypes= { ->
        //            return ["email"];
        //          }

        def defaultChannelTypeList = RsUser.getEditableChannelTypes().clone();
        assertEquals("defaultChannelTypeList can not contain null type, possibly another test modified metaclass and didnt roll back", 0, defaultChannelTypeList.findAll {it == null}.size())

        RsUser.metaClass.'static'.getEditableChannelTypes = {->
            return ["email", null];
        }

        assertEquals(["email", null], RsUser.getEditableChannelTypes());

        try {
            def rsUser = addTestUser();
            def emailInformation = rsUser.userInformations[0];

            //update params are different than add params to check beans
            //update params are different than add params to check beans
            def controller = new RsUserController();
            controller.params["username"] = "${testUsername}";
            controller.params["password1"] = "123";
            controller.params["password2"] = "123";
            controller.params["oldPassword"] = "abc";
            controller.params["email"] = "useremail2";
            controller.params["groups.id"] = userGroupId.toString();

            controller.changeProfile();

            def response = controller.response.getContentAsString();
            def responseXml = new XmlSlurper().parseText(response);

            println response

            assertEquals(2, responseXml.Error.size());
            assertTrue(responseXml.Error[0].@"error".toString().indexOf("Property [type] of class [class auth.ChannelUserInformation] cannot be null") >= 0);
            assertEquals("type", responseXml.Error[0].@"field".toString())
            assertTrue(responseXml.Error[1].@"error".toString().indexOf("Property [type] of class [class auth.ChannelUserInformation] cannot be null") >= 0);
            assertEquals("userId", responseXml.Error[1].@"field".toString())

            assertEquals(1, RsUser.countHits("username:${testUsername}"));
            assertEquals(1, ChannelUserInformation.count());
            assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

            assertEquals(1, RsUser.countHits("username:${testUsername}"));
            assertEquals(0, RsUser.countHits("username:${testUsername}2"));
            assertEquals(1, ChannelUserInformation.count());
            assertEquals(1, ChannelUserInformation.countHits("type:${emailInformation.type} AND destination:${emailInformation.destination}"));

        }
        finally {
            RsUser.metaClass.'static'.getEditableChannelTypes = {->
                return defaultChannelTypeList;
            }
            assertEquals(defaultChannelTypeList, RsUser.getEditableChannelTypes());
        }
    }



    public void testChangeProfileSuccessfully()
    {
        def rsUser = addTestUser();
        def emailInformation = rsUser.userInformations[0];

        //update params are different than add params to check beans
        def controller = new RsUserController();
        controller.params["username"] = "${testUsername}";
        controller.params["password1"] = "123";
        controller.params["password2"] = "123";
        controller.params["oldPassword"] = "abc";
        controller.params["email"] = "useremail2";
        controller.params["defaultDestination"] = "email";
        controller.changeProfile();

        def response = controller.response.getContentAsString();
        def responseXml = new XmlSlurper().parseText(response);

        println response


        assertEquals(0, responseXml.Error.size());
        assertEquals("Profile changed.", responseXml.text());
        assertEquals("Successful", responseXml.name());
        assertEquals(0, responseXml.children().size());


        def rsUserUpdated = RsUser.get(id: rsUser.id);

        def userGroups = rsUserUpdated.groups;
        assertEquals(1, userGroups.size());
        assertEquals(adminGroupId, userGroups[0].id);

        def userInformations = rsUser.userInformations;
        assertEquals(1, userInformations.size());
        assertEquals("email", userInformations[0].type);
        assertEquals("useremail2", userInformations[0].destination);
        assertTrue(userInformations[0].isDefault);

        assertEquals(1, RsUser.countHits("username:${testUsername}"));
        assertEquals(1, ChannelUserInformation.count());

    }

    public void testDeleteUserGeneratesErrorMessageWhenUserNotFound()
    {
        def controller = new RsUserController();
        //we assign a string to id so controller can not find user
        def userId = "nouser"
        controller.params["id"] = userId;

        controller.delete();

        assertEquals("User not found with id ${userId}", controller.flash.message);
        assertEquals("/rsUser/list", controller.response.redirectedUrl);
    }
    public void testDeleteUserGeneratesErrorWhenRemoveGeneratesError()
    {
        def adminUser = RsUser.get(username: RsUser.RSADMIN);

        def controller = new RsUserController();
        controller.params["id"] = adminUser.id.toString();
        controller.delete();

        assertEquals(1, controller.flash.errors.getAllErrors().size());
        assertEquals("default.custom.error", controller.flash.errors.getAllErrors()[0].code);

        assertEquals("/rsUser/list", controller.response.redirectedUrl);

        assertEquals(1, RsUser.countHits("username:${RsUser.RSADMIN}"))
    }
    public void testDeleteUserSuccessfully()
    {
        def rsUser = addTestUser();

        assertEquals(1, RsUser.countHits("username:${rsUser.username}"));
        assertEquals(1, ChannelUserInformation.count());

        def controller = new RsUserController();
        controller.params["id"] = rsUser.id.toString();
        controller.delete();

        assertEquals("User ${rsUser.id} deleted", controller.flash.message);
        assertEquals("/rsUser/list", controller.response.redirectedUrl);

        assertEquals(0, RsUser.countHits("username:${rsUser.username}"));
        assertEquals(0, ChannelUserInformation.count());
    }

}