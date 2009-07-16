import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import junit.framework.AssertionFailedError

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
        selenium.login("rsadmin", "changeme");        
        selenium.deleteAllUsers();
        selenium.deleteAllGroups();
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        // logout()
    }



    public void testCreateANewUser()
    {
        selenium.login("rsadmin", "changeme")

        def firstGroupsId = selenium.createGroup("nmd1Group", "User", [], true);
        def secondGroupsId = selenium.createGroup("nmd2Group", "User", [], true);
        def firstUsersId = selenium.createUser("nmd1User", "pass", ["nmd1Group"], [:], true);
        def secondUsersId = selenium.createUser("nmd2User", "123", ["nmd2Group"], [:], true);
        selenium.login("nmd1User", "pass")
        selenium.login("nmd2User", "123")
    }



    public void testCreateANewUserCreateWithAnExistingUsername()
    {
        def groupId = selenium.createGroup("nmd1Group", null, [], true);
        def userId = selenium.createUser("nmd1User", "pass", ["nmd1Group"], [:], true);
        selenium.createUser("nmd1User", "pass", ["nmd1Group"], [:], false);
        assertTrue(selenium.isTextPresent("Object with entered keys already exists"));
    }


    public void testUpdateUserInfo()
    {
        selenium.login("rsadmin", "changeme")
        def groupId = selenium.createGroup("nmd1Group", "User", [], true);
        def userId = selenium.createUser("nmd1User", "pass", ["nmd1Group"], [:], true);
        selenium.login("nmd1User", "pass");
        selenium.login("rsadmin", "changeme");
        def updatedFields =  [password1:"123", password2:"123", email:"nmd1@ifountain.com"]
        selenium.updateUserById(userId,updatedFields);
        try{
            selenium.login("nmd1User", "pass");
            fail("Should throw exception since");
        }catch(AssertionFailedError e)
        {
            assertTrue (e.getMessage().indexOf("Expected to end with") >= 0);
        }
        selenium.login("nmd1User", "123");
        selenium.login("rsadmin", "changeme");
        selenium.openAndWait("/RapidSuite/rsUser/show/${userId}");
        selenium.clickAndWait("_action_Edit");
        assertEquals(updatedFields.email, selenium.getValue("email"));

    }

}