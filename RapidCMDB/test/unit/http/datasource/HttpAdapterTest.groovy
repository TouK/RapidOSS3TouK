package http.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import datasource.HttpAdapter
import org.apache.commons.httpclient.ConnectTimeoutException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 5:10:20 PM
* To change this template use File | Settings | File Templates.
*/
class HttpAdapterTest extends RapidCmdbTestCase{
    public void testIsConnectionException()
    {
        HttpAdapter adapter = new HttpAdapter();
        ConnectException  exception = new ConnectException("exception");
        assertTrue (adapter.isConnectionException(exception));

        SocketException socketException = new SocketException();
        assertTrue (adapter.isConnectionException(socketException));

        ConnectTimeoutException connectTimeoutException = new ConnectTimeoutException();
        assertTrue (adapter.isConnectionException(connectTimeoutException));

        NoRouteToHostException noRouteToHostException = new NoRouteToHostException();
        assertTrue (adapter.isConnectionException(noRouteToHostException));

        IOException ioException = new IOException()
        assertFalse (adapter.isConnectionException(ioException));

        Exception nestedException = new Exception(new SocketException());
        assertTrue (adapter.isConnectionException(nestedException));

        Exception otherException = new Exception();
        assertFalse(adapter.isConnectionException(otherException));
    }
}