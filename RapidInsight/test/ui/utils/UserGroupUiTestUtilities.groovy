package utils

import com.thoughtworks.selenium.Selenium
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 14, 2009
* Time: 1:52:49 PM
* To change this template use File | Settings | File Templates.
*/
class UserGroupUiTestUtilities {
    public static void logout(Selenium selenium, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/auth/logout");
        if (validate)
        {
            Assert.assertTrue(selenium.isElementPresent("login"))
            Assert.assertTrue(selenium.isElementPresent("password"))
        }
    }

    public static void login(Selenium selenium, String name, String passWord, String targetUri = "/index/events.gsp", boolean validate = true)
    {
        logout(selenium, validate)
        selenium.openAndWait("/RapidSuite/auth/login${targetUri ? "?targetUri=${targetUri}" : ""}");
        selenium.type("login", name);
        selenium.type("password", passWord);
        selenium.clickAndWait("//input[@value='Sign in']");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite${targetUri} but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite${targetUri}"));
        }
    }


    public static createUser(Selenium selenium, String username, String password, List groups = [], Map otherProps = [:], boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/rsUser/list")
        selenium.clickAndWait("link=Users");
        selenium.clickAndWait("link=New User");
        selenium.type("username", username);
        selenium.type("password1", password);
        selenium.type("password2", password);
        CommonUiTestUtils.addToSelectList(selenium, "availablegroupsSelect", groups);
        selenium.clickAndWait("//input[@value='Create']");
        def userId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "User " + userId + " created")
        }
        return userId;
    }

    public static updateUserById(Selenium selenium, String userId, Map otherProps = [:], List groups = null, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/rsUser/show/${userId}")
        Assert.assertTrue ("User ${userId} does not exist".toString(), selenium.getLocation().indexOf("/rsUser/show") >= 0);
        selenium.clickAndWait("_action_Edit");
        if(groups != null)
        {
            CommonUiTestUtils.clearToSelectList (selenium, "groupsSelect");
            CommonUiTestUtils.addToSelectList(selenium, "availablegroupsSelect", groups);
        }
        CommonUiTestUtils.setAccordingToTypes(selenium, otherProps);
        selenium.clickAndWait("_action_Update");
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "User " + userId + " updated")
        }
        return userId;
    }

    public static deleteUserById(Selenium selenium, String userId, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/rsUser/show/" + userId);
        Assert.assertTrue ("User ${userId} does not exist".toString(), selenium.getLocation().indexOf("/rsUser/show") >= 0);
        selenium.clickAndWait("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/rsUser/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/rsUser/list"));
            CommonUiTestUtils.assertPageMessage (selenium, "User " + userId + " deleted")
        }
    }

    public static deleteUserByUsername(Selenium selenium, String userName, boolean validate = true)
    {
        def userId = CommonUiTestUtils.getIdFromRepository(selenium, "auth.RsUser", "username:${userName}")
        if (userId)
        {
            deleteUserById(selenium, userId, validate)
        }
        else
        {
            if (validate)
            {
                Assert.fail("No users found with name ${userName}");
            }
        }
    }

    public static deleteAllUsers(Selenium selenium, boolean validate = true)
    {
        def users = CommonUiTestUtils.search(selenium, "auth.RsUser", "alias:* NOT username:rsadmin")
        users.each {
            selenium.deleteUserById(it.id, validate);
        }
    }



    public static createGroup(Selenium selenium, String name, String role, List users, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/group/list")
        selenium.clickAndWait("link=Groups");
        selenium.clickAndWait("link=New Group");
        selenium.type("name", name);
        if(role)
        selenium.select("role.id", "label=${role}");
        CommonUiTestUtils.addToSelectList(selenium, "availableusersSelect", users);
        selenium.clickAndWait("//input[@value='Create']");
        def grId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Group " + grId + " created")
        }
        return grId;
    }

    public static updateGroupByName(Selenium selenium, String groupName, Map updatedProps, List groupUsers, boolean validate = true)
    {
        def groupId = CommonUiTestUtils.getIdFromRepository(selenium, "auth.Group", "name:${groupName}")
        if (groupId)
        {
            deleteGroupById(selenium, groupId, validate)
        }
        else
        {
            if (validate)
            {
                Assert.fail("No groups found with name ${groupName}");
            }
        }
    }
    public static updateGroupById(Selenium selenium, String groupId, Map updatedProps, List groupUsers = null, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/group/show/${groupId}")
        Assert.assertTrue ("Group ${groupId} does not exist".toString(), selenium.getLocation().indexOf("/group/show") >= 0);
        selenium.clickAndWait("_action_Edit");
        CommonUiTestUtils.setAccordingToTypes (selenium, updatedProps)
        if(groupUsers != null)
        {
            CommonUiTestUtils.clearToSelectList(selenium, "usersSelect");
            CommonUiTestUtils.addToSelectList(selenium, "availableusersSelect", groupUsers);
        }
        selenium.clickAndWait("_action_Update");
        def grId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "Group " + grId + " updated")
        }
        return grId;
    }
    public static deleteGroupById(Selenium selenium, String groupId, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/group/show/" + groupId);
        Assert.assertTrue ("Group ${groupId} does not exist".toString(), selenium.getLocation().indexOf("/group/show") >= 0);
        selenium.clickAndWait("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/group/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/group/list"));
            CommonUiTestUtils.assertPageMessage (selenium, "Group " + groupId + " deleted")
        }
    }


    public static deleteGroupByName(Selenium selenium, String groupName, boolean validate = true)
    {
        def groupId = CommonUiTestUtils.getIdFromRepository(selenium, "auth.Group", "name:${groupName}")
        if (groupId)
        {
            deleteGroupById(selenium, groupId, validate)
        }
        else
        {
            if (validate)
            {
                Assert.fail("No groups found with name ${groupName}");
            }
        }
    }

    public static deleteAllGroups(Selenium selenium, boolean validate = true)
    {
        def groups = CommonUiTestUtils.search(selenium, "auth.Group", "alias:* NOT name:rsadmin")
        groups.each {
            selenium.deleteGroupById(it.id, validate);
        }
    }
}


