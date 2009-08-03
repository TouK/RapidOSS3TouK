import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.DatabaseTestConstants
import com.ifountain.rcmdb.test.util.DatabaseConnectionImplTestUtils
import com.ifountain.rcmdb.test.util.DatabaseConnectionParams
import utils.CommonUiTestUtils

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
        DatabaseConnectionParams connParams = DatabaseConnectionImplTestUtils.getConnectionParams(DatabaseTestConstants.MYSQL)
        def connName = "dbConn1";
        selenium.createDatabaseConnection(connName, connParams.getDriver(), connParams.getUrl(), connParams.getUsername(), connParams.getPassword(), true);
        selenium.clickAndWait("link=DatabaseConnection List");
        selenium.clickAndWait("link=Test Connection");
        CommonUiTestUtils.assertPageMessage (selenium, "Successfully connected to server.")
    }
}