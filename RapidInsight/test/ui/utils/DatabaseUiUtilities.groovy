package utils

import com.thoughtworks.selenium.Selenium
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 3, 2009
* Time: 5:00:23 PM
* To change this template use File | Settings | File Templates.
*/
class DatabaseUiUtilities {
    public static deleteDatabaseConnectionById(Selenium selenium, String dbConnectionId, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/databaseConnection/show/" + dbConnectionId);
        Assert.assertTrue("Database connection ${dbConnectionId} does not exist".toString(), selenium.getLocation().indexOf("/databaseConnection/show") >= 0);
        selenium.clickAndWait("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/databaseConnection/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/databaseConnection/list"));
            CommonUiTestUtils.assertPageMessage(selenium, "DatabaseConnection " + dbConnectionId + " deleted")
        }
    }

    public static deleteAllDatabaseConnections(Selenium selenium, boolean validate = true)
    {
        def dbConns = CommonUiTestUtils.search(selenium, "connection.DatabaseConnection", "alias:*")
        dbConns.each {
            deleteDatabaseConnectionById(selenium, it.id, validate);
        }
    }
     public static createDatabaseConnection(Selenium selenium, String name, String driver, String url, String username, String password, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/databaseConnection/list")
        selenium.clickAndWait("link=New Database Connection");
        selenium.type("name", name);
        selenium.type("driver", driver);
        selenium.type("url", url);
        selenium.type("username", username);
        selenium.type("userPassword", password);
        selenium.type("minTimeout", "20");
        selenium.clickAndWait("//input[@value='Create']");

        def dbId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage (selenium, "DatabaseConnection " + dbId + " created")
        }
        return dbId;
    }
}