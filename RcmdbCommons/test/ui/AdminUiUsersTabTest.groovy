import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 22, 2009
* Time: 2:38:22 AM
* To change this template use File | Settings | File Templates.
*/
class AdminUiUsersTabTest extends SeleniumTestCase
{

    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/",
                SeleniumTestUtils.getSeleniumBrowser());
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        // logout()
    }


    private void logout()
    {
        selenium.click("link=Logout");
    }

    private void login()
    {
        selenium.open("/RapidSuite/auth/login?targetUri=%2Fadmin.gsp&format=html");
        selenium.waitForPageToLoad("30000");
        selenium.type("login", "rsadmin");
        selenium.type("password", "changeme");
        selenium.click("//input[@value='Sign in']");
        selenium.waitForPageToLoad("30000");
    }



    private String createNewGroup(String name)
    {
        selenium.click("link=Groups");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Group");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", name);
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        return selenium.getText("document.getElementById('id')")
    }

    private void deleteGroup(String groupId)
    {
        selenium.open("/RapidSuite/group/show/" + groupId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
    }

    private void deleteUser(String userId)
    {
        selenium.open("/RapidSuite/rsUser/show/" + userId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
    }

    public String userId()
    {
        def line = selenium.getLocation()
        def splitted = new String[3]
        splitted = line.split("RapidSuite/rsUser/show/")
        return splitted[1]
    }


    public String groupId()
    {
        return selenium.getText("document.getElementById('id')")
    }


    public void newGroup()
    {
        selenium.click("link=Groups");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Group");
        selenium.waitForPageToLoad("30000");
    }

    public void newUser()
    {
        selenium.click("link=Users");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New User");
        selenium.waitForPageToLoad("30000");
    }



    public void testCreateANewUser()
    {
        login()

        def firstGroupsId = createNewGroup("nmd1Group");
        assertEquals("Group " + firstGroupsId + " created", selenium.getText("pageMessage"))
        def secondGroupsId = createNewGroup("nmd2Group");
        assertEquals("Group " + secondGroupsId + " created", selenium.getText("pageMessage"))

        newUser()
        selenium.type("username", "nmd1User");
        selenium.type("password1", "pass");
        selenium.type("password2", "pass");
        selenium.addSelection("availablegroupsSelect", "label=nmd1Group");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def firstUsersId = userId()
        assertEquals("User " + firstUsersId + " created", selenium.getText("pageMessage"))

        newUser()
        selenium.type("username", "nmd2User");
        selenium.type("password1", "123");
        selenium.type("password2", "123");
        selenium.addSelection("availablegroupsSelect", "label=nmd2Group");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def secondUsersId = userId()
        assertEquals("User " + secondUsersId + " created", selenium.getText("pageMessage"))

        deleteGroup(firstGroupsId);
        deleteGroup(secondGroupsId);
        deleteUser(firstUsersId);
        deleteUser(secondUsersId);

    }



    public void testCreateANewUserCreateWithAnExistingUsername()
    {
        login()
        newGroup()
        selenium.type("name", "nmd1Group");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def groupsId = groupId()

        newUser()
        selenium.type("username", "nmd1User");
        selenium.addSelection("availablegroupsSelect", "label=nmd1Group");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def usersId = userId()

        newUser()
        selenium.type("username", "nmd1User");
        selenium.addSelection("availablegroupsSelect", "label=nmd1Group");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Object with entered keys already exists"));

        deleteGroup(groupsId)
        deleteUser(usersId)

    }


    public void testUpdateUserInfo()
    {
        login()
        newGroup()
        selenium.type("name", "nmd1Group");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def groupsId = groupId()

        newUser()
        selenium.type("username", "nmd1User");
        selenium.type("password1", "pass");
        selenium.type("password2", "pass");
        selenium.addSelection("availablegroupsSelect", "label=nmd1Group");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def usersId = userId()


        selenium.click("link=Users");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=nmd1User");
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.type("password1", "123");
        selenium.type("password2", "123");
        selenium.type("email", "nmd1@ifountain.com");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");
        assertEquals("User " + usersId + " updated", selenium.getText("pageMessage"))

        deleteGroup(groupsId)
        deleteUser(usersId)

    }

}