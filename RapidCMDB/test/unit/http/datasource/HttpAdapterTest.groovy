package http.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import datasource.HttpAdapter
import com.ifountain.core.datasource.Action
import datasource.UploadFileAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 20, 2009
* Time: 9:32:34 AM
* To change this template use File | Settings | File Templates.
*/
class HttpAdapterTest extends RapidCmdbTestCase {
    public void testUploadFile()
    {
        MockHttpAdapter adapter = new MockHttpAdapter();
        def url = "someurl"
        def fieldName = "file"
        File fileToBeUploaded = new File("somefile");
        def fileName = "someFileName"
        adapter.uploadFile(url, fieldName, fileToBeUploaded, fileName);
        def excutedActions = adapter.executedActions
        assertEquals (1, excutedActions.size());
        UploadFileAction action = excutedActions[0]
        assertEquals (url, action.getUrl());
        assertEquals (fieldName, action.getFieldName());
        assertEquals (fileToBeUploaded, action.getFileToBeUploaded());
        assertEquals (fileName, action.getFileNameWillBeSentToClient());
    }

    public void testUploadFileWithParameters()
    {
        MockHttpAdapter adapter = new MockHttpAdapter();
        def url = "someurl"
        def fieldName = "file"
        File fileToBeUploaded = new File("somefile");
        def fileName = "someFileName"
        def params = [param1:"param1Value", param2:"param2Value"]
        adapter.uploadFile(url, fieldName, fileToBeUploaded, fileName, params);
        def excutedActions = adapter.executedActions
        assertEquals (1, excutedActions.size());
        UploadFileAction action = excutedActions[0]
        assertEquals (url, action.getUrl());
        assertEquals (fieldName, action.getFieldName());
        assertEquals (params, action.getParams());
        assertEquals (fileToBeUploaded, action.getFileToBeUploaded());
        assertEquals (fileName, action.getFileNameWillBeSentToClient());
    }
}

class MockHttpAdapter extends HttpAdapter
{
    List executedActions = [];
    public void executeAction(Action action) {
        executedActions << action;
    }

}