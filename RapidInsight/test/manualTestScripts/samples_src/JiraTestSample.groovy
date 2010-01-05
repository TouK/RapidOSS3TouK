import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.core.connection.ConnectionParam
import connection.JiraConnectionImpl
import datasource.JiraDatasource
import connection.JiraConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.JiraDatasourceOperations
import com.ifountain.comp.test.util.logging.TestLogUtils
import org.apache.log4j.Logger
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.test.util.DatasourceTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 30, 2009
* Time: 4:58:53 PM
* To change this template use File | Settings | File Templates.
*/
class JiraTestSample extends RapidCmdbWithCompassTestCase{

    def jiraConnectionParam;
    def username="pinar";
    def password="pinar";
    public void setUp() {
        super.setUp();
        TestLogUtils.enableLogger (Logger.getLogger("scripting"));

        initializeConnectionManager();
    }
     private void initializeConnectionManager()
    {

        Map<String, Object> otherParams = new HashMap<String, Object>();

        otherParams.put(JiraConnectionImpl.USERNAME, username);
        otherParams.put(JiraConnectionImpl.PASSWORD, password);

        jiraConnectionParam=new ConnectionParam("JiraConnection", "JiraTestConnection", JiraConnectionImpl.class.getName(), otherParams, 10, 0, 0);
        DatasourceTestUtils.getParamSupplier().setParam(jiraConnectionParam);
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), gcl, 1000);
    }

    public void tearDown() {
        super.tearDown();
        ConnectionManager.destroy();
    }

    public void testDummy()
    {


        initialize([JiraDatasource,JiraConnection],[]);
        CompassForTests.addOperationSupport (JiraDatasource,JiraDatasourceOperations);

        def con=JiraConnection.add(name:"jiracon",username:username,password:password);
        assertFalse(con.hasErrors());

        def ds=JiraDatasource.add(name:"jirads",connection:con,reconnectInterval:0);
        assertFalse(ds.hasErrors());

//        def issueKey="DEMO-282";
//        def projectKey="DEMO";
//
//        def details1=ds.retrieveDetails(issueKey);
//
//
//
//        ds.updateIssue(issueKey,["type":"2","summary":"testsumm 3","project":projectKey]);
//
//        def details2=ds.retrieveDetails(issueKey);
//
//        println "details1 ${details1}"
//        println "details2 ${details2}"
//
//        println "from ${details1.type} to ${details2.type}"
//        println "from ${details1.description} to ${details2.description}"
//        assertFalse(details1.type == details2.type)

        def ticketProps=[connectorName:"jira",eventName:"event2", type:"4"];
        ticketProps.project = "DEMO";
        ticketProps.summary = "sum sum summary";
        def returnedIssue = ds.openIssue(ticketProps);
        println "returnedIssue ${returnedIssue}"

        ds.updateIssue(returnedIssue.name,["type":"2",summary:"sum sum summary 2","project":returnedIssue.project]);

        def details=ds.retrieveDetails(returnedIssue.name);
        assertEquals("2",details.type);
    }
}