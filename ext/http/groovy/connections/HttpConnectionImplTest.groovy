package connections;

import java.util.HashMap;
import java.util.Map;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.core.test.util.RapidCoreTestCase;

public class HttpConnectionImplTest extends RapidCoreTestCase {

    public void testInit() throws Exception {
    	HttpConnectionImpl conn = new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, "http://localhost:9999/");
        ConnectionParam param = new ConnectionParam(HttpConnection.TYPE, "ds", HttpConnectionImpl.class.getName(), otherParams);
        try {
            conn.init(param);
        } catch (Throwable e) {
            fail("should not throw exception");
        }
        assertSame(param, conn.getParameters());
        
        param.getOtherParams().remove(HttpConnectionImpl.BASE_URL);
        try {
            conn.init(param);
            fail("should throw exception");
        } catch (UndefinedConnectionParameterException e) {
        }
       
    }
}
