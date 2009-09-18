import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import utils.CommonUiTestUtils
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 3, 2009
* Time: 5:09:13 PM
* To change this template use File | Settings | File Templates.
*/
class AdminUiDatabaseTabTest extends SeleniumTestCase {
    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite",
                SeleniumTestUtils.getSeleniumBrowser());
        selenium.logout()
        selenium.login("rsadmin", "changeme");
        selenium.deleteAllDatabaseConnections();
    }

    public void testCreateADatabaseConnectionWithTestConnectionButton()
    {
        def driver = CommonTestUtils.getTestProperty("MYSQL.DatabaseDriver")
        def url = CommonTestUtils.getTestProperty("MYSQL.DatabaseUrl")
        def username = CommonTestUtils.getTestProperty("MYSQL.DatabaseUsername")
        def password = CommonTestUtils.getTestProperty("MYSQL.DatabasePassword")
        def connName = "dbConn1";
        selenium.createDatabaseConnection(connName, driver, url, username, password, true);
        selenium.clickAndWait("link=DatabaseConnection List");
        selenium.clickAndWait("link=Test Connection");
        CommonUiTestUtils.assertPageMessage (selenium, "Successfully connected to server.")
    }

    public void testCreateADatabaseConnectionWithInvalidConnectionParameters()
    {
        def driver = CommonTestUtils.getTestProperty("MYSQL.DatabaseDriver")
        def url = CommonTestUtils.getTestProperty("MYSQL.DatabaseUrl")
        def username = CommonTestUtils.getTestProperty("MYSQL.DatabaseUsername")
        def password = CommonTestUtils.getTestProperty("MYSQL.DatabasePassword")
        def connName = "dbConn1";
        selenium.createDatabaseConnection(connName, driver, "invalidUrl", username, password, true);
        selenium.clickAndWait("link=DatabaseConnection List");
        selenium.clickAndWait("link=Test Connection");
        assertEquals("Cannot connect to [dbConn1]. Reason: [java.sql.SQLException: No suitable driver found for invalidUrl]", CommonUiTestUtils.getPageErrorMessage(selenium))
    }
}