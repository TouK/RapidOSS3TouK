package datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import org.apache.log4j.Logger
import connection.HttpConnectionImpl
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity
import org.apache.commons.httpclient.methods.multipart.FilePart
import org.apache.commons.httpclient.methods.multipart.Part
import org.apache.commons.httpclient.methods.multipart.StringPart

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 19, 2009
* Time: 6:34:49 PM
* To change this template use File | Settings | File Templates.
*/
class UploadFileAction implements Action{
    Logger logger;
    File fileToBeUploaded;
    String fileNameWillBeSentToClient;
    String url;
    String fieldName;
    Map params;
    public UploadFileAction(Logger logger, String url, String fieldName, File fileToBeUploaded, String fileNameWillBeSentToClient)
    {
        this.fieldName = fieldName;
        this.url = url;
        this.logger = logger;
        this.fileToBeUploaded = fileToBeUploaded;
        this.fileNameWillBeSentToClient = fileNameWillBeSentToClient
    }
    public void execute(IConnection conn) {
        HttpConnectionImpl httpConnection = (HttpConnectionImpl)conn;
        def completeUrl = HttpActionUtils.getCompleteUrl(httpConnection.getBaseUrl(), this.url);
        PostMethod method = new PostMethod(completeUrl);
        def parts = [new FilePart(fieldName, fileNameWillBeSentToClient, fileToBeUploaded)];
        params.each{paramName, paramValue->
            parts.add(new StringPart(String.valueOf(paramName), String.valueOf(paramValue)));
        }
        MultipartRequestEntity multiPart = new MultipartRequestEntity(parts as Part[],method.getParams());
        method.setRequestEntity(multiPart);
        httpConnection.getHttpConnection().executePostMethod (method);

    }
}