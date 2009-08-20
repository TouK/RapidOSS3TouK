package http.datasource


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.HttpDatasourceOperations
import datasource.HttpDatasource
import connection.HttpConnection
import datasource.UploadFileAction




/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 25, 2009
* Time: 9:52:05 AM
* To change this template use File | Settings | File Templates.
*/
class HttpDatasourceOperationsTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();

    }

    public void tearDown() {
        super.tearDown();
    }


    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
        initialize([HttpDatasource, HttpConnection], []);
        CompassForTests.addOperationSupport(HttpDatasource, HttpDatasourceOperations);


        def con = HttpConnection.add(name: "testcon", baseUrl: "u");
        assertFalse(con.errors.toString(), con.hasErrors());

        def newDs = HttpDatasource.add(name: "testds", connection: con);
        assertFalse(newDs.hasErrors());
        assertNotNull(newDs.adapter);

        newDs.removeRelation(connection: con);
        assertFalse(newDs.hasErrors());
        assertNull(newDs.connection);

        try {
            def dsFromRepo = HttpDatasource.get(name: newDs.name);
            assertNull(dsFromRepo.adapter);

        }
        catch (e)
        {
            e.printStackTrace();
            fail("Should not throw exception. Exception thrown is ${e}");
        }

    }

    public void testUploadFile()
    {
        initialize([HttpDatasource, HttpConnection], []);
        CompassForTests.addOperationSupport(HttpDatasource, HttpDatasourceOperations);


        def con = HttpConnection.add(name: "testcon", baseUrl: "u");
        assertFalse(con.errors.toString(), con.hasErrors());

        def newDs = HttpDatasource.add(name: "testds", connection: con);
        assertFalse(newDs.hasErrors());
        assertNotNull(newDs.adapter);

        def mockAdapter = new MockHttpAdapter();
        newDs.adapter = mockAdapter;
        def url = "someurl"
        def fieldName = "file"
        def fileToBeUploaded = "somefile";
        def fileName = "someFileName"
        newDs.uploadFile(url, fieldName, fileToBeUploaded, fileName);
        def excutedActions = mockAdapter.executedActions
        assertEquals (1, excutedActions.size());
        UploadFileAction action = excutedActions[0]
        assertEquals (url, action.getUrl());
        assertEquals (fieldName, action.getFieldName());
        assertEquals (new File(fileToBeUploaded), action.getFileToBeUploaded());
        assertEquals (fileName, action.getFileNameWillBeSentToClient());

    }

}

