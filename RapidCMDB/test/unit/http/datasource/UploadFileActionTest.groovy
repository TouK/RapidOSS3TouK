package http.datasource

import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import connection.HttpConnectionImpl
import datasource.UploadFileAction
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 20, 2009
* Time: 8:22:13 AM
* To change this template use File | Settings | File Templates.
*/
class UploadFileActionTest extends RapidCmdbTestCase {
    def testoutDirFile = new File("../testOutput");
    public static final String baseUrl = "http://localhost:9999";
    HttpConnectionImpl conn;
    HttpUtilsMock httpUtilsMock;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.

        FileUtils.deleteDirectory (testoutDirFile);
        testoutDirFile.mkdirs()
        initConn();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    def initConn()
    {
        conn = new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, baseUrl);
        ConnectionParam param = new ConnectionParam("ds", HttpConnectionImpl.class.getName(), otherParams);
        conn.init(param);
        httpUtilsMock = new HttpUtilsMock();
        conn.setHttpConnection(httpUtilsMock);
    }

    public void testUploadFile()
    {
        def file = new File(testoutDirFile, "fileToBeuploaded.txt");
        def fileContent = "This is file content"
        file.setText (fileContent);
        def fileNameToBeSenttoServer = "../../../afilenameDirreferentFromOriginal.txt"
        def actionUrl = "/file/upload";
        def fieldName = "file";
        def paramsToBeSent = [param1:"param1Val", param2:"param2Val"]
        UploadFileAction action = new UploadFileAction(TestLogUtils.log, actionUrl, fieldName, file, fileNameToBeSenttoServer);
        action.setParams (paramsToBeSent);
        def executedPostMethods = httpUtilsMock.postMethodsExecuted;
        assertEquals (0, executedPostMethods.size());
        action.execute (conn);
        executedPostMethods = httpUtilsMock.postMethodsExecuted;
        assertEquals (1, executedPostMethods.size());
        PostMethod mt = executedPostMethods[0];
        assertEquals (new URI("${baseUrl}${actionUrl}").toString(), mt.getURI().toString());

        MultipartRequestEntity entity = mt.getRequestEntity();

        assertTrue(entity.getContentType().indexOf("multipart/form-data;") >= 0);
        ByteOutputStream out = new ByteOutputStream();
        entity.writeRequest (out);
        def content = new String(out.getBytes());
        assertTrue (content.indexOf("filename=\"${fileNameToBeSenttoServer}\"") >= 0);
        assertTrue (content.indexOf("name=\"${fieldName}\"") >= 0);
        paramsToBeSent.each{paramName, paramValue->
            assertTrue (content.indexOf("name=\"${paramName}\"") >= 0);
            assertTrue (content.indexOf(paramValue) >= 0);
        }
    }

    public void testUploadFileWithNullFileName()
    {
        def file = new File(testoutDirFile, "fileToBeuploaded.txt");
        def fileContent = "This is file content"
        file.setText (fileContent);
        def fileNameToBeSenttoServer = "../../../afilenameDirreferentFromOriginal.txt"
        def actionUrl = "/file/upload";
        def fieldName = "file";
        UploadFileAction action = new UploadFileAction(TestLogUtils.log, actionUrl, fieldName, file, null);
        def executedPostMethods = httpUtilsMock.postMethodsExecuted;
        assertEquals (0, executedPostMethods.size());
        action.execute (conn);
        executedPostMethods = httpUtilsMock.postMethodsExecuted;
        assertEquals (1, executedPostMethods.size());
        PostMethod mt = executedPostMethods[0];
        assertEquals (new URI("${baseUrl}${actionUrl}").toString(), mt.getURI().toString());
        MultipartRequestEntity entity = mt.getRequestEntity();

        assertTrue(entity.getContentType().indexOf("multipart/form-data;") >= 0);
        ByteOutputStream out = new ByteOutputStream();
        entity.writeRequest (out);
        def content = new String(out.getBytes());
        assertTrue (content.indexOf("filename=\"${file.getName()}\"") >= 0);
        assertTrue (content.indexOf("name=\"${fieldName}\"") >= 0);
    }
    public void testThrowsExceptionIfUploadFileWithNullFieldName()
    {
        def file = new File(testoutDirFile, "fileToBeuploaded.txt");
        def fileContent = "This is file content"
        file.setText (fileContent);
        def fileNameToBeSenttoServer = "../../../afilenameDirreferentFromOriginal.txt"
        def actionUrl = "/file/upload";
        UploadFileAction action = new UploadFileAction(TestLogUtils.log, actionUrl, null, file, null);
        try{
            action.execute (conn);
            fail("Should throw exception");
        }catch(IllegalArgumentException ex)
        {
            assertEquals (new IllegalArgumentException("Name must not be null").getMessage(), ex.getMessage());
        }
    }

    public void testThrowsExceptionIfUploadFileWithNonExistingFile()
    {
        def file = new File(testoutDirFile, "fileToBeuploaded.txt");
        file.delete();
        def fileNameToBeSenttoServer = "../../../afilenameDirreferentFromOriginal.txt"
        def actionUrl = "/file/upload";
        def fieldName = "file";
        UploadFileAction action = new UploadFileAction(TestLogUtils.log, actionUrl, fieldName, file, fileNameToBeSenttoServer);
        try{
            action.execute (conn);
            fail("Should throw exception since file does not exist");
        }catch(java.io.FileNotFoundException ex)
        {

        }
    }

}