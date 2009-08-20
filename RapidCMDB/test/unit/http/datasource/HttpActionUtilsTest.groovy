package http.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import datasource.HttpActionUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 20, 2009
* Time: 8:46:52 AM
* To change this template use File | Settings | File Templates.
*/
class HttpActionUtilsTest extends RapidCmdbTestCase{
    public void testGetCompleteUrl()
    {
        def baseUrl = "http://someUrl.com"
        def actionUrl = "actionUrl/action"
        def completeUrl = HttpActionUtils.getCompleteUrl (baseUrl, actionUrl);
        assertEquals ("${baseUrl}/${actionUrl}", completeUrl);

        completeUrl = HttpActionUtils.getCompleteUrl (baseUrl+"/", actionUrl);
        assertEquals ("${baseUrl}/${actionUrl}", completeUrl);

        completeUrl = HttpActionUtils.getCompleteUrl (baseUrl+"/", "/"+actionUrl);
        assertEquals ("${baseUrl}/${actionUrl}", completeUrl);

        completeUrl = HttpActionUtils.getCompleteUrl (baseUrl, "/"+actionUrl);
        assertEquals ("${baseUrl}/${actionUrl}", completeUrl);
    }
}